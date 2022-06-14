package info.nemoworks.manteau.data;

import graphql.language.Document;
import graphql.parser.Parser;
import info.nemoworks.manteau.flow.CommandQuery;

public class QLQueryMutation implements CommandQuery {

    private String graphql;
    private String id;

    public QLQueryMutation(String graphql, String id){
        this.graphql = graphql;
        this.id = id;
    }

    public Document getSchema(){
        Parser parser = new Parser();
        return parser.parse(graphql);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getCQString() {
        return graphql;
    }
}
