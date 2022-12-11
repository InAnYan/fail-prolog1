package com.inanyan.prolog.logic;

import com.inanyan.prolog.repr.Term;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Environment implements Iterable<Map.Entry<String, Term>> {
    private final Map<String, Term> map = new HashMap<>();
    public final Environment enclosing;

    public Environment() {
        this.enclosing = null;
    }

    public Environment(Environment enclosing) {
        this.enclosing = enclosing;
    }

    public static class OutOfRange extends RuntimeException {
        public final String name;

        public OutOfRange(String name) {
            super("'" + name + "' not found");
            this.name = name;
        }
    }

    public Term lookup(String name) {
        if (map.containsKey(name)) {
            return map.get(name);
        } else {
            if (enclosing != null) {
                return enclosing.lookup(name);
            } else {
                throw new OutOfRange(name);
            }
        }
    }

    public void define(String name, Term term) {
        if (enclosing != null && enclosing.has(name)) {
            this.enclosing.define(name, term);
        } else {
            map.put(name, term);
        }
    }

    public void delete(String name) {
        map.remove(name);
    }

    public boolean has(String name) {
        return this.map.containsKey(name) || (enclosing != null && enclosing.has(name));
    }

    @Override
    public Iterator<Map.Entry<String, Term>> iterator() {
        return map.entrySet().iterator();
    }

    public boolean isCompletelyEmpty() {
        return this.map.isEmpty() && (enclosing == null || enclosing.isCompletelyEmpty());
    }
}
