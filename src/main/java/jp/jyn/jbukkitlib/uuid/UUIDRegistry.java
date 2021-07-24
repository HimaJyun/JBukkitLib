package jp.jyn.jbukkitlib.uuid;

import jp.jyn.jbukkitlib.JBukkitLib;
import jp.jyn.jbukkitlib.cache.CacheFactory;
import jp.jyn.jbukkitlib.util.BukkitCompletableFuture;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * Registry managing UUID conversion and caching
 */
// In this class, we use null for "no value exists" and Optional.empty() for "user does not exist".
@SuppressWarnings("OptionalAssignedToNull")
public class UUIDRegistry {
    private final ExecutorService executor;

    private final Map<String, Optional<UUID>> nameToUUIDCache;
    private final Map<UUID, Optional<String>> uuidToNameCache;

    private final Plugin plugin;

    private UUIDRegistry(Plugin plugin,
                         Map<String, Optional<UUID>> nameToUUIDCache,
                         Map<UUID, Optional<String>> uuidToNameCache,
                         ExecutorService executor) {
        this.plugin = plugin;
        this.nameToUUIDCache = nameToUUIDCache;
        this.uuidToNameCache = uuidToNameCache;
        this.executor = executor;
    }

    public UUIDRegistry(Plugin plugin, CacheFactory cache, ExecutorService executor) {
        this(plugin, cache.create(true), cache.create(true), executor);
    }

    public UUIDRegistry(Plugin plugin, CacheFactory cache) {
        this(plugin, cache, Executors.newSingleThreadExecutor());
        this.executor.submit(() -> Thread.currentThread().setName(String.format("%s-%s UUIDRegistry", plugin.getName(), JBukkitLib.NAME)));
    }

    /**
     * Get registry to share cache with multiple plugins
     *
     * @param plugin   plugin
     * @param executor executor
     * @return Registry to share cache.
     */
    @SuppressWarnings("unchecked")
    public static UUIDRegistry getSharedCacheRegistry(Plugin plugin, ExecutorService executor) {
        // Hint: If "jp.jyn.jbukkitlib" is specified, it may be rewritten on relocation.
        final String KEY = "jbukkitlib.UUIDRegistry#";
        final BiFunction<String, Supplier<Object>, Object> getProperty = (key, supplier) -> {
            Object result = System.getProperties().get(key);
            if (result == null) {
                result = supplier.get();
                System.getProperties().put(key, result);
            }
            return result;
        };

        Map<String, Optional<UUID>> nameToUUIDCache;
        Map<UUID, Optional<String>> uuidToNameCache;

        // Because the possibility of being relocated by maven-shade-plugin,
        //   the synchronized method is (probably) meaningless.
        // It is solved by making System.getProperties() synchronized.
        synchronized (System.getProperties()) {
            Supplier<Object> mapSupplier = () -> CacheFactory.INFINITY.create(true);
            nameToUUIDCache = (Map<String, Optional<UUID>>) getProperty.apply(KEY + "nameToUUIDCache", mapSupplier);
            uuidToNameCache = (Map<UUID, Optional<String>>) getProperty.apply(KEY + "uuidToNameCache", mapSupplier);
            if (executor == null) {
                executor = (ExecutorService) getProperty.apply(KEY + "executor", () -> {
                    ExecutorService e = Executors.newSingleThreadExecutor();
                    e.submit(() -> Thread.currentThread().setName(String.format("%s Shared-UUIDRegistry", JBukkitLib.NAME))); // set thread name.
                    return e;
                });
            }
        }

        return new UUIDRegistry(plugin, nameToUUIDCache, uuidToNameCache, executor);
    }

    /**
     * Get registry to share cache with multiple plugins
     *
     * @param plugin plugin
     * @return Registry to share cache.
     */
    public static UUIDRegistry getSharedCacheRegistry(Plugin plugin) {
        return getSharedCacheRegistry(plugin, null);
    }

    private void updateCache(UUID uuid, String name) {
        if (uuid != null) {
            uuidToNameCache.put(uuid, Optional.ofNullable(name));
        }
        if (name != null) {
            nameToUUIDCache.put(lower(name), Optional.ofNullable(uuid));
        }
    }

    // region getName
    private Optional<String> tryGetName(UUID uuid) {
        // search cache.
        Optional<String> value = uuidToNameCache.get(uuid);
        if (value != null) {
            return value;
        }

        // try get online player.
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            updateCache(player.getUniqueId(), player.getName());
            return Optional.of(player.getName());
        }
        return null;
    }

    /**
     * <p>Get player Name</p>
     * <p>Note: It may block threads.</p>
     *
     * @param uuid Target UUID
     * @return name
     */
    public Optional<String> getName(UUID uuid) {
        Optional<String> value = tryGetName(uuid);
        if (value != null) {
            return value;
        }

        // Don't use getOfflinePlayer
        // getOfflinePlayer blocks threads.
        // If you query "nonexistent user", getOfflinePlayer fails and UUIDConverter is called, so a long thread blocking occurs.

        // use Mojang API(slow)
        value = (new UUIDConverter.NameGetter(uuid)).callEx();
        updateCache(uuid, value.orElse(null));
        return value;
    }

    /**
     * <p>Get user name without blocking thread.</p>
     * <p>Note: This method does not necessarily create a new thread.
     * (For example, if the player is online or the cache exists, the value is returned in the current thread)</p>
     *
     * @param uuid Target UUID
     * @return {@link jp.jyn.jbukkitlib.util.BukkitCompletableFuture}
     */
    public BukkitCompletableFuture<Optional<String>> getNameAsync(UUID uuid) {
        Optional<String> value = tryGetName(uuid);
        if (value != null) {
            return BukkitCompletableFuture.completedFuture(plugin, value);
        }

        // use Mojang API with threads.
        return BukkitCompletableFuture.supplyAsync(plugin, () -> {
            Optional<String> result = (new UUIDConverter.NameGetter(uuid)).callEx();
            // update cache
            updateCache(uuid, result.orElse(null));
            return result;
        }, executor);
    }
    // endregion

    // region getUUID
    private Optional<UUID> tryGetUUID(String name) {
        // Get online player(it is fast)
        Player player = Bukkit.getPlayer(name);
        if (player != null) {
            updateCache(player.getUniqueId(), player.getName());
            return Optional.of(player.getUniqueId());
        }

        return nameToUUIDCache.get(lower(name));
    }

    /**
     * <p>Get player UUID</p>
     * <p>Note: It may block threads.</p>
     *
     * @param name Target name
     * @return UUID
     */
    public Optional<UUID> getUUID(String name) {
        Optional<UUID> value = tryGetUUID(name);
        if (value != null) {
            return value;
        }

        Optional<Map.Entry<String, UUID>> result = (new UUIDConverter.UUIDGetter(name)).callEx();
        value = result.map(Map.Entry::getValue);
        updateCache(value.orElse(null), result.map(Map.Entry::getKey).orElse(name));
        return value;
    }

    /**
     * <p>Get user UUID without blocking thread.</p>
     * <p>Note: This method does not necessarily create a new thread.
     * (For example, if the player is online or the cache exists, the value is returned in the current thread)</p>
     *
     * @param name Target Name
     * @return {@link jp.jyn.jbukkitlib.util.BukkitCompletableFuture}
     */
    public BukkitCompletableFuture<Optional<UUID>> getUUIDAsync(String name) {
        Optional<UUID> value = tryGetUUID(name);
        if (value != null) {
            return BukkitCompletableFuture.completedFuture(plugin, value);
        }

        return BukkitCompletableFuture.supplyAsync(plugin, () -> {
            Optional<Map.Entry<String, UUID>> result = (new UUIDConverter.UUIDGetter(name)).callEx();
            Optional<UUID> uuid = result.map(Map.Entry::getValue);

            updateCache(uuid.orElse(null), result.map(Map.Entry::getKey).orElse(name));

            return uuid;
        }, executor);
    }
    // endregion

    // region multiple

    /**
     * <p>Gets the UUID of multiple users without blocking threads.</p>
     * <p>Note: This method does not necessarily create a new thread.
     * (For example, if the player is online or the cache exists, the value is returned in the current thread)</p>
     *
     * @param names Target Names
     * @return {@link jp.jyn.jbukkitlib.util.BukkitCompletableFuture}
     */
    public BukkitCompletableFuture<Map<String, UUID>> getMultipleUUIDAsync(Collection<String> names) {
        Map<String, UUID> result = new HashMap<>((names.size() * 4) / 3);
        Map<String, String> request = new HashMap<>();

        // search cache.
        for (String name : names) {
            Optional<UUID> uuid = tryGetUUID(name);
            if (uuid == null) {
                request.put(lower(name), name);
            } else {
                uuid.ifPresent(u -> result.put(name, u));
            }
        }

        if (request.isEmpty()) {
            return BukkitCompletableFuture.completedFuture(plugin, result);
        }

        return BukkitCompletableFuture.supplyAsync(plugin, () -> {
            Map<String, UUID> uuid = new UUIDConverter.MultipleUUIDGetter(request.keySet()).callEx();

            // "Correct name" might be different as "Requested name." (Upper or lower case letters, etc.)
            // Since we do not know "Correct name" from the caller of this method, we need to convert it to "Requested name".
            for (var entry : uuid.entrySet()) {
                // update cache.
                updateCache(entry.getValue(), entry.getKey());

                String name = request.remove(lower(entry.getKey()));
                if (name != null) {
                    result.put(name, entry.getValue());
                }
            }
            // negative cache
            for (String name : request.keySet()) {
                updateCache(null, name);
            }

            return result;
        }, executor);
    }

    /**
     * <p>Gets the Name of multiple UUID without blocking threads.</p>
     * <p>Note: This method does not necessarily create a new thread.
     * (For example, if the player is online or the cache exists, the value is returned in the current thread)</p>
     *
     * @param uuids Target uuids
     * @return {@link jp.jyn.jbukkitlib.util.BukkitCompletableFuture}
     */
    public BukkitCompletableFuture<Map<UUID, String>> getMultipleNameAsync(Collection<UUID> uuids) {
        Map<UUID, String> result = new HashMap<>((uuids.size() * 4) / 3);
        Set<UUID> request = new HashSet<>();

        // search cache.
        for (UUID uuid : uuids) {
            Optional<String> name = tryGetName(uuid);
            if (name == null) {
                request.add(uuid);
            } else {
                name.ifPresent(n -> result.put(uuid, n));
            }
        }

        if (request.isEmpty()) {
            return BukkitCompletableFuture.completedFuture(plugin, result);
        }

        return BukkitCompletableFuture.supplyAsync(plugin, () -> {
            for (UUID uuid : request) {
                // The Mojang API does not have an API to convert multiple UUIDs to names.
                // It is "insanely inefficient", but simply executes multiple calls.
                // Do you believe Keep-Alive? I believe.
                final Optional<String> name = (new UUIDConverter.NameGetter(uuid)).callEx();

                if (name.isPresent()) {
                    String n = name.get();
                    updateCache(uuid, n);
                    result.put(uuid, n);
                } else {
                    // negative
                    updateCache(uuid, null);
                }
            }

            return result;
        }, executor);
    }
    // endregion

    private static String lower(String str) {
        return str.toLowerCase(Locale.ENGLISH);
    }
}
