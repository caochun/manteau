package info.nemoworks.manteau.data;

import graphql.schema.GraphQLCodeRegistry;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import org.neo4j.graphql.DataFetchingInterceptor;
import org.neo4j.graphql.SchemaBuilder;
import org.neo4j.graphql.SchemaConfig;
import org.springframework.boot.autoconfigure.graphql.GraphQlSourceBuilderCustomizer;
import org.springframework.core.io.Resource;
import org.springframework.graphql.execution.GraphQlSource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class Neo4jGraphQlSourceBuilder implements GraphQlSourceBuilderCustomizer {

    private DataFetchingInterceptor dataFetchingInterceptor;
    private SchemaBuilder schemaBuilder;

    private TypeDefinitionRegistry neo4jTypeDefinitionRegistry;

    public Neo4jGraphQlSourceBuilder(DataFetchingInterceptor dataFetchingInterceptor, Resource graphQlSource) throws IOException {

        String schema = new String(graphQlSource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

        this.neo4jTypeDefinitionRegistry = new SchemaParser().parse(schema);
        this.schemaBuilder = new SchemaBuilder(neo4jTypeDefinitionRegistry, new SchemaConfig(
                new SchemaConfig.CRUDConfig(),
                new SchemaConfig.CRUDConfig(true, List.of()),
                false, true, SchemaConfig.InputStyle.INPUT_TYPE, true, true));
        this.schemaBuilder.augmentTypes();

        this.dataFetchingInterceptor = dataFetchingInterceptor;
    }


    @Override
    public void customize(GraphQlSource.SchemaResourceBuilder builder) {
        builder.configureRuntimeWiring(runtimeWiringBuilder -> {
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
}
