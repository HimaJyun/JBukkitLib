package jp.jyn.jbukkitlib.config.parser.template;

import org.bukkit.ChatColor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StringParserTest {
    private TemplateParser parser;

    @Test
    public void rawStringTest1() {
        parser = TemplateParser.parse("raw string");
        assertEquals(parser.getClass().getName(),"jp.jyn.jbukkitlib.config.parser.template.Node$RawParser");
        assertEquals(parser.apply(), "raw string");
    }

    @Test
    public void rawStringTest2() {
        parser = TemplateParser.parse("raw &0string&r&");
        assertEquals(parser.getClass().getName(),"jp.jyn.jbukkitlib.config.parser.template.Node$RawParser");
        assertEquals(parser.apply(), "raw " + ChatColor.BLACK + "string" + ChatColor.RESET + "&");
    }

    @Test
    public void urlTest1() {
        parser = TemplateParser.parse("aaa https://example.com/ aaa");
        assertEquals(parser.apply(), "aaa https://example.com/ aaa");
    }

    @Test
    public void urlTest2() {
        parser = TemplateParser.parse("https://example.com/ aaa");
        assertEquals(parser.apply(), "https://example.com/ aaa");
    }

    @Test
    public void urlTest3() {
        parser = TemplateParser.parse("aaa https://example.com/");
        assertEquals(parser.apply(), "aaa https://example.com/");
    }

    @Test
    public void colorCodeTest1() {
        parser = TemplateParser.parse("&z&0&a&r&");
        assertEquals(parser.apply(), "&z" + ChatColor.BLACK + ChatColor.GREEN + ChatColor.RESET + "&");
    }

    @Test
    public void colorCodeTest2() {
        parser = TemplateParser.parse("aaa&0&a&r");
        assertEquals(parser.apply(), "aaa" + ChatColor.BLACK + ChatColor.GREEN + ChatColor.RESET);
    }

    @Test
    public void colorCodeTest3() {
        parser = TemplateParser.parse("A &A A");
        assertEquals(parser.apply(), "A " + ChatColor.GREEN + " A");
    }

    @Test
    public void hexColorTest1() {
        parser = TemplateParser.parse("#123abc");
        assertEquals(parser.apply(), "\u00A7x\u00A71\u00A72\u00A73\u00A7a\u00A7b\u00A7c");
    }

    @Test
    public void hexColorTest2() {
        parser = TemplateParser.parse("#123");
        assertEquals(parser.apply(), "\u00A7x\u00A71\u00A71\u00A72\u00A72\u00A73\u00A73");
    }

    @Test
    public void hexColorTest3() {
        parser = TemplateParser.parse("#gggggg");
        assertEquals(parser.apply(), "#gggggg");
    }

    @Test
    public void hexColorTest4() {
        parser = TemplateParser.parse("#12345");
        assertEquals(parser.apply(), "\u00A7x\u00A71\u00A71\u00A72\u00A72\u00A73\u00A7345");
    }

    @Test
    public void hexColorTest5() {
        parser = TemplateParser.parse("#ccczzz");
        assertEquals(parser.apply(), "\u00A7x\u00A7c\u00A7c\u00A7c\u00A7c\u00A7c\u00A7czzz");
    }

    @Test
    public void hexColorTest6() {
        parser = TemplateParser.parse("zzz#ccc");
        assertEquals(parser.apply(), "zzz\u00A7x\u00A7c\u00A7c\u00A7c\u00A7c\u00A7c\u00A7c");
    }

    @Test
    public void hexColorTest7() {
        parser = TemplateParser.parse("abc#ddd#012345#0123#01234#fffz123");
        assertEquals(parser.apply(), "abc" +
            "\u00A7x\u00A7d\u00A7d\u00A7d\u00A7d\u00A7d\u00A7d" +
            "\u00A7x\u00A70\u00A71\u00A72\u00A73\u00A74\u00A75" +
            "\u00A7x\u00A70\u00A70\u00A71\u00A71\u00A72\u00A723" +
            "\u00A7x\u00A70\u00A70\u00A71\u00A71\u00A72\u00A7234" +
            "\u00A7x\u00A7f\u00A7f\u00A7f\u00A7f\u00A7f\u00A7f" +
            "z123");
    }

    @Test
    public void hexColorTest8() {
        parser = TemplateParser.parse("AAA #AAA AAA");
        assertEquals(parser.apply(), "AAA \u00A7x\u00A7a\u00A7a\u00A7a\u00A7a\u00A7a\u00A7a AAA");
    }

    @Test
    public void variableTest1() {
        parser = TemplateParser.parse("{test} variable");
        assertEquals(
            parser.apply(StringVariable.init().put("test", "aaa")),
            "aaa variable"
        );
    }

    @Test
    public void variableTest2() {
        parser = TemplateParser.parse("variable {test}");
        assertEquals(
            parser.apply(StringVariable.init().put("test", "aaa")),
            "variable aaa"
        );
    }

    @Test
    public void variableTest3() {
        parser = TemplateParser.parse("variable {test}");
        assertEquals(
            parser.apply(StringVariable.init().put("test", 1)),
            "variable 1"
        );
    }

    @Test
    public void variableTest4() {
        parser = TemplateParser.parse("variable { test }");
        assertEquals(
            parser.apply(StringVariable.init().put("test", 1)),
            "variable 1"
        );
    }

    @Test
    public void variableTest5() {
        parser = TemplateParser.parse("variable { t e s t }");
        assertEquals(
            parser.apply(StringVariable.init().put("t e s t", 1)),
            "variable 1"
        );
    }

    @Test
    public void variableTest6() {
        parser = TemplateParser.parse("{a}{b}{c}");
        assertEquals(
            parser.apply(StringVariable.init().put("a", "aaa", "b", "bbb","c")),
            "aaabbb{c}"
        );
    }

    @Test
    public void escapeTest() {
        // \\ \{ \&0 \z \ \
        parser = TemplateParser.parse("&& &{ &&0 &z & &");
        assertEquals(parser.apply(), "& { &0 &z & &");
    }

    @Test
    public void notFoundVariableTest() {
        parser = TemplateParser.parse("variable {test}");
        assertEquals(parser.apply(), "variable {test}");
    }

    @Test
    public void brokenTest1() {
        parser = TemplateParser.parse("variable {broken");
        System.out.println(parser.apply());
    }

    @Test
    public void brokenTest2() {
        parser = TemplateParser.parse("variable broken}");
        System.out.println(parser.apply());
    }

    @Test
    public void test1() {
        parser = TemplateParser.parse("test &#aaa");
        assertEquals(parser.apply(), "test #aaa");
    }

    @Test
    public void test2() {
        parser = TemplateParser.parse("test {variable}#aaa");
        assertEquals(parser.apply(), "test {variable}\u00A7x\u00A7a\u00A7a\u00A7a\u00A7a\u00A7a\u00A7a");
    }

    @Test
    public void test3() {
        parser = TemplateParser.parse("test &&#aaa");
        assertEquals(parser.apply(), "test &\u00A7x\u00A7a\u00A7a\u00A7a\u00A7a\u00A7a\u00A7a");
    }
}
