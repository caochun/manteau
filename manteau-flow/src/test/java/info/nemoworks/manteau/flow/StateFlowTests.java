package info.nemoworks.manteau.flow;

import com.google.common.io.Resources;
import info.nemoworks.manteau.flow.model.StateFlow;
import org.apache.commons.scxml2.model.ModelException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class StateFlowTests {

    @Test
    public void testFlowWithCustomAction() throws ModelException, IOException {
        StateFlow stateFlow = new StateFlow(Resources.getResource("statetask.xml"));
    }

}
