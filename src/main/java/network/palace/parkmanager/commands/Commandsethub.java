package network.palace.parkmanager.commands;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CommandPermission;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.utils.FileUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;

/**
 * Created by Marc on 3/10/15
 */
@CommandMeta(description = "Set hub location")
@CommandPermission(rank = Rank.WIZARD)
public class Commandsethub extends CoreCommand {

    public Commandsethub() {
        super("sethub");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        Location loc = player.getLocation();
        ParkManager.getInstance().setHub(loc);
        FileUtil.setHub(loc);
        player.sendMessage(ChatColor.DARK_AQUA + "The hub location has been set!");
    }
}