package jp.jyn.jbukkitlib.config.parser.template.variable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Simple string variable.
 */
public class StringVariable implements TemplateVariable {
    private final Map<String, String> variable = new HashMap<>();

    /**
     * Create new instance.
     *
     * @return for method chain.
     */
    public static StringVariable init() {
        return new StringVariable();
    }

    @Override
    public StringVariable put(String key, String value) {
        variable.put(key, value);
        return this;
    }

    /**
     * <p>Add variable.</p>
     * <p>Note: Suppliers are executed immediately.</p>
     *
     * @param key   variable name
     * @param value variable value
     * @return for method chain
     */
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
