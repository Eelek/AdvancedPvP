package me.eelek.advancedpvp.players;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.scoreboard.Scoreboard;

import me.eelek.advancedpvp.arena.ArenaManager;
import me.eelek.advancedpvp.game.GameManager.GameType;
import me.eelek.advancedpvp.kits.Kit;
import me.eelek.advancedpvp.kits.KitManager;

public class GamePlayer {
	
	private Player p;
	private UUID uuid;
	
	private int kills;
	private int deaths;
	private int points;
	private int level;
	
	private boolean playing;
	
	private String arena;
	
	private String chatChannel;
	
	private Scoreboard sB;
	
	private Kit selected;
	
	private InventoryView openInventory;
	private int openedPage;
	private String inventoryArg;
	
	/**
	 * GamePlayer object
	 * @param player The player associated with the GamePlayer.
	 */
	public GamePlayer(Player player) {
		this.p = player;
		this.uuid = player.getUniqueId();
	}
	
	/**
	 * GamePlayer object.
	 * @param player The player associated with the GamePlayer.
	 * @param kills The amount of kills of the GamePlayer.
	 * @param deaths The amount of deaths of the GamePlayer.
	 * @param points The amount of points of the GamePlayer.
	 * @param level The level of the GamePlayer.
	 * @param defaultChannel The chat channel in which the GamePlayer should be placed.
	 */
	public GamePlayer(Player player, int kills, int deaths, int points, int level, String defaultChannel) {
		this.p = player;
		this.uuid = player.getUniqueId();
		this.kills = kills;
		this.deaths = deaths;
		this.points = points;
		this.level = level;
		
		this.playing = false;
		
		this.arena = "";
		this.chatChannel = defaultChannel;
		
		this.openInventory = null;
		this.openedPage = 0;
		this.inventoryArg = "";
	}
	
	/**
	 * Get the player associated with the GamePlayer.
	 * @return The player object of the player associated with the GamePlayer.
	 */
	public Player getPlayer() {
		return p;
	}
	
	/**
	 * Get the UUID of the GamePlayer.
	 * @return The UUID of the GamePlayer.
	 */
	public UUID getUUID() {
		return uuid;
	}
	
	/**
	 * Get the amount of kills of the GamePlayer.
	 * @return The amount of kills of the GamePlayer.
	 */
	public int getKills() {
		return kills;
	}
	
	/**
	 * Add a kill to the amount of kills of the GamePlayer.
	 */
	public void addKill() {
		this.kills += 1;
		this.points += 1;
	}
	
	/**
	 * Get the amount of deaths of the GamePlayer.
	 * @return
	 */
	public int getDeaths() {
		return deaths;
	}
	
	/**
	 * Add a death to the amount of deaths of the GamePlayer.
	 */
	public void addDeath() {
		this.deaths += 1;
	}
	
	/**
	 * Get the amount of points of the GamePlayer.
	 * @return The amount of points of the GamePlayer.
	 */
	public int getPoints() {
		return points;
	}
	
	/**
	 * Set the amount of points of the GamePlayer.
	 * @param newPoints The new amount of points of the GamePlayer.
	 */
	public void setPoints(int newPoints) {
		this.points = newPoints;
	}
	
	/**
	 * Function that calculates the GamePlayer's kill/death ratio.
	 * @return The GamePlayer's kill/death ratio.
	 */
	public double calculateKDR() {
		return (deaths == 0) ? kills : kills / deaths;
	}
	
	/**
	 * Get the level of the GamePlayer.
	 * @return The level of the GamePlayer.
	 */
	public int getLevel() {
		return level;
	}
	
	/**
	 * Level the GamePlayer Up.
	 */
	public void levelUp() {
		this.level = level + 1;
	}
	
	/**
	 * Get if the GamePlayer is currently playing in an Arena.
	 * @return If the GamePlayer is currently playing in an Arena.
	 */
	public boolean isPlaying() {
		return playing;
	}
	
	/**
	 * Set if the GamePlayer is currently playing in an Arena.
	 * @param set If the GamePlayer is currently playing in an Arena. 
	 */
	public void setPlaying(boolean set) {
		playing = set;
	}
	
	/**
	 * Get the name of the Arena in which the GamePlayer is playing.
	 * @return The name of the Arena in which the GamePlayer is playing.
	 */
	public String getCurrentArena() {
		return arena;
	}
	
	/**
	 * Set the name of the Arena in which the GamePlayer is playing.
	 * @param a The name of the new Arena in which the GamePlayer is playing.
	 */
	public void setCurrentArena(String a) {
		this.arena = a;
	}
	
	/**
	 * Get the chat channel that the GamePlayer is in. 
	 * @return The chat channel that the GamePlayer is in.
	 */
	public String getChatChannel() {
		return chatChannel;
	}
	
	/**
	 * Set the chat channel that the GamePlayer is in. 
	 * @param c The new chat channel that the GamePlayer is in. 
	 */
	public void setChatChannel(String c) {
		this.chatChannel = c;
	}
	
	/**
	 * Get the scoreboard of the GamePlayer.
	 * @return The scoreboard of the GamePlayer.
	 */
	public Scoreboard getBoard() {
		return this.sB;
	}
	
	/**
	 * Set the scoreboard of the GamePlayer.
	 * @param b The new scoreboard of the GamePlayer.
	 */
	public void setBoard(Scoreboard b) {
		this.sB = b;
	}
	
	/**
	 * Set the selected Kit of the GamePlayer.
	 * @param k The new selected Kit of the GamePlayer.
	 */
	public void setSelectedKit(Kit k) {
		this.selected = k;
	}
	
	/**
	 * Get the selected Kit of the GamePlayer.
	 * @return The selected Kit of the GamePlayer.
	 */
	public Kit getSelectedKit() {
		return selected;
	}
	
	/**
	 * Make the GamePlayer open an inventory.
	 * @param inv The inventory that should be opened.
	 * @param page The page that should be displayed.
	 * @param arg The argument required to open the inventory.
	 */
	public void openInventory(Inventory inv, int page, String arg) {
		this.openInventory = this.p.openInventory(inv);
		this.openedPage = page;
		this.inventoryArg = arg;
	}
	
	/**
	 * Get the opened inventory.
	 * @return The opened inventory.
	 */
	public Inventory getOpenInventory() {
		return this.openInventory.getTopInventory();
	}
	
	/**
	 * Get the opened page.
	 * @return The opened page.
	 */
	public int getOpenPage() {
		return this.openedPage;
	}
	
	/**
	 * Get the inventory argument.
	 * @return The inventory argument.
	 */
	public String getInventoryArgument() {
		return this.inventoryArg;
	}
	
	/**
	 * Function that opens the currently open inventory at the specified page.
	 * @param page The currently open inventory at the specified page.
	 */
	public void openPage(int page) {
		if(this.openInventory.getTitle().startsWith("[Arenas]")) {
			if(this.inventoryArg.equalsIgnoreCase("type")) {
				openInventory(ArenaManager.getInstance().generateArenasInventory(page, true), page, "type");
			} else {
				openInventory(ArenaManager.getInstance().generateArenasInventory(page, false), page, "");
			}
		} else if(this.openInventory.getTitle().startsWith("[Spawns]")) {
			openInventory(ArenaManager.getInstance().getArena(this.inventoryArg).generateSpawnsInventory(page), page, this.inventoryArg);
		} else if(this.openInventory.getTitle().startsWith("[Players]")) {
			openInventory(ArenaManager.getInstance().getArena(this.inventoryArg).generatePlayersInventory(page), page, this.inventoryArg);
	    } else if(this.openInventory.getTitle().startsWith("[Kits]")) {
			openInventory(KitManager.getInstance().generateSelectInventory(ArenaManager.getInstance().getArena(this.inventoryArg), page), page, this.inventoryArg);
		} else if(this.openInventory.getTitle().startsWith("[Select] Arena")) {
			openInventory(ArenaManager.getInstance().generateSelectorInventory(page, GameType.valueOf(this.inventoryArg.toUpperCase())), page, this.inventoryArg);
		}
	}
}