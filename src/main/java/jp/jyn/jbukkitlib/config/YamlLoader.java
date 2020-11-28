package jp.jyn.jbukkitlib.config;

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
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
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
            JarFile jar = new JarFile(URLDecoder.decode(path.replace("+", "%2b"), "UTF-8"));
            Files.createDirectories(dir);
            for (Enumeration<JarEntry> entries = jar.entries(); entries.hasMoreElements(); ) {
                JarEntry e = entries.nextElement();
                if (e.isDirectory() || !e.getName().startsWith(name)) {
                    continue;
                }

                String file = e.getName().substring(name.length());
                Path dst = dir.resolve(file);
                if (!replace && Files.exists(dst)) {
                    continue;
                }

                try (BufferedInputStream in = new BufferedInputStream(jar.getInputStream(e))) {
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
     * Finds files with a yml extension in the specified directory.
     * Note: Does not include yaml.
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
     * Finds files with a yml extension in the specified directory.
     * Note: Does not include yaml.
     *
     * @param dir target directory
     * @return List of files found.
     */
    public static List<Path> findYaml(Path dir) {
        return findYaml(dir, 1);
    }

    /**
     * Finds files with a yml extension in the specified directory.
     * Note: Does not include yaml.
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
     * Finds files with a yml extension in the specified directory.
     * Note: Does not include yaml.
     *
     * @param plugin plugin
     * @param dir    directory name
     * @return List of files found.
     */
    public static List<Path> findYaml(Plugin plugin, String dir) {
        return findYaml(plugin.getDataFolder().toPath().resolve(dir), 1);
    }
}
