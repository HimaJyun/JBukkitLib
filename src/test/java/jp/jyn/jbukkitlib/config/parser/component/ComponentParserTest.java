package jp.jyn.jbukkitlib.config.parser.component;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.ForkJoinPool;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ComponentParserTest {
    ComponentParser parser;

    @Test
    public void rawStringTest1() {
        parser = ComponentParser.parse("raw string");
        assertEquals(parser.getClass().getName(), "jp.jyn.jbukkitlib.config.parser.component.Node$Raw");
        assertEquals(parser.apply().toPlaintext(), "raw string");
    }

    @Test
    public void rawStringTest2() {
        parser = ComponentParser.parse("raw &0string&r&");
        assertEquals(parser.getClass().getName(), "jp.jyn.jbukkitlib.config.parser.component.Node$Raw");
        assertEquals(parser.apply().toPlaintext(), "raw string&");
    }

    @Test
    public void stringTest1() {
        parser = ComponentParser.parse("raw string");
        TextComponent[] c = parser.apply().toTextComponent();
        assertEquals(c.length, 1);
        assertEquals(c[0].getText(), "raw string");
    }

    @Test
    public void stringTest2() {
        parser = ComponentParser.parse("raw &0string&r&");
        BaseComponent[] b = legacy("raw &0string&r&");

        TextComponent[] c = parser.apply().toTextComponent();
        assertEquals(c.length, b.length);
        assertArrayEquals(c, b);

        assertEquals(c.length, 3);
        assertEquals(c[0].getText(), "raw ");

        assertEquals(c[1].getText(), "string");
        assertEquals(c[1].getColorRaw(), ChatColor.BLACK);

        assertEquals(c[2].getText(), "&");
    }

    @Test
    public void stringTest3() {
        parser = ComponentParser.parse("aaa&l&kzzz&addd&rzzz");

        TextComponent[] c = parser.apply().toTextComponent();
        BaseComponent[] b = legacy("aaa&l&kzzz&addd&rzzz");

        assertArrayEquals(c, b);
    }

    @Test
    public void colorCodeTest() {
        parser = ComponentParser.parse("&z&0&a&r&");
        TextComponent[] c = parser.apply().toTextComponent();
        BaseComponent[] b = legacy("&z&0&a&r&");

        assertArrayEquals(c, b);
    }

    @Test
    public void variableTest1() {
        parser = ComponentParser.parse("{test} variable");
        TextComponent[] c = parser.apply("test", co -> co.setText("aaa")).toTextComponent();
        assertEquals(c.length, 2);
        assertEquals(c[0].getText(), "aaa");

        TextComponent[] b = parser.apply().toTextComponent();
        assertEquals(b[0].getText(), "{test}");

        assertEquals(c[1].getText(), " variable");
    }

    @Test
    public void variableTest2() {
        parser = ComponentParser.parse("variable {test}");
        TextComponent[] c = parser.apply("test", co -> co.setText("aaa")).toTextComponent();
        assertEquals(c.length, 2);

        assertEquals(c[0].getText(), "variable ");
        assertEquals(c[1].getText(), "aaa");

        TextComponent[] b = parser.apply().toTextComponent();
        assertEquals(b[1].getText(), "{test}");
    }

    @Test
    public void variableTest3() {
        parser = ComponentParser.parse("{test}");
        TextComponent[] c = parser.apply("test", co -> co.setText("aaa")).toTextComponent();
        assertEquals(c.length, 1);

        assertEquals(c[0].getText(), "aaa");

        TextComponent[] b = parser.apply().toTextComponent();
        assertEquals(b[0].getText(), "{test}");
    }

    @Test
    public void variableTest4() {
        parser = ComponentParser.parse("{a}{b}{c}");
        TextComponent[] c = parser.apply("a", "aaa", "b", "bbb", "c").toTextComponent();
        assertEquals(c.length, 3);

        assertEquals(c[0].getText(), "aaa");

        assertEquals(c[1].getText(), "bbb");

        assertEquals(c[2].getText(), "{c}");
    }

    @Test
    public void escapeTest() {
        // && &{ &&0 &z & &
        parser = ComponentParser.parse("&& &{ &&0 &z & &");
        TextComponent[] c = parser.apply().toTextComponent();
        assertEquals(c.length, 1);
        assertEquals(c[0].getText(), "& { &0 &z & &");
    }

    @Test
    public void urlTest() {
        parser = ComponentParser.parse("https://example.com/ aaa");
        TextComponent[] c = parser.apply().toTextComponent();
        BaseComponent[] b = legacy("https://example.com/ aaa");

        assertArrayEquals(c, b);
    }

    @Test
    public void functionTest() {
        String[] ary = new String[]{"a", "b", "c", "d"};
        parser = ComponentParser.parse("{function(\",\")}");

        TextComponent[] c = parser.apply("function", (co, a) -> co.setText(String.join(a.get(0), ary))).toTextComponent();
        assertEquals(c.length, 1);

        assertEquals(c[0].getText(), String.join(",", ary));
    }

    @Test
    public void threadTest() {
        final int NUM_THREAD = 128;
        parser = ComponentParser.parse("{test}");

        for (int i = 0; i < NUM_THREAD; i++) {
            ForkJoinPool.commonPool().execute(() -> {
                String test = UUID.randomUUID().toString();
                TextComponent[] c = parser.apply("test", co -> co.setText(test)).toTextComponent();
                assertEquals(c[0].getText(), test);
            });
        }
    }

    private BaseComponent[] legacy(String str) {
        return TextComponent.fromLegacyText(org.bukkit.ChatColor.translateAlternateColorCodes('&', str));
    }
}
