package network.palace.parkmanager.storage;

import network.palace.core.player.CPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * Created by Marc on 10/10/15
 */
public class Backpack {
    private UUID uuid;
    private StorageSize size;
    private Inventory inv;

    public Backpack(CPlayer player, StorageSize size, ItemStack[] contents) {
        this.uuid = player.getUniqueId();
        this.size = size;
        ItemStack[] arr;
        if (contents.length < size.getSlots()) {
            arr = new ItemStack[size.getSlots()];
            for (int i = 0; i < arr.length; i++) {
                arr[i] = new ItemStack(Material.AIR);
            }
            int i = 0;
            for (ItemStack item : contents) {
                arr[i] = item;
                i++;
            }
        }
        if (contents.length > size.getSlots()) {
            arr = new ItemStack[size.getSlots()];
            int i = 0;
            for (ItemStack item : contents) {
                if (i >= size.getSlots()) {
                    break;
                }
                arr[i] = item;
                i++;
            }
        } else {
            arr = contents;
        }
        inv = Bukkit.createInventory(player.getBukkitPlayer(), size.getSlots(), ChatColor.BLUE + "Your Backpack");
        if (arr != null) {
            inv.setContents(arr);
        }
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public StorageSize getSize() {
        return size;
    }

    public Inventory getInventory() {
        return inv;
    }
}