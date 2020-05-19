package jp.jyn.jbukkitlib.config.parser.template;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AbstractParserTest {
    @Test
    public void parseFunctionSimple() {
        Map.Entry<String, String[]> t = T.parseFunction("function(arg1)");
        assertEquals(t.getKey(), "function");
        assertArrayEquals(t.getValue(), ary("arg1"));

        t = T.parseFunction("function(arg1,arg2)");
        assertEquals(t.getKey(), "function");
        assertArrayEquals(t.getValue(), ary("arg1", "arg2"));

        t = T.parseFunction("function()");
        assertEquals(t.getKey(), "function");
        assertArrayEquals(t.getValue(), ary());

        t = T.parseFunction("f_u_n_c_t_i_o_n(a_r_g_1,arg2)");
        assertEquals(t.getKey(), "f_u_n_c_t_i_o_n");
        assertArrayEquals(t.getValue(), ary("a_r_g_1", "arg2"));

        t = T.parseFunction("aaa(bbb,ccc,ddd)");
        assertEquals(t.getKey(), "aaa");
        assertArrayEquals(t.getValue(), ary("bbb", "ccc", "ddd"));
    }

    @Test
    public void parseFunctionSpace() {
        Map.Entry<String, String[]> t = T.parseFunction("f u n c t i o n(arg1,arg2)");
        assertEquals(t.getKey(), "function");
        assertArrayEquals(t.getValue(), ary("arg1", "arg2"));

        t = T.parseFunction("f u n c t i o n (arg1,arg2)");
        assertEquals(t.getKey(), "function");
        assertArrayEquals(t.getValue(), ary("arg1", "arg2"));

        t = T.parseFunction("f u n c t i o n ( a r g 1 , a r g 2 ) ");
        assertEquals(t.getKey(), "function");
        assertArrayEquals(t.getValue(), ary("arg1", "arg2"));
    }

    @Test
    public void parseFunctionQuotation() {
        Map.Entry<String, String[]> t = T.parseFunction("function(\"arg1\")");
        assertEquals(t.getKey(), "function");
        assertArrayEquals(t.getValue(), ary("arg1"));

        t = T.parseFunction("function(\"arg1\",\"arg2\")");
        assertEquals(t.getKey(), "function");
        assertArrayEquals(t.getValue(), ary("arg1", "arg2"));

        t = T.parseFunction("function(\"arg1\",arg2)");
        assertEquals(t.getKey(), "function");
        assertArrayEquals(t.getValue(), ary("arg1", "arg2"));

        t = T.parseFunction("function(arg1,\"arg2\")");
        assertEquals(t.getKey(), "function");
        assertArrayEquals(t.getValue(), ary("arg1", "arg2"));

        t = T.parseFunction("function(\"a r g 1\",\",()\\\"\\\\\")"); // ,() \"\\
        assertEquals(t.getKey(), "function");
        assertArrayEquals(t.getValue(), ary("a r g 1", ",()\"\\")); // ,()"\

        t = T.parseFunction("function(\"\")");
        assertEquals(t.getKey(), "function");
        assertArrayEquals(t.getValue(), ary(""));

        t = T.parseFunction("function(\"\",\"\")");
        assertEquals(t.getKey(), "function");
        assertArrayEquals(t.getValue(), ary("", ""));
    }

    @Test
    public void parseFunctionCJK() {
        Map.Entry<String, String[]> t = T.parseFunction("function(引数)");
        assertEquals(t.getKey(), "function");
        assertArrayEquals(t.getValue(), ary("引数"));

        t = T.parseFunction("function(引数1,引数2)");
        assertEquals(t.getKey(), "function");
        assertArrayEquals(t.getValue(), ary("引数1", "引数2"));

        t = T.parseFunction("function(\"引数1\",\"引数2\")");
        assertEquals(t.getKey(), "function");
        assertArrayEquals(t.getValue(), ary("引数1", "引数2"));
    }

    private String[] ary(String... args) {
        return args;
    }

    private static class T extends AbstractParser {}
}
