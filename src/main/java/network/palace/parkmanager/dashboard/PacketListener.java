package network.palace.parkmanager.dashboard;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import network.palace.core.Core;
import network.palace.core.dashboard.packets.dashboard.PacketAudioConnect;
import network.palace.core.dashboard.packets.dashboard.PacketUpdateEconomy;
import network.palace.core.events.DashboardConnectEvent;
import network.palace.core.events.DashboardDisconnectEvent;
import network.palace.core.events.IncomingPacketEvent;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.dashboard.packets.parks.PacketImAPark;
import network.palace.parkmanager.dashboard.packets.parks.PacketInventoryContent;
import network.palace.parkmanager.dashboard.packets.parks.PacketShowRequestResponse;
import network.palace.parkmanager.dashboard.packets.parks.queue.CreateQueuePacket;
import network.palace.parkmanager.dashboard.packets.parks.queue.RemoveQueuePacket;
import network.palace.parkmanager.dashboard.packets.parks.queue.UpdateQueuePacket;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;

public class PacketListener implements Listener {

    @EventHandler
    public void onIncomingPacket(IncomingPacketEvent event) {
        String data = event.getPacket();
        JsonObject object = (JsonObject) new JsonParser().parse(data);
        if (!object.has("id")) {
            return;
        }
        int id = object.get("id").getAsInt();
        switch (id) {
            /*
             * Connected to
             * Audio server
             */
            case 51: {
                PacketAudioConnect packet = new PacketAudioConnect().fromJSON(object);
                UUID uuid = packet.getUniqueId();
                CPlayer player = Core.getPlayerManager().getPlayer(uuid);
                if (player != null) {
                    player.giveAchievement(16);
                }
                break;
            }
            /*
             * Cross-server Inventory
             */
            case 58: {
                PacketInventoryContent packet = new PacketInventoryContent().fromJSON(object);
//                Core.runTask(ParkManager.getInstance(), () -> ParkManager.getStorageManager().processIncomingPacket(packet));
                break;
            }
            /*
             * Refresh Hotel Rooms
             */
            case 59: {
                break;
            }
            /*
             * Update economy
             * Used for voting
             */
            case 67: {
                PacketUpdateEconomy packet = new PacketUpdateEconomy().fromJSON(object);
                UUID uuid = packet.getUniqueId();
//                ParkManager.getFpKioskManager().updateVoteData(uuid);
                break;
            }
            /*
             * Shareholder Show Request response
             */
            case 78: {
                PacketShowRequestResponse packet = new PacketShowRequestResponse().fromJSON(object);
                ParkManager.getShowMenuManager().handlePacket(packet);
                break;
            }
            /*
             * Create VirtualQueue
             */
            case 81: {
                ParkManager.getVirtualQueueManager().handleCreate(new CreateQueuePacket().fromJSON(object));
                break;
            }
            /*
             * Remove VirtualQueue
             */
            case 82: {
                ParkManager.getVirtualQueueManager().handleRemove(new RemoveQueuePacket().fromJSON(object));
                break;
            }
            /*
             * Update VirtualQueue
             */
            case 83: {
                ParkManager.getVirtualQueueManager().handleUpdate(new UpdateQueuePacket().fromJSON(object));
                break;
            }
        }
    }

    @EventHandler
    public void onDashboardConnect(DashboardConnectEvent event) {
        Core.getDashboardConnection().send(new PacketImAPark());
    }

    @EventHandler
    public void onDashboardDisconnect(DashboardDisconnectEvent event) {
        ParkManager.getVirtualQueueManager().clearQueues();
    }
}
