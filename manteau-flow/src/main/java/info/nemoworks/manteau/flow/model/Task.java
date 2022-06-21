package info.nemoworks.manteau.flow.model;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.logging.Log;
import org.apache.commons.scxml2.ActionExecutionContext;
import org.apache.commons.scxml2.SCXMLExpressionException;
import org.apache.commons.scxml2.TriggerEvent;
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

    // Every task has its own lifecycle
    // INITIALIZED (execute)-> <--(cancel) PENDING (accept)-> ACCEPTED (complete)-> COMPLETED (execute)--> PENDING...
    public static enum STATUS {
        INITIALIZED, PENDING, ACCEPTED, COMPLETED
    }


    //tasks will be initialized when the scxml reader build the machine up
    public Task() {
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
        log.info("task " + this.getName() + " completed");

        return true;
    }

    public boolean accept() {
        if (this.status != STATUS.PENDING)
            return false;

        this.status = STATUS.ACCEPTED;
        log.info("task " + this.getName() + " accepted");

        return true;
    }

    public boolean invoke() {

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

    @Override
    public void execute(ActionExecutionContext actionExecutionContext) throws ModelException, SCXMLExpressionException {
        log.info("task " + this.getName() + " executing");

        if (trace == null) {
            this.executionContext = actionExecutionContext;
            Object t = actionExecutionContext.getGlobalContext().get("trace");
            if ((t == null) || (!(t instanceof Trace))) {
                throw new ModelException("no trace in executionContext");
            }
            this.trace = (Trace) t;
        }

        this.invoke();

    }


    // return true if 'head' is pending and is the direct predecessor of 'to'
    public synchronized boolean unwindTask(Task to) throws ModelException {


        head.getTask().revoke();
        this.trigger(head.getTask(), "GOTO_" + to.getParentEnterableState().getId(), null);

        Trace.Node current = new Trace.Node(to, Trace.ORIGIN.WITHDRAW);
        trace.putEdge(head, current);
        head = current;
        return true;

    }


    private void trigger(Task task, String event, Object payload) {
        TriggerEvent evt = new TriggerEvent(event, TriggerEvent.SIGNAL_EVENT, payload);
        task.getExecutionContext().getInternalIOProcessor().addEvent(evt);
        log.info("Trigger event " + event + " for task " + task.getName());
    }

    public static class TaskStatusException extends Exception {
        TaskStatusException(String message) {
            super(message);
        }
    }
}
