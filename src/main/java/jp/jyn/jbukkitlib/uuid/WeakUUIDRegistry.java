package jp.jyn.jbukkitlib.uuid;

import jp.jyn.jbukkitlib.JBukkitLib;
import jp.jyn.jbukkitlib.util.BukkitCompletableFuture;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.function.Supplier;

public class WeakUUIDRegistry {
    private final static Map.Entry<UUID, String> NOT_FOUND;
    private final static Reference<Map.Entry<UUID, String>> NULL_REFERENCE;
    private final static Function<Map.Entry<UUID, String>, Reference<Map.Entry<UUID, String>>> ref = WeakReference::new;

    static {
        NOT_FOUND = getGlobal("empty.uuid", () -> new AbstractMap.SimpleImmutableEntry<>(null, null));
        NULL_REFERENCE = getGlobal("empty.name", () -> ref.apply(null));
    }

    private final Plugin plugin;
    private final ExecutorService executor;

    // 複数のプラグインから共有される可能性がある。独自のクラスは一切使えない。
    private final Map<UUID, Map.Entry<UUID, String>> uuidCache;
    private final Map<String, Reference<Map.Entry<UUID, String>>> nameCache;

    // maven-shade-pluginでリロケーションされた(別のクラスに見える)インスタンス同士でデータを共有する必要があるのでPropertiesを使う (かなり筋の悪い方法だがそうするしかない)
    @SuppressWarnings({"unchecked", "RedundantCast"})
    private static <T> T getGlobal(String key, Supplier<T> supplier) {
        // Hint: If "jp.jyn.jbukkitlib" is specified, it may be rewritten on relocation.
        final String KEY_PREFIX = "jbukkitlib.UUIDRegistry(V2)#";
        Object ret = System.getProperties().compute(KEY_PREFIX + key, (k, v) -> {
            if (v == null) {
                return supplier.get();
            }
            try {
                return (T) v;
            } catch (ClassCastException e) {
                return supplier.get();
            }
        });
        return (T) ret;
    }

    private WeakUUIDRegistry(Plugin plugin, ExecutorService executor,
                             Map<UUID, Map.Entry<UUID, String>> uuidCache, Map<String, Reference<Map.Entry<UUID, String>>> nameCache) {
        this.plugin = plugin;
        this.executor = executor;
        this.uuidCache = uuidCache;
        this.nameCache = nameCache;
    }

    public static WeakUUIDRegistry getShared(Plugin plugin, ExecutorService executor) {
        if (executor == null) {
            executor = getGlobal("executor", Executors::newSingleThreadExecutor);
            executor.submit(() -> Thread.currentThread().setName(String.format("%s Shared-UUIDRegistry", JBukkitLib.NAME))); // set thread name.
        }

        return new WeakUUIDRegistry(plugin, executor, getGlobal("uuid", ConcurrentHashMap::new), getGlobal("name", ConcurrentHashMap::new));
    }

    public static WeakUUIDRegistry getShared(Plugin plugin) {
        return getShared(plugin, null);
    }

    private String lower(String str) {
        return str.toLowerCase(Locale.ROOT);
    }

    /**
     * 両方の引数をnullにしてはならない、nameは常に正しい名前でなければならない
     *
     * @param uuid UUID
     * @param name 常に正しい名前でなければならない
     */
    private Map.Entry<UUID, String> updateCache(UUID uuid, String name) {
        if (uuid == null) {
            nameCache.put(lower(name), NULL_REFERENCE);
        } else if (name == null) {
            uuidCache.put(uuid, NOT_FOUND);
        } else {
            var v = Map.entry(uuid, name);
            uuidCache.put(uuid, v);
            nameCache.put(lower(name), ref.apply(v));
            return v;
        }
        return null;
    }

    private Map.Entry<UUID, String> tryGetName(UUID uuid) {
        var p = Bukkit.getPlayer(uuid);
        var c = uuidCache.get(uuid);
        if (p != null) {
            // キャッシュ更新
            if (c == null || !p.getName().equals(c.getValue())) { // ネガティブキャッシュの時はequalsで判別できる
                return updateCache(p.getUniqueId(), p.getName());
            }
        }
        return c;
    }

    public String getName(UUID uuid) {
        var v = tryGetName(uuid);
        if (v != null) {
            return v.getValue(); // negative cache
        }

        var name = (new UUIDConverter.NameGetter(uuid)).callEx().orElse(null);
        updateCache(uuid, name);
        return name;
    }

    public BukkitCompletableFuture<String> getNameAsync(UUID uuid) {
        var v = tryGetName(uuid);
        if (v != null) {
            return BukkitCompletableFuture.completedFuture(plugin, v.getValue());
        }

        return BukkitCompletableFuture.supplyAsync(plugin, () -> {
            var name = (new UUIDConverter.NameGetter(uuid)).callEx().orElse(null);
            updateCache(uuid, name);
            return name;
        }, executor);
    }

    private Map.Entry<UUID, String> tryGetUUID(String name) {
        var p = Bukkit.getPlayer(name);
        var c = nameCache.getOrDefault(name, NULL_REFERENCE);
        if (p != null) {
            // キャッシュ更新
            var e = c.get();
            if (e == null || !p.getName().equals(e.getValue())) { // ネガティブキャッシュの時はequalsで判別できる
                return updateCache(p.getUniqueId(), p.getName());
            }
        }
        return c.get();
    }

    public UUID getUUID(String name) {
        var v = tryGetUUID(name);
        if (v != null) {
            return v.getKey();
        }

        var entry = (new UUIDConverter.UUIDGetter(name)).callEx();
        var uuid = entry.map(Map.Entry::getValue).orElse(null);
        updateCache(uuid, entry.map(Map.Entry::getKey).orElse(name));
        return uuid;
    }

    public BukkitCompletableFuture<UUID> getUUIDAsync(String name) {
        var v = tryGetUUID(name);
        if (v != null) {
            return BukkitCompletableFuture.completedFuture(plugin, v.getKey());
        }

        return BukkitCompletableFuture.supplyAsync(plugin, () -> {
            var entry = (new UUIDConverter.UUIDGetter(name)).callEx();
            var uuid = entry.map(Map.Entry::getValue).orElse(null);
            updateCache(uuid, entry.map(Map.Entry::getKey).orElse(name));
            return uuid;
        }, executor);
    }

    public BukkitCompletableFuture<Map<String, UUID>> getMultipleUUIDAsync(Collection<String> names) {
        Map<String, UUID> result = new HashMap<>((names.size() * 4) / 3);
        Map<String, String> request = new HashMap<>();

        // cache search
        for (String name : names) {
            var v = tryGetUUID(name);
            if (v != null && v == NOT_FOUND) {
                result.put(name, v.getKey());
            } else {
                request.put(lower(name), name);
            }
        }

        if (request.isEmpty()) {
            return BukkitCompletableFuture.completedFuture(plugin, result);
        }

        return BukkitCompletableFuture.supplyAsync(plugin, () -> {
            var uuid = new UUIDConverter.MultipleUUIDGetter(request.keySet()).callEx();

            for (Map.Entry<String, UUID> entry : uuid.entrySet()) {
                updateCache(entry.getValue(), entry.getKey());

                var n = request.remove(lower(entry.getKey()));
                if (n != null) {
                    result.put(n, entry.getValue());
                }
            }
            for (String s : request.keySet()) {
                updateCache(null, s);
            }

            return result;
        }, executor);
    }

    public BukkitCompletableFuture<Map<UUID, String>> getMultipleNameAsync(Collection<UUID> uuids) {
        Map<UUID, String> result = new HashMap<>((uuids.size() * 4) / 3);
        Set<UUID> request = new HashSet<>();

        for (UUID uuid : uuids) {
            var v = tryGetName(uuid);
            if (v != null && v == NOT_FOUND) {
                result.put(uuid, v.getValue());
            } else {
                request.add(uuid);
            }
        }

        if (request.isEmpty()) {
            return BukkitCompletableFuture.completedFuture(plugin, result);
        }

        return BukkitCompletableFuture.supplyAsync(plugin, () -> {
            for (UUID uuid : request) {
                var name = (new UUIDConverter.NameGetter(uuid)).callEx();

                if (name.isPresent()) {
                    updateCache(uuid, name.get());
                    result.put(uuid, name.get());
                } else {
                    updateCache(uuid, null);
                }
            }
            return result;
        }, executor);
    }
}
