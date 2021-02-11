/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.infectus.TuinityPerPlayerViewDist.Commands;

import lombok.extern.java.Log;
import me.infectus.TuinityPerPlayerViewDist.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/*
 *
 * @author Heath
 */
@Log
public class ListSendViewDistance implements CommandExecutor  {
    
    private final Main plugin;
    
    public ListSendViewDistance(Main plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!sender.hasPermission("vd.admin")) {
            sender.sendMessage(ChatColor.LIGHT_PURPLE + "You do not have permission to use that command.");
            return false;
        }
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                    //do stuff
                    Bukkit.getOnlinePlayers().forEach((player) -> {
                        sender.sendMessage(ChatColor.LIGHT_PURPLE + player.getName() + "  -  Send View Distance: " + player.getSendViewDistance() + "  -  Render Distance: " + player.getClientViewDistance());
                    });
            });
            return true;
    }
}
