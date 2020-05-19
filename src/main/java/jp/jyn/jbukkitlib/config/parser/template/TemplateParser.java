package jp.jyn.jbukkitlib.config.parser.template;

import jp.jyn.jbukkitlib.config.parser.template.variable.StringVariable;
import jp.jyn.jbukkitlib.config.parser.template.variable.SupplierVariable;
import jp.jyn.jbukkitlib.config.parser.template.variable.TemplateVariable;

import java.util.function.Supplier;

@FunctionalInterface
public interface TemplateParser {
    String toString(TemplateVariable variable);

    default String toString(String key, String value) {
        return toString(StringVariable.init().put(key, value));
    }

    default String toString(String key, Object value) {
        return toString(StringVariable.init().put(key, value));
    }

    default String toString(String key, Supplier<String> value) {
        return toString(SupplierVariable.init().put(key, value));
    }

    default String toString(String key, int value) {
        return toString(StringVariable.init().put(key, value));
    }

    default String toString(String key, long value) {
        return toString(StringVariable.init().put(key, value));
    }

    default String toString(String key, float value) {
        return toString(StringVariable.init().put(key, value));
    }

    default String toString(String key, double value) {
        return toString(StringVariable.init().put(key, value));
    }

    default String toString(String key, boolean value) {
        return toString(StringVariable.init().put(key, value));
    }
}
