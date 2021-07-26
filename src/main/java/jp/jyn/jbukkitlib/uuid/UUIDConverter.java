package jp.jyn.jbukkitlib.uuid;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Callable;

/**
 * Minecraft UUID/Name Converter
 */
public class UUIDConverter {
    private final static Gson gson = new Gson();

    private UUIDConverter() {}

    /**
     * UUID to Name Converter
     */
    public static class NameGetter implements Callable<Optional<String>> {
        private final UUID uuid;

        public NameGetter(UUID uuid) {
            this.uuid = uuid;
        }

        public Optional<String> callEx() {
            try {
                return call();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public Optional<String> call() throws Exception {
            var connection = getConnection();
            if (connection.getResponseCode() != 200) {
                return Optional.empty();
            }

            try (var reader = new InputStreamReader(connection.getInputStream())) {
                JsonArray array = gson.fromJson(reader, JsonArray.class);
                JsonObject json = array.get(array.size() - 1).getAsJsonObject();
                return Optional.ofNullable(json.get("name").getAsString());
            }
        }

        private HttpsURLConnection getConnection() throws IOException {
            final String API_URL = "https://api.mojang.com/user/profiles/" + uuid.toString().replace("-", "") + "/names";

            var connection = (HttpsURLConnection) (new URL(API_URL).openConnection());
            connection.setRequestMethod("GET");
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            return connection;
        }
    }

    /**
     * UUID to Name Converter
     */
    public static class UUIDGetter implements Callable<Optional<Map.Entry<String, UUID>>> {
        private final String name;

        public UUIDGetter(String name) {
            this.name = name;
        }

        public Optional<Map.Entry<String, UUID>> callEx() {
            try {
                return call();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public Optional<Map.Entry<String, UUID>> call() throws Exception {
            final var getter = new MultipleUUIDGetter(name);
            for (var entry : getter.call().entrySet()) {
                return Optional.of(entry);
            }
            return Optional.empty();
        }
    }

    /**
     * Multiple UUID to Name Converter
     */
    public static class MultipleUUIDGetter implements Callable<Map<String, UUID>> {
        private final static int API_MAXSIZE = 100;
        private final static String API_URL = "https://api.mojang.com/profiles/minecraft";
        private final List<String> name;

        public MultipleUUIDGetter(String... name) {
            this.name = Arrays.asList(name);
        }

        public MultipleUUIDGetter(Collection<String> name) {
            this.name = new ArrayList<>(name);
        }

        public Map<String, UUID> callEx() {
            try {
                return call();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public Map<String, UUID> call() throws Exception {
            final StringBuilder builder = new StringBuilder(36);

            Map<String, UUID> result = new HashMap<>((name.size() * 4) / 3);

            for (List<String> subList : subLists()) {
                HttpsURLConnection connection = getConnection();
                String body = gson.toJson(subList);
                requestBody(connection, body);
                if (connection.getResponseCode() != 200) {
                    continue;
                }

                try (var reader = new InputStreamReader(connection.getInputStream())) {
                    JsonArray array = gson.fromJson(reader, JsonArray.class);
                    for (JsonElement obj : array) {
                        JsonObject json = obj.getAsJsonObject();
                        String id = json.get("id").getAsString();
                        String name = json.get("name").getAsString();

                        result.put(name, toUUID(builder, id));
                    }
                }
            }

            return result;
        }

        private void requestBody(HttpsURLConnection connection, String body) throws IOException {
            try (var stream = connection.getOutputStream()) {
                stream.write(body.getBytes());
                stream.flush();
            }
        }

        private HttpsURLConnection getConnection() throws IOException {
            var connection = (HttpsURLConnection) (new URL(API_URL).openConnection());
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setDoOutput(true);
            return connection;
        }

        private List<List<String>> subLists() {
            int max = (int) Math.ceil(name.size() / (double) API_MAXSIZE);
            List<List<String>> lists = new ArrayList<>(max);

            for (int i = 0; i < max; i++) {
                List<String> sub = name.subList(i * API_MAXSIZE, Math.min((i + 1) * 100, name.size()));
                lists.add(sub);
            }

            return lists;
        }

        private UUID toUUID(StringBuilder builder, String id) {
            builder.setLength(0);

            // 3,d,4,1,8,7,e,5,-, 5, 5, 6, 5, -, 4, 8, a, 3, -, 8, 9, 9, f, -, 0, f, c, c, 3, 6, 5, e, 7, 0, 8, 4
            // 1,2,3,4,5,6,7,8,   9,10,11,12,    13,14,15,16,   17,18,19,20,   21,22,23,24,25,26,27,28,29,30,31,32
            builder.append(id, 0, 8);
            builder.append('-');
            builder.append(id, 8, 12);
            builder.append('-');
            builder.append(id, 12, 16);
            builder.append('-');
            builder.append(id, 16, 20);
            builder.append('-');
            builder.append(id, 20, 32);
            return UUID.fromString(builder.toString());
        }
    }
}
