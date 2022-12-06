package com.inanyan.prolog.logic;

import com.inanyan.prolog.repr.Term;
import com.inanyan.prolog.util.ErrorListener;

import java.io.PrintStream;
import java.util.List;

public abstract class Goal {
    protected LogicBase base;

    public Goal(LogicBase base) {
        this.base = base;
    }

    public static class Configuration {
        public PrintStream out;
        public ErrorListener errorListener;
    }

    public abstract boolean call(Configuration conf, List<Term> args, Environment env);
    public abstract boolean redo(Environment env);

    public LogicBase getBase() {
        return base;
    }

    public void changeBase(LogicBase base) {
        this.base = base;
    }
}
