package jp.jyn.jbukkitlib.command;

import jp.jyn.jbukkitlib.util.EmptyDeque;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import java.util.Queue;

public abstract class SubCommand implements CommandExecutor, TabCompleter {
    private final static Deque<String> EMPTY_DEQUE = EmptyDeque.getInstanceException();

    public enum Result {OK, ERROR, PLAYER_ONLY, DONT_HAVE_PERMISSION, MISSING_ARGUMENT}

    @Override
    public final boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return execCommand(sender, args) == Result.OK;
    }

    public final Result execCommand(CommandSender sender, String... args) {
        boolean isPlayer = sender instanceof Player;
        // check player only mode.
        if (!isPlayer && isPlayerOnly()) {
            return Result.PLAYER_ONLY;
        }

        // check permission.
        if (!Optional.ofNullable(requirePermission()).map(sender::hasPermission).orElse(true)) {
            return Result.DONT_HAVE_PERMISSION;
        }

        // check args length
        if (args.length < (minimumArgs() + 1)) {
            return Result.MISSING_ARGUMENT;
        }

        // create args queue
        Queue<String> subArgs = argsDeque(args);
        if (isPlayer) {
            return execCommand((Player) sender, subArgs);
        } else {
            return execCommand(sender, subArgs);
        }
    }

    protected Result execCommand(Player sender, Queue<String> args) {
        return execCommand((CommandSender) sender, args);
    }

    protected Result execCommand(CommandSender sender, Queue<String> args) {
        throw new UnsupportedOperationException("This command is not implemented.");
    }

    @Override
    public final List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return execTabComplete(sender, argsDeque(args));
    }

    protected List<String> execTabComplete(CommandSender sender, Deque<String> args) {
        return Collections.emptyList();
    }

    private Deque<String> argsDeque(String... args) {
        if (args.length <= 1) {
            return EMPTY_DEQUE;
        }

        Deque<String> deque = new ArrayDeque<>(args.length - 1);
        for (int i = 1; i < args.length; i++) {
            deque.addLast(args[i]);
        }

        return deque;
    }

    protected boolean isPlayerOnly() {
        return false;
    }

    protected String requirePermission() {
        return null;
    }

    protected int minimumArgs() {
        return 0;
    }

    public CommandHelp getHelp() {
        return null;
    }

    protected final static class CommandHelp {
        public final String usage;
        public final String description;
        public final String[] example;

        public CommandHelp(String usage, String description, String... example) {
            this.usage = usage;
            this.description = description;
            this.example = example;
        }
    }
}
