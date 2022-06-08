package com.jieshixin.online.config;

import info.nemoworks.menteau.graphql.Neo4jExecutor;
import info.nemoworks.menteau.graphql.Neo4jGraphQlSourceBuilder;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphql.DataFetchingInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.graphql.GraphQlSourceBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;

@Configuration
public class Neo4jGraphQLConfig {

    @Value("classpath:jsx.graphqls")
    Resource graphQlSource;

    @Bean
    public GraphQlSourceBuilderCustomizer graphQlSourceBuilderCustomizer(DataFetchingInterceptor dataFetchingInterceptor) throws IOException {
        return new Neo4jGraphQlSourceBuilder(dataFetchingInterceptor, graphQlSource);
    }

    @Bean
    public DataFetchingInterceptor dataFetchingInterceptor(@Autowired GraphDatabaseService graphDatabaseService) {
        return new Neo4jExecutor(graphDatabaseService);
    }


}
