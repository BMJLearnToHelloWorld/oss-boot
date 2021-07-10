package com.boot.oss.controller;

import com.alibaba.fastjson.JSON;
import com.boot.oss.constant.ConstantData;
import com.boot.oss.constant.ConstantResult;
import com.boot.oss.constant.ResultModel;
import com.boot.oss.service.ObjectOperationService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.List;

/**
 * @author bmj
 */
@RestController
@RequestMapping("/oss")
public class OssController {

    private final ObjectOperationService objectOperationService;

    public OssController(ObjectOperationService objectOperationService) {
        this.objectOperationService = objectOperationService;
    }

    /**
     * 下载单个文件
     *
     * @return void
     * @throws Exception Exception
     */
    @GetMapping(value = "/object/{objId}")
    public void downloadOne(@PathVariable("objId") String objId, HttpServletResponse response, HttpServletRequest request) throws Exception {
        // TODO: add auth here if need and get userinfo
        try {
            objectOperationService.downloadOne(objId, response);
            System.out.println("下载成功");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("下载失败");
        }
    }

    /**
     * 下载多个文件并打成zip
     *
     * @param zipContent
     * @param response
     * @return void
     * @throws Exception Exception
     */
    @GetMapping(value = "/object")
    public void downloadZip(@RequestParam("zipContent") String zipContent, @RequestParam(value = "fileName", required = false) String fileName, HttpServletResponse response, HttpServletRequest request) throws Exception {
        // TODO: add auth here if need and get userinfo
        // decode to list
        // URL encoded: zipContnet = %5B%22cd67335725134aa4aa5ccdc78aadcfb9%22%2C%20%224dfdbaf90728410aad4eb9405c55ea31%22%2C%20%2210dacbe5789a40fb9b5f00066ca54f41%22%5D
        // URL decoded: zipContent = ["4dfdbaf90728410aad4eb9405c55ea31","10dacbe5789a40fb9b5f00066ca54f41"]
        String decodedContent = URLDecoder.decode(zipContent, "UTF-8");
        int count = 0;
        while (decodedContent.indexOf("%") > -1 && count < 2) {
            decodedContent = URLDecoder.decode(decodedContent, "UTF-8");
            count++;
        }
        List<String> objIdList = JSON.parseArray(decodedContent, String.class);
        try {
            objectOperationService.downloadZip(objIdList, fileName, response);
            System.out.println("下载成功");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("下载失败");
        }
    }

    /**
     * 上传文件
     *
     * @param obj
     * @return String
     * @throws Exception Exception
     */
    @PostMapping(value = "/object")
    public ResultModel uploadOne(@RequestParam("bucket") String bucket, @RequestParam("obj") MultipartFile obj, HttpServletRequest request) throws Exception {
        ResultModel resultModel = null;
        // TODO: add auth here if need and get userinfo
        if (obj.isEmpty()) {
            throw new Exception("上传文件不能为空");
        } else {
            resultModel = ConstantResult.setResultModel(ConstantData.SUCCESS_CODE, "upload success", objectOperationService.uploadObjectToBucket(bucket, obj, null, ""));
            System.out.println("上传成功");
        }
        return resultModel;
    }

    /**
     * 上传文件流
     *
     * @param httpServletRequest
     * @return String
     * @throws Exception Exception
     */
    @PostMapping(value = "/stream")
    public ResultModel uploadStream(HttpServletRequest httpServletRequest, HttpServletRequest request) throws Exception {
        ResultModel resultModel = null;
        // TODO: add auth here if need and get userinfo
        if (httpServletRequest instanceof MultipartHttpServletRequest) {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) httpServletRequest;
            // 通过表单中的参数名来接收文件流（可用 file.getInputStream() 来接收输入流）
            InputStream inputStream = multipartRequest.getFile("obj").getInputStream();
            if (inputStream.available() < 0) {
                throw new Exception("上传文件不能为空");
            } else {
                System.out.println("文件流大小: " + String.valueOf(inputStream.available()));
            }
            // 接收其他表单参数
            String bucket = multipartRequest.getParameter("bucket");
            String prefix = multipartRequest.getParameter("prefix");
            String suffix = multipartRequest.getParameter("suffix");
            System.out.println("bucket: " + bucket);
            System.out.println("文件名: " + prefix);
            System.out.println("后缀名: " + suffix);
            String objId = objectOperationService.uploadByStream(bucket, inputStream, prefix, suffix, "");
            System.out.println("objId: " + objId);
            resultModel = ConstantResult.setResultModel(ConstantData.SUCCESS_CODE, "upload success", objId);
            inputStream.close();
        } else {
            throw new Exception("不是 MultipartHttpServletRequest");
        }
        return resultModel;
    }

    /**
     * 获取zip并解压上传文件
     *
     * @param objId
     * @return String
     * @throws Exception Exception
     */
    @PutMapping(value = "/object/{objId}")
    public ResultModel unZipUpload(@PathVariable("objId") String objId, HttpServletRequest request) throws Exception {
        ResultModel resultModel = null;
        // TODO: add auth here if need and get userinfo
        resultModel = ConstantResult.setResultModel(ConstantData.SUCCESS_CODE, "upload success", objectOperationService.unZipUpload(objId, ""));
        System.out.println("解压成功");
        return resultModel;
    }

    /**
     * 删除文件
     *
     * @param objId
     * @return String
     * @throws Exception Exception
     */
    @DeleteMapping(value = "/object/{objId}")
    public void delete(@PathVariable("objId") String objId, HttpServletRequest request) throws Exception {
        // TODO: add auth here if need and get userinfo
        try {
            objectOperationService.removeObjectFromBucket(objId);
            System.out.println("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("删除失败");
        }
    }

}
