package jp.jyn.jbukkitlib.config.parser;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>Parses the time represented by a string.</p>
 * <p>Syntax: &lt;number&gt;&lt;unit&gt;</p>
 * <p>(unit: [d]ay/[h]our/[m]inute/[s]econd)</p>
 * <p><br>example:</p>
 * <ul>
 *     <li>1d</li>
 *     <li>1d12h</li>
 *     <li>12h1d</li>
 *     <li>1d1d</li>
 * </ul>
 */
public class TimeParser {
    private final static Map<Character, TimeUnit> UNIT_TABLE = Map.of(
        'd', TimeUnit.DAYS,
        'D', TimeUnit.DAYS,
        'h', TimeUnit.HOURS,
        'H', TimeUnit.HOURS,
        'm', TimeUnit.MINUTES,
        'M', TimeUnit.MINUTES,
        's', TimeUnit.SECONDS,
        'S', TimeUnit.SECONDS
    );

    /**
     * Parses the time represented by a string.
     *
     * @param value input string
     * @param unit  Unit of returned value
     * @return Parsed time
     * @throws IllegalArgumentException When a string that cannot be parsed is passed.
     */
    public static long parse(CharSequence value, TimeUnit unit) {
        long result = 0;

        int tmp = 0;
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            int v = Character.digit(c, 10);
            // number
            if (v != -1) {
                tmp *= 10;
                tmp += v;
                continue;
            }

            // not number
            TimeUnit u = UNIT_TABLE.get(c);
            if (u == null) {
                throw new IllegalArgumentException(value.toString() + " is invalid syntax.");
            }
            result += unit.convert(tmp, u);
            tmp = 0;
        }

        return result;
    }
}
