package jp.jyn.test.command;

import jp.jyn.jbukkitlib.command.SubCommand;
import jp.jyn.jbukkitlib.uuid.UUIDRegistry;
import jp.jyn.test.Main;
import org.bukkit.command.CommandSender;

import java.util.Queue;

public class UUID extends SubCommand {
    @Override
    protected Result execCommand(CommandSender sender, Queue<String> args) {
        UUIDRegistry registry = UUIDRegistry.getSharedCacheRegistry(Main.instance);
        sender.sendMessage(String.join(", ", args));
        registry.getMultipleUUIDAsync(args)
            .thenAccept(v -> v.forEach(
                (name, uuid) -> sender.sendMessage(String.format("%s: %s", name, uuid))
            ));
        sender.sendMessage("jbukkitlib.UUIDRegistry#nameToUUIDCache");
        sender.sendMessage(System.getProperties().get("jbukkitlib.UUIDRegistry#nameToUUIDCache").toString());
        return Result.OK;
    }
}
