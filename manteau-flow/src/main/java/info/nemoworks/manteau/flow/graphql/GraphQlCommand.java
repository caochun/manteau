package info.nemoworks.manteau.flow.graphql;

import graphql.schema.GraphQLSchema;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import info.nemoworks.manteau.flow.core.Command;

public class GraphQlCommand extends QueryMutation implements Command {

    public GraphQlCommand(String graphql) {
        super(graphql);
    }

    @Override
    public String getCommandString() {
        return null;
    }



}
