package jp.jyn.jbukkitlib.util;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Bukkit thread (scheduler) available CompletableFuture
 *
 * @param <T> Types of values by this Future
 */
public class BukkitCompletableFuture<T> implements CompletionStage<T>, Future<T> {
    private final Plugin plugin;
    private final CompletableFuture<T> future;

    private BukkitCompletableFuture(Plugin plugin, CompletableFuture<T> future) {
        this.plugin = plugin;
        this.future = future;
    }

    private BukkitCompletableFuture(Plugin plugin) {
        this(plugin, new CompletableFuture<>());
    }

    public static <U> BukkitCompletableFuture<U> completedFuture(Plugin plugin, U value) {
        return new BukkitCompletableFuture<>(plugin, CompletableFuture.completedFuture(value));
    }

    public static <U> BukkitCompletableFuture<U> supplyAsync(Plugin plugin, Supplier<U> supplier) {
        return new BukkitCompletableFuture<>(plugin, CompletableFuture.supplyAsync(supplier));
    }

    public static <U> BukkitCompletableFuture<U> supplyAsync(Plugin plugin, Supplier<U> supplier, Executor executor) {
        return new BukkitCompletableFuture<>(plugin, CompletableFuture.supplyAsync(supplier, executor));
    }

    public static BukkitCompletableFuture<Void> runAsync(Plugin plugin, Runnable runnable) {
        return new BukkitCompletableFuture<>(plugin, CompletableFuture.runAsync(runnable));
    }

    public static BukkitCompletableFuture<Void> runAsync(Plugin plugin, Runnable runnable, Executor executor) {
        return new BukkitCompletableFuture<>(plugin, CompletableFuture.runAsync(runnable, executor));
    }

    public static BukkitCompletableFuture<Void> allOf(Plugin plugin, CompletableFuture<?>... cfs) {
        return new BukkitCompletableFuture<>(plugin, CompletableFuture.allOf(cfs));
    }

    public static BukkitCompletableFuture<Object> anyOf(Plugin plugin, CompletableFuture<?>... cfs) {
        return new BukkitCompletableFuture<>(plugin, CompletableFuture.anyOf(cfs));
    }

    public static <U> BukkitCompletableFuture<U> failedFuture(Plugin plugin, Throwable ex) {
        return new BukkitCompletableFuture<>(plugin, CompletableFuture.failedFuture(ex));
    }

    public static <U> BukkitCompletableFuture<U> wrap(Plugin plugin, CompletableFuture<U> future) {
        return new BukkitCompletableFuture<>(plugin, future);
    }

    private <U> BukkitCompletableFuture<U> wrap(CompletableFuture<U> future) {
        return new BukkitCompletableFuture<>(plugin, future);
    }

    // region Bukkit
    private void syncRun(Runnable runnable) {
        if (Bukkit.isPrimaryThread()) {
            runnable.run();
        } else {
            Bukkit.getScheduler().runTask(plugin, runnable);
        }
    }

    private void asyncRun(Runnable runnable) {
        if (Bukkit.isPrimaryThread()) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable);
        } else {
            runnable.run();
        }
    }

    public <U> BukkitCompletableFuture<U> thenApplySync(Function<? super T, ? extends U> fn) {
        BukkitCompletableFuture<U> newFuture = new BukkitCompletableFuture<>(plugin);
        future.thenAccept(t -> syncRun(() -> newFuture.future.complete(fn.apply(t))));
        return newFuture;
    }

    public <U> BukkitCompletableFuture<U> thenApplyBukkitAsync(Function<? super T, ? extends U> fn) {
        BukkitCompletableFuture<U> newFuture = new BukkitCompletableFuture<>(plugin);
        future.thenAccept(t -> asyncRun(() -> newFuture.future.complete(fn.apply(t))));
        return newFuture;
    }

    public BukkitCompletableFuture<Void> thenAcceptSync(Consumer<? super T> action) {
        BukkitCompletableFuture<Void> newFuture = new BukkitCompletableFuture<>(plugin);
        future.thenAccept(t -> syncRun(() -> {
            action.accept(t);
            newFuture.future.complete(null);
        }));
        return newFuture;
    }

    public BukkitCompletableFuture<Void> thenAcceptBukkitAsync(Consumer<? super T> action) {
        BukkitCompletableFuture<Void> newFuture = new BukkitCompletableFuture<>(plugin);
        future.thenAccept(t -> asyncRun(() -> {
            action.accept(t);
            newFuture.future.complete(null);
        }));
        return newFuture;
    }

    public BukkitCompletableFuture<Void> thenRunSync(Runnable action) {
        BukkitCompletableFuture<Void> newFuture = new BukkitCompletableFuture<>(plugin);
        future.thenRun(() -> syncRun(() -> {
            action.run();
            newFuture.future.complete(null);
        }));
        return newFuture;
    }

    public BukkitCompletableFuture<Void> thenRunBukkitAsync(Runnable action) {
        BukkitCompletableFuture<Void> newFuture = new BukkitCompletableFuture<>(plugin);
        future.thenRun(() -> asyncRun(() -> {
            action.run();
            newFuture.future.complete(null);
        }));
        return newFuture;
    }

    public <U, V> BukkitCompletableFuture<V> thenCombineSync(CompletionStage<? extends U> other, BiFunction<? super T, ? super U, ? extends V> fn) {
        BukkitCompletableFuture<V> newFuture = new BukkitCompletableFuture<>(plugin);
        future.thenAcceptBoth(other, (t, u) -> syncRun(() -> newFuture.future.complete(fn.apply(t, u))));
        return newFuture;
    }

    public <U, V> BukkitCompletableFuture<V> thenCombineBukkitAsync(CompletionStage<? extends U> other, BiFunction<? super T, ? super U, ? extends V> fn) {
        BukkitCompletableFuture<V> newFuture = new BukkitCompletableFuture<>(plugin);
        future.thenAcceptBoth(other, (t, u) -> asyncRun(() -> newFuture.future.complete(fn.apply(t, u))));
        return newFuture;
    }

    public <U> BukkitCompletableFuture<Void> thenAcceptBothSync(CompletionStage<? extends U> other, BiConsumer<? super T, ? super U> action) {
        BukkitCompletableFuture<Void> newFuture = new BukkitCompletableFuture<>(plugin);
        future.thenAcceptBoth(other, (t, u) -> syncRun(() -> {
            action.accept(t, u);
            newFuture.future.complete(null);
        }));
        return newFuture;
    }

    public <U> BukkitCompletableFuture<Void> thenAcceptBukkitAsync(CompletionStage<? extends U> other, BiConsumer<? super T, ? super U> action) {
        BukkitCompletableFuture<Void> newFuture = new BukkitCompletableFuture<>(plugin);
        future.thenAcceptBoth(other, (t, u) -> asyncRun(() -> {
            action.accept(t, u);
            newFuture.future.complete(null);
        }));
        return newFuture;
    }

    public BukkitCompletableFuture<Void> runAfterBothSync(CompletionStage<?> other, Runnable action) {
        BukkitCompletableFuture<Void> newFuture = new BukkitCompletableFuture<>(plugin);
        future.runAfterBoth(other, () -> syncRun(() -> {
            action.run();
            newFuture.future.complete(null);
        }));
        return newFuture;
    }

    public BukkitCompletableFuture<Void> runAfterBothBukkitAsync(CompletionStage<?> other, Runnable action) {
        BukkitCompletableFuture<Void> newFuture = new BukkitCompletableFuture<>(plugin);
        future.runAfterBoth(other, () -> asyncRun(() -> {
            action.run();
            newFuture.future.complete(null);
        }));
        return newFuture;
    }

    public <U> BukkitCompletableFuture<U> applyToEitherSync(CompletionStage<? extends T> other, Function<? super T, U> fn) {
        BukkitCompletableFuture<U> newFuture = new BukkitCompletableFuture<>(plugin);
        future.acceptEither(other, t -> syncRun(() -> newFuture.future.complete(fn.apply(t))));
        return newFuture;
    }

    public <U> BukkitCompletableFuture<U> applyToEitherBukkitAsync(CompletionStage<? extends T> other, Function<? super T, U> fn) {
        BukkitCompletableFuture<U> newFuture = new BukkitCompletableFuture<>(plugin);
        future.acceptEither(other, t -> asyncRun(() -> newFuture.future.complete(fn.apply(t))));
        return newFuture;
    }

    public BukkitCompletableFuture<Void> acceptEitherSync(CompletionStage<? extends T> other, Consumer<? super T> action) {
        BukkitCompletableFuture<Void> newFuture = new BukkitCompletableFuture<>(plugin);
        future.acceptEither(other, t -> syncRun(() -> {
            action.accept(t);
            newFuture.future.complete(null);
        }));
        return newFuture;
    }

    public BukkitCompletableFuture<Void> acceptEitherBukkitAsync(CompletionStage<? extends T> other, Consumer<? super T> action) {
        BukkitCompletableFuture<Void> newFuture = new BukkitCompletableFuture<>(plugin);
        future.acceptEither(other, t -> asyncRun(() -> {
            action.accept(t);
            newFuture.future.complete(null);
        }));
        return newFuture;
    }

    public BukkitCompletableFuture<Void> runAfterEitherSync(CompletionStage<?> other, Runnable action) {
        BukkitCompletableFuture<Void> newFuture = new BukkitCompletableFuture<>(plugin);
        future.runAfterEither(other, () -> syncRun(() -> {
            action.run();
            newFuture.future.complete(null);
        }));
        return newFuture;
    }

    public BukkitCompletableFuture<Void> runAfterEitherBukkitAsync(CompletionStage<?> other, Runnable action) {
        BukkitCompletableFuture<Void> newFuture = new BukkitCompletableFuture<>(plugin);
        future.runAfterEither(other, () -> asyncRun(() -> {
            action.run();
            newFuture.future.complete(null);
        }));
        return newFuture;
    }

    public BukkitCompletableFuture<T> whenCompleteSync(BiConsumer<? super T, ? super Throwable> action) {
        BukkitCompletableFuture<T> newFuture = new BukkitCompletableFuture<>(plugin);
        //noinspection Duplicates
        future.whenComplete((t, throwable) -> syncRun(() -> {
            action.accept(t, throwable);

            if (throwable != null) {
                newFuture.future.completeExceptionally(throwable);
            } else {
                newFuture.future.complete(t);
            }
        }));
        return newFuture;
    }

    public BukkitCompletableFuture<T> whenCompleteBukkitAsync(BiConsumer<? super T, ? super Throwable> action) {
        BukkitCompletableFuture<T> newFuture = new BukkitCompletableFuture<>(plugin);
        //noinspection Duplicates
        future.whenComplete((t, throwable) -> asyncRun(() -> {
            action.accept(t, throwable);

            if (throwable != null) {
                newFuture.future.completeExceptionally(throwable);
            } else {
                newFuture.future.complete(t);
            }
        }));
        return newFuture;
    }

    public <U> BukkitCompletableFuture<U> handleSync(BiFunction<? super T, Throwable, ? extends U> fn) {
        BukkitCompletableFuture<U> newFuture = new BukkitCompletableFuture<>(plugin);
        future.whenComplete((t, throwable) -> syncRun(() -> newFuture.future.complete(fn.apply(t, throwable))));
        return newFuture;
    }

    public <U> BukkitCompletableFuture<U> handleBukkitAsync(BiFunction<? super T, Throwable, ? extends U> fn) {
        BukkitCompletableFuture<U> newFuture = new BukkitCompletableFuture<>(plugin);
        future.whenComplete((t, throwable) -> asyncRun(() -> newFuture.future.complete(fn.apply(t, throwable))));
        return newFuture;
    }
    // Stupid...
    // endregion

    // region Delegate
    @Override
    public boolean isDone() {
        return future.isDone();
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
        return future.get();
    }

    @Override
    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return future.get(timeout, unit);
    }

    public T join() {
        return future.join();
    }

    public T getNow(T valueIfAbsent) {
        return future.getNow(valueIfAbsent);
    }

    public boolean complete(T value) {
        return future.complete(value);
    }

    public boolean completeExceptionally(Throwable ex) {
        return future.completeExceptionally(ex);
    }

    @Override
    public <U> BukkitCompletableFuture<U> thenApply(Function<? super T, ? extends U> fn) {
        return wrap(future.thenApply(fn));
    }

    @Override
    public <U> BukkitCompletableFuture<U> thenApplyAsync(Function<? super T, ? extends U> fn) {
        return wrap(future.thenApplyAsync(fn));
    }

    @Override
    public <U> BukkitCompletableFuture<U> thenApplyAsync(Function<? super T, ? extends U> fn, Executor executor) {
        return wrap(future.thenApplyAsync(fn, executor));
    }

    @Override
    public BukkitCompletableFuture<Void> thenAccept(Consumer<? super T> action) {
        return wrap(future.thenAccept(action));
    }

    @Override
    public BukkitCompletableFuture<Void> thenAcceptAsync(Consumer<? super T> action) {
        return wrap(future.thenAcceptAsync(action));
    }

    @Override
    public BukkitCompletableFuture<Void> thenAcceptAsync(Consumer<? super T> action, Executor executor) {
        return wrap(future.thenAcceptAsync(action, executor));
    }

    @Override
    public BukkitCompletableFuture<Void> thenRun(Runnable action) {
        return wrap(future.thenRun(action));
    }

    @Override
    public BukkitCompletableFuture<Void> thenRunAsync(Runnable action) {
        return wrap(future.thenRunAsync(action));
    }

    @Override
    public BukkitCompletableFuture<Void> thenRunAsync(Runnable action, Executor executor) {
        return wrap(future.thenRunAsync(action, executor));
    }

    @Override
    public <U, V> BukkitCompletableFuture<V> thenCombine(CompletionStage<? extends U> other, BiFunction<? super T, ? super U, ? extends V> fn) {
        return wrap(future.thenCombine(other, fn));
    }

    @Override
    public <U, V> BukkitCompletableFuture<V> thenCombineAsync(CompletionStage<? extends U> other, BiFunction<? super T, ? super U, ? extends V> fn) {
        return wrap(future.thenCombineAsync(other, fn));
    }

    @Override
    public <U, V> BukkitCompletableFuture<V> thenCombineAsync(CompletionStage<? extends U> other, BiFunction<? super T, ? super U, ? extends V> fn, Executor executor) {
        return wrap(future.thenCombineAsync(other, fn, executor));
    }

    @Override
    public <U> BukkitCompletableFuture<Void> thenAcceptBoth(CompletionStage<? extends U> other, BiConsumer<? super T, ? super U> action) {
        return wrap(future.thenAcceptBoth(other, action));
    }

    @Override
    public <U> BukkitCompletableFuture<Void> thenAcceptBothAsync(CompletionStage<? extends U> other, BiConsumer<? super T, ? super U> action) {
        return wrap(future.thenAcceptBothAsync(other, action));
    }

    @Override
    public <U> BukkitCompletableFuture<Void> thenAcceptBothAsync(CompletionStage<? extends U> other, BiConsumer<? super T, ? super U> action, Executor executor) {
        return wrap(future.thenAcceptBothAsync(other, action, executor));
    }

    @Override
    public BukkitCompletableFuture<Void> runAfterBoth(CompletionStage<?> other, Runnable action) {
        return wrap(future.runAfterBoth(other, action));
    }

    @Override
    public BukkitCompletableFuture<Void> runAfterBothAsync(CompletionStage<?> other, Runnable action) {
        return wrap(future.runAfterBothAsync(other, action));
    }

    @Override
    public BukkitCompletableFuture<Void> runAfterBothAsync(CompletionStage<?> other, Runnable action, Executor executor) {
        return wrap(future.runAfterBothAsync(other, action, executor));
    }

    @Override
    public <U> BukkitCompletableFuture<U> applyToEither(CompletionStage<? extends T> other, Function<? super T, U> fn) {
        return wrap(future.applyToEither(other, fn));
    }

    @Override
    public <U> BukkitCompletableFuture<U> applyToEitherAsync(CompletionStage<? extends T> other, Function<? super T, U> fn) {
        return wrap(future.applyToEitherAsync(other, fn));
    }

    @Override
    public <U> BukkitCompletableFuture<U> applyToEitherAsync(CompletionStage<? extends T> other, Function<? super T, U> fn, Executor executor) {
        return wrap(future.applyToEitherAsync(other, fn, executor));
    }

    @Override
    public BukkitCompletableFuture<Void> acceptEither(CompletionStage<? extends T> other, Consumer<? super T> action) {
        return wrap(future.acceptEither(other, action));
    }

    @Override
    public BukkitCompletableFuture<Void> acceptEitherAsync(CompletionStage<? extends T> other, Consumer<? super T> action) {
        return wrap(future.acceptEitherAsync(other, action));
    }

    @Override
    public BukkitCompletableFuture<Void> acceptEitherAsync(CompletionStage<? extends T> other, Consumer<? super T> action, Executor executor) {
        return wrap(future.acceptEitherAsync(other, action, executor));
    }

    @Override
    public BukkitCompletableFuture<Void> runAfterEither(CompletionStage<?> other, Runnable action) {
        return wrap(future.runAfterEither(other, action));
    }

    @Override
    public BukkitCompletableFuture<Void> runAfterEitherAsync(CompletionStage<?> other, Runnable action) {
        return wrap(future.runAfterEitherAsync(other, action));
    }

    @Override
    public BukkitCompletableFuture<Void> runAfterEitherAsync(CompletionStage<?> other, Runnable action, Executor executor) {
        return wrap(future.runAfterEitherAsync(other, action, executor));
    }

    @Override
    public <U> BukkitCompletableFuture<U> thenCompose(Function<? super T, ? extends CompletionStage<U>> fn) {
        return wrap(future.thenCompose(fn));
    }

    @Override
    public <U> BukkitCompletableFuture<U> thenComposeAsync(Function<? super T, ? extends CompletionStage<U>> fn) {
        return wrap(future.thenComposeAsync(fn));
    }

    @Override
    public <U> BukkitCompletableFuture<U> thenComposeAsync(Function<? super T, ? extends CompletionStage<U>> fn, Executor executor) {
        return wrap(future.thenComposeAsync(fn, executor));
    }

    @Override
    public BukkitCompletableFuture<T> whenComplete(BiConsumer<? super T, ? super Throwable> action) {
        return wrap(future.whenComplete(action));
    }

    @Override
    public BukkitCompletableFuture<T> whenCompleteAsync(BiConsumer<? super T, ? super Throwable> action) {
        return wrap(future.whenCompleteAsync(action));
    }

    @Override
    public BukkitCompletableFuture<T> whenCompleteAsync(BiConsumer<? super T, ? super Throwable> action, Executor executor) {
        return wrap(future.whenCompleteAsync(action, executor));
    }

    @Override
    public <U> BukkitCompletableFuture<U> handle(BiFunction<? super T, Throwable, ? extends U> fn) {
        return wrap(future.handle(fn));
    }

    @Override
    public <U> BukkitCompletableFuture<U> handleAsync(BiFunction<? super T, Throwable, ? extends U> fn) {
        return wrap(future.handleAsync(fn));
    }

    @Override
    public <U> BukkitCompletableFuture<U> handleAsync(BiFunction<? super T, Throwable, ? extends U> fn, Executor executor) {
        return wrap(future.handleAsync(fn, executor));
    }

    @Override
    public CompletableFuture<T> toCompletableFuture() {
        return future.toCompletableFuture();
    }

    @Override
    public BukkitCompletableFuture<T> exceptionally(Function<Throwable, ? extends T> fn) {
        return wrap(future.exceptionally(fn));
    }

    public BukkitCompletableFuture<T> orTimeout(long timeout, TimeUnit unit) {
        return wrap(future.orTimeout(timeout, unit));
    }

    public <U> BukkitCompletableFuture<U> newIncompleteFuture() {
        return wrap(future.newIncompleteFuture());
    }

    public BukkitCompletableFuture<T> copy() {
        return wrap(future.copy());
    }

    public BukkitCompletableFuture<T> completeAsync(Supplier<? extends T> supplier, Executor executor) {
        return wrap(future.completeAsync(supplier, executor));
    }

    public BukkitCompletableFuture<T> completeAsync(Supplier<? extends T> supplier) {
        return wrap(future.completeAsync(supplier));
    }

    public BukkitCompletableFuture<T> completeOnTimeout(T value, long timeout, TimeUnit unit) {
        return wrap(future.completeOnTimeout(value, timeout, unit));
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return future.cancel(mayInterruptIfRunning);
    }

    @Override
    public boolean isCancelled() {
        return future.isCancelled();
    }

    public boolean isCompletedExceptionally() {
        return future.isCompletedExceptionally();
    }

    public void obtrudeValue(T value) {
        future.obtrudeValue(value);
    }

    public void obtrudeException(Throwable ex) {
        future.obtrudeException(ex);
    }

    public int getNumberOfDependents() {
        return future.getNumberOfDependents();
    }

    public Executor defaultExecutor() {
        return future.defaultExecutor();
    }

    public CompletionStage<T> minimalCompletionStage() {
        return future.minimalCompletionStage();
    }

    public static Executor delayedExecutor(long delay, TimeUnit unit, Executor executor) {
        return CompletableFuture.delayedExecutor(delay, unit, executor);
    }

    public static Executor delayedExecutor(long delay, TimeUnit unit) {
        return CompletableFuture.delayedExecutor(delay, unit);
    }

    public static <U> CompletionStage<U> completedStage(U value) {
        return CompletableFuture.completedStage(value);
    }

    public static <U> CompletableFuture<U> failedFuture(Throwable ex) {
        return CompletableFuture.failedFuture(ex);
    }

    public static <U> CompletionStage<U> failedStage(Throwable ex) {
        return CompletableFuture.failedStage(ex);
    }

    @Override
    public String toString() {
        return String.format("BukkitCompletableFuture{plugin=%s, future=%s}", plugin.getName(), future.toString());
    }
    // endregion
}
