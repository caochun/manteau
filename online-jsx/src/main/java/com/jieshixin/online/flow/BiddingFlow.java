package com.jieshixin.online.flow;

import com.qwlabs.graphql.builder.Gql;
import com.qwlabs.graphql.builder.GqlField;
import info.nemoworks.manteau.flow.core.AbstractProcess;
import info.nemoworks.manteau.flow.core.Task;
import org.apache.commons.scxml2.model.ModelException;

import java.util.List;

public class BiddingFlow extends AbstractProcess {

    Gql gql= Gql.query("contents")
            .fields(GqlField.of("nodes").fields("id","content","createdAt"),
                    GqlField.of("totalCount"),
                    GqlField.of("pageInfo").fields("limit","offset"));



    private static final String SCXML_MODEL = "scxml/bidchart.xml";

    public BiddingFlow() throws ModelException {
        super(BiddingFlow.class.getClassLoader().getResource(SCXML_MODEL));
    }

    @Override
    public Task getTask(String state) {
        switch (state) {
            case "init":
                return Task.builder()
                        .subject("Creator")
                        .expectedCommands(List.of())
                        .query(null)
                        .process(this)
                        .build();
            case "editing":
                return Task.builder()
                        .subject("Editor")
                        .expectedCommands(List.of())
                        .query(null)
                        .process(this)
                        .build();
            case "reviewing":
                return Task.builder()
                        .subject("Reviewer")
                        .expectedCommands(List.of())
                        .query(null)
                        .process(this)
                        .build();
            case "tracking":
                return Task.builder()
                        .subject("Tracker")
                        .expectedCommands(List.of())
                        .query(null)
                        .process(this)
                        .build();
            case "closed":
                return null;
        }
        return null;
    }


}

