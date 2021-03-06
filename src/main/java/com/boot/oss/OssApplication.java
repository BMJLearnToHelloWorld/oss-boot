package com.boot.oss;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

/**
 * @author bmj
 */
@SpringBootApplication
@MapperScan("com.boot.oss.mapper")
@ImportResource(locations = {"classpath:spring/spring-context.xml"})
public class OssApplication {

    public static void main(String[] args) {
        SpringApplication.run(OssApplication.class, args);
    }

}
