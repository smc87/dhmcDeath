package me.botsko.dhmcdeath.tp;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class Death {
	
	
	protected Location location;
	protected Player player;
	protected World world;
	protected String cause;
	protected String attacker;


	/**
	 * 
	 * @param location
	 * @param player
	 * @param world
	 * @param cause
	 * @param attacker
	 */
	public Death( Location location, Player player, World world, String cause, String attacker ){
		this.location = location;
		this.player = player;
		this.world = world;
		this.cause = cause;
		this.attacker = attacker;
	}

	
	/**
	 * @return the location
	 */
	public Location getLocation() {
		return location;
	}

	
	/**
	 * @return the player
	 */
	public Player getPlayer() {
		return player;
	}

	
	/**
	 * @return the world
	 */
	public World getWorld() {
		return world;
	}
	
	
	/**
	 * @return the cause
	 */
	public String getCause() {
		return cause;
	}


	/**
	 * @return the attacker
	 */
	public String getAttacker() {
		return attacker;
	}
}