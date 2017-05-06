package network.palace.parkmanager.magicband;

import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.FoodLocation;
import network.palace.parkmanager.handlers.InventoryType;
import network.palace.parkmanager.utils.BandUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Created by Marc on 12/14/14
 */
public class FoodMenuClick {

    @SuppressWarnings("deprecation")
    public static void handle(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item == null) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        if (item.equals(BandUtil.getBackItem())) {
            ParkManager.inventoryUtil.openInventory(player, InventoryType.MAINMENU);
            return;
        }
        if (item.getItemMeta() == null) {
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta.getDisplayName() == null) {
            return;
        }
        String inv = ChatColor.stripColor(event.getInventory().getName());
        int page = Integer.parseInt(inv.replaceAll("Food Menu Page ", ""));
        String name = ChatColor.stripColor(meta.getDisplayName());
        if (name.equals("Next Page")) {
            ParkManager.inventoryUtil.openFoodMenuPage(player, page + 1);
            return;
        }
        if (name.equals("Last Page")) {
            ParkManager.inventoryUtil.openFoodMenuPage(player, page - 1);
            return;
        }
        for (FoodLocation loc : ParkManager.foodLocations) {
            if (item.getTypeId() == loc.getType() && item.getData().getData() == loc.getData() &&
                    name.equalsIgnoreCase(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', loc.getName())))) {
                player.closeInventory();
                player.performCommand("warp " + loc.getWarp());
                return;
            }
        }
    }
}