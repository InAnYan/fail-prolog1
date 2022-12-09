package com.inanyan.prolog.logic;

import com.inanyan.prolog.repr.Logic;
import com.inanyan.prolog.repr.Rule;
import com.sun.nio.sctp.SendFailedNotification;

import java.io.PrintStream;
import java.util.Stack;

public class NewNewPerformer implements Logic.Visitor<Boolean> {
    private static class PreviousState {
        public final Logic logic;
        public final int current;
        public final Environment env;

        public PreviousState(Logic logic, int current, Environment env) {
            this.current = current;
            this.logic = logic;
            this.env = env;
        }
    }

    private Stack<PreviousState> states = new Stack<>();
    private int current = 0;
    private Environment env = new Environment();

    public static class Configuration {
        public final LogicBase base;
        public final PrintStream out;

        public Configuration(LogicBase base, PrintStream out) {
            this.base = base;
            this.out = out;
        }
    }

    private final Configuration conf;

    private boolean mode;

    public NewNewPerformer(Configuration conf, Logic logic) {
        this.conf = conf;
        this.states.add(new PreviousState(logic, current, env));
    }

    public boolean call(Environment env) {
        this.mode = true;
        return mainJob(env);
    }

    public boolean redo(Environment env) {
        this.mode = false;
        return mainJob(env);
    }

    public boolean mainJob(Environment env) {
        PreviousState state = popState();
        return visit(state.logic);
    }

    private PreviousState popState() {
        PreviousState state = states.pop();
        this.current = state.current;
        return state;
    }

    private boolean visit(Logic logic) {
        return logic.accept(this);
    }

    @Override
    public Boolean visitFact(Logic.Fact fact) {
        if (mode) {
            return callFact(fact);
        } else {
            return redoFact(fact);
        }
    }

    @Override
    public Boolean visitConjunction(Logic.Conjunction conjunction) {
        if (mode) {
            return callConjunction(conjunction);
        } else {
            return redoConjunction(conjunction);
        }
    }

    private boolean callFact(Logic.Fact fact) {
        return factJob(fact);
    }

    private boolean redoFact(Logic.Fact fact) {
        current++;
        return factJob(fact);
    }

    private boolean factJob(Logic.Fact fact) {
        // 1. Find fact
        // 2. If not found, then return false
        // 3. If found:
        // 3.1. Add past state
        // 3.2. Get rule
        // 3.3. Call the rule (return result of it)
    }

    private boolean callConjunction(Logic.Conjunction conjunction) {
        return conjunctionJob(conjunction);
    }

    private boolean redoConjunction(Logic.Conjunction conjunction) {
        current--; // TODO ??
        return conjunctionJob(conjunction)
    }

    private boolean conjunctionJob(Logic.Conjunction conjunction) {

    }
}
