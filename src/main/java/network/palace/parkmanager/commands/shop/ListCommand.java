package network.palace.parkmanager.commands.shop;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.Park;
import network.palace.parkmanager.handlers.shop.Shop;
import org.bukkit.ChatColor;

@CommandMeta(description = "List all shops")
public class ListCommand extends CoreCommand {

    public ListCommand() {
        super("list");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        Park park = ParkManager.getParkUtil().getPark(player.getLocation());
        if (park == null) {
            player.sendMessage(ChatColor.RED + "You must be inside a park when running this command!");
            return;
        }
        player.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + park.getId().getTitle() + ChatColor.GREEN + " Shops:");
        for (Shop shop : ParkManager.getShopManager().getShops(park.getId())) {
            player.sendMessage(ChatColor.AQUA + "- [" + shop.getId() + "] " + ChatColor.YELLOW + shop.getName() + ChatColor.GREEN + " at " + ChatColor.YELLOW + "/warp " + shop.getWarp());
        }
    }
}
