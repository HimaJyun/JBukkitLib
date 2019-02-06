package jp.jyn.jbukkitlib.config.parser.template.variable;

import java.util.function.Supplier;

public interface TemplateVariable {
    TemplateVariable put(String key, String value);

    TemplateVariable put(String key, Supplier<String> value);

    default TemplateVariable put(String key, Object value) {
        return this.put(key, value.toString());
    }

    default TemplateVariable put(String key, int value) {
        return this.put(key, String.valueOf(value));
    }

    default TemplateVariable put(String key, long value) {
        return this.put(key, String.valueOf(value));
    }

    default TemplateVariable put(String key, float value) {
        return this.put(key, String.valueOf(value));
    }

    default TemplateVariable put(String key, double value) {
        return this.put(key, String.valueOf(value));
    }

    default TemplateVariable put(String key, boolean value) {
        return this.put(key, String.valueOf(value));
    }

    TemplateVariable clear();

    String get(String key);
}
