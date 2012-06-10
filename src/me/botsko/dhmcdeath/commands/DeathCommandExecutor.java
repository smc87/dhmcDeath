package me.botsko.dhmcdeath.commands;

import me.botsko.dhmcdeath.DhmcDeath;
import me.botsko.dhmcdeath.tp.Death;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.IllegalPluginAccessException;

public class DeathCommandExecutor implements CommandExecutor {
	
	private DhmcDeath plugin;
	
	/**
	 * 
	 * @param plugin
	 * @return 
	 */
	public DeathCommandExecutor(DhmcDeath plugin) {
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
			returnToDeathPoint( player );
			return true;
    	}

		return false; 
		
	}
	
	
	/**
	 * 
	 * @param player
	 */
	protected void returnToDeathPoint( Player player ){
		plugin.log("Deaths: " + plugin.getDeaths().size());
		if(plugin.getDeaths().containsKey(player.getName())){
			Death d = plugin.getDeaths().get( player.getName() );
			player.teleport( d.getLocation() );
		} else {
			player.sendMessage(plugin.playerError("No death location was saved."));
		}
	}
}
