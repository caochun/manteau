package info.nemoworks.manteau.flow.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TaskBulletin {

    private static TaskBulletin bulletin;

    private Map<String, TaskDelegate> delegates;

    private TaskBulletin() {
        this.delegates = new HashMap<>();
    }

    public static TaskBulletin INSTANCE() {
        if (bulletin == null)
            bulletin = new TaskBulletin();
        return bulletin;
    }

    public void addDelegate(TaskDelegate delegate) {
        this.delegates.put(delegate.getName(), delegate);
    }

    public TaskDelegate getDelegate(String name) {
        return this.delegates.get(name);
    }

    public void deleteDelegate(String name) {
        this.delegates.remove(name);
    }

    public List<TaskDelegate> getDelegates() {
        return this.delegates.values().stream().collect(Collectors.toList());
    }
}
