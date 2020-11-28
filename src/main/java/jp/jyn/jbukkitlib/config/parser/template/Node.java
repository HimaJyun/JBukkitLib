package jp.jyn.jbukkitlib.config.parser.template;

import jp.jyn.jbukkitlib.util.PackagePrivate;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

@PackagePrivate
abstract class Node {
    protected final String value;

    protected Node(String value) {
        this.value = value;
    }

    @PackagePrivate
    protected abstract void apply(StringBuilder builder, TemplateVariable variable);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return value.equals(node.value);
    }

    @Override
    public int hashCode() {
        return 31 + Objects.hashCode(value);
    }

    @PackagePrivate
    static final class StringNode extends Node {
        @PackagePrivate
        StringNode(String value) {
            super(value);
        }

        @Override
        protected void apply(StringBuilder builder, TemplateVariable variable) {
            builder.append(value);
        }

        @Override
        public String toString() {
            return "StringNode{value=\'" + value + "\'}";
        }

        @Override
        public boolean equals(Object o) {
            return super.equals(o);
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }
    }

    @PackagePrivate
    static final class VariableNode extends Node {
        @PackagePrivate
        VariableNode(String value) {
            super(value);
        }

        @Override
        protected void apply(StringBuilder builder, TemplateVariable variable) {
            String v = variable.get(value);
            if (v == null) {
                // unknown variable(typo... etc)
                builder.append('{').append(value).append('}');
            } else {
                builder.append(v);
            }
        }

        @Override
        public String toString() {
            return "VariableNode{name=\'" + value + "\'}";
        }

        @Override
        public boolean equals(Object o) {
            return super.equals(o);
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }
    }


    @PackagePrivate
    static TemplateParser build(List<Node> nodes) {
        // string only
        if (nodes.size() == 0) {
            return new RawParser("");
        } else if (nodes.size() == 1 && (nodes.get(0) instanceof StringNode)) {
            StringBuilder sb = StringParser.LOCAL_BUILDER.get(); // ちょっと失敬
            sb.setLength(0);
            nodes.get(0).apply(sb, TemplateVariable.EMPTY_VARIABLE);
            return new RawParser(sb.toString());
        }

        return new StringParser(nodes);
    }

    private final static class StringParser implements TemplateParser {
        private final static ThreadLocal<StringBuilder> LOCAL_BUILDER = ThreadLocal.withInitial(StringBuilder::new);
        private final List<Node> nodes;

        private StringParser(List<Node> nodes) {
            this.nodes = nodes;
        }

        @Override
        public String apply(TemplateVariable variable) {
            StringBuilder builder = LOCAL_BUILDER.get();
            builder.setLength(0);

            for (Node node : nodes) {
                node.apply(builder, variable);
            }

            return builder.toString();
        }

        @Override
        public String toString() {
            return "TemplateParser{" + // 大嘘
                nodes +
                '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            StringParser that = (StringParser) o;
            return nodes.equals(that.nodes);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(nodes);
        }
    }

    private final static class RawParser implements TemplateParser {
        private final String value;

        private RawParser(String value) {
            this.value = value;
        }

        @Override
        public String apply(TemplateVariable variable) { return this.value; }

        @Override
        public String apply() { return this.value; }

        @Override
        public String apply(String key, String value) { return this.value; }

        @Override
        public String apply(String key, Supplier<String> value) { return this.value; }

        @Override
        public String apply(String key, Object value) { return this.value; }

        @Override
        public String apply(String... values) { return this.value; }

        @Override
        public String apply(String key, int value) { return this.value; }

        @Override
        public String apply(String key, long value) { return this.value; }

        @Override
        public String apply(String key, double value) { return this.value; }

        @Override
        public String apply(String key, char value) { return this.value; }

        @Override
        public String apply(String key, boolean value) { return this.value; }

        @Override
        public String toString() {
            return "TemplateParser.Raw{" + // 大嘘
                "value='" + value + '\'' +
                '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            RawParser rawParser = (RawParser) o;
            return value.equals(rawParser.value);
        }

        @Override
        public int hashCode() {
            return 31 + Objects.hashCode(value);
        }
    }
}
