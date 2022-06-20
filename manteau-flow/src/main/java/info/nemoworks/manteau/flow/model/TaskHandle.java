package info.nemoworks.manteau.flow.model;

import lombok.Getter;

public class TaskHandle {

    @Getter
    private Task task;

    @Getter
    private String name;

    public TaskHandle(Task task, String name) {
        this.task = task;
        this.name = name;
//        TaskTrace.INSTANCE().addDelegate(this);
    }
}
