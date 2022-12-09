package com.inanyan.prolog.logic;

import com.inanyan.prolog.repr.Logic;
import com.inanyan.prolog.repr.Rule;
import com.inanyan.prolog.repr.Term;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogicBase {

    public static class Record {
        public final List<Term> terms;
        public final Logic rule;

        public Record(List<Term> terms, Logic rule) {
            this.terms = terms;
            this.rule = rule;
        }
    }

    private final Map<String, Map<Integer, List<Record>>> baseImpl = new HashMap<>();

    public void add(Rule clause) {
        String name = clause.head.name;
        List<Term> terms = clause.head.args;
        int termCount = terms.size();
        Logic rule = clause.body;

        if (baseImpl.containsKey(name)) {
            Map<Integer, List<Record>> map = baseImpl.get(name);
            if (map.containsKey(termCount)) {
                map.get(termCount).add(new Record(terms, rule));
            } else {
                map.put(termCount, new ArrayList<>());
                add(clause);
            }
        } else {
            baseImpl.put(name, new HashMap<>());
            add(clause);
        }
    }

    public void add(List<Rule> rules) {
        for (Rule rule : rules) {
            add(rule);
        }
    }

    public boolean has(String name, int arity) {
        return baseImpl.containsKey(name) && baseImpl.get(name).containsKey(arity);
    }

    public class OutOfRange extends RuntimeException {
        public final String name;
        public final int arity;

        public OutOfRange(String name, int arity) {
            super("there is no such source as '" + name + "/" + arity + "'");
            this.name = name;
            this.arity = arity;
        }
    }

    public List<Record> get(String name, int arity) {
        if (!has(name, arity)) {
            throw new OutOfRange(name, arity);
        }

        return baseImpl.get(name).get(arity);
    }
}
