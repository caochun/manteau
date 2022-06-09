package com.jieshixin.online;

import org.apache.commons.scxml2.model.ModelException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OnlineApplication  {
    public static void main(String[] args) throws ModelException {
        SpringApplication.run(OnlineApplication.class, args);
    }

}
