package info.nemoworks.manteau.flow.model;

import lombok.Getter;

public class TaskHandle {

    @Getter
    private StateTask stateTask;

    @Getter
    private String name;

    public TaskHandle(StateTask stateTask, String name) {
        this.stateTask = stateTask;
        this.name = name;
//        TaskTrace.INSTANCE().addDelegate(this);
    }
}
