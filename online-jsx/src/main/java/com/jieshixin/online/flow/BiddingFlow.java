package com.jieshixin.online.flow;

import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import info.nemoworks.manteau.flow.core.AbstractProcess;
import info.nemoworks.manteau.flow.core.Task;
import info.nemoworks.manteau.flow.graphql.GraphQlCommand;
import org.apache.commons.scxml2.model.ModelException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.client.GraphQlClient;
import org.springframework.graphql.execution.GraphQlSource;

import java.util.List;

public class BiddingFlow extends AbstractProcess {

    static String createUser1 = "mutation{createUser(id:\"user1\", name: \"Alice\"){ name } }";
    static String createUser2 = "mutation{createUser(id:\"user2\", name: \"Bob\"){ name } }";
    static String createUser3 = "mutation{createUser(id:\"user2\", name: \"Tom\"){ name } }";

    static String createBid1 = "mutation{createBid(id:\"bid1\", title:\"new\"){ id } }";

    static String addBid1Creator1 = "mutation{ addBidCreator(id: \"bid1\", creator: \"user1\"){  creator{ name } } }";


    private static final String SCXML_MODEL = "scxml/bidding.xml";



    public BiddingFlow() throws ModelException {
        super(BiddingFlow.class.getClassLoader().getResource(SCXML_MODEL));
//        GraphQlCommand cu = new GraphQlCommand(userCreate);




//        graphQL.execute(createUser1);

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

