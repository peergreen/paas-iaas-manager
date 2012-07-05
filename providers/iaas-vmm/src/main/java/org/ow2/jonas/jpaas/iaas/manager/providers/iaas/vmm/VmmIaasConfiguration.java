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


import org.w3c.dom.Element;

/**
 * Describes a Vmm configuration
 *
 * @author pelletib
 */
public class VmmIaasConfiguration {

    /**
     * JASMINe VMM agent host
     */
    private String agentHost;

    /**
     * Property name of the JASMINe VMM agent port
     */
    private int agentPort;

    /**
     * new VM memory size
     */
    private long newVmMemorySize;

    /**
     * new VM CPU number
     */
    private int newVmCpuNumber;

    /**
     * new VM disk size
     */
    private int newVmDiskSize;

    /**
     * new VM image UUID
     */
    private String newVmUuid;

    /**
     * new VM IP assignment mode
     */
    private String newVmIpAssignmentMode;

    /**
     * new VM fixed IP range
     */
    private String newVmIpAssignmentFixedIpRange;

    /**
     * Property name of the new VM fixed IP submask
     */
    private String newVmIpAssignmentFixedSubnetMask;

    /**
     * Property name of the new VM fixed IP gateway
     */
    private String newVmIpAssignmentFixedGateway;

    /**
     * Pool name
     */
    private String poolName;


    /**
     * Default constructor
     *
     */
    public VmmIaasConfiguration() {
    }

    // getters and setters
    /**
     * Load configuration
     *
     */
    public void load(Element element) throws ConfigurationException {
        try {
            setAgentHost(element.getElementsByTagName("host").item(0).getFirstChild().getNodeValue());
            setAgentPort(Integer.parseInt(element.getElementsByTagName("port").item(0).getFirstChild().getNodeValue()));
            setNewVmMemorySize(Long.parseLong(element.getElementsByTagName("memory-size").item(0)
                    .getFirstChild().getNodeValue()));
            setNewVmCpuNumber(Integer.parseInt(element.getElementsByTagName("cpu-number").item(0)
                    .getFirstChild().getNodeValue()));
            setNewVmDiskSize(Integer.parseInt(element.getElementsByTagName("disk-size").item(0)
                    .getFirstChild().getNodeValue()));
            setNewVmUuid(element.getElementsByTagName("image-UUID").item(0).getFirstChild().getNodeValue());
            setNewVmIpAssignmentMode(element.getElementsByTagName("ip-assignment-mode").item(0)
                    .getFirstChild().getNodeValue());
            setNewVmIpAssignmentFixedIpRange(element.getElementsByTagName("ip-assignment-fixed-iprange").item(0)
                    .getFirstChild().getNodeValue());
            setNewVmIpAssignmentFixedSubnetMask(element.getElementsByTagName("ip-assignment-fixed-subnetmask")
                    .item(0).getFirstChild().getNodeValue());
            setNewVmIpAssignmentFixedGateway(element.getElementsByTagName("ip-assignment-fixed-gateway")
                    .item(0).getFirstChild().getNodeValue());
            setPoolName(element.getElementsByTagName("pool-name").item(0).getFirstChild().getNodeValue());
            } catch (Exception e) {
            throw new ConfigurationException(
                    "Error during configuration loading - " + e.getMessage(), e);
        }
    }

    public String getAgentHost() {
        return agentHost;
    }

    public void setAgentHost(String agentHost) {
        this.agentHost = agentHost;
    }

    public int getAgentPort() {
        return agentPort;
    }

    public void setAgentPort(int agentPort) {
        this.agentPort = agentPort;
    }

    public long getNewVmMemorySize() {
        return newVmMemorySize;
    }

    public void setNewVmMemorySize(long newVmMemorySize) {
        this.newVmMemorySize = newVmMemorySize;
    }

    public int getNewVmCpuNumber() {
        return newVmCpuNumber;
    }

    public void setNewVmCpuNumber(int newVmCpuNumber) {
        this.newVmCpuNumber = newVmCpuNumber;
    }

    public int getNewVmDiskSize() {
        return newVmDiskSize;
    }

    public void setNewVmDiskSize(int newVmDiskSize) {
        this.newVmDiskSize = newVmDiskSize;
    }

    public String getNewVmUuid() {
        return newVmUuid;
    }

    public void setNewVmUuid(String newVmUuid) {
        this.newVmUuid = newVmUuid;
    }

    public String getNewVmIpAssignmentMode() {
        return newVmIpAssignmentMode;
    }

    public void setNewVmIpAssignmentMode(String newVmIpAssignmentMode) {
        this.newVmIpAssignmentMode = newVmIpAssignmentMode;
    }

    public String getNewVmIpAssignmentFixedIpRange() {
        return newVmIpAssignmentFixedIpRange;
    }

    public void setNewVmIpAssignmentFixedIpRange(
            String newVmIpAssignmentFixedIpRange) {
        this.newVmIpAssignmentFixedIpRange = newVmIpAssignmentFixedIpRange;
    }

    public String getNewVmIpAssignmentFixedSubnetMask() {
        return newVmIpAssignmentFixedSubnetMask;
    }

    public void setNewVmIpAssignmentFixedSubnetMask(
            String newVmIpAssignmentFixedSubnetMask) {
        this.newVmIpAssignmentFixedSubnetMask = newVmIpAssignmentFixedSubnetMask;
    }

    public String getNewVmIpAssignmentFixedGateway() {
        return newVmIpAssignmentFixedGateway;
    }

    public void setNewVmIpAssignmentFixedGateway(
            String newVmIpAssignmentFixedGateway) {
        this.newVmIpAssignmentFixedGateway = newVmIpAssignmentFixedGateway;
    }

    /**
     * ToString
     */
    public String toString() {

        String ret = super.toString();

        ret += "agentHost=" + agentHost + "\n";
        ret += "agentPort=" + agentPort + "\n";
        ret += "newVmMemorySize=" + newVmMemorySize + "\n";
        ret += "newVmCpuNumber=" + newVmCpuNumber + "\n";
        ret += "newVmDiskSize=" + newVmDiskSize + "\n";
        ret += "newVmUuid=" + newVmUuid + "\n";
        ret += "newVmIpAssignmentMode=" + newVmIpAssignmentMode + "\n";
        ret += "newVmIpAssignmentFixedIpRange=" + newVmIpAssignmentFixedIpRange + "\n";
        ret += "newVmIpAssignmentFixedSubnetMask=" + newVmIpAssignmentFixedSubnetMask + "\n";
        ret += "newVmIpAssignmentFixedGateway=" + newVmIpAssignmentFixedGateway + "\n";
        ret += "poolName=" + poolName + "\n";

        return ret;
    }

    public void setPoolName(String poolName) {
        this.poolName = poolName;
    }

    public String getPoolName() {
        return poolName;
    }


}
