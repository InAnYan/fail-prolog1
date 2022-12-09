package com.inanyan.prolog.logic;

import com.inanyan.prolog.repr.Clause;
import com.inanyan.prolog.repr.Term;

import java.io.PrintStream;
import java.util.List;
import java.util.Stack;

public class Performer implements Clause.Visitor<Boolean> {
    public static class Configuration {
        public final LogicBase base;
        public final PrintStream out;

        public Configuration(LogicBase base, PrintStream out) {
            this.base = base;
            this.out = out;
        }
    }

    private final Configuration conf;
    private final Clause clause;

    private final Stack<Integer> backtracking = new Stack<>();

    private final Environment env = new Environment();

    public Performer(Configuration conf, Clause clause) {
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

    private boolean visit(Clause clause) {
        return clause.accept(this);
    }

    @Override
    public Boolean visitFact(Clause.Fact fact) {
        if (mode) {
            return callFact(fact);
        } else {
            return redoFact(fact);
        }
    }

    private boolean callFact(Clause.Fact fact) {
        List<List<Term>> terms = findPredicate(fact);
        return findMatching(0, fact, terms);
    }

    private boolean redoFact(Clause.Fact fact) {
        List<List<Term>> terms = findPredicate(fact);
        int lastPos = backtracking.pop();
        return findMatching(lastPos + 1, fact, terms);
    }

    private boolean findMatching(int start, Clause.Fact fact, List<List<Term>> terms) {
        for (int i = start; i < terms.size(); i++) {
            if (match(terms.get(i), fact)) {
                backtracking.push(i);
                return true;
            }
        }

        clearSetVariables(fact);
        return false;
    }

    private void clearSetVariables(Clause.Fact fact) {
        for (String term : fact.ownVariables) {
            env.delete(term);
        }
    }

    private boolean match(List<Term> fromBase, Clause.Fact fact) {
        for (int i = 0; i < fromBase.size(); i++) {
            if (fact.args.get(i) instanceof Term.Variable varTerm) {
                if (fact.ownVariables.contains(varTerm.name.text)) {
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

    private List<List<Term>> findPredicate(Clause.Fact fact) {
        String name = fact.name.text;
        int arity = fact.args.size();

        if (!conf.base.has(name, arity)) {
            throw new Error(fact.name.line, "unknown source '" + fact.toInfoString() + "'");
        }
        return conf.base.get(name, arity);
    }

    @Override
    public Boolean visitCompoundClauses(Clause.Compound compound) {
        if (mode) {
            return callCompound(compound);
        } else {
            return redoCompound(compound);
        }
    }

    private boolean callCompound(Clause.Compound compound) {
        return executeCompound(0, compound);
    }

    private boolean redoCompound(Clause.Compound compound) {
        return executeCompound(compound.clauses.size() - 1, compound);
    }

    private boolean executeCompound(int start, Clause.Compound compound) {
        int index = start;
        while (true) {
            if (index < 0) {
                return false;
            } else if (index == compound.clauses.size()){
                return true;
            }

            Clause currentClause = compound.clauses.get(index);
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
