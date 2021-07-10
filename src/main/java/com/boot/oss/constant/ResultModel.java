package com.boot.oss.constant;

import java.io.Serializable;

/**
 * @author bmj
 */

public class ResultModel implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private String resultCode;

    private String resultMsg;

    private Object data;

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMsg() {
        return resultMsg;
    }

    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

}
