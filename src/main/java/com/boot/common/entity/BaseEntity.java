package com.boot.common.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * @author bmj
 */
public class BaseEntity implements Serializable {

    private static final long serialVersionUID = 5186895444254742565L;

    private Date creationDate;

    private String createdBy;

    private Date lastUpdateDate;

    private String lastUpdatedBy;

    public BaseEntity() {
        super();
    }

    public BaseEntity(String createdBy, String lastUpdatedBy) {
        super();
        this.createdBy = createdBy;
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }
}
