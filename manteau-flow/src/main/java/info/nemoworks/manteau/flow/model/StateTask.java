package info.nemoworks.manteau.flow.model;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.scxml2.*;
import org.apache.commons.scxml2.model.Action;
import org.apache.commons.scxml2.model.ModelException;

public class StateTask extends Action {

    Log log;

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private String completeEvent;


    public StateTask() {
        log = LogFactory.getLog(this.getClass());
    }

    private ActionExecutionContext executionContext;

    @Override
    public void execute(ActionExecutionContext actionExecutionContext) throws ModelException, SCXMLExpressionException {
        log.info("task " + this.getName() + " executing");
        this.executionContext = actionExecutionContext;
        complete();
    }

    public void complete() {
        TriggerEvent event = new TriggerEvent(this.getCompleteEvent(), TriggerEvent.SIGNAL_EVENT, this.getCompleteEvent());
        this.executionContext.getInternalIOProcessor().addEvent(event);
        log.info("task " + this.getName() + " completed");
    }

    public void accept() {

    }
}
