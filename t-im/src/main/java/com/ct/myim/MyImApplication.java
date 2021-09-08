package com.ct.myim;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class MyImApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyImApplication.class, args);
    }

}
