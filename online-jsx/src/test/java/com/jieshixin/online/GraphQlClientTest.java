package com.jieshixin.online;

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

public class GraphQlClientTest {
    static String createUser1 = "mutation{createUser(id:\"user1\", name: \"Alice\"){ name } }";
    static String queryUsers= "query{ users{ id name } }";
    @Test
    public void testHttpClient_Mutation(){
        WebClient webClient = WebClient.builder().baseUrl("http://localhost:8080/graphql").build();
        webClient.post();
        HttpGraphQlClient graphQlClient = HttpGraphQlClient.builder(webClient)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .interceptor(new MyInterceptor())
                .build();

        List<Object> elements = new ArrayList<>();

        Mono<String> mono = graphQlClient.document(queryUsers).execute().map(r ->r.getData().toString());

        mono.subscribe(e -> System.out.println(e));

        System.out.println(elements.size());
    }

    static class MyInterceptor implements GraphQlClientInterceptor {

        @Override
        public Mono<ClientGraphQlResponse> intercept(ClientGraphQlRequest request, Chain chain) {
            // ...
            System.out.println("req");
            return chain.next(request);
        }

        @Override
        public Flux<ClientGraphQlResponse> interceptSubscription(ClientGraphQlRequest request, SubscriptionChain chain) {
            // ...
            return chain.next(request);
        }

    }
}
