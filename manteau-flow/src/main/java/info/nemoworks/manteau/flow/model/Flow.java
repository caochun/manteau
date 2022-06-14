package info.nemoworks.manteau.flow.model;

import org.apache.commons.scxml2.env.AbstractStateMachine;
import org.apache.commons.scxml2.model.ModelException;

import java.net.URL;

public class Flow extends AbstractStateMachine {
    public Flow(URL scxmlDocument) throws ModelException {
        super(scxmlDocument);
    }
}
