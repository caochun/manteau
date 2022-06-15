package info.nemoworks.manteau.flow.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TaskTrace {

    private static TaskTrace bulletin;

    private Map<String, TaskHandle> delegates;

    private TaskTrace() {
        this.delegates = new HashMap<>();
    }

    public static TaskTrace INSTANCE() {
        if (bulletin == null)
            bulletin = new TaskTrace();
        return bulletin;
    }

    public void addDelegate(TaskHandle delegate) {
        this.delegates.put(delegate.getName(), delegate);
    }

    public TaskHandle getDelegate(String name) {
        return this.delegates.get(name);
    }

    public void deleteDelegate(String name) {
        this.delegates.remove(name);
    }

    public List<TaskHandle> getDelegates() {
        return this.delegates.values().stream().collect(Collectors.toList());
    }
}
