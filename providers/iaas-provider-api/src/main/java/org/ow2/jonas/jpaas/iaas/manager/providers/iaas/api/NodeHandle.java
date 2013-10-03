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

package org.ow2.jonas.jpaas.iaas.manager.providers.iaas.api;

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
     * Out parameter: id
     */
    private String id;


    /**
     * Out parameter: status
     */
    private String status;

    /**
     * Name of the IaaS
     */
    private String iaasName;

    /**
     * Status
     */
    public static final String STATUS_UNKNOWN = "UNKNOWN";
    public static final String STATUS_STARTED = "STARTED";
    public static final String STATUS_CREATED = "CREATED";
    public static final String STATUS_STOPPED = "STOPPED";
    public static final String STATUS_DELETED = "DELETED";
    public static final String STATUS_ERROR = "ERROR";


    // constructors
    public NodeHandle() {
        status = STATUS_UNKNOWN;
    }

    public NodeHandle(String name, String ipAddress, String iaasName) {
        this.name = name;
        this.ipAddress = ipAddress;
        this.iaasName = iaasName;
        status = STATUS_UNKNOWN;
    }

    public NodeHandle(NodeHandle handle) {

        this.name=handle.getName();
        this.ipAddress=handle.getIpAddress();
        this.iaasName=handle.getIaasName();
        this.id=handle.getId();
        this.status = handle.getStatus();
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String toString() {

        String ret = "\n";
        ret += "NodeHandle [" + this.getName() + "].id=" + this.getId() + "\n";
        ret += "NodeHandle [" + this.getName() + "].ipAddress=" + this.getIpAddress() + "\n";
        ret += "NodeHandle [" + this.getName() + "].iaas=" + this.getIaasName() + "\n";
        ret += "NodeHandle [" + this.getName() + "].status=" + this.getStatus() + "\n";

        return ret;
    }

    public boolean isValid() {
        return !(getIpAddress() == null || getIpAddress().equals(""));
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


}
