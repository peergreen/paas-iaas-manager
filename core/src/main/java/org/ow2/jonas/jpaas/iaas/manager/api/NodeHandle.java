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

import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

import java.io.Serializable;

public class NodeHandle implements Serializable {

    private static Log logger = LogFactory.getLog(NodeHandle.class);

    /**
     * serialization version
     */
    private static final long serialVersionUID = 1L;

    /**
     * In parameter: name
     */
    private String name;

    /**
     * Out parameter: ip address
     */
    private String ipAddress;

    /**
     * Name of the IaaS
     */
    private String iaasName;


    // constructors
    public NodeHandle() {
    }

    public NodeHandle(String name, String ipAddress, String iaasName) {
        this.name = name;
        this.ipAddress = ipAddress;
        this.iaasName = iaasName;
    }

    public NodeHandle(NodeHandle handle) {

        this.name=handle.getName();
        this.ipAddress=handle.getIpAddress();
    }

    // getters/setters

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getIaasName() {
        return iaasName;
    }

    public void setIaasName(String iaasName) {
        this.iaasName = iaasName;
    }

    public String toString() {

        String ret = "\n";
        ret += "NodeHandle [" + this.getName() + "].ipAddress=" + this.getIpAddress() + "\n";
        ret += "NodeHandle [" + this.getName() + "].iaas=" + this.getIaasName() + "\n";

        return ret;
    }

    public boolean isValid() {
        return !(getIpAddress() == null || getIpAddress().equals(""));
    }


}
