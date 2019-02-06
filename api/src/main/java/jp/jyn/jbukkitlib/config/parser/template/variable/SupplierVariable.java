package jp.jyn.jbukkitlib.config.parser.template.variable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class SupplierVariable implements TemplateVariable {
    private final Map<String, Supplier<String>> variable = new HashMap<>();

    public static SupplierVariable init() {
        return new SupplierVariable();
    }

    @Override
    public SupplierVariable put(String key, String value) {
        return this.put(key, () -> value);
    }

    @Override
    public SupplierVariable put(String key, Supplier<String> value) {
        variable.put(key, value);
        return this;
    }

    @Override
    public SupplierVariable put(String key, Object value) {
        return this.put(key, value::toString);
    }

    @Override
    public SupplierVariable clear() {
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
}
