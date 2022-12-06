package com.inanyan.prolog.logic;

import com.inanyan.prolog.repr.Clause;
import com.inanyan.prolog.util.ErrorListener;

import java.io.PrintStream;
import java.util.Stack;

public class Performer implements Clause.Visitor<Boolean> {
    public static class Configuration {
        public LogicBase base;
        public PrintStream out;
        public ErrorListener errorListener;
    }

    private final Configuration conf;
    private final Clause clause;

    private static class BackImpl {
        public int lastPos;
    }

    private final Stack<BackImpl> backtracking = new Stack<>();

    private final Environment env = new Environment();

    public Performer(Configuration conf, Clause clause) {
        this.conf = conf;
        this.clause = clause;
    }

    private boolean mode;

    public boolean call() {
        mode = true;
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
        return null;
    }

    @Override
    public Boolean visitCompoundClauses(Clause.CompoundClauses compound) {
        return null;
    }
}
