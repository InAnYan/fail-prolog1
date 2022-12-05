package com.inanyan.prolog.repr;

import com.inanyan.prolog.parsing.Token;
import com.inanyan.prolog.util.Rules;

public abstract class Term {
    public abstract <R> R accept(Visitor<R> visitor);

    public abstract boolean match(Term term);
    public abstract String toString();

    public static class Atom extends Term {
        public final Token name;

        public Atom(Token name) {
            this.name = name;
        }

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitAtom(this);
        }

        @Override
        public boolean match(Term node) {
            if (node instanceof Term.Atom) {
                return this.name.text.equals(((Atom) node).name.text);
            }
            return false;
        }
        @Override
        public String toString() {
            if (Rules.isStringLooksLikeVariable(this.name.text)) {
                return "'" + this.name + "'";
            } else {
                return this.name.text;
            }
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
        public boolean match(Term node) {
            if (node instanceof Term.Variable) {
                return this.name.equals(((Variable) node).name);
            }
            return false;
        }

        @Override
        public String toString() {
            return this.name.text;
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
        public boolean match(Term node) {
            if (node instanceof Term.Number) {
                return this.num == ((Number) node).num;
            }
            return false;
        }

        @Override
        public String toString() {
            return String.valueOf(this.num);
        }
    }

    public interface Visitor <R> {
        R visitAtom(Atom term);
        R visitVariable(Variable term);
        R visitNumber(Number term);
    }
}
