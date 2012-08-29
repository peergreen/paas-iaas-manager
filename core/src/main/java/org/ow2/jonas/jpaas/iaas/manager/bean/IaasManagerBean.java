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
            iSrIaasComputeFacade.createIaasCompute(iaasComputeVO);
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
