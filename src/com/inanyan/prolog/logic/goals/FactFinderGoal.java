package com.inanyan.prolog.logic.goals;

import com.inanyan.prolog.logic.Environment;
import com.inanyan.prolog.logic.Goal;
import com.inanyan.prolog.logic.LogicBase;
import com.inanyan.prolog.repr.Term;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        return mainJob(env);
    }

    @Override
    public boolean redo(Environment env) {
        return mainJob(env);
    }

    private boolean mainJob(Environment env) {
        if (base.has(name, terms.size())) {
            List<List<Term>> lst = base.get(name, terms.size());
            lastPos++;
            while (lastPos < lst.size()) {
                if (match(lst.get(lastPos), terms)) {
                    setVars(env, terms, lst.get(lastPos));
                    return true;
                } else {
                    lastPos++;
                }
            }
        } else {
            //errorListener.reportRuntimeError(fact.name.line, "error: unknown source '" + fact + "'");
        }
        return false;
    }

    private boolean match(List<Term> fromBase, List<Term> fromFact) {
        Map<String, Term> varsToMatch = new HashMap<>();
        for (int i = 0; i < fromBase.size(); i++) {
            if (fromFact.get(i) instanceof Term.Variable varFromFact) {
                if (varsToMatch.containsKey(varFromFact.name.text)) {
                    if (!fromBase.get(i).match(varsToMatch.get(varFromFact.name.text))) {
                        return false;
                    }
                } else {
                    varsToMatch.put(varFromFact.name.text, fromBase.get(i));
                }
                continue;
            }

            if (!fromBase.get(i).match(fromFact.get(i))) {
                return false;
            }
        }
        return true;
    }

    private void setVars(Environment env, List<Term> fromFact, List<Term> fromBase) {
        for (int i = 0; i < fromBase.size(); i++) {
            if (fromFact.get(i) instanceof Term.Variable) {
                env.define(((Term.Variable)fromFact.get(i)).name.text, fromBase.get(i));
            }
        }
    }
}
