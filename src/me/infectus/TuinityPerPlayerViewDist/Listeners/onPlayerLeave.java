package me.infectus.TuinityPerPlayerViewDist.Listeners;

import me.infectus.TuinityPerPlayerViewDist.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class onPlayerLeave implements Listener {
    
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        //some quick cleanup
        Main.playerUsingPermBasedVd.remove(event.getPlayer().getName());
    }
}
