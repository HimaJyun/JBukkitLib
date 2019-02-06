package jp.jyn.jbukkitlib.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class SubExecutor implements CommandExecutor, TabCompleter {
    private final Map<String, SubCommand> commands;
    private final String defaultCommand;
    private final ErrorExecutor errorExecutor;

    private SubExecutor(Builder builder) {
        this.commands = Collections.unmodifiableMap(builder.commands);
        this.defaultCommand = builder.defaultCommand;
        this.errorExecutor = builder.errorExecutor;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String cmd = defaultCommand;
        if (args.length == 0) {
            args = new String[]{defaultCommand};
        } else {
            cmd = lower(args[0]);
        }

        SubCommand sub = commands.get(cmd);
        if (sub == null) {
            return errorExecutor.onError(new ErrorExecutor.Info(
                ErrorExecutor.Cause.COMMAND_NOT_FOUND, cmd, null, sender, command, label, args
            ));
        }

        ErrorExecutor.Cause cause;
        switch (sub.execCommand(sender, args)) {
            case OK:
                return true;
            case ERROR:
                cause = ErrorExecutor.Cause.ERROR;
                break;
            case PLAYER_ONLY:
                cause = ErrorExecutor.Cause.PLAYER_ONLY;
                break;
            case MISSING_ARGUMENT:
                cause = ErrorExecutor.Cause.MISSING_ARGUMENT;
                break;
            case DONT_HAVE_PERMISSION:
                cause = ErrorExecutor.Cause.DONT_HAVE_PERMISSION;
                break;
            default:
                cause = ErrorExecutor.Cause.UNKNOWN;
                break;
        }
        return errorExecutor.onError(new ErrorExecutor.Info(cause, cmd, sub, sender, command, label, args));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 0) {
            return new ArrayList<>(commands.keySet());
        }

        if (args.length == 1) {
            return commands.keySet().stream()
                .filter(str -> str.startsWith(args[0]))
                .collect(Collectors.toList());
            // Note: "string".startsWith("") -> true
        }

        SubCommand sub = commands.get(lower(args[0]));
        if (sub == null) {
            return Collections.emptyList();
        }

        return sub.onTabComplete(sender, command, alias, args);
    }

    private static String lower(String str) {
        return str.toLowerCase(Locale.ENGLISH);
    }

    public static class Builder {
        private final Map<String, SubCommand> commands = new LinkedHashMap<>();

        private String defaultCommand = "";
        private ErrorExecutor errorExecutor = error -> false;

        public static Builder init() {
            return new Builder();
        }

        public Builder putCommand(String key, SubCommand command) {
            commands.put(lower(key), command);
            return this;
        }

        public Map<String, SubCommand> getSubCommands() {
            return Collections.unmodifiableMap(commands);
        }

        public Builder setDefaultCommand(String defaultCommand) {
            this.defaultCommand = defaultCommand;
            return this;
        }

        public Builder setErrorExecutor(ErrorExecutor errorExecutor) {
            this.errorExecutor = errorExecutor;
            return this;
        }

        public SubExecutor build() {
            return new SubExecutor(this);
        }

        public SubExecutor register(PluginCommand command) {
            SubExecutor executor = new SubExecutor(this);
            command.setExecutor(executor);
            command.setTabCompleter(executor);
            return executor;
        }
    }
}
