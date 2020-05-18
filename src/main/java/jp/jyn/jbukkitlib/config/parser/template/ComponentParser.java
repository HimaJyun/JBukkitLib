package jp.jyn.jbukkitlib.config.parser.template;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;

public class ComponentParser extends AbstractParser {
    private final TextComponent[] components;
    private final Map<String, TextComponent> variables;

    public ComponentParser(TextComponent[] components, Map<String, TextComponent> variables) {
        this.components = components;
        this.variables = variables;
    }

    public TextComponent[] getComponents() {
        return components;
    }

    public Optional<TextComponent> getVariable(String name) {
        return Optional.ofNullable(variables.get(name));
    }

    public ComponentParser setVariable(String name, String text) {
        TextComponent component = variables.get(name);
        if (component != null) {
            component.setText(text);
        }
        return this;
    }

    public ComponentParser send(Player player) {
        player.spigot().sendMessage(components);
        return this;
    }

    public ComponentParser broadcast() {
        Bukkit.spigot().broadcast(components);
        return this;
    }

    public static ComponentParser parse(CharSequence sequence) {
        Queue<String> expr = exprQueue(sequence);
        List<TextComponent> components = new LinkedList<>();
        Map<String, TextComponent> variables = new HashMap<>();

        Style style = new Style();
        StringBuilder buf = new StringBuilder();
        while (!expr.isEmpty()) {
            String value = expr.remove();
            if (value.isEmpty()) {
                continue;
            }
            char c = value.charAt(0);

            if (c == '{' && value.charAt(value.length() - 1) == '}') { // variable
                if (buf.length() != 0) {
                    TextComponent text = style.apply(new TextComponent(buf.toString()));
                    components.add(text);
                    buf.setLength(0);
                }

                String name = value.substring(1, value.length() - 1);
                TextComponent variable = style.apply(new TextComponent(value));
                components.add(variable);
                variables.put(name, variable);
                continue;
            }

            if (c == '&') {
                char c2 = value.charAt(1);
                switch (c2) { // escape
                    case '{':
                    case '}':
                    case '&':
                        buf.append(c2);
                        continue;
                }

                ChatColor color = ChatColor.getByChar(c2);
                if (color != null) {
                    // &1 &2 -> ChatColor
                    if (buf.length() != 0) {
                        TextComponent text = style.apply(new TextComponent(buf.toString()));
                        components.add(text);
                        buf.setLength(0);
                    }
                    style.set(color);
                } else {
                    // &z -> &z
                    buf.append(c).append(c2);
                }
                continue;
            }

            // string
            buf.append(value);
        }
        if (buf.length() != 0) {
            TextComponent text = style.apply(new TextComponent(buf.toString()));
            components.add(text);
            buf.setLength(0);
        }

        return new ComponentParser(components.toArray(new TextComponent[0]), variables);
    }

    private static class Style {
        private boolean bold = false;
        private boolean italic = false;
        private boolean obfuscated = false;
        private boolean strikethrough = false;
        private boolean underlined = false;
        private ChatColor color = null;

        public void set(ChatColor color) {
            switch (color) {
                case BOLD:
                    bold = true;
                    break;
                case ITALIC:
                    italic = true;
                    break;
                case MAGIC:
                    obfuscated = true;
                    break;
                case STRIKETHROUGH:
                    strikethrough = true;
                    break;
                case UNDERLINE:
                    underlined = true;
                    break;
                case RESET:
                    bold = false;
                    italic = false;
                    obfuscated = false;
                    strikethrough = false;
                    underlined = false;
                    this.color = null;
                    break;
                default:
                    this.color = color;
            }
        }

        public TextComponent apply(TextComponent component) {
            if (bold) {
                component.setBold(true);
            }
            if (italic) {
                component.setItalic(true);
            }
            if (obfuscated) {
                component.setObfuscated(true);
            }
            if (strikethrough) {
                component.setStrikethrough(true);
            }
            if (underlined) {
                component.setUnderlined(true);
            }
            if (color != null) {
                component.setColor(color.asBungee());
            }
            return component;
        }
    }
}
