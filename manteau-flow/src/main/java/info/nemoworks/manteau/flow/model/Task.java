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

    @Getter
    private STATUS status;

    public static enum STATUS {
        INITIALIZED, PENDING, ACCEPTED, COMPLETED
    }


    public Task() {
        this.status = STATUS.INITIALIZED;
    }

    @Getter
    private ActionExecutionContext executionContext;

    @Override
    public void execute(ActionExecutionContext actionExecutionContext) throws ModelException, SCXMLExpressionException {
        log.info("task " + this.getName() + " executing");

        this.executionContext = actionExecutionContext;
        Object trace = actionExecutionContext.getGlobalContext().get("trace");
        if ((trace != null) && (trace instanceof TaskTrace)) {
            ((TaskTrace) trace).appendTask(this);
        }
        this.status = STATUS.PENDING;
    }

    public boolean revoke() {
        if (this.status == STATUS.PENDING) {
            this.status = STATUS.INITIALIZED;
            return true;
        } else {
            return false;
        }
    }


    public boolean complete() {
        if (this.status != STATUS.ACCEPTED)
            return false;


        return true;
    }

    public void accept() {
        this.status = STATUS.ACCEPTED;
    }
}
