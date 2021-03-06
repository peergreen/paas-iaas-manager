/**
 * JPaaS
 * Copyright 2012 Bull S.A.S.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * $Id:$
 */ 

package org.ow2.jonas.jpaas.iaas.manager.osgi.impl;


import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Unbind;
import org.apache.felix.ipojo.annotations.Validate;
import org.osgi.framework.ServiceReference;
import org.ow2.jonas.jpaas.iaas.manager.api.IaasManagerException;
import org.ow2.jonas.jpaas.iaas.manager.osgi.api.OperationType;
import org.ow2.jonas.jpaas.iaas.manager.osgi.api.ServiceInvoker;
import org.ow2.jonas.jpaas.iaas.manager.providers.iaas.api.IaasException;
import org.ow2.jonas.jpaas.iaas.manager.providers.iaas.api.IaasService;
import org.ow2.jonas.jpaas.iaas.manager.providers.iaas.api.NodeHandle;
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
        logger.info("Bind IaasService - " + name);
    }

    @Unbind
    public void unbindIaasService(ServiceReference ref) {
        String name = (String) ref.getProperty("instance.name");
        iaasServices.remove(name);
        logger.info("Unbind IaasService - " + name);
    }

    /**
     * Run the operation
     */
    public NodeHandle doOperation(
            OperationType operationType,
            String nodeName,
            NodeHandle nodeHandle
    ) throws IaasManagerException {

        logger.info("nodeHandle - " + nodeHandle);

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
                    outNodeHandle = iaasService.startNode(nodeHandle);
                    logger.debug("Executor - Node started !");
                    break;

                case STOP_NODE:
                    // invoke iaas service
                    outNodeHandle = iaasService.stopNode(nodeHandle);
                    break;

                case DELETE_NODE:
                    // invoke iaas service
                    outNodeHandle = iaasService.deleteNode(nodeHandle);
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
                    + nodeName + ">", e.getCause());

        }
        return outNodeHandle;
    }
}
