package com.inanyan.prolog.logic;

public abstract class Goal {
    protected LogicBase base;

    public Goal(LogicBase base) {
        this.base = base;
    }

    public abstract boolean call(Environment env);
    public abstract boolean redo(Environment env);

    public LogicBase getBase() {
        return base;
    }

    public void changeBase(LogicBase base) {
        this.base = base;
    }
}
