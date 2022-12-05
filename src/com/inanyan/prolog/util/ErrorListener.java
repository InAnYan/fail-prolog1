package com.inanyan.prolog.util;

public interface ErrorListener {
    void reportParsingError(int line, String msg);
    void reportParsingWarning(int line, String msg);

    void reportRuntimeError(int line, String msg);
    // void reportRuntimeWarning(int line, String msg);
}
