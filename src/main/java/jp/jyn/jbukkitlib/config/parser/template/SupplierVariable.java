package jp.jyn.jbukkitlib.config.parser.template;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Lazy evaluation variable for {@link TemplateParser}. (non Thread-Safe)
 */
public class SupplierVariable implements TemplateVariable {
    private final Map<String, Supplier<String>> variable = new HashMap<>();

    /**
     * Create new instance.
     *
     * @return for method chain
     */
    public static SupplierVariable init() {
        return new SupplierVariable();
    }

    @Override
    public TemplateVariable put(String key, String value) {
        return put(key, () -> value);
    }

    @Override
    public TemplateVariable put(String key, Supplier<String> value) {
        variable.put(key, value);
        return this;
    }

    /**
     * <p>Put variable.</p>
     *
     * <p>{@link Object#toString()} is execute at the time it is used.
     * That is, the thread and timing to be executed is undefined.</p>
     *
     * @param key   variable name
     * @param value variable value
     * @return for method chain
     */
    @Override
    public TemplateVariable put(String key, Object value) {
        return put(key, value::toString);
    }

    @Override
    public TemplateVariable put(String key, byte value) {
        return put(key, () -> String.valueOf(value));
    }

    @Override
    public TemplateVariable put(String key, short value) {
        return put(key, () -> String.valueOf(value));
    }

    @Override
    public TemplateVariable put(String key, int value) {
        return put(key, () -> String.valueOf(value));
    }

    @Override
    public TemplateVariable put(String key, long value) {
        return put(key, () -> String.valueOf(value));
    }

    @Override
    public TemplateVariable put(String key, float value) {
        return put(key, () -> String.valueOf(value));
    }

    @Override
    public TemplateVariable put(String key, double value) {
        return put(key, () -> String.valueOf(value));
    }

    @Override
    public TemplateVariable put(String key, char value) {
        return put(key, () -> String.valueOf(value));
    }

    @Override
    public TemplateVariable put(String key, boolean value) {
        return put(key, () -> String.valueOf(value));
    }

    @Override
    public TemplateVariable clear() {
        variable.clear();
        return this;
    }

    @Override
    public String get(String key) {
        Supplier<String> supplier = variable.get(key);
        if (supplier == null) {
            return null;
        }
        return supplier.get();
    }

    @Override
    public String toString() {
        return "SupplierVariable{" + variable + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SupplierVariable that = (SupplierVariable) o;
        return variable.equals(that.variable);
    }

    @Override
    public int hashCode() {
        return 31 + Objects.hashCode(variable);
    }
}
