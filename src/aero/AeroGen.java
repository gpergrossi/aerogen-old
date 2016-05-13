package aero;

import island.BiomeDescription;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * AeroGen is a special generator for Minecraft, which is, if you are reading this, 
 * something you should already know. The generator uses the Bukkit world generator
 * controls to provide the data for new chunks in place of the normal world generator.
 * Which generator is responsible for creating a world is decided by Bukkit through
 * either the Bukkit.yml file or a plugin, like multiverse.
 * 
 * @author MortusNegati
 */
public class AeroGen extends JavaPlugin {
	
	private static AeroGen instance;
	private static Logger log = Logger.getLogger("Minecraft");
	public static String pluginFolder;
	public static String pluginName = "AeroGen";
	public static String pluginVersion = "0.0";
	public static String pluginDescription = "plugin.yml not found!";
	
	/**
	 * This method provides public access to the CraftBukkit logging system.
	 * @param msg - message to log
	 */
	public static void log(String msg) {
		instance.logMessage(msg);
	}
	
	private AeroGenerator generator = null;
	
	/**
	 * This method is called when the plugin is enabled. Since this is a generator 
	 * plugin, it should be enabled before any generation has started. This is 
	 * achieved by placing "load: STARTUP" in the plugin.yml file.
	 */
	public void onEnable() {		
		//Define a field for public static access to this plugin
		instance = this;
		
		//Get the plugin name, version, and description from the plugin.yml file
		PluginDescriptionFile pluginYMLFile = this.getDescription();
		pluginName = pluginYMLFile.getName();
		pluginVersion = pluginYMLFile.getVersion();
		pluginDescription = pluginYMLFile.getDescription();
		pluginFolder = "plugins/"+pluginName+"/";
		
		//Load Config File
		FileConfiguration config = this.getConfig();
		if(!new File(pluginFolder+"config.yml").exists()) {
			config.options().copyDefaults(true);
			this.saveDefaultConfig();
		}
		BiomeDescription.loadBiomes(config.getConfigurationSection("biomes"));

		//Log a message saying the plugin has loaded successfully.
		logMessage(pluginName + " is now enabled.");
	}
	
	/**
	 * This method is called when the plugin is disabled, usually on server shutdown.
	 */
	public void onDisable() {
		//Log a message saying the plugin has closed successfully.
		logMessage(pluginName + " is now disabled.");
	}
	
	/**
	 * Logs a message with the plugin name and version prefacing the given string.
	 * @param msg - message to log
	 */
	private void logMessage(String msg) {
		AeroGen.log.info(pluginName + " " + pluginVersion + ": " + msg);
	}
	
	/**
	 * Returns the chunk generator for any given world. If this method is asked, then the world
	 * with the information provided is using the AeroGen generator. Each world should have its
	 * own generator object, hence the HashMap. 
	 */
	public ChunkGenerator getDefaultWorldGenerator(String worldName, String worldUID) {
		if(generator == null) {
			generator = new AeroGenerator(this, worldName);
		}
		return generator;
	}
	
	/**
	 * Called when a valid command is registered for this plugin
	 */
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(command.getName().equalsIgnoreCase("memory")) {
		}
		return false;
	}

}
