package info.nemoworks.manteau.flow.model;

import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import info.nemoworks.manteau.flow.semantics.SCXMLGotoSemanticsImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.scxml2.*;
import org.apache.commons.scxml2.env.SimpleDispatcher;
import org.apache.commons.scxml2.env.SimpleErrorReporter;
import org.apache.commons.scxml2.env.jexl.JexlContext;
import org.apache.commons.scxml2.env.jexl.JexlEvaluator;
import org.apache.commons.scxml2.io.SCXMLReader;
import org.apache.commons.scxml2.model.*;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.net.URL;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class StateFlow {

    private Log log;

    private SCXML stateMachine;

    private SCXMLExecutor engine;

    private SCXMLExecutionContext executionContext;


    public StateFlow(final URL scxmlDocument) throws ModelException {
        this(scxmlDocument, new JexlContext(), new JexlEvaluator());
    }

    public StateFlow(URL scxmlDocument, Context rootCtx, Evaluator evaluator) throws ModelException {
        log = LogFactory.getLog(this.getClass());

        List<CustomAction> customActions = new ArrayList<CustomAction>();
        CustomAction ca = new CustomAction("https://nemoworks.info/", "task", StateTask.class);
        customActions.add(ca);

        try {
            stateMachine = SCXMLReader.read(scxmlDocument, new SCXMLReader.Configuration(null, null, customActions));
        } catch (IOException | XMLStreamException | ModelException ioe) {
            logError(ioe);
        }
        initialize(stateMachine, rootCtx, evaluator);
    }

    private void initialize(final SCXML stateMachine, final Context rootCtx, final Evaluator evaluator) throws ModelException {

        SCXMLSemantics semantics = new SCXMLGotoSemanticsImpl();
        engine = new SCXMLExecutor(evaluator, new SimpleDispatcher(), new SimpleErrorReporter(), semantics);
        engine.setStateMachine(stateMachine);
        engine.setRootContext(rootCtx);
        engine.addListener(stateMachine, new EntryListener());
        try {
            engine.go();
        } catch (final ModelException me) {
            logError(me);
        }
    }

    public boolean resetMachine() {
        try {
            engine.reset();
        } catch (final ModelException me) {
            logError(me);
            return false;
        }
        return true;
    }

    protected void logError(final Exception exception) {
        if (log.isErrorEnabled()) {
            log.error(exception.getMessage(), exception);
        }
    }

    protected class EntryListener implements SCXMLListener {

        UUID last, current;

        /**
         * {@inheritDoc}
         */
        public void onEntry(final EnterableState entered) {
            if (current == null) {
                current = UUID.randomUUID();
                StateFlow.this.graph.addNode(new AbstractMap.SimpleEntry<>(current.toString(), entered.getObservableId()));
            }
        }

        /**
         * No-op.
         *
         * @param from       The &quot;source&quot; transition target.
         * @param to         The &quot;destination&quot; transition target.
         * @param transition The transition being followed.
         * @param event      The event triggering the transition
         */
        public void onTransition(final TransitionTarget from,
                                 final TransitionTarget to, final Transition transition, final String event) {
            //TODO: possible bugs here if parallel state
            last = current;
            current = UUID.randomUUID();
            StateFlow.this.graph.putEdge(new AbstractMap.SimpleEntry<>(last.toString(), from.getObservableId()), new AbstractMap.SimpleEntry<>(current.toString(), to.getObservableId()));
        }

        /**
         * No-op.
         *
         * @param exited The state being exited.
         */
        public void onExit(final EnterableState exited) {
            // nothing to do
        }

    }

    private MutableGraph<AbstractMap.SimpleEntry<String, Integer>> graph = GraphBuilder.directed().build();

    public String getTrace() {
        return graph.toString();
    }

    public static class TraceVertex {

    }


}
