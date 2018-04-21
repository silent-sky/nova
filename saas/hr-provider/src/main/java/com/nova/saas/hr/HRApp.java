package com.nova.saas.hr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableAsync
@EnableTransactionManagement
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, MongoAutoConfiguration.class})
public class HRApp {
    public static void main(String[] args) {
        SpringApplication.run(HRApp.class, args);
    }

}
