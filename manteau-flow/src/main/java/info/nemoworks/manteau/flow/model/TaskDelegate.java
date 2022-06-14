package info.nemoworks.manteau.flow.model;

import lombok.Getter;

public class TaskDelegate {

    @Getter
    private Task task;

    @Getter
    private String name;

    public TaskDelegate(Task task, String name) {
        this.task = task;
        this.name = name;
        TaskBulletin.INSTANCE().addDelegate(this);
    }
}
