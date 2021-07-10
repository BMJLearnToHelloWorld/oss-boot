package com.boot.oss.entity;

import com.boot.common.entity.BaseEntity;

/**
 * @author bmj
 */
public class ObjInfoEntity extends BaseEntity {

    private static final long serialVersionUID = -8574175091026306948L;

    private String objId;

    private String objName;

    private String objSuffix;

    private String objPath;

    private String preObjId;

    private String bucket;

    public ObjInfoEntity() {
        super();
    }

    public ObjInfoEntity(String objId, String objName, String objSuffix, String objPath, String preObjId, String createdBy, String lastUpdatedBy, String bucket) {
        super(createdBy, lastUpdatedBy);
        this.objId = objId;
        this.objName = objName;
        this.objSuffix = objSuffix;
        this.objPath = objPath;
        this.preObjId = preObjId;
        this.bucket = bucket;
    }

    public String getObjId() {
        return objId;
    }

    public void setObjId(String objId) {
        this.objId = objId;
    }

    public String getObjName() {
        return objName;
    }

    public void setObjName(String objName) {
        this.objName = objName;
    }

    public String getObjSuffix() {
        return objSuffix;
    }

    public void setObjSuffix(String objSuffix) {
        this.objSuffix = objSuffix;
    }

    public String getObjPath() {
        return objPath;
    }

    public void setObjPath(String objPath) {
        this.objPath = objPath;
    }

    public String getPreObjId() {
        return preObjId;
    }

    public void setPreObjId(String preObjId) {
        this.preObjId = preObjId;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }
}
