package com.inanyan.prolog.logic;

import com.inanyan.prolog.repr.Logic;
import com.inanyan.prolog.repr.Term;

import java.io.PrintStream;
import java.util.List;
import java.util.Stack;

public class Performer implements Logic.Visitor<Boolean> {
    public static class Configuration {
        public final LogicBase base;
        public final PrintStream out;

        public Configuration(LogicBase base, PrintStream out) {
            this.base = base;
            this.out = out;
        }
    }

    private final Configuration conf;
    private final Logic clause;

    private final Stack<Integer> backtracking = new Stack<>();

    private final Environment env = new Environment();

    public Performer(Configuration conf, Logic clause) {
        this.conf = conf;
        this.clause = clause;
    }

    public static class Error extends RuntimeException {
        public final String msg;
        public final int line;

        public Error(int line, String msg) {
            this.line = line;
            this.msg = msg;
        }
    }

    private boolean mode;

    public Environment getEnvironment() {
        return env;
    }

    public boolean call() {
        mode = true;
        env.clear();
        backtracking.clear();
        return visit(clause);
    }

    public boolean redo() {
        mode = false;
        return visit(clause);
    }

    private boolean visit(Logic clause) {
        return clause.accept(this);
    }

    @Override
    public Boolean visitFact(Logic.Fact fact) {
        if (mode) {
            return callFact(fact);
        } else {
            return redoFact(fact);
        }
    }

    private boolean callFact(Logic.Fact fact) {
        List<List<Term>> terms = findPredicate(fact);
        return findMatching(0, fact, terms);
    }

    private boolean redoFact(Logic.Fact fact) {
        List<List<Term>> terms = findPredicate(fact);
        int lastPos = backtracking.pop();
        return findMatching(lastPos + 1, fact, terms);
    }

    private boolean findMatching(int start, Logic.Fact fact, List<List<Term>> terms) {
        for (int i = start; i < terms.size(); i++) {
            if (match(terms.get(i), fact)) {
                backtracking.push(i);
                return true;
            }
        }

        clearSetVariables(fact);
        return false;
    }

    private void clearSetVariables(Logic.Fact fact) {
        for (String term : fact.ownVars) {
            env.delete(term);
        }
    }

    private boolean match(List<Term> fromBase, Logic.Fact fact) {
        for (int i = 0; i < fromBase.size(); i++) {
            if (fact.args.get(i) instanceof Term.Var varTerm) {
                if (fact.ownVars.contains(varTerm.name.text)) {
                    env.define(varTerm.name.text, fromBase.get(i));
                } else {
                    if (!fromBase.get(i).match(env.lookup(varTerm.name.text))) {
                        return false;
                    }
                }
            } else if (!fromBase.get(i).match(fact.args.get(i))){
                return false;
            }
        }
        return true;
    }

    private List<List<Term>> findPredicate(Logic.Fact fact) {
        String name = fact.name.text;
        int arity = fact.args.size();

        if (!conf.base.has(name, arity)) {
            throw new Error(fact.name.line, "unknown source '" + fact.toInfoString() + "'");
        }
        return conf.base.get(name, arity);
    }

    @Override
    public Boolean visitConjunction(Logic.Conjunction conjunction) {
        if (mode) {
            return callCompound(conjunction);
        } else {
            return redoCompound(conjunction);
        }
    }

    private boolean callCompound(Logic.Conjunction conjunction) {
        return executeCompound(0, conjunction);
    }

    private boolean redoCompound(Logic.Conjunction conjunction) {
        return executeCompound(conjunction.facts.size() - 1, conjunction);
    }

    private boolean executeCompound(int start, Logic.Conjunction conjunction) {
        int index = start;
        while (true) {
            if (index < 0) {
                return false;
            } else if (index == conjunction.facts.size()){
                return true;
            }

            Logic currentClause = conjunction.facts.get(index);
            boolean result = visit(currentClause);

            if (result) {
                index++;
                mode = true;
            } else {
                index--;
                mode = false;
            }
        }
    }
}
