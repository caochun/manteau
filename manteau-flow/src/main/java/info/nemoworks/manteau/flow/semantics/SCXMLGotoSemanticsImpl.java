package info.nemoworks.manteau.flow.semantics;

import org.apache.commons.scxml2.SCXMLExecutionContext;
import org.apache.commons.scxml2.TriggerEvent;
import org.apache.commons.scxml2.model.*;
import org.apache.commons.scxml2.semantics.SCXMLSemanticsImpl;
import org.apache.commons.scxml2.semantics.Step;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SCXMLGotoSemanticsImpl extends SCXMLSemanticsImpl {

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

        String eventName = event.getName();

        String nextId = eventName.split("GOTO_", 2)[1];

        Map<String, TransitionTarget> targets = executionContext.getScInstance().getStateMachine().getTargets();

        Transition transition = new Transition();
        transition.setEvent(event.getName());
        transition.setNext(nextId);
        transition.getTargets().add(targets.get(nextId));
        transition.setNamespaces(executionContext.getScInstance().getStateMachine().getNamespaces());
        transition.setType(TransitionType.external);
        transition.setParent((TransitionalState) executionContext.getScInstance().getCurrentStatus().getAllStates().iterator().next());

        step.getTransitList().add(transition);


    }


    //Currently we treat an event as a fallback event if it has a String payload with a prefix of "GOTO_"
    //followed by the id of target state
    private boolean isGotoEvent(TriggerEvent event) {
        return event.getName().startsWith("GOTO_");
    }
//
//    @Override
//    public void macroStep(SCXMLExecutionContext exctx, Set<TransitionalState> statesToInvoke) throws ModelException {
//        do {
//            boolean macroStepDone = false;
//            do {
//                Step step = new Step(null);
//                selectTransitions(exctx, step);
//                if (step.getTransitList().isEmpty()) {
//                    TriggerEvent event = exctx.nextInternalEvent();
//                    if (event != null) {
//                        if (isCancelEvent(event)) {
//                            exctx.stopRunning();
//                        }
//                        else {
//                            setSystemEventVariable(exctx.getScInstance(), event, true);
//                            step = new Step(event);
//                            if (isGotoEvent(event)){
//                                buildTemporaryTransition(exctx,event,step);
//                            }else{
//                                selectTransitions(exctx, step);
//
//                            }
//                        }
//                    }
//                }
//                if (step.getTransitList().isEmpty()) {
//                    macroStepDone = true;
//                }
//                else {
//                    microStep(exctx, step, statesToInvoke);
//                    setSystemAllStatesVariable(exctx.getScInstance());
//                }
//
//            } while (exctx.isRunning() && !macroStepDone);
//
//            if (exctx.isRunning() && !statesToInvoke.isEmpty()) {
//                initiateInvokes(exctx, statesToInvoke);
//                statesToInvoke.clear();
//            }
//        } while (exctx.isRunning() && exctx.hasPendingInternalEvent());    }
}
