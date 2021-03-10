package me.infectus.TuinityPerPlayerViewDist;

import static java.lang.Integer.min;
import java.util.HashMap;
import java.util.Map;
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
    
    private static ConfigManager configManager;
    
    @Override
    public void onEnable() {
        
        PluginManager pluginManager = getServer().getPluginManager();
        
        //check to make sure this server is supported
        try {
            Player.class.getMethod("setNoTickViewDistance", int.class);
            Player.class.getMethod("getNoTickViewDistance");
            Player.class.getMethod("getSendViewDistance");
        } catch (NoSuchMethodException | SecurityException e) {
            pluginManager.disablePlugin(this);
            log.log(Level.SEVERE, "{0}Server is not running a compatible version of Tuinity. Plugin disabled.", ChatColor.RED);
            return;
        }
        
        //set up bStats
        int pluginId = 10301;
        Metrics metrics = new Metrics(this, pluginId);
        
        configManager = new ConfigManager(this);
        
        //register leave event so we can cleanup after ourselves
        pluginManager.registerEvents(new onPlayerLeave(), this);
        
        //register command
        PluginCommand cmd = this.getCommand("svd");
        if(cmd != null) cmd.setExecutor(new ListSendViewDistance(this));
        
        //get update interval
        long intervalTicks = configManager.getLong(ConfigManager.CFG_UPDATE_INTERVAL) * 20;
        
        //get per world y-level vd limit
        Map<String, Object> worldsCfg = configManager.getWorlds();
        
        //asynchronously check players at the interval from the config and update their unticked view distance accordingly
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            Bukkit.getOnlinePlayers().forEach((player) -> {
                String worldName = player.getWorld().getName();
                int yLevelVD = 32;
                int yLevel = 0;
                if(worldsCfg != null && worldsCfg.containsKey(worldName)) {
                    Map<String, Integer> worldCfg = (Map<String, Integer>) worldsCfg.get(worldName);
                    yLevel = worldCfg.get("y");
                    if(player.getLocation().getBlockY() < yLevel)
                        yLevelVD = worldCfg.get("vd");
                }
                int vd = min(getVdPermission(player), yLevelVD);
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
                if(vd < yLevelVD) {
                    log.log(Level.INFO, "{0}Un-ticked view-distance changed from {1} to {2} for player {3} via permissions.", new Object[]{ChatColor.AQUA, currentVd, vd, pName});
                } else {
                    log.log(Level.INFO, "{0}Un-ticked view-distance changed from {1} to {2} for player {3} becase they are below y-level {4} in {5}.", new Object[]{ChatColor.AQUA, currentVd, vd, pName, yLevel, worldName});
                }
            });
            
        },
        0L,
        (intervalTicks < 20) ? 20L : intervalTicks); //prevent values less than 1 second.
    }
    
    public int getVdPermission(Player player) {
        String permissionPrefix = "vd.max";

        for (PermissionAttachmentInfo attachmentInfo : player.getEffectivePermissions()) {
            String permission = attachmentInfo.getPermission();
            if (permission.startsWith(permissionPrefix)) {
                return Integer.parseInt(permission.substring(permission.lastIndexOf(".") + 1));
            }
        }

        return player.getWorld().getNoTickViewDistance(); //return the server setting if no nodes exist. We have to do this as opposed to setting -1, so it will work properly with the y-level limits
    }

}