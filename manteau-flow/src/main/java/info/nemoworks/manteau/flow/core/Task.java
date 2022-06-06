package info.nemoworks.manteau.flow.core;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Task {

    enum STATUS {
        PENDING, ACCEPTED, COMPLETED
    }

    private String subject;
    private Query query;

    private List<Command> expectedCommands = null;

    private Command completingCommand = null;

    private AbstractProcess process;

    private STATUS status = STATUS.PENDING;

    public boolean accept() {
        if (this.status != STATUS.PENDING) return false;
        this.status = STATUS.ACCEPTED;
        process.taskStatusChanged(this);
        return true;
    }

    public boolean complete(Command command) {
        if (!this.expectedCommands.contains(command)) return false;
        this.status = STATUS.COMPLETED;
        this.completingCommand = command;
        return process.taskStatusChanged(this);
    }

}
