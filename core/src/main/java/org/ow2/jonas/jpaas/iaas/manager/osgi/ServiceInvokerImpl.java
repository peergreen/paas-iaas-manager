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

package org.ow2.jonas.jpaas.iaas.manager.osgi;


import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Unbind;
import org.apache.felix.ipojo.annotations.Validate;
import org.osgi.framework.ServiceReference;
import org.ow2.jonas.jpaas.iaas.manager.api.IaasException;
import org.ow2.jonas.jpaas.iaas.manager.api.IaasManagerException;
import org.ow2.jonas.jpaas.iaas.manager.api.IaasService;
import org.ow2.jonas.jpaas.iaas.manager.api.NodeHandle;
import org.ow2.jonas.jpaas.iaas.manager.api.OperationType;
import org.ow2.jonas.jpaas.iaas.manager.api.ServiceInvoker;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

import java.util.HashMap;
import java.util.Map;


@Component(immediate=true)
@Instantiate
@Provides
public class ServiceInvokerImpl implements ServiceInvoker {
    /**
     * Logger
     */
    private static Log logger = LogFactory.getLog(ServiceInvokerImpl.class);

    /**
     * Map of IaaS service with name as key
     */
    private Map<String, IaasService> iaasServices = new HashMap<String, IaasService>();

    @Validate
    public void start() {
        logger.info("Started");
    }

    @Bind(optional = true, aggregate = true)
    public void bindIaasService(IaasService service, ServiceReference ref) {
        String name = (String) ref.getProperty("instance.name");
        iaasServices.put(name, service);
        logger.debug("Bind IaasService - " + name);
    }

    @Unbind
    public void unbindIaasService(ServiceReference ref) {
        String name = (String) ref.getProperty("instance.name");
        iaasServices.remove(name);
        logger.debug("Unbind IaasService - " + name);
    }

    /**
     * Run the operation
     */
    public NodeHandle doOperation(
            OperationType operationType,
            String nodeName,
            NodeHandle nodeHandle
    ) throws IaasManagerException {

        // get iaas service
        IaasService iaasService = iaasServices.get(nodeHandle
                .getIaasName());

        if (iaasService == null) {
            throw new IaasManagerException("Service <"
                    + nodeHandle.getIaasName()
                    + " not available for node <" + nodeName + ">");
        }

        boolean ret;
        NodeHandle outNodeHandle = null;

        try {
            switch (operationType) {
                case NONE:
                    logger.debug("Executor - None action !");
                    break;


                case PROVISIONING_NEW_NODE:
                    // invoke iaas service
                    outNodeHandle = iaasService.createNode(nodeHandle);
                    break;

                case START_NEW_NODE:
        /*             // invoke iaas service if needed
                    if (!iaasService.isAutoStart()) {

                        outNodeHandle = iaasService.startNode(nodeHandle);
                    } else {
                        logger.debug("Executor - IaaS configured with autostart, don't invoke service" );
                        outNodeHandle = nodeHandle;
                    }

                   // wait for availability of the node
                    long period = ScalerManager.getScalerManager().getSystemRepresentation().getGlobalConfiguration().getPeriodApplicationChecking() * 1000;
                    long retryMax = ScalerManager.getScalerManager().getSystemRepresentation().getGlobalConfiguration().getMaxRetriesApplicationChecking();
                    long retryNb = 0;


                    try {
                        while (retryNb < retryMax &&
                                chefManagerService.isNodeAvailable(nodeHandle.getIpAddress()) == false) {
                            logger.info("Executor - Node not already available - Waiting ..." );
                            try {
                                Thread.sleep(period);
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            retryNb++;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if ( retryNb>= retryMax) {
                        throw new IaasException("Node <" + nodeName + "> not already available - max retries reached");
                    }*/

                    logger.debug("Executor - Node started !" );
                    break;

                case STOP_NODE:
                    // invoke iaas service
                    outNodeHandle =  iaasService.stopNode(nodeHandle);
                    break;

                case DELETE_NODE:
                    // invoke iaas service
                    outNodeHandle =  iaasService.deleteNode(nodeHandle);
                    break;

                case GET_INFO:
                    // invoke iaas service
                    outNodeHandle = iaasService.getInfo(nodeHandle);
                    break;

                default:
                    logger.debug("RM - triggerEvent : unexpected operation");
                    break;
            }
        } catch (IaasException e) {
            throw new IaasManagerException("Error during execution Service <"
                    + nodeHandle.getIaasName() + " for node <"
                    + nodeName + ">", e);

        }
        return outNodeHandle;
    }
}
