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


import org.apache.felix.ipojo.*;
import org.apache.felix.ipojo.annotations.*;
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
            filter="(component.providedServiceSpecifications=org.ow2.jonas.jpaas.iaas.manager.providers.iaas.api.IaasService)")
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
        logger.info("Factory - " + factory.getName());

        // get IaasConfiguration
        List<IaasConfiguration> iaasList = iIaasCatalogFacade.getIaasConfigurationList();

        for (IaasConfiguration iaas : iaasList) {
            logger.info("IaasConfiguration : " + iaas.getName() + "," + iaas.getType() + "," + iaas.getSubType());

            if (iaas.getSubType().equalsIgnoreCase(type)) {

                Dictionary<String, Object> instanceConfiguration = new Hashtable<String, Object>();
                instanceConfiguration.put("instance.name", iaas.getName());
                instanceConfiguration.put("conf", loadSpecificConfig(iaas.getSpecificConfig()));

                logger.info("Create instance IaasService <"
                        + iaas.getType() + "," + iaas.getSubType() + "," + iaas.getName() + ">");

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

        logger.info("loadSpecificConfig : " + file);

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
