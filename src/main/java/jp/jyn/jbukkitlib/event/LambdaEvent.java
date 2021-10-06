package jp.jyn.jbukkitlib.event;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * EventListener with Lambda.
 */
public class LambdaEvent {
    /**
     * Registers the specified consumer to the given event class.<br>
     * See: {@link org.bukkit.plugin.PluginManager#registerEvent(Class, Listener, EventPriority, EventExecutor, Plugin, boolean)}
     *
     * @param type            Event class, for generics identify.
     * @param priority        Event priority. See: {@link EventHandler#priority()}
     * @param ignoreCancelled Whether to pass cancelled events or not. See {@link EventHandler#ignoreCancelled()}
     * @param plugin          Plugin to register.
     * @param consumer        Event handler.
     * @param <T>             Event type.
     * @return See: {@link org.bukkit.event.HandlerList#unregister(Listener)} {@link org.bukkit.event.HandlerList#unregisterAll(Listener)}
     */
    @SuppressWarnings("unchecked")
    public static <T extends Event> Listener register(Class<T> type, EventPriority priority, boolean ignoreCancelled, Plugin plugin, Consumer<T> consumer) {
        Objects.requireNonNull(consumer, "Consumer is null");

        var l = new Listener() {};
        Bukkit.getPluginManager().registerEvent(type, l, priority, (listener, event) -> consumer.accept((T) event), plugin, ignoreCancelled);
        return l;
    }

    /**
     * Registers the specified consumer to the given event class.<br>
     * See: {@link org.bukkit.plugin.PluginManager#registerEvent(Class, Listener, EventPriority, EventExecutor, Plugin, boolean)}
     *
     * @param type     Event class, for generics identify.
     * @param priority Event priority. See: {@link EventHandler#priority()}
     * @param plugin   Plugin to register.
     * @param consumer Event handler.
     * @param <T>      Event type.
     * @return See: {@link org.bukkit.event.HandlerList#unregister(Listener)} {@link org.bukkit.event.HandlerList#unregisterAll(Listener)}
     */
    public static <T extends Event> Listener register(Class<T> type, EventPriority priority, Plugin plugin, Consumer<T> consumer) {
        return register(type, priority, false, plugin, consumer);
    }

    /**
     * Registers the specified consumer to the given event class.<br>
     * See: {@link org.bukkit.plugin.PluginManager#registerEvent(Class, Listener, EventPriority, EventExecutor, Plugin, boolean)}
     *
     * @param type            Event class, for generics identify.
     * @param ignoreCancelled Whether to pass cancelled events or not. See {@link EventHandler#ignoreCancelled()}
     * @param plugin          Plugin to register.
     * @param consumer        Event handler.
     * @param <T>             Event type.
     * @return See: {@link org.bukkit.event.HandlerList#unregister(Listener)} {@link org.bukkit.event.HandlerList#unregisterAll(Listener)}
     */
    public static <T extends Event> Listener register(Class<T> type, boolean ignoreCancelled, Plugin plugin, Consumer<T> consumer) {
        return register(type, EventPriority.NORMAL, ignoreCancelled, plugin, consumer);
    }

    /**
     * Registers the specified consumer to the given event class.<br>
     * See: {@link org.bukkit.plugin.PluginManager#registerEvent(Class, Listener, EventPriority, EventExecutor, Plugin, boolean)}
     *
     * @param type     Event class, for generics identify.
     * @param plugin   Plugin to register.
     * @param consumer Event handler.
     * @param <T>      Event type.
     * @return See: {@link org.bukkit.event.HandlerList#unregister(Listener)} {@link org.bukkit.event.HandlerList#unregisterAll(Listener)}
     */
    public static <T extends Event> Listener register(Class<T> type, Plugin plugin, Consumer<T> consumer) {
        return register(type, EventPriority.NORMAL, false, plugin, consumer);
    }
}
