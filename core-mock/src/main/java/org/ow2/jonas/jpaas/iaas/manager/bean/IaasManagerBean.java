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


import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.ow2.jonas.jpaas.catalog.api.IIaasCatalogFacade;
import org.ow2.jonas.jpaas.catalog.api.IaasCatalogException;
import org.ow2.jonas.jpaas.catalog.api.IaasConfiguration;
import org.ow2.jonas.jpaas.iaas.manager.api.IIaasManager;
import org.ow2.jonas.jpaas.iaas.manager.api.IaasManagerException;
import org.ow2.jonas.jpaas.sr.facade.api.ISrIaasComputeFacade;
import org.ow2.jonas.jpaas.sr.facade.vo.IaasComputeVO;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

import java.util.List;
import java.util.UUID;


@Component
@Provides
@Instantiate
public class IaasManagerBean implements IIaasManager {


    @Requires
    private ISrIaasComputeFacade iSrIaasComputeFacade;

    @Requires
    private IIaasCatalogFacade iIaasCatalogFacade;

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

        logger.info("computeName=" + computeName + ", iaasConfigurationName=" + iaasConfigurationName);

        IaasConfiguration iaasConfiguration = null;
        try {
            iaasConfiguration = iIaasCatalogFacade.getIaasConfiguration(iaasConfigurationName);
        } catch (IaasCatalogException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        IaasComputeVO iaasComputeVO = new IaasComputeVO() ;
            iaasComputeVO.setName(computeName);
            iaasComputeVO.setState("RUNNING");
            iaasComputeVO.setIpAddress("127.0.0.1");

            iaasComputeVO.setHostname("hostname");
            iaasComputeVO.setInternalId(UUID.randomUUID().toString());

            iaasComputeVO.setConf(iaasConfigurationName);
            iaasComputeVO.setCapabilities(iaasConfiguration.getCapabilities());
            iaasComputeVO = iSrIaasComputeFacade.createIaasCompute(iaasComputeVO);
            logger.debug("End of Create Compute :" + iaasComputeVO.getIpAddress());

    }

    /**
     * remove a Compute
     *
     * @param computeName the name of the Compute
     */
    @Override
    public void removeCompute(String computeName) throws IaasManagerException {

        logger.info("computeName=" + computeName);


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
            logger.info("iaasComputeVO=" + iaasComputeVO);

            iSrIaasComputeFacade.deleteIaasCompute(computeId);

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
