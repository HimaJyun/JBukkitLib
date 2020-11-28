package jp.jyn.jbukkitlib.config.parser.template;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * String variable for {@link TemplateParser}. (non Thread-Safe)
 */
public class StringVariable implements TemplateVariable {
    private final Map<String, String> variable = new HashMap<>();

    /**
     * Create new instance.
     *
     * @return for method chain
     */
    public static StringVariable init() {
        return new StringVariable();
    }

    @Override
    public TemplateVariable put(String key, String value) {
        variable.put(key, value);
        return this;
    }

    /**
     * <p>Put variable.</p>
     * <p>Supplier are execute immediately.</p>
     *
     * @param key   variable name
     * @param value variable value
     * @return for method chain
     */
    @Override
    public TemplateVariable put(String key, Supplier<String> value) {
        variable.put(key, value.get());
        return this;
    }

    @Override
    public TemplateVariable clear() {
        variable.clear();
        return this;
    }

    @Override
    public String get(String key) {
        return variable.get(key);
    }

    @Override
    public String toString() {
        return "StringVariable{" + variable + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StringVariable that = (StringVariable) o;
        return variable.equals(that.variable);
    }

    @Override
    public int hashCode() {
        return 31 + Objects.hashCode(variable);
    }
}
