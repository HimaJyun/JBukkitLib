package jp.jyn.jbukkitlib.config.parser;

import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TimeParserTest {
    @Test
    public void parseTest() {
        Stream.of(
            Tests.init("1d", TimeUnit.SECONDS, TimeUnit.DAYS.toSeconds(1)),
            Tests.init("1D", TimeUnit.SECONDS, TimeUnit.DAYS.toSeconds(1)),
            Tests.init("1h", TimeUnit.SECONDS, TimeUnit.HOURS.toSeconds(1)),
            Tests.init("1H", TimeUnit.SECONDS, TimeUnit.HOURS.toSeconds(1)),
            Tests.init("1m", TimeUnit.SECONDS, TimeUnit.MINUTES.toSeconds(1)),
            Tests.init("1M", TimeUnit.SECONDS, TimeUnit.MINUTES.toSeconds(1)),
            Tests.init("1s", TimeUnit.SECONDS, TimeUnit.SECONDS.toSeconds(1)),
            Tests.init("1S", TimeUnit.SECONDS, TimeUnit.SECONDS.toSeconds(1)),

            Tests.init("1d", TimeUnit.MILLISECONDS, TimeUnit.DAYS.toMillis(1)),
            Tests.init("12d", TimeUnit.SECONDS, TimeUnit.DAYS.toSeconds(12)),
            Tests.init("1d12h", TimeUnit.SECONDS, TimeUnit.DAYS.toSeconds(1) + TimeUnit.HOURS.toSeconds(12)),
            Tests.init("12h1d", TimeUnit.SECONDS, TimeUnit.DAYS.toSeconds(1) + TimeUnit.HOURS.toSeconds(12)),
            Tests.init("1d23h45m", TimeUnit.SECONDS, TimeUnit.DAYS.toSeconds(1) + TimeUnit.HOURS.toSeconds(23) + TimeUnit.MINUTES.toSeconds(45)),
            Tests.init("1d1d", TimeUnit.SECONDS, TimeUnit.DAYS.toSeconds(2))
        ).forEach(t -> assertEquals(t.actual, TimeParser.parse(t.input, t.unit), t::toString));
    }

    @Test
    public void exceptionTest() {
        assertThrows(
            NumberFormatException.class,
            () -> TimeParser.parse("invalid", TimeUnit.SECONDS)
        );
    }

    @Test
    public void floatTest() {
        Stream.of(
            Tests.init("0.5h", TimeUnit.SECONDS, 1800),

            Tests.init("0.5d0.5h", TimeUnit.MINUTES, 720 + 30),
            Tests.init("0.5h0.5d", TimeUnit.MINUTES, 30 + 720),

            Tests.init("0.5m1s", TimeUnit.SECONDS, 31),
            Tests.init("1s0.5m", TimeUnit.SECONDS, 31)
        ).forEach(t -> assertEquals(t.actual, TimeParser.parse(t.input, t.unit), t::toString));
    }

    @Test
    public void calcTest() {
        Stream.of(
            Tests.init("-1h", TimeUnit.HOURS, -1),
            Tests.init("+1h", TimeUnit.HOURS, 1),

            Tests.init("+1h-1h", TimeUnit.HOURS, 0),
            Tests.init("+1h+1h", TimeUnit.HOURS, 2),
            Tests.init("+1h+1h+1h", TimeUnit.HOURS, 3),
            Tests.init("-1h-1h", TimeUnit.HOURS, -2),
            Tests.init("-1h-1h-1h", TimeUnit.HOURS, -3),

            Tests.init("1h+30m", TimeUnit.MINUTES, 90),
            Tests.init("1h-30m", TimeUnit.MINUTES, 30),

            Tests.init("0.5h+30m", TimeUnit.MINUTES, 60),
            Tests.init("0.5h-30m", TimeUnit.MINUTES, 0),

            Tests.init("0.5d+0.5h", TimeUnit.MINUTES, 750),
            Tests.init("0.5d-0.5h", TimeUnit.MINUTES, 690)
        ).forEach(t -> assertEquals(t.actual, TimeParser.parse(t.input, t.unit), t::toString));
    }

    @Test
    public void emptyTest() {
        assertEquals(30, TimeParser.parse("30", TimeUnit.SECONDS));
        assertEquals(90, TimeParser.parse("1m30", TimeUnit.SECONDS));
    }

    @Test
    public void secretUnitTest() {
        Stream.of(
            Tests.init("1日", TimeUnit.SECONDS, TimeUnit.DAYS.toSeconds(1)),
            Tests.init("1時", TimeUnit.SECONDS, TimeUnit.HOURS.toSeconds(1)),
            Tests.init("1分", TimeUnit.SECONDS, TimeUnit.MINUTES.toSeconds(1)),
            Tests.init("1秒", TimeUnit.SECONDS, TimeUnit.SECONDS.toSeconds(1)),

            Tests.init("1u", TimeUnit.NANOSECONDS, TimeUnit.MICROSECONDS.toNanos(1)),
            Tests.init("1n", TimeUnit.NANOSECONDS, TimeUnit.NANOSECONDS.toNanos(1)),

            Tests.init("1㎳", TimeUnit.NANOSECONDS, TimeUnit.MILLISECONDS.toNanos(1)),
            Tests.init("1㎲", TimeUnit.NANOSECONDS, TimeUnit.MICROSECONDS.toNanos(1)),
            Tests.init("1㎱", TimeUnit.NANOSECONDS, TimeUnit.NANOSECONDS.toNanos(1)),

            Tests.init("1\u03bc", TimeUnit.MICROSECONDS, TimeUnit.NANOSECONDS.toNanos(1)),
            Tests.init("1μ", TimeUnit.MICROSECONDS, TimeUnit.NANOSECONDS.toNanos(1)),
            Tests.init("1\u00b5", TimeUnit.MICROSECONDS, TimeUnit.NANOSECONDS.toNanos(1)),
            Tests.init("1µ", TimeUnit.MICROSECONDS, TimeUnit.NANOSECONDS.toNanos(1))
        ).forEach(t -> assertEquals(t.actual, TimeParser.parse(t.input, t.unit), t::toString));
    }

    private final static class Tests {
        private final String input;
        private final TimeUnit unit;
        private final long actual;

        private Tests(String input, TimeUnit unit, long actual) {
            this.input = input;
            this.unit = unit;
            this.actual = actual;
        }

        private static Tests init(String input, TimeUnit unit, long actual) {
            return new Tests(input, unit, actual);
        }

        @Override
        public String toString() {
            return input + " " + unit;
        }
    }
}
