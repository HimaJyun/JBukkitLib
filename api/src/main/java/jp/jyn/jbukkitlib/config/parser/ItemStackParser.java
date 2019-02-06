package jp.jyn.jbukkitlib.config.parser;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ItemStackParser {

    public static ItemStack parse(CharSequence value) {
        Material material = null;
        int amount = 1;
        Map<Enchantment, Integer> enchantments = Collections.emptyMap();

        boolean bracket = false;
        boolean asterisk = false;
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (bracket) {
                if (c == ']') {
                    enchantments = parseEnchantments(buf.toString());
                    buf.setLength(0);
                    bracket = false;
                } else {
                    buf.append(c);
                }
                continue;
            }

            switch (c) {
                case '[':
                    if (buf.length() != 0) {
                        material = Material.getMaterial(buf.toString().toUpperCase(Locale.ENGLISH));
                        buf.setLength(0);
                    }
                    bracket = true;
                    break;
                case '*':
                    if (buf.length() != 0) {
                        material = Material.getMaterial(buf.toString().toUpperCase(Locale.ENGLISH));
                        buf.setLength(0);
                    }
                    asterisk = true;
                    break;
                default:
                    buf.append(c);
                    break;
            }
        }
        if (buf.length() != 0) {
            if (asterisk) {
                amount = Integer.parseInt(buf.toString());
            } else {
                material = Material.getMaterial(buf.toString().toUpperCase(Locale.ENGLISH));
            }
        }

        if (material == null) {
            throw new IllegalArgumentException("Invalid item: " + value.toString());
        }

        ItemStack itemStack = new ItemStack(material, amount);
        // add enchant
        if (material == Material.ENCHANTED_BOOK) {
            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) itemStack.getItemMeta();
            for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                meta.addStoredEnchant(entry.getKey(), entry.getValue(), true);
            }
            itemStack.setItemMeta(meta);
        } else {
            itemStack.addUnsafeEnchantments(enchantments);
        }
        return itemStack;
    }

    public static String toString(ItemStack itemStack) {
        StringBuilder builder = new StringBuilder();

        // Material
        builder.append(itemStack.getType().name());

        // Material[Enchant@level]
        Map<Enchantment, Integer> enchantments;
        if (itemStack.getType() == Material.ENCHANTED_BOOK) {
            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) itemStack.getItemMeta();
            enchantments = meta.getStoredEnchants();
        } else {
            enchantments = itemStack.getEnchantments();
        }

        if (!enchantments.isEmpty()) {
            toString(builder.append('['), enchantments).append(']');
        }

        // Material[Enchant@level]*amount
        if (itemStack.getAmount() > 1) {
            builder.append('*').append(itemStack.getAmount());
        }
        return builder.toString();
    }

    public static Map<Enchantment, Integer> parseEnchantments(String value) {
        Map<Enchantment, Integer> result = new HashMap<>();
        for (String s : value.split(",")) {
            Map.Entry<Enchantment, Integer> entry = parseEnchantment(s);
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    public static String toString(Map<Enchantment, Integer> enchantments) {
        return toString(new StringBuilder(), enchantments).toString();
    }

    private static StringBuilder toString(StringBuilder builder, Map<Enchantment, Integer> enchantments) {
        if (enchantments.isEmpty()) {
            return builder;
        }

        boolean first = true;
        for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
            if (first) {
                first = false;
            } else {
                builder.append(',');
            }
            // Enchant
            //noinspection ResultOfMethodCallIgnored
            toString(builder, entry.getKey(), entry.getValue());
        }
        return builder;
    }

    public static Map.Entry<Enchantment, Integer> parseEnchantment(String value) {
        Enchantment enchantment;
        Integer level = 1;

        String[] tmp = value.split("@");
        if (tmp.length == 2) {
            level = Integer.valueOf(tmp[1]);
        }

        enchantment = Enchantment.getByKey(NamespacedKey.minecraft(tmp[0].toLowerCase(Locale.ENGLISH)));
        if (enchantment == null) {
            throw new IllegalArgumentException("Invalid enchantment: " + value);
        }

        return new AbstractMap.SimpleEntry<>(enchantment, level);
    }

    public static String toString(Enchantment enchantment, int level) {
        return toString(new StringBuilder(), enchantment, level).toString();
    }

    private static StringBuilder toString(StringBuilder builder, Enchantment enchantment, int level) {
        builder.append(enchantment.getKey().getKey());
        if (level > 1) {
            builder.append('@').append(level);
        }
        return builder;
    }
}
