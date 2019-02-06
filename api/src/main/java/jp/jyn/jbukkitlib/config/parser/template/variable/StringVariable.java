package jp.jyn.jbukkitlib.config.parser.template.variable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class StringVariable implements TemplateVariable {
    private final Map<String, String> variable = new HashMap<>();

    public static StringVariable init() {
        return new StringVariable();
    }

    @Override
    public StringVariable put(String key, String value) {
        variable.put(key, value);
        return this;
    }

    @Override
    public StringVariable put(String key, Supplier<String> value) {
        variable.put(key, value.get());
        return this;
    }

    @Override
    public StringVariable clear() {
        variable.clear();
        return this;
    }

    @Override
    public String get(String key) {
        return variable.get(key);
    }
}
