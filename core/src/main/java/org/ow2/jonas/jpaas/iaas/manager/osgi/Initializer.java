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

import org.apache.felix.ipojo.ComponentInstance;
import org.apache.felix.ipojo.ConfigurationException;
import org.apache.felix.ipojo.Factory;
import org.apache.felix.ipojo.MissingHandlerException;
import org.apache.felix.ipojo.UnacceptableConfiguration;
import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Unbind;
import org.apache.felix.ipojo.annotations.Validate;
import org.osgi.framework.ServiceReference;
import org.ow2.jonas.jpaas.catalog.api.IIaasCatalogFacade;
import org.ow2.jonas.jpaas.catalog.api.IaasConfiguration;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

@Component
@Instantiate
public class Initializer {

    /**
     * Logger
     */
    private static Log logger = LogFactory.getLog(Initializer.class);

    /**
     * IaaS components Factories
     */
    private Map<String,Factory> iaasFactories = new HashMap<String,Factory>();

    /**
     * IaasCatalog facade
     */
    @Requires
    private IIaasCatalogFacade iIaasCatalogFacade;

    @Validate
    public void start() {
        logger.info("Started");
    }

    @Bind(aggregate=true, optional=true,
            filter="(component.providedServiceSpecifications=org.ow2.jonas.jpaas.iaas.manager.api.IaasService)")
    public void bindIaasServiceFactory(Factory factory, ServiceReference ref) {
        String name = (String) ref.getProperty("factory.name");
        iaasFactories.put(name, factory);
        logger.info("Bind Factory IaasService - " + name);

        // create instances related to this factory
        createIaasInstances(name);
    }

    @Unbind
    public void unbindIaasServiceFactory(ServiceReference ref) {
        String name = (String) ref.getProperty("factory.name");
        iaasFactories.remove(name);
        logger.info("Unbind Factory IaasService - " + name);
    }


    private void createIaasInstances(String type) {

        Factory factory = iaasFactories.get(type);

        // get IaasConfiguration
        List<IaasConfiguration> iaasList = iIaasCatalogFacade.getIaasConfigurationList();

        for (IaasConfiguration iaas : iaasList) {
            if (iaas.getSubType().equalsIgnoreCase(type)) {
                Dictionary<String, Object> instanceConfiguration = new Hashtable<String, Object>();
                instanceConfiguration.put("instance.name", iaas.getName());
                instanceConfiguration.put("conf", loadSpecificConfig(iaas.getSpecificConfig()));

                logger.info("Create instance IaasService <"
                        + iaas.getSubType() + "," + iaas.getName() + ">");

                try {
                    ComponentInstance ci = factory
                            .createComponentInstance(instanceConfiguration);

                } catch (UnacceptableConfiguration e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (MissingHandlerException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (ConfigurationException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    private Element loadSpecificConfig(String file) {
        Element result = null;
        try {
            File f = new File(file);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(f);
            result = document.getDocumentElement();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        return result;
    }
}
