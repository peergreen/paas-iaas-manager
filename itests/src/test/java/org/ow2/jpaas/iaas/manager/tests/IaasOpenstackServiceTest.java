package org.ow2.jpaas.iaas.manager.tests;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.ow2.jonas.jpaas.catalog.api.IIaasCatalogFacade;
import org.ow2.jonas.jpaas.catalog.api.IaasCatalogException;
import org.ow2.jonas.jpaas.catalog.api.IaasConfiguration;
import org.ow2.jonas.jpaas.iaas.manager.providers.iaas.api.IaasException;
import org.ow2.jonas.jpaas.iaas.manager.providers.iaas.api.IaasService;
import org.ow2.jonas.jpaas.iaas.manager.providers.iaas.api.NodeHandle;

import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import java.util.UUID;

import static org.junit.Assert.*;
import static org.ops4j.pax.exam.CoreOptions.*;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class IaasOpenstackServiceTest {

    @Inject
    BundleContext context;

    @Inject
    private IaasService iaasService;

    @Inject
    private IIaasCatalogFacade catalog;

    @Configuration
    public Option[] config() {

        // Reduce log level.
        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.INFO);

        return options(systemProperty("org.ops4j.pax.logging.DefaultServiceLog.level").value("WARN"),
                mavenBundle("org.ow2.jonas.jpaas.system-representation", "system-representation-vo").version(Definitions.PROJECT_VERSION),
                mavenBundle("org.ow2.jonas.jpaas.system-representation", "system-representation-api").version(Definitions.PROJECT_VERSION),
                mavenBundle("org.ow2.jonas.jpaas.catalog", "jpaas-catalog-api").version(Definitions.PROJECT_VERSION),
                mavenBundle("org.ow2.jonas.jpaas.iaas-manager", "iaas-manager-external-mocks").version(Definitions.PROJECT_VERSION),
                mavenBundle("org.ow2.jonas.jpaas.iaas-manager.providers", "iaas-provider-api").version(Definitions.PROJECT_VERSION),
                mavenBundle("org.ow2.jonas.jpaas.iaas-manager.providers", "iaas-openstack-woorea").version(Definitions.PROJECT_VERSION),
                mavenBundle("org.ow2.jonas.jpaas.iaas-manager", "iaas-manager-api").version(Definitions.PROJECT_VERSION),
                mavenBundle("org.ow2.jonas.jpaas.iaas-manager", "iaas-manager-core").version(Definitions.PROJECT_VERSION),

                junitBundles());
    }

    @Test
    public void checkInjectContext() {
        assertNotNull(context);
    }

    @Test
    public void checkInjectIaasService() {
        assertNotNull(iaasService);
    }

    @Test
    public void checkBundleIaasService() {
        Boolean found = false;
        Boolean active = false;
        Bundle[] bundles = context.getBundles();
        for (Bundle bundle : bundles) {
            if (bundle != null) {
                if (bundle.getSymbolicName().equals("org.ow2.jonas.jpaas.iaas-manager.providers.iaas-provider-api")) {
                    found = true;
                    if (bundle.getState() == Bundle.ACTIVE) {
                        active = true;
                    }
                }
            }
        }
        assertTrue(found);
        assertTrue(active);
    }

    @Test
    public void resourceAllocation() {

        // createNode
        IaasConfiguration iaasConf = null;
        try {
            iaasConf = catalog.getIaasConfiguration("os1");
        } catch (IaasCatalogException e) {
            fail(e.getMessage()) ;
        }
        NodeHandle nodeHandle = new NodeHandle();
        String name = iaasConf.getPrefixResourceName() + "TEST-allocate-instance";
        nodeHandle.setName(name);
        nodeHandle.setIaasName("os1");

        try {
            nodeHandle = iaasService.createNode(nodeHandle);
        } catch (IaasException e) {
            fail(e.getMessage()) ;
        }
        assertNotNull(nodeHandle);
        assertEquals(nodeHandle.getIaasName(), "os1");
        assertEquals(nodeHandle.getName(), name);
        assertNotNull(nodeHandle.getIpAddress());
        assertNotNull(nodeHandle.getId());
        assertEquals(nodeHandle.getStatus(),NodeHandle.STATUS_STARTED);

        // getInfo
        try {
            nodeHandle = iaasService.getInfo(nodeHandle);
        } catch (IaasException e) {
            fail(e.getMessage()) ;
        }
        assertNotNull(nodeHandle);
        assertEquals(nodeHandle.getIaasName(), "os1");
        assertEquals(nodeHandle.getName(), name);
        assertNotNull(nodeHandle.getIpAddress());
        assertNotNull(nodeHandle.getId());
        assertEquals(nodeHandle.getStatus(),NodeHandle.STATUS_STARTED);

        // deleteNode
        try {
            nodeHandle = iaasService.deleteNode(nodeHandle);
        } catch (IaasException e) {
            fail(e.getMessage()) ;
        }
        assertNotNull(nodeHandle);
        assertEquals(nodeHandle.getIaasName(), "os1");
        assertEquals(nodeHandle.getName(), name);
        assertNull(nodeHandle.getIpAddress());
        assertNull(nodeHandle.getId());
        assertEquals(nodeHandle.getStatus(),NodeHandle.STATUS_DELETED);

    }


}
