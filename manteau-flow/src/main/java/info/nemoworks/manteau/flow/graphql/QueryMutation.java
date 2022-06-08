package info.nemoworks.manteau.flow.graphql;

import graphql.language.Document;
import graphql.parser.Parser;

public class QueryMutation{


    private String graphql;


    QueryMutation(String graphql){
        this.graphql = graphql;
    }


    public Document getSchema(){
        Parser parser = new Parser();


        return parser.parse(graphql);
    }


}
