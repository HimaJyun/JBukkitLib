package jp.jyn.jbukkitlib.config.parser.template;

import jp.jyn.jbukkitlib.util.PackagePrivate;

import java.util.LinkedList;
import java.util.Queue;

@PackagePrivate
abstract class AbstractParser {
    protected static Queue<String> exprQueue(CharSequence sequence) {
        Queue<String> exp = new LinkedList<>();
        StringBuilder buf = new StringBuilder();
        buf.setLength(0);

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
}
