package com.jieshixin.online.flow;

import com.google.common.collect.ImmutableMap;
import info.nemoworks.manteau.flow.CommandQuery;
import info.nemoworks.manteau.flow.Task;
import info.nemoworks.manteau.data.QLQueryMutation;
import org.apache.commons.scxml2.model.ModelException;
import org.json.JSONObject;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class FlowRunner {

    private JSONObject executeQl(QLQueryMutation queryMutation){

        WebClient webClient = WebClient.builder().baseUrl("http://localhost:8080/graphql").build();
        webClient.post();
        HttpGraphQlClient graphQlClient = HttpGraphQlClient.builder(webClient)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

        JSONObject result;


        ImmutableMap<String, Object> vars = ImmutableMap.of("id", "u1","name", "jason");
        Mono mono = graphQlClient.document(queryMutation.getCQString()).variables(vars).execute().map(r->r.getData());

        mono.subscribe();

        return new JSONObject((Map) mono.block());

    }

    private BiddingFlow biddingFlow;

    public FlowRunner() throws ModelException {
        biddingFlow = new BiddingFlow();
    }


    public void simrun() throws IOException {

        Task task = biddingFlow.getPendingTask();

        while (task!=null){


            task.accept();
            QLQueryMutation query = (QLQueryMutation) task.getQuery();

            JSONObject qResult = executeQl(query);

            List<CommandQuery> commands = task.getExpectedCommands();

            JSONObject cResult = executeQl((QLQueryMutation) commands.get(0));
            task.complete(commands.get(0));

        }

    }


    public static void main(String[] args) throws ModelException, IOException {
        new FlowRunner().simrun();
    }

}
