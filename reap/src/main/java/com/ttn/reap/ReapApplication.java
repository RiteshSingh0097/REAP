package com.ttn.reap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories("com.ttn.reap.repositories")
public class ReapApplication {
    
    public static void main(String[] args) {
        System.out.println("Application up");
        SpringApplication.run(ReapApplication.class, args);
    }
}
