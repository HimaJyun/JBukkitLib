package jp.jyn.jbukkitlib.config.parser;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@SuppressWarnings("PointlessArithmeticExpression")
public class ExpressionParserTest {
    @Test
    public void test() {
        assertEquals(0, new ExpressionParser("0").calc(), 0);
        assertEquals(1 + 1, new ExpressionParser("1+1").calc(), 0);
        assertEquals(1 + 1 - 1, new ExpressionParser("1+1-1").calc(), 0);
        assertEquals(12 + 34 - 1, new ExpressionParser("12+34-1").calc(), 0);
        assertEquals(1 - 1, new ExpressionParser("1-1").calc(), 0);
        assertEquals(1 * 2, new ExpressionParser("1*2").calc(), 0);
        assertEquals(1.0d / 2.0d, new ExpressionParser("1/2").calc(), 0);
        assertEquals(1 % 2, new ExpressionParser("1%2").calc(), 0);
        assertEquals(Math.pow(2, 16), new ExpressionParser("2^16").calc(), 0);
    }

    @Test
    public void priority() {
        assertEquals(1 * 2 * 3, new ExpressionParser("1*2*3").calc(), 0);
        assertEquals(1 + 2 * 3, new ExpressionParser("1+2*3").calc(), 0);
        assertEquals(1 * 2 + 3, new ExpressionParser("1*2+3").calc(), 0);
        assertEquals(1.0 / 2.0 / 4.0, new ExpressionParser("1/2/4").calc(), 0);
        assertEquals(1.0 + 2.0 / 4.0, new ExpressionParser("1+2/4").calc(), 0);
        assertEquals(1.0 / 2.0 + 4.0, new ExpressionParser("1/2+4").calc(), 0);
        assertEquals(1 % 2 % 3, new ExpressionParser("1%2%3").calc(), 0);
        assertEquals(1 + 2 % 3, new ExpressionParser("1+2%3").calc(), 0);
        assertEquals(1 % 2 + 3, new ExpressionParser("1%2+3").calc(), 0);
    }

    @Test
    public void floating() {
        assertEquals(0.25 + 0.25, new ExpressionParser("0.25+0.25").calc(), 0);
        assertEquals(1 - 0.5, new ExpressionParser("1-0.5").calc(), 0);
        assertEquals(1 * 0.5, new ExpressionParser("1*0.5").calc(), 0);
        assertEquals(1 / 0.5, new ExpressionParser("1/0.5").calc(), 0);
        assertEquals(1 % 0.5, new ExpressionParser("1%0.5").calc(), 0);
    }

    @Test
    public void factor() {
        assertEquals((1 + 2) * 3, new ExpressionParser("(1+2)*3").calc(), 0);
        assertEquals(1 * (2 + 3), new ExpressionParser("1*(2+3)").calc(), 0);
        assertEquals(1 * (2 * (3 + 4)), new ExpressionParser("1*(2*(3+4))").calc(), 0);
    }

    @Test
    public void space() {
        assertEquals(1 + 1, new ExpressionParser("1 + 1").calc(), 0);
        assertEquals(1 * (2 * (3 + 4)), new ExpressionParser("1 * (2 * (3 + 4))").calc(), 0);
    }

    @Test
    public void variable() {
        assertEquals(1 + 1, new ExpressionParser("1 + var").calc("var", 1.0d), 0);
        assertEquals(1 * (2 * (3 + 4)), new ExpressionParser("1*(2*(var+4))").calc("var", 3.0d), 0);
    }

    @Test
    public void function() {
        assertEquals(-10, new ExpressionParser("negative(10)").calc(), 0);
        assertEquals(Math.abs(-10), new ExpressionParser("abs(negative(10))").calc(), 0);
        assertEquals(Math.min(1, 2), new ExpressionParser("min(1,2)").calc(), 0);
        // random
        Map<String, Double> variable = new HashMap<>();
        variable.put("min", 10.0d);
        variable.put("max", 20.0d);
        System.out.println("floor(random()*(max-min-1))+min: " + new ExpressionParser("floor(random()*(max-min-1))+min").calc(variable));
        ExpressionParser rnd = new ExpressionParser("random()");
        System.out.println("random(): " + rnd.calc() + ", " + rnd.calc());
    }
}
