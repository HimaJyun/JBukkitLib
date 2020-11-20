package jp.jyn.jbukkitlib.config.parser.template;

import jp.jyn.jbukkitlib.config.parser.template.variable.StringVariable;
import org.bukkit.ChatColor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StringParserTest {
    private TemplateParser parser;

    @Test
    public void rawStringTest1() {
        parser = StringParser.parse("raw string");
        assertTrue(parser instanceof RawStringParser);
        assertEquals(parser.toString(), "raw string");
    }

    @Test
    public void rawStringTest2() {
        parser = StringParser.parse("raw &0string&r\\");
        assertTrue(parser instanceof RawStringParser);
        assertEquals(parser.toString(), "raw " + ChatColor.BLACK + "string" + ChatColor.RESET + "\\");
    }

    @Test
    public void urlTest1() {
        parser = StringParser.parse("aaa https://example.com/ aaa");
        assertEquals(parser.toString(),"aaa https://example.com/ aaa");
    }

    @Test
    public void urlTest2() {
        parser = StringParser.parse("https://example.com/ aaa");
        assertEquals(parser.toString(),"https://example.com/ aaa");
    }

    @Test
    public void urlTest3() {
        parser = StringParser.parse("aaa https://example.com/");
        assertEquals(parser.toString(),"aaa https://example.com/");
    }

    @Test
    public void colorCodeTest1() {
        parser = StringParser.parse("&z&0&a&r&");
        assertEquals(parser.toString(), "&z" + ChatColor.BLACK + ChatColor.GREEN + ChatColor.RESET + "&");
    }

    @Test
    public void colorCodeTest2() {
        parser = StringParser.parse("aaa&0&a&r");
        assertEquals(parser.toString(), "aaa"+ChatColor.BLACK + ChatColor.GREEN + ChatColor.RESET);
    }

    @Test
    public void colorCodeTest3() {
        parser = StringParser.parse("A &A A");
        assertEquals(parser.toString(), "A "+ChatColor.GREEN +" A");
    }

    @Test
    public void hexColorTest1() {
        parser = StringParser.parse("#123abc");
        assertEquals(parser.toString(),"\u00A7x\u00A71\u00A72\u00A73\u00A7a\u00A7b\u00A7c");
    }

    @Test
    public void hexColorTest2() {
        parser = StringParser.parse("#123");
        assertEquals(parser.toString(),"\u00A7x\u00A71\u00A71\u00A72\u00A72\u00A73\u00A73");
    }

    @Test
    public void hexColorTest3() {
        parser = StringParser.parse("#gggggg");
        assertEquals(parser.toString(),"#gggggg");
    }

    @Test
    public void hexColorTest4() {
        parser = StringParser.parse("#12345");
        assertEquals(parser.toString(), "\u00A7x\u00A71\u00A71\u00A72\u00A72\u00A73\u00A7345");
    }

    @Test
    public void hexColorTest5() {
        parser = StringParser.parse("#ccczzz");
        assertEquals(parser.toString(), "\u00A7x\u00A7c\u00A7c\u00A7c\u00A7c\u00A7c\u00A7czzz");
    }

    @Test
    public void hexColorTest6() {
        parser = StringParser.parse("zzz#ccc");
        assertEquals(parser.toString(), "zzz\u00A7x\u00A7c\u00A7c\u00A7c\u00A7c\u00A7c\u00A7c");
    }

    @Test
    public void hexColorTest7() {
        parser = StringParser.parse("abc#ddd#012345#0123#01234#fffz123");
        assertEquals(parser.toString(), "abc" +
            "\u00A7x\u00A7d\u00A7d\u00A7d\u00A7d\u00A7d\u00A7d" +
            "\u00A7x\u00A70\u00A71\u00A72\u00A73\u00A74\u00A75" +
            "\u00A7x\u00A70\u00A70\u00A71\u00A71\u00A72\u00A723" +
            "\u00A7x\u00A70\u00A70\u00A71\u00A71\u00A72\u00A7234" +
            "\u00A7x\u00A7f\u00A7f\u00A7f\u00A7f\u00A7f\u00A7f" +
            "z123");
    }

    @Test
    public void hexColorTest8() {
        parser = StringParser.parse("AAA #AAA AAA");
        assertEquals(parser.toString(), "AAA \u00A7x\u00A7a\u00A7a\u00A7a\u00A7a\u00A7a\u00A7a AAA");
    }

    @Test
    public void variableTest1() {
        parser = StringParser.parse("{test} variable");
        assertEquals(
            parser.toString(StringVariable.init().put("test", "aaa")),
            "aaa variable"
        );
    }

    @Test
    public void variableTest2() {
        parser = StringParser.parse("variable {test}");
        assertEquals(
            parser.toString(StringVariable.init().put("test", "aaa")),
            "variable aaa"
        );
    }

    @Test
    public void variableTest3() {
        parser = StringParser.parse("variable {test}");
        assertEquals(
            parser.toString(StringVariable.init().put("test", 1)),
            "variable 1"
        );
    }

    @Test
    public void variableTest4() {
        parser = StringParser.parse("variable { test }");
        assertEquals(
            parser.toString(StringVariable.init().put("test", 1)),
            "variable 1"
        );
    }

    @Test
    public void variableTest5() {
        parser = StringParser.parse("variable { t e s t }");
        assertEquals(
            parser.toString(StringVariable.init().put("t e s t", 1)),
            "variable 1"
        );
    }

    @Test
    public void escapeTest() {
        // \\ \{ \&0 \z \ \
        parser = StringParser.parse("\\\\ \\{ \\&0 \\z \\ \\");
        assertEquals(parser.toString(), "\\ { &0 \\z \\ \\");
    }

    @Test
    public void notFoundVariableTest() {
        parser = StringParser.parse("variable {test}");
        assertEquals(parser.toString(), "variable {test}");
    }

    @Test
    public void brokenTest1() {
        parser = StringParser.parse("variable {broken");
        System.out.println(parser.toString());
    }

    @Test
    public void brokenTest2() {
        parser = StringParser.parse("variable broken}");
        System.out.println(parser.toString());
    }

    @Test
    public void test1() {
        parser = StringParser.parse("test &#aaa");
        assertEquals(parser.toString(), "test &\u00A7x\u00A7a\u00A7a\u00A7a\u00A7a\u00A7a\u00A7a");
    }

    @Test
    public void test2() {
        parser = StringParser.parse("test {variable}#aaa");
        assertEquals(parser.toString(), "test {variable}\u00A7x\u00A7a\u00A7a\u00A7a\u00A7a\u00A7a\u00A7a");
    }

    @Test
    public void test3() {
        parser = StringParser.parse("test \\\\#aaa");
        assertEquals(parser.toString(), "test \\\u00A7x\u00A7a\u00A7a\u00A7a\u00A7a\u00A7a\u00A7a");
    }
}
