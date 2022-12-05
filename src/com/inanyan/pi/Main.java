package com.inanyan.pi;

import com.inanyan.prolog.logic.*;
import com.inanyan.prolog.parsing.Lexer;
import com.inanyan.prolog.parsing.Parser;
import com.inanyan.prolog.parsing.Token;
import com.inanyan.prolog.repr.Clause;
import com.inanyan.prolog.repr.Term;
import com.inanyan.prolog.util.ErrorListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class Main {
    private final static LogicBase base = new LogicBase();
    private static boolean hadError = false;
    private static String currentSource;

    private static InputStreamReader input = new InputStreamReader(System.in);
    private static BufferedReader reader = new BufferedReader(input);

    private final static ErrorListener errorListener = new ErrorListener() {
        @Override
        public void reportParsingError(int line, String msg) {
            hadError = true;
            report(line, "error", msg);
        }

        @Override
        public void reportParsingWarning(int line, String msg) {
            report(line, "warning", msg);
        }

        @Override
        public void reportRuntimeError(int line, String msg) {
            hadError = true;
            report(line, "runtime error", msg);
        }

        private void report(int pos, String type, String msg) {
            System.out.println(currentSource + ":" + String.valueOf(pos) + ": " + type + ": " + msg + ".");
        }
    };

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: pi filename");
            System.exit(1);
        } else {
            if (!loadFile(args[0])) {
                System.out.println("Errors occurred while file was loading. Exiting...");
                System.exit(2);
            } else {
                printWelcomeMsg();

                if (!startConsulting()) {
                    System.out.println("Errors occurred while file consulting. Exiting...");
                    System.exit(3);
                }
            }
        }
    }

    private static boolean loadFile(String path) {
        currentSource = path;
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(path));
            String source = new String(bytes, Charset.defaultCharset());
            List<Clause> clauses = generate(source);
            if (clauses == null) return false;
            base.add(clauses);
            return true;
        } catch (IOException e) {
            // TODO: Print e?
            return false;
        }
    }

    private static void printWelcomeMsg() {
        System.out.println("PI - prolog interpreter(?) v0.1 by InAnYan.");
        System.out.println("Written for educational purposes.");
        System.out.println("To exit type ':quit' (without quotes).");
        System.out.println("\n");
    }

    private static boolean startConsulting() {
        currentSource = "<consult>";
        hadError = false;

        while (true) {
            System.out.print("> ");
            try {
                String line = reader.readLine();

                if (line == null) return true;
                else if (line.isEmpty()) continue;
                else if (line.charAt(0) == ':') {
                    if (line.equals(":quit"))
                        return true;
                }

                if (line.startsWith("+")) {
                    addStr(line.substring(1));
                } else {
                    consultStr(line);
                }
                hadError = false;
            } catch (IOException e) {
                // TODO: Print e?
                return false;
            }
        }
    }

    private static List<Clause> generate(String src) {
        Lexer lexer = new Lexer(errorListener, src);
        List<Token> tokens = lexer.scanTokens();
        if (hadError) return null;

        Parser parser = new Parser(errorListener, tokens);
        List<Clause> clauses = parser.parse();
        if (hadError) return null;

        return clauses;
    }

    private static void consultStr(String line) {
        List<Clause> clauses = generate(line);
        if (clauses == null) return;

        if (clauses.size() != 1) {
            System.out.println("error: only one clause is allowed to consult");
        } else {
            GoalPerformer performer = new GoalPerformer(errorListener, base, clauses.get(0));
            run(performer);
        }
    }

    private static void addStr(String line) {
        List<Clause> clauses = generate(line);
        if (clauses == null) return;

        base.add(clauses);
    }

    private static void run(GoalPerformer performer) {
        Environment env = new Environment();
        boolean result = performer.call(env);

        if (result) {
            for (Map.Entry<String, Term> entry : env) {
                System.out.println(entry.getKey() + " = " + entry.getValue().toString());
            }

            if (env.isEmpty()) {
                System.out.println("yes.");
                return;
            }

            if (askUserToRedo()) {
                run(performer);
            }
        } else {
            System.out.println("no.");
        }
    }

    public static boolean askUserToRedo() {
        try {
            while (true) {
                String in = reader.readLine();

                if (in == null || in.isEmpty()) {
                    return false;
                }

                if (in.equals(".")) {
                    return false;
                } else if (in.equals(";")) {
                    return true;
                } else {
                    System.out.println("info: you should answer '.' or ';' (without quotes).");
                }
            }
        } catch (IOException e) {
            // TODO: Print e?
            return false;
        }
    }
}