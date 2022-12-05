package com.inanyan.prolog.logic.goals;

import com.inanyan.prolog.logic.Environment;
import com.inanyan.prolog.logic.Goal;
import com.inanyan.prolog.repr.Term;
import com.inanyan.prolog.util.LongASTStringify;

import java.util.Map;

public class BuiltinGoals {
    public static final Goal writer = new Goal(null) {
        @Override
        public boolean call(Environment env) {
            // TODO: What should it do?
            for (Map.Entry<String, Term> entry : env) {
                System.out.println(entry.getKey() + " = " + new LongASTStringify().convert(entry.getValue()));
            }
            return true;
        }

        @Override
        public boolean redo(Environment env) {
            return false;
        }
    };

    public static final Goal fail = new Goal(null) {
        @Override
        public boolean call(Environment env) {
            return false;
        }

        @Override
        public boolean redo(Environment env) {
            // TODO: Should not be called
            assert false;
            return false;
        }
    };

    public static final Goal nl = new Goal(null) {
        @Override
        public boolean call(Environment env) {
            System.out.print("\n");
            return true;
        }

        @Override
        public boolean redo(Environment env) {
            return false;
        }
    };
}
