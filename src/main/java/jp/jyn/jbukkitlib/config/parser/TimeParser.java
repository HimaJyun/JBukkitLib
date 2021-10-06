package jp.jyn.jbukkitlib.config.parser;

import java.math.BigDecimal;
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
    /**
     * Parses the time represented by a string.
     *
     * @param value       input string
     * @param unit        Unit of returned value
     * @param defaultUnit if unit is not specified, use this unit. (nullable, throw NumberFormatException if null)
     * @return Parsed time
     * @throws NumberFormatException input value can not parse.
     */
    public static long parse(CharSequence value, TimeUnit unit, TimeUnit defaultUnit) {
        var sb = new StringBuilder();
        BigDecimal result = BigDecimal.ZERO;

        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            TimeUnit u = switch (c) {
                case 'd', 'D', '日' -> TimeUnit.DAYS;
                case 'h', 'H', '時' -> TimeUnit.HOURS;
                case 'm', 'M', '分' -> TimeUnit.MINUTES;
                case 's', 'S', '秒' -> TimeUnit.SECONDS;
                case '㎳' -> TimeUnit.MILLISECONDS; // 隠し機能
                case 'u', '㎲', '\u03bc'/*μ*/, '\u00b5'/*µ*/ -> TimeUnit.MICROSECONDS; // 隠し機能
                case 'n', '㎱' -> TimeUnit.NANOSECONDS; // 隠し機能
                default -> null;
            };

            if (u == null) {
                sb.append(c);
            } else {
                result = result.add(parseTime(sb.toString(), unit, u));
                sb.setLength(0);
            }
        }

        if (!sb.isEmpty()) {
            if (defaultUnit == null) {
                throw new NumberFormatException(sb.toString() + " is invalid value, need unit.");
            } else {
                // 単位指定がなければデフォルト単位を使う
                result = result.add(parseTime(sb.toString(), unit, defaultUnit));
            }
        }

        return result.longValue();
    }

    /**
     * Parses the time represented by a string.
     *
     * @param value input string
     * @param unit  Unit of returned value
     * @return Parsed time
     * @throws NumberFormatException input value can not parse.
     */
    public static long parse(CharSequence value, TimeUnit unit) {
        return parse(value, unit, TimeUnit.SECONDS);
    }

    private static BigDecimal parseTime(String value, TimeUnit from, TimeUnit to) {
        // 0.5h -> 0.5 * TimeUnit.HOURS.toSeconds(1)
        BigDecimal d;
        try {
            d = new BigDecimal(value);
        } catch (NumberFormatException e) {
            throw new NumberFormatException(value + " is invalid value");
        }

        var unit1 = BigDecimal.valueOf(from.convert(1, to));
        return d.multiply(unit1);
    }
}
