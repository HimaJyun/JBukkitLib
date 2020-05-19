package jp.jyn.jbukkitlib.config.parser.template;

import jp.jyn.jbukkitlib.config.parser.template.variable.TemplateVariable;

import java.util.function.Supplier;

public class RawStringParser implements TemplateParser {
    private final String string;

    public RawStringParser(CharSequence string) {
        this.string = string.toString();
    }

    @Override
    public String toString(TemplateVariable variable) {
        return string;
    }

    @Override
    public String toString(String key, String value) {
        return string;
    }

    @Override
    public String toString(String key, Object value) {
        return string;
    }

    @Override
    public String toString(String key, Supplier<String> value) {
        return string;
    }

    @Override
    public String toString(String key, int value) {
        return string;
    }

    @Override
    public String toString(String key, long value) {
        return string;
    }

    @Override
    public String toString(String key, float value) {
        return string;
    }

    @Override
    public String toString(String key, double value) {
        return string;
    }

    @Override
    public String toString(String key, boolean value) {
        return string;
    }

    @Override
    public String toString() {
        return string;
    }
}
