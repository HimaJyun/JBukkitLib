package jp.jyn.jbukkitlib.command;

import jp.jyn.jbukkitlib.util.EmptyDeque;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Queue;
import java.util.function.BiFunction;

/**
 * SubCommand class
 */
public abstract class SubCommand implements TabExecutor {
    private final static Deque<String> EMPTY_DEQUE = EmptyDeque.getInstanceException();

    /**
     * Execution result
     */
    public enum Result {
        OK, ERROR, UNKNOWN_COMMAND,
        PLAYER_ONLY, DONT_HAVE_PERMISSION, MISSING_ARGUMENT
    }

    @Override
    public final boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return onCommand(sender, args) == Result.OK;
    }

    /**
     * Execute the command. The check is executed as necessary.
     *
     * @param sender Sender
     * @param args   args
     * @return Result
     */
    public final Result onCommand(CommandSender sender, String... args) {
        // check player only mode.
        if (isPlayerOnly() && !(sender instanceof Player)) {
            return Result.PLAYER_ONLY;
        }

        // check permission.
        String permission = requirePermission();
        if (permission != null && !sender.hasPermission(permission)) {
            return Result.DONT_HAVE_PERMISSION;
        }

        // check args length
        if (args.length < (minimumArgs() + 1)) {
            return Result.MISSING_ARGUMENT;
        }

        return onCommand(sender, argsDeque(args));
    }

    /**
     * <p>Execute the command.</p>
     *
     * @param sender Sender
     * @param args   args
     * @return Result
     */
    abstract protected Result onCommand(CommandSender sender, Queue<String> args);

    @Override
    public final List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return onTabComplete(sender, argsDeque(args));
    }

    protected List<String> onTabComplete(CommandSender sender, Deque<String> args) {
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

    /**
     * Is this command exclusively for players?
     *
     * @return If true then for players only
     */
    protected boolean isPlayerOnly() {
        return false;
    }

    /**
     * Required Permissions
     *
     * @return Permission
     */
    protected String requirePermission() {
        return null;
    }

    /**
     * The minimum number of arguments required for execution
     *
     * @return minimum args.
     */
    protected int minimumArgs() {
        return 0;
    }

    /**
     * Get command help
     *
     * @return help
     */
    public CommandHelp getHelp() {
        return null;
    }

    /**
     * Command Help.
     */
    public static class CommandHelp {
        /**
         * Command usage.
         */
        public final String usage;
        /**
         * Command description.
         */
        public final String description;
        /**
         * Command example.
         */
        public final String[] example;

        public CommandHelp(String usage, String description, String... example) {
            this.usage = usage;
            this.description = description;
            this.example = example;
        }
    }

    /**
     * Initialize command with lambda.
     *
     * @param command Command
     * @return SubCommand
     */
    public static SubCommand withCommand(BiFunction<CommandSender, Queue<String>, Result> command) {
        return Lambda.init().setCommand(command).build();
    }

    public static class Lambda {
        private BiFunction<CommandSender, Queue<String>, Result> command = (s, a) -> Result.ERROR;
        private BiFunction<CommandSender, Deque<String>, List<String>> tabComplete = (s, a) -> Collections.emptyList();
        private boolean playerOnly = false;
        private String requirePermission = null;
        private int minimumArgs = 0;
        private CommandHelp commandHelp = null;

        public static Lambda init() {
            return new Lambda();
        }

        public Lambda setCommand(BiFunction<CommandSender, Queue<String>, Result> command) {
            this.command = command;
            return this;
        }

        public Lambda setTabComplete(BiFunction<CommandSender, Deque<String>, List<String>> tabComplete) {
            this.tabComplete = tabComplete;
            return this;
        }

        public Lambda setPlayerOnly(boolean playerOnly) {
            this.playerOnly = playerOnly;
            return this;
        }

        public Lambda setRequirePermission(String requirePermission) {
            this.requirePermission = requirePermission;
            return this;
        }

        public Lambda setMinimumArgs(int minimumArgs) {
            this.minimumArgs = minimumArgs;
            return this;
        }

        public Lambda setCommandHelp(CommandHelp commandHelp) {
            this.commandHelp = commandHelp;
            return this;
        }

        public SubCommand build() {
            return new LambdaWrapper(this);
        }
    }

    private final static class LambdaWrapper extends SubCommand {
        private final Lambda lambda;

        public LambdaWrapper(Lambda lambda) {
            this.lambda = lambda;
        }

        @Override
        protected Result onCommand(CommandSender sender, Queue<String> args) {
            return lambda.command.apply(sender, args);
        }

        @Override
        protected List<String> onTabComplete(CommandSender sender, Deque<String> args) {
            return lambda.tabComplete.apply(sender, args);
        }

        @Override
        protected boolean isPlayerOnly() {
            return lambda.playerOnly;
        }

        @Override
        protected String requirePermission() {
            return lambda.requirePermission;
        }

        @Override
        protected int minimumArgs() {
            return lambda.minimumArgs;
        }

        @Override
        public CommandHelp getHelp() {
            return lambda.commandHelp;
        }
    }
}
