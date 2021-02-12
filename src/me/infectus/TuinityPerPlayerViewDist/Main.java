package me.infectus.TuinityPerPlayerViewDist;

import java.util.HashMap;
import java.util.logging.Level;
import lombok.extern.java.Log;
import me.infectus.TuinityPerPlayerViewDist.Commands.ListSendViewDistance;
import me.infectus.TuinityPerPlayerViewDist.Listeners.onPlayerLeave;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bstats.bukkit.Metrics;

@Log(topic="TuinityPerPlayerViewDist")
public class Main extends JavaPlugin {
    
    public static HashMap<String, Boolean> playerUsingPermBasedVd = new HashMap<>();
    
    @Override
    public void onEnable() {
        
        PluginManager pluginManager = getServer().getPluginManager();
        
        //check to make sure this server is supported
        try {
            Player.class.getMethod("setNoTickViewDistance", int.class);
            Player.class.getMethod("getNoTickViewDistance");
            Player.class.getMethod("getSendViewDistance");
        } catch (NoSuchMethodException | SecurityException e) {
            // TODO Auto-generated catch block
            pluginManager.disablePlugin(this);
            log.log(Level.SEVERE, "{0}Server is not running a compatible version of Tuinity. Plugin disabled.", ChatColor.RED);
            return;
        }
        
        //set up bStats
        int pluginId = 10301;
        Metrics metrics = new Metrics(this, pluginId);
        
        //register leave event so we can cleanup after ourselves
        pluginManager.registerEvents(new onPlayerLeave(), this);
        
        //register command
        PluginCommand cmd = this.getCommand("svd");
        if(cmd != null) cmd.setExecutor(new ListSendViewDistance(this));
        
        //asynchronously check player permissions once every 5 seconds and update their unticked view distance accordingly
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