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

package org.ow2.jonas.jpaas.iaas.manager.api;

import java.util.List;

/**
 * Interface for the IaasManager.
 * @author David Richard
 */
public interface IIaasManager {

    /**
     * Create a Compute
     *
     * @param computeName the name of the Compute
     * @param iaasConfigurationName the name of the IaaS Configuration
     */
    public void createCompute(String computeName, String iaasConfigurationName) throws IaasManagerException;

    /**
     * remove a Compute
     *
     * @param computeName the name of the Compute
     */
    public void removeCompute(String computeName) throws IaasManagerException;

    /**
     * Get the port range of a Compute
     *
     * @param computeName the name of the Compute
     * @return a list of port
     */
    public List<Integer> getPortRange(String computeName, int size);

}
