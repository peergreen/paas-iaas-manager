
package org.ow2.jonas.jpaas.iaas.manager.providers.iaas.openstack.woorea;


import org.w3c.dom.Element;
import org.ow2.jonas.jpaas.iaas.manager.providers.iaas.api.IaasException;


public class OpenstackIaasConfiguration {

    /**
     * Tenant name
     */
    private String tenantName;

    /**
     * Keystone url
     */
    private String keystoneUrl;

    /**
     * Keystone user
     */
    private String keystoneUser;

    /**
     * Keystone password
     */
    private String keystonePassword;

    /**
     * Image id
     */
    private String imageId;

    /**
     * Flavor id
     */
    private String flavorId;

    /**
     * Security group
     */
    private String securityGroup;

    /**
     * Synchronous creation expected ?
     * ie. waiting to be active
     */
    private boolean syncCreate;

    /**
     * Max retry during creation process
     */
    private int syncCreateMaxRetry;

    /**
     * Period retry during creation process
     */
    private int syncCreatePeriodRetry;



    /**
     * Default constructor
     *
     */
    public OpenstackIaasConfiguration() {
    }

    // getters and setters
    /**
     * Load configuration
     *
     */
    public void load(Element element) throws ConfigurationException {
        try {
            setTenantName(element.getElementsByTagName("tenantName").item(0).getFirstChild().getNodeValue());
            setKeystoneUrl(element.getElementsByTagName("keystoneUrl").item(0).getFirstChild().getNodeValue());
            setKeystoneUser(element.getElementsByTagName("keystoneUser").item(0).getFirstChild().getNodeValue());
            setKeystonePassword(element.getElementsByTagName("keystonePassword").item(0).getFirstChild().getNodeValue());
            setImageId(element.getElementsByTagName("imageId").item(0).getFirstChild().getNodeValue());
            setFlavorId(element.getElementsByTagName("flavorId").item(0).getFirstChild().getNodeValue());
            setSecurityGroup(element.getElementsByTagName("securityGroup").item(0).getFirstChild().getNodeValue());
            setSyncCreate(Boolean.parseBoolean(element.getElementsByTagName("syncCreate").item(0).getFirstChild().getNodeValue()));
            setSyncCreateMaxRetry(Integer.parseInt(element.getElementsByTagName("syncCreateMaxRetry").item(0).getFirstChild().getNodeValue()));
            setSyncCreatePeriodRetry(Integer.parseInt(element.getElementsByTagName("syncCreatePeriodRetry").item(0).getFirstChild().getNodeValue()));
            } catch (Exception e) {
            throw new ConfigurationException(
                    "Error during configuration loading - " + e.getMessage(), e);
        }
    }

    /**
     * ToString
     */
    public String toString() {

        String ret = super.toString();

        ret += "tenantName=" + tenantName + "\n";
        ret += "keystoneUrl=" + keystoneUrl + "\n";
        ret += "keystoneUser=" + keystoneUser + "\n";
        ret += "keystonePassword=" + keystonePassword + "\n";
        ret += "imageId=" + imageId + "\n";
        ret += "flavorId=" + flavorId + "\n";
        ret += "securityGroup=" + securityGroup + "\n";
        ret += "isSyncCreate=" + syncCreate + "\n";
        ret += "syncCreateMaxRetry=" + syncCreateMaxRetry + "\n";
        ret += "syncCreatePeriodRetry=" + syncCreatePeriodRetry + "\n";
        return ret;
    }


    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public String getKeystoneUrl() {
        return keystoneUrl;
    }

    public void setKeystoneUrl(String keystoneUrl) {
        this.keystoneUrl = keystoneUrl;
    }

    public String getKeystoneUser() {
        return keystoneUser;
    }

    public void setKeystoneUser(String keystoneUser) {
        this.keystoneUser = keystoneUser;
    }

    public String getKeystonePassword() {
        return keystonePassword;
    }

    public void setKeystonePassword(String keystonePassword) {
        this.keystonePassword = keystonePassword;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getFlavorId() {
        return flavorId;
    }

    public void setFlavorId(String flavorId) {
        this.flavorId = flavorId;
    }

    public String getSecurityGroup() {
        return securityGroup;
    }

    public void setSecurityGroup(String securityGroup) {
        this.securityGroup = securityGroup;
    }

    public int getSyncCreateMaxRetry() {
        return syncCreateMaxRetry;
    }

    public void setSyncCreateMaxRetry(int syncCreateMaxRetry) {
        this.syncCreateMaxRetry = syncCreateMaxRetry;
    }

    public int getSyncCreatePeriodRetry() {
        return syncCreatePeriodRetry;
    }

    public void setSyncCreatePeriodRetry(int syncCreatePeriodRetry) {
        this.syncCreatePeriodRetry = syncCreatePeriodRetry;
    }

    public boolean isSyncCreate() {
        return syncCreate;
    }

    public void setSyncCreate(boolean syncCreate) {
        this.syncCreate = syncCreate;
    }
}
