package network.palace.parkmanager.commands;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CommandPermission;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.Rank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandMeta(description = "Set player time")
@CommandPermission(rank = Rank.MOD)
public class Commandptime extends CoreCommand {

    public Commandptime() {
        super("ptime");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof Player)) {
            if (args.length == 2) {
                Player tp = Bukkit.getPlayer(args[1]);
                if (tp == null) {
                    sender.sendMessage(ChatColor.RED + "Player not found!");
                    return;
                }
                args[0] = args[0].toLowerCase().replaceAll("ticks", "");
                switch (args[0]) {
                    case "day":
                        tp.setPlayerTime(1000, false);
                        sender.sendMessage(ChatColor.DARK_AQUA + tp.getName()
                                + "'s " + ChatColor.GREEN + "time has been set to "
                                + ChatColor.DARK_AQUA + "1000" + ChatColor.GREEN
                                + "!");
                        break;
                    case "noon":
                        tp.setPlayerTime(6000, false);
                        sender.sendMessage(ChatColor.DARK_AQUA + tp.getName()
                                + "'s " + ChatColor.GREEN + "time has been set to "
                                + ChatColor.DARK_AQUA + "6000" + ChatColor.GREEN
                                + "!");
                        break;
                    case "night":
                        tp.setPlayerTime(16000, false);
                        sender.sendMessage(ChatColor.DARK_AQUA + tp.getName()
                                + "'s " + ChatColor.GREEN + "time has been set to "
                                + ChatColor.DARK_AQUA + "16000" + ChatColor.GREEN
                                + "!");
                        break;
                    case "reset":
                        tp.resetPlayerTime();
                        sender.sendMessage(ChatColor.DARK_AQUA + tp.getName()
                                + "'s " + ChatColor.GREEN
                                + "time now matches the server.");
                        break;
                    default:
                        if (isInt(args[0])) {
                            int time = Integer.parseInt(args[0]);
                            tp.setPlayerTime(time, false);
                            sender.sendMessage(ChatColor.DARK_AQUA + tp.getName()
                                    + "'s " + ChatColor.GREEN
                                    + "time has been set to " + ChatColor.DARK_AQUA
                                    + time + ChatColor.GREEN
                                    + "!");
                        } else {
                            sender.sendMessage(ChatColor.RED
                                    + "/ptime [day/noon/night/1000/reset] [Username]");
                        }
                        break;
                }
                return;
            }
            sender.sendMessage(ChatColor.RED + "/ptime [day/noon/night/1000/reset] [Username]");
            return;
        }
        Player player = (Player) sender;
        if (args.length == 1) {
            args[0] = args[0].toLowerCase().replaceAll("ticks", "");
            switch (args[0]) {
                case "day":
                    player.setPlayerTime(1000, false);
                    player.sendMessage(ChatColor.DARK_AQUA + player.getName()
                            + "'s " + ChatColor.GREEN + "time has been set to "
                            + ChatColor.DARK_AQUA + "1000" + ChatColor.GREEN + "!");
                    break;
                case "noon":
                    player.setPlayerTime(6000, false);
                    player.sendMessage(ChatColor.DARK_AQUA + player.getName()
                            + "'s " + ChatColor.GREEN + "time has been set to "
                            + ChatColor.DARK_AQUA + "6000" + ChatColor.GREEN + "!");
                    break;
                case "night":
                    player.setPlayerTime(16000, false);
                    player.sendMessage(ChatColor.DARK_AQUA + player.getName()
                            + "'s " + ChatColor.GREEN + "time has been set to "
                            + ChatColor.DARK_AQUA + "16000" + ChatColor.GREEN + "!");
                    break;
                case "reset":
                    player.resetPlayerTime();
                    player.sendMessage(ChatColor.GREEN
                            + "Your time now matches the server.");
                    break;
                default:
                    if (isInt(args[0])) {
                        player.setPlayerTime(Integer.parseInt(args[0]), false);
                        player.sendMessage(ChatColor.DARK_AQUA + player.getName()
                                + "'s " + ChatColor.GREEN + "time has been set to "
                                + ChatColor.DARK_AQUA + Integer.parseInt(args[0])
                                + ChatColor.GREEN + "!");
                    } else {
                        player.sendMessage(ChatColor.RED
                                + "/ptime [day/noon/night/1000/reset] [Username]");
                    }
                    break;
            }
            return;
        }
        if (args.length == 2) {
            Player tp = Bukkit.getPlayer(args[1]);
            if (tp == null) {
                player.sendMessage(ChatColor.RED + "Player not found!");
                return;
            }
            args[0] = args[0].toLowerCase().replaceAll("ticks", "");
            switch (args[0]) {
                case "day":
                    tp.setPlayerTime(1000, false);
                    player.sendMessage(ChatColor.DARK_AQUA + tp.getName() + "'s "
                            + ChatColor.GREEN + "time has been set to "
                            + ChatColor.DARK_AQUA + "1000" + ChatColor.GREEN + "!");
                    break;
                case "noon":
                    tp.setPlayerTime(6000, false);
                    player.sendMessage(ChatColor.DARK_AQUA + tp.getName() + "'s "
                            + ChatColor.GREEN + "time has been set to "
                            + ChatColor.DARK_AQUA + "6000" + ChatColor.GREEN + "!");
                    break;
                case "night":
                    tp.setPlayerTime(16000, false);
                    player.sendMessage(ChatColor.DARK_AQUA + tp.getName() + "'s "
                            + ChatColor.GREEN + "time has been set to "
                            + ChatColor.DARK_AQUA + "16000" + ChatColor.GREEN + "!");
                    break;
                case "reset":
                    tp.resetPlayerTime();
                    player.sendMessage(ChatColor.DARK_AQUA + tp.getName() + "'s "
                            + ChatColor.GREEN + "time now matches the server.");
                    break;
                default:
                    if (isInt(args[0])) {
                        tp.setPlayerTime(Integer.parseInt(args[0]), false);
                        player.sendMessage(ChatColor.DARK_AQUA + tp.getName()
                                + "'s " + ChatColor.GREEN + "time has been set to "
                                + ChatColor.DARK_AQUA + Integer.parseInt(args[0])
                                + ChatColor.GREEN + "!");
                    } else {
                        player.sendMessage(ChatColor.RED + "/ptime [day/noon/night/1000/reset] [Username]");
                    }
                    break;
            }
            return;
        }
        player.sendMessage(ChatColor.RED + "/ptime [day/noon/night/1000/reset] [Username]");
    }

    private static boolean isInt(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException ignored) {
            return false;
        }
    }
}
