package info.nemoworks.manteau.flow;

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
    private CommandQuery query;

    private List<CommandQuery> expectedCommands = null;

    private CommandQuery completingCommand = null;

    private AbstractProcess process;

    private STATUS status = STATUS.PENDING;

    public boolean accept() {
        if (this.status != STATUS.PENDING) return false;
        this.status = STATUS.ACCEPTED;
        process.taskStatusChanged(this);
        return true;
    }

    public boolean complete(CommandQuery command) {
        if (!this.expectedCommands.contains(command)) return false;
        this.status = STATUS.COMPLETED;
        this.completingCommand = command;
        return process.taskStatusChanged(this);
    }

}
