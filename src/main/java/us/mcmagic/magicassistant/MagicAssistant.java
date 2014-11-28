package us.mcmagic.magicassistant;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import us.mcmagic.magicassistant.commands.*;
import us.mcmagic.magicassistant.listeners.ChatListener;
import us.mcmagic.magicassistant.listeners.PlayerJoinAndLeave;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MagicAssistant extends JavaPlugin implements Listener {
	public static MagicAssistant plugin;
	public static Inventory ni;
	public int randomNumber = 0;
	public final HashMap<Player, ArrayList<Block>> watching = new HashMap<>();
	public final HashMap<Player, ArrayList<Block>> chattimeout = new HashMap<>();
	public static List<Warp> warps;
	public static String serverName;
	public static Location spawn;
	public static Location hub;
	private WorldGuardPlugin wg;

	public void onEnable() {
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(this, this);
		pm.registerEvents(new ChatListener(this), this);
		pm.registerEvents(new StitchEscape(this), this);
		pm.registerEvents(new PlayerJoinAndLeave(this), this);
		getConfig().options().copyDefaults(true);
		Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		saveConfig();
		getLogger().info("Magic Assistant is ready to help!");
	}

	public WorldGuardPlugin getWG() {
		return wg;
	}

	public void onDisable() {
		getLogger().info("Magic Assistant is taking a coffee break.");
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		if (label.equalsIgnoreCase("mb")) {
			Command_mb.execute(sender, label, args);
			return true;
		} else if (label.equalsIgnoreCase("bc")) {
			Command_bc.execute(sender, label, args);
			return true;
		} else if (label.equalsIgnoreCase("fly")) {
			Command_fly.execute(sender, label, args);
			return true;
		} else if (label.equalsIgnoreCase("vanish")
				|| label.equalsIgnoreCase("v")) {
			Command_vanish.execute(sender, label, args);
			return true;
		} else if (label.equalsIgnoreCase("warp")) {
			Command_warp.execute(label, sender, args);
			return true;
		} else if (label.equalsIgnoreCase("setwarp")) {
			Command_setwarp.execute(sender, label, args);
			return true;
		} else if (label.equalsIgnoreCase("uwarp")) {
			Command_uwarp.execute(sender, label, args);
			return true;
		} else if (label.equalsIgnoreCase("wrl")) {
			Command_wrl.execute(sender, label, args);
			return true;
		} else if (label.equalsIgnoreCase("invsee")) {
			Command_invsee.execute(sender, label, args);
			return true;
		} else if (label.equalsIgnoreCase("enderchest")) {
			Command_enderchest.execute(sender, label, args);
			return true;
		} else if (label.equalsIgnoreCase("give")
				|| label.equalsIgnoreCase("item")
				|| label.equalsIgnoreCase("i")) {
			Command_give.execute(sender, label, args);
		} else if (label.equalsIgnoreCase("delwarp")
				|| label.equalsIgnoreCase("removewarp")) {
			Command_delwarp.execute(sender, label, args);
			return true;
		} else if (label.equalsIgnoreCase("delay")) {
			Command_delay.execute(sender, args);
			return true;
		} else if (label.equalsIgnoreCase("spawn")) {
			Command_spawn.execute(sender, label, args);
			return true;
		} else if (label.equalsIgnoreCase("setspawn")) {
			Command_setspawn.execute(sender, label, args);
			return true;
		} else if (label.equalsIgnoreCase("smite")) {
			Command_smite.execute(sender, label, args);
			return true;
		} else if (label.equalsIgnoreCase("more")) {
			Command_more.execute(sender, label, args);
			return true;
		} else if (label.equalsIgnoreCase("heal")) {
			Command_heal.execute(sender, label, args);
			return true;
		} else if (label.equalsIgnoreCase("helpop")
				|| label.equalsIgnoreCase("ac")) {
			Command_helpop.execute(sender, label, args);
		} else if (label.equalsIgnoreCase("ptime")) {
			Command_ptime.execute(sender, label, args);
			return true;
		} else if (label.equalsIgnoreCase("pweather")) {
			Command_pweather.execute(sender, label, args);
			return true;
		} else if (label.equalsIgnoreCase("invcheck")) {
			Command_invcheck.execute(sender, label, args);
			return true;
		} else if (label.equalsIgnoreCase("day")) {
			Command_day.execute(label, args, sender);
			return true;
		} else if (label.equalsIgnoreCase("night")) {
			Command_night.execute(label, args, sender);
			return true;
		} else if (label.equalsIgnoreCase("noon")) {
			Command_noon.execute(label, args, sender);
			return true;
		} else if (label.equalsIgnoreCase("gwts")) {
			// List of types of hats:
			ItemStack clear = new ItemStack(Material.AIR);
			ItemStack red = new ItemStack(Material.LEATHER_HELMET, 1);
			ItemStack orj = new ItemStack(Material.LEATHER_HELMET, 1);
			ItemStack yel = new ItemStack(Material.LEATHER_HELMET, 1);
			ItemStack grn = new ItemStack(Material.LEATHER_HELMET, 1);
			ItemStack blu = new ItemStack(Material.LEATHER_HELMET, 1);
			ItemStack pur = new ItemStack(Material.LEATHER_HELMET, 1);
			ItemStack wht = new ItemStack(Material.LEATHER_HELMET, 1);
			ItemStack blk = new ItemStack(Material.LEATHER_HELMET, 1);
			ItemStack pnk = new ItemStack(Material.LEATHER_HELMET, 1);
			ItemStack aqa = new ItemStack(Material.LEATHER_HELMET, 1);
			LeatherArmorMeta rarm = (LeatherArmorMeta) red.getItemMeta();
			LeatherArmorMeta oarm = (LeatherArmorMeta) orj.getItemMeta();
			LeatherArmorMeta yarm = (LeatherArmorMeta) yel.getItemMeta();
			LeatherArmorMeta garm = (LeatherArmorMeta) grn.getItemMeta();
			LeatherArmorMeta barm = (LeatherArmorMeta) blu.getItemMeta();
			LeatherArmorMeta parm = (LeatherArmorMeta) pur.getItemMeta();
			LeatherArmorMeta warm = (LeatherArmorMeta) wht.getItemMeta();
			LeatherArmorMeta blarm = (LeatherArmorMeta) blk.getItemMeta();
			LeatherArmorMeta piarm = (LeatherArmorMeta) pnk.getItemMeta();
			LeatherArmorMeta aqam = (LeatherArmorMeta) aqa.getItemMeta();
			rarm.setColor(Color.fromRGB(170, 0, 0));
			oarm.setColor(Color.fromRGB(255, 102, 0));
			yarm.setColor(Color.fromRGB(255, 222, 0));
			garm.setColor(Color.fromRGB(0, 153, 0));
			barm.setColor(Color.fromRGB(51, 51, 255));
			parm.setColor(Color.fromRGB(39, 31, 155));
			warm.setColor(Color.fromRGB(255, 255, 255));
			blarm.setColor(Color.fromRGB(0, 0, 0));
			piarm.setColor(Color.fromRGB(255, 0, 255));
			aqam.setColor(Color.fromRGB(0, 255, 255));
			rarm.setDisplayName(ChatColor.LIGHT_PURPLE + "Mickey Ears");
			oarm.setDisplayName(ChatColor.LIGHT_PURPLE + "Mickey Ears");
			yarm.setDisplayName(ChatColor.LIGHT_PURPLE + "Mickey Ears");
			garm.setDisplayName(ChatColor.LIGHT_PURPLE + "Mickey Ears");
			barm.setDisplayName(ChatColor.LIGHT_PURPLE + "Mickey Ears");
			parm.setDisplayName(ChatColor.LIGHT_PURPLE + "Mickey Ears");
			warm.setDisplayName(ChatColor.LIGHT_PURPLE + "Mickey Ears");
			blarm.setDisplayName(ChatColor.LIGHT_PURPLE + "Mickey Ears");
			piarm.setDisplayName(ChatColor.LIGHT_PURPLE + "Mickey Ears");
			aqam.setDisplayName(ChatColor.LIGHT_PURPLE + "Mickey Ears");
			red.setItemMeta(rarm);
			orj.setItemMeta(oarm);
			yel.setItemMeta(yarm);
			grn.setItemMeta(garm);
			blu.setItemMeta(barm);
			pur.setItemMeta(parm);
			wht.setItemMeta(warm);
			blk.setItemMeta(blarm);
			pnk.setItemMeta(piarm);
			aqa.setItemMeta(aqam);
			// Referring to console
			if (!(sender instanceof Player)) {
				if (args.length == 0) {
					sender.sendMessage(ChatColor.DARK_AQUA
							+ "/gwts <red,orange,yellow,green,blue,aqua,purple,pink,white,black,done> [player]");
				} else if (args.length == 1) {
					sender.sendMessage(ChatColor.RED
							+ "Error: Cannot execute command through console");
				} else if (args.length == 2) {
					Player tp = sender.getServer().getPlayer(args[1]);
					PlayerInventory tpi = tp.getInventory();
					if (args[0].equals("red")) {
						tpi.setHelmet(red);
					} else if (args[0].equalsIgnoreCase("orange")) {
						tpi.setHelmet(orj);
					} else if (args[0].equalsIgnoreCase("yellow")) {
						tpi.setHelmet(yel);
					} else if (args[0].equalsIgnoreCase("green")) {
						tpi.setHelmet(grn);
					} else if (args[0].equalsIgnoreCase("blue")) {
						tpi.setHelmet(blu);
					} else if (args[0].equalsIgnoreCase("aqua")) {
						tpi.setHelmet(aqa);
					} else if (args[0].equalsIgnoreCase("purple")) {
						tpi.setHelmet(pur);
					} else if (args[0].equalsIgnoreCase("white")) {
						tpi.setHelmet(wht);
					} else if (args[0].equalsIgnoreCase("black")) {
						tpi.setHelmet(blk);
					} else if (args[0].equalsIgnoreCase("pink")) {
						tpi.setHelmet(pnk);
					} else if (args[0].equalsIgnoreCase("done")) {
						tpi.remove(red);
						tpi.remove(orj);
						tpi.remove(yel);
						tpi.remove(grn);
						tpi.remove(blu);
						tpi.remove(pur);
						tpi.remove(wht);
						tpi.remove(blk);
						tpi.remove(pnk);
						tpi.remove(aqa);
						tpi.setHelmet(clear);
					}
				}
				// As a player command
			} else if (sender.hasPermission("magicassistant.gwts")) {
				Player player = (Player) sender;
				if (args.length == 0) {
					player.sendMessage(ChatColor.DARK_AQUA
							+ "/gwts <red,orange,yellow,green,blue,aqua,purple,pink,white,black,done> [player]");
				} else if (args.length == 1) {
					PlayerInventory pi = player.getInventory();
					if (args[0].equalsIgnoreCase("red")) {
						pi.setHelmet(red);
					} else if (args[0].equalsIgnoreCase("orange")) {
						pi.setHelmet(orj);
					} else if (args[0].equalsIgnoreCase("yellow")) {
						pi.setHelmet(yel);
					} else if (args[0].equalsIgnoreCase("green")) {
						pi.setHelmet(grn);
					} else if (args[0].equalsIgnoreCase("blue")) {
						pi.setHelmet(blu);
					} else if (args[0].equalsIgnoreCase("aqua")) {
						pi.setHelmet(aqa);
					} else if (args[0].equalsIgnoreCase("purple")) {
						pi.setHelmet(pur);
					} else if (args[0].equalsIgnoreCase("white")) {
						pi.setHelmet(wht);
					} else if (args[0].equalsIgnoreCase("black")) {
						pi.setHelmet(blk);
					} else if (args[0].equalsIgnoreCase("pink")) {
						pi.setHelmet(pnk);
					} else if (args[0].equalsIgnoreCase("done")) {
						pi.remove(red);
						pi.remove(orj);
						pi.remove(yel);
						pi.remove(grn);
						pi.remove(blu);
						pi.remove(pur);
						pi.remove(wht);
						pi.remove(blk);
						pi.remove(pnk);
						pi.remove(aqa);
						pi.setHelmet(clear);
					}
				} else if (args.length == 2) {
					Player tp = sender.getServer().getPlayer(args[1]);
					PlayerInventory tpi = tp.getInventory();
					if (args[0].equals("red")) {
						tpi.setHelmet(red);
					} else if (args[0].equals("orange")) {
						tpi.setHelmet(orj);
					} else if (args[0].equals("yellow")) {
						tpi.setHelmet(yel);
					} else if (args[0].equals("green")) {
						tpi.setHelmet(grn);
					} else if (args[0].equals("blue")) {
						tpi.setHelmet(blu);
					} else if (args[0].equalsIgnoreCase("aqua")) {
						tpi.setHelmet(aqa);
					} else if (args[0].equals("purple")) {
						tpi.setHelmet(pur);
					} else if (args[0].equals("white")) {
						tpi.setHelmet(wht);
					} else if (args[0].equals("black")) {
						tpi.setHelmet(blk);
					} else if (args[0].equalsIgnoreCase("pink")) {
						tpi.setHelmet(pnk);
					} else if (args[0].equals("done")) {
						tpi.remove(red);
						tpi.remove(orj);
						tpi.remove(yel);
						tpi.remove(grn);
						tpi.remove(blu);
						tpi.remove(pur);
						tpi.remove(wht);
						tpi.remove(blk);
						tpi.remove(pnk);
						tpi.remove(aqa);
						tpi.setHelmet(clear);
					}
				}
			}
		} else if (label.equalsIgnoreCase("magicassistant")) {
			if (args.length == 0) {
				Player player = (Player) sender;
				player.sendMessage(ChatColor.GREEN
						+ "----------------------------------------------------");
				player.sendMessage(ChatColor.DARK_GREEN
						+ "Magic assistant was created by " + ChatColor.BLUE
						+ "Legobuilder0813");
				player.sendMessage(ChatColor.DARK_GREEN
						+ "Version Number: "
						+ ChatColor.GOLD
						+ Bukkit.getServer().getPluginManager()
								.getPlugin("magicassistant").getDescription()
								.getVersion());
				player.sendMessage(ChatColor.GREEN
						+ "----------------------------------------------------");
			} else if (args.length == 1) {
				Player player = (Player) sender;
				if (args[0].equalsIgnoreCase("reload")
						&& player.hasPermission("magicassistant.reload")) {
					player.sendMessage(ChatColor.AQUA + "[magicassistant]"
							+ ChatColor.BLUE + " Now reloading configuration");
					this.reloadConfig();
					player.sendMessage(ChatColor.AQUA + "[magicassistant]"
							+ ChatColor.BLUE + " Configuration reloaded!");
				}
			}
		} else if (label.equalsIgnoreCase("save")) {
			if (!(sender instanceof Player)) {
				return true;
			}
			Player player = (Player) sender;
			player.performCommand("save-all");
		} else if (label.equalsIgnoreCase("tp")) {
			Command_Tp.execute(label, args, sender);
			return true;
		} else if (label.equalsIgnoreCase("head")) {
			Player player = (Player) sender;
			PlayerInventory pi = player.getInventory();
			if (args.length == 1) {
				ItemStack head = new ItemStack(Material.SKULL_ITEM, 1,
						(short) 3);
				SkullMeta headm = (SkullMeta) head.getItemMeta();
				headm.setOwner(args[0]);
				head.setItemMeta(headm);
				pi.addItem(head);
				player.sendMessage(ChatColor.AQUA + "[magicassistant]"
						+ ChatColor.BLUE + " Enjoy your new head of " + args[0]
						+ "!");
			} else {
				player.sendMessage(ChatColor.RED + "/head [playerhead]");
			}
		}
		return false;
	}

	public static boolean isInt(String s) {
		try {
			Integer.parseInt(s);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public static void sendToServer(Player player, String server) {
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);
		try {
			out.writeUTF("Connect");
			out.writeUTF(server);
			player.sendPluginMessage(
					Bukkit.getPluginManager().getPlugin("magicassistant"),
					"BungeeCord", b.toByteArray());
		} catch (Exception e) {
			player.sendMessage(ChatColor.RED
					+ "Sorry! It looks like something went wrong! It's probably out fault. We will try to fix it as soon as possible!");
		}
	}

	public static YamlConfiguration config() {
		return YamlConfiguration.loadConfiguration(new File(
				"plugins/magicassistant/config.yml"));
	}

	public static boolean isInPermGroup(Player player, String group) {
		String[] groups = WorldGuardPlugin.inst().getGroups(player);
		for (int i = 0; i < groups.length; i++) {
			if (groups[i].toLowerCase().equals(group.toLowerCase())) {
				return true;
			}
		}
		return false;
	}
}