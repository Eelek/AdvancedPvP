package me.eelek.advancedpvp;

import java.io.File;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;

import me.eelek.advancedpvp.arena.ArenaManager;
import me.eelek.advancedpvp.cmds.ArenaCmd;
import me.eelek.advancedpvp.cmds.KitCmd;
import me.eelek.advancedpvp.kits.KitManager;
import me.eelek.advancedpvp.players.GamePlayer;
import me.eelek.advancedpvp.players.PlayerManager;

public class APvPMain extends JavaPlugin implements Listener {
	
	//This is Advanced PvP version: 2.0

	@Override
	public void onEnable() {
		//Create a config
		saveDefaultConfig();
		getConfig().options().copyDefaults(true);
	    
		//Create kits.json and arenas.json files.
		if(!(new File(getDataFolder() + "/kits.json").exists())) {
			saveResource("kits.json", false);
		}
		
		if(!(new File(getDataFolder() + "/arenas.json").exists())) {
			saveResource("arenas.json", false);
		}
		
		DataManager.getInstance().boot(this);
		
	    //Setting up PlayerManager
		PlayerManager.getInstance().setDefaultChannel(getConfig().getString("default-channel"));
		
		//Register Events
		getServer().getPluginManager().registerEvents(PlayerManager.getInstance(), this);
		getServer().getPluginManager().registerEvents(KitManager.getInstance(), this);
		getServer().getPluginManager().registerEvents(ArenaManager.getInstance(), this);
		getServer().getPluginManager().registerEvents(this, this);
		
		//CommandExecutors
		getCommand("kit").setExecutor(new KitCmd());
		getCommand("arena").setExecutor(new ArenaCmd());
	}
	
	@Override
	public void onDisable() {
		DataManager.getInstance().shutdown(this, PlayerManager.getInstance().getAllPlayerData() != null);
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		if(e.getCurrentItem() == null) return;
		if(e.getCurrentItem().getType() != Material.REDSTONE_TORCH && e.getCurrentItem().getType() != Material.FEATHER) return;
		
		e.setCancelled(true);
		
		GamePlayer player = PlayerManager.getInstance().getPlayer(e.getWhoClicked().getUniqueId());
		
		if(e.getCurrentItem().getType() == Material.REDSTONE_TORCH) {
			player.openPage(player.getOpenPage() - 1);
		} else {
			player.openPage(player.getOpenPage() + 1);
		}
	}
}