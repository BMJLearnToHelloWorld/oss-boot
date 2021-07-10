package com.boot.common.entity;

import java.io.Serializable;

/**
 * @author bmj
 */
public class BaseEntity implements Serializable {

    private static final long serialVersionUID = 5186895444254742565L;

    private long creationDate;

    private String createdBy;

    private long lastUpdateDate;

    private String lastUpdatedBy;

    public BaseEntity() {
        super();
    }

    public BaseEntity(String createdBy, String lastUpdatedBy) {
        super();
        this.createdBy = createdBy;
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(long creationDate) {
        this.creationDate = creationDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public long getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(long lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }
}
