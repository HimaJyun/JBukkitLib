package jp.jyn.jbukkitlib.config.parser.template.variable;

import net.md_5.bungee.api.chat.TextComponent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Component variable
 */
public class ComponentVariable {
    private Map<String, BiConsumer<TextComponent, String[]>> function;
    private Map<String, Consumer<TextComponent>> variable;

    private Map<String, Consumer<TextComponent>> v() { // 遅延初期化
        return variable == null ? (variable = new HashMap<>()) : variable;
    }

    private Map<String, BiConsumer<TextComponent, String[]>> f() { // 遅延初期化
        return function == null ? (function = new HashMap<>()) : function;
    }

    /**
     * Create new instance.
     *
     * @return for method chain.
     */
    public static ComponentVariable init() {
        return new ComponentVariable();
    }

    /**
     * Put variable
     *
     * @param key   variable name
     * @param value variable value
     * @return for method chain
     */
    public ComponentVariable variable(String key, String value) {
        v().put(key, c -> c.setText(value));
        return this;
    }

    /**
     * Put variable
     *
     * @param key   variable name
     * @param value variable value
     * @return for method chain
     */
    public ComponentVariable variable(String key, Supplier<String> value) {
        v().put(key, c -> c.setText(value.get()));
        return this;
    }

    /**
     * Put variable
     *
     * @param key   variable name
     * @param value variable consumer
     * @return for method chain
     */
    public ComponentVariable variable(String key, Consumer<TextComponent> value) {
        v().put(key, value);
        return this;
    }

    /**
     * Put function
     *
     * @param key   function name
     * @param value function consumer
     * @return for method chain
     */
    public ComponentVariable function(String key, BiConsumer<TextComponent, String[]> value) {
        f().put(key, value);
        return this;
    }

    /**
     * Alias of {@link ComponentVariable#variable(String, String)}
     *
     * @param key   variable name
     * @param value variable value
     * @return for method chain
     */
    public ComponentVariable put(String key, String value) {
        return variable(key, value);
    }

    /**
     * Alias of {@link ComponentVariable#variable(String, Supplier)}
     *
     * @param key   variable name
     * @param value variable value
     * @return for method chain
     */
    public ComponentVariable put(String key, Supplier<String> value) {
        return variable(key, value);
    }

    /**
     * Alias of {@link ComponentVariable#variable(String, Consumer)}
     *
     * @param key   variable name
     * @param value variable consumer
     * @return for method chain
     */
    public ComponentVariable put(String key, Consumer<TextComponent> value) {
        return variable(key, value);
    }

    /**
     * Alias of {@link ComponentVariable#function(String, BiConsumer)}
     *
     * @param key   function name
     * @param value function consumer
     * @return for method chain
     */
    public ComponentVariable put(String key, BiConsumer<TextComponent, String[]> value) {
        return function(key, value);
    }

    /**
     * Clear variable
     *
     * @return for method chain
     */
    public ComponentVariable clearVariable() {
        if (variable != null) {
            variable.clear();
        }
        return this;
    }

    /**
     * Clear function
     *
     * @return for method chain
     */
    public ComponentVariable clearFunction() {
        if (function != null) {
            function.clear();
        }
        return this;
    }

    /**
     * Clear variable and function.
     *
     * @return for method chain
     */
    public ComponentVariable clear() {
        if (variable != null) {
            variable.clear();
        }
        if (function != null) {
            function.clear();
        }
        return this;
    }

    /**
     * get variable
     *
     * @param key variable name
     * @return value, null if it does not exist.
     */
    public Consumer<TextComponent> getVariable(String key) {
        return variable == null ? null : variable.get(key);
    }

    /**
     * get function
     *
     * @param key function name
     * @return value, null if it does not exist.
     */
    public BiConsumer<TextComponent, String[]> getFunction(String key) {
        return function == null ? null : function.get(key);
    }
}
