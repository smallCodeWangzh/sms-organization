package com.briup.organization;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.briup.organization.mapper")
public class SmsOrganizationApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmsOrganizationApplication.class, args);
    }

}
