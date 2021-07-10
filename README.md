# oss_boot project #

## 1. minio server create ##

windows :

	minio.exe server C:/workspace

docker in linux :

	mkdir -p /home/data/minio/data
	mkdir -p /home/data/minio/config
	chmod 777 /home/data/minio/data/

	docker pull minio/minio

	docker run -p 9000:9000 --name oss_server \
	  -e "MINIO_ACCESS_KEY=minioadmin" \
      -e "MINIO_SECRET_KEY=minioadmin" \
	  -v /home/data/minio/data:/data \
	  -v /home/data/minio/config:/root/.minio \
	  minio/minio server /data

## 2. springboot create ##

import minio maven dependency

	https://mvnrepository.com/artifact/io.minio/minio

minio config application.yml

	minio:
	url: http://127.0.0.1:9000
	accessKey: minioadmin
	secretKey: minioadmin
	bucketName: onenext-minio-bucket

init minioTemplate --封装调用miniClient的api

	statObject
	getObject
	putObject
	deleteObject

配置servlet上传文件大小 config application.yml

	spring:
	  servlet:
	    multipart:
	      enabled: true
	      maxFileSize: 100MB
	      maxRequestSize: 100MB
	      fileSizeThreshold: 100MB

---

*！注意！*

**如需要做用户认证，请在controller的TODO中添加request的解析即可，获取userinfo。**

---

create controller, service, impl --定义其他service可以调用的接口

| 功能 | rest mapping | api | params | request header | url example |
| ---- | ---- | ---- | ---- | ---- | ---- |
| 上传对象 | post | /oss/object | MultipartFile file, String bucket | HttpServletRequest | http://<span></span>127.0.0.1:8090/oss/object |
| 上传文件流 | post | /oss/stream | HttpServletRequest httpServletRequest | HttpServletRequest | http://<span></span>127.0.0.1:8090/oss/stream |
| 下载单个对象 | get | /oss/object/{objId} | String objId | HttpServletRequest | http://<span></span>127.0.0.1:8090/oss/object/01c0668bca0e46d79a25c269ddcf0605 |
| 下载多个对象打成zip | get | /oss/object | String zipContent(urlcode encode), String fileName(not required) | HttpServletRequest | http://<span></span>127.0.0.1:8090/oss/object?zipContent=%5B%2245fb2e9619b84ad6be7653f395e599b7%22%5D&fileName=filename |
| 删除文件服务器上的文件 | delete | /oss/object/{objId} | String objId | HttpServletRequest | http://<span></span>127.0.0.1:8090/oss/object/01c0668bca0e46d79a25c269ddcf0605 |
| 解压在server上的zip并上传zip里的对象 | put | /oss/object/{objId} | String objId | HttpServletRequest | http://<span></span>127.0.0.1:8090/oss/object/01c0668bca0e46d79a25c269ddcf0605 |

create mapper

	-- 上传对象，解压zip包时，做insert操作
	-- 下载单个或多个文件时，做select操作
	-- 删除服务器文件时，做delete操作

## 3. 操作表 -- oss_obj_info ##

| column | info |
| ---- | ---- |
| id: | PK |
| obj_id: | object id |
| obj_name: | 对象文件名（不包括后缀名） |
| obj_suffix: | 对象文件后缀名 |
| obj_path: | 对象存放的相对路径 |
| pre_obj_id: | 解压前zip文件的objId |
| bucket: | bucket name |

## 4. docker build ##

**将Dockerfile和jar放到同一目录下**

Dockerfile:

    FROM openjdk:8
    VOLUME /tmp
    ADD oss_boot-0.0.1-SNAPSHOT.jar app.jar
    RUN bash -c 'touch /app.jar'
    ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]

docker build & run:

    docker build -t oss_boot .
    docker run --name oss_boot -d -p 8091:8091 oss_boot

## 5. 把端口加到防火墙白名单 ##

    firewall-cmd --zone=public --list-ports
    firewall-cmd --zone=public  --add-port=8091/tcp --permanent
    firewall-cmd --reload

## Bucket命名规则 ##

+ 名称必须介于3到63个字符之间。

+ 名称只能包含小写字母，数字，点(.)和连字符(-)。

+ 名称必须以字母或数字开头和结尾。

+ 存储桶名称不得格式化为IP地址（例如192.168.5.4）。

+ 值区名称不能以xn--开头（对于2020年2月之后创建的值区）。

### Example Bucket names: ###

**以下示例存储桶名称有效，并遵循建议的命名准则：**

+ docexamplebucket

+ log-delivery-march-2020

+ my-hosted-content

**以下示例存储桶名称有效，但不建议用于静态网站托管以外的用途：**

+ docexamplewebsite.com

+ www<span></span>.<span></span>docexamplewebsite.com

+ my.example.s3.bucket

**以下示例存储桶名称无效：**

+ doc_example_bucket (包含下划线)

+ DocExampleBucket (包含大写字母)

+ doc-example-bucket- (以连字符'-'结尾)

更多请参阅：https://docs.aws.amazon.com/AmazonS3/latest/dev/BucketRestrictions.html

***
