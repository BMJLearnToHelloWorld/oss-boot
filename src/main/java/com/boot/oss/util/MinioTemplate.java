package com.boot.oss.util;

import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.ListObjectsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveBucketArgs;
import io.minio.RemoveObjectArgs;
import io.minio.Result;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import io.minio.messages.Bucket;
import io.minio.messages.Item;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * @author bmj
 */
@Component
public class MinioTemplate implements InitializingBean {
    /**
     * minio地址+端口号
     */
    @Value("${minio.url}")
    private String url;
    /**
     * minio用户名
     */
    @Value("${minio.accessKey}")
    private String accessKey;
    /**
     * minio密码
     */
    @Value("${minio.secretKey}")
    private String secretKey;

    private MinioClient minioClient;

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.hasText(url, "Minio url 为空");
        Assert.hasText(accessKey, "Minio accessKey为空");
        Assert.hasText(secretKey, "Minio secretKey为空");
        this.minioClient = MinioClient.builder().endpoint(url).credentials(accessKey, secretKey).build();
    }

    /**
     * 创建bucket
     *
     * @param bucketName bucket名称
     */
    public void createBucket(String bucketName) throws Exception {
        if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }
    }

    /**
     * 获取全部bucket
     * <p>
     * https://docs.minio.io/cn/java-client-api-reference.html#listBuckets
     */
    public List<Bucket> getAllBuckets() throws Exception {
        return minioClient.listBuckets();
    }

    /**
     * 根据bucketName获取信息
     *
     * @param bucketName bucket名称
     */
    public Optional<Bucket> getBucket(String bucketName) throws Exception {
        return minioClient.listBuckets().stream().filter(b -> b.name().equals(bucketName)).findFirst();
    }

    /**
     * 根据bucketName删除信息
     *
     * @param bucketName bucket名称
     */
    public void removeBucket(String bucketName) throws Exception {
        minioClient.removeBucket(RemoveBucketArgs.builder().bucket(bucketName).build());
    }

    /**
     * 根据文件前缀查询文件
     *
     * @param bucketName bucket名称
     * @param prefix     前缀
     * @param recursive  是否递归查询
     * @return MinioItem 列表
     */
    public List getAllObjectsByPrefix(String bucketName, String prefix, boolean recursive) throws Exception {
        List<Item> list = new ArrayList<>();
        Iterable<Result<Item>> objectsIterator = minioClient.listObjects(ListObjectsArgs.builder().bucket(bucketName).prefix(prefix).recursive(recursive).build());
        if (objectsIterator != null) {
            Iterator<Result<Item>> iterator = objectsIterator.iterator();
            if (iterator != null) {
                while (iterator.hasNext()) {
                    Result<Item> result = iterator.next();
                    Item item = result.get();
                    list.add(item);
                }
            }
        }

        return list;
    }

    /**
     * 获取文件外链
     *
     * @param bucketName bucket名称
     * @param objectName 文件名称
     * @param expires    过期时间 <=7
     * @return url
     */
    public String getObjectUrl(String bucketName, String objectName, Integer expires) throws Exception {
        if (StringUtils.isEmpty(bucketName)) {
//            bucketName = this.bucketName;
            throw new Exception("bucketName is empty, please check!");
        }
        return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder().bucket(bucketName).object(objectName).expiry(expires).build());
    }

    /**
     * 获取文件路径
     *
     * @param bucketName
     * @param objectName
     * @return
     */
    public String getObjectUrl(String bucketName, String objectName) throws Exception {
        if (StringUtils.isEmpty(bucketName)) {
//            bucketName = this.bucketName;
            throw new Exception("bucketName is empty, please check!");
        }
        return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder().bucket(bucketName).object(objectName).expiry(7).build());
    }

    /**
     * 获取文件
     *
     * @param bucketName bucket名称
     * @param objectName 文件名称
     * @return 二进制流
     */
    public InputStream getObject(String bucketName, String objectName) throws Exception {
        if (StringUtils.isEmpty(bucketName)) {
//            bucketName = this.bucketName;
            throw new Exception("bucketName is empty, please check!");
        }
        return minioClient.getObject(GetObjectArgs.builder().bucket(bucketName).object(objectName).build());
    }

    /**
     * 获取文件
     *
     * @param bucketName
     * @param objectName
     * @return
     */
    public StatObjectResponse statObject(String bucketName, String objectName) throws Exception {
        if (StringUtils.isEmpty(bucketName)) {
//            bucketName = this.bucketName;
            throw new Exception("bucketName is empty, please check!");
        }
        return minioClient.statObject(StatObjectArgs.builder().bucket(bucketName).object(objectName).build());
    }

    /**
     * 上传文件
     *
     * @param bucketName bucket名称
     * @param objectName 文件名称
     * @param stream     文件流
     * @throws Exception https://docs.minio.io/cn/java-client-api-reference.html#putObject
     */
    public void putObject(String bucketName, String objectName, InputStream stream) throws Exception {
        if (StringUtils.isEmpty(bucketName)) {
//            bucketName = this.bucketName;
            throw new Exception("bucketName is empty, please check!");
        }
        minioClient.putObject(PutObjectArgs.builder().bucket(bucketName).object(objectName).stream(stream, stream.available(), -1).contentType("application/octet-stream").build());
    }

    /**
     * 上传文件
     *
     * @param bucketName bucket名称
     * @param objectName 文件名称
     * @param stream     文件流
     * @param size       大小
     * @throws Exception https://docs.minio.io/cn/java-client-api-reference.html#putObject
     */
    public void putObject(String bucketName, String objectName, InputStream stream, long size) throws Exception {
        if (StringUtils.isEmpty(bucketName)) {
//            bucketName = this.bucketName;
            throw new Exception("bucketName is empty, please check!");
        }
        minioClient.putObject(PutObjectArgs.builder().bucket(bucketName).object(objectName).stream(stream, stream.available(), size).contentType("application/octet-stream").build());
    }

    /**
     * 获取文件信息, 如果抛出异常则说明文件不存在
     *
     * @param bucketName bucket名称
     * @param objectName 文件名称
     * @throws Exception https://docs.minio.io/cn/java-client-api-reference.html#statObject
     */
    public StatObjectResponse getObjectInfo(String bucketName, String objectName) throws Exception {
        if (StringUtils.isEmpty(bucketName)) {
//            bucketName = this.bucketName;
            throw new Exception("bucketName is empty, please check!");
        }
        return minioClient.statObject(StatObjectArgs.builder().bucket(bucketName).object(objectName).build());
    }

    /**
     * 删除文件
     *
     * @param bucketName bucket名称
     * @param objectName 文件名称
     * @throws Exception https://docs.minio.io/cn/java-client-api-reference.html#removeObject
     */
    public void removeObject(String bucketName, String objectName) throws Exception {
        if (StringUtils.isEmpty(bucketName)) {
//            bucketName = this.bucketName;
            throw new Exception("bucketName is empty, please check!");
        }
        minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(objectName).build());
    }
}