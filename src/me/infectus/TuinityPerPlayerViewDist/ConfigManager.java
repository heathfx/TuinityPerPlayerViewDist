package me.infectus.TuinityPerPlayerViewDist;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import lombok.extern.java.Log;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

@Log(topic="TuinityPerPlayerViewDist")
public class ConfigManager {
    
    public static final String CFG_UPDATE_INTERVAL = "player-check-interval";
    public static final String CFG_WORLDS = "worlds";
    public static final String CFG_YLEVEL_REDUCE_VD = "reduce-view-distance-below-y";
    public static final String CFG_VD_REDUCE_VD = "view-distance";
    public static final String CFG_DEBUG = "verbose-logging";
    
    private FileConfiguration config;
    

    public ConfigManager(Plugin plugin) {
        this.config = plugin.getConfig();
        plugin.saveDefaultConfig();
    }

    public boolean getBoolean(String name) {
        return this.config.getBoolean(name);
    }

    public int getInt(String name) {
        return this.config.getInt(name);
    }
    
    public long getLong(String name) {
        return this.config.getLong(name);
    }

    public String getString(String name) {
        return this.config.getString(name);
    }
    
    public Map<String, Object> getWorlds() {
        
        
        HashMap<String, Object> worldsCfg = new HashMap<>();

        for(String world : this.config.getConfigurationSection(CFG_WORLDS).getKeys(false)) {
            if(world != null) {
                HashMap<String, Integer> worldCfg = new HashMap<>();
                worldCfg.put("y", this.config.getInt(CFG_WORLDS + "." + world + "." + CFG_YLEVEL_REDUCE_VD));
                worldCfg.put("vd", this.config.getInt(CFG_WORLDS + "." + world + "." + CFG_VD_REDUCE_VD));
                worldsCfg.put(world, worldCfg);
                if(this.config.getBoolean(CFG_DEBUG)) log.log(Level.INFO, "{0}Added {1} to worlds config.", new Object[]{ChatColor.AQUA, world});
                
            }
        }
        return worldsCfg;
    }

}
