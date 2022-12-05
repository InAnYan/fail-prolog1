package com.inanyan.prolog.parsing;

import com.inanyan.prolog.repr.Clause;
import com.inanyan.prolog.repr.Term;
import com.inanyan.prolog.util.ErrorListener;

import java.util.ArrayList;
import java.util.List;

public class Parser {
    private final List<Token> tokens;
    private final ErrorListener errorListener;
    private int pos = 0;

    public Parser(ErrorListener errorListener, List<Token> tokens) {
        this.tokens = tokens;
        this.errorListener = errorListener;
    }

    private static class ParserError extends RuntimeException {}

    public List<Clause> parse() {
        List<Clause> clauses = new ArrayList<>();

        while (!isAtEnd()) {
            try {
                clauses.add(clause());
            } catch (ParserError e) {
                synchronize();
            }
        }

        return clauses;
    }

    private Clause clause() {
        return compound();
    }

    private Clause compound() {
        List<Clause> clauses = new ArrayList<>();
        do {
            clauses.add(factWithoutDot());
        } while (match(TokenType.COMMA));
        require(TokenType.DOT, "expected '.' at the end of"
                + (clauses.size() == 1 ? " fact clause" : " compound clause"));

        if (clauses.size() == 1) {
            return clauses.get(0);
        } else {
            return new Clause.CompoundClauses(clauses);
        }
    }

    private Clause.Fact factWithoutDot() {
        Token name = require(TokenType.ATOM, "expected fact name");
        if (match(TokenType.OPEN_PAREN)) {
            List<Term> terms = termsCommaList();
            require(TokenType.CLOSE_PAREN, "expected ')' at the end of fact clause");
            return new Clause.Fact(name, terms);
        } else {
            return new Clause.Fact(name, new ArrayList<>());
        }
    }

    private Clause.Fact fact() {
        Clause.Fact res = factWithoutDot();
        require(TokenType.DOT, "expected '.' at the end of fact clause");
        return res;
    }

    private List<Term> termsCommaList() {
        List<Term> terms = new ArrayList<>();

        do {
            terms.add(term());
        } while (match(TokenType.COMMA));

        return terms;
    }

    private Term term() {
        if (match(TokenType.ATOM)) {
            return new Term.Atom(previous());
        } else if (match(TokenType.NUMBER)) {
            try {
                return new Term.Number(previous().line, Integer.parseInt(previous().text));
            } catch (NumberFormatException e) {
                errorAtPrevious("can not parse number");
            }
        } else if (match(TokenType.VARIABLE)) {
            return new Term.Variable(previous());
        }

        errorAtPrevious("expected term");
        return null;
    }

    private void synchronize() {
        if (!isAtEnd()) pos++;
        if (previous().type == TokenType.DOT) return;
        while (!isAtEnd() && !match(TokenType.DOT)) {
            pos++;
        }
    }

    private Token require(TokenType type, String errorMsg) {
        if (match(type)) {
            return previous();
        } else {
            errorAtCurrent(errorMsg);
            return null;
        }
    }

    private void errorAtCurrent(String msg) {
        error(tokens.get(pos).line, msg);
    }

    private void errorAtPrevious(String msg) {
        error(tokens.get(pos - 1).line, msg);
    }

    private void error(int line, String msg) {
        errorListener.reportParsingError(line, msg);
        throw new ParserError();
    }

    private boolean match(TokenType type) {
        if (peek().type == type) {
            pos++;
            return true;
        } else {
            return false;
        }
    }

    private Token previous() {
        return tokens.get(pos - 1);
    }

    private Token peek() {
        return tokens.get(pos);
    }

    private boolean isAtEnd() {
        return peek().type == TokenType.EOF;
    }
}
