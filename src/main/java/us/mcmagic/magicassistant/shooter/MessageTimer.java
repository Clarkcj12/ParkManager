package us.mcmagic.magicassistant.shooter;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.mcmagiccore.actionbar.ActionBarManager;

import java.util.UUID;

/**
 * Created by Marc on 1/19/15
 */
public class MessageTimer {

    public static void start() {
        Bukkit.getScheduler().runTaskTimer(MagicAssistant.getInstance(), new Runnable() {
            @Override
            public void run() {
                String msg = "";
                switch (MagicAssistant.shooter.game) {
                    case "buzz":
                        msg = ChatColor.BLUE + "" + ChatColor.BOLD + "Buzz Points: " + ChatColor.GREEN + "" +
                                ChatColor.BOLD;
                        break;
                    case "tsm":
                        msg = ChatColor.GOLD + "" + ChatColor.BOLD + "Toy Story Mania Points: " + ChatColor.GREEN + ""
                                + ChatColor.BOLD;
                        break;
                    case "mm":
                        msg = ChatColor.RED + "" + ChatColor.BOLD + "Monstropolis Mayhem Points: " + ChatColor.YELLOW +
                                "" + ChatColor.BOLD;
                        break;
                }
                for (UUID uuid : MagicAssistant.shooter.getIngame()) {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player == null) {
                        continue;
                    }
                    ActionBarManager.sendMessage(player, msg + player.getMetadata("shooter").get(0).asInt());
                }
            }
        }, 0, 20L);
    }
}