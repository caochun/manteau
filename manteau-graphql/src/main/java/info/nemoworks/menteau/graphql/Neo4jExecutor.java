package info.nemoworks.menteau.graphql;

import graphql.schema.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphql.Cypher;
import org.neo4j.graphql.DataFetchingInterceptor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Neo4jExecutor implements DataFetchingInterceptor {

    private GraphDatabaseService graphDatabaseService;

    public Neo4jExecutor(GraphDatabaseService graphDatabaseService){
        this.graphDatabaseService = graphDatabaseService;
    }
    @Nullable
    @Override
    public Object fetchData(@NotNull DataFetchingEnvironment env, @NotNull DataFetcher<Cypher> delegate) throws Exception {
        Cypher cypher = delegate.get(env);
        Map<String, Object> boltParams = new HashMap<>(cypher.getParams());
        boltParams.replaceAll((key, value) -> toBoltValue(value));

        return graphDatabaseService.executeTransactionally(cypher.getQuery(), boltParams, r -> {
            if (isListType(cypher.getType())) {
                return r.stream()
                        .map(record -> record.get(cypher.getVariable()))
                        .collect(Collectors.toList());
            } else {
                return r.stream()
                        .map(record -> record.get(cypher.getVariable()))
                        .findFirst()
                        .orElse(Collections.emptyMap());
            }
        });
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
