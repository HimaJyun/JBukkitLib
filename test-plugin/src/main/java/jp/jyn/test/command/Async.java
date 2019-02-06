package jp.jyn.test.command;

import jp.jyn.jbukkitlib.command.SubCommand;
import jp.jyn.jbukkitlib.util.BukkitCompletableFuture;
import jp.jyn.test.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.function.Consumer;

public class Async extends SubCommand {

    @Override
    protected Result execCommand(CommandSender sender, Queue<String> args) {
        BukkitCompletableFuture<String> future;
        if ("sync".equals(args.poll())) {
            future = BukkitCompletableFuture.completedFuture(Main.instance, "completed");
        } else {
            future = BukkitCompletableFuture.supplyAsync(Main.instance, () -> {
                out(sender, "supplyAsync");
                return "async";
            });
        }

        test(sender, future);
        return Result.OK;
    }

    private <T> void test(CommandSender sender, BukkitCompletableFuture<T> future) {
        future.thenApply(v -> {
            out(sender, v);
            return "thenApply";
        }).thenApplyAsync(v -> {
            out(sender, v);
            return "thenApplyAsync";
        }).thenApplySync(v -> {
            out(sender, v);
            return "thenApplySync";
        }).thenApply(v -> {
            out(sender, v);
            return "thenApply";
        });
    }

    private void out(CommandSender sender, Object obj) {
        List<CommandSender> senders = new ArrayList<>(2);
        senders.add(sender);
        Consumer<Object> send = v -> senders.forEach(s -> s.sendMessage(String.valueOf(v)));
        if (sender instanceof Player) {
            senders.add(Bukkit.getConsoleSender());
        }

        send.accept(obj);
        sleep(1000);
        send.accept(Thread.currentThread().toString());
        send.accept("Primary: " + Bukkit.isPrimaryThread());
    }

    private void sleep(long l) {
        try {
            Thread.sleep(l);
        } catch (InterruptedException ignore) { }
    }
}
