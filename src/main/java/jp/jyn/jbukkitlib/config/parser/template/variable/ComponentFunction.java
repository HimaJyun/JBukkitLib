package jp.jyn.jbukkitlib.config.parser.template.variable;

import net.md_5.bungee.api.chat.TextComponent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Component function
 */
public class ComponentFunction {
    private final Map<String, BiConsumer<TextComponent, String[]>> function = new HashMap<>();

    /**
     * Create new instance.
     *
     * @return for method chain.
     */
    public static ComponentFunction init() {
        return new ComponentFunction();
    }

    /**
     * Add function
     *
     * @param key   function name
     * @param value function consumer
     * @return for method chain
     */
    public ComponentFunction put(String key, BiConsumer<TextComponent, String[]> value) {
        function.put(key, value);
        return this;
    }

    /**
     * Clear function
     *
     * @return for method chain
     */
    public ComponentFunction clear() {
        function.clear();
        return this;
    }

    /**
     * get value
     *
     * @param key function name
     * @return value, Null if it does not exist.
     */
    public BiConsumer<TextComponent, String[]> get(String key) {
        return function.get(key);
    }
}
