package com.inanyan.prolog.parsing;

import com.inanyan.prolog.repr.Logic;
import com.inanyan.prolog.repr.Rule;
import com.inanyan.prolog.repr.Term;
import com.inanyan.prolog.util.ErrorListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Parser {
    private final List<Token> tokens;
    private final ErrorListener errorListener;
    private int pos = 0;

    private final Set<Term.Var> boundVariables = new HashSet<>();

    public Parser(ErrorListener errorListener, List<Token> tokens) {
        this.tokens = tokens;
        this.errorListener = errorListener;
    }

    private static class ParserError extends RuntimeException {}

    public List<Rule> parseProgram() {
        List<Rule> clauses = new ArrayList<>();

        while (!isAtEnd()) {
            try {
                boundVariables.clear();
                clauses.add(rule());
            } catch (ParserError e) {
                synchronize();
            }
        }

        return clauses;
    }

    public Logic parseREPL() {
        return conjunction();
    }

    private Rule rule() {
        Logic.Fact head = factWithoutDot();

        if (match(TokenType.NECK)) {
            Logic body = conjunction();
            return new Rule(head, body);
        } else {
            int line = previous().line;
            return new Rule(head, new Logic.Fact("true", line, new ArrayList<>(), new HashSet<>()));
        }
    }

    private Logic conjunction() {
        List<Logic> clauses = new ArrayList<>();
        do {
            clauses.add(factWithoutDot());
        } while (match(TokenType.COMMA));
        require(TokenType.DOT, "expected '.' at the end of"
                + (clauses.size() == 1 ? " fact clause" : " compound clause"));

        if (clauses.size() == 1) {
            return clauses.get(0);
        } else {
            return new Logic.Conjunction(clauses);
        }
    }

    private Logic.Fact factWithoutDot() {
        Token name = require(TokenType.ATOM, "expected fact name");
        if (match(TokenType.OPEN_PAREN)) {
            List<Term> terms = termsCommaList();
            require(TokenType.CLOSE_PAREN, "expected ')' at the end of fact clause");

            Set<Term.Var> ownSet = getFactsOwnSet(terms);
            boundVariables.addAll(ownSet);

            return new Logic.Fact(name, terms, ownSet);
        } else {
            return new Logic.Fact(name, new ArrayList<>(), new HashSet<>());
        }
    }

    private Set<Term.Var> getFactsOwnSet(List<Term> terms) {
        Set<Term.Var> ownSet = new HashSet<>();

        for (Term term : terms) {
            if (term instanceof Term.Var varTerm) {
                ownSet.add(varTerm);
            }
        }

        ownSet.removeAll(boundVariables);

        return ownSet;
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
            return new Term.Atom(previous().text);
        } else if (match(TokenType.NUMBER)) {
            try {
                return new Term.Number(Integer.parseInt(previous().text));
            } catch (NumberFormatException e) {
                errorAtPrevious("can not parse number");
            }
        } else if (match(TokenType.VARIABLE)) {
            return new Term.Var(previous().text);
        }

        errorAtPrevious("expected term");
        return null;
    }

    private void synchronize() {
        boundVariables.clear();
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
            return new Token(-1, TokenType.NECK, "Unreachable code, beacuse of error");
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
