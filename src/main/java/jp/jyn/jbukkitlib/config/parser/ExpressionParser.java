package jp.jyn.jbukkitlib.config.parser;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleUnaryOperator;

/**
 * <p>Arithmetic expression parser.</p>
 * <p>Priority: (), function -&gt; *, /, %, ^ -&gt; +, -</p>
 * <p>Available functions:
 * abs, sin, cos, tan, sinh, cosh, tanh, asin, acos, atan,
 * round, floor, ceil, exp, log, log10, log1p, sqrt, cbrt,
 * signum, radian, degrees, negate, min, max, pow, hypot,
 * eq, ne, gt, lt, ge ,le, scale, random, pi, e</p>
 */
public class ExpressionParser {
    private final Node node;

    public ExpressionParser(CharSequence expression) {
        Node node = expr(expression);
        this.node = preCalc(node);
    }

    private static Node preCalc(Node node) {
        // It can be calculated in advance.
        if (!node.isImmutable() || node instanceof NumberNode) {
            return node;
        }
        return new NumberNode(node.calc(Collections.emptyMap()));
    }

    /**
     * Parsing the expression
     *
     * @param expression expression
     * @return parsed expression
     */
    public static ExpressionParser parse(CharSequence expression) {
        return new ExpressionParser(expression);
    }

    /**
     * Calculate using variables
     *
     * @param variable Variable map
     * @return result
     */
    public double calc(Map<String, Double> variable) {
        return node.calc(variable);
    }

    /**
     * Calculate using single variables
     *
     * @param key   Variable key
     * @param value Variable value
     * @return result
     */
    public double calc(String key, Double value) {
        return calc(Collections.singletonMap(key, value));
    }

    /**
     * Calculate
     *
     * @return result
     */
    public double calc() {
        return calc(Collections.emptyMap());
    }

    private static Node expr(CharSequence expression) {
        Queue<String> exp = exprQueue(expression);

        Node last = term(exp);
        while (!exp.isEmpty()) {
            var v = exp.peek();
            if (v.length() != 1) {
                break;
            }

            switch (v.charAt(0)) {
                case '+':
                    exp.remove();
                    //noinspection Convert2MethodRef
                    last = new BinaryOperatorNode((left, right) -> left + right, last, term(exp));
                    continue;
                case '-':
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
        while (!exp.isEmpty()) {
            var v = exp.peek();
            if (v.length() != 1) {
                break;
            }

            switch (v.charAt(0)) {
                case '*':
                    exp.remove();
                    last = new BinaryOperatorNode((left, right) -> left * right, last, factor(exp));
                    continue;
                case '/':
                    exp.remove();
                    last = new BinaryOperatorNode((left, right) -> left / right, last, factor(exp));
                    continue;
                case '%':
                    exp.remove();
                    last = new BinaryOperatorNode((left, right) -> left % right, last, factor(exp));
                    continue;
                case '^':
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

    private final static Map<String, DoubleUnaryOperator> UNARY_FUNCTIONS = Map.ofEntries(
        Map.entry("abs", Math::abs),
        Map.entry("sin", Math::sin),
        Map.entry("cos", Math::cos),
        Map.entry("tan", Math::tan),
        Map.entry("sinh", Math::sinh),
        Map.entry("cosh", Math::cosh),
        Map.entry("tanh", Math::tanh),
        Map.entry("asin", Math::asin),
        Map.entry("acos", Math::acos),
        Map.entry("atan", Math::atan),
        Map.entry("round", Math::round),
        Map.entry("floor", Math::floor),
        Map.entry("ceil", Math::ceil),
        Map.entry("exp", Math::exp),
        Map.entry("log", Math::log),
        Map.entry("log10", Math::log10),
        Map.entry("log1p", Math::log1p),
        Map.entry("sqrt", Math::sqrt),
        Map.entry("cbrt", Math::cbrt),
        Map.entry("signum", Math::signum),
        Map.entry("radian", Math::toRadians),
        Map.entry("degrees", Math::toDegrees),
        Map.entry("negate", d -> -d)
    );

    private final static Map<String, DoubleBinaryOperator> BINARY_FUNCTIONS = Map.ofEntries(
        Map.entry("min", Math::min),
        Map.entry("max", Math::max),
        Map.entry("pow", Math::pow),
        Map.entry("hypot", Math::hypot),
        Map.entry("eq", (v1, v2) -> v1 == v2 ? 1.0 : 0.0),
        Map.entry("ne", (v1, v2) -> v1 != v1 ? 1.0 : 0.0),
        Map.entry("gt", (v1, v2) -> v1 > v2 ? 1.0 : 0.0),
        Map.entry("ge", (v1, v2) -> v1 >= v2 ? 1.0 : 0.0),
        Map.entry("lt", (v1, v2) -> v1 < v2 ? 1.0 : 0.0),
        Map.entry("le", (v1, v2) -> v1 <= v2 ? 1.0 : 0.0),
        Map.entry("scale", (d, scale) -> BigDecimal.valueOf(d).setScale((int) scale, RoundingMode.DOWN).doubleValue()),
        Map.entry("range", (min, max) -> ThreadLocalRandom.current().nextDouble(min, max))
    );

    private final static Map<String, Node> SUPPLIER_FUNCTIONS = Map.of(
        "random", new RandomNode(),
        "pi", new NumberNode(Math.PI),
        "e", new NumberNode(Math.E)
    );

    private static Node function(String value) {
        // func(123+456) -> func,123+456)
        String[] tmp = value.split("\\(", 2);
        String func = tmp[0];
        String args = tmp[1];
        // 123+456)->123+456
        if (!args.isEmpty() && args.charAt(args.length() - 1) == ')') {
            args = args.substring(0, args.length() - 1);
        }

        // search function.
        DoubleUnaryOperator unaryOperator = UNARY_FUNCTIONS.get(func);
        if (unaryOperator != null) {
            return new UnaryOperatorNode(unaryOperator, expr(args));
        }

        DoubleBinaryOperator binaryOperator = BINARY_FUNCTIONS.get(func);
        if (binaryOperator == null) {
            // supplier
            Node supplier = SUPPLIER_FUNCTIONS.get(func);
            if (supplier != null) {
                return supplier;
            }
            throw new IllegalArgumentException("Unknown function:" + func);
        }

        // parse left/right expression.
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

        return new BinaryOperatorNode(binaryOperator, left, right);
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
                case '+', '-', '*', '/', '%', '^':
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
        private RandomNode() { }

        @Override
        public double calc(Map<String, Double> variable) {
            return ThreadLocalRandom.current().nextDouble();
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
            this.node = preCalc(node);
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
            this.left = preCalc(left);
            this.right = preCalc(right);
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
