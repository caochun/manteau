package info.nemoworks.manteau.flow.graphql;

import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;

public class QueryMutation{


    private String graphql;


    QueryMutation(String graphql){
        this.graphql = graphql;
    }


    public TypeDefinitionRegistry getSchema(){
        SchemaParser parser = new SchemaParser();
        return parser.parse(graphql);
    }


}
