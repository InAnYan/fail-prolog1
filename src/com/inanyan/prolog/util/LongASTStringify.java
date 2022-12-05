package com.inanyan.prolog.util;

import com.inanyan.prolog.repr.Clause;
import com.inanyan.prolog.repr.Term;

public class LongASTStringify implements Clause.Visitor<String>, Term.Visitor<String> {

    public String convert(Clause clause) {
        return clause.accept(this);
    }

    public String convert(Term term) {
        return term.accept(this);
    }
    @Override
    public String visitFact(Clause.Fact fact) {
        StringBuilder ss = new StringBuilder();
        ss.append(fact.name);
        ss.append("(");
        for (int i = 0; i < fact.args.size(); i++) {
            ss.append(convert(fact.args.get(i)));
            if (i != fact.args.size() - 1) {
                ss.append(", ");
            }
        }
        ss.append(").");
        return ss.toString();
    }

    @Override
    public String visitCompoundClauses(Clause.CompoundClauses fact) {
        StringBuilder ss = new StringBuilder();
        for (int i = 0; i < fact.clauses.size(); i++) {
            ss.append(convert(fact.clauses.get(i)));
            if (i != fact.clauses.size() - 1) {
                ss.append(", ");
            }
        }
        ss.append(".");
        return ss.toString();
    }

    @Override
    public String visitAtom(Term.Atom term) {
        if (Rules.isStringLooksLikeVariable(term.name.text)) {
            return "'" + term.name + "'";
        } else {
            return term.name.text;
        }
    }

    @Override
    public String visitVariable(Term.Variable term) {
        return term.name.text;
    }

    @Override
    public String visitNumber(Term.Number term) {
        return String.valueOf(term.num);
    }
}
