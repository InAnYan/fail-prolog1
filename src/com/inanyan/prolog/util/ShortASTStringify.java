package com.inanyan.prolog.util;

import com.inanyan.prolog.repr.Clause;
import com.inanyan.prolog.repr.Term;

public class ShortASTStringify implements Clause.Visitor<String>, Term.Visitor<String> {

    public String convert(Clause clause) {
        return clause.accept(this);
    }

    public String convert(Term term) {
        return term.accept(this);
    }
    @Override
    public String visitFact(Clause.Fact fact) {
        return "<" + fact.name + "/" + fact.args.size() + ">";
    }

    @Override
    public String visitCompoundClauses(Clause.CompoundClauses fact) {
        return "<compound/" + fact.clauses.size() + ">";
    }

    @Override
    public String visitAtom(Term.Atom term) {
        return "<atom:'" + term.name.text + "'>";
    }

    @Override
    public String visitVariable(Term.Variable term) {
        return "<var:'" + term.name.text + "'>";
    }

    @Override
    public String visitNumber(Term.Number term) {
        return "<number: " + term.num + ">";
    }
}
