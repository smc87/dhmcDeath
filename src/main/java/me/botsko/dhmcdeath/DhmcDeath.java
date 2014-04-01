package me.botsko.dhmcdeath;


/**
 * dhmcDeath
 * 
 * The lively community of the Darkhelmet server (s.dhmc.us) have been using HeroicDeath since we began.
 * However our needs have grown and while our players really enjoy the death messages we've come up
 * with, it's time to improve.
 * 
 * Get Help:
 * irc.esper.net #dhmc_us
 * 
 * FEATURES
 * - Tons of great messages out-of-the-box.
 * - Easy configuration of death messages. Similar syntax as HeroicDeath for easy conversion.
 * - Use generic mob messages or add them for specific mob types
 * - Custom colors allowed in messages.
 * - Disable message for specific events, i.e. show mob death messages but ignore PVP
 * - Optionally limit messages by world
 * - Optionally limit the message to players within a range of the death
 * - Shows owners of tamed wolves
 * - Supports all 1.1 Minecraft mobs
 * 
 * Version 0.1
 * - Added configurable death messages for all death types, and mob types
 * - Added color code support
 * - Optional world limit
 * - Optional range limit
 * - Tamed wolf owners are listed too
 * - Per-death-type event disabling
 * Version 0.1.1
 * - Fixed message log left inside a loop
 * - Fixed weapon of "air" reported when using hands
 * Version 0.1.2
 * - Minor change to character in a message
 * Version 0.1.3
 * - Corrected string comparison for "air"
 * Version 0.1.4
 * - Adding config reload command, with basic permission support
 * - Moved debug option to config
 * - Added central logging/debug methods
 * Version 0.1.5
 * - Attempting to fix distance between worlds error (issue #1)
 * Version 0.1.6
 * - Fixed issue with radius being ignored in certain situations
 * - Fixed issue with removed configuration options being re-added
 * - Removed use_hear_distance config, hear distance will be ignored if set to 0
 * - Thanks to napalm1 for testing the radius fix with me.
 * Version 0.1.7
 * - Added /death command that returns a player to their last death
 * - Modified config system
 * - Added config to disallow /death when died in pvp
 * - Added config to disable logging deaths
 * - Changed log to log the death info, not the message
 * Version 0.1.8
 * - Added permission node dhmcdeath.tp for using /death
 * Version 0.1.9
 * - Added metrics
 * - Added support for wither
 * - Added support for witherskeleton
 * Version 0.2
 * - Updated to use new dhmc code library, elixr
 * - Fixed config value spelling error
 * 
 * @author Mike Botsko (viveleroi aka botskonet) http://www.botsko.net
 * 
 */

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import me.botsko.dhmcdeath.commands.DeathCommandExecutor;
import me.botsko.dhmcdeath.commands.DhmcdeathCommandExecutor;
import me.botsko.dhmcdeath.tp.Death;
import me.botsko.elixr.DeathUtils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * dhmcDeath
 * @author botskonet
 *
 */
public class DhmcDeath extends JavaPlugin implements Listener  {
	
	Logger log = Logger.getLogger("Minecraft");
	protected FileConfiguration config;
	protected HashMap<String,Death> deaths = new HashMap<String,Death>();
	
	
	/**
     * Enables the plugin and activates our player listeners
     */
	public void onEnable(){
		
		this.log("Initializing plugin. By Viveleroi, Darkhelmet Minecraft: s.dhmc.us");
		
		try {
		    Metrics metrics = new Metrics(this);
		    metrics.start();
		} catch (IOException e) {
		    log("MCStats submission failed.");
		}
		
		// Load configuration, or install if new
		config = DeathConfig.init( this );
		
		getServer().getPluginManager().registerEvents(this, this);
		
		getCommand("dhmcdeath").setExecutor( (CommandExecutor) new DhmcdeathCommandExecutor(this) );
		getCommand("death").setExecutor( (CommandExecutor) new DeathCommandExecutor(this) );

	}
	
	
	/**
	 * Handles the entity death.
	 * 
	 * @param event
	 */
	@SuppressWarnings("unchecked")
	@EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDeath(final EntityDeathEvent event) {
		
		if (event.getEntity() instanceof Player){
			
			// Disable Bukkit death messages
	        if (event instanceof PlayerDeathEvent) {
	            PlayerDeathEvent e = (PlayerDeathEvent) event;
	            e.setDeathMessage(null);
	        }

	        // Find player who died
	        Player p = (Player)event.getEntity();
			
	        // Determine the cause
	        String cause = DeathUtils.getCauseNiceName( p );
	        String attacker = DeathUtils.getAttackerName( p );
	        String weapon_used = DeathUtils.getWeapon( p );
	        //String mobtype = event.getEntity().getKiller().getName();
	        
	        //Get Cause if a random mob
	        //Elixr seems broken, trying to reproduce :(
            //debug("mobtype: " + mobtype);
            debug("cause: " + cause);
            debug("attacker: " + attacker);
            debug("weapon used: " + weapon_used);
            // Verify death messages are enabled for this type
           // if( getConfig().getBoolean("messages."+cause.toLowerCase()+".enabled") || getConfig().getBoolean("messages..enabled") ){

            if (cause == "skeleton" || cause == "creeper"){
            	cause = "mob";
            }
            if( getConfig().getBoolean("messages."+cause.toLowerCase()+".enabled") ){
            
	            // Load the death message for this type
	            List<Object> messages = (List<Object>) getConfig().getList( "messages." + cause.toLowerCase() + ".messages" );
	            
	            String final_msg = "";
	            if(messages != null && !messages.isEmpty()){
	            	
	            	// If mob, and mob has specific messages, we need to override the standard mob messages
		            if(cause == "mob"){
		            	List<Object> mob_msg = (List<Object>) getConfig().getList( "messages.mob."+attacker.toLowerCase()+".messages" );
		            	if(mob_msg != null && !mob_msg.isEmpty()){
		            		messages = mob_msg;
		            	}
		            }
	            	
	            	Random rand = new Random();
		            int choice = rand.nextInt(messages.size());
		            final_msg = (String) messages.get(choice);  
	            } else {
	            	messages = (List<Object>) getConfig().getList( "messages.default.messages" );
	            	Random rand = new Random();
		            int choice = rand.nextInt(messages.size());
		            final_msg = (String) messages.get(choice);  
	            }
	            
	            // Build the final message
	            final_msg = final_msg.replaceAll("%d", p.getName());

	            if(attacker == "pvpwolf"){
	            	String owner = DeathUtils.getTameWolfOwner(event);
	            	attacker = owner+"'s wolf";
	            }
	            final_msg = final_msg.replaceAll("%a", attacker);
	            final_msg = final_msg.replaceAll("%i", weapon_used );
	            
	            // Colorize
	            final_msg = colorize(final_msg);
	            
	            // Store the death data
	            if(this.deaths.containsKey(p.getName())){
	            	this.deaths.remove(p.getName());
	            }
	            
	            boolean allow_tp = true;
	            if(!getConfig().getBoolean("allow_deathpoint_tp_on_pvp")){
	            	if(cause.equals("pvp")){
	            		allow_tp = false;
	            	}
	            }
	            Death death = new Death( p.getLocation(), p, p.getWorld(), cause, attacker );
	            if(allow_tp){
	            	deaths.put( p.getName(), death );
	            }
	            
	            // Send the final message
	            if( getConfig().getBoolean("messages.allow_cross_world") ){
	            //Replace default death message	
	    	        if (event instanceof PlayerDeathEvent) {
	    	        	PlayerDeathEvent e = (PlayerDeathEvent) event;
	    	            e.setDeathMessage(final_msg); 
	    	            } else {
	            	//arnt they all?
	            	}
	            } else {
	            	
	            	// Iterate all players within the world
		            for (Player player : p.getWorld().getPlayers()) {
		            	if(player.hasPermission("dhmcdeath.hear")){
			            	double dist = player.getLocation().distance( p.getLocation() );
			            	debug("Distance for "+ player.getName()+ " is " + dist );
			            	// Only send message if player is within distance
			            	if( getConfig().getDouble("messages.hear_distance") == 0 || dist <= getConfig().getDouble("messages.hear_distance") ) {
				            	player.sendMessage( final_msg );
				            	debug("Messaging Player: " + player.getName() + " " + dist + " <= " + getConfig().getInt("messages.hear_distance"));
			            	}
		            	}
		    		}
	            }
	            if(getConfig().getBoolean("messages.log_deaths")){
	            	this.log(death.getPlayer().getName() + " died from " + death.getCause() + " (killer: "+death.getAttacker()+") in " + death.getWorld().getName() + " at x:" + death.getLocation().getX() + " y:" + death.getLocation().getY() + " z:" + death.getLocation().getZ() );
	            }
            
            	debug("Messages are disabled for this cause: " + cause);
            }
		}
	}
	
	
	/**
	 * 
	 * @return
	 */
	public HashMap<String,Death> getDeaths(){
		return this.deaths;
	}
	
	
	/**
	 * Converts colors place-holders.
	 * @param text
	 * @return
	 */
	private String colorize(String text){
        String colorized = text.replaceAll("(&([a-f0-9A-F]))", "\u00A7$2");
        return colorized;
    }
	
	
	/**
	 * 
	 * @param msg
	 * @return
	 */
	public String playerMsg(String msg){
		return ChatColor.GOLD + "[dhmcDeath]: " + ChatColor.WHITE + msg;
	}
	
	
	/**
	 * 
	 * @param msg
	 * @return
	 */
	public String playerError(String msg){
		return ChatColor.GOLD + "[dhmcDeath]: " + ChatColor.RED + msg;
	}
	
	
	/**
	 * 
	 * @param message
	 */
	public void log(String message){
		log.info("[dhmcDeath]: "+message);
	}
	
	
	/**
	 * 
	 * @param message
	 */
	public void debug(String message){
		if(this.getConfig().getBoolean("debug")){
			this.log("[dhmcDeath]: " + message);
		}
	}


	/**
	 * Disables plugin
	 */
	@Override
	public void onDisable() {
		this.log("[dhmcDeath]: Shutting down.");
	}
}