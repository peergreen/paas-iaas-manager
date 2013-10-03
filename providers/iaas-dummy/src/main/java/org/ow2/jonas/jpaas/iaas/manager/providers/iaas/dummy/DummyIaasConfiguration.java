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

import org.w3c.dom.Element;

public class DummyIaasConfiguration {

    /**
     * Default constructor
     *
     */
    public DummyIaasConfiguration() {
    }

    // getters and setters
	/**
	 * Load configuration
	 *
	 */
	public void load(Element element) throws ConfigurationException {
	}

    /**
     * ToString
     */
    public String toString() {

    	String ret = super.toString();

		return ret;
    }


}
