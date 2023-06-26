package com.artificialncool.hostapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication  (exclude={DataSourceAutoConfiguration.class})
@EnableMongoRepositories
public class HostappApplication {

//    @Value("${spring.application.name}")
//    private String applicationName;

    public static void main(String[] args) {
        SpringApplication.run(HostappApplication.class, args);
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedOrigins("**");
            }
        };
    }

//    @Bean
//    public Tracer tracer() {
//            Configuration.SamplerConfiguration samplerConfig = Configuration.SamplerConfiguration.fromEnv().withType("const").withParam(1);
//            Configuration.ReporterConfiguration reporterConfig = Configuration.ReporterConfiguration.fromEnv().withLogSpans(true);
//            Configuration config = new Configuration(applicationName).withSampler(samplerConfig).withReporter(reporterConfig);
//            return config.getTracer();
//    }


//        Configuration.SenderConfiguration senderConfiguration = Configuration.SenderConfiguration.fromEnv()
//                .withAgentHost("jaeger-collector")
//                .withAgentPort(14268);
//
//        Configuration.ReporterConfiguration reporterConfiguration = Configuration.ReporterConfiguration.fromEnv()
//                .withSender(senderConfiguration)
//                .withLogSpans(true);
//
//        Configuration configuration = new Configuration(serviceName)
//                .withReporter(reporterConfiguration)
//                .withSampler(Configuration.SamplerConfiguration.fromEnv().withType("const").withParam(1));
//
//        return configuration.getTracer();
//    }
}
