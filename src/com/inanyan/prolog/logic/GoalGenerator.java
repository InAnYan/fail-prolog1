package com.inanyan.prolog.logic;

import com.inanyan.prolog.logic.goals.CompoundGoal;
import com.inanyan.prolog.logic.goals.FactFinderGoal;
import com.inanyan.prolog.repr.Clause;

import java.util.ArrayList;
import java.util.List;

public class GoalGenerator implements Clause.Visitor<Goal> {
    private final Clause mainClause;
    private final LogicBase base;

    public GoalGenerator(Clause clause, LogicBase base) {
        this.mainClause = clause;
        this.base = base;
    }

    public Goal generate() {
        return visit(mainClause);
    }

    public Goal visit(Clause clause) {
        return clause.accept(this);
    }

    @Override
    public Goal visitFact(Clause.Fact fact) {
        return new FactFinderGoal(base, fact.name.text, fact.args);
    }

    @Override
    public Goal visitCompoundClauses(Clause.CompoundClauses compound) {
        List<Goal> goals = new ArrayList<>();
        for (Clause clause : compound.clauses) {
            goals.add(visit(clause));
        }
        return new CompoundGoal(base, goals);
    }
}
