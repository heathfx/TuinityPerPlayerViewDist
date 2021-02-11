package me.infectus.TuinityPerPlayerViewDist;

import java.util.HashMap;
import java.util.logging.Level;
import lombok.extern.java.Log;
import me.infectus.TuinityPerPlayerViewDist.Commands.ListSendViewDistance;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;


@Log
public class Main extends JavaPlugin {
    
    public HashMap<String, Boolean> playerUsingPermBasedVd = new HashMap<>();
    
    @Override
    public void onEnable() {
        
        this.getCommand("svd").setExecutor(new ListSendViewDistance(this));
        
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            Bukkit.getOnlinePlayers().forEach((player) -> {
                int vd = getVdPermission(player);
                int currentVd = player.getNoTickViewDistance();

                if(vd == currentVd) return; //nothing to do for this player.

                String pName = player.getName();
                if(vd == -1) {
                    if(playerUsingPermBasedVd.containsKey(pName) && playerUsingPermBasedVd.get(pName)) {
                        player.setNoTickViewDistance(vd);
                        playerUsingPermBasedVd.put(pName, false);
                        log.log(Level.INFO, "{0}Un-ticked view-distance changed from {1} to the server default for player {2} because no vd.max.<vd> permission node was found.", new Object[]{ChatColor.AQUA, currentVd, pName});
                    }
                    return;
                }

                if(vd > 32) vd = 32; //cap the max vd to 32 chunks

                player.setNoTickViewDistance(vd);
                playerUsingPermBasedVd.put(pName, true);
                log.log(Level.INFO, "{0}Un-ticked view-distance changed from {1} to {2} for player {3} via permissions.", new Object[]{ChatColor.AQUA, currentVd, vd, pName});
            });
            
        },
        0L,
        100L);
    }
    
    public int getVdPermission(Player player) {
        String permissionPrefix = "vd.max";

        for (PermissionAttachmentInfo attachmentInfo : player.getEffectivePermissions()) {
            String permission = attachmentInfo.getPermission();
            if (permission.startsWith(permissionPrefix)) {
                return Integer.parseInt(permission.substring(permission.lastIndexOf(".") + 1));
            }
        }

        return -1;
    }

}