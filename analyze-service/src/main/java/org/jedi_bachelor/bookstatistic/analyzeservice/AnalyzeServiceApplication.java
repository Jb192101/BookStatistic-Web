package org.jedi_bachelor.bookstatistic.analyzeservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@RefreshScope
//@EnableDiscoveryClient
@EnableAsync
@EnableMongoRepositories(basePackages = "org.jedi_bachelor.bookstatistic.analyzeservice.repository")
public class AnalyzeServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AnalyzeServiceApplication.class, args);
    }
}