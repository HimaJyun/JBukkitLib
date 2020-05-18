package jp.jyn.jbukkitlib;

import java.io.IOException;
import java.io.InputStream;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

public class JBukkitLib {
    private JBukkitLib() { }

    /**
     * This library name
     */
    public final static String NAME;
    /**
     * Version
     */
    public final static String VERSION;
    /**
     * Build time
     */
    public final static OffsetDateTime BUILD_TIME;
    /**
     * Bukkit API version
     */
    public final static String API_VERSION;
    /**
     * JBukkitLib URL
     */
    public final static String URL;
    /**
     * Issue URL
     */
    public final static String ISSUE_URL;
    /**
     * Git URL
     */
    public final static String GIT_URL;
    /**
     * Git commit id
     */
    public final static String GIT_COMMIT;

    static {
        Properties properties = new Properties();
        try (InputStream in = JBukkitLib.class.getClassLoader().getResourceAsStream("jbukkitlib.properties")) {
            if (in != null) {
                properties.load(in);
            }
        } catch (IOException ignore) {
            // Hint: Execution of "/reload" will prevent resources from being acquired.
        }

        NAME = properties.getProperty("name", "JBukkitLib");
        VERSION = properties.getProperty("version", "Unknown");

        BUILD_TIME = OffsetDateTime.parse(
            properties.getProperty("build.time", "1970-01-01T00:00:00+0000"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ")
        );

        API_VERSION = properties.getProperty("bukkit.version", "Unknown");
        URL = properties.getProperty("url", "Unknown");
        ISSUE_URL = properties.getProperty("issue", "Unknown");
        GIT_URL = properties.getProperty("git.url", "Unknown");
        GIT_COMMIT = properties.getProperty("git.commit", "Unknown");
    }
}
