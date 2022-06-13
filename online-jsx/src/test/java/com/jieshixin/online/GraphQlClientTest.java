package com.jieshixin.online;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.graphql.client.ClientGraphQlRequest;
import org.springframework.graphql.client.ClientGraphQlResponse;
import org.springframework.graphql.client.GraphQlClientInterceptor;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GraphQlClientTest {
    static String createUser1 = "mutation createUser($id: ID, $name: String){createUser(id:$id, name:$name){ name } }";
    static String queryUsers = "query{ users{ id name } }";

    @Test
    public void testHttpClient_Mutation() {
        WebClient webClient = WebClient.builder().baseUrl("http://localhost:8080/graphql").build();
        webClient.post();
        HttpGraphQlClient graphQlClient = HttpGraphQlClient.builder(webClient)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .interceptor(new MyInterceptor())
                .build();


        ImmutableMap<String, Object> vars = ImmutableMap.of("id", "u1","name", "jason");
        Mono<JSONObject> mono = graphQlClient.document(createUser1).variables(vars).execute().map(r -> {

            return new JSONObject(r.<Map>getData());
        });

        mono.subscribe(e -> {
            System.out.println(e);

        });

        mono.block();

    }

    static class MyInterceptor implements GraphQlClientInterceptor {

        @Override
        public Mono<ClientGraphQlResponse> intercept(ClientGraphQlRequest request, Chain chain) {

            return chain.next(request);
        }

        @Override
        public Flux<ClientGraphQlResponse> interceptSubscription(ClientGraphQlRequest request, SubscriptionChain chain) {
            return chain.next(request);
        }

    }
}
