package jp.jyn.jbukkitlib.config.parser;

import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

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
        ).forEach(t -> assertEquals(TimeParser.parse(t.input, t.unit), t.actual));

        assertThrows(
            IllegalArgumentException.class,
            () -> TimeParser.parse("invalid", TimeUnit.SECONDS)
        );
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
    }
}
