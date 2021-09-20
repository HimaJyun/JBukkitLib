package jp.jyn.jbukkitlib.config.parser.component;

import net.md_5.bungee.api.chat.TextComponent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * variable for {@link ComponentParser}. (non Thread-Safe)
 */
public class ComponentVariable {
    /**
     * "Do nothing" variable, always empty.
     */
    public final static ComponentVariable EMPTY_VARIABLE = new ComponentVariable() {
        // 何もしないように細工しておく
        @Override
        public ComponentVariable put(String key, Consumer<TextComponent> value) {return this;}

        @Override
        public ComponentVariable put(String key, BiConsumer<TextComponent, List<String>> value) {return this;}

        @Override
        public ComponentVariable put(String... values) {return this;}

        @Override
        public ComponentVariable clearVariable() {return this;}

        @Override
        public ComponentVariable clearFunction() {return this;}

        @Override
        public Consumer<TextComponent> getVariable(String key) {return null;}

        @Override
        public BiConsumer<TextComponent, List<String>> getFunction(String key) {return null;}

        @Override
        public String toString() {
            return "ComponentVariable.Empty{}";
        }

        @Override
        public boolean equals(Object o) {
            return this == o;
        }

        @Override
        public int hashCode() {
            return 0;
        }
    };

    private Map<String, Consumer<TextComponent>> variable = null;
    private Map<String, BiConsumer<TextComponent, List<String>>> function = null;

    /**
     * Create new instance.
     *
     * @return for method chain
     */
    public static ComponentVariable init() {
        return new ComponentVariable();
    }

    /**
     * Put variable.
     *
     * @param key   variable name
     * @param value variable consumer
     * @return for method chain
     */
    public ComponentVariable put(String key, Consumer<TextComponent> value) {
        // 遅延初期化
        (variable == null ? (variable = new HashMap<>()) : variable).put(key, value);
        return this;
    }

    /**
     * Put function.
     *
     * @param key   function name
     * @param value function consumer
     * @return for method chain
     */
    public ComponentVariable put(String key, BiConsumer<TextComponent, List<String>> value) {
        // 遅延初期化
        (function == null ? (function = new HashMap<>()) : function).put(key, value);
        return this;
    }

    /**
     * Clear variable.
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
     * Clear function.
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
        clearVariable();
        clearFunction();
        return this;
    }

    /**
     * Get variable.
     *
     * @param key variable name
     * @return value, null if it does not exist.
     */
    public Consumer<TextComponent> getVariable(String key) {
        return variable == null ? null : variable.get(key);
    }

    /**
     * Get function.
     *
     * @param key function name
     * @return value, null if it does not exist.
     */
    public BiConsumer<TextComponent, List<String>> getFunction(String key) {
        return function == null ? null : function.get(key);
    }

    // region put variation

    /**
     * Put variable.
     *
     * @param key   variable name
     * @param value variable value
     * @return for method chain
     */
    public ComponentVariable put(String key, String value) {
        return put(key, c -> c.setText(value));
    }

    /**
     * Put variable.
     *
     * @param key   variable name
     * @param value variable value
     * @return for method chain
     */
    public ComponentVariable put(String key, Supplier<String> value) {
        return put(key, c -> c.setText(value.get()));
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
    public ComponentVariable put(String key, Object value) {
        return put(key, c -> c.setText(value.toString()));
    }

    /**
     * Put variable.
     *
     * @param key   variable name
     * @param value variable value
     * @return for method chain
     */
    public ComponentVariable put(String key, byte value) {
        return put(key, c -> c.setText(String.valueOf(value)));
    }

    /**
     * Put variable.
     *
     * @param key   variable name
     * @param value variable value
     * @return for method chain
     */
    public ComponentVariable put(String key, short value) {
        return put(key, c -> c.setText(String.valueOf(value)));
    }

    /**
     * Put variable.
     *
     * @param key   variable name
     * @param value variable value
     * @return for method chain
     */
    public ComponentVariable put(String key, int value) {
        return put(key, c -> c.setText(String.valueOf(value)));
    }

    /**
     * Put variable.
     *
     * @param key   variable name
     * @param value variable value
     * @return for method chain
     */
    public ComponentVariable put(String key, long value) {
        return put(key, c -> c.setText(String.valueOf(value)));
    }

    /**
     * Put variable.
     *
     * @param key   variable name
     * @param value variable value
     * @return for method chain
     */
    public ComponentVariable put(String key, float value) {
        return put(key, c -> c.setText(String.valueOf(value)));
    }

    /**
     * Put variable.
     *
     * @param key   variable name
     * @param value variable value
     * @return for method chain
     */
    public ComponentVariable put(String key, double value) {
        return put(key, c -> c.setText(String.valueOf(value)));
    }

    /**
     * Put variable.
     *
     * @param key   variable name
     * @param value variable value
     * @return for method chain
     */
    public ComponentVariable put(String key, char value) {
        return put(key, c -> c.setText(String.valueOf(value)));
    }

    /**
     * Put variable.
     *
     * @param key   variable name
     * @param value variable value
     * @return for method chain
     */
    public ComponentVariable put(String key, boolean value) {
        return put(key, c -> c.setText(String.valueOf(value)));
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
    public ComponentVariable put(String... values) {
        Map<String, Consumer<TextComponent>> v = (variable == null ? (variable = new HashMap<>()) : variable);
        for (int i = 1; i < values.length; i += 2) {
            final String value = values[i];
            v.put(values[i - 1], c -> c.setText(value));
        }
        return this;
    }
    // endregion

    @Override
    public String toString() {
        return "ComponentVariable{" +
            "variable=" + variable +
            ", function=" + function +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ComponentVariable that = (ComponentVariable) o;
        return Objects.equals(variable, that.variable) &&
            Objects.equals(function, that.function);
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = result * 31 + Objects.hashCode(variable);
        result = result * 31 + Objects.hashCode(function);
        return result;
    }
}
