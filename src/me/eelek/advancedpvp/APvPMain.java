package me.eelek.advancedpvp;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import me.eelek.advancedpvp.arena.ArenaManager;
import me.eelek.advancedpvp.cmds.ArenaCmd;
import me.eelek.advancedpvp.cmds.KitCmd;
import me.eelek.advancedpvp.kits.KitManager;
import me.eelek.advancedpvp.players.PlayerManager;

public class APvPMain extends JavaPlugin implements Listener {
	
	//This is Advanced PvP version: 1.2

	@Override
	public void onEnable() {
		//Create a config
		saveDefaultConfig();
		getConfig().options().copyDefaults(true);
		
		File kitsFile = new File(getDataFolder() + "/kits.json");
		
		if(!kitsFile.exists()) {
			saveResource("kits.json", false);
		}
		
		//Start connection to the database
		DataManager.getInstance().startConnection(getConfig().getString("MySQL-Host"), getConfig().getString("MySQL-Port"), getConfig().getString("MySQL-DB_Name"), getConfig().getString("MySQL-User"), getConfig().getString("MySQL-Password"));
		PlayerManager.getInstance().setDefaultChannel(getConfig().getString("default-channel"));
		
		//Create table if not exist
		try {
			Connection c = DataManager.getInstance().getConnection();
			PreparedStatement s = c.prepareStatement("CREATE TABLE IF NOT EXISTS `" + getConfig().getString("MySQL-DB_Name") + "`.`player_data` ( `player_name` VARCHAR(17) NOT NULL , `player_uuid` VARCHAR(37) NOT NULL , `kills` INT(255) NOT NULL , `deaths` INT(255) NOT NULL , `points` INT(255) NOT NULL );");
			s.execute();
			s = c.prepareStatement("CREATE TABLE IF NOT EXISTS `" + getConfig().getString("MySQL-DB_Name") + "`.`arena_data` ( `id` INT(255) NOT NULL AUTO_INCREMENT , `name` VARCHAR(16) NOT NULL , `max_players` INT(10) NOT NULL , `minimum_level` INT(10) NOT NULL , `world` VARCHAR(32) NOT NULL , `lobby_x` INT(6) NOT NULL , `lobby_y` INT(6) NOT NULL , `lobby_z` INT(6) NOT NULL , `type` VARCHAR(20) NOT NULL , PRIMARY KEY (`id`), UNIQUE (`name`));");
			s.execute();
			s = c.prepareStatement("CREATE TABLE IF NOT EXISTS `" + getConfig().getString("MySQL-DB_Name") + "`.`arena_spawns` ( `spawn_id` INT(255) NOT NULL AUTO_INCREMENT , `world` VARCHAR(10) NOT NULL , `x` INT(6) NOT NULL , `y` INT(6) NOT NULL , `z` INT(6) NOT NULL , `pitch` INT(3) NOT NULL , `yaw` INT(3) NOT NULL , `spawns` INT(255) NOT NULL , `team` INT(1) NOT NULL , `arena_id` INT(255) NOT NULL , PRIMARY KEY (`spawn_id`), INDEX `arena` (`arena_id`));");
			s.execute();
			s = c.prepareStatement("CREATE TABLE IF NOT EXISTS `" + getConfig().getString("MySQL-DB_Name") + "`.`level_data` ( `level` INT(255) NOT NULL AUTO_INCREMENT , `minimum_kills` INT(255) NOT NULL , `prefix` VARCHAR(32) NOT NULL , PRIMARY KEY (`level`));");
			s.execute();
			s = c.prepareStatement("CREATE TABLE IF NOT EXISTS `" + getConfig().getString("MySQL-DB_Name") + "`.`arena_kits` ( `kit_id` INT(255) NOT NULL AUTO_INCREMENT , `arena_id` INT(255) NOT NULL , `kit` VARCHAR(64) NOT NULL , PRIMARY KEY (`kit_id`), INDEX (`arena_id`));");
			s.execute();
			s.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		//Load Data
		DataManager.getInstance().loadKits(this);
		DataManager.getInstance().loadItemsFromDatabase();
		
		//Playerdata will be loaded when player logs on to the server.
		//But can be saved in the event of a shutdown when there are still players online.
		
		//Register Events
		getServer().getPluginManager().registerEvents(PlayerManager.getInstance(), this);
		getServer().getPluginManager().registerEvents(KitManager.getInstance(), this);
		getServer().getPluginManager().registerEvents(ArenaManager.getInstance(), this);
		
		//CommandExecutors
		getCommand("kit").setExecutor(new KitCmd());
		getCommand("arena").setExecutor(new ArenaCmd());
	}
	
	@Override
	public void onDisable() {
		if(PlayerManager.getInstance().getAllPlayerData() != null) {
			DataManager.getInstance().updateOnDisable();
		}
	    
		DataManager.getInstance().saveData();
		
		DataManager.getInstance().closeConnection();
	}
}