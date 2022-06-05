package info.nemoworks.menteau.graphql;

import graphql.schema.*;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Result;
import org.neo4j.driver.SessionConfig;
import org.neo4j.graphql.Cypher;
import org.neo4j.graphql.DataFetchingInterceptor;
import org.neo4j.graphql.SchemaBuilder;
import org.neo4j.graphql.SchemaConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.graphql.GraphQlSourceBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class Neo4GraphQLConfig {

    @Value("classpath:schema.graphql")
    Resource graphQl;

//
//    @Bean
//    GraphQLSchema graphQLSchema(DataFetchingInterceptor dataFetchingInterceptor) throws IOException {
//        String schema = new String(graphQl.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
//        GraphQLSchema graphQLSchema =  SchemaBuilder.buildSchema(schema, new SchemaConfig(), dataFetchingInterceptor);
//        return graphQLSchema;
//    }

    @Bean
    public GraphQlSourceBuilderCustomizer graphQlSourceBuilderCustomizer(DataFetchingInterceptor dataFetchingInterceptor) throws IOException {

        String schema = new String(graphQl.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

        TypeDefinitionRegistry neo4jTypeDefinitionRegistry = new SchemaParser().parse(schema);
        SchemaBuilder schemaBuilder = new SchemaBuilder(neo4jTypeDefinitionRegistry, new SchemaConfig(
                new SchemaConfig.CRUDConfig(),
                new SchemaConfig.CRUDConfig(true, List.of()),
                false, true, SchemaConfig.InputStyle.INPUT_TYPE, true, false));
        schemaBuilder.augmentTypes();

        return builder -> builder
                .configureRuntimeWiring(runtimeWiringBuilder -> {
                    schemaBuilder.registerTypeNameResolver(runtimeWiringBuilder);
                    schemaBuilder.registerScalars(runtimeWiringBuilder);
                    GraphQLCodeRegistry.Builder codeRegistryBuilder = GraphQLCodeRegistry.newCodeRegistry();
                    schemaBuilder.registerDataFetcher(codeRegistryBuilder, dataFetchingInterceptor);
                    runtimeWiringBuilder.codeRegistry(codeRegistryBuilder);
                })
                .schemaFactory((typeDefinitionRegistry, runtimeWiring) -> {
                    typeDefinitionRegistry.merge(neo4jTypeDefinitionRegistry);
                    return new SchemaGenerator().makeExecutableSchema(typeDefinitionRegistry, runtimeWiring);
                });
    }

    @Bean
    public DataFetchingInterceptor dataFetchingInterceptor(
            Driver driver,
            @Value("${database}") String database) {
        return (env, delegate) -> {
            Cypher cypher = delegate.get(env);
            return driver.session(SessionConfig.forDatabase(database)).writeTransaction(tx -> {
                Map<String, Object> boltParams = new HashMap<>(cypher.getParams());
                boltParams.replaceAll((key, value) -> toBoltValue(value));

                Result result = tx.run(cypher.getQuery(), boltParams);
                if (isListType(cypher.getType())) {
                    return result.list()
                            .stream()
                            .map(record -> record.get(cypher.getVariable()).asObject())
                            .collect(Collectors.toList());
                } else {
                    return result.list()
                            .stream()
                            .map(record -> record.get(cypher.getVariable()).asObject())
                            .collect(Collectors.toList())
                            .stream().findFirst()
                            .orElse(Collections.emptyMap());
                }
            });
        };
    }

    private Object toBoltValue(Object value) {
        if (value instanceof BigInteger) {
            return ((BigInteger) value).longValueExact();
        }
        if (value instanceof BigDecimal) {
            return ((BigDecimal) value).doubleValue();
        }
        return value;
    }

    private boolean isListType(GraphQLType type) {
        if (type instanceof GraphQLList) {
            return true;
        }
        return type instanceof GraphQLNonNull
                && this.isListType(((GraphQLNonNull) type).getWrappedType());
    }

}
