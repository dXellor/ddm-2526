package com.ddm.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication

// Postgres JPA config
@EnableJpaRepositories(
        basePackages = "com.ddm.server.dll.repositories.postgres"
)
// Elastic config
@EnableElasticsearchRepositories(
        basePackages = "com.ddm.server.dll.repositories.es"
)
public class ServerApplication {

    public static void main(String[] args) {
        try{
            SpringApplication.run(ServerApplication.class, args);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
