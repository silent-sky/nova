package com.nova.saas.hr.starter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableAsync
@EnableTransactionManagement
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, MongoAutoConfiguration.class})
//public class AppStarter extends SpringBootServletInitializer implements EmbeddedServletContainerCustomizer {
public class AppStarter {
    //    public static void main(String[] args) {
    //        SpringApplication.run(AppStarter.class, args);
    //    }
    //
    //    @Override
    //    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
    //        return builder.sources(AppStarter.class);
    //    }
    //
    //    @Override
    //    public void customize(ConfigurableEmbeddedServletContainer container) {
    //        container.setPort(8081);
    //    }

    public static void main(String[] args) {
        SpringApplication.run(AppStarter.class, args);
    }
}
