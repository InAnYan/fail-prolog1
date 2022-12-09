package com.inanyan.prolog.util;

public class ParsingRules {
    public static boolean isStringLooksLikeAtom(String str) {
        return !isStringLooksLikeVariable(str);
    }

    public static boolean isStringLooksLikeVariable(String str) {
        return str.contains(" ") || isShowsVariable(str.charAt(0));
    }

    public static boolean isShowsVariable(char ch) {
        return Character.isUpperCase(ch) || ch == '_';
    }

    public static boolean isDigit(char ch) {
        return Character.isDigit(ch);
    }

    public static boolean isAlpha(char ch) {
        return Character.isAlphabetic(ch);
    }

    public static boolean isAlphanum(char ch) {
        return isAlpha(ch) || isDigit(ch);
    }
}
