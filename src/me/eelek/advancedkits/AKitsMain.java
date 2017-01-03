package me.eelek.advancedkits;

import java.util.logging.Logger;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;

import me.eelek.advancedkits.arena.GameHandler;
import me.eelek.advancedkits.cmds.ArenaCmd;
import me.eelek.advancedkits.cmds.KitCmd;
import me.eelek.advancedkits.kits.KitManager;
import me.eelek.advancedkits.mysql.MySQLConnect;
import me.eelek.advancedkits.mysql.SaveData;
import me.eelek.advancedkits.players.PlayerHandler;

public class AKitsMain extends JavaPlugin implements Listener {
	
	public static Logger log;
	
	static Scoreboard s;
	
	//This is Advanced Kits version: 3.0
	
	@Override
	public void onEnable() {
		log = getLogger();
		
		//CommandExecutors here
		getCommand("kit").setExecutor(new KitCmd(this));
		getCommand("arena").setExecutor(new ArenaCmd());
		
		// Setup configs and such.
		CustomConfigHandler.reloadKits(this);
		CustomConfigHandler.saveDefaultKits(this);
		CustomConfigHandler.getKits(this).options().copyDefaults(true);

		CustomConfigHandler.reloadPlayers(this);
		CustomConfigHandler.saveDefaultPlayers(this);
		CustomConfigHandler.getPlayers(this).options().copyDefaults(true);

		CustomConfigHandler.reloadLevels(this);
		CustomConfigHandler.saveDefaultLevels(this);
		CustomConfigHandler.getLevels(this).options().copyDefaults(true);

		CustomConfigHandler.reloadArenas(this);
		CustomConfigHandler.saveDefaultArenas(this);
		CustomConfigHandler.getArenas(this).options().copyDefaults(true);

		saveDefaultConfig();
		getConfig().options().copyDefaults(true);
		
		//Load on server data
		if(useConfig()) {
			ConfigDataManager.loadOnServerData(this);
			ConfigDataManager.getLevels(this);
			ConfigDataManager.loadArenas(this);
		}
		
		//Playerdata will be loaded when player logs on to the server.
		//But can be saved in the event of a shutdown when there are still players online.
		
		//Create table if not exist
		MySQLConnect.createTable(getMySQLData("host"), getMySQLData("user"), getMySQLData("pass"), getMySQLData("database"), getMySQLData("table"), this);
		log.info("[AdvancedKits] Succesfully connected to database.");
		
		//Register Events
		getServer().getPluginManager().registerEvents(new PlayerHandler(this), this);
		getServer().getPluginManager().registerEvents(new KitManager(), this);
		getServer().getPluginManager().registerEvents(new GameHandler(this), this);
	}
	
	@Override
	public void onDisable() {
		//Check for MySQL usage, then saved either to the database, server, or both.
		if(!useConfig()) {
			MySQLConnect.establishMySQLConnection(getMySQLData("host"), getMySQLData("user"), getMySQLData("pass"), getMySQLData("database"));
			SaveData.updateOnDisable(this);
			MySQLConnect.closeConnection();
		} else {
			ConfigDataManager.saveDataToServer(this);
			ConfigDataManager.saveArenas(this);
			
			CustomConfigHandler.saveArenas(this);
			CustomConfigHandler.saveKits(this);
			CustomConfigHandler.saveLevels(this);
			CustomConfigHandler.savePlayers(this);
		}
	}
	
	public boolean useConfig() {
		if(getConfig().getBoolean("use-MySQL") == false || getConfig().getBoolean("Use-On-Server-Backup")) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean useDatabase() {
		return getConfig().getBoolean("use-MySQL");
	}
	
	//Get MySQL data from config.
	public String getMySQLData(String param) {
		if(param.equals("host")) {
			return getConfig().getString("MySQL-Host") + ":" + getConfig().getString("MySQL-Port");
		} else if(param.equals("database")) {
			return getConfig().getString("MySQL-DB_Name");
		} else if(param.equals("user")) {
			return getConfig().getString("MySQL-User");
		} else if(param.equals("pass")) {
			return getConfig().getString("MySQL-Password");
		} else if(param.equals("table")) {
			return getConfig().getString("MySQL-Player-Table");
		} else {
			return null;
		}
	}
}