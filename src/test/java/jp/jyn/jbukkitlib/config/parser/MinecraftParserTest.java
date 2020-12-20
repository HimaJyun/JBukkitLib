package jp.jyn.jbukkitlib.config.parser;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static jp.jyn.jbukkitlib.config.parser.MinecraftParser.*;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MinecraftParserTest {

    @Test
    public void variableTest1() {
        List<Node> n = parse("aaa{aaa}aaa");

        assertEquals(n.size(), 3);

        assertEquals(n.get(0).type, Type.STRING);
        assertEquals(n.get(0).getValue(), "aaa");

        assertEquals(n.get(1).type, Type.VARIABLE);
        assertEquals(n.get(1).getValue(), "aaa");

        assertEquals(n.get(2).type, Type.STRING);
        assertEquals(n.get(2).getValue(), "aaa");
    }

    @Test
    public void variableTest2() {
        List<Node> n = parse("{aaa}aaa{aaa}");

        assertEquals(n.size(), 3);

        assertEquals(n.get(0).type, Type.VARIABLE);
        assertEquals(n.get(0).getValue(), "aaa");

        assertEquals(n.get(1).type, Type.STRING);
        assertEquals(n.get(1).getValue(), "aaa");

        assertEquals(n.get(2).type, Type.VARIABLE);
        assertEquals(n.get(2).getValue(), "aaa");
    }

    @Test
    public void variableTest3() {
        List<Node> n = parse("{aaa}{aaa}");

        assertEquals(n.size(), 2);

        assertEquals(n.get(0).type, Type.VARIABLE);
        assertEquals(n.get(0).getValue(), "aaa");

        assertEquals(n.get(1).type, Type.VARIABLE);
        assertEquals(n.get(1).getValue(), "aaa");
    }

    @Test
    public void variableTest4() {
        List<Node> n = parse("aaa{a{a}a}aaa");

        assertEquals(n.size(), 3);

        assertEquals(n.get(0).type, Type.STRING);
        assertEquals(n.get(0).getValue(), "aaa");

        assertEquals(n.get(1).type, Type.VARIABLE);
        assertEquals(n.get(1).getValue(), "a{a}a");

        assertEquals(n.get(2).type, Type.STRING);
        assertEquals(n.get(2).getValue(), "aaa");
    }

    @Test
    public void variableTest5() {
        List<Node> n = parse("aaa{aa&}a}aaa");

        assertEquals(n.size(), 3);

        assertEquals(n.get(0).type, Type.STRING);
        assertEquals(n.get(0).getValue(), "aaa");

        assertEquals(n.get(1).type, Type.VARIABLE);
        assertEquals(n.get(1).getValue(), "aa}a");

        assertEquals(n.get(2).type, Type.STRING);
        assertEquals(n.get(2).getValue(), "aaa");
    }

    @Test
    public void variableTest6() {
        List<Node> n = parse("aaa{a&{aa}aaa");

        assertEquals(n.size(), 3);

        assertEquals(n.get(0).type, Type.STRING);
        assertEquals(n.get(0).getValue(), "aaa");

        assertEquals(n.get(1).type, Type.VARIABLE);
        assertEquals(n.get(1).getValue(), "a{aa");

        assertEquals(n.get(2).type, Type.STRING);
        assertEquals(n.get(2).getValue(), "aaa");
    }

    @Test
    public void variableTest7() {
        List<Node> n = parse("aaa{aaa&&}aaa");

        assertEquals(n.size(), 3);

        assertEquals(n.get(0).type, Type.STRING);
        assertEquals(n.get(0).getValue(), "aaa");

        assertEquals(n.get(1).type, Type.VARIABLE);
        assertEquals(n.get(1).getValue(), "aaa&");

        assertEquals(n.get(2).type, Type.STRING);
        assertEquals(n.get(2).getValue(), "aaa");
    }

    @Test
    public void variableTest8() {
        List<Node> n = parse("aaa{ aaa }aaa");

        assertEquals(n.size(), 3);

        assertEquals(n.get(0).type, Type.STRING);
        assertEquals(n.get(0).getValue(), "aaa");

        assertEquals(n.get(1).type, Type.VARIABLE);
        assertEquals(n.get(1).getValue(), "aaa");

        assertEquals(n.get(2).type, Type.STRING);
        assertEquals(n.get(2).getValue(), "aaa");
    }

    @Test
    public void variableTest9() {
        List<Node> n = parse("aaa{a a a}aaa");

        assertEquals(n.size(), 3);

        assertEquals(n.get(0).type, Type.STRING);
        assertEquals(n.get(0).getValue(), "aaa");

        assertEquals(n.get(1).type, Type.VARIABLE);
        assertEquals(n.get(1).getValue(), "a a a");

        assertEquals(n.get(2).type, Type.STRING);
        assertEquals(n.get(2).getValue(), "aaa");
    }

    @Test
    public void mcColorTest1() {
        List<Node> n = parse("aaa&aaaaaa");

        assertEquals(n.size(), 3);

        assertEquals(n.get(0).type, Type.STRING);
        assertEquals(n.get(0).getValue(), "aaa");

        assertEquals(n.get(1).type, Type.MC_COLOR);
        assertEquals(n.get(1).getValue(), "a");

        assertEquals(n.get(2).type, Type.STRING);
        assertEquals(n.get(2).getValue(), "aaaaa");
    }

    @Test
    public void mcColorTest2() {
        List<Node> n = parse("aaa&Aaaaaa");

        assertEquals(n.size(), 3);

        assertEquals(n.get(0).type, Type.STRING);
        assertEquals(n.get(0).getValue(), "aaa");

        assertEquals(n.get(1).type, Type.MC_COLOR);
        assertEquals(n.get(1).getValue(), "a");

        assertEquals(n.get(2).type, Type.STRING);
        assertEquals(n.get(2).getValue(), "aaaaa");
    }

    @Test
    public void mcColorTest3() {
        List<Node> n = parse("&aa&a");

        assertEquals(n.size(), 3);

        assertEquals(n.get(0).type, Type.MC_COLOR);
        assertEquals(n.get(0).getValue(), "a");

        assertEquals(n.get(1).type, Type.STRING);
        assertEquals(n.get(1).getValue(), "a");

        assertEquals(n.get(2).type, Type.MC_COLOR);
        assertEquals(n.get(2).getValue(), "a");
    }

    @Test
    public void mcColorTest4() {
        List<Node> n = parse("&a&a");

        assertEquals(n.size(), 2);

        assertEquals(n.get(0).type, Type.MC_COLOR);
        assertEquals(n.get(0).getValue(), "a");

        assertEquals(n.get(1).type, Type.MC_COLOR);
        assertEquals(n.get(1).getValue(), "a");
    }

    @Test
    public void hexColorTest1() {
        List<Node> n = parse("aaa#aaaaaa");

        assertEquals(n.size(), 2);

        assertEquals(n.get(0).type, Type.STRING);
        assertEquals(n.get(0).getValue(), "aaa");

        assertEquals(n.get(1).type, Type.HEX_COLOR);
        assertEquals(n.get(1).getValue(), "aaaaaa");
    }

    @Test
    public void hexColorTest2() {
        List<Node> n = parse("aaa#aaazzz");

        assertEquals(n.size(), 3);

        assertEquals(n.get(0).type, Type.STRING);
        assertEquals(n.get(0).getValue(), "aaa");

        assertEquals(n.get(1).type, Type.HEX_COLOR);
        assertEquals(n.get(1).getValue(), "aaaaaa");

        assertEquals(n.get(2).type, Type.STRING);
        assertEquals(n.get(2).getValue(), "zzz");
    }

    @Test
    public void hexColorTest3() {
        List<Node> n = parse("aaa#AAAAAA");

        assertEquals(n.size(), 2);

        assertEquals(n.get(0).type, Type.STRING);
        assertEquals(n.get(0).getValue(), "aaa");

        assertEquals(n.get(1).type, Type.HEX_COLOR);
        assertEquals(n.get(1).getValue(), "aaaaaa");
    }

    @Test
    public void hexColorTest4() {
        List<Node> n = parse("#aaazzz#aaa");

        assertEquals(n.size(), 3);

        assertEquals(n.get(0).type, Type.HEX_COLOR);
        assertEquals(n.get(0).getValue(), "aaaaaa");

        assertEquals(n.get(1).type, Type.STRING);
        assertEquals(n.get(1).getValue(), "zzz");

        assertEquals(n.get(2).type, Type.HEX_COLOR);
        assertEquals(n.get(2).getValue(), "aaaaaa");
    }

    @Test
    public void hexColorTest5() {
        List<Node> n = parse("#aaa#aaa");

        assertEquals(n.size(), 2);

        assertEquals(n.get(0).type, Type.HEX_COLOR);
        assertEquals(n.get(0).getValue(), "aaaaaa");

        assertEquals(n.get(1).type, Type.HEX_COLOR);
        assertEquals(n.get(1).getValue(), "aaaaaa");
    }

    @Test
    public void escapeTest1() {
        List<Node> n = parse("&{a&}&#aaa&&a");

        assertEquals(n.size(), 1);

        assertEquals(n.get(0).type, Type.STRING);
        assertEquals(n.get(0).getValue(), "{a}#aaa&a");
    }

    @Test
    public void escapeTest2() {
        List<Node> n = parse("&/&$&%&|&!&");

        assertEquals(n.size(), 1);

        assertEquals(n.get(0).type, Type.STRING);
        assertEquals(n.get(0).getValue(), "&/&$&%&|&!&");
    }

    @Test
    public void urlTest1() {
        List<Node> n = parse("https://example.com/ http://example.com/");

        assertEquals(n.size(), 3);

        assertEquals(n.get(0).type, Type.URL);
        assertEquals(n.get(0).getValue(), "https://example.com/");

        assertEquals(n.get(1).type, Type.STRING);
        assertEquals(n.get(1).getValue(), " ");

        assertEquals(n.get(2).type, Type.URL);
        assertEquals(n.get(2).getValue(), "http://example.com/");
    }

    @Test
    public void urlTest2() {
        List<Node> n = parse("https://example.com/?a&aaa#fff aaa");

        assertEquals(n.size(), 2);

        assertEquals(n.get(0).type, Type.URL);
        assertEquals(n.get(0).getValue(), "https://example.com/?a&aaa#fff");

        assertEquals(n.get(1).type, Type.STRING);
        assertEquals(n.get(1).getValue(), " aaa");
    }

    @Test
    public void urlTest3() {
        List<Node> n = parse("aaa example.com aaa");

        assertEquals(n.size(), 1);

        assertEquals(n.get(0).type, Type.STRING);
        assertEquals(n.get(0).getValue(), "aaa example.com aaa");
    }

    @Test
    public void surrogateTest() {
        List<Node> n = parse("\uD867\uDE3D{\uD867\uDE3D}\uD867\uDE3D"); // 𩸽(ほっけ)

        assertEquals(n.size(), 3);

        assertEquals(n.get(0).type, Type.STRING);
        assertEquals(n.get(0).getValue(), "\uD867\uDE3D");

        assertEquals(n.get(1).type, Type.VARIABLE);
        assertEquals(n.get(1).getValue(), "\uD867\uDE3D");

        assertEquals(n.get(2).type, Type.STRING);
        assertEquals(n.get(2).getValue(), "\uD867\uDE3D");
    }

    @Nested
    public class FunctionTest {
        @Test
        public void argsTest1() {
            Map.Entry<String, List<String>> e = MinecraftParser.parseFunction("aaa()");

            assertEquals(e.getKey(), "aaa");
            assertEquals(e.getValue().size(), 0);
        }

        @Test
        public void argsTest2() {
            Map.Entry<String, List<String>> e = MinecraftParser.parseFunction("aaa(bbb)");

            assertEquals(e.getKey(), "aaa");
            assertListEquals(e.getValue(), "bbb");
        }

        @Test
        public void argsTest3() {
            Map.Entry<String, List<String>> e = MinecraftParser.parseFunction("aaa(bbb,ccc)");

            assertEquals(e.getKey(), "aaa");
            assertListEquals(e.getValue(), "bbb", "ccc");
        }

        @Test
        public void escapeTest1() {
            Map.Entry<String, List<String>> e = MinecraftParser.parseFunction("aaa(b&,bb,ccc)");

            assertEquals(e.getKey(), "aaa");
            assertListEquals(e.getValue(), "b,bb", "ccc");
        }

        @Test
        public void escapeTest2() {
            Map.Entry<String, List<String>> e = MinecraftParser.parseFunction("aaa(b&\"bb,ccc)");

            assertEquals(e.getKey(), "aaa");
            assertListEquals(e.getValue(), "b\"bb", "ccc");
        }

        @Test
        public void escapeTest3() {
            Map.Entry<String, List<String>> e = MinecraftParser.parseFunction("aaa(b&'bb,ccc)");

            assertEquals(e.getKey(), "aaa");
            assertListEquals(e.getValue(), "b'bb", "ccc");
        }

        @Test
        public void escapeTest4() {
            Map.Entry<String, List<String>> e = MinecraftParser.parseFunction("aaa(b&(bb,ccc)");

            assertEquals(e.getKey(), "aaa");
            assertListEquals(e.getValue(), "b(bb", "ccc");
        }

        @Test
        public void escapeTest5() {
            Map.Entry<String, List<String>> e = MinecraftParser.parseFunction("aaa(b&)bb,ccc)");

            assertEquals(e.getKey(), "aaa");
            assertListEquals(e.getValue(), "b)bb", "ccc");
        }

        @Test
        public void escapeTest6() {
            Map.Entry<String, List<String>> e = MinecraftParser.parseFunction("aaa(b&&bb,ccc)");

            assertEquals(e.getKey(), "aaa");
            assertListEquals(e.getValue(), "b&bb", "ccc");
        }

        @Test
        public void escapeTest7() {
            Map.Entry<String, List<String>> e = MinecraftParser.parseFunction("aaa(b&bb,ccc)");

            assertEquals(e.getKey(), "aaa");
            assertListEquals(e.getValue(), "b&bb", "ccc");
        }

        @Test
        public void whitespaceTest1() {
            Map.Entry<String, List<String>> e = MinecraftParser.parseFunction("aaa(b b b,ccc)");

            assertEquals(e.getKey(), "aaa");
            assertListEquals(e.getValue(), "bbb", "ccc");
        }

        @Test
        public void quoteTest1() {
            Map.Entry<String, List<String>> e = MinecraftParser.parseFunction("aaa(\"b b b\",'c c c')");

            assertEquals(e.getKey(), "aaa");
            assertListEquals(e.getValue(), "b b b", "c c c");
        }

        @Test
        public void quoteTest2() {
            Map.Entry<String, List<String>> e = MinecraftParser.parseFunction("aaa(\"b,)'b\",'c,)\"c)'");

            assertEquals(e.getKey(), "aaa");
            assertListEquals(e.getValue(), "b,)'b", "c,)\"c)");
        }

        @Test
        public void quoteTest3() {
            Map.Entry<String, List<String>> e = MinecraftParser.parseFunction("aaa(\"bb&&\",'cc&&')");

            assertEquals(e.getKey(), "aaa");
            assertListEquals(e.getValue(), "bb&", "cc&");
        }

        @Test
        public void quoteTest4() {
            Map.Entry<String, List<String>> e = MinecraftParser.parseFunction("aaa(\"b&\"b\",'c&'c')");

            assertEquals(e.getKey(), "aaa");
            assertListEquals(e.getValue(), "b\"b", "c'c");
        }

        @Test
        public void quoteTest5() {
            Map.Entry<String, List<String>> e = MinecraftParser.parseFunction("aaa(\"b'b\",'c\"c')");

            assertEquals(e.getKey(), "aaa");
            assertListEquals(e.getValue(), "b'b", "c\"c");
        }

        private void assertListEquals(List<String> list, String... str) {
            assertArrayEquals(list.toArray(new String[0]), str);
        }
    }
}
