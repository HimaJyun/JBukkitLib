package jp.jyn.test.command;

import jp.jyn.jbukkitlib.command.SubCommand;
import jp.jyn.jbukkitlib.config.parser.ItemStackParser;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Queue;

public class Item extends SubCommand {
    @Override
    protected Result execCommand(Player sender, Queue<String> args) {
        if (args.isEmpty()) {
            sender.sendMessage(
                ItemStackParser.toString(sender.getInventory().getItemInMainHand())
            );
            return Result.OK;
        }

        ItemStack itemStack = ItemStackParser.parse(args.remove());
        sender.getInventory().addItem(itemStack);
        return Result.OK;
    }

    @Override
    protected boolean isPlayerOnly() {
        return true;
    }
}
