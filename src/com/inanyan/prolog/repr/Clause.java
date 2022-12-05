package com.inanyan.prolog.repr;

import com.inanyan.prolog.parsing.Token;

import java.util.List;

public abstract class Clause {
    public abstract <R> R accept(Visitor<R> visitor);

    public static class Fact extends Clause {
        public final Token name;
        public final List<Term> args;

        public Fact(Token name, List<Term> args) {
            this.name = name;
            this.args = args;
        }

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitFact(this);
        }

        public String toString() {
            return this.name + "/" + this.args.size();
        }
    }

    public static class CompoundClauses extends Clause {
        public final List<Clause> clauses;

        public CompoundClauses(List<Clause> clauses) {
            this.clauses = clauses;
        }

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitCompoundClauses(this);
        }
    }

    public interface Visitor<R> {
        R visitFact(Fact fact);
        R visitCompoundClauses(CompoundClauses fact);
    }
}
