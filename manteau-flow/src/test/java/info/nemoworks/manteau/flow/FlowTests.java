package info.nemoworks.manteau.flow;

import com.google.common.io.Resources;
import info.nemoworks.manteau.flow.model.Flow;
import info.nemoworks.manteau.flow.model.Task;
import org.apache.commons.scxml2.model.ModelException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class FlowTests {

    @Test
    public void testFlowWithCustomAction() throws ModelException {
        Flow flow = new Flow(Resources.getResource("statetask.xml"));
        Task createTask = flow.getTrace().getHeadTask();
        createTask.complete();
        createTask.trigger("create");
        Task next = flow.getTrace().getHeadTask();
        createTask.uncomplete();

        flow.getTrace().getHeadTask().jump(next);
    }

}
