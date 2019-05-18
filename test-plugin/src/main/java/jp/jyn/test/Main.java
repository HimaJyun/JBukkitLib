package jp.jyn.test;

import jp.jyn.jbukkitlib.JBukkitLib;
import jp.jyn.jbukkitlib.command.SubCommand;
import jp.jyn.jbukkitlib.command.SubExecutor;
import jp.jyn.test.command.Async;
import jp.jyn.test.command.Chat;
import jp.jyn.test.command.Item;
import jp.jyn.test.command.UUID;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.stream.Stream;

public class Main extends JavaPlugin {
    public static Main instance = null;

    @Override
    public void onEnable() {
        instance = this;
        getInfo().forEach(getLogger()::info);

        SubExecutor.Builder.init()
            .setDefaultCommand("info")
            .putCommand("uuid", new UUID())
            .putCommand("info", SubCommand.withCommand((sender, strings) -> {
                getInfo().forEach(sender::sendMessage);
                return SubCommand.Result.OK;
            }))
            .putCommand("async", new Async())
            .putCommand("item", new Item())
            .putCommand("chat", new Chat())
            .putCommand("lambda", SubCommand.Lambda.init().setCommand((sender, strings) -> {
                sender.sendMessage("Console");
                return SubCommand.Result.OK;
            }).setPlayerCommand((player, strings) -> {
                player.sendMessage("Player");
                return SubCommand.Result.OK;
            }).build())
            .register(getCommand("test"));
    }

    private Stream<String> getInfo() {
        return Stream.of(
            "Name: " + JBukkitLib.NAME,
            "Version: " + JBukkitLib.VERSION,
            "BuildTime: " + JBukkitLib.BUILD_TIME,
            "ApiVersion: " + JBukkitLib.API_VERSION,
            "URL: " + JBukkitLib.URL,
            "Issue: " + JBukkitLib.ISSUE_URL,
            "GitURL: " + JBukkitLib.GIT_URL,
            "Commit: " + JBukkitLib.GIT_COMMIT
        );
    }
}
