package jp.jyn.jbukkitlib.util.updater;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertSame;

public class GitHubReleaseCheckerTest {

    @Disabled
    @Test
    public void call() {
        // https://github.com/HimaJyun/JBukkitLib
        UpdateChecker checker = new GitHubReleaseChecker("HimaJyun", "JBukkitLib");
        UpdateChecker.LatestVersion latestVersion = checker.callEx();
        Stream.of(
            getClass().toString(),
            latestVersion.version,
            latestVersion.url,
            latestVersion.release,
            latestVersion.description,
            ""
        ).forEach(System.out::println);

        // cache test
        assertSame(latestVersion, checker.callEx());
    }
}
