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
 * 
 * 
 * TODO
 * - Allow players to ignore all death messages
 * 
 * @author Mike Botsko (viveleroi aka botskonet) http://www.botsko.net
 * 
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Silverfish;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * dhmcDeath
 * @author botskonet
 *
 */
public class dhmcdeath extends JavaPlugin implements Listener  {
	
	Logger log = Logger.getLogger("Minecraft");
	protected FileConfiguration config;
	
	
	/**
	 * 
	 */
	private void handleConfig(){
		
		config = getConfig();
		
		// other configs
		this.getConfig().set("debug", this.getConfig().get("debug", false) );
		
		// Base config
		this.getConfig().set("messages.allow_cross_world", this.getConfig().get("messages.allow_cross_world", false) );
		this.getConfig().set("messages.use_hear_distance", this.getConfig().get("messages.use_hear_distance", true) );
		this.getConfig().set("messages.hear_distance", this.getConfig().get("messages.hear_distance", 200) );

		// Set initial methods as enabled
		this.getConfig().set("messages.custom.enabled", this.getConfig().get("messages.custom.enabled", true) );
		this.getConfig().set("messages.cactus.enabled", this.getConfig().get("messages.cactus.enabled", true) );
		this.getConfig().set("messages.drowning.enabled", this.getConfig().get("messages.drowning.enabled", true) );
		this.getConfig().set("messages.fall.enabled", this.getConfig().get("messages.fall.enabled", true) );
		this.getConfig().set("messages.fire.enabled", this.getConfig().get("messages.fire.enabled", true) );
		this.getConfig().set("messages.lava.enabled", this.getConfig().get("messages.lava.enabled", true) );
		this.getConfig().set("messages.lightning.enabled", this.getConfig().get("messages.lightning.enabled", true) );
		this.getConfig().set("messages.mob.enabled", this.getConfig().get("messages.mob.enabled", true) );
		this.getConfig().set("messages.poison.enabled", this.getConfig().get("messages.poison.enabled", true) );
		this.getConfig().set("messages.pvp.enabled", this.getConfig().get("messages.pvp.enabled", true) );
		this.getConfig().set("messages.starvation.enabled", this.getConfig().get("messages.starvation.enabled", true) );
		this.getConfig().set("messages.suffocation.enabled", this.getConfig().get("messages.suffocation.enabled", true) );
		this.getConfig().set("messages.suicide.enabled", this.getConfig().get("messages.suicide.enabled", true) );
		this.getConfig().set("messages.tnt.enabled", this.getConfig().get("messages.tnt.enabled", true) );
		this.getConfig().set("messages.void.enabled", this.getConfig().get("messages.void.enabled", true) );
		this.getConfig().set("messages.default.enabled", this.getConfig().get("messages.default.enabled", true) );

//		this.getConfig().set("messages.potion.enabled", this.getConfig().get("messages.potion.enabled", true) );
		
		
		List<String> cactus=new ArrayList<String>();
		cactus.add("&3%d &cdied from a cactus. We know, that's lame.");
		cactus.add("&3%d &cpoked a cactus, but the cactus poked back.");
		this.getConfig().set("messages.cactus.messages", this.getConfig().get("messages.cactus.messages", cactus ) );
		
		List<String> drowning=new ArrayList<String>();
		drowning.add("&3%d &cdrowned.");
		drowning.add("&3%d &cis swimming with the fishes.");
		drowning.add("&3%d &ctook a long walk off a short pier.");
		this.getConfig().set("messages.drowning.messages", this.getConfig().get("messages.drowning.messages", drowning ) );
		
		List<String> fall=new ArrayList<String>();
		fall.add("&3%d &cfell to his ultimate demise.");
		fall.add("&3%d &chit the ground too hard.");
		fall.add("&3%d &cperished from a brutal fall.");
		fall.add("&3%d &csuccumbed to gravity.");
		fall.add("&3%d &cfinally experienced terminal velocity.");
		fall.add("&3%d &cwent skydiving, forgot the parachute.");
		fall.add("&cWe'll hold a moment of silence while we laugh at your falling death, &3%d.");
		fall.add("&cAttempting a high-wire stunt yet again, &3%d &cslipped, and died.");
		fall.add("&cSomehow tree tops are immune to gravity. &3%d &cis not.");
		fall.add("&cNice going &3%d, &cyou've fallen. You're in a group that includes sand, and gravel - the losers of three.");
		fall.add("&cWe're here today to mourn the loss of &3%d&c. He is survived by his Nyan Cat and Creeper statues.");
		fall.add("&cLike everything in life, &3%d &cchose the most average, unexciting, unadventerous death - falling. Whoopie.");
		fall.add("&cOh man that was hard fall &3%d&c! You ok? &3%d&c? How many fingers dude? Um, dude? Oh sh...");
		fall.add("&3%d &chad a whoopsie-daisy!");
		fall.add("&3%d &cwas testing gravity. Yup, still works.");
		fall.add("&cAlthough &3%d's &cbody lies on the ground somewhere, the question stands. Will it blend?");
		this.getConfig().set("messages.fall.messages", this.getConfig().get("messages.fall.messages", fall ) );
		
		List<String> fire=new ArrayList<String>();
		fire.add("&3%d &cburned to death.");
		fire.add("&3%d &cforgot how to stop, drop and roll.");
		fire.add("&3%d &cspontaneiously combusted, or possibly walked into fire.");
		fire.add("&3%d &cbecame a human torch. Not a very long-lasting one either.");
		fire.add("&cNot only did you burn up &3%d&c, but you may have started a forest fire. Nice going.");
		fire.add("&cYou are not a replacement for coal, &3%d&c. I'm not sure that even death can teach you that lesson.");
		fire.add("&cTaking himself out of the gene pool for us, &3%d &cburned to death. Good job!");
		this.getConfig().set("messages.fire.messages", this.getConfig().get("messages.fire.messages", fire ) );
		
		List<String> lava=new ArrayList<String>();
		lava.add("&3%d &cwas killed by lava.");
		lava.add("&3%d &cbecame obsidian.");
		lava.add("&3%d &ctook a bath in a lake of fire.");
		lava.add("&3%d lost an entire inventory to lava. He died too, but man, loosing your stuff's a bummer!");
		lava.add("&cI told you not to dig straight down &3%d&c. Now look what happened.");
		lava.add("&cLook &3%d&c, I'm sorry I boiled you to death. I just wanted a friend. No one likes me. - Your Best Buddy, Lava.");
		lava.add("&cThen &3%d &csaid \"Take my picture in front of this pit of boiling, killer lava.\"");
		this.getConfig().set("messages.lava.messages", this.getConfig().get("messages.lava.messages", lava ) );
		
		List<String> lightning=new ArrayList<String>();
		lightning.add("%d was struck with a bolt of inspiration. Wait, nevermind. Lightning.");
		this.getConfig().set("messages.lightning.messages", this.getConfig().get("messages.lightning.messages", lightning ) );
		
		List<String> mob=new ArrayList<String>();
		mob.add("&3%d &cwas ravaged by &3%a&c.");
		mob.add("&3%d &cdied after encountering the fierce &3%a&c.");
		mob.add("&3%d &cwas killed by an angry &3%a&c.");
		mob.add("&cIt was a horrible death for &3%d &c- ravaged by a &3%a&c.");
		mob.add("&cDinner time for &3%a&c. Cooked pork for the main course, &3%d &cfor dessert.");
		mob.add("&3%d &cwent off into the woods alone and shall never return. Until respawn.");
		mob.add("&cWhile hunting, &3%d &cwas unaware that a &3%a &cwas hunting him. Rest in pieces.");
		mob.add("&cWe have unconfirmed reports that &3%d &cwas attacked by an &3%a.");
		mob.add("&cLook &3%d&c, I'm sorry I killed you. I just wanted a friend. No one likes me. - Your Best Buddy, &3%a&c.");
		mob.add("&cSomething killed &3%d&c!");
		mob.add("&cDear &3%d&c, good luck finding your stuff. - &3%a&c.");
		mob.add("&3%d &cwas ravaged by &3%a&c.");
		this.getConfig().set("messages.mob.messages", this.getConfig().get("messages.mob.messages", mob ) );
		
		// MOB SPECIFIC TYPES
		
			List<String> zombie=new ArrayList<String>();
			zombie.add("&cHaving not seen the plethora of zombie movies, &3%d &cwas amazingly unaware of how to escape.");
			zombie.add("&cPoor &3%d &c- that zombie only wanted a hug! That's why his arms were stretched out.");
			this.getConfig().set("messages.mob.zombie.messages", this.getConfig().get("messages.mob.zombie.messages", zombie ) );
		
			List<String> creeper=new ArrayList<String>();
			creeper.add("&3%d &cwas creeper bombed.");
			creeper.add("&3%d &chugged a creeper.");
			creeper.add("&cSorry you died &3%d&c, a creeper's gonna creep!");
			creeper.add("&3%d &cwas testing a new creeper-proof suit. It didn't work.");
			creeper.add("&3%d &cwas not involved in any explosion, nor are we able to confirm the existence of the \"creeper\". Move along.");
			creeper.add("&cDue to the laws of physics, the sound of a creeper explosion only reached &3%d &cafter he died from it.");
			creeper.add("&cHell hath no fury like a creeper scorned. We drink to thy untimely end, lord &3%d&c.");
			creeper.add("&cI'm sorry &3%d&c, that's the only birthday gift Creepers know how to give. ;(");
			this.getConfig().set("messages.mob.creeper.messages", this.getConfig().get("messages.mob.creeper.messages", creeper ) );
			
		// posion
		
		List<String> pvp=new ArrayList<String>();
		pvp.add("&3%d &cwas just murdered by &3%a&c, using &3%i&c.");
		pvp.add("&3%d &cdied, by &3%a's %i.");
		pvp.add("&3%a &ckilled &3%d &cwielding &3%i");
		pvp.add("&cYou think it was &3%a who &ckilled &3%d&c? Nope, Chuck Testa.");
		pvp.add("&cIt was a bitter end for &3%d&c, but &3%a &cwon victoriously.");
		pvp.add("&cEmbarrassingly, &3%d &cdied of fright before &3%a &ccould even raise his weapon.");
		pvp.add("&3%a &cstruck the mighty blow and ended &3%d&c.");
		pvp.add("&3%d &cnever saw &3%a &ccoming.");
		pvp.add("&3%a &cdelivered the fatal blow on &3%d&c.");
		pvp.add("&3%d's &cinventory now belongs to &3%a&c.");
		pvp.add("&3%a &ctaught &3%d &cthe true meaning of PVP.");
		pvp.add("&cIn the case of &3%d &cv. &3%a&c, &3%d &cis suing on charges of voluntary manslaughter. This judge finds &3%a &cguilty of BEING AWESOME!");
		pvp.add("&cWhat is this, like death number ten for &3%d&c? Ask &3%a&c.");
		this.getConfig().set("messages.pvp.messages", this.getConfig().get("messages.pvp.messages", pvp ) );
		
		List<String> starvation=new ArrayList<String>();
		starvation.add("&3%d &cstarved to death.");
		starvation.add("&3%d &cstarved to death. Because food is *so* hard to find.");
		this.getConfig().set("messages.starvation.messages", this.getConfig().get("messages.starvation.messages", starvation ) );
		
		List<String> suffocation=new ArrayList<String>();
		suffocation.add("&3%d &csuffocated.");
		this.getConfig().set("messages.suffocation.messages", this.getConfig().get("messages.suffocation.messages", suffocation ) );
		
		List<String> suicide=new ArrayList<String>();
		suicide.add("&3%d &ckilled himself.");
		suicide.add("&3%d &cended it all. Goodbye cruel world!");
		this.getConfig().set("messages.suicide.messages", this.getConfig().get("messages.suicide.messages", suicide ) );
		
		List<String> tnt=new ArrayList<String>();
		tnt.add("&3%d &cblew up.");
		tnt.add("&3%d &cwas blown to tiny bits.");
		this.getConfig().set("messages.tnt.messages", this.getConfig().get("messages.tnt.messages", tnt ) );
		
		List<String> thevoid=new ArrayList<String>();
		thevoid.add("&3%d &cceased to exist. Thanks void!");
		thevoid.add("&3%d &cpassed the event horizon.");
		this.getConfig().set("messages.void.messages", this.getConfig().get("messages.void.messages", thevoid ) );
		
		List<String> defaultmsg=new ArrayList<String>();
		defaultmsg.add("&3%d &cpossibly died - we're looking into it.");
		defaultmsg.add("&cNothing happened. &3%d &cis totally ok. Why are you asking?");
		this.getConfig().set("messages.default.messages", this.getConfig().get("messages.default.messages", defaultmsg ) );
		
		saveConfig();
		
	}
	
	
	/**
     * Enables the plugin and activates our player listeners
     */
	public void onEnable(){
		
		this.log("[dhmcDeath]: Initializing.");
		handleConfig();
		getServer().getPluginManager().registerEvents(this, this);
		
		getCommand("dhmcdeath").setExecutor( (CommandExecutor) new DhmcdeathCommandExecutor(this) );
		
	}
	
	
	/**
	 * Handles the entity death.
	 * 
	 * @param event
	 */
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
	        String cause = getCauseOfDeath( event, p );
	        String attacker = getAttacker(event, p);
            
            // Verify death messages are enabled for this type
            if( getConfig().getBoolean("messages."+cause.toLowerCase()+".enabled") ){
            
	            // Load the death message for this type
	            List<Object> messages = getConfig().getList( "messages." + cause.toLowerCase() + ".messages" );
	            
	            String final_msg = "";
	            if(messages != null && !messages.isEmpty()){
	            	
	            	// If mob, and mob has specific messages, we need to override the standard mob messages
		            if(cause == "mob"){
		            	List<Object> mob_msg = getConfig().getList( "messages.mob."+attacker.toLowerCase()+".messages" );
		            	if(mob_msg != null && !mob_msg.isEmpty()){
		            		messages = mob_msg;
		            	}
		            }
	            	
	            	Random rand = new Random();
		            int choice = rand.nextInt(messages.size());
		            final_msg = (String) messages.get(choice);  
	            } else {
	            	messages = getConfig().getList( "messages.default.messages" );
	            	Random rand = new Random();
		            int choice = rand.nextInt(messages.size());
		            final_msg = (String) messages.get(choice);  
	            }
	            
	            // Build the final message
	            final_msg = final_msg.replaceAll("%d", p.getName());

	            if(attacker == "pvpwolf"){
	            	String owner = getTameWolfOwner(event);
	            	attacker = owner+"'s wolf";
	            }
	            final_msg = final_msg.replaceAll("%a", attacker);
	            
	            final_msg = final_msg.replaceAll("%i", getWeapon(p) );
	            
	            // Colorize
	            final_msg = colorize(final_msg);
	            
	            // If we allow all worlds, get all online players
				Player[] players = null;
				if( getConfig().getBoolean("allow_cross_world") ){
					players = getServer().getOnlinePlayers();
				} else {
					players = p.getWorld().getPlayers().toArray(new Player[0]);
				}
	            
	            // Iterate all players within the world
	            for (Player player : players) {
	            	debug("[dhmcDeath]: Distance Was: " + player.getLocation().distance( p.getLocation() ) );
	            	// Only send message if player is within distance
	            	if( !getConfig().getBoolean("use_hear_distance") || player.getLocation().distance( p.getLocation() ) <= getConfig().getInt("messages.hear_distance") ) {
		            	player.sendMessage( final_msg );
		            	debug("[dhmcDeath]: Messaging Player: " + player.getName());
	            	}
	    		}
	            this.log("[dhmcDeath]: " + final_msg);
            } else {
            	debug("[dhmcDeath]: Messages are disabled for this cause: " + cause);
            }
		}
	}
	
	
	/**
	 * Translates the cause of death into a more consistent naming convention we
	 * can use to relate messages.
	 * 
	 * All possible deaths:
	 * custom
	 * drowning
	 * fall
	 * lava
	 * lightning
	 * mob
	 * poison ?
	 * pvp
	 * starvation
	 * suffocation
	 * suicide
	 * void
	 * 
	 * http://jd.bukkit.org/apidocs/org/bukkit/event/entity/EntityDamageEvent.DamageCause.html
	 * 
	 * @param event
	 * @param p
	 * @return String
	 */
	private String getCauseOfDeath(EntityDeathEvent event, Player p){
		
		// Determine the root cause
        String cause = event.getEntity().getLastDamageCause().getCause().toString();
        debug("[dhmcDeath]: Raw Cause: " + cause);
        
        // Detect additional suicide. For example, when you potion
        // yourself with instant damage it doesn't show as suicide.
        if(p.getKiller() instanceof Player){
        	Player killer = p.getKiller();
        	if(killer.getName() == p.getName()){
        		cause = "suicide";
        	}
        }
        
        // translate bukkit events to nicer names
        if(cause == "ENTITY_ATTACK" && p.getKiller() instanceof Player){
        	cause = "pvp";
        }
        if(cause == "ENTITY_ATTACK" && !(p.getKiller() instanceof Player)){
        	cause = "mob";
        }
        if(cause == "PROJECTILE" && !(p.getKiller() instanceof Player)){
        	cause = "mob"; // skeleton
        }
        if(cause == "PROJECTILE" && (p.getKiller() instanceof Player)){
        	cause = "pvp"; // bow and arrow
        }
        if(cause == "ENTITY_EXPLOSION"){
        	cause = "mob"; // creeper
        }
        if(cause == "CONTACT"){
        	cause = "cactus";
        }
        if(cause == "BLOCK_EXPLOSION"){
        	cause = "tnt";
        }
        if(cause == "FIRE" || cause == "FIRE_TICK"){
        	cause = "fire";
        }
        if(cause == "MAGIC"){
        	cause = "potion";
        }
        
        debug("[dhmcDeath]: Parsed Cause: " + cause);
        
        return cause;
		
	}
	
	
	/**
	 * Returns the name of the attacker, whether mob or player.
	 * 
	 * @param event
	 * @param p
	 * @return
	 */
	private String getAttacker(EntityDeathEvent event, Player p){
		
		String attacker = "";
		String cause = getCauseOfDeath(event, p);
        if(p.getKiller() instanceof Player){
        	attacker = p.getKiller().getName();
        } else {
            if(cause == "mob"){
            	
            	Entity killer = ((EntityDamageByEntityEvent)event.getEntity().getLastDamageCause()).getDamager();
            	debug("[dhmcDeath]: Entity Was: " + killer);
            	
            	if (killer instanceof Blaze){
            		attacker = "blaze";
            	}
            	if (killer instanceof CaveSpider){
            		attacker = "cave spider";
            	}
            	if (killer instanceof Creeper){
            		attacker = "creeper";
            	}
            	if (killer instanceof EnderDragon){
            		attacker = "ender dragon";
            	}
            	if (killer instanceof Enderman){
            		attacker = "enderman";
            	}
            	if (killer instanceof Ghast){
            		attacker = "ghast";
            	}
            	if (killer instanceof MagmaCube){
            		attacker = "magma cube";
            	}
            	if (killer instanceof PigZombie){
            		attacker = "pig zombie";
            	}
            	if (killer instanceof Silverfish){
            		attacker = "silverfish";
            	}
            	if (killer instanceof Skeleton){
            		attacker = "skeleton";
            	}
            	if (killer instanceof Arrow){
            		attacker = "skeleton";
            	}
            	if (killer instanceof Slime){
            		attacker = "slime";
            	}
            	if (killer instanceof Spider){
            		attacker = "spider";
            	}
            	if (killer instanceof Wolf){
                    Wolf wolf = (Wolf)killer;
                    if(wolf.isTamed()){
                        if(wolf.getOwner() instanceof Player || wolf.getOwner() instanceof OfflinePlayer ){
                            attacker = "pvpwolf";
                        } else {
                        	attacker = "wolf";
                        }
                    } else {
                    	attacker = "wolf";
                    }
            		
            	}
            	if (killer instanceof Zombie){
            		attacker = "zombie";
            	}
            }
        }
        debug("[dhmcDeath]: Attacker: " + attacker);
        
        return attacker;
        
	}
	
	
	/**
	 * Determines the owner of a tamed wolf.
	 * 
	 * @param event
	 * @return
	 */
	private String getTameWolfOwner(EntityDeathEvent event){
		String owner = "";
		Entity killer = ((EntityDamageByEntityEvent)event.getEntity().getLastDamageCause()).getDamager();
		if (killer instanceof Wolf){
            Wolf wolf = (Wolf)killer;
            if(wolf.isTamed()){
                if(wolf.getOwner() instanceof Player){
                    owner = ((Player)wolf.getOwner()).getName();
                }
                if(wolf.getOwner() instanceof OfflinePlayer){
                    owner = ((OfflinePlayer)wolf.getOwner()).getName();
                }
            }
    	}
		debug("[dhmcDeath]: Wolf Owner: " + owner);
		return owner;
	}
	
	
	/**
	 * Determines the weapon used.
	 * 
	 * @param p
	 * @return
	 */
	private String getWeapon(Player p){

        String death_weapon = "";
        if(p.getKiller() instanceof Player){
        	ItemStack weapon = p.getKiller().getItemInHand();
        	death_weapon = weapon.getType().toString().toLowerCase();
        	death_weapon = death_weapon.replaceAll("_", " ");
        	if(death_weapon.equalsIgnoreCase("air")){
        		death_weapon = " hands";
        	}
        }
        debug("[dhmcDeath]: Weapon: " + death_weapon );
        
        return death_weapon;
        
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
		log.info("[dhmcDeath]: " + message);
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