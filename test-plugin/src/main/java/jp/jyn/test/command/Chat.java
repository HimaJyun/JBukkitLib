package jp.jyn.test.command;

import jp.jyn.jbukkitlib.command.SubCommand;
import jp.jyn.jbukkitlib.config.parser.template.ComponentParser;
import jp.jyn.jbukkitlib.util.MapBuilder;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.function.Consumer;
import java.util.function.Function;

public class Chat extends SubCommand {
    private final List<ComponentParser> parsers = new ArrayList<>();
    private final Map<String, Function<Player, Consumer<Queue<String>>>> options =
        MapBuilder.initUnmodifiableMap(new HashMap<>(), m -> {
            m.put("show", p -> s -> show(p, s));
            m.put("set", p -> s -> set(p, s));
            m.put("hover", p -> s -> hoverText(p, s));
            m.put("url", p -> s -> clickOpenURL(p, s));
            m.put("suggest", p -> s -> clickSuggestCommand(p, s));
            m.put("run", p -> s -> clickRunCommand(p, s));
            m.put("help", p -> s -> help(p));
        });

    @Override
    protected Result execCommand(Player sender, Queue<String> args) {
        Function<Player, Consumer<Queue<String>>> option = null;
        if (!args.isEmpty()) {
            option = options.get(args.peek().toLowerCase(Locale.ENGLISH));
        }

        if (option != null) {
            args.remove();
            option.apply(sender).accept(args);
            return Result.OK;
        }

        ComponentParser parser = ComponentParser.parse(String.join(" ", args));
        parsers.add(parser);
        parser.send(sender);
        sender.sendMessage("ID: " + (parsers.size() - 1));

        return Result.OK;
    }

    private void help(Player player) {
        player.sendMessage("/test chat <Value>");
        player.sendMessage("/test chat show <ID>");
        player.sendMessage("/test chat set <ID> <Name> <Value>");
        player.sendMessage("/test chat hover <ID> <Name> <Value>");
        player.sendMessage("/test chat url <ID> <Name> <Value>");
        player.sendMessage("/test chat run <ID> <Name> <Value>");
        player.sendMessage("/test chat suggest <ID> <Name> <Value>");
    }

    private void show(Player player, Queue<String> args) {
        if (args.size() < 1) {
            player.sendMessage("/test chat show <ID>");
            return;
        }
        ComponentParser parser = getParser(args.remove());
        if (parser == null) {
            player.sendMessage("Parser not found");
            return;
        }

        parser.send(player);
    }

    private void set(Player player, Queue<String> args) {
        if (args.size() < 3) {
            player.sendMessage("/test chat set <ID> <Name> <Value>");
            return;
        }
        ComponentParser parser = getParser(args.remove());
        if (parser == null) {
            player.sendMessage("Parser not found");
            return;
        }

        parser.setVariable(args.remove(), String.join(" ", args));
        parser.send(player);
    }

    private void hoverText(Player player, Queue<String> args) {
        if (args.size() < 3) {
            player.sendMessage("/test chat hover <ID> <Name> <Value>");
            return;
        }
        ComponentParser parser = getParser(args.remove());
        if (parser == null) {
            player.sendMessage("Parser not found");
            return;
        }

        parser.getVariable(args.remove()).ifPresent(c -> c.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder(String.join(" ", args)).create()
            ))
        );
        parser.send(player);
    }

    private void clickOpenURL(Player player, Queue<String> args) {
        if (args.size() < 3) {
            player.sendMessage("/test chat url <ID> <Name> <Value>");
            return;
        }
        ComponentParser parser = getParser(args.remove());
        if (parser == null) {
            player.sendMessage("Parser not found");
            return;
        }

        parser.getVariable(args.remove()).ifPresent(c -> c.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,
            String.join(" ", args)
        )));
        parser.send(player);
    }

    private void clickRunCommand(Player player, Queue<String> args) {
        if (args.size() < 3) {
            player.sendMessage("/test chat run <ID> <Name> <Value>");
            return;
        }
        ComponentParser parser = getParser(args.remove());
        if (parser == null) {
            player.sendMessage("Parser not found");
            return;
        }

        parser.getVariable(args.remove()).ifPresent(c -> c.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
            String.join(" ", args)
        )));
        parser.send(player);
    }

    private void clickSuggestCommand(Player player, Queue<String> args) {
        if (args.size() < 3) {
            player.sendMessage("/test chat suggest <ID> <Name> <Value>");
            return;
        }
        ComponentParser parser = getParser(args.remove());
        if (parser == null) {
            player.sendMessage("Parser not found");
            return;
        }

        parser.getVariable(args.remove()).ifPresent(c -> c.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
            String.join(" ", args)
        )));
        parser.send(player);
    }

    private ComponentParser getParser(String str) {
        try {
            return parsers.get(Integer.parseInt(str));
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    @Override
    protected boolean isPlayerOnly() {
        return true;
    }
}
