package jp.jyn.jbukkitlib.util.updater;

import org.junit.Test;

import java.util.stream.Stream;

import static org.junit.Assert.*;

public class GitHubReleaseCheckerTest {

    @Test
    public void call() {
        // https://github.com/HimaJyun/Zabbigot
        UpdateChecker checker = new GitHubReleaseChecker("HimaJyun", "Zabbigot");
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
