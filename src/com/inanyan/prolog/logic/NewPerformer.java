package com.inanyan.prolog.logic;

import com.inanyan.prolog.repr.Logic;

import java.io.PrintStream;
import java.util.Stack;

public class NewPerformer {
    private final Stack<Integer> backtracking = new Stack<>();
    private final Stack<Logic.Fact> facts = new Stack<>();
    private int current = 0;

    private final Configuration conf;

    public static class Configuration {
        public final LogicBase base;
        public final PrintStream out;

        public Configuration(LogicBase base, PrintStream out) {
            this.base = base;
            this.out = out;
        }
    }

    public NewPerformer(Configuration conf, Logic logic) {
        this.conf = conf;
        if (logic instanceof Logic.Fact fact) {
            facts.add(fact);
        } else if (logic instanceof Logic.Conjunction conjunction) {
            facts.addAll(conjunction.facts);
        }
    }

    public boolean call(Environment env) {
        return mainJob(env, true);
    }

    public boolean redo(Environment env) {
        return mainJob(env, false);
    }

    private boolean mainJob(Environment env, boolean mode) {
        while (true) {
            if (current < 0) {
                current++;
                return false;
            } else if (current == facts.size()){
                current--;
                return true;
            }

            boolean result = mode ? callFact(env) : redoFact(env);

            if (result) {
                current++;
                mode = true;
            } else {
                current--;
                mode = false;
            }
        }
    }

    private boolean callFact(Environment env) {
        Logic.Fact fact = facts.get(current);
    }

    private boolean redoFact(Environment env) {
        Logic.Fact fact = facts.get(current);
    }
}
