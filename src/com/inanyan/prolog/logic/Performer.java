package com.inanyan.prolog.logic;

import com.inanyan.prolog.repr.Logic;
import com.inanyan.prolog.repr.Term;

import java.io.PrintStream;
import java.util.List;
import java.util.Stack;

public class Performer implements Logic.Visitor<Boolean> {
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

    public static class Error extends RuntimeException {
        public final int line;
        public final String msg;

        public Error(int line, String msg) {
            super(msg);
            this.line = line;
            this.msg = msg;
        }
    }

    private final Configuration conf;

    private boolean mode;

    public Performer(Configuration conf, Logic logic) {
        this.conf = conf;
        this.states.add(new PreviousState(logic, current, env));
    }

    public boolean call() {
        this.mode = true;
        return mainJob();
    }

    public boolean redo() {
        this.mode = false;
        return mainJob();
    }

    public boolean mainJob() {
        PreviousState state = popState();
        return visit(state.logic);
    }

    private PreviousState popState() {
        PreviousState state = states.pop();
        this.current = state.current;
        this.env = state.env;
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
        current = 0;
        return factJob(fact);
    }

    private boolean redoFact(Logic.Fact fact) {
        current++;
        return factJob(fact);
    }

    private boolean factJob(Logic.Fact fact) {
        try {
            List<LogicBase.Record> terms = conf.base.get(fact.name, fact.args.size());

            boolean found = findMatchingFact(fact, terms);

            if (found) {
                pushState(fact);

                LogicBase.Record record = terms.get(current);
                if (record.rule instanceof Logic.Fact rulefact) {
                    if (rulefact.name.equals("true")) {
                        return true;
                    }
                }

                boolean result = this.newCall(record.rule);
                env = env.enclosing;
                return result;
            } else {
                return false;
            }
        } catch (LogicBase.OutOfRange e) {
            throw new Error(fact.line, e.getMessage());
        }
    }

    private boolean newCall(Logic logic) {
        env = new Environment(env);
        current = 0;
        pushState(logic);
        return this.call();
    }

    private void pushState(Logic logic) {
        states.push(new PreviousState(logic, current, env));
    }

    private boolean findMatchingFact(Logic.Fact fact, List<LogicBase.Record> records) {
        for (; current < records.size(); current++) {
            if (matchFact(fact, records.get(current).terms)) {
                return true;
            }
        }

        clearSetVariables(fact);
        return false;
    }

    private boolean matchFact(Logic.Fact fact, List<Term> fromBase) {
        for (int i = 0; i < fromBase.size(); i++) {
            if (fact.args.get(i) instanceof Term.Var varTerm) {
                if (fact.ownVars.contains(varTerm)) {
                    env.define(varTerm.name, fromBase.get(i));
                } else {
                    if (!fromBase.get(i).equals(env.lookup(varTerm.name))) {
                        return false;
                    }
                }
            } else if (!fromBase.get(i).equals(fact.args.get(i))){
                return false;
            }
        }
        return true;
    }

    private void clearSetVariables(Logic.Fact fact) {
        for (Term.Var term : fact.ownVars) {
            env.delete(term.name);
        }
    }

    private boolean callConjunction(Logic.Conjunction conjunction) {
        return conjunctionJob(conjunction);
    }

    private boolean redoConjunction(Logic.Conjunction conjunction) {
        current--;
        return conjunctionJob(conjunction);
    }

    private boolean conjunctionJob(Logic.Conjunction conjunction) {
        while (true) {
            if (current < 0) {
                return false;
            } else if (current >= conjunction.list.size()){
                pushState(conjunction);
                return true;
            }

            boolean result;
            int oldCurrent = current;
            if (mode) {
                Logic currentClause = conjunction.list.get(current);
                result = visit(currentClause);
            } else {
                PreviousState previous = popState();
                result = visit(previous.logic);
            }
            current = oldCurrent;

            if (result) {
                current++;
                mode = true;
            } else {
                current--;
                mode = false;
            }
        }
    }

    public Environment getEnvironment() {
        return this.env;
    }
}
