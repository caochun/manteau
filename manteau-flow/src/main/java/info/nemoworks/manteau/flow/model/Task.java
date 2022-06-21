package info.nemoworks.manteau.flow.model;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.scxml2.ActionExecutionContext;
import org.apache.commons.scxml2.SCXMLExpressionException;
import org.apache.commons.scxml2.SCXMLSystemContext;
import org.apache.commons.scxml2.TriggerEvent;
import org.apache.commons.scxml2.model.Action;
import org.apache.commons.scxml2.model.ModelException;
import org.apache.commons.scxml2.system.EventVariable;

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

    // Every task has its own lifecycle
    // INITIALIZED (execute)-> <--(cancel) PENDING (accept)-> ACCEPTED (complete)-> COMPLETED (execute)--> PENDING...
    public static enum STATUS {
        INITIALIZED, PENDING, ACCEPTED, COMPLETED
    }


    //tasks will be initialized when the scxml reader build the machine up
    public Task() {
        log = LogFactory.getLog(this.getClass());
        this.status = STATUS.INITIALIZED;
    }

    public boolean cancel() {
        if (this.status != STATUS.PENDING)
            return false;

        this.status = STATUS.INITIALIZED;
        this.trace.deduct(this);

        log.info("task " + this.getName() + " cancelled");

        return false;
    }

    public boolean complete() {
        if (this.status != STATUS.ACCEPTED)
            return false;

        this.status = STATUS.COMPLETED;

        this.trigger(this.completeEvent, null);
        log.info("task " + this.getName() + " completed");

        return true;
    }

    public boolean uncomplete() {
        if (trace.getHeadTask().getStatus() != STATUS.PENDING)
            return false;

        if (trace.getPre(trace.getHead()).getTask() != this)
            return false;

        if (trigger("GOTO_" + trace.getHead().getTask().getName(), null)) {
            trace.append(this, Trace.ORIGIN.WITHDRAW);
            this.status = STATUS.ACCEPTED;
            log.info("task " + this.getName() + " uncompleted");
            return true;
        }

        log.info("task " + this.getName() + " fail to uncomplete");

        return false;

    }

    public boolean accept() {
        if (this.status != STATUS.PENDING)
            return false;

        this.status = STATUS.ACCEPTED;
        log.info("task " + this.getName() + " accepted");

        return true;
    }

    public boolean invoke(ActionExecutionContext actionExecutionContext) {

        if ((this.status != STATUS.INITIALIZED) && (this.status != STATUS.COMPLETED))
            return false;

        log.info("task " + this.getName() + " invoked");

        this.status = STATUS.PENDING;

        this.trace.append(this);

        return true;

    }

    @Getter
    private ActionExecutionContext executionContext;

    private Trace trace = null;

    private Flow flow = null;

    @Override
    public void execute(ActionExecutionContext actionExecutionContext) throws ModelException, SCXMLExpressionException {

        this.executionContext = actionExecutionContext;

        if (trace == null) {
            Object t = actionExecutionContext.getGlobalContext().get("trace");
            if ((t == null) || (!(t instanceof Trace))) {
                throw new ModelException("no trace in executionContext");
            }
            this.trace = (Trace) t;
        }

        if (flow == null) {
            Object t = actionExecutionContext.getGlobalContext().get("flow");
            if ((t == null) || (!(t instanceof Flow))) {
                throw new ModelException("no flow in executionContext");
            }
            this.flow = (Flow) t;
        }

        EventVariable eventVariable = (EventVariable) actionExecutionContext.getGlobalContext().get(SCXMLSystemContext.EVENT_KEY);

        if (eventVariable != null)
            log.info("task " + this.getName() + " executing, triggered by event " + eventVariable.getName());

        this.invoke(actionExecutionContext);

    }


    public boolean trigger(String event, Object payload) {


        TriggerEvent evt = new TriggerEvent(event, TriggerEvent.SIGNAL_EVENT, payload);
        try {
            flow.getEngine().triggerEvent(evt);
        } catch (ModelException e) {
            return false;
        }
        log.info("Trigger event " + event + " for task " + this.getName());
        return true;
    }

}
