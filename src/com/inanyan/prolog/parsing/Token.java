package com.inanyan.prolog.parsing;

public class Token {
    public final TokenType type;
    public final String text;
    public final int line;

    public Token(int line, TokenType type, String text) {
        this.line = line;
        this.text = text;
        this.type = type;
    }
}
