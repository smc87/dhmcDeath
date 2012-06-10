package me.botsko.dhmcdeath.tp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import me.botsko.dhmcdeath.DhmcDeath;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class DeathCapture {
	
	protected Player player;
	protected DhmcDeath plugin;
//	protected HashMap<String,Death> deaths = new HashMap<String, Death>();
	protected Death death;
	
	
	/**
	 * 
	 * @param plugin
	 * @param player
	 */
	public DeathCapture( DhmcDeath plugin, Player player, Death death){
		this.player = player;
		this.plugin = plugin;
		this.death = death;
		
		// Make players directory
		File dir = getDirectory();
		dir.mkdir();
		
	}
	
	
	/**
	 * 
	 * @param death
	 */
//	public void addDeath( Death death ){
//		deaths.put( death.getPlayer().getName(), death );
//	}
//	
	
	
	public File getDirectory(){
		File dir = new File(plugin.getDataFolder()+"/players/");
		return dir;
	}
	
	/**
	 * 
	 * @return
	 */
	protected File getFile(){
		File file = new File(getDirectory(), player.getName() + ".yml");
		return file;
	}
	
	
	/**
	 * 
	 * @param player
	 * @return
	 */
	public FileConfiguration getConfig(){
		File file = getFile();
		if(file.exists()){
			return YamlConfiguration.loadConfiguration(file);
		}
		return null;
	}
	
	
	/**
	 * 
	 * @param config
	 */
	public void saveConfig(FileConfiguration config){
		File file = getFile();
		try {
			config.save(file);
		} catch (IOException e) {
			plugin.log("Could not save the configuration file to "+file);
		}
	}
	
	
	/**
	 * 
	 */
	public void write(){
		
		
		try {
			BufferedWriter bw = new BufferedWriter( new FileWriter( getFile(), true ) );
			
			FileConfiguration config = getConfig();
			
			config.set("death.name",player.getName());
			config.set("death.cause",death.getCause());
			config.set("death.attacker",death.getAttacker());
			config.set("death.loc.x", death.getLocation().getX());
			config.set("death.loc.y", death.getLocation().getY());
			config.set("death.loc.z", death.getLocation().getZ());
			config.set("death.loc.world", death.getWorld().getName());
			
			saveConfig(config);
			
//			for(String p:deaths.keySet()){
//				Death d = deaths.get(p);
//				bw.write(p + "," + d.getPlayer().getName());
//				bw.newLine();
//			}
			bw.flush();
			bw.close();
		} catch (IOException e){
//            log.info("[ServerNews] + '" + getDataFolder().getPath() + "/player_toggle.yml' not found.");
        }
	}
	
	
	
	/**
	 * 
	 */
	public void read(){
		
//		HashMap<String, Boolean> hashmap = new HashMap<String, Boolean>();
//		File file = //your file
//		try
//		{
//		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
//		String l;
//		while((l = br.readLine()) != null)
//		{
//		String[] args = l.split("[,]", 2);
//		if(args.length != 2)continue;
//		String p = args[0].replaceAll(" ", "");
//		String b = args[1].replaceAll(" ", "");
//		if(b.equalsIgnoreCase("true"))hashmap.put(p, true);
//		else hashmap.put(p, false);
//		}
//		br.close();
//		}
		
	}

}
