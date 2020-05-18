package jp.jyn.jbukkitlib.config.parser.template.variable;

import java.util.function.Supplier;

/**
 * Variable used by TemplateParser
 */
public interface TemplateVariable {
    /**
     * Add variable
     *
     * @param key   variable name
     * @param value variable value
     * @return for method chain
     */
    TemplateVariable put(String key, String value);

    /**
     * Add variable
     *
     * @param key   variable name
     * @param value variable value
     * @return for method chain
     */
    TemplateVariable put(String key, Supplier<String> value);

    /**
     * Add variable
     *
     * @param key   variable name
     * @param value variable value
     * @return for method chain
     */
    default TemplateVariable put(String key, Object value) {
        return this.put(key, value.toString());
    }

    /**
     * Add variable
     *
     * @param key   variable name
     * @param value variable value
     * @return for method chain
     */
    default TemplateVariable put(String key, int value) {
        return this.put(key, String.valueOf(value));
    }

    /**
     * Add variable
     *
     * @param key   variable name
     * @param value variable value
     * @return for method chain
     */
    default TemplateVariable put(String key, long value) {
        return this.put(key, String.valueOf(value));
    }

    /**
     * Add variable
     *
     * @param key   variable name
     * @param value variable value
     * @return for method chain
     */
    default TemplateVariable put(String key, float value) {
        return this.put(key, String.valueOf(value));
    }

    /**
     * Add variable
     *
     * @param key   variable name
     * @param value variable value
     * @return for method chain
     */
    default TemplateVariable put(String key, double value) {
        return this.put(key, String.valueOf(value));
    }

    /**
     * Add variable
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
     * get value
     *
     * @param key variable name
     * @return value, Null if it does not exist.
     */
    String get(String key);
}