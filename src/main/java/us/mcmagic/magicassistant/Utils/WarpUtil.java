package us.mcmagic.magicassistant.utils;

import com.legobuilder0813.MCMagicCore.MCMagicCore;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.Warp;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.Listener;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WarpUtil implements Listener {
	public static MagicAssistant pl;
	private static Connection connection;

	public WarpUtil(MagicAssistant instance) {
		pl = instance;
	}

	public synchronized static void openConnection() {
		try {
			connection = DriverManager.getConnection("jdbc:mysql://"
					+ MCMagicCore.config.getString("sql.ip") + ":"
					+ MCMagicCore.config.getString("sql.port") + "/"
					+ MCMagicCore.config.getString("sql.invdatabase"),
					MCMagicCore.config.getString("sql.username"),
					MCMagicCore.config.getString("sql.password"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public synchronized static void closeConnection() {
		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static boolean warpExists(String warp) {
		openConnection();
		try {
			PreparedStatement sql = connection
					.prepareStatement("SELECT * FROM `warps` WHERE name = ?");
			sql.setString(1, warp);
			ResultSet result = sql.executeQuery();
			boolean contains = result.next();
			result.close();
			sql.close();
			return contains;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			closeConnection();
		}
	}

	public static String getServer(String warp) {
		openConnection();
		try {
			PreparedStatement sql = connection
					.prepareStatement("SELECT * FROM `warps` WHERE name = ?");
			sql.setString(1, warp);
			ResultSet result = sql.executeQuery();
			result.next();
			String server = result.getString("server");
			result.close();
			sql.close();
			return server;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeConnection();
		}
		return "";
	}

	public static Location getLocation(String warp) {
		openConnection();
		try {
			PreparedStatement sql = connection
					.prepareStatement("SELECT * FROM `warps` WHERE name=?");
			sql.setString(1, warp);
			ResultSet result = sql.executeQuery();
			result.next();
			String world = result.getString("world");
			double x = result.getDouble("x");
			double y = result.getDouble("y");
			double z = result.getDouble("z");
			float yaw = result.getFloat("yaw");
			float pitch = result.getFloat("pitch");
			result.close();
			sql.close();
			return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeConnection();
		}
		return null;
	}

	@SuppressWarnings("deprecation")
	public static void crossServerWarp(final String uuid, final String warp,
			final String server) {
		try {
			ByteArrayOutputStream b = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(b);
			out.writeUTF("MagicWarp");
			out.writeUTF(uuid);
			out.writeUTF(server);
			out.writeUTF(warp);
			Bukkit.getPlayer(UUID.fromString(uuid)).sendPluginMessage(pl, "BungeeCord",
					b.toByteArray());
		} catch (IOException e) {
			Bukkit.getPlayer(UUID.fromString(uuid))
					.sendMessage(
							ChatColor.RED
									+ "There was a problem joining that server, please type /join instead!");
		}
	}

	public synchronized static List<Warp> getWarps() {
		List<Warp> warps = new ArrayList<>();
		openConnection();
		try {
			PreparedStatement sql = connection
					.prepareStatement("SELECT * FROM `warps`");
			ResultSet result = sql.executeQuery();
			while (result.next()) {
				Warp warp = new Warp(result.getString("name"),
						result.getString("server"), result.getDouble("x"),
						result.getDouble("y"), result.getDouble("z"),
						result.getFloat("yaw"), result.getFloat("pitch"),
						result.getString("world"));
				warps.add(warp);
			}
			result.close();
			sql.close();
			return warps;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} finally {
			closeConnection();
		}
	}

	public synchronized static void addWarp(Warp warp) {
		openConnection();
		try {
			PreparedStatement sql = connection
					.prepareStatement("INSERT INTO `warps` values(0,?,?,?,?,?,?,?,?)");
			sql.setString(1, warp.getName());
			sql.setDouble(2, warp.getX());
			sql.setDouble(3, warp.getY());
			sql.setDouble(4, warp.getZ());
			sql.setFloat(5, warp.getYaw());
			sql.setFloat(6, warp.getPitch());
			sql.setString(7, warp.getWorld().getName());
			sql.setString(8, warp.getServer());
			sql.execute();
			sql.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeConnection();
		}
	}

	public synchronized static void removeWarp(Warp warp) {
		openConnection();
		try {
			PreparedStatement sql = connection
					.prepareStatement("DELETE FROM `warps` WHERE name=?");
			sql.setString(1, warp.getName());
			sql.execute();
			sql.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeConnection();
		}
	}

	public static Warp findWarp(String name) {
		List<Warp> warps = MagicAssistant.warps;
		for (int i = 0; i < warps.size(); i++) {
			if (warps.get(i).getName().toLowerCase().equals(name.toLowerCase())) {
				return warps.get(i);
			}
		}
		return null;
	}

	public synchronized static void updateWarps() {
		try {
			ByteArrayOutputStream b = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(b);
			out.writeUTF("UpdateWarps");
			out.writeUTF(MagicAssistant.serverName);
			Bukkit.getServer().sendPluginMessage(pl, "BungeeCord",
					b.toByteArray());
		} catch (IOException e) {
			Bukkit.getServer()
					.getLogger()
					.severe("There was an error contacting the Bungee server to update Warps!");
		}
	}

	public synchronized static void refreshWarps() {
		MagicAssistant.warps.clear();
		for (Warp warp : WarpUtil.getWarps()) {
			MagicAssistant.warps.add(warp);
		}
	}
}