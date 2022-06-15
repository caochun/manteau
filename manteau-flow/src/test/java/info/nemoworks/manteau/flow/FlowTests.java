package info.nemoworks.manteau.flow;

import com.google.common.io.Resources;
import info.nemoworks.manteau.flow.model.Flow;
import org.apache.commons.scxml2.model.ModelException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class FlowTests {

    @Test
    public void testFlowWithCustomAction() throws ModelException, IOException {
        Flow flow = new Flow(Resources.getResource("statetask.xml"));
        System.in.read();
    }

}
