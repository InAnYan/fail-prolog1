package com.inanyan.prolog.logic;

import com.inanyan.prolog.repr.Clause;
import com.inanyan.prolog.repr.Term;
import com.inanyan.prolog.util.ErrorListener;

import javax.imageio.ImageTranscoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GoalPerformer implements Clause.Visitor<Boolean> {
    private static class GoalImpl {
        public Clause clause;
        public int lastPos;

        public GoalImpl(Clause clause, int lastPos) {
            this.clause = clause;
            this.lastPos = lastPos;
        }
    }

    private final List<GoalImpl> goalList = new ArrayList<>();
    private final LogicBase base;
    private final ErrorListener errorListener;

    public GoalPerformer(ErrorListener errorListener, LogicBase base, Clause clause) {
        this.errorListener = errorListener;
        this.base = base;
        if (clause instanceof Clause.Fact) {
            goalList.add(new GoalImpl(clause, -1));
        } else if (clause instanceof Clause.CompoundClauses) {
            for (Clause part : ((Clause.CompoundClauses)clause).clauses) {
                goalList.add(new GoalImpl(part, -1));
            }
        }
    }

    private boolean mode;
    private Environment env;
    private int lastPos;

    public boolean call(Environment env) {
        mode = true;
        this.env = env;
        return mainJob();
    }

    public boolean redo(Environment env) {
        mode = false;
        this.env = env;
        return mainJob();
    }

    private boolean mainJob() {
        int currentGoalIndex = 0;
        while (true) {
            if (currentGoalIndex < 0) {
                return false;
            } else if (currentGoalIndex >= goalList.size()){
                return true;
            }

            GoalImpl currentGoal = goalList.get(currentGoalIndex);
            this.lastPos = currentGoal.lastPos;
            boolean result = visit(currentGoal.clause);
            currentGoal.lastPos = this.lastPos;

            if (result) {
                currentGoalIndex++;
                mode = true;
            } else {
                currentGoalIndex--;
                mode = false;
            }
        }
    }

    private boolean visit(Clause clause) {
        return clause.accept(this);
    }

    @Override
    public Boolean visitFact(Clause.Fact fact) {
        if (base.has(fact.name.text, fact.args.size())) {
            List<List<Term>> lst = base.get(fact.name.text, fact.args.size());
            lastPos++;
            while (lastPos < lst.size()) {
                if (match(lst.get(lastPos), fact.args)) {
                    setVars(fact.args, lst.get(lastPos));
                    return true;
                } else {
                    lastPos++;
                }
            }
        } else {
            errorListener.reportRuntimeError(fact.name.line, "error: unknown source '" + fact + "'");
        }
        return false;
    }

    private boolean match(List<Term> fromBase, List<Term> fromFact) {
        Map<String, Term> varsToMatch = new HashMap<>();
        for (int i = 0; i < fromBase.size(); i++) {
            if (fromFact.get(i) instanceof Term.Variable varFromFact) {
                if (varsToMatch.containsKey(varFromFact.name.text)) {
                    if (!fromBase.get(i).compareTo(varsToMatch.get(varFromFact.name.text))) {
                        return false;
                    }
                } else {
                    varsToMatch.put(varFromFact.name.text, fromBase.get(i));
                }
                continue;
            }

            if (!fromBase.get(i).compareTo(fromFact.get(i))) {
                return false;
            }
        }
        return true;
    }

    private void setVars(List<Term> fromFact, List<Term> fromBase) {
        for (int i = 0; i < fromBase.size(); i++) {
            if (fromFact.get(i) instanceof Term.Variable) {
                env.define(((Term.Variable)fromFact.get(i)).name.text, fromBase.get(i));
            }
        }
    }

    @Override
    public Boolean visitCompoundClauses(Clause.CompoundClauses fact) {
        assert false;
        return false;
    }
}
