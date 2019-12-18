package network.palace.parkmanager.listeners;

import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.RideCount;
import network.palace.ridemanager.events.RideEndEvent;
import network.palace.ridemanager.handlers.ride.Ride;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.TreeMap;
import java.util.UUID;

public class RideListener implements Listener {

    @EventHandler
    public void onRideEnd(RideEndEvent event) {
        Ride ride = event.getRide();
        ParkManager parkManager = ParkManager.getInstance();
        String finalRideName = ChatColor.stripColor(ride.getName());
        Core.runTaskAsynchronously(() -> {
            UUID[] players = event.getPlayers();
            for (UUID uuid : players) {
                CPlayer tp = Core.getPlayerManager().getPlayer(uuid);
                if (tp == null) continue;
                Core.getMongoHandler().logRideCounter(tp.getUniqueId(), finalRideName);

                TreeMap<String, RideCount> rides = ParkManager.getRideCounterUtil().getRideCounters(tp);
                if (rides.containsKey(finalRideName)) {
                    rides.get(finalRideName).addCount(1);
                } else {
                    rides.put(finalRideName, new RideCount(finalRideName, Core.getServerType()));
                }
                if (rides.size() >= 30) {
                    tp.giveAchievement(15);
                } else if (rides.size() >= 20) {
                    tp.giveAchievement(14);
                } else if (rides.size() >= 10) {
                    tp.giveAchievement(13);
                } else if (rides.size() >= 1) {
                    tp.giveAchievement(12);
                }

                tp.sendMessage(ChatColor.GREEN + "--------------" + ChatColor.GOLD + "" + ChatColor.BOLD +
                        "Ride Counter" + ChatColor.GREEN + "-------------\n" + ChatColor.YELLOW +
                        "Ride Counter for " + ChatColor.AQUA + finalRideName + ChatColor.YELLOW +
                        " is now at " + ChatColor.AQUA + rides.get(finalRideName).getCount() +
                        ChatColor.GREEN + "\n----------------------------------------");
                tp.playSound(tp.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 100f, 0.75f);
            }
        });
    }

}
