package com.inanyan.prolog.parsing;

import com.inanyan.prolog.util.ErrorListener;
import com.inanyan.prolog.util.ParsingRules;

import java.util.ArrayList;
import java.util.List;

public class Lexer {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private final ErrorListener errorListener;
    private int start, current;
    private int line = 1;

    public Lexer(ErrorListener errorListener, String source) {
        this.errorListener = errorListener;
        this.source = source;
        this.start = 0;
        this.current = 0;
    }

    public List<Token> scanTokens() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }

        tokens.add(new Token(source.length(), TokenType.EOF, ""));
        return tokens;
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private void scanToken() {
        char cur = advance();
        switch (cur) {
            case '\n':
                line++;
            case ' ':
            case '\t':
            case '\r':
                break;

            case '.':  addToken(TokenType.DOT); break;
            case ',':  addToken(TokenType.COMMA); break;
            case ')':  addToken(TokenType.CLOSE_PAREN); break;
            case '(':  addToken(TokenType.OPEN_PAREN); break;

            case ':': {
                if (peek() == '-') {
                    addToken(TokenType.NECK);
                } else {
                    errorListener.reportParsingError(line, "unknown character");
                }
                advance();
                break;
            }

            case '\'': atomInQuotes(); break;

            default: {
                if (ParsingRules.isDigit(cur)) {
                    number();
                } else if (ParsingRules.isAlpha(cur) || cur == '_') {
                    identifier();
                } else {
                    errorListener.reportParsingError(line, "unknown character");
                }
                break;
            }
        }
    }

    private void atomInQuotes() {
        while (!isAtEnd() && peek() != '\'') {
            advance();
        }

        if (isAtEnd()) {
            errorListener.reportParsingError(line, "unterminated atom");
            return;
        }

        advance();
        addToken(TokenType.ATOM);
    }

    private void number() {
        while (!isAtEnd() && ParsingRules.isDigit(peek())) {
            advance();
        }
        addToken(TokenType.NUMBER);
    }

    private void identifier() {
        while (!isAtEnd() && (ParsingRules.isAlphanum(peek()) || peek() == '_')) {
            advance();
        }
        addToken(ParsingRules.isShowsVariable(source.charAt(start)) ? TokenType.VARIABLE : TokenType.ATOM);
    }

    private void addToken(TokenType type) {
        tokens.add(new Token(line, type, source.substring(start, current)));
    }

    private char peek() {
        if (isAtEnd()) return '\0';
        else return source.charAt(current);
    }

    private char advance() {
        current++;
        return source.charAt(current - 1);
    }
}
