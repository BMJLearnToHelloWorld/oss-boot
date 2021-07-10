package com.boot.oss.service;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.List;

/**
 * @author bmj
 */
public interface ObjectOperationService {

    String uploadObjectToBucket(String bucket, MultipartFile multipartFile, String preObjId, String userName) throws Exception;

    String uploadByStream(String bucket, InputStream is, String filePrefix, String fileSuffix, String userName) throws Exception;

    List<String> unZipUpload(String objId, String userName) throws Exception;

    void downloadOne(String objId, HttpServletResponse response) throws Exception;

    void downloadZip(List<String> fileIdList, String fileName, HttpServletResponse response) throws Exception;

    void removeObjectFromBucket(String objId) throws Exception;
}
