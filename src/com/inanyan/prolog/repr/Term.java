package com.inanyan.prolog.repr;

import com.inanyan.prolog.util.ParsingRules;

public abstract class Term {
    public abstract <R> R accept(Visitor<R> visitor);

    public abstract String toString();
    public abstract boolean equals(Object object);

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

        @Override
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }

            if (object == null || getClass() != object.getClass()) {
                return false;
            }

            Term.Atom atom = (Term.Atom)object;
            return atom.name.equals(this.name);
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

        @Override
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }

            if (object == null || getClass() != object.getClass()) {
                return false;
            }

            Term.Var atom = (Term.Var)object;
            return atom.name.equals(this.name);
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

        @Override
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }

            if (object == null || getClass() != object.getClass()) {
                return false;
            }

            Term.Number atom = (Term.Number)object;
            return atom.num == this.num;
        }
    }

    public interface Visitor <R> {
        R visitAtom(Atom term);
        R visitVariable(Var term);
        R visitNumber(Number term);
    }
}
