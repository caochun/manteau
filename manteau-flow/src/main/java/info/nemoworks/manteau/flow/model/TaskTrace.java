package info.nemoworks.manteau.flow.model;

import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import lombok.Data;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.scxml2.TriggerEvent;
import org.apache.commons.scxml2.model.ModelException;

import java.time.Instant;

public class TaskTrace {

    private TaskNode head;

    Log log;

    public TaskTrace() {
        log = LogFactory.getLog(this.getClass());
        trace = GraphBuilder.directed().build();
    }


    public synchronized void appendTask(Task task) {
        TaskNode current = new TaskNode(task);
        trace.putEdge(head, current);
        head = current;
    }

    // return true if 'head' is pending and is the direct predecessor of 'to'
    public synchronized boolean unwindTask(Task to) throws ModelException {

        if (trace.predecessors(head).stream().filter(n -> n.getTask().equals(to)).count() == 0) return false;
        if (head.getTask().getStatus() != Task.STATUS.PENDING) return false;

        head.getTask().revoke();
        this.trigger(head.getTask(), "GOTO_" + to.getParentEnterableState().getId(), null);

        TaskNode current = new TaskNode(to, ORIGIN.WITHDRAW);
        trace.putEdge(head, current);
        head = current;
        return true;

    }


    private void trigger(Task task, String event, Object payload) {
        TriggerEvent evt = new TriggerEvent(event, TriggerEvent.SIGNAL_EVENT, payload);
        task.getExecutionContext().getInternalIOProcessor().addEvent(evt);
        log.info("Trigger event " + event + " for task " + task.getName());
    }

    private MutableGraph<TaskNode> trace;


    @Data
    public static class TaskNode {

        private Instant instant;
        private Task task;
        private ORIGIN origin;

        public TaskNode(Task task) {
            this(task, ORIGIN.NORMAL);
        }

        public TaskNode(Task task, ORIGIN origin) {
            this(Instant.now(), task, origin);
        }

        public TaskNode(Instant instant, Task task) {
            this(instant, task, ORIGIN.NORMAL);
        }

        public TaskNode(Instant instant, Task task, ORIGIN origin) {
            this.instant = instant;
            this.task = task;
            this.origin = origin;
        }

        public String toString() {
            return task + "@" + instant.toString();
        }


    }

    public static enum ORIGIN {
        NORMAL, GOTO, WITHDRAW
    }
}
