package network.palace.parkmanager.magicband;

import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.InventoryType;
import network.palace.parkmanager.handlers.Ride;
import network.palace.parkmanager.utils.BandUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Created by Marc on 3/27/16
 */
public class MeetAndGreetListClick {

    public static void handle(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item == null) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        if (item.equals(BandUtil.getBackItem())) {
            ParkManager.inventoryUtil.openInventory(player, InventoryType.RIDESANDATTRACTIONS);
            return;
        }
        if (item.getItemMeta() == null) {
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta.getDisplayName() == null) {
            return;
        }
        String name = ChatColor.stripColor(meta.getDisplayName());
        if (meta.getDisplayName().equals(ChatColor.RED + "Uh oh!")) {
            player.closeInventory();
            player.sendMessage(ChatColor.RED + "Sorry, but there are no Meet & Greets on this server!");
            return;
        }
        Ride meetandgreet = ParkManager.getMeetAndGreet(name);
        if (meetandgreet == null) {
            player.closeInventory();
            player.sendMessage(ChatColor.RED + "There was an error, please tell a Staff Member!");
            return;
        }
        player.closeInventory();
        player.performCommand("warp " + meetandgreet.getWarp());
    }
}
