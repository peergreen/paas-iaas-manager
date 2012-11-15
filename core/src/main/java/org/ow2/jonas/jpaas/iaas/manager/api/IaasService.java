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

/**
 * Iaas Service interface
 */
public interface IaasService {

    /**
     * Create a node
     *
     * @param nodeHandle the node to create
     * @return the node created
     * @throws IaasException
     */
    NodeHandle createNode(NodeHandle nodeHandle) throws IaasException;

    /**
     * Start a node
     *
     * @param nodeHandle the node to start
     * @return the node started
     * @throws IaasException
     */
    NodeHandle startNode(NodeHandle nodeHandle) throws IaasException;

    /**
     * Stop a node
     *
     * @param nodeHandle the node to stop
     * @return the node stoped
     * @throws IaasException
     */
    NodeHandle stopNode(NodeHandle nodeHandle) throws IaasException;

    /**
     * Delete a node
     *
     * @param nodeHandle the node to delete
     * @return the node deleted
     * @throws IaasException
     */
    NodeHandle deleteNode(NodeHandle nodeHandle) throws IaasException;

    /**
     * Get info on a node
     *
     * @param nodeHandle the node
     * @return the node
     * @throws IaasException
     */
    NodeHandle getInfo(NodeHandle nodeHandle) throws IaasException;
}
