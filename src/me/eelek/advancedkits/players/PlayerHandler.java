package me.eelek.advancedkits.players;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.eelek.advancedkits.AKitsMain;
import me.eelek.advancedkits.ConfigDataManager;
import me.eelek.advancedkits.kits.KitManager;
import me.eelek.advancedkits.mysql.LoadData;
import me.eelek.advancedkits.mysql.MySQLConnect;
import me.eelek.advancedkits.mysql.SaveData;
import me.eelek.advancedkits.mysql.UUIDFetcher;

public class PlayerHandler implements Listener {
	
	public static ArrayList<GamePlayer> data;
	
	private static AKitsMain plugin;
	
	public PlayerHandler(AKitsMain plugin) {
		PlayerHandler.plugin = plugin;
		data = new ArrayList<GamePlayer>();
	}
	
	public static void inputData(GamePlayer gP) {
		data.add(gP);
	}
	
	public static void inputData(Player p, int kills, int deaths, int points) {
		GamePlayer player = new GamePlayer(p, kills, deaths, points);
		data.add(player);
	}
	
	public static void removePlayer(Player p) {
		data.remove(p.getPlayerListName());
	}
	
	public static ArrayList<GamePlayer> getAllPlayerData() {
		return data;
	}
	
	public static GamePlayer getPlayer(String name) {
		for(GamePlayer p : data) {
			if(p.getPlayer().getPlayerListName().equals(name)) {
				 return p;
			}
		}
		
		return null;
	}
	
	public static ItemStack getKitSelectCompass() {
		ItemStack compass = new ItemStack(Material.COMPASS, 1);
		ItemMeta cM = (ItemMeta) compass.getItemMeta();
		cM.setDisplayName("" + ChatColor.GOLD + ChatColor.BOLD + "|" + ChatColor.DARK_RED + ChatColor.BOLD + " Select your kit! " + ChatColor.GOLD + ChatColor.BOLD + "|");
		compass.setItemMeta(cM);
		return compass;
	}

	@EventHandler
	public void playerJoin(PlayerJoinEvent e) {
		if(e.getPlayer().hasPlayedBefore()) {
			if(plugin.useDatabase()) {
				MySQLConnect.establishMySQLConnection(plugin.getMySQLData()[0], plugin.getMySQLData()[2], plugin.getMySQLData()[3], plugin.getMySQLData()[1]);
				try {
					GamePlayer player = LoadData.getPlayerData(e.getPlayer(), plugin);
					data.add(player);
					
				} catch(Exception ex) {
					ex.printStackTrace();
				}
				
				MySQLConnect.closeConnection();
			}
			
			if(plugin.useConfig()) {
				ConfigDataManager.getPlayerDataFromServer(e.getPlayer(), plugin);
			}
		} else {
			if(plugin.useDatabase()) {
				MySQLConnect.establishMySQLConnection(plugin.getMySQLData()[0], plugin.getMySQLData()[2], plugin.getMySQLData()[3], plugin.getMySQLData()[1]);
				try {
					LoadData.addNewPlayer(e.getPlayer(), plugin);
					GamePlayer player = LoadData.getPlayerData(e.getPlayer(), plugin);
					data.add(player);
				} catch(Exception ex) {
					ex.printStackTrace();
				}
				
				MySQLConnect.closeConnection();
			} 
			
			if(plugin.useConfig()) {
				inputData(e.getPlayer(), 0, 0, 0);
			}
		}
		
		Scoresboard.setScoreboard(plugin, e.getPlayer());
	}
	
	@EventHandler
	public void playerLeave(PlayerQuitEvent e) {
		MySQLConnect.establishMySQLConnection(plugin.getMySQLData()[0], plugin.getMySQLData()[2], plugin.getMySQLData()[3], plugin.getMySQLData()[1]);
		SaveData.savePlayerDataToDatabase(getPlayer(e.getPlayer().getPlayerListName()), plugin);
		MySQLConnect.closeConnection();
		removePlayer(e.getPlayer());
	}
	
	public static UUID getUUID(Player p) {
		UUIDFetcher fetcher = new UUIDFetcher(Arrays.asList(p.getPlayerListName()));
		Map<String, UUID> response = null;
		try {
			response = fetcher.call();
		} catch(Exception e) {
			AKitsMain.log.warning("[AdvancedKits] Exception while running UUIDFetcher");
			e.printStackTrace();
		}
		
		return response.get(p.getPlayerListName());
	}
	
	@EventHandler
	public void onKill(PlayerDeathEvent e) {
		Player killed = (Player) e.getEntity();
		Player killer = e.getEntity().getKiller();
		
		e.setDeathMessage(null);
		
		e.getDrops().clear();
		
		if(killed.getLastDamageCause().getCause().equals(DamageCause.ENTITY_ATTACK)) {
			e.setDeathMessage(ChatColor.BLUE + killed.getPlayerListName() + ChatColor.AQUA + " was slain by " + ChatColor.BLUE + killer.getPlayerListName());
			getPlayer(killer.getPlayerListName()).addKill();
			Scoresboard.setScoreboard(plugin, killer);
		}
		
		getPlayer(killed.getPlayerListName()).addDeath();
	}
	
	@EventHandler
	public void onRespawn(PlayerRespawnEvent e) {
		e.getPlayer().getInventory().clear();
		for(PotionEffect pE : e.getPlayer().getActivePotionEffects()) {
			PotionEffectType t = pE.getType();
			e.getPlayer().removePotionEffect(t);
		}
		
		e.getPlayer().getInventory().setItem(4, getKitSelectCompass());
		e.getPlayer().getInventory().setHeldItemSlot(4);
		
		Scoresboard.setScoreboard(plugin, e.getPlayer());
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if(p.getInventory().getItemInMainHand().getType() == Material.COMPASS) {
				if(p.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals("" + ChatColor.GOLD + ChatColor.BOLD + "|" + ChatColor.DARK_RED + ChatColor.BOLD + " Select your kit! " + ChatColor.GOLD + ChatColor.BOLD + "|")) {
					p.openInventory(KitManager.getSelectInventory());
				}
			}
		}
	}
}
