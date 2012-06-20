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

package org.ow2.jonas.jpaas.iaas.manager.bean;


import org.ow2.jonas.jpaas.iaas.manager.api.IIaasManager;

import javax.ejb.Stateless;
import java.util.List;


/**
 * IaasManager Bean
 * @author David Richard
 */
@Stateless
public class IaasManagerBean implements IIaasManager {

    /**
     * Create a Compute
     *
     * @param computeName           the name of the Compute
     * @param iaasConfigurationName the name of the IaaS Configuration
     */
    @Override
    public void createCompute(String computeName, String iaasConfigurationName) {
        //ToDo
    }

    /**
     * remove a Compute
     *
     * @param computeName the name of the Compute
     */
    @Override
    public void removeCompute(String computeName) {
        //ToDo
    }

    /**
     * Get the port range of a Compute
     *
     * @param computeName the name of the Compute
     * @return a list of port
     */
    @Override
    public List<Integer> getPortRange(String computeName, int size) {
        return null;  //ToDo
    }
}
