package com.nova;

import com.nova.paas.common.support.NovaBeanNameGenerator;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.system.ApplicationPidFileWriter;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableAsync
@EnableTransactionManagement
@SpringBootApplication(exclude = {MongoAutoConfiguration.class})
//@MapperScan("com.nova.paas.auth.mapper")
@ComponentScan(basePackages = {"com.nova"})
@MapperScan(basePackages = "com.nova.*.mapper")
public class HRApp extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication webApp = new SpringApplication(HRApp.class);
        webApp.addListeners(new ApplicationPidFileWriter());
        webApp.setBeanNameGenerator(new NovaBeanNameGenerator());
        webApp.run(args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(HRApp.class);
    }
}
//public class HRApp extends WebMvcConfigurerAdapter {
//    public static void main(String[] args) {
//        SpringApplication webApp = new SpringApplication(HRApp.class);
//        webApp.addListeners(new ApplicationPidFileWriter());
//        webApp.setBeanNameGenerator(new NovaBeanNameGenerator());
//        webApp.run(args);
//    }
//
//    @Override
//    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
//        FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();
//        FastJsonConfig fastJsonConfig = new FastJsonConfig();
//        fastJsonConfig.setSerializerFeatures(SerializerFeature.PrettyFormat);
//        //处理中文乱码问题
//        List<MediaType> fastMediaTypes = new ArrayList<>();
//        fastMediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
//        fastConverter.setSupportedMediaTypes(fastMediaTypes);
//        fastConverter.setFastJsonConfig(fastJsonConfig);
//        converters.add(fastConverter);
//    }
//}
