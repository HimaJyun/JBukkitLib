package jp.jyn.jbukkitlib.config.parser.template;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ComponentParserTest {
    ComponentParser parser;

    @Test
    public void stringTest1() {
        parser = ComponentParser.parse("raw string");
        TextComponent[] c = parser.getComponents();
        assertEquals(c.length, 1);
        assertEquals(c[0].getText(), "raw string");
    }

    @Test
    public void stringTest2() {
        parser = ComponentParser.parse("raw &0string&r\\");
        TextComponent[] c = parser.getComponents();
        assertEquals(c.length, 3);
        assertEquals(c[0].getText(), "raw ");

        assertEquals(c[1].getText(), "string");
        assertEquals(c[1].getColorRaw(), ChatColor.BLACK);

        assertEquals(c[2].getText(), "\\");
    }

    @Test
    public void stringTest3() {
        parser = ComponentParser.parse("aaa&l&kzzz&addd&rzzz");
        TextComponent[] c = parser.getComponents();
        assertEquals(c.length, 4);
        assertEquals(c[0].getText(), "aaa");

        assertEquals(c[1].getText(), "zzz");
        assertTrue(c[1].isObfuscatedRaw());
        assertTrue(c[1].isBoldRaw());

        assertEquals(c[2].getText(), "ddd");
        assertEquals(c[2].getColorRaw(), ChatColor.GREEN);

        assertEquals(c[3].getText(), "zzz");
        assertNull(c[3].isObfuscatedRaw());
        assertNull(c[3].getColorRaw());
    }

    @Test
    public void colorCodeTest() {
        parser = ComponentParser.parse("&z&0&a&r&");
        TextComponent[] c = parser.getComponents();
        assertEquals(c.length, 2);

        assertEquals(c[0].getText(), "&z");

        assertEquals(c[1].getText(), "&");
        assertNull(c[1].getColorRaw());
    }

    @Test
    public void variableTest1() {
        parser = ComponentParser.parse("{test} variable");
        TextComponent[] c = parser.getComponents();
        assertEquals(c.length, 2);

        assertEquals(c[0].getText(), "{test}");
        parser.setVariable("test", "aaa");
        assertEquals(c[0].getText(), "aaa");

        assertEquals(c[1].getText(), " variable");
    }

    @Test
    public void variableTest2() {
        parser = ComponentParser.parse("variable {test}");
        TextComponent[] c = parser.getComponents();
        assertEquals(c.length, 2);

        assertEquals(c[0].getText(), "variable ");

        assertEquals(c[1].getText(), "{test}");
        parser.setVariable("test", "aaa");
        assertEquals(c[1].getText(), "aaa");
    }

    @Test
    public void escapeTest() {
        // && &{ &&0 &z & &
        parser = ComponentParser.parse("&& &{ &&0 &z & &");
        TextComponent[] c = parser.getComponents();
        assertEquals(c.length, 1);
        assertEquals(c[0].getText(), "& { &0 &z & &");
    }
}
