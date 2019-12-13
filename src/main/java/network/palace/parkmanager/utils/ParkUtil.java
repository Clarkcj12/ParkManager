package network.palace.parkmanager.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import network.palace.core.Core;
import network.palace.core.utils.TextUtil;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.Park;
import network.palace.parkwarp.ParkWarp;
import network.palace.parkwarp.handlers.Warp;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ParkUtil {
    private List<Park> parks = new ArrayList<>();

    public ParkUtil() {
        FileUtil.FileSubsystem subsystem = ParkManager.getFileUtil().getRootSubsystem();
        try {
            JsonElement element = subsystem.getFileContents("parks");
            if (element.isJsonArray()) {
                JsonArray array = element.getAsJsonArray();
                for (JsonElement entry : array) {
                    JsonObject object = entry.getAsJsonObject();

                    String id = object.get("id").getAsString();
                    String displayName = object.get("displayName").getAsString();
                    Warp warp = ParkWarp.getWarpUtil().findWarp(object.get("warp").getAsString());
                    World world = Bukkit.getWorld(object.get("world").getAsString());

                    ProtectedRegion region;
                    try {
                        region = WorldGuardPlugin.inst()
                                .getRegionManager(world)
                                .getRegion(object.get("region").getAsString());
                    } catch (Exception e) {
                        Core.logMessage("ParkUtil", "There was an error loading the '" + id + "' park: Invalid region " +
                                object.get("region").getAsString() + " in world " + object.get("world").getAsString());
                        continue;
                    }

                    parks.add(new Park(id, displayName, warp, world, region));
                }
            }
            saveToFile();
            Core.logMessage("ParkUtil", "Loaded " + parks.size() + " park" + TextUtil.pluralize(parks.size()) + "!");
        } catch (IOException e) {
            Core.logMessage("ParkUtil", "There was an error loading the ParkUtil config!");
            e.printStackTrace();
        }
    }

    public List<Park> getParks() {
        return new ArrayList<>(parks);
    }

    public Park getPark(String id) {
        for (Park park : getParks()) {
            if (park.getId().equals(id)) {
                return park;
            }
        }
        return null;
    }

    public void addPark(Park park) {
        parks.add(park);
        saveToFile();
    }

    public boolean removePark(String id) {
        Park park = getPark(id);
        if (park == null) return false;
        parks.remove(park);
        saveToFile();
        return true;
    }

    public void saveToFile() {
        JsonArray array = new JsonArray();
        for (Park park : parks) {
            JsonObject object = new JsonObject();
            object.addProperty("id", park.getId());
            object.addProperty("displayName", park.getDisplayName());
            object.addProperty("warp", park.getWarp().getName());
            object.addProperty("world", park.getWorld().getName());
            object.addProperty("region", park.getRegion().getId());
            array.add(object);
        }
        try {
            ParkManager.getFileUtil().getRootSubsystem().writeFileContents("parks", array);
        } catch (IOException e) {
            Core.logMessage("ParkUtil", "There was an error writing to parks.json!");
            e.printStackTrace();
        }
    }
}
