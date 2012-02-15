package me.botsko.dhmcdeath;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.IllegalPluginAccessException;

public class DhmcdeathCommandExecutor implements CommandExecutor {
	
	private dhmcdeath plugin;
	
	/**
	 * 
	 * @param plugin
	 * @return 
	 */
	public DhmcdeathCommandExecutor(dhmcdeath plugin) {
		this.plugin = plugin;
	}
	
	
	/**
     * Handles all of the commands.
     * 
     * 
     * @param sender
     * @param command
     * @param label
     * @param args
     * @return
     */
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) throws IllegalPluginAccessException {
		
		// Is a player issue this command?
    	if (sender instanceof Player) {
    		
    		Player player = (Player) sender;
    		
    		// Buy item|repair <quantity>
    		if(args[0].equalsIgnoreCase("reload")){
				if (player.hasPermission("dhmcdeath.*") || player.hasPermission("dhmcdeath.admin.*")){
					plugin.reloadConfig();
					player.sendMessage( plugin.playerMsg("Configuration reloaded successfully.") );
					return true;
				}
			}
    	}

		return false; 
		
	}
}
