package com.fastcampus.projectboard2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class FastCampusProjectBoard2Application {

    public static void main(String[] args) {
        SpringApplication.run(FastCampusProjectBoard2Application.class, args);
    }

}
