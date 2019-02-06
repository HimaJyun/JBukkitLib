package jp.jyn.jbukkitlib.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * Processing when an error occurs in a subcommand
 */
@FunctionalInterface
public interface ErrorExecutor {
    /**
     * Cause of error.
     */
    enum Cause {
        ERROR, COMMAND_NOT_FOUND,
        PLAYER_ONLY, DONT_HAVE_PERMISSION, MISSING_ARGUMENT,
        NOT_IMPLEMENTED, UNKNOWN
    }

    /**
     * onError
     *
     * @param error error info
     * @return result a boolean to return with onCommand
     */
    boolean onError(Info error);

    /**
     * Error info.
     */
    class Info {
        /**
         * Cause of error.
         */
        public final Cause cause;
        /**
         * The subcommand you attempted to execute
         */
        public final String subArgs;
        /**
         * <p>The subcommand you attempted to execute</p>
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
