package network.palace.parkmanager.magicband;

import network.palace.parkmanager.ParkManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Created by Marc on 5/29/15
 */
public class ShopConfirmClick {

    public static void handle(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item == null) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }
        String name = ChatColor.stripColor(meta.getDisplayName());
        switch (name) {
            case "Confirm Purchase":
                ParkManager.getInstance().getShopManager().confirmPurchase(player);
                return;
            case "Cancel Purchase":
                ParkManager.getInstance().getShopManager().cancelPurchase(player);
                player.closeInventory();
        }
    }
}