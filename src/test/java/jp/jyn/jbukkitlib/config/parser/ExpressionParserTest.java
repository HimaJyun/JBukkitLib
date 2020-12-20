package jp.jyn.jbukkitlib.config.parser;

import jp.jyn.jbukkitlib.util.MapBuilder;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        assertEquals(-10, new ExpressionParser("negate(10)").calc(), 0);
        assertEquals(Math.abs(-10), new ExpressionParser("abs(negate(10))").calc(), 0);
        assertEquals(Math.min(1, 2), new ExpressionParser("min(1,2)").calc(), 0);
        // random
        ExpressionParser p = new ExpressionParser("floor(random()*(max-min-1))+min");
        Map<String, Double> variable = new HashMap<>();
        variable.put("min", 10.0d);
        variable.put("max", 20.0d);
        System.out.println("floor(random()*(max-min-1))+min: " + p.calc(variable) + ", " + p.calc(variable));
        ExpressionParser rnd = new ExpressionParser("random()");
        System.out.println("random(): " + rnd.calc() + ", " + rnd.calc());
    }

    @Test
    public void preCalc() {
        int x1 = 100, y1 = 200, z1 = 300, x2 = 400, y2 = 500, z2 = 600;
        ExpressionParser p = new ExpressionParser(String.format("sqrt(pow(%d-%d,2)+pow(%d-%d,2)+pow(%d-%d,2))", x1, x2, y1, y2, z1, z2));
        assertEquals(Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2) + Math.pow(z1 - z2, 2)), p.calc(), 0);
        assertEquals(1 + Math.min(1, Math.abs(-(5 * 10))), new ExpressionParser("1+min(1,abs(negate(5*10)))").calc(), 0);

        Map<String, Double> variable = MapBuilder.initMap(new HashMap<>(), m -> {
            m.put("x1", (double) x1);
            m.put("x2", (double) x2);
            m.put("y1", (double) y1);
            m.put("y2", (double) y2);
            m.put("z1", (double) z1);
            m.put("z2", (double) z2);
        });
        assertEquals(new ExpressionParser("sqrt(pow(x1-x2,2)+pow(y1-y2,2)+pow(z1-z2,2))").calc(variable), p.calc(), 0);
    }
}
