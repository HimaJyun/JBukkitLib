package jp.jyn.jbukkitlib;

import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

public class JBukkitLibTest {
    @Test
    public void test() {
        Stream.of(
            getClass().toString(),
            JBukkitLib.NAME,
            JBukkitLib.VERSION,
            JBukkitLib.BUILD_TIME.toString(),
            JBukkitLib.API_VERSION,
            JBukkitLib.URL,
            JBukkitLib.ISSUE_URL,
            JBukkitLib.GIT_URL,
            JBukkitLib.GIT_COMMIT
        ).forEach(System.out::println);
    }
}
