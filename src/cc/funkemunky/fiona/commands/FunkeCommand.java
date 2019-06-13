package cc.funkemunky.fiona.commands;

import cc.funkemunky.fiona.Fiona;
import cc.funkemunky.fiona.detections.Check;
import cc.funkemunky.fiona.utils.Color;
import cc.funkemunky.fiona.utils.JsonMessage;
import cc.funkemunky.fiona.utils.MathUtils;
import cc.funkemunky.fiona.utils.MiscUtils;
import com.google.common.collect.Lists;
import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public abstract class FunkeCommand
        implements CommandExecutor, TabCompleter {
    private static FunkeCommand instance;
    private final String name;
    private final String display;
    private final String permission;
    private final String description;
    private final List<FunkeArgument> arguments;

    protected FunkeCommand(String name, String display, String description, String permission) {
        this.name = name;
        this.display = display;
        this.permission = permission;
        this.description = description;
        this.arguments = new ArrayList<>();
        instance = this;
        Fiona.getInstance().getCommand(name).setExecutor(this);
        Fiona.getInstance().getCommand(name).setTabCompleter(this);
        this.addArguments();
    }

    public List<String> onTabComplete(CommandSender commandSender, Command command, String label, String[] args) {
        List<String> toReturn = new ArrayList<>();

        if (label.equalsIgnoreCase(name)) {
            final FunkeArgument[] funkeArgument = new FunkeArgument[1];
            arguments.forEach(argument -> {
                if (args.length == 1 && argument.getName().toLowerCase().startsWith(args[0].toLowerCase()) && !args[0].contains(argument.getName())) {
                    toReturn.add(argument.getName());
                }

                if (argument.getName().equalsIgnoreCase(args[0])) {
                    funkeArgument[0] = argument;
                } else if (getArgumentByAlias(args[0]) != null) {
                    funkeArgument[0] = getArgumentByAlias(args[0]);
                }
            });

            if (funkeArgument[0] != null) {
                funkeArgument[0].getTabComplete().getOrDefault(args.length, Lists.newArrayList()).forEach(string -> {
                    String[] split = string.split(",").length == 0 ? new String[]{string} : string.split(","), conditional = split.length > 1 ? new String[]{split[1], split[2]} : new String[0];

                    String arg = split[0];

                    if (conditional.length > 0) {
                        if (args[Integer.parseInt(conditional[1]) - 1].equalsIgnoreCase(conditional[0].replaceAll("!", "")) == !conditional[0].startsWith("!")) {
                            switch (arg.toLowerCase()) {
                                case "%check%":
                                    Fiona.getInstance().getCheckManager().getChecks().stream().filter(check -> check.getName().toLowerCase().replaceAll(" ", "_").startsWith(args[args.length - 1].toLowerCase())).forEach(check -> toReturn.add(check.getName().toLowerCase().replaceAll(" ", "_")));
                                    break;
                                case "%detection%":
                                    Check check = Fiona.getInstance().getCheckManager().getCheckByName(args[args.length - 2]);
                                    assert check != null;
                                    check.getDetections().stream().filter(detection -> detection.getId().toLowerCase().replaceAll(" ", "_").startsWith(args[args.length - 1])).forEach(detection -> toReturn.add(detection.getId().toLowerCase().replaceAll(" ", "_")));
                                    break;
                                default:
                                    if (arg.toLowerCase().startsWith(args[args.length - 1].toLowerCase())) {
                                        toReturn.add(arg);
                                    }
                                    break;
                            }
                        }
                    } else {
                        switch (arg.toLowerCase()) {
                            case "%check%":
                                Fiona.getInstance().getCheckManager().getChecks().stream().filter(check -> check.getName().toLowerCase().replaceAll(" ", "_").startsWith(args[args.length - 1].toLowerCase())).forEach(check -> toReturn.add(check.getName().toLowerCase().replaceAll(" ", "_")));
                                break;
                            case "%detection%":
                                Check check = Fiona.getInstance().getCheckManager().getCheckByName(args[args.length - 2]);
                                assert check != null;
                                check.getDetections().stream().filter(detection -> detection.getId().toLowerCase().replaceAll(" ", "_").startsWith(args[args.length - 1])).forEach(detection -> toReturn.add(detection.getId().toLowerCase().replaceAll(" ", "_")));
                                break;
                            default:
                                if (arg.toLowerCase().startsWith(args[args.length - 1].toLowerCase())) {
                                    toReturn.add(arg);
                                }
                                break;
                        }
                    }
                });
            }

        }
        return toReturn.size() == 0 ? null : toReturn;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (this.permission != null && !sender.hasPermission(this.permission)) {
            sender.sendMessage(getName().equalsIgnoreCase("fiona") ? Color.Red + "This server is using Fiona AntiCheat v" + Fiona.getInstance().getDescription().getVersion() + " by funkemunky, XtasyCode, and Martin." : Fiona.getInstance().getMessageFields().noPermission);
            return true;
        }
        try {
            int page = args.length > 0 ? Integer.parseInt(args[0]) : 1;
            sender.sendMessage(MiscUtils.line(Color.Dark_Gray));
            sender.sendMessage(Color.Gold + Color.Bold + this.display + Color.Yellow + " Command Help " + Color.White + "Page (" + page + " / " + (int) MathUtils.round(arguments.size() / 6D) + ")");
            sender.sendMessage("");
            sender.sendMessage(Color.translate("&b<> &7= required. &b[] &7= optional."));
            sender.sendMessage("");
            if (sender instanceof Player) {
                for (int i = (page - 1) * 6; i < Math.min(page * 6, arguments.size()); i++) {
                    FunkeArgument argument = arguments.get(i);
                    JsonMessage message = new JsonMessage();

                    StringBuilder aliasesFormatted = new StringBuilder();
                    List<String> aliases = argument.getAliases();
                    if (aliases.size() > 0) {
                        for (String aliase : aliases) {
                            aliasesFormatted.append(Color.White).append(aliase).append(Color.Gray).append(", ");
                        }
                        int length = aliasesFormatted.length();
                        aliasesFormatted = new StringBuilder(aliasesFormatted.substring(0, length - 2));
                    } else {
                        aliasesFormatted = new StringBuilder(Color.Red + "None");
                    }


                    String hoverText = Color.translate((argument.getPermission().length > 1 ? "&6Permissions&7: &f" + Arrays.toString(argument.getPermission()) : "&6Permission&7: &f" + argument.getPermission()[0])
                            + "\n&6Aliases&7: " + aliasesFormatted);
                    message.addText(Color.Gray + "/" + label.toLowerCase() + Color.White + " " + argument.getDisplay() + Color.Gray + " to " + argument.getDescription()).addHoverText(hoverText);
                    message.sendToPlayer((Player) sender);
                }
            } else {
                for (int i = (page - 1) * 6; i < Math.min(arguments.size(), page * 6); i++) {
                    FunkeArgument argument = arguments.get(i);
                    sender.sendMessage(Color.Gray + "/" + label.toLowerCase() + Color.White + " " + argument.getDisplay() + Color.Gray + " to " + argument.getDescription());
                }
            }
            sender.sendMessage(MiscUtils.line(Color.Dark_Gray));
        } catch (Exception e) {
            for (FunkeArgument argument : this.arguments) {

                if (!args[0].equalsIgnoreCase(argument.getName()) && !argument.getAliases().contains(args[0].toLowerCase()))
                    continue;

                if ((argument.getPermission() == null || sender.hasPermission("fiona.admin")
                        || sender.hasPermission(permission))) {
                    argument.onArgument(sender, cmd, args);
                    break;
                }
                sender.sendMessage(Fiona.getInstance().getMessageFields().noPermission);
                break;
            }
        }
        return true;
    }

    private FunkeArgument getArgumentByAlias(String alias) {
        return arguments.stream().filter(arg -> arg.getAliases().stream().anyMatch(alias2 -> alias2.equalsIgnoreCase(alias))).findFirst().orElse(null);
    }

    protected abstract void addArguments();
}

