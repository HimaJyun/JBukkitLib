package jp.jyn.jbukkitlib.config.parser.component;

import jp.jyn.jbukkitlib.util.PackagePrivate;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

@PackagePrivate
abstract class Node {
    protected final TextComponent component;

    protected Node(TextComponent component) {
        this.component = component;
    }

    protected abstract TextComponent apply(ComponentVariable variable);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return component.equals(node.component);
    }

    @Override
    public int hashCode() {
        return 31 + Objects.hashCode(component);
    }

    @PackagePrivate
    static final class TextNode extends Node {
        @PackagePrivate
        TextNode(TextComponent component) {
            super(component);
        }

        @Override
        protected TextComponent apply(ComponentVariable variable) {
            return component;
        }

        @Override
        public String toString() {
            return "TextNode{" +
                "component=" + component +
                '}';
        }
    }

    @PackagePrivate
    static final class VariableNode extends Node {
        private final String name;

        @PackagePrivate
        VariableNode(TextComponent component, String name) {
            super(component);
            this.name = name;
        }

        @Override
        protected TextComponent apply(ComponentVariable variable) {
            TextComponent c = component.duplicate();

            Consumer<TextComponent> v = variable.getVariable(name);
            if (v == null) { // unknown variable(typo... etc)
                v = co -> co.setText("{" + name + "}");
            }
            v.accept(c);

            return c;
        }

        @Override
        public String toString() {
            return "VariableNode{" +
                "name='" + name + '\'' +
                ", component=" + component +
                '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;
            VariableNode that = (VariableNode) o;
            return name.equals(that.name);
        }

        @Override
        public int hashCode() {
            int result = 1;
            result = result * 31 + super.hashCode();
            result = result * 31 + Objects.hashCode(name);
            return result;
        }
    }

    @PackagePrivate
    static final class FunctionNode extends Node {
        private final String name;
        private final List<String> args;

        @PackagePrivate
        FunctionNode(TextComponent component, String name, List<String> args) {
            super(component);
            this.name = name;
            // 起動中はずっと使いまわすのでunmodifiableに詰め直す
            this.args = Collections.unmodifiableList(Arrays.asList(args.toArray(new String[0])));
        }

        @Override
        protected TextComponent apply(ComponentVariable variable) {
            TextComponent c = component.duplicate();

            BiConsumer<TextComponent, List<String>> f = variable.getFunction(name);
            if (f == null) { // unknown function
                f = (co, a) -> co.setText("{" + name + "(" + String.join(",", a) + ")}");
            }
            f.accept(c, args);

            return c;
        }

        @Override
        public String toString() {
            return "FunctionNode{" +
                "name='" + name + '\'' +
                ", args=[" + String.join(",", args) + ']' +
                ", component=" + component +
                '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;
            FunctionNode that = (FunctionNode) o;
            return name.equals(that.name) &&
                args.equals(that.args);
        }

        @Override
        public int hashCode() {
            int result = 1;
            result = result * 31 + super.hashCode();
            result = result * 31 + Objects.hashCode(name);
            result = result * 31 + Objects.hashCode(args);
            return result;
        }
    }


    @PackagePrivate
    static ComponentParser build(Collection<Node> nodes) {
        for (Node node : nodes) {
            if (!(node instanceof Node.TextNode)) {
                return new Simple(nodes);
            }
        }
        return new Raw(nodes);
    }

    private final static class Simple implements ComponentParser {
        private final Node[] node;

        private Simple(Collection<Node> node) {
            this.node = node.toArray(new Node[0]);
        }

        @Override
        public Component apply(ComponentVariable variable) {
            TextComponent[] components = new TextComponent[node.length];
            for (int i = 0; i < node.length; i++) {
                components[i] = node[i].apply(variable);
            }
            return new Component(components);
        }

        @Override
        public String toString() {
            return "ComponentParser{" + // 大嘘
                Arrays.toString(node) +
                '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Simple simple = (Simple) o;
            return Arrays.equals(node, simple.node);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(node);
        }
    }

    private final static class Raw implements ComponentParser {
        private final Component component;

        private Raw(Collection<Node> node) {
            TextComponent[] components = new TextComponent[node.size()];
            int i = 0;
            for (Node n : node) {
                components[i++] = n.apply(ComponentVariable.EMPTY_VARIABLE);
            }
            this.component = new Component(components);
        }

        @Override
        public Component apply(ComponentVariable variable) { return component; }

        @Override
        public Component apply() { return component; }

        @Override
        public Component apply(String key, String value) { return component; }

        @Override
        public Component apply(String key, Object value) { return component; }

        @Override
        public Component apply(String key, Supplier<String> value) { return component; }

        @Override
        public Component apply(String... values) { return component; }

        @Override
        public Component apply(String key, int value) { return component; }

        @Override
        public Component apply(String key, long value) { return component; }

        @Override
        public Component apply(String key, double value) { return component; }

        @Override
        public Component apply(String key, char value) { return component; }

        @Override
        public Component apply(String key, boolean value) { return component; }

        @Override
        public String toString() {
            return "ComponentParser.Raw{" + // 大嘘
                component +
                '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Raw raw = (Raw) o;
            return component.equals(raw.component);
        }

        @Override
        public int hashCode() {
            return 31 + component.hashCode();
        }
    }
}
