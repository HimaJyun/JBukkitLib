package jp.jyn.jbukkitlib.config.parser.template.variable;

import net.md_5.bungee.api.chat.TextComponent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Component variable
 */
public class ComponentVariable {
    private final Map<String, Consumer<TextComponent>> variable = new HashMap<>();

    /**
     * Create new instance.
     *
     * @return for method chain.
     */
    public static ComponentVariable init() {
        return new ComponentVariable();
    }

    /**
     * Add variable
     *
     * @param key   variable name
     * @param value variable consumer
     * @return for method chain
     */
    public ComponentVariable put(String key, Consumer<TextComponent> value) {
        variable.put(key, value);
        return this;
    }

    /**
     * Clear variable
     *
     * @return for method chain
     */
    public ComponentVariable clear() {
        variable.clear();
        return this;
    }

    /**
     * get value
     *
     * @param key variable name
     * @return value, Null if it does not exist.
     */
    public Consumer<TextComponent> get(String key) {
        return variable.get(key);
    }
}
