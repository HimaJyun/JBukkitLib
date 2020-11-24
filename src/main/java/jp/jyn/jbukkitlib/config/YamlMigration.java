package jp.jyn.jbukkitlib.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * static methods for migration of configuration files.
 * convenient to use by static import.
 */
public class YamlMigration {
    private YamlMigration() {}

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
     * Alias of {@link #rename(ConfigurationSection, String, String)}
     *
     * @param config config
     * @param from   old key
     * @param to     new key
     */
    public static void move(ConfigurationSection config, String from, String to) {
        rename(config, from, to);
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
}
