package network.palace.parkmanager.magicband;

import com.google.common.collect.ImmutableMap;
import network.palace.core.Core;
import network.palace.core.menu.Menu;
import network.palace.core.menu.MenuButton;
import network.palace.core.message.FormattedMessage;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.core.utils.HeadUtil;
import network.palace.core.utils.ItemUtil;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.attractions.Attraction;
import network.palace.parkmanager.food.FoodLocation;
import network.palace.parkmanager.handlers.AttractionCategory;
import network.palace.parkmanager.handlers.magicband.BandType;
import network.palace.parkmanager.handlers.magicband.MenuType;
import network.palace.parkmanager.queues.Queue;
import network.palace.parkmanager.shop.Shop;
import network.palace.parkmanager.utils.VisibilityUtil;
import org.apache.commons.lang.WordUtils;
import org.bson.Document;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MagicBandManager {

    public void openInventory(CPlayer player, BandInventory inventory) {
        switch (inventory) {
            case MAIN: {
                VisibilityUtil.Setting setting = ParkManager.getVisibilityUtil().getSetting(player);
                ChatColor color = setting.getColor();

                ItemStack band = getMagicBandItem(player);
                ItemMeta meta = band.getItemMeta();
                meta.setDisplayName(ChatColor.GREEN + "Customize your MagicBand");
                meta.setLore(Arrays.asList("", ChatColor.GRAY + "Choose from a variety of MagicBand",
                        ChatColor.GRAY + "designs and customize the color",
                        ChatColor.GRAY + "of the name for your MagicBand!"));
                band.setItemMeta(meta);

                ItemStack profile = HeadUtil.getPlayerHead(player.getTextureValue(), ChatColor.AQUA + "My Profile");
                meta = profile.getItemMeta();
                meta.setLore(Arrays.asList("", ChatColor.GREEN + "Loading...", ""));
                profile.setItemMeta(meta);

                List<MenuButton> buttons = new ArrayList<>(Arrays.asList(
                        new MenuButton(2, ItemUtil.create(Material.LIGHT_BLUE_BED, ChatColor.AQUA + "Hotels and Resorts",
                                Arrays.asList(ChatColor.GREEN + "Visit and rent a room", ChatColor.GREEN + "from a Resort Hotel!")),
                                ImmutableMap.of(ClickType.LEFT, p -> openInventory(p, BandInventory.HOTELS))),
                        new MenuButton(4, profile, ImmutableMap.of(ClickType.LEFT, p -> openInventory(p, BandInventory.PROFILE))),
                        new MenuButton(10, ItemUtil.create(Material.POTATO, ChatColor.AQUA + "Find Food",
                                Arrays.asList(ChatColor.GREEN + "Visit a restaurant", ChatColor.GREEN + "to get some food!")),
                                ImmutableMap.of(ClickType.LEFT, p -> openInventory(p, BandInventory.FOOD))),
                        new MenuButton(11, ItemUtil.create(Material.FIREWORK_ROCKET, ChatColor.AQUA + "Shows and Events",
                                Arrays.asList(ChatColor.GREEN + "Watch stage shows, nighttime", ChatColor.GREEN + "spectaculars, and much more!")),
                                ImmutableMap.of(ClickType.LEFT, p -> openInventory(p, BandInventory.SHOWS))),
                        new MenuButton(12, ItemUtil.create(Material.MINECART, ChatColor.AQUA + "Attractions",
                                Arrays.asList(ChatColor.GREEN + "View all of our available", ChatColor.GREEN + "theme park attractions!")),
                                ImmutableMap.of(ClickType.LEFT, p -> openInventory(p, BandInventory.ATTRACTION_MENU))),
                        new MenuButton(13, ItemUtil.create(Material.NETHER_STAR, ChatColor.AQUA + "Park Menu",
                                Arrays.asList(ChatColor.GREEN + "Travel to all of our", ChatColor.GREEN + "available theme parks!")),
                                ImmutableMap.of(ClickType.LEFT, p -> openInventory(p, BandInventory.PARKS))),
                        new MenuButton(14, ItemUtil.create(Material.GOLDEN_BOOTS, ChatColor.AQUA + "Shop",
                                Arrays.asList(ChatColor.GREEN + "Purchase souveniers and", ChatColor.GREEN + "all kinds of collectibles!")),
                                ImmutableMap.of(ClickType.LEFT, p -> openInventory(p, BandInventory.SHOP))),
                        new MenuButton(15, ItemUtil.create(Material.IRON_CHESTPLATE, ChatColor.AQUA + "Wardrobe Manager",
                                Arrays.asList(ChatColor.GREEN + "Change your outfit to make you", ChatColor.GREEN + "look like your favorite characters!")),
                                ImmutableMap.of(ClickType.LEFT, p -> openInventory(p, BandInventory.WARDROBE))),
                        new MenuButton(16, ItemUtil.create(setting.getBlock(), ChatColor.AQUA + "Guest Visibility " +
                                        ChatColor.GOLD + "➠ " + setting.getColor() + setting.getText(),
                                Arrays.asList(ChatColor.YELLOW + "Right-Click to " + (setting.equals(VisibilityUtil.Setting.ALL_HIDDEN) ? "show" : "hide") + " all players",
                                        ChatColor.YELLOW + "Left-Click for more options")),
                                ImmutableMap.of(ClickType.LEFT, p -> openInventory(p, BandInventory.VISIBILITY), ClickType.RIGHT, p -> {
                                    if (ParkManager.getVisibilityUtil().toggleVisibility(player)) {
                                        openInventory(p, BandInventory.MAIN);
                                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
                                    }
                                })),
                        new MenuButton(player.getRank().getRankId() < Rank.NOBLE.getRankId() ? 6 : 22, band, ImmutableMap.of(ClickType.LEFT, p -> openInventory(p, BandInventory.CUSTOMIZE_BAND)))
                ));

                Menu menu = new Menu(27, ChatColor.BLUE + "Your MagicBand", player, buttons);
                if (player.getRank().getRankId() >= Rank.NOBLE.getRankId()) {
                    menu.setButton(new MenuButton(6, ItemUtil.create(Material.CLOCK, ChatColor.AQUA + "Player Time",
                            Arrays.asList(ChatColor.GREEN + "Change the time of day you see", ChatColor.GREEN + "for the park you're currently in!")),
                            ImmutableMap.of(ClickType.LEFT, p -> openInventory(p, BandInventory.PLAYER_TIME))));
                }
                menu.open();
                Core.runTaskAsynchronously(ParkManager.getInstance(), () -> {
                    ItemStack updatedProfile = profile.clone();
                    ItemMeta menuMeta = updatedProfile.getItemMeta();
                    menuMeta.setLore(Arrays.asList(
                            ChatColor.GREEN + "Name: " + ChatColor.YELLOW + player.getName(),
                            ChatColor.GREEN + "Rank: " + player.getRank().getFormattedName(),
                            ChatColor.GREEN + "Balance: " + ChatColor.YELLOW + "$" + player.getBalance(),
                            ChatColor.GREEN + "Tokens: " + ChatColor.YELLOW + "✪ " + player.getTokens()
                    ));
                    updatedProfile.setItemMeta(menuMeta);

                    menu.setButton(new MenuButton(4, updatedProfile, ImmutableMap.of(ClickType.LEFT, p -> openInventory(p, BandInventory.PROFILE))));
                });
                break;
            }
            case FOOD: {
                List<MenuButton> buttons = new ArrayList<>();
                int i = 0;
                int size = 18;
                for (FoodLocation food : ParkManager.getFoodManager().getFoodLocations()) {
                    ItemStack item = food.getItem();
                    ItemMeta meta = item.getItemMeta();
                    meta.setLore(Arrays.asList("", ChatColor.YELLOW + "/warp " + food.getWarp()));
                    item.setItemMeta(meta);
                    if (i != 0 && i % 9 == 0) {
                        size += 9;
                    }
                    if (size > 54) {
                        size = 54;
                        break;
                    }
                    buttons.add(new MenuButton(i++, item, ImmutableMap.of(ClickType.LEFT, p -> {
                        p.performCommand("warp " + food.getWarp());
                        p.closeInventory();
                    })));
                }
                if (buttons.isEmpty()) {
                    buttons.add(new MenuButton(4, ItemUtil.create(Material.REDSTONE_BLOCK, ChatColor.RED + "No Food Locations",
                            Arrays.asList(ChatColor.GRAY + "Sorry, it looks like there are", ChatColor.GRAY + "no food locations on this server!"))));
                }
                buttons.add(getBackButton(size - 5, BandInventory.MAIN));
                new Menu(size, ChatColor.BLUE + "Food Locations", player, buttons).open();
                break;
            }
            case SHOWS: {
                new Menu(27, ChatColor.BLUE + "Shows and Events", player, Arrays.asList(
                        new MenuButton(8, ItemUtil.create(Material.BOOK, ChatColor.AQUA + "Show Timetable"),
                                ImmutableMap.of(ClickType.LEFT, p -> openInventory(player, BandInventory.TIMETABLE))),
                        new MenuButton(10, ItemUtil.create(Material.DIAMOND_SWORD, ChatColor.RED + "Symphony in the Stars"),
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    p.performCommand("warp sits");
                                    p.closeInventory();
                                })),
                        new MenuButton(11, ItemUtil.create(Material.DIAMOND_HELMET, ChatColor.BLUE + "Fantasmic!"),
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    p.performCommand("warp fant");
                                    p.closeInventory();
                                })),
                        new MenuButton(12, ItemUtil.create(Material.BLAZE_ROD, ChatColor.AQUA + "Wishes!"),
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    p.performCommand("warp castle");
                                    p.closeInventory();
                                })),
                        new MenuButton(13, ItemUtil.create(Material.SHEEP_SPAWN_EGG, ChatColor.GREEN + "Illuminations: Reflections of Earth"),
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    p.performCommand("warp iroe");
                                    p.closeInventory();
                                })),
                        new MenuButton(14, ItemUtil.create(Material.LIGHT_BLUE_DYE, ChatColor.DARK_AQUA + "Festival of Fantasy"),
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    p.performCommand("warp fof");
                                    p.closeInventory();
                                })),
                        new MenuButton(15, ItemUtil.create(Material.GLOWSTONE_DUST, ChatColor.YELLOW + "Main Street Electrical Parade"),
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    p.performCommand("warp msep");
                                    p.closeInventory();
                                })),
                        new MenuButton(16, ItemUtil.create(Material.TROPICAL_FISH, ChatColor.GOLD + "Finding Nemo: The Musical"),
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    p.performCommand("warp fntm");
                                    p.closeInventory();
                                })),
                        getBackButton(22, BandInventory.MAIN))).open();
                break;
            }
            case ATTRACTION_MENU:
                new Menu(27, ChatColor.BLUE + "Attractions Menu", player, Arrays.asList(
                        new MenuButton(11, ItemUtil.create(Material.MINECART, ChatColor.AQUA + "Attractions List",
                                Arrays.asList(ChatColor.GREEN + "View all of our available", ChatColor.GREEN + "theme park attractions")),
                                ImmutableMap.of(ClickType.LEFT, p -> openInventory(player, BandInventory.ATTRACTION_LIST))),
                        new MenuButton(15, ItemUtil.create(Material.CLOCK, ChatColor.AQUA + "Wait Times",
                                Arrays.asList(ChatColor.GREEN + "View the wait times for all", ChatColor.GREEN + "queues on this server")),
                                ImmutableMap.of(ClickType.LEFT, p -> openInventory(player, BandInventory.WAIT_TIMES))),
                        getBackButton(22, BandInventory.MAIN))).open();
                break;
            case ATTRACTION_LIST: {
                List<MenuButton> buttons = new ArrayList<>();
                int i = 0;
                int size = 18;
                for (Attraction attraction : ParkManager.getAttractionManager().getAttractions()) {
                    ItemStack item = attraction.getItem();
                    ItemMeta meta = item.getItemMeta();
                    List<String> lore = new ArrayList<>(Collections.singletonList(""));
                    String[] descriptionList = WordUtils.wrap(attraction.getDescription(), 30).split("\n");
                    for (String s : descriptionList) {
                        lore.add(ChatColor.DARK_AQUA + s);
                    }
                    lore.addAll(Arrays.asList("", ChatColor.GREEN + "Warp: " + ChatColor.YELLOW + "/warp " + attraction.getWarp(),
                            "", ChatColor.GREEN + "Status: " + (attraction.isOpen() ? "OPEN" : ChatColor.RED + "CLOSED"),
                            "", ChatColor.GREEN + "Categories:"));
                    if (attraction.getLinkedQueue() != null) {
                        Queue queue = ParkManager.getQueueManager().getQueue(attraction.getLinkedQueue());
                        if (queue != null)
                            lore.addAll(5 + descriptionList.length, Arrays.asList("", ChatColor.GREEN + "Wait: " + ChatColor.YELLOW + queue.getWaitFor(null)));
                    }
                    for (AttractionCategory category : attraction.getCategories()) {
                        lore.add(ChatColor.AQUA + "- " + ChatColor.YELLOW + category.getFormattedName());
                    }
                    meta.setLore(lore);
                    item.setItemMeta(meta);
                    if (i != 0 && i % 9 == 0) {
                        size += 9;
                    }
                    if (size > 54) {
                        size = 54;
                        break;
                    }
                    buttons.add(new MenuButton(i++, item, ImmutableMap.of(ClickType.LEFT, p -> {
                        p.performCommand("warp " + attraction.getWarp());
                        p.closeInventory();
                    })));
                }
                if (buttons.isEmpty()) {
                    buttons.add(new MenuButton(4, ItemUtil.create(Material.REDSTONE_BLOCK, ChatColor.RED + "No Attractions",
                            Arrays.asList(ChatColor.GRAY + "Sorry, it looks like there are", ChatColor.GRAY + "no attractions on this server!"))));
                }
                buttons.add(getBackButton(size - 5, BandInventory.ATTRACTION_MENU));
                new Menu(size, ChatColor.BLUE + "Attractions List", player, buttons).open();
                break;
            }
            case WAIT_TIMES: {
                List<MenuButton> buttons = new ArrayList<>();
                int i = 0;
                int size = 18;
                for (Queue queue : ParkManager.getQueueManager().getQueues()) {
                    ItemStack item = ItemUtil.create(Material.SIGN);
                    ItemMeta meta = item.getItemMeta();
                    meta.setDisplayName(queue.getName());
                    List<String> lore = new ArrayList<>(Arrays.asList("", ChatColor.GREEN + "Wait: " + ChatColor.YELLOW + queue.getWaitFor(null),
                            "", ChatColor.GREEN + "Warp: " + ChatColor.YELLOW + "/warp " + queue.getWarp(),
                            "", ChatColor.GREEN + "Status: " + (queue.isOpen() ? "OPEN" : ChatColor.RED + "CLOSED")));
                    meta.setLore(lore);
                    item.setItemMeta(meta);
                    if (i != 0 && i % 9 == 0) {
                        size += 9;
                    }
                    if (size > 54) {
                        size = 54;
                        break;
                    }
                    buttons.add(new MenuButton(i++, item, ImmutableMap.of(ClickType.LEFT, p -> {
                        p.performCommand("warp " + queue.getWarp());
                        p.closeInventory();
                    })));
                }
                if (buttons.isEmpty()) {
                    buttons.add(new MenuButton(4, ItemUtil.create(Material.REDSTONE_BLOCK, ChatColor.RED + "No Queues",
                            Arrays.asList(ChatColor.GRAY + "Sorry, it looks like there are", ChatColor.GRAY + "no queues on this server!"))));
                }
                buttons.add(getBackButton(size - 5, BandInventory.ATTRACTION_MENU));
                new Menu(size, ChatColor.BLUE + "Wait Times", player, buttons).open();
                break;
            }
            case PARKS: {
                List<MenuButton> buttons = Arrays.asList(
                        new MenuButton(10, ItemUtil.create(Material.FILLED_MAP, ChatColor.AQUA + "Universal Orlando Resort", Collections.singletonList(ChatColor.GREEN + "/warp USO")),
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    p.performCommand("warp uso");
                                    p.closeInventory();
                                })),
                        new MenuButton(13, ItemUtil.create(Material.FILLED_MAP, ChatColor.AQUA + "Walt Disney World Resort", Collections.singletonList(ChatColor.GREEN + "/warp WDW")),
                                ImmutableMap.of(ClickType.LEFT, p -> openInventory(player, BandInventory.PARKS_WDW))),
                        new MenuButton(16, ItemUtil.create(Material.SNOWBALL, ChatColor.AQUA + "Seasonal", Collections.singletonList(ChatColor.GREEN + "/warp Seasonal")),
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    p.performCommand("warp seasonal");
                                    p.closeInventory();
                                })),
                        getBackButton(22, BandInventory.MAIN)
                );
                new Menu(27, ChatColor.BLUE + "Park Menu", player, buttons).open();
                break;
            }
            case PARKS_WDW: {
                List<MenuButton> buttons = Arrays.asList(
                        new MenuButton(10, ItemUtil.create(Material.DIAMOND_HOE, ChatColor.AQUA + "Magic Kingdom", Collections.singletonList(ChatColor.GREEN + "/warp MK")),
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    p.performCommand("warp mk");
                                    p.closeInventory();
                                })),
                        new MenuButton(11, ItemUtil.create(Material.SNOWBALL, ChatColor.AQUA + "Epcot", Collections.singletonList(ChatColor.GREEN + "/warp Epcot")),
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    p.performCommand("warp epcot");
                                    p.closeInventory();
                                })),
                        new MenuButton(12, ItemUtil.create(Material.JUKEBOX, ChatColor.AQUA + "Disney's Hollywood Studios", Collections.singletonList(ChatColor.GREEN + "/warp DHS")),
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    p.performCommand("warp dhs");
                                    p.closeInventory();
                                })),
                        new MenuButton(13, ItemUtil.create(Material.OAK_SAPLING, ChatColor.AQUA + "Disney's Animal Kingdom", Collections.singletonList(ChatColor.GREEN + "/warp AK")),
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    p.performCommand("warp ak");
                                    p.closeInventory();
                                })),
                        new MenuButton(14, ItemUtil.create(Material.LIGHT_BLUE_BED, ChatColor.AQUA + "Resorts", Collections.singletonList(ChatColor.GREEN + "/warp Resorts")),
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    p.performCommand("warp resorts");
                                    p.closeInventory();
                                })),
                        new MenuButton(15, ItemUtil.create(Material.WATER_BUCKET, ChatColor.AQUA + "Typhoon Lagoon", Collections.singletonList(ChatColor.GREEN + "/warp Typhoon")),
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    p.performCommand("warp typhoon");
                                    p.closeInventory();
                                })),
                        new MenuButton(16, ItemUtil.create(Material.OAK_BOAT, ChatColor.AQUA + "Disney Cruise Line", Collections.singletonList(ChatColor.GREEN + "/warp DCL")),
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    p.performCommand("warp dcl");
                                    p.closeInventory();
                                })),
                        getBackButton(22, BandInventory.PARKS)
                );
                new Menu(27, ChatColor.BLUE + "Park Menu - WDW", player, buttons).open();
                break;
            }
            case SHOP: {
                List<MenuButton> buttons = new ArrayList<>();
                int i = 0;
                int size = 18;
                for (Shop shop : ParkManager.getShopManager().getShops()) {
                    ItemStack item = shop.getItem();
                    ItemMeta meta = item.getItemMeta();
                    meta.setLore(Arrays.asList("", ChatColor.YELLOW + "/warp " + shop.getWarp()));
                    item.setItemMeta(meta);
                    if (i != 0 && i % 9 == 0) {
                        size += 9;
                    }
                    if (size > 54) {
                        size = 54;
                        break;
                    }
                    buttons.add(new MenuButton(i++, item, ImmutableMap.of(ClickType.LEFT, p -> {
                        p.performCommand("warp " + shop.getWarp());
                        p.closeInventory();
                    })));
                }
                if (buttons.isEmpty()) {
                    buttons.add(new MenuButton(4, ItemUtil.create(Material.REDSTONE_BLOCK, ChatColor.RED + "No Shops",
                            Arrays.asList(ChatColor.GRAY + "Sorry, it looks like there are", ChatColor.GRAY + "no shops on this server!"))));
                }
                buttons.add(getBackButton(size - 5, BandInventory.MAIN));
                new Menu(size, ChatColor.BLUE + "Shop List", player, buttons).open();
                break;
            }
            case WARDROBE: {
                new Menu(27, ChatColor.BLUE + "Wardrobe Manager", player, Arrays.asList(
                        new MenuButton(13, ItemUtil.create(Material.REDSTONE_BLOCK, ChatColor.AQUA + "Pardon Our Pixie Dust!",
                                Arrays.asList(ChatColor.GRAY + "We've temporarily disabled outfits",
                                        ChatColor.GRAY + "while we work to improve them",
                                        ChatColor.GRAY + "behind the scenes.", "",
                                        ChatColor.GRAY + "We apologize for the inconvenience,",
                                        ChatColor.GRAY + "they will be returning shortly!"))),
                        getBackButton(22, BandInventory.MAIN))
                ).open();
                break;
            }
            case HOTELS: {
                new Menu(27, ChatColor.BLUE + "Hotels and Resorts", player, Arrays.asList(
                        new MenuButton(13, ItemUtil.create(Material.REDSTONE_BLOCK, ChatColor.AQUA + "Pardon Our Pixie Dust!",
                                Arrays.asList(ChatColor.GRAY + "Resort room renting will be",
                                        ChatColor.GRAY + "returning soon! In the meantime,",
                                        ChatColor.GRAY + "you're welcome to visit our",
                                        ChatColor.GRAY + "resorts at " + ChatColor.AQUA + "/join Resorts!"))),
                        getBackButton(22, BandInventory.MAIN)
                )).open();
                break;
            }
            case PROFILE: {
                new Menu(27, ChatColor.BLUE + "My Profile", player, Arrays.asList(
                        new MenuButton(10, ItemUtil.create(Material.NETHER_STAR, ChatColor.AQUA + "Website",
                                Collections.singletonList(ChatColor.GREEN + "Visit our website!")),
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    new FormattedMessage("\n")
                                            .then("Click here to visit our website")
                                            .color(ChatColor.YELLOW).style(ChatColor.UNDERLINE)
                                            .tooltip(ChatColor.GREEN + "Click to visit " + ChatColor.YELLOW + "https://palace.network")
                                            .link("https://palace.network").then("\n").send(p);
                                    p.closeInventory();
                                })),
                        new MenuButton(11, ItemUtil.create(Material.DIAMOND, ChatColor.AQUA + "Store",
                                Collections.singletonList(ChatColor.GREEN + "Visit our store!")),
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    new FormattedMessage("\n")
                                            .then("Click here to visit our store")
                                            .color(ChatColor.YELLOW).style(ChatColor.UNDERLINE)
                                            .tooltip(ChatColor.GREEN + "Click to visit " + ChatColor.YELLOW + "https://store.palace.network")
                                            .link("https://store.palace.network").then("\n").send(p);
                                    p.closeInventory();
                                })),
                        new MenuButton(12, ItemUtil.create(Material.ENDER_CHEST, ChatColor.AQUA + "Locker",
                                Collections.singletonList(ChatColor.GREEN + "Click to view your Locker")),
                                ImmutableMap.of(ClickType.LEFT, p -> ParkManager.getInventoryUtil().openMenu(p, MenuType.LOCKER))),
                        new MenuButton(13, ItemUtil.create(Material.GOLD_INGOT, ChatColor.AQUA + "Ride Counters",
                                Arrays.asList(ChatColor.GREEN + "View the number of times you've",
                                        ChatColor.GREEN + "been on different theme park rides")),
                                ImmutableMap.of(ClickType.LEFT, p -> openInventory(p, BandInventory.RIDE_COUNTERS))),
                        new MenuButton(14, ItemUtil.create(Material.EMERALD, ChatColor.AQUA + "Achievements", Arrays.asList(ChatColor.GREEN +
                                        "You've earned " + ChatColor.YELLOW + player.getAchievementManager().getAchievements().size() + ChatColor.GREEN + " achievements!",
                                ChatColor.GREEN + "There are " + ChatColor.YELLOW + Core.getAchievementManager().getAchievements().size() + ChatColor.GREEN + " total to earn",
                                ChatColor.GRAY + "Click to view all of your achievements")),
                                ImmutableMap.of(ClickType.LEFT, p -> Core.getCraftingMenu().openAchievementPage(p, 1))),
                        new MenuButton(15, ItemUtil.create(Material.NOTE_BLOCK, ChatColor.AQUA + "Resource Packs",
                                Collections.singletonList(ChatColor.GREEN + "Manage your Resource Pack settings")),
                                ImmutableMap.of(ClickType.LEFT, p -> ParkManager.getPackManager().openMenu(p))),
                        new MenuButton(16, ItemUtil.create(Material.COMPASS, ChatColor.AQUA + "Discord",
                                Collections.singletonList(ChatColor.GREEN + "Join the conversation on our Discord!")),
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    new FormattedMessage("\n")
                                            .then("Click here to join our Discord")
                                            .color(ChatColor.YELLOW).style(ChatColor.UNDERLINE)
                                            .tooltip(ChatColor.GREEN + "Click to run " + ChatColor.YELLOW + "/discord")
                                            .command("/discord").then("\n").send(p);
                                    p.closeInventory();
                                })),
                        getBackButton(22, BandInventory.MAIN)
                )).open();
                break;
            }
            case RIDE_COUNTERS: {
                new Menu(27, ChatColor.GREEN + "Ride Counters", player, Collections.singletonList(getBackButton(22, BandInventory.PROFILE))).open();
                break;
            }
            case VISIBILITY: {
                VisibilityUtil.Setting setting = ParkManager.getVisibilityUtil().getSetting(player);
                ItemStack visible = ItemUtil.create(VisibilityUtil.Setting.ALL_VISIBLE.getBlock(),
                        VisibilityUtil.Setting.ALL_VISIBLE.getColor() + VisibilityUtil.Setting.ALL_VISIBLE.getText()
                                + (setting.equals(VisibilityUtil.Setting.ALL_VISIBLE) ? (ChatColor.YELLOW + " (SELECTED)") : ""),
                        Collections.singletonList(ChatColor.GREEN + "Show all players"));
                ItemStack staffFriends = ItemUtil.create(VisibilityUtil.Setting.ONLY_STAFF_AND_FRIENDS.getBlock(),
                        VisibilityUtil.Setting.ONLY_STAFF_AND_FRIENDS.getColor() + VisibilityUtil.Setting.ONLY_STAFF_AND_FRIENDS.getText()
                                + (setting.equals(VisibilityUtil.Setting.ONLY_STAFF_AND_FRIENDS) ? (ChatColor.YELLOW + " (SELECTED)") : ""),
                        Collections.singletonList(ChatColor.GREEN + "Show only staff and friends"));
                ItemStack friends = ItemUtil.create(VisibilityUtil.Setting.ONLY_FRIENDS.getBlock(),
                        VisibilityUtil.Setting.ONLY_FRIENDS.getColor() + VisibilityUtil.Setting.ONLY_FRIENDS.getText()
                                + (setting.equals(VisibilityUtil.Setting.ONLY_FRIENDS) ? (ChatColor.YELLOW + " (SELECTED)") : ""),
                        Collections.singletonList(ChatColor.GREEN + "Show only friends"));
                ItemStack none = ItemUtil.create(VisibilityUtil.Setting.ALL_HIDDEN.getBlock(),
                        VisibilityUtil.Setting.ALL_HIDDEN.getColor() + VisibilityUtil.Setting.ALL_HIDDEN.getText()
                                + (setting.equals(VisibilityUtil.Setting.ALL_HIDDEN) ? (ChatColor.YELLOW + " (SELECTED)") : ""),
                        Collections.singletonList(ChatColor.GREEN + "Hide all players"));
                switch (setting) {
                    case ALL_VISIBLE:
                        visible.addUnsafeEnchantment(Enchantment.LUCK, 1);
                        break;
                    case ONLY_STAFF_AND_FRIENDS:
                        staffFriends.addUnsafeEnchantment(Enchantment.LUCK, 1);
                        break;
                    case ONLY_FRIENDS:
                        friends.addUnsafeEnchantment(Enchantment.LUCK, 1);
                        break;
                    case ALL_HIDDEN:
                        none.addUnsafeEnchantment(Enchantment.LUCK, 1);
                        break;
                }
                List<MenuButton> buttons = Arrays.asList(
                        new MenuButton(10, visible,
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    if (ParkManager.getVisibilityUtil().setSetting(p, VisibilityUtil.Setting.ALL_VISIBLE, false)) {
                                        openInventory(p, BandInventory.VISIBILITY);
                                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
                                    }
                                })),
                        new MenuButton(12, staffFriends,
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    if (ParkManager.getVisibilityUtil().setSetting(p, VisibilityUtil.Setting.ONLY_STAFF_AND_FRIENDS, false)) {
                                        openInventory(p, BandInventory.VISIBILITY);
                                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
                                    }
                                })),
                        new MenuButton(14, friends,
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    if (ParkManager.getVisibilityUtil().setSetting(p, VisibilityUtil.Setting.ONLY_FRIENDS, false)) {
                                        openInventory(p, BandInventory.VISIBILITY);
                                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
                                    }
                                })),
                        new MenuButton(16, none,
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    if (ParkManager.getVisibilityUtil().setSetting(p, VisibilityUtil.Setting.ALL_HIDDEN, false)) {
                                        openInventory(p, BandInventory.VISIBILITY);
                                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
                                    }
                                })),
                        getBackButton(22, BandInventory.MAIN)
                );
                new Menu(27, ChatColor.BLUE + "Visibility Settings", player, buttons).open();
                break;
            }
            case CUSTOMIZE_BAND: {
                List<MenuButton> buttons = Arrays.asList(
                        new MenuButton(11, ItemUtil.create(getMaterial(BandType.SORCERER_MICKEY), ChatColor.GREEN + "Customize MagicBand Type"),
                                ImmutableMap.of(ClickType.LEFT, p -> openInventory(p, BandInventory.CUSTOMIZE_BAND_TYPE))),
                        new MenuButton(15, ItemUtil.create(Material.JUKEBOX, ChatColor.GREEN + "Customize MagicBand Name Color"),
                                ImmutableMap.of(ClickType.LEFT, p -> openInventory(p, BandInventory.CUSTOMIZE_BAND_NAME))),
                        getBackButton(22, BandInventory.MAIN)
                );
                new Menu(27, ChatColor.BLUE + "Customize MagicBand", player, buttons).open();
                break;
            }
            case CUSTOMIZE_BAND_TYPE: {
                ItemStack red = getMagicBandItem("red", (String) player.getRegistry().getEntry("bandNameColor"));
                ItemMeta meta = red.getItemMeta();
                meta.setDisplayName(BandType.RED.getName());
                red.setItemMeta(meta);
                ItemStack orange = getMagicBandItem("orange", (String) player.getRegistry().getEntry("bandNameColor"));
                meta = orange.getItemMeta();
                meta.setDisplayName(BandType.ORANGE.getName());
                orange.setItemMeta(meta);
                ItemStack yellow = getMagicBandItem("yellow", (String) player.getRegistry().getEntry("bandNameColor"));
                meta = yellow.getItemMeta();
                meta.setDisplayName(BandType.YELLOW.getName());
                yellow.setItemMeta(meta);
                ItemStack green = getMagicBandItem("green", (String) player.getRegistry().getEntry("bandNameColor"));
                meta = green.getItemMeta();
                meta.setDisplayName(BandType.GREEN.getName());
                green.setItemMeta(meta);
                ItemStack blue = getMagicBandItem("blue", (String) player.getRegistry().getEntry("bandNameColor"));
                meta = blue.getItemMeta();
                meta.setDisplayName(BandType.BLUE.getName());
                blue.setItemMeta(meta);
                ItemStack purple = getMagicBandItem("purple", (String) player.getRegistry().getEntry("bandNameColor"));
                meta = purple.getItemMeta();
                meta.setDisplayName(BandType.PURPLE.getName());
                purple.setItemMeta(meta);
                ItemStack pink = getMagicBandItem("pink", (String) player.getRegistry().getEntry("bandNameColor"));
                meta = pink.getItemMeta();
                meta.setDisplayName(BandType.PINK.getName());
                pink.setItemMeta(meta);
                List<MenuButton> buttons = Arrays.asList(
                        new MenuButton(10, red, ImmutableMap.of(ClickType.LEFT, p -> setBandType(p, BandType.RED.getDBName()))),
                        new MenuButton(11, orange, ImmutableMap.of(ClickType.LEFT, p -> setBandType(p, BandType.ORANGE.getDBName()))),
                        new MenuButton(12, yellow, ImmutableMap.of(ClickType.LEFT, p -> setBandType(p, BandType.YELLOW.getDBName()))),
                        new MenuButton(13, green, ImmutableMap.of(ClickType.LEFT, p -> setBandType(p, BandType.GREEN.getDBName()))),
                        new MenuButton(14, blue, ImmutableMap.of(ClickType.LEFT, p -> setBandType(p, BandType.BLUE.getDBName()))),
                        new MenuButton(15, purple, ImmutableMap.of(ClickType.LEFT, p -> setBandType(p, BandType.PURPLE.getDBName()))),
                        new MenuButton(16, pink, ImmutableMap.of(ClickType.LEFT, p -> setBandType(p, BandType.PINK.getDBName()))),

                        new MenuButton(20, ItemUtil.create(getMaterial(BandType.SORCERER_MICKEY), BandType.SORCERER_MICKEY.getName()),
                                ImmutableMap.of(ClickType.LEFT, p -> setBandType(p, BandType.SORCERER_MICKEY.getDBName()))),
                        new MenuButton(21, ItemUtil.create(getMaterial(BandType.HAUNTED_MANSION), BandType.HAUNTED_MANSION.getName()),
                                ImmutableMap.of(ClickType.LEFT, p -> setBandType(p, BandType.HAUNTED_MANSION.getDBName()))),
                        new MenuButton(22, ItemUtil.create(getMaterial(BandType.PRINCESSES), BandType.PRINCESSES.getName()),
                                ImmutableMap.of(ClickType.LEFT, p -> setBandType(p, BandType.PRINCESSES.getDBName()))),
                        new MenuButton(23, ItemUtil.create(getMaterial(BandType.BIG_HERO_SIX), BandType.BIG_HERO_SIX.getName()),
                                ImmutableMap.of(ClickType.LEFT, p -> setBandType(p, BandType.BIG_HERO_SIX.getDBName()))),
                        new MenuButton(24, ItemUtil.create(getMaterial(BandType.HOLIDAY), BandType.HOLIDAY.getName()),
                                ImmutableMap.of(ClickType.LEFT, p -> setBandType(p, BandType.HOLIDAY.getDBName()))),
                        getBackButton(31, BandInventory.CUSTOMIZE_BAND)
                );
                new Menu(36, ChatColor.BLUE + "Customize MagicBand Type", player, buttons).open();
                break;
            }
            case CUSTOMIZE_BAND_NAME: {
                List<MenuButton> buttons = Arrays.asList(
                        new MenuButton(10, ItemUtil.create(Material.RED_CONCRETE, ChatColor.RED + "Red"),
                                ImmutableMap.of(ClickType.LEFT, p -> setBandNameColor(p, "red"))),
                        new MenuButton(11, ItemUtil.create(Material.ORANGE_CONCRETE, ChatColor.GOLD + "Orange"),
                                ImmutableMap.of(ClickType.LEFT, p -> setBandNameColor(p, "orange"))),
                        new MenuButton(12, ItemUtil.create(Material.YELLOW_CONCRETE, ChatColor.YELLOW + "Yellow"),
                                ImmutableMap.of(ClickType.LEFT, p -> setBandNameColor(p, "yellow"))),
                        new MenuButton(13, ItemUtil.create(Material.GREEN_CONCRETE, ChatColor.DARK_GREEN + "Green"),
                                ImmutableMap.of(ClickType.LEFT, p -> setBandNameColor(p, "green"))),
                        new MenuButton(14, ItemUtil.create(Material.BLUE_CONCRETE, ChatColor.BLUE + "Blue"),
                                ImmutableMap.of(ClickType.LEFT, p -> setBandNameColor(p, "blue"))),
                        new MenuButton(15, ItemUtil.create(Material.PURPLE_CONCRETE, ChatColor.DARK_PURPLE + "Purple"),
                                ImmutableMap.of(ClickType.LEFT, p -> setBandNameColor(p, "purple"))),
                        new MenuButton(16, ItemUtil.create(Material.PINK_CONCRETE, ChatColor.LIGHT_PURPLE + "Pink"),
                                ImmutableMap.of(ClickType.LEFT, p -> setBandNameColor(p, "pink"))),
                        getBackButton(22, BandInventory.CUSTOMIZE_BAND)
                );
                new Menu(27, ChatColor.BLUE + "Customize MagicBand Name Color", player, buttons).open();
                break;
            }
            case TIMETABLE: {
                List<MenuButton> buttons = ParkManager.getScheduleManager().getButtons();
                buttons.add(getBackButton(49, BandInventory.SHOWS));
                new Menu(54, ChatColor.BLUE + "Show Timetable", player, buttons).open();
                break;
            }
            case PLAYER_TIME: {
                long time = player.getBukkitPlayer().getPlayerTime() % 24000;
                List<String> current = Collections.singletonList(ChatColor.YELLOW + "Currently Selected!");
                List<String> not = Collections.singletonList(ChatColor.GRAY + "Click to Select!");
                new Menu(27, ChatColor.BLUE + "Player Time", player, Arrays.asList(
                        new MenuButton(9, ItemUtil.create(Material.WHITE_STAINED_GLASS_PANE, ChatColor.GREEN + "Reset",
                                Collections.singletonList(ChatColor.GREEN + "Match Park Time")),
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    p.sendMessage(ChatColor.GREEN + "You " + ChatColor.AQUA + "reset " + ChatColor.GREEN + "your Player Time!");
                                    p.getBukkitPlayer().resetPlayerTime();
                                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 100, 2);
                                    openInventory(p, BandInventory.PLAYER_TIME);
                                })),
                        new MenuButton(10, ItemUtil.create(Material.CLOCK, ChatColor.GREEN + "6AM", time == 0 ? current : not),
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    p.getBukkitPlayer().setPlayerTime(0, false);
                                    p.sendMessage(ChatColor.GREEN + "Your Player Time has been set to " + ChatColor.AQUA + "6AM");
                                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 100, 2);
                                    openInventory(p, BandInventory.PLAYER_TIME);
                                })),
                        new MenuButton(11, ItemUtil.create(Material.CLOCK, ChatColor.GREEN + "9AM", time == 3000 ? current : not),
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    p.getBukkitPlayer().setPlayerTime(3000, false);
                                    p.sendMessage(ChatColor.GREEN + "Your Player Time has been set to " + ChatColor.AQUA + "9AM");
                                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 100, 2);
                                    openInventory(p, BandInventory.PLAYER_TIME);
                                })),
                        new MenuButton(12, ItemUtil.create(Material.CLOCK, ChatColor.GREEN + "12PM", time == 6000 ? current : not),
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    p.getBukkitPlayer().setPlayerTime(6000, false);
                                    p.sendMessage(ChatColor.GREEN + "Your Player Time has been set to " + ChatColor.AQUA + "12PM");
                                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 100, 2);
                                    openInventory(p, BandInventory.PLAYER_TIME);
                                })),
                        new MenuButton(13, ItemUtil.create(Material.CLOCK, ChatColor.GREEN + "3PM", time == 9000 ? current : not),
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    p.getBukkitPlayer().setPlayerTime(9000, false);
                                    p.sendMessage(ChatColor.GREEN + "Your Player Time has been set to " + ChatColor.AQUA + "3PM");
                                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 100, 2);
                                    openInventory(p, BandInventory.PLAYER_TIME);
                                })),
                        new MenuButton(14, ItemUtil.create(Material.CLOCK, ChatColor.GREEN + "6PM", time == 12000 ? current : not),
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    p.getBukkitPlayer().setPlayerTime(12000, false);
                                    p.sendMessage(ChatColor.GREEN + "Your Player Time has been set to " + ChatColor.AQUA + "6PM");
                                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 100, 2);
                                    openInventory(p, BandInventory.PLAYER_TIME);
                                })),
                        new MenuButton(15, ItemUtil.create(Material.CLOCK, ChatColor.GREEN + "9PM", time == 15000 ? current : not),
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    p.getBukkitPlayer().setPlayerTime(15000, false);
                                    p.sendMessage(ChatColor.GREEN + "Your Player Time has been set to " + ChatColor.AQUA + "9PM");
                                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 100, 2);
                                    openInventory(p, BandInventory.PLAYER_TIME);
                                })),
                        new MenuButton(16, ItemUtil.create(Material.CLOCK, ChatColor.GREEN + "12AM", time == 18000 ? current : not),
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    p.getBukkitPlayer().setPlayerTime(18000, false);
                                    p.sendMessage(ChatColor.GREEN + "Your Player Time has been set to " + ChatColor.AQUA + "12AM");
                                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 100, 2);
                                    openInventory(p, BandInventory.PLAYER_TIME);
                                })),
                        new MenuButton(17, ItemUtil.create(Material.CLOCK, ChatColor.GREEN + "3AM", time == 21000 ? current : not),
                                ImmutableMap.of(ClickType.LEFT, p -> {
                                    p.getBukkitPlayer().setPlayerTime(21000, false);
                                    p.sendMessage(ChatColor.GREEN + "Your Player Time has been set to " + ChatColor.AQUA + "3AM");
                                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 100, 2);
                                    openInventory(p, BandInventory.PLAYER_TIME);
                                })),
                        getBackButton(22, BandInventory.MAIN)
                )).open();
                break;
            }
        }
    }

    private void setBandType(CPlayer player, String type) {
        player.getRegistry().addEntry("bandType", type.toLowerCase());
        ParkManager.getStorageManager().updateInventory(player);
        player.sendMessage(ChatColor.GREEN + "You've changed to a " + BandType.fromString(type).getName() + ChatColor.GREEN + " MagicBand!");
        player.closeInventory();
        Core.runTaskAsynchronously(ParkManager.getInstance(), () -> Core.getMongoHandler().setMagicBandData(player.getUniqueId(), "namecolor", type.toLowerCase()));
    }

    private void setBandNameColor(CPlayer player, String color) {
        player.getRegistry().addEntry("bandNameColor", color.toLowerCase());
        ParkManager.getStorageManager().updateInventory(player);
        player.sendMessage(ChatColor.GREEN + "You've set your MagicBand's name color to " + getNameColor(color) + color + "!");
        player.closeInventory();
        Core.runTaskAsynchronously(ParkManager.getInstance(), () -> Core.getMongoHandler().setMagicBandData(player.getUniqueId(), "namecolor", color.toLowerCase()));
    }

    public void handleJoin(CPlayer player, Document doc) {
        String bandtype, namecolor;
        if (!doc.containsKey("bandtype") || !doc.containsKey("namecolor")) {
            bandtype = "red";
            namecolor = "gold";
        } else {
            bandtype = doc.getString("bandtype");
            namecolor = doc.getString("namecolor");
        }
        player.getRegistry().addEntry("bandType", bandtype);
        player.getRegistry().addEntry("bandNameColor", namecolor);
    }

    public ItemStack getMagicBandItem(CPlayer player) {
        if (!player.getRegistry().hasEntry("bandType") || !player.getRegistry().hasEntry("bandNameColor")) {
            return getMagicBandItem("red", "gold");
        }
        return getMagicBandItem((String) player.getRegistry().getEntry("bandType"), (String) player.getRegistry().getEntry("bandNameColor"));
    }

    public ItemStack getMagicBandItem(String type, String color) {
        BandType bandType = BandType.fromString(type);
        ItemStack item;
        switch (bandType) {
            case RED:
            case ORANGE:
            case YELLOW:
            case GREEN:
            case BLUE:
            case PURPLE:
            case PINK: {
                item = ItemUtil.create(Material.FIREWORK_STAR, getNameColor(color) + "MagicBand " + ChatColor.GRAY + "(Right-Click)");
                FireworkEffectMeta meta = (FireworkEffectMeta) item.getItemMeta();
                meta.setEffect(FireworkEffect.builder().withColor(getBandColor(bandType)).build());
                item.setItemMeta(meta);
                break;
            }
            case SORCERER_MICKEY:
            case HAUNTED_MANSION:
            case PRINCESSES:
            case BIG_HERO_SIX:
            case HOLIDAY:
                item = ItemUtil.create(getMaterial(bandType), getNameColor(color) + "MagicBand " + ChatColor.GRAY + "(Right-Click)");
                break;
            default:
                return getMagicBandItem("red", "gold");
        }
        return item;
    }

    private Material getMaterial(BandType type) {
        switch (type) {
            case SORCERER_MICKEY:
                return Material.DIAMOND_HORSE_ARMOR;
            case HAUNTED_MANSION:
                return Material.GOLDEN_HORSE_ARMOR;
            case PRINCESSES:
                return Material.GHAST_TEAR;
            case BIG_HERO_SIX:
                return Material.IRON_HORSE_ARMOR;
            case HOLIDAY:
                return Material.PAPER;
            default:
                return Material.FIREWORK_STAR;
        }
    }

    private Color getBandColor(BandType type) {
        switch (type) {
            case ORANGE:
                return Color.fromRGB(247, 140, 0);
            case YELLOW:
                return Color.fromRGB(239, 247, 0);
            case GREEN:
                return Color.fromRGB(0, 192, 13);
            case BLUE:
                return Color.fromRGB(41, 106, 255);
            case PURPLE:
                return Color.fromRGB(176, 0, 220);
            case PINK:
                return Color.fromRGB(246, 120, 255);
            default:
                //Red
                return Color.fromRGB(255, 40, 40);
        }
    }

    private ChatColor getNameColor(String color) {
        switch (color.toLowerCase()) {
            case "red":
                return ChatColor.RED;
            case "yellow":
                return ChatColor.YELLOW;
            case "green":
                return ChatColor.DARK_GREEN;
            case "blue":
                return ChatColor.BLUE;
            case "purple":
                return ChatColor.DARK_PURPLE;
            case "pink":
                return ChatColor.LIGHT_PURPLE;
            default:
                //Gold
                return ChatColor.GOLD;
        }
    }

    public MenuButton getBackButton(int slot, BandInventory inv) {
        return new MenuButton(slot, ItemUtil.create(Material.ARROW, ChatColor.GRAY + "Back"),
                ImmutableMap.of(ClickType.LEFT, p -> openInventory(p, inv)));
    }
}
