package me.eelek.advancedkits.players;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
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
import me.eelek.advancedkits.mysql.MySQLConnect;
import me.eelek.advancedkits.mysql.UUIDFetcher;
import net.minecraft.server.v1_11_R1.EntityPlayer;
import net.minecraft.server.v1_11_R1.IChatBaseComponent;
import net.minecraft.server.v1_11_R1.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_11_R1.PacketPlayOutChat;

public class PlayerHandler implements Listener {
	
	private static ArrayList<GamePlayer> data;
	
	private AKitsMain plugin;
	
	public PlayerHandler(AKitsMain plugin) { 
		this.plugin = plugin;
		data = new ArrayList<GamePlayer>();
	}
	
	void inputData(GamePlayer gP) {
		data.add(gP);
	}
	
	public static void inputData(Player p, int kills, int deaths, int points, int level, String c) {
		data.add(new GamePlayer(p, kills, deaths, points, level, c));
	}
	
	void removePlayer(Player p) {
		data.remove(getPlayer(p.getPlayerListName()));
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
	
	ItemStack getKitSelectCompass() {
		ItemStack compass = new ItemStack(Material.COMPASS, 1);
		ItemMeta cM = (ItemMeta) compass.getItemMeta();
		cM.setDisplayName("" + ChatColor.GOLD + ChatColor.BOLD + "|" + ChatColor.DARK_RED + ChatColor.BOLD + " Select your kit! " + ChatColor.GOLD + ChatColor.BOLD + "|");
		compass.setItemMeta(cM);
		return compass;
	}

	@EventHandler
	void playerJoin(PlayerJoinEvent e) {
		if(e.getPlayer().hasPlayedBefore()) {
			if(plugin.useDatabase()) {
				MySQLConnect.establishMySQLConnection(plugin.getMySQLData("host"), plugin.getMySQLData("user"), plugin.getMySQLData("pass"), plugin.getMySQLData("database"));
				try {
					data.add(MySQLConnect.getPlayerData(e.getPlayer(), plugin));
				} catch(Exception ex) {
					ex.printStackTrace();
				}
				
				MySQLConnect.closeConnection();
			} else if(plugin.useConfig()) {
				ConfigDataManager.getPlayerDataFromServer(e.getPlayer(), plugin);
			}
		} else {
			if(plugin.useDatabase()) {
				MySQLConnect.establishMySQLConnection(plugin.getMySQLData("host"), plugin.getMySQLData("user"), plugin.getMySQLData("pass"), plugin.getMySQLData("database"));
				try {
					MySQLConnect.addNewPlayer(e.getPlayer(), plugin);
					data.add(MySQLConnect.getPlayerData(e.getPlayer(), plugin));
				} catch(Exception ex) {
					ex.printStackTrace();
				}
				
				MySQLConnect.closeConnection();
			} 
			
			if(plugin.useConfig() && !plugin.useDatabase()) {
				inputData(e.getPlayer(), 0, 0, 0, 0, plugin.getConfig().getString("default-channel"));
			}
		}
		
		Scoresboard.setScoreboard(plugin, e.getPlayer());
		
		e.getPlayer().teleport(e.getPlayer().getWorld().getSpawnLocation());
	}
	
	@EventHandler
	void playerLeave(PlayerQuitEvent e) {
		if(plugin.useDatabase()) {
			MySQLConnect.establishMySQLConnection(plugin.getMySQLData("host"), plugin.getMySQLData("user"), plugin.getMySQLData("pass"), plugin.getMySQLData("database"));
			MySQLConnect.savePlayerDataToDatabase(getPlayer(e.getPlayer().getPlayerListName()), plugin);
			MySQLConnect.closeConnection();
		}
		
		if(plugin.useConfig()) {
			ConfigDataManager.savePlayer(getPlayer(e.getPlayer().getPlayerListName()), plugin);
		}
		
		if(getPlayer(e.getPlayer().getPlayerListName()).isPlaying()) {
			ArenaManager.getInstance().getArena(getPlayer(e.getPlayer().getPlayerListName()).getCurrentArena()).removePlayer(e.getPlayer());
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
	void onKill(PlayerDeathEvent e) {
		Player killed = (Player) e.getEntity();
		Player killer = e.getEntity().getKiller();
		
		if(killed.getPlayerListName().equals(killer.getPlayerListName())) return;
		
		e.setDeathMessage(null);
		
		e.getDrops().clear();
		
		if(killed.getLastDamageCause().getCause().equals(DamageCause.ENTITY_ATTACK)) {
			e.setDeathMessage(ChatColor.BLUE + killed.getPlayerListName() + ChatColor.AQUA + " was slain by " + ChatColor.BLUE + killer.getPlayerListName());
			getPlayer(killer.getPlayerListName()).addKill();
			
			Levels.getInstance().levelUp(getPlayer(killer.getPlayerListName()));
			
			getPlayer(killer.getPlayerListName()).getBoard().resetScores("" + ChatColor.GREEN + (getPlayer(killer.getPlayerListName()).getKills() - 1));
			getPlayer(killer.getPlayerListName()).getBoard().getObjective("scores").getScore("" + ChatColor.GREEN + getPlayer(killer.getPlayerListName()).getKills()).setScore(10);
		} else if(killed.getLastDamageCause().getCause().equals(DamageCause.PROJECTILE)) {
			e.setDeathMessage(ChatColor.BLUE + killed.getPlayerListName() + ChatColor.AQUA + " was shot by " + ChatColor.BLUE + killer.getPlayerListName());
			getPlayer(killer.getPlayerListName()).addKill();
			
			Levels.getInstance().levelUp(getPlayer(killer.getPlayerListName()));
			
			getPlayer(killer.getPlayerListName()).getBoard().resetScores("" + ChatColor.GREEN + (getPlayer(killer.getPlayerListName()).getKills() - 1));
			getPlayer(killer.getPlayerListName()).getBoard().getObjective("scores").getScore("" + ChatColor.GREEN + getPlayer(killer.getPlayerListName()).getKills()).setScore(10);
		}
		
		getPlayer(killed.getPlayerListName()).addDeath();
		ArenaManager.getInstance().getArena(getPlayer(killed.getPlayerListName()).getCurrentArena()).removeSpawnPlayer(killed.getPlayerListName());
	}
	
	@EventHandler (priority = EventPriority.HIGHEST)
	void onRespawn(PlayerRespawnEvent e) {
		e.getPlayer().getInventory().clear();
		for(PotionEffect pE : e.getPlayer().getActivePotionEffects()) {
			e.getPlayer().removePotionEffect(pE.getType());
		}
		
		getPlayer(e.getPlayer().getPlayerListName()).getBoard().resetScores("" + ChatColor.GREEN + (getPlayer(e.getPlayer().getPlayerListName()).getDeaths() - 1));
		getPlayer(e.getPlayer().getPlayerListName()).getBoard().getObjective("scores").getScore("" + ChatColor.GREEN + getPlayer(e.getPlayer().getPlayerListName()).getDeaths()).setScore(7);
		
		if(getPlayer(e.getPlayer().getPlayerListName()).isPlaying()) {
			e.setRespawnLocation(ArenaManager.getInstance().getArena(getPlayer(e.getPlayer().getPlayerListName()).getCurrentArena()).getLobbyLocation());
			e.getPlayer().getInventory().setItem(4, getKitSelectCompass());
			e.getPlayer().getInventory().setHeldItemSlot(4);
		} else {
			e.setRespawnLocation(e.getPlayer().getWorld().getSpawnLocation());
		}
	}
	
	@EventHandler
	void onPlayerChat(AsyncPlayerChatEvent e) {
		GamePlayer sender = getPlayer(e.getPlayer().getPlayerListName());
		String senderChannel = sender.getChatChannel();
		
		for(Player player : e.getRecipients()) {
			GamePlayer recipient = getPlayer(player.getPlayerListName());
			if(recipient.getChatChannel().equalsIgnoreCase(senderChannel)) {
				if(senderChannel.equalsIgnoreCase("staff")) {
					e.setCancelled(true);
					
					String message = "[\"\",{\"text\":\"[Staff]\",\"color\":\"dark_green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/kit channel staff\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"Go to the staff channel\"}]}}},{\"text\":\" " + Levels.getInstance().getLevel(sender.getLevel()).getPrefix() + "\",\"color\":\"none\"},{\"text\":\" " + e.getPlayer().getDisplayName() + "\",\"color\":\"none\"},{\"text\":\":\",\"color\":\"gray\"},{\"text\":\" " + e.getMessage() + "\",\"color\":\"gray\"}]";
					
					IChatBaseComponent msg = ChatSerializer.a(message);
					PacketPlayOutChat packet = new PacketPlayOutChat(msg, (byte) 1);
					
					EntityPlayer eP = ((CraftPlayer) recipient.getPlayer()).getHandle();
					eP.playerConnection.sendPacket(packet);
				} else if (senderChannel.equalsIgnoreCase("broad")) {
					e.setCancelled(true);
					
					String message = "[\"\",{\"text\":\"[Broad]\",\"color\":\"dark_blue\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/kit channel broad\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"Go to the broad channel\"}]}}},{\"text\":\" " + Levels.getInstance().getLevel(sender.getLevel()).getPrefix() + "\",\"color\":\"none\"},{\"text\":\" " + e.getPlayer().getDisplayName() + "\",\"color\":\"none\"},{\"text\":\":\",\"color\":\"gray\"},{\"text\":\" " + e.getMessage() + "\",\"color\":\"gray\"}]";
					
					IChatBaseComponent msg = ChatSerializer.a(message);
					PacketPlayOutChat packet = new PacketPlayOutChat(msg);
					
					EntityPlayer eP = ((CraftPlayer) recipient.getPlayer()).getHandle();
					eP.playerConnection.sendPacket(packet);
				} else {
					e.setCancelled(true);
					player.sendMessage(Levels.getInstance().getLevel(sender.getLevel()).getPrefix() + " " + ChatColor.RESET + e.getPlayer().getDisplayName() + ChatColor.GRAY + ": " + e.getMessage());	
				}
			} else if(senderChannel.equalsIgnoreCase("broad")) {
				if(!recipient.getChatChannel().equalsIgnoreCase("staff")) {
					e.setCancelled(true);
					player.sendMessage(Levels.getInstance().getLevel(sender.getLevel()).getPrefix() + " " + ChatColor.RESET + e.getPlayer().getDisplayName() + ChatColor.GRAY + ": " + e.getMessage());	
				}
			} else if(recipient.getChatChannel().equalsIgnoreCase("broad")) {
				if(!senderChannel.equalsIgnoreCase("staff")) {
					e.setCancelled(true);
					
                    String message = "[\"\",{\"text\":\"[" + senderChannel + "]\",\"color\":\"yellow\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/kit channel " + senderChannel + "\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"Go to channel " + senderChannel + "\"}]}}},{\"text\":\" " + Levels.getInstance().getLevel(sender.getLevel()).getPrefix() + "\",\"color\":\"none\"},{\"text\":\" " + e.getPlayer().getDisplayName() + "\",\"color\":\"none\"},{\"text\":\":\",\"color\":\"gray\"},{\"text\":\" " + e.getMessage() + "\",\"color\":\"gray\"}]";
					
					IChatBaseComponent msg = ChatSerializer.a(message);
					PacketPlayOutChat packet = new PacketPlayOutChat(msg);
					
					EntityPlayer eP = ((CraftPlayer) recipient.getPlayer()).getHandle();
					eP.playerConnection.sendPacket(packet);	
				}
			}
		}
	}
	
	@EventHandler
	void onInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		
		if(getPlayer(p.getPlayerListName()).isPlaying()) {
			if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.LEFT_CLICK_BLOCK) {
				if (p.getInventory().getItemInMainHand().getType() == Material.COMPASS) {
					if (p.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals("" + ChatColor.GOLD + ChatColor.BOLD + "|" + ChatColor.DARK_RED + ChatColor.BOLD + " Select your kit! " + ChatColor.GOLD + ChatColor.BOLD + "|")) {
						p.openInventory(KitManager.getInstance().getSelectInventory(p, ArenaManager.getInstance().getArena(getPlayer(p.getPlayerListName()).getCurrentArena())));
					}
				} else if (p.getInventory().getItemInMainHand().getType() == Material.DIAMOND_HOE) {
					if (p.getInventory().getItemInMainHand().getItemMeta().getDisplayName().contains(ChatColor.BLUE + "Select spawns ")) {
						e.setCancelled(true);
						Arena a = ArenaManager.getInstance().getArena(p.getInventory().getItemInMainHand().getItemMeta().getDisplayName().split("" + ChatColor.AQUA)[1]);
						a.addSpawn(e.getClickedBlock().getLocation());
						p.sendMessage(ChatColor.BLUE + "Added spawn " + ChatColor.AQUA + a.getAmountOfSpawns() + ChatColor.BLUE + " for arena " + ChatColor.AQUA + a.getName() + ChatColor.BLUE + ".");
					}
				}
			}
			
			if(e.hasBlock()) {
				if(e.getClickedBlock().getType() == Material.SIGN_POST || e.getClickedBlock().getType() == Material.WALL_SIGN) {
					Sign s = (Sign) e.getClickedBlock().getState();
					if(s.getLine(0).contains("§3§l[§2§l")) {
						Arena a = ArenaManager.getInstance().getArena(s.getLine(2));
						p.teleport(p.getWorld().getSpawnLocation());
						getPlayer(p.getPlayerListName()).setPlaying(false);
						getPlayer(p.getPlayerListName()).setCurrentArena(null);
						a.removePlayer(p);
						p.getInventory().clear();
						for(PotionEffect pE : e.getPlayer().getActivePotionEffects()) {
							e.getPlayer().removePotionEffect(pE.getType());
						}
						
						if(!getPlayer(p.getPlayerListName()).getChatChannel().equals("staff") && !getPlayer(p.getPlayerListName()).getChatChannel().equals("broad")) {
							getPlayer(p.getPlayerListName()).setChatChannel("lobby");
						}
					}
				}
			}
		} else {
			if(p.getInventory().getItemInMainHand().getType() == Material.DIAMOND_HOE) {
				if(p.getInventory().getItemInMainHand().getItemMeta().getDisplayName().contains(ChatColor.BLUE + "Select spawns ")) {
					e.setCancelled(true);
					Arena a = ArenaManager.getInstance().getArena(p.getInventory().getItemInMainHand().getItemMeta().getDisplayName().split("" + ChatColor.AQUA)[1]);
					a.addSpawn(e.getClickedBlock().getLocation());
					p.sendMessage(ChatColor.BLUE + "Added spawn " + ChatColor.AQUA + a.getAmountOfSpawns() + ChatColor.BLUE + " for arena " + ChatColor.AQUA + a.getName() + ChatColor.BLUE + ".");
				}
			} else if(p.getInventory().getItemInMainHand().getType() == Material.GOLD_HOE) {
				if(p.getInventory().getItemInMainHand().getItemMeta().getDisplayName().contains(ChatColor.BLUE + "Select lobby ")) {
					e.setCancelled(true);
					Arena a = ArenaManager.getInstance().getArena(p.getInventory().getItemInMainHand().getItemMeta().getDisplayName().split("" + ChatColor.AQUA)[1]);
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
						 if(s.getLine(0).equals("§6§l[§4§lDUEL§6§l]")) {
							Arena a = ArenaManager.getInstance().getDuelArena();
							if(getPlayer(p.getPlayerListName()).getLevel() >= a.getMinimumLevel()) {
								if(a.isActive() && (a.getCurrentPlayers().size() < a.getMaxPlayers())) {
									p.teleport(a.getLobbyLocation());
									getPlayer(p.getPlayerListName()).setPlaying(true);
									getPlayer(p.getPlayerListName()).setCurrentArena(a.getName());
									a.addPlayer(p);
									p.getInventory().setItem(4, getKitSelectCompass());
									p.getInventory().setHeldItemSlot(4);
									if(!getPlayer(p.getPlayerListName()).getChatChannel().equals("staff")) {
										getPlayer(p.getPlayerListName()).setChatChannel(a.getName());
									}									
								} else {
									p.sendMessage(ChatColor.RED + "Arena " + ChatColor.DARK_RED + a.getName() + ChatColor.RED + " is disabled.");
								}
							} else {
								p.sendMessage(ChatColor.RED + "You can't join this arena. You need to be atleast level " + ChatColor.DARK_RED + a.getMinimumLevel() + ChatColor.RED + ".");
							}
						} else {
							Arena a = ArenaManager.getInstance().getArena(s.getLine(1));
							if(getPlayer(p.getPlayerListName()).getLevel() >= a.getMinimumLevel()) {
								if(a.isActive() && (a.getCurrentPlayers().size() < a.getMaxPlayers())) {
									p.teleport(a.getLobbyLocation());
									getPlayer(p.getPlayerListName()).setPlaying(true);
									getPlayer(p.getPlayerListName()).setCurrentArena(a.getName());
									a.addPlayer(p);
									p.getInventory().setItem(4, getKitSelectCompass());
									p.getInventory().setHeldItemSlot(4);
									if(!getPlayer(p.getPlayerListName()).getChatChannel().equals("staff")) {
										getPlayer(p.getPlayerListName()).setChatChannel(a.getName());
									}
								} else {
									p.sendMessage(ChatColor.RED + "Arena " + ChatColor.DARK_RED + a.getName() + ChatColor.RED + " is disabled.");
								}
							} else {
								p.sendMessage(ChatColor.RED + "You can't join this arena. You need to be atleast level " + ChatColor.DARK_RED + a.getMinimumLevel() + ChatColor.RED + ".");
							}
						}
					} else if(s.getLine(0).contains("§3§l[§2§l")) {
						p.sendMessage(ChatColor.DARK_RED + "You are not in an arena.");
					}
				}
			}
		}
	}
}
