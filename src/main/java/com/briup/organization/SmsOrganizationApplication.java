package com.briup.organization;

import com.spring4all.swagger.EnableSwagger2Doc;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.briup.organization.mapper")
@EnableSwagger2Doc
public class SmsOrganizationApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmsOrganizationApplication.class, args);
    }

}
