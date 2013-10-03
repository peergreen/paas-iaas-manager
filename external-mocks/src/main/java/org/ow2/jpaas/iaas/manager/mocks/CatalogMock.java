package org.ow2.jpaas.iaas.manager.mocks;

import org.apache.felix.ipojo.*;
import org.apache.felix.ipojo.annotations.*;
import org.ow2.jonas.jpaas.catalog.api.IIaasCatalogFacade;
import org.ow2.jonas.jpaas.catalog.api.IaasCatalogException;
import org.ow2.jonas.jpaas.catalog.api.IaasConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;


@Component(immediate=true)
@Instantiate
@Provides
public class CatalogMock implements IIaasCatalogFacade {

    private List<IaasConfiguration> iaasConfigurations;
    private static Log logger = LogFactory.getLog(CatalogMock.class);


    @Validate
    public void initialize() {

        logger.info("Started");

        iaasConfigurations = new ArrayList<IaasConfiguration>();

        IaasConfiguration iaasConf1 = new IaasConfiguration(
                "dummy1",
                "COMPUTE",
                "DUMMYIAAS",
                true,
                false,
                "src/test/resources/dummy.xml",
                "dummy",
                new HashMap<String,String>());
        iaasConfigurations.add(iaasConf1);

        IaasConfiguration iaasConf2 = new IaasConfiguration(
                "os1",
                "COMPUTE",
                "OSIAAS",
                false,
                false,
                "src/test/resources/os.xml",
                "OCW-PAAS-",
                new HashMap<String,String>());
        iaasConfigurations.add(iaasConf2);


    }
    @Override
    public List<IaasConfiguration> getIaasConfigurationList() {
        logger.info("iaasConfigurations.size()="+iaasConfigurations.size());

        return    iaasConfigurations;
    }

    @Override
    public String getDefaultIaasConfigurationName() throws IaasCatalogException {
        for (IaasConfiguration conf:getIaasConfigurationList()) {
            if (conf.isDefault()) {
                return conf.getName();
            }

        }
        throw new IaasCatalogException("Default IaaS configuration not found");

    }

    @Override
    public IaasConfiguration getIaasConfiguration(String name) throws IaasCatalogException {
        for (IaasConfiguration conf:getIaasConfigurationList()) {
            if (conf.getName().equals(name)) {
                return conf;
            }
        }
        throw new IaasCatalogException("IaaS configuration " + name + "not found");

    }
}
