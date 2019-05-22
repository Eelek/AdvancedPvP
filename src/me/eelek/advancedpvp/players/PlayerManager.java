package me.eelek.advancedpvp.players;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import me.eelek.advancedpvp.DataManager;
import me.eelek.advancedpvp.ItemStackMaker;
import me.eelek.advancedpvp.arena.Arena;
import me.eelek.advancedpvp.arena.ArenaManager;
import me.eelek.advancedpvp.kits.KitManager;
import net.minecraft.server.v1_13_R2.EntityPlayer;
import net.minecraft.server.v1_13_R2.IChatBaseComponent;
import net.minecraft.server.v1_13_R2.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_13_R2.PacketPlayOutChat;

public class PlayerManager implements Listener {

	private ArrayList<GamePlayer> data;

	private static PlayerManager instance = null;

	private String defaultChannel;

	protected PlayerManager() {
		data = new ArrayList<GamePlayer>();
	}

	public static PlayerManager getInstance() {
		if (instance == null) {
			instance = new PlayerManager();
		}

		return instance;
	}

	/**
	 * Add a GamePlayer.
	 * @param gP The GamePlayer to be added.
	 */
	void inputData(GamePlayer gP) {
		data.add(gP);
	}

	/**
	 * Add a GamePlayer.
	 * @param p The player associated with the GamePlayer.
	 * @param kills The kills of the GamePlayer.
	 * @param deaths The deaths of the GamePlayer.
	 * @param points The points of the GamePlayer.
	 * @param level The level of the GamePlayer.
	 * @param c The chat channel of the GamePlayer.
	 */
	public void inputData(Player p, int kills, int deaths, int points, int level, String c) {
		data.add(new GamePlayer(p, kills, deaths, points, level, c));
	}

	/**
	 * Remove a GamePlayer.
	 * @param p The player that should be removed.
	 */
	void removePlayer(Player p) {
		data.remove(getPlayer(p.getUniqueId()));
	}

	/**
	 * Set the default chat channel.
	 * @param channel The new default chat channel.
	 */
	public void setDefaultChannel(String channel) {
		this.defaultChannel = channel;
	}

	/**
	 * Get all GamePlayers.
	 * @return A list containing all GamePlayers.e
	 */
	public ArrayList<GamePlayer> getAllPlayerData() {
		return data;
	}
	
	/**
	 * Get a GamePlayer by uuid.
	 * @param uuid The uuid of the GamePlayer.
	 * @return The GamePlayer.
	 */
	public GamePlayer getPlayer(UUID uuid) {
		for(GamePlayer p : data) {
			if(p.getUUID().equals(uuid)) {
				return p;
			}
		}
		
		return null;
	}

	@EventHandler
	void onPlayerJoin(PlayerJoinEvent e) {
		if (e.getPlayer().hasPlayedBefore()) {
			if(!(DataManager.getInstance().getPlayerData(e.getPlayer(), defaultChannel))) {
				inputData(e.getPlayer(), 0, 0, 0, 0, defaultChannel);
				DataManager.getInstance().createPlayerData(e.getPlayer());
			}
		} else {
			inputData(e.getPlayer(), 0, 0, 0, 0, defaultChannel);
			DataManager.getInstance().createPlayerData(e.getPlayer());
		}

		setFFAScoreboard(e.getPlayer());

		e.getPlayer().teleport(e.getPlayer().getWorld().getSpawnLocation());
		
		e.getPlayer().getInventory().clear();
		e.getPlayer().getActivePotionEffects().clear();
		e.getPlayer().getInventory().setItem(4, ItemStackMaker.start(Material.CLOCK, 1).setName(ChatColor.DARK_GREEN + "Select an arena.").create());
        e.getPlayer().getInventory().setHeldItemSlot(4);
	}

	@EventHandler
	void onPlayerLeave(PlayerQuitEvent e) {
		GamePlayer leave = getPlayer(e.getPlayer().getUniqueId());

		DataManager.getInstance().updateOnLeave(leave);

		if (leave.isPlaying()) {
			ArenaManager.getInstance().getArena(leave.getCurrentArena()).removePlayer(leave);
		}

		removePlayer(e.getPlayer());

		e.getPlayer().getInventory().clear();
		for (PotionEffect pE : e.getPlayer().getActivePotionEffects()) {
			e.getPlayer().removePotionEffect(pE.getType());
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	void onKill(PlayerDeathEvent e) {
		GamePlayer killed = getPlayer(e.getEntity().getUniqueId());
		
		if(e.getEntity().getKiller() != null) {
			GamePlayer killer = getPlayer(e.getEntity().getKiller().getUniqueId());
			
			killer.addKill();
			
			LevelManager.getInstance().levelUp(killer);
			
			killer.getBoard().resetScores("" + ChatColor.GREEN + (killer.getKills() - 1));
			killer.getBoard().getObjective("show").getScore("" + ChatColor.GREEN + killer.getKills()).setScore(10);

			killer.getBoard().resetScores("" + ChatColor.GOLD + (killer.getPoints() - 1));
			killer.getBoard().getObjective("show").getScore("" + ChatColor.GOLD + killer.getPoints()).setScore(4);
			
			if (killed.getPlayer().getLastDamageCause().getCause().equals(DamageCause.ENTITY_ATTACK)) {
				e.setDeathMessage(ChatColor.BLUE + killed.getPlayer().getPlayerListName() + ChatColor.AQUA + " was slain by " + ChatColor.BLUE + killer.getPlayer().getPlayerListName());
			} else if (killed.getPlayer().getLastDamageCause().getCause().equals(DamageCause.PROJECTILE)) {
				e.setDeathMessage(ChatColor.BLUE + killed.getPlayer().getPlayerListName() + ChatColor.AQUA + " was shot by " + ChatColor.BLUE + killer.getPlayer().getPlayerListName());
			} else {
				e.setDeathMessage(ChatColor.BLUE + killed.getPlayer().getPlayerListName() + ChatColor.AQUA + " died.");
			}
		}

		e.setDeathMessage(null);

		e.getDrops().clear();

		getPlayer(killed.getPlayer().getUniqueId()).addDeath();
		
		ArenaManager.getInstance().getArena(killed.getCurrentArena()).getSpawn(killed.getPlayer().getUniqueId()).resetSpawn(); 

		killed.getBoard().resetScores("" + ChatColor.RED + (killed.getDeaths() - 1));
		killed.getBoard().getObjective("show").getScore("" + ChatColor.RED + killed.getDeaths()).setScore(7);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	void onRespawn(PlayerRespawnEvent e) {
		e.getPlayer().getInventory().clear();
		for (PotionEffect pE : e.getPlayer().getActivePotionEffects()) {
			e.getPlayer().removePotionEffect(pE.getType());
		}

		if (getPlayer(e.getPlayer().getUniqueId()).isPlaying()) {
			e.setRespawnLocation(ArenaManager.getInstance().getArena(getPlayer(e.getPlayer().getUniqueId()).getCurrentArena()).getLobbyLocation());
			e.getPlayer().getInventory().setItem(4, ItemStackMaker.start(Material.COMPASS, 1).setName("" + ChatColor.GOLD + ChatColor.BOLD + "|" + ChatColor.DARK_RED + ChatColor.BOLD + " Select your kit! " + ChatColor.GOLD + ChatColor.BOLD + "|").create());
			e.getPlayer().getInventory().setItem(8, ItemStackMaker.start(Material.COAL_BLOCK, 1).setName(ChatColor.RED + "Go back to the lobby.").create());
			e.getPlayer().getInventory().setHeldItemSlot(4);
		} else {
			e.setRespawnLocation(e.getPlayer().getWorld().getSpawnLocation());
		}
	}

	@EventHandler
	void onPlayerChat(AsyncPlayerChatEvent e) {
		GamePlayer sender = getPlayer(e.getPlayer().getUniqueId());
		String senderChannel = sender.getChatChannel();

		for (Player player : e.getRecipients()) {
			if (getPlayer(player.getUniqueId()).getChatChannel().equalsIgnoreCase(senderChannel)) {
				if (senderChannel.equalsIgnoreCase("staff")) {
					e.setCancelled(true);

					String message = "[\"\",{\"text\":\"[Staff]\",\"color\":\"dark_green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/kit channel staff\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"Go to the staff channel\"}]}}},{\"text\":\" " + LevelManager.getInstance().getLevel(sender.getLevel()).getPrefix() + "\",\"color\":\"none\"},{\"text\":\" " + e.getPlayer().getDisplayName() + "\",\"color\":\"none\"},{\"text\":\":\",\"color\":\"gray\"},{\"text\":\" " + e.getMessage() + "\",\"color\":\"gray\"}]";

					IChatBaseComponent msg = ChatSerializer.a(message);
					PacketPlayOutChat packet = new PacketPlayOutChat(msg);

					EntityPlayer eP = ((CraftPlayer) player).getHandle();
					eP.playerConnection.sendPacket(packet);
				} else if (senderChannel.equalsIgnoreCase("broad")) {
					e.setCancelled(true);

					String message = "[\"\",{\"text\":\"[Broad]\",\"color\":\"dark_blue\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/kit channel broad\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"Go to the broad channel\"}]}}},{\"text\":\" " + LevelManager.getInstance().getLevel(sender.getLevel()).getPrefix() + "\",\"color\":\"none\"},{\"text\":\" " + e.getPlayer().getDisplayName() + "\",\"color\":\"none\"},{\"text\":\":\",\"color\":\"gray\"},{\"text\":\" " + e.getMessage() + "\",\"color\":\"gray\"}]";

					IChatBaseComponent msg = ChatSerializer.a(message);
					PacketPlayOutChat packet = new PacketPlayOutChat(msg);

					EntityPlayer eP = ((CraftPlayer) player).getHandle();
					eP.playerConnection.sendPacket(packet);
				} else {
					e.setCancelled(true);
					player.sendMessage(LevelManager.getInstance().getLevel(sender.getLevel()).getPrefix() + " " + ChatColor.RESET + e.getPlayer().getDisplayName() + ChatColor.GRAY + ": " + e.getMessage());
				}
			} else if (senderChannel.equalsIgnoreCase("broad")) {
				if (!getPlayer(player.getUniqueId()).getChatChannel().equalsIgnoreCase("staff")) {
					e.setCancelled(true);
					player.sendMessage("" + ChatColor.DARK_BLUE + ChatColor.BOLD +  "[Broad] " + ChatColor.RESET + LevelManager.getInstance().getLevel(sender.getLevel()).getPrefix() + " " + ChatColor.RESET + e.getPlayer().getDisplayName() + ChatColor.GRAY + ": " + e.getMessage());
				}
			} else if (getPlayer(player.getUniqueId()).getChatChannel().equalsIgnoreCase("broad")) {
				if (!senderChannel.equalsIgnoreCase("staff")) {
					e.setCancelled(true);

					String message = "[\"\",{\"text\":\"[" + senderChannel + "]\",\"color\":\"yellow\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/kit channel " + senderChannel + "\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"Go to channel " + senderChannel + "\"}]}}},{\"text\":\" " + LevelManager.getInstance().getLevel(sender.getLevel()).getPrefix() + "\",\"color\":\"none\"},{\"text\":\" " + e.getPlayer().getDisplayName() + "\",\"color\":\"none\"},{\"text\":\":\",\"color\":\"gray\"},{\"text\":\" " + e.getMessage() + "\",\"color\":\"gray\"}]";

					IChatBaseComponent msg = ChatSerializer.a(message);
					PacketPlayOutChat packet = new PacketPlayOutChat(msg);

					EntityPlayer eP = ((CraftPlayer) player).getHandle();
					eP.playerConnection.sendPacket(packet);
				}
			}
		}
	}

	@EventHandler
	void onInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		GamePlayer player = getPlayer(p.getUniqueId());

		if (player.isPlaying()) {
			if (p.getInventory().getItemInMainHand().getType() == Material.COMPASS) {
				if (p.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals("" + ChatColor.GOLD + ChatColor.BOLD + "|" + ChatColor.DARK_RED + ChatColor.BOLD + " Select your kit! " + ChatColor.GOLD + ChatColor.BOLD + "|")) {
					if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.LEFT_CLICK_BLOCK) {
						player.openInventory(KitManager.getInstance().generateSelectInventory(ArenaManager.getInstance().getArena(player.getCurrentArena()), 0), 0, ArenaManager.getInstance().getArena(player.getCurrentArena()).getName());
					}
				}
			} else if (p.getInventory().getItemInMainHand().getType() == Material.COAL_BLOCK) {
				Arena a = ArenaManager.getInstance().getArena(player.getCurrentArena());
				p.teleport(p.getWorld().getSpawnLocation());
				a.removePlayer(player);
				p.getInventory().clear();
				e.getPlayer().getInventory().setItem(4, ItemStackMaker.start(Material.CLOCK, 1).setName(ChatColor.DARK_GREEN + "Select an arena.").create());
		        e.getPlayer().getInventory().setHeldItemSlot(4);
				for (PotionEffect pE : e.getPlayer().getActivePotionEffects()) {
					e.getPlayer().removePotionEffect(pE.getType());
				}

				if (!player.getChatChannel().equals("staff") && !player.getChatChannel().equals("broad")) {
					player.setChatChannel("lobby");
				}
			}
		} else {
			if(p.getInventory().getItemInMainHand().getType() == Material.CLOCK && p.getInventory().getItemInMainHand().hasItemMeta()) {
				if(p.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals(ChatColor.DARK_GREEN + "Select an arena.")) {
					e.setCancelled(true);
					if(e.getAction() != Action.PHYSICAL) {
						p.closeInventory();
						player.openInventory(ArenaManager.getInstance().generateSelectorInventory(), 0, "");
					}
				}
			}
		}
	}
	
	/**
	 * Prevent the dropping of items.
	 * @param e The event object.
	 */
	@EventHandler
	public void onDrop(PlayerDropItemEvent e) {
		e.setCancelled(true);
	}
	
	/**
	 * Function that generates a scoreboard for a player.
	 * @param p The player that needs a scoreboard.
	 */
	private void setFFAScoreboard(Player p) {
		Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
		Objective show = board.registerNewObjective("show", "dummy", "show");
		show.setDisplaySlot(DisplaySlot.SIDEBAR);
		show.setDisplayName("§d§l§k|§4§lKit-PvP§d§l§k|");

		Score top = show.getScore(" ");
		top.setScore(15);

		Score welcome = show.getScore("" + ChatColor.WHITE + ChatColor.BOLD + "Hello there,");
		welcome.setScore(14);

		Score player = show.getScore("" + ChatColor.GOLD + ChatColor.BOLD + p.getPlayerListName() + ChatColor.BLUE + ChatColor.BOLD + "!");
		player.setScore(13);

		Score empty = show.getScore("  ");
		empty.setScore(12);

		Score yourKills = show.getScore("" + ChatColor.WHITE + ChatColor.BOLD + "Your kills: ");
		yourKills.setScore(11);

		Score kills = show.getScore("" + ChatColor.GREEN + PlayerManager.getInstance().getPlayer(p.getUniqueId()).getKills());
		kills.setScore(10);

		Score empty1 = show.getScore("   ");
		empty1.setScore(9);

		Score yourDeaths = show.getScore("" + ChatColor.WHITE + ChatColor.BOLD + "Your deaths:");
		yourDeaths.setScore(8); 

		Score deaths = show.getScore("" + ChatColor.RED + PlayerManager.getInstance().getPlayer(p.getUniqueId()).getDeaths());
		deaths.setScore(7);

		Score empty2 = show.getScore("    ");
		empty2.setScore(6);

		Score yourPoints = show.getScore("" + ChatColor.WHITE + ChatColor.BOLD + "Your points: ");
		yourPoints.setScore(5);

		Score points = show.getScore("" + ChatColor.GOLD + PlayerManager.getInstance().getPlayer(p.getUniqueId()).getPoints());
		points.setScore(4);

		Score empty3 = show.getScore("     ");
		empty3.setScore(3);
		
		Score yourLevel = show.getScore("" + ChatColor.WHITE + ChatColor.BOLD + "Your level: ");
		yourLevel.setScore(2);
		
		Score level = show.getScore("" + ChatColor.AQUA + PlayerManager.getInstance().getPlayer(p.getUniqueId()).getLevel());
		level.setScore(1);
		
		Score empty4 = show.getScore("            ");
		empty4.setScore(0);

		p.setScoreboard(board);
		getPlayer(p.getUniqueId()).setBoard(board);
	}
}