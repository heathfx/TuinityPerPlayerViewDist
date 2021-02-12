package me.infectus.TuinityPerPlayerViewDist.Commands;

import me.infectus.TuinityPerPlayerViewDist.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;


public class ListSendViewDistance implements CommandExecutor  {
    
    private final Main plugin;
    
    public ListSendViewDistance(Main plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        
        //permission check
        if(!sender.hasPermission("vd.admin")) {
            sender.sendMessage(ChatColor.LIGHT_PURPLE + "You do not have permission for that command.");
            return false;
        }
        
        //get all online players and print a list of their names, send view distance and render distance
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                //do stuff
                Bukkit.getOnlinePlayers().forEach((player) -> {
                    sender.sendMessage(ChatColor.LIGHT_PURPLE + player.getName() + "  -  Send View Distance: " + player.getSendViewDistance() + "  -  Render Distance: " + player.getClientViewDistance());
                });
        });
        return true;
    }
}
