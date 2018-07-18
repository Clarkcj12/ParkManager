package network.palace.parkmanager.commands;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CommandPermission;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.Rank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;

@CommandMeta(description = "Spawn lightning")
@CommandPermission(rank = Rank.DEVELOPER)
public class SmiteCommand extends CoreCommand {

    public SmiteCommand() {
        super("smite");
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof Player)) {
            if (args.length == 1) {
                Player tp = Bukkit.getPlayer(args[0]);
                if (tp == null) {
                    sender.sendMessage(ChatColor.RED + "Player not found!");
                    return;
                }
                strike(tp);
                return;
            }
            sender.sendMessage(ChatColor.RED + "/smite [Username]");
            return;
        }
        Player player = (Player) sender;
        if (args.length == 1) {
            Player tp = Bukkit.getPlayer(args[0]);
            if (tp == null) {
                player.sendMessage(ChatColor.RED + "Player not found!");
                return;
            }
            player.sendMessage(ChatColor.GRAY + "Smiting " + tp.getName());
            strike(tp);
            return;
        }
        player.getWorld().strikeLightning(player.getTargetBlock((Set<Material>) null, 50).getLocation());
    }

    private void strike(Player tp) {
        tp.getWorld().strikeLightning(tp.getLocation());
        tp.sendMessage(ChatColor.GRAY + "Thou hast been smitted!");
    }
}