package jp.jyn.jbukkitlib.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

@FunctionalInterface
public interface ErrorExecutor {
    enum Cause {ERROR, COMMAND_NOT_FOUND, PLAYER_ONLY, DONT_HAVE_PERMISSION, MISSING_ARGUMENT, UNKNOWN}

    boolean onError(Info error);

    class Info {
        public final Cause cause;
        public final String subArgs;
        public final SubCommand subCommand;
        public final CommandSender sender;
        public final Command command;
        public final String label;
        public final String[] args;

        public Info(Cause cause, String subArgs, SubCommand subCommand, CommandSender sender, Command command, String label, String[] args) {
            this.cause = cause;
            this.subArgs = subArgs;
            this.subCommand = subCommand;
            this.sender = sender;
            this.command = command;
            this.label = label;
            this.args = args;
        }
    }
}
