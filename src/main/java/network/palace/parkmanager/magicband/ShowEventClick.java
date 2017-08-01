package network.palace.parkmanager.magicband;

import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.InventoryType;
import network.palace.parkmanager.utils.BandUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Created by Marc on 12/22/14
 */
public class ShowEventClick {

    @SuppressWarnings("deprecation")
    public static void handle(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item == null || item.getItemMeta() == null) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        if (item.equals(BandUtil.getBackItem())) {
            ParkManager.getInstance().getInventoryUtil().openInventory(player, InventoryType.MAINMENU);
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta.getDisplayName() == null) {
            return;
        }
        String name = ChatColor.stripColor(meta.getDisplayName());
        switch (name) {
            case "Show Timetable":
                ParkManager.getInstance().getInventoryUtil().openInventory(player, InventoryType.SHOWTIMES);
                return;
            case "Fantasmic!":
                player.performCommand("warp fantasmic");
                return;
            case "IROE":
                player.performCommand("warp iroe");
                return;
            case "Wishes!":
                player.performCommand("warp wishes");
                return;
            case "Main Street Electrical Parade":
                player.performCommand("warp mainstreet");
                return;
            case "Finding Nemo the Musical":
                player.performCommand("warp fntm");
        }
    }
}
