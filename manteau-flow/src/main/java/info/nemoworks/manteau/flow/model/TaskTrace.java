package info.nemoworks.manteau.flow.model;

import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;

import java.util.List;
import java.util.stream.Collectors;

public class TaskTrace {

    private StateTask head = null;

    private MutableGraph<StateTask> graph;

    private static TaskTrace taskTrace;

    private TaskTrace() {
        this.graph = GraphBuilder.directed().build();

    }

    public static TaskTrace INSTANCE() {
        if (taskTrace == null)
            taskTrace = new TaskTrace();
        return taskTrace;
    }

    public void appendTask(StateTask task) {
        this.appendTask(null, task);
    }

    public void appendTask(StateTask predecessor, StateTask task) {
        this.graph.addNode(task);
        if (predecessor != null) {
            this.graph.putEdge(predecessor, task);
        } else {
            if (head != null) {
                this.graph.putEdge(head, task);
            }
            this.head = task;
        }
    }

    public List<StateTask> getTask(String name) {
        return this.graph.nodes().stream().filter(n -> n.getName().equals(name)).collect(Collectors.toList());
    }

    @Override
    public String toString(){
        return this.graph.toString();
    }

}
