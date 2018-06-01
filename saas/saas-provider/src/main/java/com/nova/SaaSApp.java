package com.nova;

import com.nova.paas.common.support.NovaBeanNameGenerator;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.system.ApplicationPidFileWriter;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableAsync
@EnableTransactionManagement
@SpringBootApplication
@ComponentScan(basePackages = {"com.nova"})
@MapperScan(basePackages = "com.nova.**.mapper")
public class SaaSApp extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication webApp = new SpringApplication(SaaSApp.class);
        webApp.addListeners(new ApplicationPidFileWriter());
        webApp.setBeanNameGenerator(new NovaBeanNameGenerator());
        webApp.run(args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(SaaSApp.class);
    }
}
