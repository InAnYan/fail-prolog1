package com.inanyan.prolog.repr;

import com.inanyan.prolog.util.ParsingRules;

public abstract class Term {
    public abstract <R> R accept(Visitor<R> visitor);

    public abstract String toString();

    public static class Atom extends Term {
        public final String name;

        public Atom(String name) {
            this.name = name;
        }

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitAtom(this);
        }
        @Override
        public String toString() {
            if (ParsingRules.isStringLooksLikeVariable(this.name)) {
                return "'" + this.name + "'";
            } else {
                return this.name;
            }
        }
    }

    public static class Var extends Term {
        public final String name;

        public Var(String name) {
            this.name = name;
        }

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitVariable(this);
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    public static class Number extends Term {
        public final int num;

        public Number(int num) {
            this.num = num;
        }

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitNumber(this);
        }

        @Override
        public String toString() {
            return String.valueOf(this.num);
        }
    }

    public interface Visitor <R> {
        R visitAtom(Atom term);
        R visitVariable(Var term);
        R visitNumber(Number term);
    }
}
