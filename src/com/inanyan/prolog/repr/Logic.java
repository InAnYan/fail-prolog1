package com.inanyan.prolog.repr;

import com.inanyan.prolog.parsing.Token;

import java.util.List;
import java.util.Set;

public abstract class Logic {
    public abstract <R> R accept(Visitor<R> visitor);

    public static class Fact extends Logic {
        public final int line;
        public final String name;
        public final List<Term> args;
        public final Set<Term.Var> ownVars;

        public Fact(Token name, List<Term> args, Set<Term.Var> ownVars) {
            this.name = name.text;
            this.line = name.line;
            this.args = args;
            this.ownVars = ownVars;
        }

        public Fact(String name, int line, List<Term> args, Set<Term.Var> ownVars) {
            this.name = name;
            this.line = line;
            this.args = args;
            this.ownVars = ownVars;
        }

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitFact(this);
        }

        public String toInfoString() {
            return this.name + "/" + this.args.size();
        }
    }

    public static class Conjunction extends Logic {
        public final List<Logic> list;

        public Conjunction(List<Logic> list) {
            this.list = list;
        }

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitConjunction(this);
        }
    }

    public interface Visitor<R> {
        R visitFact(Fact fact);
        R visitConjunction(Conjunction conjunction);
    }
}
