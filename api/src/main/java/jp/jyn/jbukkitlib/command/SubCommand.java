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
import java.util.function.BiFunction;

/**
 * Subcommand class
 */
public abstract class SubCommand implements CommandExecutor, TabCompleter {
    private final static Deque<String> EMPTY_DEQUE = EmptyDeque.getInstanceException();

    /**
     * Execution result
     */
    public enum Result {
        OK, ERROR,
        PLAYER_ONLY, DONT_HAVE_PERMISSION, MISSING_ARGUMENT,
        NOT_IMPLEMENTED
    }

    @Override
    public final boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return execCommand(sender, args) == Result.OK;
    }

    /**
     * Execute the command. The check is executed as necessary.
     *
     * @param sender Sender
     * @param args   args
     * @return Result
     */
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

    /**
     * <p>Process when the player executes a command</p>
     * <p>When not overwriting this method, the processing for the console is used.</p>
     *
     * @param sender Player
     * @param args   args
     * @return Result
     */
    protected Result execCommand(Player sender, Queue<String> args) {
        return execCommand((CommandSender) sender, args);
    }

    /**
     * <p>Processing when command is executed from the console</p>
     * <p>If the processing for the player is not implemented, it is also called by the command execution by the player.</p>
     *
     * @param sender Sender
     * @param args   args
     * @return Result
     */
    protected Result execCommand(CommandSender sender, Queue<String> args) {
        return Result.NOT_IMPLEMENTED;
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
     * Initialize player specific commands using lambda.
     *
     * @param playerCommand Command lambda.
     * @return SubCommand
     */
    public static SubCommand withPlayerCommand(BiFunction<Player, Queue<String>, Result> playerCommand) {
        return Lambda.init().setPlayerOnly(true).setPlayerCommand(playerCommand).build();
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
        private BiFunction<Player, Queue<String>, Result> playerCommand = null;
        private BiFunction<CommandSender, Queue<String>, Result> command = (s, a) -> Result.NOT_IMPLEMENTED;
        private BiFunction<CommandSender, Deque<String>, List<String>> tabComplete = (s, a) -> Collections.emptyList();
        private boolean playerOnly = false;
        private String requirePermission = null;
        private int minimumArgs = 0;
        private CommandHelp commandHelp = null;

        public static Lambda init() {
            return new Lambda();
        }

        public Lambda setPlayerCommand(BiFunction<Player, Queue<String>, Result> playerCommand) {
            this.playerCommand = playerCommand;
            return this;
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
        protected Result execCommand(Player sender, Queue<String> args) {
            if (lambda.playerCommand == null) {
                return this.execCommand((CommandSender) sender, args);
            }
            return lambda.playerCommand.apply(sender, args);
        }

        @Override
        protected Result execCommand(CommandSender sender, Queue<String> args) {
            return lambda.command.apply(sender, args);
        }

        @Override
        protected List<String> execTabComplete(CommandSender sender, Deque<String> args) {
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
