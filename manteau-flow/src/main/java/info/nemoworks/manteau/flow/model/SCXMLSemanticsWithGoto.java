package info.nemoworks.manteau.flow.model;

import org.apache.commons.scxml2.SCXMLExecutionContext;
import org.apache.commons.scxml2.TriggerEvent;
import org.apache.commons.scxml2.model.*;
import org.apache.commons.scxml2.semantics.SCXMLSemanticsImpl;
import org.apache.commons.scxml2.semantics.Step;

import java.util.HashSet;
import java.util.Map;

public class SCXMLSemanticsWithGoto extends SCXMLSemanticsImpl {

    @Override
    public void nextStep(SCXMLExecutionContext exctx, TriggerEvent event) throws ModelException {

        if (!exctx.isRunning()) {
            return;
        }

        if (isGotoEvent(event)) {

            setSystemEventVariable(exctx.getScInstance(), event, false);
            processInvokes(exctx, event);
            Step step = new Step(event);

            buildTemporaryTransition(exctx, event, step);
            if (!step.getTransitList().isEmpty()) {
                HashSet<TransitionalState> statesToInvoke = new HashSet<>();
                microStep(exctx, step, statesToInvoke);
                setSystemAllStatesVariable(exctx.getScInstance());
                if (exctx.isRunning()) {
                    macroStep(exctx, statesToInvoke);
                }
            }

        } else {
            super.nextStep(exctx, event);
        }

    }

    private void buildTemporaryTransition(SCXMLExecutionContext executionContext, TriggerEvent event, Step step) {

        String payload = (String) (event.getPayload());

        String nextId = payload.split("GOTO_", 2)[1];

        Map<String, TransitionTarget> targets = executionContext.getScInstance().getStateMachine().getTargets();

        Transition transition = new Transition();
        transition.setEvent(event.getName());
        transition.setNext(nextId);
        transition.setNamespaces(executionContext.getScInstance().getStateMachine().getNamespaces());
        transition.setType(TransitionType.external);

        step.getTransitList().add(transition);


    }


    //Currently we treat an event as a fallback event if it has a String payload with a prefix of "GOTO_"
    //followed by the id of target state
    private boolean isGotoEvent(TriggerEvent event) {
        Object payload = event.getPayload();
        return ((payload instanceof String) && (((String) payload).startsWith("GOTO_")));
    }
}
