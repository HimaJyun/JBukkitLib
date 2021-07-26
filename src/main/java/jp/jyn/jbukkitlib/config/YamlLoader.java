package jp.jyn.jbukkitlib.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Yaml loader that can specify file name
 */
public class YamlLoader {
    private FileConfiguration config = null;
    private final File configFile;
    private final String file;
    private final Plugin plugin;

    /**
     * YamlLoader using config.yml
     *
     * @param plugin plugin
     */
    public YamlLoader(Plugin plugin) {
        this(plugin, "config.yml");
    }

    /**
     * YamlLoader to use the specified file
     *
     * @param plugin   plugin
     * @param fileName file name.
     */
    public YamlLoader(Plugin plugin, String fileName) {
        this.plugin = plugin;
        this.file = fileName;
        this.configFile = new File(plugin.getDataFolder(), file);
    }

    /**
     * {@link Plugin#saveDefaultConfig()}
     */
    public void saveDefaultConfig() {
        if (!configFile.exists()) {
            plugin.saveResource(file, false);
        }
    }

    /**
     * {@link Plugin#getConfig()}
     *
     * @return FileConfiguration
     */
    public FileConfiguration getConfig() {
        if (config == null) {
            reloadConfig();
        }
        return config;
    }

    /**
     * {@link Plugin#saveConfig()}
     */
    public void saveConfig() {
        if (config == null) {
            return;
        }
        try {
            getConfig().save(configFile);
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE, "Could not save config to " + configFile, ex);
        }
    }

    /**
     * {@link Plugin#reloadConfig()}
     */
    public void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(configFile);

        final InputStream defConfigStream = plugin.getResource(file);
        if (defConfigStream == null) {
            return;
        }

        config.setDefaults(
            YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, StandardCharsets.UTF_8)));
    }

    // region one-shot loader

    /**
     * Load Yaml file.<br>
     * Hint: This method is loading default value from plugin jar and write out if file not exists.
     *
     * @param plugin Plugin
     * @param file   file name.
     * @return Loaded Yaml configuration.
     */
    public static FileConfiguration load(Plugin plugin, String file) {
        var l = new YamlLoader(plugin, file);
        l.saveDefaultConfig();
        return l.getConfig();
    }

    /**
     * Load Yaml file.<br>
     * Hint: This method doesn't load the yml file from plugin jar. In such a case, use {@link YamlLoader#load(Plugin, String)}.
     *
     * @param file file
     * @return Loaded Yaml configuration. empty configuration if file not exists.
     */
    public static FileConfiguration load(File file) {
        return YamlConfiguration.loadConfiguration(file);
    }

    /**
     * Load Yaml file.<br>
     * Hint: This method doesn't load the yml file from plugin jar. In such a case, use {@link YamlLoader#load(Plugin, String)}.
     *
     * @param path file path
     * @return Loaded Yaml configuration. empty configuration if file not exists.
     */
    public static FileConfiguration load(Path path) {
        return load(path.toFile());
    }

    /**
     * Save configuration.
     *
     * @param config configuration
     * @param file   file
     */
    public static void save(FileConfiguration config, File file) {
        if (config == null) {
            return;
        }

        try {
            config.save(file);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Save configuration.<br>
     * Use relative path from {@link Plugin#getDataFolder()}.
     *
     * @param config configuration
     * @param plugin Plugin
     * @param file   file name
     */
    public static void save(FileConfiguration config, Plugin plugin, String file) {
        save(config, new File(plugin.getDataFolder(), file));
    }

    /**
     * Save configuration.
     *
     * @param config configuration
     * @param path   file path
     */
    public static void save(FileConfiguration config, Path path) {
        save(config, path.toFile());
    }
    // endregion

    // null -> default
    // String -> Singleton string
    // Empty array -> Empty list
    // String array -> String list
    private static <T> List<T> getStrings(ConfigurationSection config, String path, List<T> def, Function<Object, T> mapper, boolean ignoreDefault) {
        var v = ignoreDefault ? config.get(path, null) : config.get(path);
        if (v instanceof List l) {
            if (l.isEmpty()) {
                return Collections.emptyList();
            }
            var r = new ArrayList<T>(l.size());
            for (Object o : l) {
                r.add(mapper.apply(o));
            }
            return r;
        }

        var t = mapper.apply(v);
        return t == null ? def : Collections.singletonList(t);
    }

    /**
     * get String or String list
     *
     * @param config ConfigurationSection
     * @param path   Path of the Object to get.
     * @param def    The default value to return if the path is not found.
     * @param mapper Object to T mapper
     * @param <T>    Type
     * @return null if not exist.
     */
    public static <T> List<T> getStrings(ConfigurationSection config, String path, List<T> def, Function<Object, T> mapper) {
        return getStrings(config, path, def, mapper, true);
    }

    /**
     * get String or String list
     *
     * @param config ConfigurationSection
     * @param path   Path of the String to get.
     * @param def    The default value to return if the path is not found.
     * @return null if not exist.
     */
    public static List<String> getStrings(ConfigurationSection config, String path, List<String> def) {
        return getStrings(config, path, def, o -> (o != null ? o.toString() : null));
    }

    /**
     * get String or String list
     *
     * @param config ConfigurationSection
     * @param path   Path of the Object to get.
     * @param mapper Object to T mapper
     * @param <T>    Type
     * @return null if not exist.
     */
    public static <T> List<T> getStrings(ConfigurationSection config, String path, Function<Object, T> mapper) {
        return getStrings(config, path, null, mapper, false);
    }

    /**
     * get String or String list
     *
     * @param config ConfigurationSection
     * @param path   Path of the String to get.
     * @return null if not exist.
     */
    public static List<String> getStrings(ConfigurationSection config, String path) {
        return getStrings(config, path, o -> (o != null ? o.toString() : null));
    }

    /**
     * Iterates the child elements of the specified ConfigurationSection.
     *
     * @param config   ConfigurationSection
     * @param consumer The ConfigurationSection of the child element is passed as an argument.
     */
    public static void section(ConfigurationSection config, Consumer<ConfigurationSection> consumer) {
        if (config == null) return;

        for (String k : config.getKeys(false)) {
            if (config.isConfigurationSection(k)) {
                consumer.accept(Objects.requireNonNull(config.getConfigurationSection(k)));
            }
        }
    }

    /**
     * Iterates the child elements of the specified ConfigurationSection.
     *
     * @param config   ConfigurationSection
     * @param consumer The key of the child element and the ConfigurationSection passed as an argument are passed.
     */
    public static void section(ConfigurationSection config, BiConsumer<String, ConfigurationSection> consumer) {
        if (config == null) return;

        for (String k : config.getKeys(false)) {
            consumer.accept(k, config);
        }
    }

    /**
     * Create a Map using the section keys and values.
     *
     * @param config ConfigurationSection
     * @param mapper The ConfigurationSection of the child element is passed as an argument.
     * @param <E>    Element type
     * @return Map
     */
    public static <E> Map<String, E> section(ConfigurationSection config, Function<ConfigurationSection, E> mapper) {
        if (config == null) return Collections.emptyMap();

        Map<String, E> result = new HashMap<>();
        for (String k : config.getKeys(false)) {
            if (!config.isConfigurationSection(k)) continue;
            result.put(k, mapper.apply(Objects.requireNonNull(config.getConfigurationSection(k))));
        }
        return result;
    }

    /**
     * Create a Map using the section keys and values.
     *
     * @param config ConfigurationSection
     * @param mapper The key of the child element and the ConfigurationSection passed as an argument are passed.
     * @param <E>    Element type
     * @return Map
     */
    public static <E> Map<String, E> section(ConfigurationSection config, BiFunction<String, ConfigurationSection, E> mapper) {
        if (config == null) return Collections.emptyMap();

        Map<String, E> result = new HashMap<>();
        for (String k : config.getKeys(false)) {
            result.put(k, mapper.apply(k, config));
        }
        return result;
    }

    /**
     * Copy directory from plugin jar.
     *
     * @param plugin  plugin
     * @param src     source directory name
     * @param dir     target directory name
     * @param replace replace if exists
     */
    public static void copyDir(Plugin plugin, String src, Path dir, boolean replace) {
        String name = src + "/";
        try {
            String path = plugin.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
            var jar = new JarFile(URLDecoder.decode(path.replace("+", "%2b"), StandardCharsets.UTF_8));
            Files.createDirectories(dir);
            for (var entries = jar.entries(); entries.hasMoreElements(); ) {
                var e = entries.nextElement();
                if (e.isDirectory() || !e.getName().startsWith(name)) {
                    continue;
                }

                String file = e.getName().substring(name.length());
                Path dst = dir.resolve(file);
                if (!replace && Files.exists(dst)) {
                    continue;
                }

                try (var in = new BufferedInputStream(jar.getInputStream(e))) {
                    Files.copy(in, dst, StandardCopyOption.REPLACE_EXISTING);
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Copy directory from plugin jar.
     * Does not replace if exists.
     *
     * @param plugin plugin
     * @param src    source directory name
     * @param dir    target directory name
     */
    public static void copyDir(Plugin plugin, String src, Path dir) {
        copyDir(plugin, src, dir, false);
    }

    /**
     * Copy directory from plugin jar to plugin config directory.
     *
     * @param plugin  plugin
     * @param src     source directory name
     * @param replace replace if exists
     */
    public static void copyDir(Plugin plugin, String src, boolean replace) {
        copyDir(plugin, src, plugin.getDataFolder().toPath().resolve(src), replace);
    }

    /**
     * Copy directory from plugin jar to plugin config directory.
     *
     * @param plugin plugin
     * @param src    source directory name
     */
    public static void copyDir(Plugin plugin, String src) {
        copyDir(plugin, src, false);
    }

    /**
     * remove extension from filename
     *
     * @param file filename
     * @return filename without extension. If filename does not have extension return as-is.
     */
    public static String removeExtension(String file) {
        int i = file.lastIndexOf('.');
        return i == -1 ? file : file.substring(0, i);
    }

    /**
     * call {@link Path#getFileName()} and remove extension from filename.
     *
     * @param file file
     * @return filename without extension. If filename does not have extension return as-is.
     */
    public static String removeExtension(Path file) {
        return removeExtension(file.getFileName().toString());
    }

    /**
     * Finds files with a .yml extension in the specified directory.
     * Note: Does not include .yaml.
     *
     * @param dir      target directory
     * @param maxDepth max depth
     * @return List of files found.
     */
    public static List<Path> findYaml(Path dir, int maxDepth) {
        try (Stream<Path> stream = Files.find(dir, maxDepth, (p, a) -> a.isRegularFile() && p.toString().endsWith(".yml"))) {
            return stream.collect(Collectors.toList());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Finds files with a .yml extension in the specified directory.
     * Note: Does not include .yaml.
     *
     * @param dir target directory
     * @return List of files found.
     */
    public static List<Path> findYaml(Path dir) {
        return findYaml(dir, 1);
    }

    /**
     * Finds files with a .yml extension in the specified directory.
     * Note: Does not include .yaml.
     *
     * @param plugin   plugin
     * @param dir      directory name
     * @param maxDepth max depth
     * @return List of files found.
     */
    public static List<Path> findYaml(Plugin plugin, String dir, int maxDepth) {
        return findYaml(plugin.getDataFolder().toPath().resolve(dir), maxDepth);
    }

    /**
     * Finds files with a .yml extension in the specified directory.
     * Note: Does not include .yaml.
     *
     * @param plugin plugin
     * @param dir    directory name
     * @return List of files found.
     */
    public static List<Path> findYaml(Plugin plugin, String dir) {
        return findYaml(plugin.getDataFolder().toPath().resolve(dir), 1);
    }


    /**
     * Remove config value
     *
     * @param config config
     * @param key    key
     */
    public static void remove(ConfigurationSection config, String key) {
        config.set(key, null);
    }

    /**
     * Rename config value
     *
     * @param config config
     * @param from   old key
     * @param to     new key
     */
    public static void rename(ConfigurationSection config, String from, String to) {
        Object old = config.get(from);
        config.set(from, null);
        config.set(to, old);
    }

    /**
     * File move
     *
     * @param src source
     * @param dst destination
     */
    public static void move(Path src, Path dst) {
        try {
            Files.move(src, dst, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * File move, Use relative path from {@link Plugin#getDataFolder()}.
     *
     * @param plugin plugin
     * @param src    source path, relative path from plugin data folder.
     * @param dst    destination path, relative path from plugin data folder.
     */
    public static void move(Plugin plugin, String src, String dst) {
        Path base = plugin.getDataFolder().toPath();
        move(base.resolve(src), base.resolve(dst));
    }

    /**
     * File copy
     *
     * @param src source
     * @param dst destination
     */
    public static void copy(Path src, Path dst) {
        try {
            Files.copy(src, dst, StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * File copy, Use relative path from {@link Plugin#getDataFolder()}.
     *
     * @param plugin plugin
     * @param src    source path, relative path from plugin data folder.
     * @param dst    destination path, relative path from plugin data folder.
     */
    public static void copy(Plugin plugin, String src, String dst) {
        Path base = plugin.getDataFolder().toPath();
        copy(base.resolve(src), base.resolve(dst));
    }

    /**
     * File backup, Use relative path from {@link Plugin#getDataFolder()}.
     *
     * @param plugin plugin
     * @param file   target file path, relative path from plugin data folder.
     * @param suffix backup file suffix
     */
    public static void backup(Plugin plugin, String file, String suffix) {
        Path base = plugin.getDataFolder().toPath();
        copy(base.resolve(file), base.resolve(file + "." + suffix));
    }

    /**
     * File backup, Use relative path from {@link Plugin#getDataFolder()}.
     *
     * @param plugin plugin
     * @param file   target file path, relative path from plugin data folder.
     * @param suffix backup file suffix
     */
    public static void backup(Plugin plugin, String file, int suffix) {
        Path base = plugin.getDataFolder().toPath();
        copy(base.resolve(file), base.resolve(file + "." + suffix));
    }

    /**
     * File backup, Use relative path from {@link Plugin#getDataFolder()}.<br>
     * Use time-based suffix.
     *
     * @param plugin plugin
     * @param file   target file path, relative path from plugin data folder.
     */
    public static void backup(Plugin plugin, String file) {
        copy(plugin, file, ZonedDateTime.now().format(DateTimeFormatter.ofPattern("uuuuMMddHHmmss")));
    }
}
