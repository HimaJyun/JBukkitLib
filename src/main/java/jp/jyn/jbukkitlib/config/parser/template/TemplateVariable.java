package jp.jyn.jbukkitlib.config.parser.template;

import java.util.function.Supplier;

/**
 * variable for {@link TemplateParser}. (non Thread-Safe)
 */
public interface TemplateVariable {
    /**
     * "Do nothing" variable, always empty.
     */
    TemplateVariable EMPTY_VARIABLE = new TemplateVariable() {
        @Override
        public TemplateVariable put(String key, String value) { return this; }

        @Override
        public TemplateVariable put(String key, Supplier<String> value) { return this; }

        @Override
        public TemplateVariable put(String key, Object value) { return this; }

        @Override
        public TemplateVariable put(String... values) { return this; }

        @Override
        public TemplateVariable clear() { return this; }

        @Override
        public String get(String key) { return null; }
    };

    /**
     * Create new instance.
     *
     * @return for method chain
     */
    static TemplateVariable init() {
        return StringVariable.init();
    }

    /**
     * Put variable.
     *
     * @param key   variable name
     * @param value variable value
     * @return for method chain
     */
    TemplateVariable put(String key, String value);

    /**
     * Put variable.
     *
     * @param key   variable name
     * @param value variable value
     * @return for method chain
     */
    TemplateVariable put(String key, Supplier<String> value);

    /**
     * Put variable.
     *
     * @param key   variable name
     * @param value variable value
     * @return for method chain
     */
    default TemplateVariable put(String key, Object value) {
        return this.put(key, value.toString());
    }

    /**
     * <p>Put variable.</p>
     *
     * <p>Arguments should be key and value alternately.
     * If not, the extra key will be ignored.</p>
     *
     * @param values key value pair (eg: {@code ["key1", "value2", "key2", "value2"]})
     * @return for method chain
     */
    default TemplateVariable put(String... values) {
        for (int i = 1; i < values.length; i += 2) {
            this.put(values[i - 1], values[i]);
        }
        return this;
    }

    /**
     * Put variable.
     *
     * @param key   variable name
     * @param value variable value
     * @return for method chain
     */
    default TemplateVariable put(String key, int value) {
        return this.put(key, String.valueOf(value));
    }

    /**
     * Put variable.
     *
     * @param key   variable name
     * @param value variable value
     * @return for method chain
     */
    default TemplateVariable put(String key, long value) {
        return this.put(key, String.valueOf(value));
    }

    /**
     * Put variable.
     *
     * @param key   variable name
     * @param value variable value
     * @return for method chain
     */
    default TemplateVariable put(String key, double value) {
        return this.put(key, String.valueOf(value));
    }

    /**
     * Put variable.
     *
     * @param key   variable name
     * @param value variable value
     * @return for method chain
     */
    default TemplateVariable put(String key, char value) {
        return this.put(key, String.valueOf(value));
    }

    /**
     * Put variable.
     *
     * @param key   variable name
     * @param value variable value
     * @return for method chain
     */
    default TemplateVariable put(String key, boolean value) {
        return this.put(key, String.valueOf(value));
    }

    /**
     * Clear variable
     *
     * @return for method chain
     */
    TemplateVariable clear();

    /**
     * Get value
     *
     * @param key variable name
     * @return value, null if it does not exist.
     */
    String get(String key);
}
