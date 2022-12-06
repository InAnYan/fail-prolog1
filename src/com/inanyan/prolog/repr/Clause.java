package com.inanyan.prolog.repr;

import com.inanyan.prolog.parsing.Token;

import java.util.List;
import java.util.Set;

public abstract class Clause {
    public abstract <R> R accept(Visitor<R> visitor);

    public static class Fact extends Clause {
        public final Token name;
        public final List<Term> args;
        public final Set<Term> ownVariables;

        public Fact(Token name, List<Term> args, Set<Term> ownVariables) {
            this.name = name;
            this.args = args;
            this.ownVariables = ownVariables;
        }

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitFact(this);
        }

        public String toInfoString() {
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
        R visitCompoundClauses(CompoundClauses compound);
    }
}
