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
