package jp.jyn.jbukkitlib.config.parser.template.variable;

import java.util.function.Supplier;

public class EmptyVariable implements TemplateVariable {
    private final static EmptyVariable instance = new EmptyVariable();

    public static EmptyVariable getInstance() { return instance; }

    private EmptyVariable() {}

    @Override
    public EmptyVariable put(String key, String value) {
        return this;
    }

    @Override
    public EmptyVariable put(String key, Supplier<String> value) {
        return this;
    }

    @Override
    public EmptyVariable put(String key, Object value) {
        return this;
    }

    @Override
    public EmptyVariable put(String key, int value) {
        return this;
    }

    @Override
    public EmptyVariable put(String key, long value) {
        return this;
    }

    @Override
    public EmptyVariable put(String key, float value) {
        return this;
    }

    @Override
    public EmptyVariable put(String key, double value) {
        return this;
    }

    @Override
    public EmptyVariable put(String key, boolean value) {
        return this;
    }

    @Override
    public EmptyVariable clear() {
        return this;
    }

    @Override
    public String get(String key) {
        return null;
    }
}
