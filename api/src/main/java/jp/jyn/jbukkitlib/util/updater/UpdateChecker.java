package jp.jyn.jbukkitlib.util.updater;

import java.time.Instant;
import java.util.concurrent.Callable;

@FunctionalInterface
public interface UpdateChecker extends Callable<UpdateChecker.LatestVersion> {
    default LatestVersion callEx() {
        try {
            return call();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    class LatestVersion {
        public final String version;
        public final String url;
        public final Instant release;
        public final String description;

        public LatestVersion(String version, String url, Instant release, String description) {
            this.version = version;
            this.url = url;
            this.release = release;
            this.description = description;
        }
    }
}
