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

package org.ow2.jonas.jpaas.iaas.manager.providers.iaas.dummy;

import java.util.HashMap;
import java.util.Map;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Validate;
import org.w3c.dom.Element;
import org.ow2.jonas.jpaas.iaas.manager.providers.iaas.api.NodeHandle;
import org.ow2.jonas.jpaas.iaas.manager.providers.iaas.api.IaasException;
import org.ow2.jonas.jpaas.iaas.manager.providers.iaas.api.IaasService;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

@Component(public_factory=true,name="DUMMYIAAS")
@Provides
public class DummyIaasService implements IaasService {

	/**
	 * Logger
	 */
    private static Log logger = LogFactory.getLog(DummyIaasService.class);

    @Property
    private Element conf;

    /**
     * Stack configuration
     */
    private DummyIaasConfiguration dummyConf = new DummyIaasConfiguration();


	@Validate
	public void start() {
		logger.info("Load specific configuration to the stack");
		try {
			dummyConf.load(conf);
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public NodeHandle createNode(NodeHandle nodeHandle) throws IaasException {
		nodeHandle.setIpAddress("dummyipaddress");
        simulateProcessing("IaaS Dummy : create node <" + nodeHandle.getName() + ">", 3);

		return nodeHandle;

	}

	@Override
	public NodeHandle deleteNode(NodeHandle nodeHandle) throws IaasException {
		simulateProcessing("IaaS Dummy : delete node <" + nodeHandle.getName() + ">", 3);
		return nodeHandle;

	}

	@Override
	public NodeHandle startNode(NodeHandle nodeHandle) throws IaasException {
		simulateProcessing("IaaS Dummy : start node <" + nodeHandle.getName() + ">", 3);
		return nodeHandle;

	}

	@Override
	public NodeHandle stopNode(NodeHandle nodeHandle) throws IaasException {
		simulateProcessing("IaaS Dummy : stop node <" + nodeHandle.getName() + ">", 3);
		return nodeHandle;

	}

	@Override
	public NodeHandle getInfo(NodeHandle nodeHandle) throws IaasException {
		NodeHandle outNodeHandle = new NodeHandle(nodeHandle);
		outNodeHandle.setIpAddress("localhost");
		return outNodeHandle;
	}

	/**
	 * Simulate processing
	 */
	protected void simulateProcessing(String msg, int n) {
		logger.info(msg + " in progress");
		for (int i = 1; i <= n; i++) {
			logger.info(i + "...");
			try {
				Thread.sleep(1000l);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		logger.info(msg + " done!");

	}

}
