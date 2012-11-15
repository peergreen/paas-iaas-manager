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

package org.ow2.jonas.jpaas.iaas.manager.bean;


import org.ow2.easybeans.osgi.annotation.OSGiResource;
import org.ow2.jonas.jpaas.catalog.api.IIaasCatalogFacade;
import org.ow2.jonas.jpaas.catalog.api.IaasCatalogException;
import org.ow2.jonas.jpaas.catalog.api.IaasConfiguration;
import org.ow2.jonas.jpaas.iaas.manager.api.IIaasManager;
import org.ow2.jonas.jpaas.iaas.manager.api.IaasManagerException;
import org.ow2.jonas.jpaas.iaas.manager.api.NodeHandle;
import org.ow2.jonas.jpaas.iaas.manager.api.OperationType;
import org.ow2.jonas.jpaas.iaas.manager.api.ServiceInvoker;
import org.ow2.jonas.jpaas.sr.facade.api.ISrIaasComputeFacade;
import org.ow2.jonas.jpaas.sr.facade.vo.IaasComputeVO;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import java.util.List;


/**
 * IaasManager Bean
 * @author David Richard
 */
@Stateless(mappedName = "IaasManagerBean")
@Local(IIaasManager.class)
@Remote(IIaasManager.class)
public class IaasManagerBean implements IIaasManager {


    @OSGiResource
    private ISrIaasComputeFacade iSrIaasComputeFacade;

    @OSGiResource
    private IIaasCatalogFacade iIaasCatalogFacade;

    @OSGiResource
    private ServiceInvoker serviceInvoker;

    /**
     * Logger
     */
    private static Log logger = LogFactory.getLog(IaasManagerBean.class);


    /**
     * Create a Compute
     *
     * @param computeName           the name of the Compute
     * @param iaasConfigurationName the name of the IaaS Configuration
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void createCompute(String computeName, String iaasConfigurationName) throws IaasManagerException {
        NodeHandle nodeHandle = new NodeHandle();
        nodeHandle.setName(computeName);
        nodeHandle.setIaasName(iaasConfigurationName);
        try {
            nodeHandle = serviceInvoker.doOperation(OperationType.PROVISIONING_NEW_NODE, nodeHandle.getName(), nodeHandle);

            IaasConfiguration iaasConfiguration = iIaasCatalogFacade.getIaasConfiguration(iaasConfigurationName);

            IaasComputeVO iaasComputeVO = new IaasComputeVO() ;
            iaasComputeVO.setName(computeName);
            iaasComputeVO.setState("RUNNING");
            iaasComputeVO.setIpAddress(nodeHandle.getIpAddress());
            iaasComputeVO.setHostname(nodeHandle.getName());
            iaasComputeVO.setConf(iaasConfigurationName);
            iaasComputeVO.setCapabilities(iaasConfiguration.getCapabilities());
            iaasComputeVO = iSrIaasComputeFacade.createIaasCompute(iaasComputeVO);
            logger.debug("End of Create Compute :" + iaasComputeVO.getIpAddress());
        } catch (IaasManagerException e) {
            throw new IaasManagerException("Cannot create the node " + nodeHandle.getName(), e.getCause());
        } catch (IaasCatalogException e) {
            throw new IaasManagerException("Error to find the IaaS Configuration named " +
                                iaasConfigurationName + ".", e.getCause());
        }
    }

    /**
     * remove a Compute
     *
     * @param computeName the name of the Compute
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void removeCompute(String computeName) throws IaasManagerException {
        List<IaasComputeVO> iaasComputeList = iSrIaasComputeFacade.findIaasComputes();
        String computeId = null;
        for (IaasComputeVO tmp : iaasComputeList) {
            if (tmp.getName().equals(computeName)) {
                computeId = tmp.getId();
                break;
            }
        }
        if (computeId == null) {
            throw new IaasManagerException("Cannot find the Compute named " + computeName);
        } else {
            IaasComputeVO iaasComputeVO = iSrIaasComputeFacade.getIaasCompute(computeId);
            NodeHandle nodeHandle = new NodeHandle();
            nodeHandle.setName(computeName);
            nodeHandle.setIaasName(iaasComputeVO.getConf());
            nodeHandle.setIpAddress(iaasComputeVO.getIpAddress());
            try {
                serviceInvoker.doOperation(OperationType.DELETE_NODE, nodeHandle.getName(), nodeHandle);
                iSrIaasComputeFacade.deleteIaasCompute(computeId);
            } catch (IaasManagerException e) {
                throw new IaasManagerException("Cannot delete the node " + nodeHandle.getName(), e.getCause());
            }
        }
    }

    /**
     * Get the port range of a Compute
     *
     * @param computeName the name of the Compute
     * @return a list of port
     */
    @Override
    public List<Integer> getPortRange(String computeName, int size) {
        return null;  //ToDo
    }
}
