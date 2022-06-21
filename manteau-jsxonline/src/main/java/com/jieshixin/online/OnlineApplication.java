package com.jieshixin.online;

import com.google.common.io.Resources;
import info.nemoworks.manteau.flow.model.Flow;
import info.nemoworks.manteau.flow.model.Trace;
import org.apache.commons.scxml2.model.ModelException;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OnlineApplication implements ApplicationRunner {
    Flow flow;

    public static void main(String[] args) throws ModelException {
        SpringApplication.run(OnlineApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        flow = new Flow(Resources.getResource("statetask.xml"));
        flow.getTrace().getHeadTask().trigger("create", null);
        Trace.Node n = flow.getTrace().getHead();
    }

}
