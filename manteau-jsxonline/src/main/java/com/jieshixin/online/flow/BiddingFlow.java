package com.jieshixin.online.flow;

import info.nemoworks.manteau.flow.AbstractProcess;
import info.nemoworks.manteau.flow.Task;
import info.nemoworks.manteau.data.QLQueryMutation;
import org.apache.commons.scxml2.model.ModelException;

import java.util.List;

public class BiddingFlow extends AbstractProcess {

    static String createUser1 = "mutation{createUser(id:\"user1\", name: \"Alice\"){ name } }";
    static String createUser2 = "mutation{createUser(id:\"user2\", name: \"Bob\"){ name } }";
    static String createUser3 = "mutation{createUser(id:\"user2\", name: \"Tom\"){ name } }";

    static String queryBids = "query{  bids{ id title } }";
    static String queryBid1 = "query{  bids(where:{id: \"bid1\"}){ title content  } }";

    static String createBid1 = "mutation{createBid(id:\"bid1\", title:\"new\"){ id } }";

    static String addBid1Creator1 = "mutation{ addBidCreator(id: \"bid1\", creator: \"user1\"){  creator{ name } } }";

    static String updateBid1Content = "mutation{ updateBid(id: \"bid1\", content:\"content1\"){ id title content } }";



    public BiddingFlow() throws ModelException {
        super(BiddingFlow.class.getClassLoader().getResource("scxml/bidding.xml"));
    }

    @Override
    public Task getTask(String state) {
        switch (state) {
            case "init":
                return Task.builder()
                        .subject("Creator")
                        .expectedCommands(List.of(new QLQueryMutation(createBid1,"create"),new QLQueryMutation(addBid1Creator1,"addCreator")))
                        .query(new QLQueryMutation(queryBids,"query"))
                        .process(this)
                        .build();
            case "editing":
                return Task.builder()
                        .subject("Editor")
                        .expectedCommands(List.of(new QLQueryMutation(updateBid1Content,"save"), new QLQueryMutation(updateBid1Content, "submit")))
                        .query(new QLQueryMutation(queryBid1, "query"))
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

