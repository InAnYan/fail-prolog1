package com.inanyan.prolog.logic;

import com.inanyan.prolog.repr.Clause;
import com.inanyan.prolog.repr.Term;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogicBase implements Clause.Visitor<Void> {
    private static class BaseIndex {
        private final String name;
        private final int arity;

        public BaseIndex(String name, int arity) {
            this.name = name;
            this.arity = arity;
        }
    }

    private final Map<String, Map<Integer, List<List<Term>>>> baseImpl = new HashMap<>();

    public LogicBase(List<Clause> clauses) {
        add(clauses);
    }

    public LogicBase() {

    }

    public void add(Clause clause) {
        clause.accept(this);
    }

    public void add(List<Clause> clauses) {
        for (Clause clause : clauses) {
            add(clause);
        }
    }

    public boolean has(String name, int arity) {
        return baseImpl.containsKey(name) && baseImpl.get(name).containsKey(arity);
    }

    public List<List<Term>> get(String name, int arity) {
        return baseImpl.get(name).get(arity);
    }

    @Override
    public Void visitFact(Clause.Fact fact) {
        if (baseImpl.containsKey(fact.name)) {
            Map<Integer, List<List<Term>>> map = baseImpl.get(fact.name);
            if (map.containsKey(fact.args.size())) {
                map.get(fact.args.size()).add(fact.args);
            } else {
                map.put(fact.args.size(), new ArrayList<>());
                visitFact(fact);
            }
        } else {
            baseImpl.put(fact.name.text, new HashMap<>());
            visitFact(fact);
        }

        return null;
    }

    @Override
    public Void visitCompoundClauses(Clause.CompoundClauses fact) {
        // TODO: What is this?
        assert false;
        return null;
    }
}
