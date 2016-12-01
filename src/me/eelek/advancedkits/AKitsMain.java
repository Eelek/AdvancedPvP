package me.eelek.advancedkits;

import java.util.logging.Logger;

import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;

import me.eelek.advancedkits.cmds.KitCmd;
import me.eelek.advancedkits.kits.KitManager;
import me.eelek.advancedkits.mysql.MySQLConnect;
import me.eelek.advancedkits.mysql.SaveData;
import me.eelek.advancedkits.players.PlayerHandler;

public class AKitsMain extends JavaPlugin implements Listener {
	
	public static Logger log;
	
	static Scoreboard s;
	
	//This is Advanced Kits version: 2.0
	
	@Override
	public void onEnable() {
		log = getLogger();
		
		//CommandExecutors here
		getCommand("kit").setExecutor(new KitCmd(this));
		
		//Load on server data
		if(useConfig()) {
			ConfigDataManager.loadOnServerData(this);
		}
		
		//Playerdata will be loaded when player logs on to the server.
		//But can be saved in the event of a shutdown when there are still players online.
		
		//Create table if not exist
		MySQLConnect.createTable(getMySQLData()[0], getMySQLData()[2], getMySQLData()[3], getMySQLData()[1], getMySQLData()[4], this);
		log.info("[AdvancedKits] Succesfully connected to database.");
		
		//Setup configs and such.
		CustomConfigHandler.saveDefaultKits(this);
		CustomConfigHandler.getKits(this).options().copyDefaults(true);
		
		CustomConfigHandler.saveDefaultPlayers(this);
		CustomConfigHandler.getPlayers(this).options().copyDefaults(true);
		
		saveDefaultConfig();
		getConfig().options().copyDefaults(true);
		
		//Register Events
		PluginManager pl = getServer().getPluginManager();
		pl.registerEvents(new PlayerHandler(this), this);
		pl.registerEvents(new KitManager(), this);
	}
	
	@Override
	public void onDisable() {
		//Check for MySQL usage, then saved either to the database, server, or both.
		if(useConfig()) {
			MySQLConnect.establishMySQLConnection(getMySQLData()[0], getMySQLData()[2], getMySQLData()[3], getMySQLData()[1]);
			SaveData.updateOnDisable(this);
			MySQLConnect.closeConnection();
		} else {
			ConfigDataManager.saveDataToServer(this);
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
	public String[] getMySQLData() {
		String host = getConfig().getString("MySQL-Host");
		String port = getConfig().getString("MySQL-Port");
		String db_name = getConfig().getString("MySQL-DB_Name");
		String user = getConfig().getString("MySQL-User");
		String password = getConfig().getString("MySQL-Password");
		String table = getConfig().getString("MySQL-Player-Table");
		
		String cmd = host + ":" + port + "-" + db_name + "-" + user + "-" + password + "-" + table;
		String[] split = cmd.split("-");
		return split;
	}
}