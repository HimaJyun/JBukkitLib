package jp.jyn.jbukkitlib.config.parser.template;

import jp.jyn.jbukkitlib.util.PackagePrivate;

import java.util.AbstractMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

@PackagePrivate
abstract class AbstractParser {
    protected static Queue<String> exprQueue(CharSequence sequence) {
        Queue<String> exp = new LinkedList<>();
        StringBuilder buf = new StringBuilder();

        int nest = 0;
        boolean escape = false;

        for (int i = 0; i < sequence.length(); i++) {
            char c = sequence.charAt(i);

            if (escape) {
                exp.add(buf.append(c).toString());
                buf.setLength(0);
                escape = false;
                continue;
            }

            if (nest > 0) {
                switch (c) {
                    case '{':
                        nest += 1;
                        break;
                    case '}':
                        nest -= 1;
                        break;
                }
                buf.append(c);
                if (nest == 0) {
                    exp.add(buf.toString());
                    buf.setLength(0);
                }
                continue;
            }

            switch (c) {
                case '{':
                    nest += 1;
                    exp.add(buf.toString());
                    buf.setLength(0);
                    buf.append(c);
                    break;
                case '&':
                    escape = true;
                    exp.add(buf.toString());
                    buf.setLength(0);
                    buf.append(c);
                    break;
                default:
                    buf.append(c);
                    break;
            }
        }

        if (buf.length() != 0) {
            if (escape) { // End with &
                exp.add("&&");
            } else {
                exp.add(buf.toString());
            }
        }

        return exp;
    }

    protected static Map.Entry<String, String[]> parseFunction(String str) {
        String name;
        List<String> args = new LinkedList<>();

        int argsBegin = str.indexOf('(');
        if (argsBegin == -1) {
            throw new IllegalArgumentException("Not function");
        }
        name = str.substring(0, argsBegin).replace(" ", "");
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Function name not found");
        }

        String rawArgs = str.substring(argsBegin + 1);
        if (rawArgs.length() == 1 && rawArgs.charAt(0) == ')') {
            // empty args
            return new AbstractMap.SimpleImmutableEntry<>(name, new String[0]);
        }

        StringBuilder buf = new StringBuilder();
        boolean quotation = false;
        boolean escape = false;
        for (int i = 0, len = rawArgs.length(); i < len; i++) {
            char c = rawArgs.charAt(i);

            if (c == '"') {
                if (escape) {
                    buf.append('"');
                    escape = false;
                } else {
                    quotation = !quotation;
                }
                continue;
            }

            if (quotation && c == '\\') {
                // \\ -> \
                if (escape) {
                    buf.append('\\');
                }
                escape = !escape;
                continue;
            }

            if (!quotation) {
                if (c == ',') {
                    args.add(buf.toString());
                    buf.setLength(0);
                    continue;
                } else if (c == ' ') {
                    continue;
                } else if (c == ')') {
                    break;
                }
            }

            if (escape) {
                // \a -> \a
                buf.append('\\');
                escape = false;
            }
            buf.append(c);
        }
        if (escape) {
            buf.append('\\');
        }
        args.add(buf.toString());

        return new AbstractMap.SimpleImmutableEntry<>(name, args.toArray(new String[0]));
    }
}
