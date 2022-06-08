package com.jieshixin.online;

import com.jieshixin.online.flow.BiddingFlow;
import org.apache.commons.scxml2.model.ModelException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.graphql.execution.DefaultExecutionGraphQlService;
import org.springframework.graphql.execution.GraphQlSource;

@SpringBootApplication
public class OnlineApplication implements ApplicationRunner {
    public static void main(String[] args) throws ModelException {
        new BiddingFlow();
        SpringApplication.run(OnlineApplication.class, args);
    }

    static String createUser1 = "mutation{createUser(id:\"user1\", name: \"Alice\"){ name } }";

    DefaultExecutionGraphQlService defaultExecutionGraphQlService;
    @Autowired
    public void setDefaultExecutionGraphQlService(DefaultExecutionGraphQlService defaultExecutionGraphQlService) {
        this.defaultExecutionGraphQlService = defaultExecutionGraphQlService;
    }



    @Override
    public void run(ApplicationArguments args) throws Exception {

        Object r = graphQlSource.graphQl().execute(createUser1).getData();
        System.out.println(r);
    }
}
