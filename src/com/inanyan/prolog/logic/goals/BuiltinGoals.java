package com.inanyan.prolog.logic.goals;

import com.inanyan.prolog.logic.Environment;
import com.inanyan.prolog.logic.Goal;
import com.inanyan.prolog.repr.Clause;
import com.inanyan.prolog.repr.Term;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class BuiltinGoals {
    public static final Goal write = new Goal(null) {
        @Override
        public boolean call(Configuration conf, List<Term> args, Environment env) {
            StringJoiner sj = new StringJoiner(" ");
            for (Term term : args) {
                if (term instanceof Term.Variable termVar) {
                    Term value = env.lookup(termVar.name.text);
                    if (value == null) {
                        conf.errorListener.reportRuntimeError(termVar.name.line,
                                "unassigned logical variable '" + termVar.name.text + "'");
                        return false; // TODO: Really? Maybe it should throw error?
                    } else {
                        sj.add(value.toString());
                    }
                } else {
                    sj.add(term.toString());
                }
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
        public boolean call(Configuration conf, List<Term> args, Environment env) {
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
        public boolean call(Configuration conf, List<Term> args, Environment env) {
            conf.out.print("\n");
            return true;
        }

        @Override
        public boolean redo(Environment env) {
            return false;
        }
    };

    public static class FuncPair {
        public final int arity;
        public final Goal goal;

        public FuncPair(int arity, Goal goal) {
            this.arity = arity;
            this.goal = goal;
        }
    }

    public static final Map<String, FuncPair> funcs = new HashMap<>();
    static {
        funcs.put("write", new FuncPair(-1, write)); // TODO: Really -1?
        funcs.put("nl", new FuncPair(0, nl));
        funcs.put("fail", new FuncPair(0, fail));
    }

    public static Goal builtinCheck(Clause.Fact fact) {
        FuncPair pair = funcs.get(fact.name.text);
        if (pair == null) {
            return null;
        }

        int providedArity = fact.args.size();
        // TODO: If the arity is incorrect is that a semantic error or other predicate?
        if (providedArity == pair.arity) {
            return pair.goal;
        } else if (providedArity >= Math.abs(pair.arity)) {
            return pair.goal;
        }

        return null;
    }
}
