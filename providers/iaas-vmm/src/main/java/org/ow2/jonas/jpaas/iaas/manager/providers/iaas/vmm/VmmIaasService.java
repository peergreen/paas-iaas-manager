/**
 * JPaaS
 * Copyright (C) 2012 Bull S.A.S.
 * Contact: jasmine@ow2.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 * --------------------------------------------------------------------------
 * $Id$
 * --------------------------------------------------------------------------
 */

package org.ow2.jonas.jpaas.iaas.manager.providers.iaas.vmm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Validate;
import org.ow2.jonas.jpaas.iaas.manager.api.IaasException;
import org.ow2.jonas.jpaas.iaas.manager.api.IaasService;
import org.ow2.jonas.jpaas.iaas.manager.api.NodeHandle;
import org.w3c.dom.Element;
import org.ow2.sirocco.vmm.api.CustomizationSpec;
import org.ow2.sirocco.vmm.api.DiskOperation;
import org.ow2.sirocco.vmm.api.HostMXBean;
import org.ow2.sirocco.vmm.api.InvalidVMConfigException;
import org.ow2.sirocco.vmm.api.ServerPoolMXBean;
import org.ow2.sirocco.vmm.api.StoragePoolMXBean;
import org.ow2.sirocco.vmm.api.VMMException;
import org.ow2.sirocco.vmm.api.VNICSpec;
import org.ow2.sirocco.vmm.api.VirtualDiskSpec;
import org.ow2.sirocco.vmm.api.VirtualMachineConfigSpec;
import org.ow2.sirocco.vmm.api.VirtualMachineMXBean;
import org.ow2.sirocco.vmm.api.VnicIPSettings;
import org.ow2.sirocco.vmm.api.Volume;
import org.ow2.sirocco.vmm.api.VNICSpec.MacAddressAssignement;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

@Component(public_factory=true,name="VMM")
@Provides
public class VmmIaasService implements IaasService {

    /**
     * Logger
     */
    private static Log logger = LogFactory.getLog(VmmIaasService.class);

    @Property
    private Element conf;

    /**
     * Stack configuration
     */
    private VmmIaasConfiguration vmmConf = new VmmIaasConfiguration();

    /**
     * Period (in ms) after a creation of a VM when no new creation is allowed. Fixed to 3 min.
     */
    private static final long TIME_BEFORE_NEW_CREATION_ALLOWED = 180000;

    /**
     * System time of the last VM creation.
     */
    private long lastCreationTime = 0;

    /**
    * IP address list
    */
    private List ipList;

    @SuppressWarnings("unchecked")
    @Validate
    public void start() {
        logger.info("Load specific configuration to the stack");
        try {
            vmmConf.load(conf);
        } catch (ConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (vmmConf.getNewVmIpAssignmentFixedIpRange() != null) {
            ipList = new ArrayList();
            StringTokenizer tokenizer = new StringTokenizer(vmmConf.getNewVmIpAssignmentFixedIpRange(), ",");
            ipList = new ArrayList<String>();
            while (tokenizer.hasMoreTokens()) {
                ipList.add(tokenizer.nextToken());
            }
        }
    }


    public NodeHandle createNode(NodeHandle nodeHandle) throws IaasException {
        logger.info("createNode(" + nodeHandle + ")");
        VirtualMachineMXBean vm;
        NodeHandle outNodeHandle = new NodeHandle(nodeHandle);
        try {
            vm = provisionAndStartNewVM(nodeHandle.getName());
        } catch (InvalidVMConfigException e) {
            e.printStackTrace();
            throw new IaasException(e.getMessage(), e.getCause());
        } catch (VMMException e) {
            e.printStackTrace();
            throw new IaasException(e.getMessage(), e.getCause());
        }
        try {
            outNodeHandle.setIpAddress(vm.getGuestIpAddress());
        } catch (VMMException e) {
            throw new IaasException("Error during VM creation.", e.getCause());
        }
        return outNodeHandle;
    }


    public NodeHandle deleteNode(NodeHandle nodeHandle) throws IaasException {
        NodeHandle outNodeHandle = new NodeHandle(nodeHandle);
        try {
            removeVM(nodeHandle.getName());
        } catch (VMMException e) {
            throw new IaasException(e.getMessage(), e.getCause());
        }
        outNodeHandle.setIpAddress(null);
        return outNodeHandle;
    }

    public NodeHandle startNode(NodeHandle nodeHandle) throws IaasException {
        logger.info("IaaS VMM : start node <" + nodeHandle.getName() + "> - nothing to do !");
        return nodeHandle;
    }

    public NodeHandle stopNode(NodeHandle nodeHandle) throws IaasException {
        try {
            stopVM(nodeHandle.getName());
        } catch (VMMException e) {
            throw new IaasException(e.getMessage(), e.getCause());
        }
        return nodeHandle;
    }

    public NodeHandle getInfo(NodeHandle nodeHandle) throws IaasException {
        VirtualMachineMXBean vm = null;
        try {
            vm = getVM(nodeHandle.getName());
            NodeHandle outNodeHandle = new NodeHandle(nodeHandle);
            outNodeHandle.setIpAddress(vm.getGuestIpAddress());
            return outNodeHandle;
        } catch (VMMException e) {
            throw new IaasException(e.getMessage(), e.getCause());
        }

    }


    /**
     * Connect to a JASMINe VMM agent and provision and start a new Virtual Machine
     *
     * @param name
     *            Name of the Virtual Machine to create
     * @return The managed bean representing the created Virtual Machine
     * @throws VMMException
     * @throws InvalidVMConfigException
     * @throws VMMException
     */
    private VirtualMachineMXBean provisionAndStartNewVM(String name)
            throws InvalidVMConfigException, VMMException {

        VirtualMachineMXBean vm = null;

        logger.info("VM provision request received <" + name + ">");

        logger.info("VM creation request accepted - elapsed time since last creation: "+(int)((System.currentTimeMillis()-lastCreationTime)/1000));

        ServerPoolMXBean serversPool = getServerPool();

        // build configuration
        Volume templateVolume = null;
        String templateVolumeKey=vmmConf.getNewVmUuid();
        StoragePoolMXBean storagePool = null;
        for (StoragePoolMXBean pool : serversPool.getStoragePools()) {
                Volume vol=pool.getVolumeByKey(templateVolumeKey);
                if(vol!=null) {
                    templateVolume = vol;
                    storagePool = pool;
                    break;
            }
        }

        VirtualDiskSpec diskSpec = new VirtualDiskSpec();
        diskSpec.setDiskOp(DiskOperation.CREATE_FROM);
        diskSpec.setCapacityMB(vmmConf.getNewVmDiskSize());
        diskSpec.setVolume(templateVolume);
        diskSpec.setStoragePool(storagePool);

        CustomizationSpec customizationSpec = new CustomizationSpec();
        VirtualMachineConfigSpec vmConfigSpec = new VirtualMachineConfigSpec();

        VNICSpec nicSpec = new VNICSpec();
        nicSpec.setAddressType(MacAddressAssignement.GENERATED);
        nicSpec.setNetworkName("default");

        vmConfigSpec.setName(name);
        vmConfigSpec.setMemoryMB(vmmConf.getNewVmMemorySize());
        vmConfigSpec.setNumVCPUs(vmmConf.getNewVmCpuNumber());
        vmConfigSpec.setVnicSpecs(Collections.singletonList(nicSpec));
        vmConfigSpec.setProperties(new HashMap<String, String>());
        vmConfigSpec.getProperties().put("bootDevice", "hd");
        vmConfigSpec.setDiskSpecs(Collections.singletonList(diskSpec));

        VnicIPSettings guestIPSettings = new VnicIPSettings();

        String mode = vmmConf.getNewVmIpAssignmentMode();
        if ("DHCP".equals(mode)) {
            guestIPSettings.setIpAssignmentMode(VnicIPSettings.IpAssignmentMode.DHCP);
        } else if ("FIXED".equals(mode)) {
            guestIPSettings.setIpAssignmentMode(VnicIPSettings.IpAssignmentMode.FIXED);
            guestIPSettings.setIpAddress(getIP());
            guestIPSettings.setSubnetMask(vmmConf.getNewVmIpAssignmentFixedSubnetMask());
            guestIPSettings.setGateway(vmmConf.getNewVmIpAssignmentFixedGateway());
        } else {
            guestIPSettings
            .setIpAssignmentMode(VnicIPSettings.IpAssignmentMode.NONE);
        }

        customizationSpec.setVnicIpSettingsList(Collections.singletonList(guestIPSettings));
        customizationSpec.setGuestOsHostName(name);

        // TODO : set constraints ?
        Map<String, String> constraints = new HashMap<String, String>();

        // sync provisioning
        try {
            // in selfxl demo, we create the VM on the first host and then Entropy is launched
            // to optimize the placement according different policies
            List<HostMXBean> list =  serversPool.getManagedHosts();
            HostMXBean host = list.get(0);
            for (HostMXBean tmp : list) {
                if (tmp.getHostName().equals("selfxl-5")) {
                    logger.info("Selfxl-5 found");
                    host = tmp;
                    break;
                }
            }
            vm = host.createVirtualMachine(vmConfigSpec, customizationSpec, true, true);
            logger.info(vm.getNameLabel() + " provisioned");
            //vm.start();
            String vmIP = vm.getGuestIpAddress();
            int maxRetry = 3;
            while (vmIP == null && maxRetry != 0) {
                maxRetry--;
                vmIP = vm.getGuestIpAddress();
            }
            if (maxRetry==0) {
                throw new VMMException("Cannot get the IP address of the created VM");
            }
            logger.info(vm.getNameLabel() + " started with id address : "
                + vm.getGuestIpAddress());
            lastCreationTime = System.currentTimeMillis();
        } catch (Exception e) {
            e.printStackTrace();
            throw new VMMException("Exception during provision or start-up: " + e.getCause());
        }
        return vm;

    }

    /**
     * getServerPool MXBean
     *
     * @return The managed bean representing the pool
     * @throws VMMException
     */
    private ServerPoolMXBean getServerPool()
            throws VMMException {

        logger.info("Get server pool <" + vmmConf.getPoolName() + ">");
        MBeanServerConnection mbsc;
        Set<ObjectName> names;
        try {
            mbsc = connectVMMAgent(vmmConf.getAgentHost(), vmmConf.getAgentPort());
            names = mbsc.queryNames(new ObjectName(
                    "org.ow2.sirocco.vmm.api:type=ServerPool,name=" + vmmConf.getPoolName()), null);
        } catch (IOException e) {
            throw new VMMException(e.getCause());
        } catch (MalformedObjectNameException e) {
            throw new VMMException(e.getCause());
        } catch (NullPointerException e) {
            throw new VMMException(e.getCause());
        }

        Iterator<ObjectName> it = names.iterator();
        while (it.hasNext()) {
            ObjectName hostObjectName = it.next();
            ServerPoolMXBean pool = (ServerPoolMXBean) JMX.newMXBeanProxy(mbsc,
                    hostObjectName, ServerPoolMXBean.class);
            logger.info("Pool found");

            return pool;
        }

        return null;
    }

    /**
     * get VM by name
     * @param name
     * @return VM proxy
     * @throws VMMException
     */
    private VirtualMachineMXBean getVM(String name) throws VMMException{
        ServerPoolMXBean pool = getServerPool();

        List<HostMXBean> hostsList = pool.getManagedHosts();

        for (HostMXBean host:hostsList){
            List<VirtualMachineMXBean> vmList = host.getResidentVMs();
            for (VirtualMachineMXBean vm:vmList) {
                if (vm.getNameLabel().equals(name)) {
                    logger.info("get VM <" + name + "> - found !");
                    return vm;
                }
            }
        }
        logger.info("get VM <" + name + "> - not found !");
        return null;
    }

    /**
     * Stop a named VM
     * @param name
     * @throws VMMException
     */
    private void stopVM(String name) throws VMMException {

        logger.info("VM stopping request received !<" + name + ">");

        VirtualMachineMXBean vm = getVM(name);
        if (vm == null) {
            throw new VMMException("VM <" + name + "> not found");
        } else {
            logger.info("Shutdown VM <" + name + ">");
            vm.shutdown();
        }
    }

    /**
     * Remove a named VM
     * @param name
     * @throws VMMException
     */
    private void removeVM(String name) throws VMMException {

        logger.info("VM removing request received !<" + name + ">");

        VirtualMachineMXBean vm = getVM(name);
        if (vm == null) {
            throw new VMMException("VM <" + name + "> not found");
        } else {
            logger.info("Destroy VM <" + name + ">");
            vm.destroy(true);  //true to delete the volume file
        }
    }

    /**
     * Connect to a JASMINe VMM agent and destroy all VMs managed by it.
     *
     * @throws VMMException
     */
    private void removeAllVMs() throws VMMException {

        MBeanServerConnection mbsc;
        Set<ObjectName> names;
        try {
            mbsc = connectVMMAgent(vmmConf.getAgentHost(), vmmConf.getAgentPort());
            names = mbsc.queryNames(new ObjectName(
                    "org.ow2.sirocco.vmm.api:type=Host,*"), null);
        } catch (IOException e) {
            throw new VMMException(e.getCause());
        } catch (MalformedObjectNameException e) {
            throw new VMMException(e.getCause());
        } catch (NullPointerException e) {
            throw new VMMException(e.getCause());
        }

        Iterator<ObjectName> it = names.iterator();
        while (it.hasNext()) {

            ObjectName hostObjectName = it.next();
            HostMXBean host = (HostMXBean) JMX.newMXBeanProxy(mbsc,
                    hostObjectName, HostMXBean.class);
            logger.info("Host hostname=" + host.getHostName());

            List<VirtualMachineMXBean> vms = host.getResidentVMs();
            for (VirtualMachineMXBean vm : vms) {

                logger.info(vm.getNameLabel() + " will be detroyed.");
                vm.destroy();
            }
        }
    }

    /**
     * Connect to a JASMINe VMM agent.
     *
     * @param host
     *            Host of the JASMINe VMM agent to connect.
     * @param port
     *            Port of the JASMINe VMM agent to connect.
     * @return The MBean server connection of the JASMINe VMM agent
     * @throws java.io.IOException
     */
    private MBeanServerConnection connectVMMAgent(String host, int port)
            throws IOException {

        JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://"
                + host + ":" + port + "/server");
        JMXConnector jmxc = JMXConnectorFactory.connect(url, null);

        return jmxc.getMBeanServerConnection();
    }

    private String getIP() throws VMMException {
        try {
            return ipList.remove(0).toString();
        } catch(IndexOutOfBoundsException e) {
            throw new VMMException("No more IP address available");
        }
    }

}
