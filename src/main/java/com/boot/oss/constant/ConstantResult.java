package com.boot.oss.constant;

import org.springframework.util.StringUtils;

/**
 * @author bmj
 */
public class ConstantResult {

    public static ResultModel setResultModel(String resultCode, String resultMsg, Object data) {
        ResultModel resultModel = new ResultModel();
        resultModel.setResultCode(resultCode);
        if (resultMsg == null) {
            resultModel.setResultMsg("error");
        } else {
            resultModel.setResultMsg("".equals(resultMsg) || resultMsg == null ? "" : resultMsg);
        }
        resultModel.setData(data);
        return resultModel;
    }

    public static ResultModel setResultModel(String resultCode, String msgFormat, Object data, Object... args) {
        ResultModel resultModel = new ResultModel();
        resultModel.setResultCode(resultCode);

        String resultMsg = msgFormat;

        if (resultMsg == null) {
            resultMsg = "error";
        } else if (StringUtils.isEmpty(msgFormat)) {
            resultMsg = msgFormat;
        } else if (msgFormat.contains("Exception") || resultMsg.contains("Connection refused")) {
            resultMsg = "error";
        } else {
            resultMsg = "".equals(msgFormat) || msgFormat == null ? "" : msgFormat;
        }
        resultMsg = String.format(resultMsg, args);
        resultModel.setResultMsg(resultMsg);
        resultModel.setData(data);
        return resultModel;
    }

}
