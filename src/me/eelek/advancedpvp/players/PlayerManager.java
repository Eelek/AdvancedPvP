package me.eelek.advancedpvp.players;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
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

import me.eelek.advancedpvp.DataManager;
import me.eelek.advancedpvp.arena.Arena;
import me.eelek.advancedpvp.arena.ArenaManager;
import me.eelek.advancedpvp.game.GameManager.GameType;
import me.eelek.advancedpvp.kits.KitManager;
import net.minecraft.server.v1_12_R1.EntityPlayer;
import net.minecraft.server.v1_12_R1.IChatBaseComponent;
import net.minecraft.server.v1_12_R1.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_12_R1.PacketPlayOutChat;

public class PlayerManager implements Listener {
	
	private ArrayList<GamePlayer> data;
	
	private static PlayerManager instance = null;
	
	private String defaultChannel;
	
	protected PlayerManager() { 
		data = new ArrayList<GamePlayer>();
	}
	
    public static PlayerManager getInstance() {
		if(instance == null) {
			instance = new PlayerManager();
		}
		
		return instance;
	}
	
	void inputData(GamePlayer gP) {
		data.add(gP);
	}
	
	public void inputData(Player p, int kills, int deaths, int points, int level, String c) {
		data.add(new GamePlayer(p, kills, deaths, points, level, c));
	}
	
	void removePlayer(Player p) {
		data.remove(getPlayer(p.getPlayerListName()));
	}
	
	public void setDefaultChannel(String channel) {
		this.defaultChannel = channel;
	}
	
	public ArrayList<GamePlayer> getAllPlayerData() {
		return data;
	}
	
	public GamePlayer getPlayer(String name) {
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
			DataManager.getInstance().getPlayerData(e.getPlayer(), defaultChannel);
		} else {
			inputData(e.getPlayer(), 0, 0, 0, 0, defaultChannel);
			DataManager.getInstance().createPlayerData(e.getPlayer());
		}
		
		e.getPlayer().setScoreboard(Scoreboards.setFFAScoreboard(e.getPlayer()));
		getPlayer(e.getPlayer().getPlayerListName()).setBoard(e.getPlayer().getScoreboard());
		
		e.getPlayer().teleport(e.getPlayer().getWorld().getSpawnLocation());
	}
	
	@EventHandler
	void playerLeave(PlayerQuitEvent e) {
		GamePlayer leave = getPlayer(e.getPlayer().getPlayerListName());
		
		DataManager.getInstance().updateOnLeave(leave);
		
		if(leave.isPlaying()) {
			ArenaManager.getInstance().getArena(leave.getCurrentArena()).removePlayer(e.getPlayer());
			leave.setPlaying(false);
		}
		
		removePlayer(e.getPlayer());
		
		e.getPlayer().getInventory().clear();
		for(PotionEffect pE : e.getPlayer().getActivePotionEffects()) {
			e.getPlayer().removePotionEffect(pE.getType());
		}
	}
	
	public UUID getUUID(Player p) {
		UUIDFetcher fetcher = new UUIDFetcher(Arrays.asList(p.getPlayerListName()));
		Map<String, UUID> response = null;
		try {
			response = fetcher.call();
		} catch(Exception e) {
			System.out.println("[AdvancedKits] Exception while running UUIDFetcher");
			e.printStackTrace();
		}
		
		return response.get(p.getPlayerListName());
	}
	
	@EventHandler (priority = EventPriority.HIGHEST)
	void onKill(PlayerDeathEvent e) {
		GamePlayer killed = getPlayer(((Player) e.getEntity()).getPlayerListName());
		GamePlayer killer = getPlayer(e.getEntity().getKiller().getPlayerListName());
		
		e.setDeathMessage(null);
		
		e.getDrops().clear();
		
		if(killed.getPlayer().getLastDamageCause().getCause().equals(DamageCause.ENTITY_ATTACK)) {
			e.setDeathMessage(ChatColor.BLUE + killed.getPlayer().getPlayerListName() + ChatColor.AQUA + " was slain by " + ChatColor.BLUE + killer.getPlayer().getPlayerListName());
			
		} else if(killed.getPlayer().getLastDamageCause().getCause().equals(DamageCause.PROJECTILE)) {
			e.setDeathMessage(ChatColor.BLUE + killed.getPlayer().getPlayerListName() + ChatColor.AQUA + " was shot by " + ChatColor.BLUE + killer.getPlayer().getPlayerListName());
		} else {
			e.setDeathMessage(ChatColor.BLUE + killed.getPlayer().getPlayerListName() + ChatColor.AQUA + " died.");
		}
		
		killer.addKill();
		
		Levels.getInstance().levelUp(killer);
		
		killer.getBoard().resetScores("" + ChatColor.GREEN + (killer.getKills() - 1));
		killer.getBoard().getObjective("show").getScore("" + ChatColor.GREEN + killer.getKills()).setScore(10);
		
		killer.getBoard().resetScores("" + ChatColor.GOLD + (killer.getPoints() - 1));
		killer.getBoard().getObjective("show").getScore("" + ChatColor.GOLD + killer.getPoints()).setScore(4);
		
		getPlayer(killed.getPlayer().getPlayerListName()).addDeath();
		
		ArenaManager.getInstance().getArena(killed.getCurrentArena()).getSpawn(killed.getPlayer().getPlayerListName()).resetPlayer();
		
		killed.getBoard().resetScores("" + ChatColor.RED + (killed.getDeaths() - 1));
	    killed.getBoard().getObjective("show").getScore("" + ChatColor.RED + killed.getDeaths()).setScore(7);
	}
	
	@EventHandler (priority = EventPriority.HIGHEST)
	void onRespawn(PlayerRespawnEvent e) {
		e.getPlayer().getInventory().clear();
		for(PotionEffect pE : e.getPlayer().getActivePotionEffects()) {
			e.getPlayer().removePotionEffect(pE.getType());
		}
				
		if(getPlayer(e.getPlayer().getPlayerListName()).isPlaying()) {
			e.setRespawnLocation(ArenaManager.getInstance().getArena(getPlayer(e.getPlayer().getPlayerListName()).getCurrentArena()).getLobbyLocation());
			if(ArenaManager.getInstance().getArena(getPlayer(e.getPlayer().getPlayerListName()).getCurrentArena()).getType() != GameType.TDM_RANK) {
				e.getPlayer().getInventory().setItem(4, getKitSelectCompass());
				e.getPlayer().getInventory().setHeldItemSlot(4);	
			} else {
				KitManager.getInstance().giveKit(getPlayer(e.getPlayer().getPlayerListName()), getPlayer(e.getPlayer().getPlayerListName()).getSelectedKit());
			}
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
					PacketPlayOutChat packet = new PacketPlayOutChat(msg);
					
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
		GamePlayer player = getPlayer(p.getPlayerListName());
		
		if(player.isPlaying()) {
			if (p.getInventory().getItemInMainHand().getType() == Material.COMPASS) {
				if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.LEFT_CLICK_BLOCK) {
					if (p.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals("" + ChatColor.GOLD + ChatColor.BOLD + "|" + ChatColor.DARK_RED + ChatColor.BOLD + " Select your kit! " + ChatColor.GOLD + ChatColor.BOLD + "|")) {
						p.openInventory(KitManager.getInstance().getSelectInventory(player, ArenaManager.getInstance().getArena(player.getCurrentArena())));
					}
				}
			}
			
			if(e.hasBlock()) {
				if(e.getClickedBlock().getType() == Material.SIGN_POST || e.getClickedBlock().getType() == Material.WALL_SIGN) {
					Sign s = (Sign) e.getClickedBlock().getState();
					if(s.getLine(0).contains("§3§l[§2§l")) {
						Arena a = ArenaManager.getInstance().getArena(s.getLine(2));
						p.teleport(p.getWorld().getSpawnLocation());
						player.setPlaying(false);
						player.setCurrentArena(null);
						a.removePlayer(p);
						p.getInventory().clear();
						for(PotionEffect pE : e.getPlayer().getActivePotionEffects()) {
							e.getPlayer().removePotionEffect(pE.getType());
						}
						
						if(!player.getChatChannel().equals("staff") && !player.getChatChannel().equals("broad")) {
							player.setChatChannel("lobby");
						}
					}
				}
			}
		} else {
			if(e.hasBlock()) {
				if(e.getClickedBlock().getType() == Material.SIGN_POST || e.getClickedBlock().getType() == Material.WALL_SIGN) {
					Sign s = (Sign) e.getClickedBlock().getState();
					if(s.getLine(0).contains("§6§l[§4§l")) {
						 if(s.getLine(0).equals("§6§l[§4§lDUEL§6§l]")) {
							Arena a = ArenaManager.getInstance().getDuelArena();
							if(player.getLevel() >= a.getMinimumLevel()) {
								if(a.isActive()) {
									if(a.getCurrentPlayers().size() < a.getMaxPlayers()) {
										p.teleport(a.getLobbyLocation());
										player.setPlaying(true);
										player.setCurrentArena(a.getName());
										a.addPlayer(p);
										p.getInventory().setItem(4, getKitSelectCompass());
										p.getInventory().setHeldItemSlot(4);
										if(!player.getChatChannel().equals("staff")) {
											player.setChatChannel(a.getName());
										}					
									} else {
										p.sendMessage(ChatColor.RED + "Arena " + ChatColor.DARK_RED + a.getName() + ChatColor.RED + " is full.");
									}
								} else {
									p.sendMessage(ChatColor.RED + "Arena " + ChatColor.DARK_RED + a.getName() + ChatColor.RED + " is disabled.");
								}
							} else {
								p.sendMessage(ChatColor.RED + "You can't join this arena. You need to be atleast level " + ChatColor.DARK_RED + a.getMinimumLevel() + ChatColor.RED + ".");
							}
						} else {
							Arena a = ArenaManager.getInstance().getArena(s.getLine(1));
							if(player.getLevel() >= a.getMinimumLevel()) {
								if(a.isActive() && (a.getCurrentPlayers().size() < a.getMaxPlayers())) {
									p.teleport(a.getLobbyLocation());
									player.setPlaying(true);
									player.setCurrentArena(a.getName());
									a.addPlayer(p);
									p.getInventory().setItem(4, getKitSelectCompass());
									p.getInventory().setHeldItemSlot(4);
									if(player.getChatChannel().equals("staff")) {
										player.setChatChannel(a.getName());
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
