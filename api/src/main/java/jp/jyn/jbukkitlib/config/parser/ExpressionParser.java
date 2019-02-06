package jp.jyn.jbukkitlib.config.parser;

import jp.jyn.jbukkitlib.util.XorShift;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleUnaryOperator;

public class ExpressionParser {
    private final Node node;

    public ExpressionParser(CharSequence expression) {
        Node node = expr(expression);
        if (node.isImmutable()) {
            // It can be calculated in advance.
            node = new NumberNode(node.calc(Collections.emptyMap()));
        }
        this.node = node;
    }

    public static ExpressionParser parse(CharSequence expression) {
        return new ExpressionParser(expression);
    }

    public double calc(Map<String, Double> variable) {
        return node.calc(variable);
    }

    public double calc(String key, Double value) {
        return calc(Collections.singletonMap(key, value));
    }

    public double calc() {
        return calc(Collections.emptyMap());
    }

    private static Node expr(CharSequence expression) {
        Queue<String> exp = exprQueue(expression);

        Node last = term(exp);
        while (true) {
            if (exp.isEmpty()) {
                break;
            } else switch (exp.peek()) {
                case "+":
                    exp.remove();
                    last = new BinaryOperatorNode((left, right) -> left + right, last, term(exp));
                    continue;
                case "-":
                    exp.remove();
                    last = new BinaryOperatorNode((left, right) -> left - right, last, term(exp));
                    continue;
            }
            break;
        }
        return last;
    }

    private static Node term(Queue<String> exp) {
        Node last = factor(exp);
        while (true) {
            if (exp.isEmpty()) {
                break;
            } else switch (exp.peek()) {
                case "*":
                    exp.remove();
                    last = new BinaryOperatorNode((left, right) -> left * right, last, factor(exp));
                    continue;
                case "/":
                    exp.remove();
                    last = new BinaryOperatorNode((left, right) -> left / right, last, factor(exp));
                    continue;
                case "%":
                    exp.remove();
                    last = new BinaryOperatorNode((left, right) -> left % right, last, factor(exp));
                    continue;
                case "^":
                    exp.remove();
                    last = new BinaryOperatorNode(Math::pow, last, factor(exp));
                    continue;
            }
            break;
        }
        return last;
    }

    private static Node factor(Queue<String> exp) {
        String value = exp.remove();
        if (value.charAt(0) == '(') { // (123 + 456)
            return expr(value.substring(1, value.length() - 1));
        }

        try { // 123.456
            return new NumberNode(Double.parseDouble(value));
        } catch (NumberFormatException ignore) { }

        if (!value.contains("(")) { // variable
            return new VariableNode(value);
        }

        // func(123+456)
        return function(value);
    }

    private static Node function(String value) {
        // func(123+456) -> func,123+456)
        String[] tmp = value.split("\\(", 2);
        String func = tmp[0];
        String args = tmp[1];
        // 123+456)->123+456
        if (!args.isEmpty() && args.charAt(args.length() - 1) == ')') {
            args = args.substring(0, args.length() - 1);
        }

        switch (func) { // UnaryOperator function
            case "abs":
                return new UnaryOperatorNode(Math::abs, expr(args));
            case "sin":
                return new UnaryOperatorNode(Math::sin, expr(args));
            case "cos":
                return new UnaryOperatorNode(Math::cos, expr(args));
            case "tan":
                return new UnaryOperatorNode(Math::tan, expr(args));
            case "round":
                return new UnaryOperatorNode(Math::round, expr(args));
            case "floor":
                return new UnaryOperatorNode(Math::floor, expr(args));
            case "ceil":
                return new UnaryOperatorNode(Math::ceil, expr(args));
            case "log":
                return new UnaryOperatorNode(Math::log, expr(args));
            case "negative":
                return new UnaryOperatorNode(d -> -d, expr(args));
            case "random": // special
                return RandomNode.instance;
            case "min":
            case "max":
                break;
            default:
                throw new IllegalArgumentException("Unknown function: " + func);
        }

        // BinaryOperator function
        // need help: How to realize with regex?
        int nest = 0;
        Node left = null;
        Node right = null;
        for (int i = 0; i < args.length(); i++) {
            char c = args.charAt(i);
            nest += nest(c);

            if (nest == 0 && c == ',') {
                left = expr(args.substring(0, i));
                right = expr(args.substring(i + 1));
                break;
            }
        }

        if (left == null || right == null) {
            throw new IllegalArgumentException("Argument is missing: " + value);
        }

        // double check.
        switch (func) {
            case "min":
                return new BinaryOperatorNode(Math::min, left, right);
            case "max":
                return new BinaryOperatorNode(Math::max, left, right);
            default:
                throw new IllegalArgumentException("Unknown function: " + func);
        }
    }

    private static int nest(char c) {
        switch (c) {
            case '(':
                return +1;
            case ')':
                return -1;
            default:
                return 0;
        }
    }

    private static Queue<String> exprQueue(CharSequence expression) {
        Queue<String> exp = new LinkedList<>();
        int nest = 0;

        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);

            if (nest > 0) {
                nest += nest(c);
                buf.append(c);
                if (nest == 0) {
                    exp.add(buf.toString());
                    buf.setLength(0);
                }
                continue;
            }

            switch (c) {
                case ' ':
                    continue;
                case '(':
                    nest += 1;
                    buf.append(c);
                    break;
                case '+':
                case '-':
                case '*':
                case '/':
                case '%':
                case '^':
                    if (buf.length() != 0) {
                        exp.add(buf.toString());
                        buf.setLength(0);
                    }
                    exp.add(Character.toString(c));
                    break;
                default:
                    buf.append(c);
                    break;
            }
        }
        if (buf.length() != 0) {
            exp.add(buf.toString());
        }
        return exp;
    }

    // region node
    private interface Node {
        double calc(Map<String, Double> variable);

        boolean isImmutable();
    }

    private static class NumberNode implements Node {
        private final double number;

        public NumberNode(double number) {
            this.number = number;
        }

        @Override
        public double calc(Map<String, Double> variable) {
            return number;
        }

        @Override
        public boolean isImmutable() {
            return true;
        }
    }

    private static class VariableNode implements Node {
        private final String key;

        public VariableNode(String key) {
            this.key = key;
        }

        @Override
        public double calc(Map<String, Double> variable) {
            return variable.getOrDefault(key, Double.NaN);
        }

        @Override
        public boolean isImmutable() {
            return false;
        }
    }

    private static class RandomNode implements Node {
        public final static RandomNode instance = new RandomNode();

        private RandomNode() { }

        @Override
        public double calc(Map<String, Double> variable) {
            return XorShift.THREAD_LOCAL.get().nextDouble();
        }

        @Override
        public boolean isImmutable() {
            return false;
        }
    }

    private static class UnaryOperatorNode implements Node {
        private final DoubleUnaryOperator operator;
        private final Node node;

        public UnaryOperatorNode(DoubleUnaryOperator operator, Node node) {
            this.operator = operator;
            this.node = node;
        }

        @Override
        public double calc(Map<String, Double> variable) {
            return operator.applyAsDouble(node.calc(variable));
        }

        @Override
        public boolean isImmutable() {
            return node.isImmutable();
        }
    }

    private static class BinaryOperatorNode implements Node {
        private final DoubleBinaryOperator operator;
        private final Node left;
        private final Node right;

        public BinaryOperatorNode(DoubleBinaryOperator operator, Node left, Node right) {
            this.operator = operator;
            this.left = left;
            this.right = right;
        }

        @Override
        public double calc(Map<String, Double> variable) {
            return operator.applyAsDouble(left.calc(variable), right.calc(variable));
        }

        @Override
        public boolean isImmutable() {
            return left.isImmutable() && right.isImmutable();
        }
    }
    // endregion
}
