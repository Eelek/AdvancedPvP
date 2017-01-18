package me.eelek.advancedkits.players;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;

import me.eelek.advancedkits.AKitsMain;
import me.eelek.advancedkits.ConfigDataManager;
import me.eelek.advancedkits.arena.Arena;
import me.eelek.advancedkits.arena.ArenaManager;
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
	
	public static void inputData(Player p, int kills, int deaths, int points, int level) {
		data.add(new GamePlayer(p, kills, deaths, points, level));
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
				MySQLConnect.establishMySQLConnection(plugin.getMySQLData("host"), plugin.getMySQLData("user"), plugin.getMySQLData("pass"), plugin.getMySQLData("database"));
				try {
					data.add(LoadData.getPlayerData(e.getPlayer(), plugin));
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
				MySQLConnect.establishMySQLConnection(plugin.getMySQLData("host"), plugin.getMySQLData("user"), plugin.getMySQLData("pass"), plugin.getMySQLData("database"));
				try {
					LoadData.addNewPlayer(e.getPlayer(), plugin);
					data.add(LoadData.getPlayerData(e.getPlayer(), plugin));
				} catch(Exception ex) {
					ex.printStackTrace();
				}
				
				MySQLConnect.closeConnection();
			} 
			
			if(plugin.useConfig()) {
				inputData(e.getPlayer(), 0, 0, 0, 0);
			}
		}
		
		Scoresboard.setScoreboard(plugin, e.getPlayer());
		
		e.getPlayer().setDisplayName(Levels.getLevel(getPlayer(e.getPlayer().getPlayerListName()).getLevel()).getPrefix() + " " + ChatColor.RESET + e.getPlayer().getPlayerListName());
	}
	
	@EventHandler
	public void playerLeave(PlayerQuitEvent e) {
		MySQLConnect.establishMySQLConnection(plugin.getMySQLData("host"), plugin.getMySQLData("user"), plugin.getMySQLData("pass"), plugin.getMySQLData("database"));
		SaveData.savePlayerDataToDatabase(getPlayer(e.getPlayer().getPlayerListName()), plugin);
		MySQLConnect.closeConnection();
		if(getPlayer(e.getPlayer().getPlayerListName()).isPlaying()) {
			ArenaManager.getArena(getPlayer(e.getPlayer().getPlayerListName()).getCurrentArena()).removePlayer(e.getPlayer());
			getPlayer(e.getPlayer().getPlayerListName()).setPlaying(false);
		}
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
			
			Levels.levelUp(getPlayer(killer.getPlayerListName()));
			
			Scoresboard.setScoreboard(plugin, killer);
		} else if(killed.getLastDamageCause().getCause().equals(DamageCause.PROJECTILE)) {
			e.setDeathMessage(ChatColor.BLUE + killed.getPlayerListName() + ChatColor.AQUA + " was shot by " + ChatColor.BLUE + killer.getPlayerListName());
			getPlayer(killer.getPlayerListName()).addKill();
			
			Levels.levelUp(getPlayer(killer.getPlayerListName()));
			
			Scoresboard.setScoreboard(plugin, killer);
		}
		
		getPlayer(killed.getPlayerListName()).addDeath();
	}
	
	@EventHandler
	public void onRespawn(PlayerRespawnEvent e) {
		e.getPlayer().getInventory().clear();
		for(PotionEffect pE : e.getPlayer().getActivePotionEffects()) {
			e.getPlayer().removePotionEffect(pE.getType());
		}
		
		Scoresboard.setScoreboard(plugin, e.getPlayer());
		
		if(getPlayer(e.getPlayer().getPlayerListName()).isPlaying()) {
			e.setRespawnLocation(ArenaManager.getArena(getPlayer(e.getPlayer().getPlayerListName()).getCurrentArena()).getLobbyLocation());
			e.getPlayer().getInventory().setItem(4, getKitSelectCompass());
			e.getPlayer().getInventory().setHeldItemSlot(4);
		}
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		
		if(getPlayer(p.getPlayerListName()).isPlaying()) {
			if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.LEFT_CLICK_BLOCK) {
				if (p.getInventory().getItemInMainHand().getType() == Material.COMPASS) {
					if (p.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals("" + ChatColor.GOLD + ChatColor.BOLD + "|" + ChatColor.DARK_RED + ChatColor.BOLD + " Select your kit! " + ChatColor.GOLD + ChatColor.BOLD + "|")) {
						p.openInventory(KitManager.getSelectInventory(ArenaManager.getArena(getPlayer(p.getPlayerListName()).getCurrentArena())));
					}
				} else if (p.getInventory().getItemInMainHand().getType() == Material.DIAMOND_HOE) {
					if (p.getInventory().getItemInMainHand().getItemMeta().getDisplayName().contains(ChatColor.BLUE + "Select spawns ")) {
						e.setCancelled(true);
						Arena a = ArenaManager.getArena(p.getInventory().getItemInMainHand().getItemMeta().getDisplayName().split("" + ChatColor.AQUA)[1]);
						a.addSpawn(e.getClickedBlock().getLocation());
						p.sendMessage(ChatColor.BLUE + "Added spawn " + ChatColor.AQUA + a.getAmountOfSpawns() + ChatColor.BLUE + " for arena " + ChatColor.AQUA + a.getName() + ChatColor.BLUE + ".");
					}
				}
			}
			
			if(e.hasBlock()) {
				if(e.getClickedBlock().getType() == Material.SIGN_POST || e.getClickedBlock().getType() == Material.WALL_SIGN) {
					Sign s = (Sign) e.getClickedBlock().getState();
					if(s.getLine(0).contains("§3§l[§2§l")) {
						Arena a = ArenaManager.getArena(s.getLine(2));
						p.teleport(p.getWorld().getSpawnLocation());
						getPlayer(p.getPlayerListName()).setPlaying(false);
						getPlayer(p.getPlayerListName()).setCurrentArena(null);
						a.removePlayer(p);
						p.getInventory().clear();
						for(PotionEffect pE : e.getPlayer().getActivePotionEffects()) {
							e.getPlayer().removePotionEffect(pE.getType());
						}
					}
				}
			}
		} else {
			if(p.getInventory().getItemInMainHand().getType() == Material.DIAMOND_HOE) {
				if(p.getInventory().getItemInMainHand().getItemMeta().getDisplayName().contains(ChatColor.BLUE + "Select spawns ")) {
					e.setCancelled(true);
					Arena a = ArenaManager.getArena(p.getInventory().getItemInMainHand().getItemMeta().getDisplayName().split("" + ChatColor.AQUA)[1]);
					a.addSpawn(e.getClickedBlock().getLocation());
					p.sendMessage(ChatColor.BLUE + "Added spawn " + ChatColor.AQUA + a.getAmountOfSpawns() + ChatColor.BLUE + " for arena " + ChatColor.AQUA + a.getName() + ChatColor.BLUE + ".");
				}
			} else if(p.getInventory().getItemInMainHand().getType() == Material.GOLD_HOE) {
				if(p.getInventory().getItemInMainHand().getItemMeta().getDisplayName().contains(ChatColor.BLUE + "Select lobby ")) {
					e.setCancelled(true);
					Arena a = ArenaManager.getArena(p.getInventory().getItemInMainHand().getItemMeta().getDisplayName().split("" + ChatColor.AQUA)[1]);
					if(a.hasLobby() == false) {
						a.setLobbyLocation(e.getClickedBlock().getLocation());
						p.sendMessage(ChatColor.BLUE + "Set " + ChatColor.AQUA + a.getName() + ChatColor.BLUE +  "'s lobby location to: " + ChatColor.AQUA + e.getClickedBlock().getX() + ", " + e.getClickedBlock().getY() + ", " + e.getClickedBlock().getZ() + ChatColor.BLUE + ".");
					}
				}
			}
			
			if(e.hasBlock()) {
				if(e.getClickedBlock().getType() == Material.SIGN_POST || e.getClickedBlock().getType() == Material.WALL_SIGN) {
					Sign s = (Sign) e.getClickedBlock().getState();
					if(s.getLine(0).contains("§6§l[§4§l")) {
						Arena a = ArenaManager.getArena(s.getLine(1));
						if(getPlayer(p.getPlayerListName()).getLevel() >= a.getMinimumLevel()) {
							if(a.isActive() && (a.getCurrentPlayers().size() < a.getMaxPlayers())) {
								p.teleport(a.getLobbyLocation());
								getPlayer(p.getPlayerListName()).setPlaying(true);
								getPlayer(p.getPlayerListName()).setCurrentArena(a.getName());
								a.addPlayer(p);
								p.getInventory().setItem(4, getKitSelectCompass());
								p.getInventory().setHeldItemSlot(4);
							} else {
								p.sendMessage(ChatColor.RED + "Arena " + ChatColor.DARK_RED + a.getName() + ChatColor.RED + " is disabled.");
							}
						} else {
							p.sendMessage(ChatColor.RED + "You can't join this arena. You need to be atleast level " + ChatColor.DARK_RED + a.getMinimumLevel() + ChatColor.RED + ".");
						}
					} else if(s.getLine(0).contains("§3§l[§2§l")) {
						Arena a = ArenaManager.getArena(s.getLine(2));
						p.teleport(p.getWorld().getSpawnLocation());
						getPlayer(p.getPlayerListName()).setPlaying(false);
						getPlayer(p.getPlayerListName()).setCurrentArena(null);
						a.removePlayer(p);
						p.getInventory().clear();
						for(PotionEffect pE : e.getPlayer().getActivePotionEffects()) {
							e.getPlayer().removePotionEffect(pE.getType());
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		e.setFormat(e.getPlayer().getDisplayName() + ChatColor.GRAY + ": " + e.getMessage());
	}
}
