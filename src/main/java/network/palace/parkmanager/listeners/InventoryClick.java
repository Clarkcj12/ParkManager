package network.palace.parkmanager.listeners;

import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.designstation.DesignStationClick;
import network.palace.parkmanager.handlers.Resort;
import network.palace.parkmanager.magicband.*;
import network.palace.parkmanager.watch.WatchTask;
import org.bukkit.ChatColor;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class InventoryClick implements Listener {

    @EventHandler
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        CPlayer player = Core.getPlayerManager().getPlayer(event.getWhoClicked().getUniqueId());
        Inventory inv = event.getInventory();
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null) {
            return;
        }
        int slot = event.getSlot();
        if (event.getClickedInventory().getType().equals(InventoryType.PLAYER)) {
            if (!BlockEdit.isInBuildMode(((PlayerInventory) event.getClickedInventory()).getHolder().getUniqueId())) {
                if (event.getAction().equals(InventoryAction.HOTBAR_SWAP)) {
                    event.setCancelled(true);
                    return;
                }
                if (event.getSlotType().equals(InventoryType.SlotType.ARMOR)) {
                    event.setCancelled(true);
                    return;
                }
                if (slot > 3) {
                    event.setResult(Event.Result.DENY);
                    event.setCancelled(true);
                    return;
                }
            }
        } else {
            if (!BlockEdit.isInBuildMode(player.getUniqueId())) {
                if (event.getAction().equals(InventoryAction.HOTBAR_SWAP)) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
        ParkManager parkManager = ParkManager.getInstance();
        String name = ChatColor.stripColor(inv.getName());
        if (((parkManager.isResort(Resort.WDW) || parkManager.isResort(Resort.DLR)) && name.equals(player.getName() + "'s MagicBand")) ||
                (parkManager.isResort(Resort.USO) && name.equals(player.getName() + "'s Power Pass"))) {
            MainMenuClick.handle(event);
            event.setCancelled(true);
            return;
        }
        if (name.startsWith("Achievements Page ")) {
            event.setCancelled(true);
            AchievementClick.handle(event);
            return;
        }
        if (name.startsWith("Food Menu Page ")) {
            event.setCancelled(true);
            FoodMenuClick.handle(event);
            return;
        }
        if (name.startsWith("Ride Counter Page ")) {
            event.setCancelled(true);
            RideCounterClick.handle(event);
            return;
        }
        if (name.startsWith("Wardrobe Manager Page")) {
            event.setCancelled(true);
            parkManager.getWardrobeManager().handle(event);
            return;
        }
        if (name.startsWith("Resource Pack")) {
            event.setCancelled(true);
            parkManager.getPackManager().handleClick(event);
            return;
        }
        if (name.startsWith("Rooms in")) {
            event.setCancelled(true);
            HotelRoomMenuClick.handle(event);
            return;
        }
        if (name.startsWith("Shop - ")) {
            event.setCancelled(true);
            String shop = inv.getName().replaceFirst(ChatColor.GREEN + "Shop - ", "");
            if (shop.equals(ChatColor.RED + "Confirm")) {
                ShopConfirmClick.handle(event);
                return;
            }
            ParkManager.getInstance().getShopManager().handleClick(event, shop);
            return;
        }
        switch (name) {
            case "Choose an Autograph Book":
                event.setCancelled(true);
                parkManager.getAutographManager().openBook(player, event.getCurrentItem());
                return;
            case "Ride List":
                event.setCancelled(true);
                RideListClick.handle(event);
                return;
            case "Attraction List":
                event.setCancelled(true);
                AttractionListClick.handle(event);
                return;
            case "Meet & Greet List":
                event.setCancelled(true);
                MeetAndGreetListClick.handle(event);
                return;
            case "Player Settings":
                event.setCancelled(true);
                PlayerSettingsClick.handle(event);
                return;
            case "My Profile":
                event.setCancelled(true);
                MyProfileMenuClick.handle(event);
                return;
            case "Shows and Events":
                event.setCancelled(true);
                ShowEventClick.handle(event);
                return;
            case "Customize Menu":
                event.setCancelled(true);
                CustomizeMenuClick.handle(event);
                return;
            case "Wardrobe Manager":
                event.setCancelled(true);
                parkManager.getWardrobeManager().handle(event);
                return;
            case "Customize Band Color":
                event.setCancelled(true);
                CustomBandClick.handle(event);
                return;
            case "Customize Name Color":
                event.setCancelled(true);
                CustomNameClick.handle(event);
                return;
            case "Park Menu":
            case "Park Menu - WDW":
                event.setCancelled(true);
                ParkMenuClick.handle(event);
                return;
            case "Rides and Meet & Greets":
                event.setCancelled(true);
                RideAttractionClick.handle(event);
                return;
            case "Special Edition MagicBands":
                event.setCancelled(true);
                SpecialEditionClick.handle(event);
                return;
            case "Hotels and Resorts":
                event.setCancelled(true);
                HotelAndResortMenuClick.handle(event);
                return;
            case "Hotels":
                event.setCancelled(true);
                HotelMenuClick.handle(event);
                return;
            case "Storage Upgrade":
                event.setCancelled(true);
                StorageUpgradeClick.handle(event);
                return;
            case "My Hotel Rooms":
                event.setCancelled(true);
                MyHotelRoomsMenuClick.handle(event);
                return;
            case "Book Room?":
                event.setCancelled(true);
                HotelRoomMenuClick.handle(event);
                return;
            case "Check Out?":
                event.setCancelled(true);
                HotelCheckoutMenuClick.handle(event);
                return;
            case "Visit Hotels and Resorts?":
                event.setCancelled(true);
                VisitHotelMenuClick.handle(event);
                return;
            case "Pick Model":
                event.setCancelled(true);
                DesignStationClick.handleModel(event);
                return;
            case "Pick Size/Color":
                event.setCancelled(true);
                DesignStationClick.handleSizeAndColor(event);
                return;
            case "Pick Engine":
                event.setCancelled(true);
                DesignStationClick.handleEngine(event);
                return;
            case "Shop":
                event.setCancelled(true);
                ShopMainMenuClick.handle(event);
                return;
            case "Wait Times":
                event.setCancelled(true);
                WaitTimeClick.handle(event);
                return;
            case "FastPass Kiosk":
                event.setCancelled(true);
                parkManager.getFpKioskManager().handle(event);
                return;
            case "Show Timetable":
                event.setCancelled(true);
                ShowTimeClick.handle(event);
                return;
            case "Player Time":
                event.setCancelled(true);
                PlayerTimeClick.handle(event);
                return;
            case "Rip Ride Rockit Song Selection":
                event.setCancelled(true);
                parkManager.getRipRideRockit().handle(event);
        }
    }

    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        CPlayer player = Core.getPlayerManager().getPlayer(event.getPlayer());
        if (BlockEdit.isInBuildMode(player.getUniqueId())) {
            return;
        }
        if (event.getNewSlot() == 6) {
            player.getActionBar().show(ChatColor.YELLOW + "" + ChatColor.BOLD + "Current time in EST: " +
                    ChatColor.GREEN + ParkManager.getInstance().getBandUtil().currentTime());
            WatchTask.addToMessage(player.getUniqueId());
        } else if (event.getPreviousSlot() == 6) {
            WatchTask.removeFromMessage(player.getUniqueId());
        }
    }
}