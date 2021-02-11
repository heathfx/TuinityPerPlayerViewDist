package me.infectus.TuinityPerPlayerViewDist;

import java.util.Arrays;
import java.util.logging.Level;
import lombok.extern.java.Log;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;


@Log
public class Main extends JavaPlugin {
    

    @Override
    public void onEnable() {
        /*
        //check if running on Tuinity with support for per-player vd
        PluginManager pluginManager = getServer().getPluginManager();
        boolean isTuinity = false;
        try {
            if(Arrays.toString(Class.forName("org.bukkit.entity.Player").getInterfaces()).contains("setNoTickViewDistance")) isTuinity = true;
        } catch (ClassNotFoundException exception) {
            isTuinity = false;
        }
        if (!isTuinity) {
            log.log(Level.INFO, "{0}This plugin requires Tuinity with per-player view-distance to function; disabling plugin", ChatColor.RED);
            pluginManager.disablePlugin(this);
            return;
        }
        */
        
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                Bukkit.getOnlinePlayers().forEach((player) -> {
                    int vd = getVdPermission(player);
                    if(vd == -1) return;
                    if(vd > 32) vd = 32;
                    int currentVd = player.getNoTickViewDistance();
                    if(vd == currentVd) return;
                    player.setNoTickViewDistance(vd);
                    log.log(Level.INFO, "{0}Un-ticked view-distance changed from {1} to {2} for player {3} via permissions.", new Object[]{ChatColor.AQUA, currentVd, vd, player.getName()});

                });
            }
        },
        0L,
        20L);
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
