package org.ow2.jonas.jpaas.iaas.manager.providers.iaas.openstack.woorea;

import org.ow2.jonas.jpaas.iaas.manager.providers.iaas.api.IaasException;

public class ConfigurationException extends IaasException {


    private static final long serialVersionUID = 1L;

    public ConfigurationException(String msg) {
        super(msg);
    }

    public ConfigurationException(String msg, Throwable t) {
        super(msg,t);
    }
}
