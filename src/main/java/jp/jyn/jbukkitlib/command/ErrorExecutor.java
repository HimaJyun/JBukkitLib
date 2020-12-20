package jp.jyn.jbukkitlib.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * Processing when an error occurs in a subcommand
 */
@FunctionalInterface
public interface ErrorExecutor {
    /**
     * onError
     *
     * @param error error info
     * @return result a boolean to return with {@link org.bukkit.command.CommandExecutor#onCommand(CommandSender, Command, String, String[])}
     */
    boolean onError(Info error);

    /**
     * Error info.
     */
    class Info {
        /**
         * Cause of error.
         */
        public final SubCommand.Result cause;
        /**
         * <p>SubCommand that the player tried to execute.</p>
         * <p>Note: It is null if it is called with no argument specified when the default command is not set.</p>
         */
        public final String subArgs;
        /**
         * <p>SubCommand that the player tried to execute.</p>
         * <p>Note: It is null if no subcommand was found.</p>
         */
        public final SubCommand subCommand;
        /**
         * Command sender.
         */
        public final CommandSender sender;
        public final Command command;
        public final String label;
        /**
         * Command raw args.
         */
        public final String[] args;

        public Info(SubCommand.Result cause, String subArgs, SubCommand subCommand, CommandSender sender, Command command, String label, String[] args) {
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
