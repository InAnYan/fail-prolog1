package com.inanyan.prolog.logic;

import com.inanyan.prolog.repr.Term;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Environment implements Iterable<Map.Entry<String, Term>> {
    private final Map<String, Term> map = new HashMap<>();

    public Term lookup(String name) {
        return map.get(name);
    }

    public void define(String name, Term term) {
        map.put(name, term);
    }

    @Override
    public Iterator<Map.Entry<String, Term>> iterator() {
        return map.entrySet().iterator();
    }

    public boolean isEmpty() {
        return this.map.isEmpty();
    }
}
