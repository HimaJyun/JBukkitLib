package jp.jyn.jbukkitlib.util.updater;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jp.jyn.jbukkitlib.JBukkitLib;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.OffsetDateTime;

public class GitHubReleaseChecker implements UpdateChecker {
    private final static String ACCEPT = "application/vnd.github.v3+json";
    private final static Gson gson = new Gson();
    private final String URL;

    private String etag = null;
    private long lastModified = 0;
    private LatestVersion cache = null;

    public GitHubReleaseChecker(String user, String repo) {
        // latest does not include prerelease.
        // https://developer.github.com/v3/repos/releases/#get-the-latest-release
        URL = String.format("https://api.github.com/repos/%s/%s/releases/latest", user, repo);
    }

    @Override
    public LatestVersion call() throws Exception {
        HttpsURLConnection connection = getConnection();
        if (connection.getResponseCode() == HttpURLConnection.HTTP_NOT_MODIFIED) {
            return cache;
        }
        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new HttpNotOKException(connection.getResponseCode(), connection.getResponseMessage());
        }

        try (InputStreamReader reader = new InputStreamReader(connection.getInputStream())) {
            JsonObject json = gson.fromJson(reader, JsonObject.class);
            cache = new LatestVersion(
                json.get("tag_name").getAsString(),
                json.get("html_url").getAsString(),
                OffsetDateTime.parse(json.get("published_at").getAsString()).toInstant(),
                json.get("body").getAsString()
            );
            etag = connection.getHeaderField("ETag");
            lastModified = connection.getLastModified();

            return cache;
        }
    }

    private HttpsURLConnection getConnection() throws IOException {
        HttpsURLConnection connection = (HttpsURLConnection) (new URL(this.URL).openConnection());
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", ACCEPT);
        connection.setRequestProperty("User-Agent", String.format("%s - %s ( %s )", JBukkitLib.NAME, JBukkitLib.VERSION, JBukkitLib.URL));
        connection.setInstanceFollowRedirects(true);
        connection.setIfModifiedSince(lastModified);
        if (etag != null) {
            connection.setRequestProperty("If-None-Match", etag);
        }
        return connection;
    }
}
