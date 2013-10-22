package org.ow2.jpaas.iaas.manager.mocks;

import org.apache.felix.ipojo.annotations.*;
import org.ow2.jonas.jpaas.sr.facade.api.ISrIaasComputeFacade;
import org.ow2.jonas.jpaas.sr.facade.vo.IaasComputeVO;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;


@Component(immediate=true)
@Instantiate
@Provides
public class SrComputeMock implements ISrIaasComputeFacade {

    private List<IaasComputeVO> iaasComputes;
    private static Log logger = LogFactory.getLog(SrComputeMock.class);


    @Validate
    private void initialize(){
        logger.info("Started");

        iaasComputes = new ArrayList<IaasComputeVO>();
    }
    @Override
    public IaasComputeVO createIaasCompute(IaasComputeVO iaasComputeVO) {
        iaasComputeVO.setId(UUID.randomUUID().toString());
        logger.info("iaasComputeVO=" + iaasComputeVO);
        iaasComputes.add(iaasComputeVO);
        return iaasComputeVO;
    }

    @Override
    public void deleteIaasCompute(String iaasComputeId) {
        logger.info("iaasComputeId=" + iaasComputeId);

        for(IaasComputeVO compute:iaasComputes)    {
             if (compute.getId().equals(iaasComputeId)) {
                 iaasComputes.remove(compute);
                 logger.info("Removed");

                 break;
             }
        }
    }

    @Override
    public IaasComputeVO updateIaasCompute(IaasComputeVO iaasComputeVO) {
        logger.info("iaasComputeVO=" + iaasComputeVO);
        return iaasComputeVO;
    }

    @Override
    public IaasComputeVO getIaasCompute(String iaasComputeId) {
        logger.info("iaasComputeId=" + iaasComputeId);

        for(IaasComputeVO compute:iaasComputes) {
            if (compute.getId().equals(iaasComputeId)) {
                logger.info("compute=" + compute);

                return compute;
            }
        }
        return null;
    }

    @Override
    public List<IaasComputeVO> findIaasComputes() {
        return iaasComputes;
    }
}
