package com.inanyan.prolog.repr;

import com.inanyan.prolog.parsing.Token;

public abstract class Term {
    public abstract <R> R accept(Visitor<R> visitor);
    public abstract boolean compareTo(Term term);

    public static class Atom extends Term {
        public final Token name;

        public Atom(Token name) {
            this.name = name;
        }

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitAtom(this);
        }

        @Override
        public boolean compareTo(Term term) {
            if (term instanceof Term.Atom) {
                return this.name.equals(((Atom) term).name);
            }
            return false;
        }
    }

    public static class Variable extends Term {
        public final Token name;

        public Variable(Token name) {
            this.name = name;
        }

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitVariable(this);
        }

        @Override
        public boolean compareTo(Term term) {
            if (term instanceof Term.Variable) {
                return this.name.equals(((Variable) term).name);
            }
            return false;
        }
    }

    public static class Number extends Term {
        // TODO: What is a Prolog Number Term?
        public final int line;
        public final int num;

        public Number(int line, int num) {
            this.line = line;
            this.num = num;
        }

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitNumber(this);
        }

        @Override
        public boolean compareTo(Term term) {
            if (term instanceof Term.Number) {
                return this.num == ((Number) term).num;
            }
            return false;
        }
    }

    public interface Visitor <R> {
        R visitAtom(Atom term);
        R visitVariable(Variable term);
        R visitNumber(Number term);
    }
}
