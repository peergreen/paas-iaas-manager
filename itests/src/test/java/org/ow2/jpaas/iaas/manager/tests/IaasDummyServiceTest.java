package org.ow2.jpaas.iaas.manager.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.systemProperty;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.slf4j.LoggerFactory;

import org.ow2.jonas.jpaas.iaas.manager.providers.iaas.api.IaasException;
import org.ow2.jonas.jpaas.iaas.manager.providers.iaas.api.IaasService;
import org.ow2.jonas.jpaas.iaas.manager.providers.iaas.api.NodeHandle;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;


@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class IaasDummyServiceTest {

    @Inject
    BundleContext context;

    @Inject
    private IaasService iaasService;

    @Configuration
    public Option[] config() {

        // Reduce log level.
        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.INFO);

        return options(systemProperty("org.ops4j.pax.logging.DefaultServiceLog.level").value("WARN"),
                mavenBundle("com.peergreen.paas", "paas-system-representation-vo").version(Definitions.PROJECT_VERSION),
                mavenBundle("com.peergreen.paas", "paas-system-representation-api").version(Definitions.PROJECT_VERSION),
                mavenBundle("com.peergreen.paas", "paas-catalog-api").version(Definitions.PROJECT_VERSION),
                mavenBundle("com.peergreen.paas", "paas-iaas-manager-external-mocks").version(Definitions.PROJECT_VERSION),
                mavenBundle("com.peergreen.paas", "paas-iaas-manager-providers-api").version(Definitions.PROJECT_VERSION),
                mavenBundle("com.peergreen.paas", "paas-iaas-manager-providers-dummy").version(Definitions.PROJECT_VERSION),
                mavenBundle("com.peergreen.paas", "paas-iaas-manager-api").version(Definitions.PROJECT_VERSION),
                mavenBundle("com.peergreen.paas", "paas-iaas-manager-core").version(Definitions.PROJECT_VERSION),


                junitBundles());
    }

    @Test
    public void checkInjectContext() {

        assertNotNull(context);
    }


    @Test
    public void checkBundleIaasService() {
        Boolean found = false;
        Boolean active = false;
        Bundle[] bundles = context.getBundles();
        for (Bundle bundle : bundles) {
            if (bundle != null) {
                if (bundle.getSymbolicName().equals("com.peergreen.paas.iaas-manager-providers-api")) {
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
    public void checkInjectIaasService() {
        assertNotNull(iaasService);
    }

    @Test
    public void createNode() {

        NodeHandle nodeHandle = new NodeHandle();
        nodeHandle.setName("vm1");
        nodeHandle.setIaasName("dummy1");

        try {
            nodeHandle = iaasService.createNode(nodeHandle);
        } catch (IaasException e) {
            e.printStackTrace();
        }
        assertNotNull(nodeHandle);
        assertEquals(nodeHandle.getIaasName(), "dummy1");
        assertEquals(nodeHandle.getName(), "vm1");
        assertEquals(nodeHandle.getIpAddress(), "dummyipaddress");

    }
}
