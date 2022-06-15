package info.nemoworks.manteau.flow.model;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.scxml2.*;
import org.apache.commons.scxml2.model.Action;
import org.apache.commons.scxml2.model.ModelException;

public class Task extends Action {

    Log log;

    @Getter
    @Setter
    private String name;


    @Getter
    @Setter
    private String completeEvent;


    public Task() {
        log = LogFactory.getLog(this.getClass());
        log.info("task creating " + this.hashCode());

    }


    private ActionExecutionContext executionContext;

    @Override
    public void execute(ActionExecutionContext actionExecutionContext) throws ModelException, SCXMLExpressionException {

        log.info("task executing " + this.hashCode());

        this.executionContext = actionExecutionContext;
        complete();

    }




    public void complete(){
        TriggerEvent event = new TriggerEvent(this.getCompleteEvent(), TriggerEvent.SIGNAL_EVENT, this.getCompleteEvent());
        this.executionContext.getInternalIOProcessor().addEvent(event);
        log.info(((SCXMLExecutionContext) (this.executionContext.getInternalIOProcessor())).hasPendingInternalEvent());
        log.info("task " + this.hashCode() +" completed");
    }

    public void accept(){

    }
}
