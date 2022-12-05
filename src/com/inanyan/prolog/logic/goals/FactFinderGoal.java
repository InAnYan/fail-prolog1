package com.inanyan.prolog.logic.goals;

import com.inanyan.prolog.logic.Environment;
import com.inanyan.prolog.logic.Goal;
import com.inanyan.prolog.logic.LogicBase;
import com.inanyan.prolog.repr.Term;

import java.util.List;

public class FactFinderGoal extends Goal {
    private final List<Term> terms;
    private final String name;
    private int lastPos = -1;

    public FactFinderGoal(LogicBase base, String name, List<Term> terms) {
        super(base);
        this.terms = terms;
        this.name = name;
    }

    @Override
    public boolean call(Environment env) {
        return false;
    }

    @Override
    public boolean redo(Environment env) {
        return false;
    }
}
