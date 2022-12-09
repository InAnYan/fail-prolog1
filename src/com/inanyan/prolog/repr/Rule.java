package com.inanyan.prolog.repr;

public class Rule {
    public final Logic.Fact head;
    public final Logic body;

    public Rule(Logic.Fact head, Logic body) {
        this.head = head;
        this.body = body;
    }
}
