package info.nemoworks.manteau.flow.core;

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
import java.util.List;
import java.util.function.BiConsumer;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public abstract class AbstractProcess {

    Logger logger = Logger.getLogger(this.getClass().getName());

    private SCXML stateMachine;
    private SCXMLExecutor engine;
    private Log log;



    public AbstractProcess(URL scxmlDocument) throws ModelException {
        this(scxmlDocument, new JexlContext(), new JexlEvaluator());
    }

    private AbstractProcess(URL scxmlDocument, Context rootCtx, Evaluator evaluator) throws ModelException {
        this.log = LogFactory.getLog(this.getClass());

        try {
            this.stateMachine = SCXMLReader.read(scxmlDocument);
        } catch (IOException | XMLStreamException | ModelException e) {
            this.logError(e);
        }

        this.initialize(this.stateMachine, rootCtx, evaluator);
    }


    private void initialize(SCXML stateMachine, Context rootCtx, Evaluator evaluator) throws ModelException {
        this.engine = new SCXMLExecutor(evaluator, new SimpleDispatcher(), new SimpleErrorReporter());
        this.engine.setStateMachine(stateMachine);
        this.engine.addListener(stateMachine, new EntryListener());
        this.engine.setRootContext(rootCtx);
        try {
            this.engine.go();
        } catch (ModelException var5) {
            this.logError(var5);
        }
    }


    protected class EntryListener implements SCXMLListener {


        public void onEntry(EnterableState entered) {
            logger.info("Entering state " + entered.getId());
            invoke(entered.getId());
        }

        public void onTransition(TransitionTarget from, TransitionTarget to, Transition transition, String event) {
            logger.info("Triggering transition " + transition.getEvent());
        }

        public void onExit(EnterableState exited) {
        }
    }

    public boolean fireEvent(String event) {

        TriggerEvent[] events = new TriggerEvent[]{new TriggerEvent(event, 3)};

        try {
            this.engine.triggerEvents(events);
        } catch (ModelException var4) {
            this.logError(var4);
        }

        return this.engine.getCurrentStatus().isFinal();
    }

    public SCXMLExecutor getEngine() {
        return this.engine;
    }

    public Log getLog() {
        return this.log;
    }

    public void setLog(Log log) {
        this.log = log;
    }


    public boolean resetMachine() {
        try {
            this.engine.reset();
            return true;
        } catch (ModelException var2) {
            this.logError(var2);
            return false;
        }
    }

    protected void logError(Exception exception) {
        if (this.log.isErrorEnabled()) {
            this.log.error(exception.getMessage(), exception);
        }

    }

    Task pendingTask;

    public Task getPendingTask() {
        return pendingTask;
    }

    public boolean taskStatusChanged(Task task) {
        if (pendingTask.getStatus() == Task.STATUS.COMPLETED) {
            return this.fireEvent(pendingTask.getCompletingCommand().getCommandString());
        }
        return false;
    }

    private String currentEntered;

    public String getCurrentEntered() {
        return this.currentEntered;
    }

    public boolean invoke(String state) {
        this.currentEntered = state;
        pendingTask = getTask(state);
        return (pendingTask != null);
    }

    protected abstract Task getTask(String state);


}
