package com.boot.oss.service.impl;

import com.boot.oss.entity.ObjInfoEntity;
import com.boot.oss.mapper.ObjInfoMapper;
import com.boot.oss.service.ObjectOperationService;
import com.boot.oss.service.model.FileModel;
import com.boot.oss.util.MinioTemplate;
import com.boot.oss.util.UUIDUtil;
import io.minio.StatObjectResponse;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * @author bmj
 */
@Service("ObjectOperationService")
public class ObjectOperationServiceImpl implements ObjectOperationService {

    private final MinioTemplate minioTemplate;

    private final ObjInfoMapper objInfoMapper;

    public ObjectOperationServiceImpl(MinioTemplate minioTemplate, ObjInfoMapper objInfoMapper) {
        this.minioTemplate = minioTemplate;
        this.objInfoMapper = objInfoMapper;
    }

    @Override
    public String uploadObjectToBucket(String bucket, MultipartFile multipartFile, String preObjId, String userName) throws Exception {
        // bucket不存在就创建
        minioTemplate.createBucket(bucket);
        // 得到文件流
        final InputStream is = multipartFile.getInputStream();
        System.out.println(String.valueOf(is.available()));
        // 获取文件名，不包括后缀名
        final String filePrefix = multipartFile.getOriginalFilename().substring(0, multipartFile.getOriginalFilename().lastIndexOf("."));
        // 获取文件后缀名
        final String fileSuffix = multipartFile.getOriginalFilename().substring(multipartFile.getOriginalFilename().lastIndexOf(".") + 1);
        System.out.println(filePrefix + " ===== " + fileSuffix);
        return uploadOneByStream(bucket, is, filePrefix, fileSuffix, preObjId, userName);
    }

    private String uploadOneByStream(String bucket, InputStream is, String filePrefix, String fileSuffix, String preObjId, String userName) throws Exception {
        // 生成文件id和存放路径
        String get32uuid = UUIDUtil.get32Uuid();
        String actualDestPath = UUIDUtil.convertUuidToPath(get32uuid);
        // 把文件放到minio的桶里面
        minioTemplate.putObject(bucket, actualDestPath + get32uuid, is);
        // 保存到DB
        objInfoMapper.insertObjInfo(new ObjInfoEntity(get32uuid, filePrefix, fileSuffix, actualDestPath, preObjId, userName, userName, bucket));
        // 关闭输入流
        is.close();

        return get32uuid;
    }

    @Override
    public String uploadByStream(String bucket, InputStream is, String filePrefix, String fileSuffix, String userName) throws Exception {
        // bucket不存在就创建
        minioTemplate.createBucket(bucket);
        return uploadOneByStream(bucket, is, filePrefix, fileSuffix, null, userName);
    }

    @Override
    public List<String> unZipUpload(String objId, String userName) throws Exception {
        Map<String, Object> selectedZipFile = objInfoMapper.selectObjInfo(objId);
        if (selectedZipFile == null || selectedZipFile.size() == 0 || selectedZipFile.get("objPath") == null) {
            throw new Exception("Zip file does not exist in server, please check!");
        }
        String bucket = selectedZipFile.get("bucket") == null ? "" : (String) selectedZipFile.get("bucket");
        ZipInputStream zipInputStream = new ZipInputStream(minioTemplate.getObject(bucket, selectedZipFile.get("objPath") + objId), Charset.forName("GBK"));
        List<String> unzippedObjIdList = new ArrayList<String>();
        unZipBeforeUpload(zipInputStream, objId, unzippedObjIdList, userName, bucket);
        zipInputStream.closeEntry();
        zipInputStream.close();

        return unzippedObjIdList;
    }

    private void unZipBeforeUpload(ZipInputStream zipInputStream, String objId, List<String> unzippedObjIdList, String userName, String bucket) throws Exception {
        ZipEntry zipEntry = null;
        while ((zipEntry = zipInputStream.getNextEntry()) != null) {
            if (zipEntry.isDirectory()) {

            } else {
                // 创建字节数组输出流，用于返回压缩后的输出流字节数组
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                FileModel fileModel = new FileModel();

                fileModel.setFilePrefix(zipEntry.getName().substring(0, zipEntry.getName().lastIndexOf(".")).substring(zipEntry.getName().lastIndexOf("/") + 1));
                fileModel.setFileSuffix(zipEntry.getName().substring(zipEntry.getName().lastIndexOf(".") + 1));

                byte[] bytes = new byte[1024 * 2];
                int len = 0;
                while ((len = zipInputStream.read(bytes, 0, 1024 * 2)) != -1) {
                    byteArrayOutputStream.write(bytes, 0, len);
                }
                InputStream inputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
                byteArrayOutputStream.close();
                fileModel.setFileInputStream(inputStream);
                unzippedObjIdList.add(uploadOneByStream(bucket, fileModel.getFileInputStream(), fileModel.getFilePrefix(), fileModel.getFileSuffix(), objId, userName));
                inputStream.close();
            }
        }
    }

    @Override
    public void downloadOne(String objId, HttpServletResponse response) throws Exception {
        // 通过fileId获取文件信息
        Map<String, Object> fileInfo = objInfoMapper.selectObjInfo(objId);
        // 拼接存储在bucket的路劲及文件名
        String objInBucketWithPath = (fileInfo.get("objPath") == null ? "" : (String) fileInfo.get("objPath")) + objId;
        // 拼接原始的文件名包括后缀名
        String objNameWithSuffix = (fileInfo.get("objName") == null ? "" : (String) fileInfo.get("objName")) + "." + (fileInfo.get("objSuffix") == null ? "" : (String) fileInfo.get("objSuffix"));
        String bucket = fileInfo.get("bucket") == null ? "" : (String) fileInfo.get("bucket");

        StatObjectResponse objectStat = minioTemplate.statObject(bucket, objInBucketWithPath);
        response.setContentType(objectStat.contentType());
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(objNameWithSuffix, "UTF-8"));
        System.out.println(objNameWithSuffix);
        InputStream in = minioTemplate.getObject(bucket, objInBucketWithPath);
        IOUtils.copy(in, response.getOutputStream());
        in.close();
    }

    @Override
    public void downloadZip(List<String> objIdList, String fileName, HttpServletResponse response) throws Exception {
        downloadAfterZip(objIdList, fileName, response);
    }

    private void downloadAfterZip(List<String> objIdList, String fileName, HttpServletResponse response) throws Exception {
        if (objIdList == null) {
            throw new Exception("The object list is null, please check!");
        }

        // 生成文件id和存放路径
        String get32uuid = UUIDUtil.get32Uuid();

        // 创建字节数组输出流，用于返回压缩后的输出流字节数组
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        // 创建压缩输出流
        ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream);

        List<String> fileNameWithoutDuplicate = new ArrayList<String>();

        for (String objId : objIdList) {
            // 通过fileId获取文件信息
            Map<String, Object> fileInfo = objInfoMapper.selectObjInfo(objId);
            // 拼接存储在bucket的路劲及文件名
            String fileNameInBucketWithPath = (fileInfo.get("objPath") == null ? "" : (String) fileInfo.get("objPath")) + objId;
            String oriFileNamePrefix = fileInfo.get("objName") == null ? "" : (String) fileInfo.get("objName");
            int index = 1;
            while (fileNameWithoutDuplicate.contains(oriFileNamePrefix)) {
                oriFileNamePrefix = fileInfo.get("objName") + " (" + index + ")";
                index++;
            }
            fileNameWithoutDuplicate.add(oriFileNamePrefix);
            // 拼接原始的文件名包括后缀名
            String oriFileNameWithSuffix = oriFileNamePrefix + "." + (fileInfo.get("objSuffix") == null ? "" : (String) fileInfo.get("objSuffix"));

            String bucket = fileInfo.get("bucket") == null ? "" : (String) fileInfo.get("bucket");
            InputStream in = minioTemplate.getObject(bucket, fileNameInBucketWithPath);
            inputReader(in, zipOutputStream, oriFileNameWithSuffix);

            // 关闭流
            byteArrayOutputStream.flush();
            in.close();
        }
        // 需要先关闭zip流，再对byteArrayOutputStream做操作，不然最后写入的文件会是0kb
        zipOutputStream.closeEntry();
        zipOutputStream.close();

        // 下载zip
        String zipFileName = fileName == null || fileName.isEmpty() ? "zip_generated_" + get32uuid : fileName;
        String fileSuffix = "zip";
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(zipFileName + "." + fileSuffix, "UTF-8"));
        IOUtils.copy(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()), response.getOutputStream());

        byteArrayOutputStream.close();
    }

    private void inputReader(InputStream in, ZipOutputStream zipOutputStream, String oriFileNameWithsuffix) throws Exception {
        byte[] byteBuffer = new byte[1024 * 2];
        ZipEntry zipEntry = new ZipEntry(oriFileNameWithsuffix);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(in, 1024 * 2);
        zipOutputStream.putNextEntry(zipEntry);
        int len = 0;
        while ((len = bufferedInputStream.read(byteBuffer, 0, 1024 * 2)) != -1) {
            zipOutputStream.write(byteBuffer, 0, len);
        }
        if (null != bufferedInputStream) {
            bufferedInputStream.close();  //关闭
        }
    }

    @Override
    public void removeObjectFromBucket(String objId) throws Exception {
        // 通过fileId获取文件信息
        Map<String, Object> objInfo = objInfoMapper.selectObjInfo(objId);
        // 拼接存储在bucket的路劲及文件名
        String objInBucketWithPath = (objInfo.get("objPath") == null ? "" : (String) objInfo.get("objPath")) + objId;
        String bucket = objInfo.get("bucket") == null ? "" : (String) objInfo.get("bucket");
        minioTemplate.removeObject(bucket, objInBucketWithPath);
        objInfoMapper.deleteObjInfo(objId);
    }
}
