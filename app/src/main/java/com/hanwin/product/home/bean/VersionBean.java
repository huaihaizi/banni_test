package com.hanwin.product.home.bean;

/**
 * Created by zhaopf on 2018/8/29 0029.
 */

public class VersionBean {
    private String id;
    private String version;
    private String description;
    private String forceVersion;

    public String getForceVersion() {
        return forceVersion;
    }

    public void setForceVersion(String forceVersion) {
        this.forceVersion = forceVersion;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
