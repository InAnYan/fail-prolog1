package com.inanyan.pi;

import com.inanyan.prolog.logic.Environment;
import com.inanyan.prolog.logic.Goal;
import com.inanyan.prolog.logic.GoalGenerator;
import com.inanyan.prolog.logic.LogicBase;
import com.inanyan.prolog.parsing.Lexer;
import com.inanyan.prolog.parsing.Parser;
import com.inanyan.prolog.parsing.Token;
import com.inanyan.prolog.repr.Clause;
import com.inanyan.prolog.util.ErrorListener;

import java.util.List;

public class Interpreter {
    private final LogicBase base = new LogicBase();
    private boolean hadError = false;

    private final ErrorListener parentErrorListener;

    private final ErrorListener errorListener = new ErrorListener() {
        @Override
        public void reportParsingError(int line, String msg) {
            hadError = true;
            parentErrorListener.reportParsingError(line, msg);
        }

        @Override
        public void reportParsingWarning(int line, String msg) {
            parentErrorListener.reportParsingWarning(line, msg);
        }

        @Override
        public void reportRuntimeError(int line, String msg) {
            hadError = true;
            parentErrorListener.reportRuntimeError(line, msg);
        }
    };

    private boolean consultState = false;
    private Goal currentGoal = null;
    private Environment currentEnvironment = new Environment();

    public Interpreter(ErrorListener parentErrorListener) {
        this.parentErrorListener = parentErrorListener;
    }

    private List<Clause> generate(String source) {
        Lexer lexer = new Lexer(errorListener, source);
        List<Token> tokens = lexer.scanTokens();
        if (hadError) return null;

        Parser parser = new Parser(errorListener, tokens);
        List<Clause> clauses = parser.parse();
        if (hadError) return null;

        return clauses;
    }

    public void add(String source) {
        hadError = false;
        List<Clause> clauses = generate(source);
        if (hadError || clauses == null) return;

        base.add(clauses);
    }

    public Environment getCurrentEnvironment() {
        return this.currentEnvironment;
    }

    public boolean startConsulting(String source) {
        hadError = false;
        List<Clause> clauses = generate(source);
        if (hadError || clauses == null) return false;

        if (clauses.size() != 1) {
            errorListener.reportRuntimeError(0, "only one clause is allowed to consult");
            return false;
        }

        currentGoal = new GoalGenerator(clauses.get(0), base).generate();
        consultState = true;
        return true;
    }

    public void abort() {
        consultState = false;
        currentGoal = null;
        currentEnvironment.clear();
    }

    public boolean call() {
        assert consultState;

        return currentGoal.call(currentEnvironment);
    }

    public boolean redo() {
        assert consultState;

        return currentGoal.redo(currentEnvironment);
    }

    public boolean isInConsultState() {
        return this.consultState;
    }
}
