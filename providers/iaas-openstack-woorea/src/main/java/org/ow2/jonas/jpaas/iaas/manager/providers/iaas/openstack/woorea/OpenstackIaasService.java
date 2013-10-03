package org.ow2.jonas.jpaas.iaas.manager.providers.iaas.openstack.woorea;

import com.woorea.openstack.base.client.OpenStackResponseException;
import com.woorea.openstack.keystone.Keystone;
import com.woorea.openstack.keystone.model.Access;
import com.woorea.openstack.keystone.model.authentication.UsernamePassword;
import com.woorea.openstack.keystone.utils.KeystoneUtils;
import com.woorea.openstack.nova.Nova;
import com.woorea.openstack.nova.model.Server;
import com.woorea.openstack.nova.model.ServerForCreate;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Validate;
import org.ow2.jonas.jpaas.iaas.manager.providers.iaas.api.IaasException;
import org.ow2.jonas.jpaas.iaas.manager.providers.iaas.api.IaasService;
import org.ow2.jonas.jpaas.iaas.manager.providers.iaas.api.NodeHandle;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;
import org.w3c.dom.Element;

import java.util.List;

@Component(public_factory = true, name = "OSIAAS")
@Provides
public class OpenstackIaasService implements IaasService {

    /**
     * Logger
     */
    private static Log logger = LogFactory.getLog(OpenstackIaasService.class);

    @Property
    private Element conf;

    /**
     * Stack configuration
     */
    private OpenstackIaasConfiguration openstackConf = new OpenstackIaasConfiguration();


    /**
     * Nova client
     */
    private Nova novaClient;


    /**
     * IP address list
     */
    private List ipList;

    @SuppressWarnings("unchecked")
    @Validate
    public void start() {
        logger.info("Load specific configuration to the stack");
        try {
            openstackConf.load(conf);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        logger.info("Connect to keystone :" +
                openstackConf.getKeystoneUrl() + "," +
                openstackConf.getKeystoneUser() + "," +
                openstackConf.getKeystonePassword());

        Keystone keystone = new Keystone(openstackConf.getKeystoneUrl());
        Access access = keystone.tokens()
                .authenticate(new UsernamePassword(openstackConf.getKeystoneUser(), openstackConf.getKeystonePassword()))
                .withTenantName(openstackConf.getTenantName()).execute();

        // use the token in the following requests
        keystone.token(access.getToken().getId());

        logger.info("Connect to nova :" +
                KeystoneUtils.findEndpointURL(access.getServiceCatalog(), "compute", null, "public"));

        this.novaClient = new Nova(KeystoneUtils.findEndpointURL(access.getServiceCatalog(), "compute", null, "public"));
        this.novaClient.token(access.getToken().getId());

    }


    public NodeHandle createNode(NodeHandle nodeHandle) throws IaasException {

        logger.info("createNode(" + nodeHandle + ")");
        NodeHandle outNodeHandle = new NodeHandle(nodeHandle);

        ServerForCreate serverForCreate = new ServerForCreate();

        serverForCreate.setName(nodeHandle.getName());
        serverForCreate.setFlavorRef(openstackConf.getFlavorId());
        serverForCreate.setImageRef(openstackConf.getImageId());

        //serverForCreate.setKeyName(keyPairName);
        serverForCreate.getSecurityGroups().add(new ServerForCreate.SecurityGroup("default")); /*default security group*/

        Server server = null;
        try {
            server = this.novaClient.servers().boot(serverForCreate).execute(); /*get the server id*/
            server = this.novaClient.servers().show(server.getId()).execute(); /*get detailed information about the server*/
        } catch (OpenStackResponseException ex) {
            ex.printStackTrace();
            throw new IaasException("Error during VM creation.", ex.getCause());
        }

        logger.info("status=" + server.getStatus());
        if (openstackConf.isSyncCreate()) {
            logger.info("Sync creation expected");

            int retry = 0;
            while (server.getStatus().equals("BUILD") && retry < openstackConf.getSyncCreateMaxRetry()) {
                try {
                    Thread.sleep(openstackConf.getSyncCreatePeriodRetry());
                } catch (InterruptedException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
                try {
                    server = this.novaClient.servers().show(server.getId()).execute(); /*get detailed information about the server*/
                } catch (OpenStackResponseException ex) {
                    ex.printStackTrace();
                }
                retry++;
                logger.info("retry=" + retry);
                logger.info("status=" + server.getStatus());

            }
        } else {
            logger.info("Async creation expected");
        }

        if (server.getStatus().equals("ACTIVE")) {
            outNodeHandle.setStatus(NodeHandle.STATUS_STARTED);
        } else if (server.getStatus().equals("BUILD")) {
            outNodeHandle.setStatus(NodeHandle.STATUS_CREATED);
        } else {
            outNodeHandle.setStatus(NodeHandle.STATUS_ERROR);
        }

        outNodeHandle.setId(server.getId());
        outNodeHandle.setIpAddress(server.getAccessIPv4());

        return outNodeHandle;
    }


    public NodeHandle deleteNode(NodeHandle nodeHandle) throws IaasException {
        NodeHandle outNodeHandle = new NodeHandle(nodeHandle);
        logger.info("deleteNode(" + nodeHandle + ")");

        try {
            this.novaClient.servers().delete(nodeHandle.getId()).execute();
        } catch (OpenStackResponseException e) {
            e.printStackTrace();
            throw new IaasException(e.getMessage(), e.getCause());
        }
        outNodeHandle.setIpAddress(null);
        outNodeHandle.setStatus(NodeHandle.STATUS_DELETED);
        outNodeHandle.setId(null);
        return outNodeHandle;
    }

    public NodeHandle startNode(NodeHandle nodeHandle) throws IaasException {
        logger.info("startNode (" + nodeHandle + ") - nothing to do !");
        return nodeHandle;
    }

    public NodeHandle stopNode(NodeHandle nodeHandle) throws IaasException {
        logger.info("stopNode (" + nodeHandle + ") - nothing to do !");
        return nodeHandle;
    }

    public NodeHandle getInfo(NodeHandle nodeHandle) throws IaasException {
        logger.info("getInfo (" + nodeHandle + ")");

        NodeHandle outNodeHandle = new NodeHandle(nodeHandle);

        Server server = null;

        try {
            server = this.novaClient.servers().show(nodeHandle.getId()).execute(); /*get detailed information about the server*/
        } catch (OpenStackResponseException ex) {
            ex.printStackTrace();
        }

        if (server.getStatus().equals("ACTIVE")) {
            outNodeHandle.setStatus(NodeHandle.STATUS_STARTED);
        } else if (server.getStatus().equals("BUILD")) {
            outNodeHandle.setStatus(NodeHandle.STATUS_CREATED);
        } else {
            outNodeHandle.setStatus(NodeHandle.STATUS_ERROR);
        }

        outNodeHandle.setId(server.getId());
        outNodeHandle.setIpAddress(server.getAccessIPv4());

        return nodeHandle;
    }

}
