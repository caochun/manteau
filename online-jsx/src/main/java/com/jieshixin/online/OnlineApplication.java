package com.jieshixin.online;

import com.jieshixin.online.flow.BiddingFlow;
import org.apache.commons.scxml2.model.ModelException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OnlineApplication {
    public static void main(String[] args) throws ModelException {
        new BiddingFlow();
        SpringApplication.run(OnlineApplication.class, args);
    }
}
