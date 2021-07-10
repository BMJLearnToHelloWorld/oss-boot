package com.boot.oss.service.model;

import java.io.InputStream;
import java.io.Serializable;

/**
 * @author bmj
 */
public class FileModel implements Serializable {

    private String filePrefix;
    private String fileSuffix;
    private InputStream fileInputStream;

    public String getFilePrefix() {
        return filePrefix;
    }

    public void setFilePrefix(String filePrefix) {
        this.filePrefix = filePrefix;
    }

    public String getFileSuffix() {
        return fileSuffix;
    }

    public void setFileSuffix(String fileSuffix) {
        this.fileSuffix = fileSuffix;
    }

    public InputStream getFileInputStream() {
        return fileInputStream;
    }

    public void setFileInputStream(InputStream fileInputStream) {
        this.fileInputStream = fileInputStream;
    }
}
