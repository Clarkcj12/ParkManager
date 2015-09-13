package us.mcmagic.magicassistant.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.commands.Commandvanish;
import us.mcmagic.magicassistant.handlers.PlayerData;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.permissions.Rank;
import us.mcmagic.mcmagiccore.player.User;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class VisibleUtil {
    private List<UUID> hideall = new ArrayList<>();
    private List<UUID> hidden = new ArrayList<>();

    public VisibleUtil() {
        if (!MCMagicCore.getMCMagicConfig().serverName.equalsIgnoreCase("hub")) {
            return;
        }
        Bukkit.getScheduler().runTaskTimer(MagicAssistant.getInstance(), new Runnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (!hidden.contains(player.getUniqueId()) && player.getLocation().distance(MagicAssistant.spawn) <= 5) {
                        User user = MCMagicCore.getUser(player.getUniqueId());
                        if (user.getRank().getRankId() < Rank.SPECIALGUEST.getRankId()) {
                            hidden.add(player.getUniqueId());
                            vanish(player);
                        }
                        return;
                    }
                    if (hidden.contains(player.getUniqueId()) && player.getLocation().distance(MagicAssistant.spawn) > 5) {
                        hidden.remove(player.getUniqueId());
                        show(player);
                    }
                }
            }
        }, 0L, 20L);
    }

    private void vanish(Player player) {
        for (Player tp : Bukkit.getOnlinePlayers()) {
            if (tp.getUniqueId().equals(player.getUniqueId())) {
                continue;
            }
            tp.hidePlayer(player);
        }
    }

    private void show(Player player) {
        for (Player tp : Bukkit.getOnlinePlayers()) {
            if (tp.getUniqueId().equals(player.getUniqueId())) {
                continue;
            }
            tp.showPlayer(player);
        }
    }

    public void logout(Player player) {
        hideall.remove(player.getUniqueId());
        hidden.remove(player.getUniqueId());
    }

    public void addToHideAll(final Player player) {
        PlayerData data = MagicAssistant.getPlayerData(player.getUniqueId());
        List<UUID> friends = data.getFriendList();
        hideall.add(player.getUniqueId());
        for (User user : MCMagicCore.getUsers()) {
            Player tp = Bukkit.getPlayer(user.getUniqueId());
            if (tp == null) {
                continue;
            }
            if (friends.contains(user.getUniqueId()) && user.getRank().getRankId() < Rank.SPECIALGUEST.getRankId()) {
                player.showPlayer(tp);
                continue;
            }
            if (!tp.getUniqueId().equals(player.getUniqueId())) {
                if (user.getRank().getRankId() < Rank.SPECIALGUEST.getRankId()) {
                    player.hidePlayer(tp);
                }
            }
        }
    }

    public void removeFromHideAll(final Player player) {
        PlayerData data = MagicAssistant.getPlayerData(player.getUniqueId());
        List<UUID> friends = data.getFriendList();
        hideall.remove(player.getUniqueId());
        for (User user : MCMagicCore.getUsers()) {
            Player tp = Bukkit.getPlayer(user.getUniqueId());
            if (tp == null) {
                continue;
            }
            if (!tp.getUniqueId().equals(player.getUniqueId())) {
                if (user.getRank().getRankId() < Rank.SPECIALGUEST.getRankId()) {
                    player.showPlayer(tp);
                }
            }
        }
    }

    public boolean isInHideAll(UUID uuid) {
        return hideall.contains(uuid);
    }

    public void login(Player player) {
        PlayerData data = MagicAssistant.getPlayerData(player.getUniqueId());
        List<UUID> friends = data.getFriendList();
        for (UUID uuid : hideall) {
            if (friends.contains(uuid)) {
                continue;
            }
            Bukkit.getPlayer(uuid).hidePlayer(player);
        }
        for (UUID uuid : Commandvanish.getHidden()) {
            player.hidePlayer(Bukkit.getPlayer(uuid));
        }
    }

}