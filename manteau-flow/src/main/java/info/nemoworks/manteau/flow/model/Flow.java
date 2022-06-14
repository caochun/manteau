package info.nemoworks.manteau.flow.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.scxml2.Context;
import org.apache.commons.scxml2.Evaluator;
import org.apache.commons.scxml2.SCXMLExecutor;
import org.apache.commons.scxml2.env.SimpleDispatcher;
import org.apache.commons.scxml2.env.SimpleErrorReporter;
import org.apache.commons.scxml2.env.jexl.JexlContext;
import org.apache.commons.scxml2.env.jexl.JexlEvaluator;
import org.apache.commons.scxml2.io.SCXMLReader;
import org.apache.commons.scxml2.model.CustomAction;
import org.apache.commons.scxml2.model.ModelException;
import org.apache.commons.scxml2.model.SCXML;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Flow {

    private Log log;

    private SCXML stateMachine;

    private SCXMLExecutor engine;


    public Flow(final URL scxmlDocument) throws ModelException {
        this(scxmlDocument, new JexlContext(), new JexlEvaluator());
    }

    public Flow(URL scxmlDocument, Context rootCtx, Evaluator evaluator) throws ModelException {
        log = LogFactory.getLog(this.getClass());

        List<CustomAction> customActions = new ArrayList<CustomAction>();
        CustomAction ca = new CustomAction("https://nemoworks.info/manteau", "task", Task.class);
        customActions.add(ca);

        try {
            stateMachine = SCXMLReader.read(scxmlDocument, new SCXMLReader.Configuration(null, null, customActions));
        } catch (IOException | XMLStreamException | ModelException ioe) {
            logError(ioe);
        }
        initialize(stateMachine, rootCtx, evaluator);
    }

    private void initialize(final SCXML stateMachine, final Context rootCtx, final Evaluator evaluator) throws ModelException {
        engine = new SCXMLExecutor(evaluator, new SimpleDispatcher(), new SimpleErrorReporter());
        engine.setStateMachine(stateMachine);
        engine.setRootContext(rootCtx);
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

}
