package jp.jyn.jbukkitlib.util.updater;

import org.junit.Test;

import static org.junit.Assert.*;

public class GitHubReleaseCheckerTest {

    @Test
    public void call() {
        // https://github.com/HimaJyun/Zabbigot
        UpdateChecker checker = new GitHubReleaseChecker("HimaJyun", "Zabbigot");
        UpdateChecker.LatestVersion latestVersion = checker.callEx();
        System.out.println(getClass());
        System.out.println(latestVersion.version);
        System.out.println(latestVersion.url);
        System.out.println(latestVersion.release);
        System.out.println(latestVersion.description);
        System.out.println();

        // cache test
        assertSame(latestVersion, checker.callEx());
    }
}
