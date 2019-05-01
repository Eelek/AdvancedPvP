package me.eelek.advancedpvp.arena;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import me.eelek.advancedpvp.game.GameManager.GameType;
import me.eelek.advancedpvp.kits.Kit;
import me.eelek.advancedpvp.players.GamePlayer;

public class Arena {
	
	private World world;
	private ArrayList<Spawn> spawns;
	private ArrayList<UUID> currentPlayers;
	private int maxPlayers;
	private int minLevel;
	private ArrayList<Kit> kitSet;
	private String name;
	private GameType type;
	private Location lobby;
	private boolean created = false;
	private boolean setup = false;
	private Material displayItem;
	
	boolean active;
	
	/**
	 * Arena object
	 * @param name The name of the Arena
	 * @param world The world in which the Arena is up
	 * @param maxPlayers The maximun amount of players allowed in the Arena
	 * @param minLevel The minimum level required to join the Arena
	 * @param spawns A list of all the spawns in the Arena
	 * @param type The GameType of the Arena
	 * @param kitSet A list of kits which can be used in the Arena
	 * @param lobby The lobby location of the Arena
	 * @param displayItem The material of the display item of the Arena
	 */
	public Arena(String name, World world, int maxPlayers, int minLevel, ArrayList<Spawn> spawns, GameType type, ArrayList<Kit> kitSet, Location lobby, Material displayItem, boolean active) {
		this.name = name;
		this.world = world;
		this.maxPlayers = maxPlayers;
		this.minLevel = minLevel;
		this.spawns = spawns;
		this.type = type;
		this.kitSet = kitSet;
		this.lobby = lobby;
		this.displayItem = displayItem;
		this.active = active;
		
		this.currentPlayers = new ArrayList<UUID>();
		
		checkSetup();
	}
	
	/**
	 * Arena object
	 * @param name The name of the Arena
	 * @param world The world in which the Arena is
	 * @param maxPlayers The maximum amount of players allowed in the Arena
	 * @param minLevel The minimum level required to join the Arena
	 * @param spawns A list of all the spawns in the Arena
	 * @param type The GameType of the Arena
	 * @param kitSet A list of kits which can be used in the Arena
	 * @param displayItem The material of display item of the Arena
	 */
	public Arena(String name, World world, int maxPlayers, int minLevel, ArrayList<Spawn> spawns, GameType type, ArrayList<Kit> kitSet, Material displayItem) {
		this.name = name;
		this.world = world;
		this.maxPlayers = maxPlayers;
		this.minLevel = minLevel;
		this.spawns = spawns;
		this.type = type;
		this.kitSet = kitSet;
		this.lobby = null;
		this.displayItem = displayItem;
		
		this.active = false;
		
		this.currentPlayers = new ArrayList<UUID>();
		
		checkSetup();
	}
	
	/**
	 * Arena object
	 * @param name The name of the Arena
	 * @param world The world in which the Arena is
	 * @param maxPlayers The maximum amount of players allowed in the Arena
	 * @param minLevel The minimum level required to join the Arena
	 */
	public Arena(String name, World world, int maxPlayers, int minLevel) {
		this.name = name;
		this.world = world;
		this.minLevel = minLevel;
		this.maxPlayers = maxPlayers;
		this.displayItem = Material.SPONGE;
		this.name = name;
		this.world = world;
		this.maxPlayers = maxPlayers;
		this.minLevel = minLevel;
		this.spawns = new ArrayList<Spawn>();
		this.currentPlayers = new ArrayList<UUID>();
		this.type = null;
		this.kitSet = new ArrayList<Kit>();
		this.lobby = null;
		
		this.active = false;
		this.created = true;
		
		checkSetup();
	}
	
	/**
	 * Get the amount of spawns in the Arena
	 * @return The amount of spawns in the Arena
	 */
	public int getAmountOfSpawns() {
		return spawns.size();
	}
	
	/**
	 * Generate a spawn location for a player
	 * @param p The player who needs a spawn
	 * @return A spawn location
	 */
	public Location getSpawnLocation(String p) {
		if(type == GameType.DUEL) {
			for(Spawn s : spawns) {
				if(s.getCount() != 0) {
					if(s.getIndex() < s.getCount()) {
						s.addToIndex(1);
						s.setPlayer(p);
						return s.getLocation();
					}
				}
			}
		} else if(type == GameType.FFA_RANK || type == GameType.FFA_UNLOCKED) {
			Random r = new Random();
			int random = r.nextInt(spawns.size());
			
			if(getSpawn(random).getIndex() == 0) {
				for(Spawn s : spawns) {
					s.resetIndex();
				}
				getSpawn(random).addToIndex(1);
				getSpawn(random).setPlayer(p);
				return getSpawn(random).getLocation();
			} else {
				return getSpawnLocation(p);
			}
		}
		return null;
	}
	
	/**
	 * Get a spawn by its ID.
	 * @param id The ID.
	 * @return
	 */
	public Spawn getSpawn(int id) {
		for(Spawn s: spawns) {
			if(s.getId() == id) {
				return s;
			}
		}
		
		return null;
	}
	
	/**
	 * Get a spawn based on the player that used it.
	 * @param player The player.
	 * @return
	 */
	public Spawn getSpawn(String player) {
		for(Spawn s : spawns) {
			if(s.getPlayer().equals(player)) {
				return s;
			}
		}
		
		return null;
	}
	
	/**
	 * Get the name of the Arena
	 * @return The name of the Arena
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Set the maximum amount of players allowed in the Arena.
	 * @param max The new maximum amount of players allowed in the Arena.
	 */
	public void setMaxPlayers(int max) {
		this.maxPlayers = max;
	}
	
	/**
	 * Change the 'active' state of the Arena.
	 * @param b The new 'active' state.
	 */
	public void setActive(boolean b) {
		this.active = b;
		checkSetup();
	}
	
	/**
	 * Set the minimum level required to join the Arena.
	 * @param le The new minimum level required to join the Arena.
	 */
	public void setMinimumLevel(int le) {
		this.minLevel = le;
	}
	
	/**
	 * Get the maximum amount of players allowed in the Arena.
	 * @return The maximum amount of players allowed in the Arena.
	 */
	public int getMaxPlayers() {
		return maxPlayers;
	}
	
	/**
	 * Get the minimum level required to join the Arena.
	 * @return The minimum level required to join the Arena.
	 */
	public int getMinimumLevel() {
		return minLevel;
	}
	
	/**
	 * Get the Arena's 'active' state.
	 * @return The Arena's 'active' state.
	 */
	public boolean isActive() {
		return active;
	}
	
	/**
	 * Get the world the Arena is in.
	 * @return The world the Arena is in.
	 */
	public World getWorld() {
		return world;
	}
	
	/**
	 * Get the list of spawns in the Arena.
	 * @return The list of spawns in the Arena.
	 */
	public ArrayList<Spawn> getSpawns() {
		return spawns;
	}
	
	/**
	 * Add a player to the Arena.
	 * @param p The player to be added.
	 */
	public void addPlayer(GamePlayer p) {
		currentPlayers.add(p.getPlayer().getUniqueId());
		p.setPlaying(true);
		p.setCurrentArena(this.name);
		p.getPlayer().teleport(this.lobby);
		if(!(p.getChatChannel().equalsIgnoreCase("staff"))) p.setChatChannel(this.name);
		
	}
	
	/**
	 * Remove a player from the Arena.
	 * @param p The player to be removed.
	 */
	public void removePlayer(GamePlayer p) {
		currentPlayers.remove(p.getPlayer().getUniqueId());
		p.setPlaying(false);
		p.setCurrentArena(null);
	}
	
	/**
	 * Remove a spawn from the Arena.
	 * @param s The spawn to be removed.
	 */
	public void removeSpawn(Spawn s) {
		spawns.remove(s);
	}
	
	/**
	 * Get the current players in the Arena.
	 * @return The current players in the Arena.
	 */
	public ArrayList<UUID> getCurrentPlayers() {
		return currentPlayers;
	}
	
	/**
	 * Add a spawn to the Arena.
	 * @param loc The location of the new spawn.
	 */
	public void addSpawn(Location loc) {
		spawns.add(new Spawn(spawns.size(), loc, 0));
		checkSetup();
	}
	
	/**
	 * Get the Arena's type.
	 * @return The Arena's type.
	 */
	public GameType getType() {
		return type;
	}
	
	/**
	 * Set the Arena's type.
	 * @param t The new GameType.
	 */
	public void setType(GameType t) {
		type = t;
		checkSetup();
	}
	
	/**
	 * Get the list of kits used in the Arena.
	 * @return The list of kits used in the Arena.
	 */
	public ArrayList<Kit> getKitSet() {
		return kitSet;
	}
	
	/**
	 * Get the lobby location of the Arena.
	 * @return The lobby location of the Arena.
	 */
	public Location getLobbyLocation() { 
		return lobby;
	}
	
	/**
	 * Set the lobby location of the Arena.
	 * @param loc The new lobby location of the Arena.
	 */
	public void setLobbyLocation(Location loc) {
		lobby = loc;
		checkSetup();
	}
	
	/**
	 * Get if the serevr has a lobby.
	 * @return If the server has a lobby.
	 */
	public boolean hasLobby() {
		return lobby != null;
	}
	
	/*
	public int getSpawnTeam(Location loc) {
		if(spawnTeam.get(loc) == Team.ALPHA) {
		    return 1;	
		} else if(spawnTeam.get(loc) == Team.BETA) {
			return 2;
		} else {
			return 0;
		}
	}
	*/
	
	/**
	 * Check if the Arena has ben created.
	 * @return If the Arena has been created.
	 */
	public boolean isCreated() {
		return created;
	}
	
	/**
	 * Get the Material of the display item of the Arena.
	 * @return The Material of the display item of The Arena.
	 */
	public Material getDisplayItem() {
		return displayItem;
	}
	
	/**
	 * Set the Material of the display item of the Arena.
	 * @param m The new Material of the display item of the Arena.
	 */
	public void setDisplayItem(Material m) {
		this.displayItem = m;
		checkSetup();
	}

	/**
	 * Get if the Arena has been setup.
	 * @return If the Arena has been setup.
	 */
	public boolean isSetup() {
		return setup;
	}
	
	/**
	 * A private method checking if the Arena has been setup.
	 */
	private void checkSetup() {
		this.setup = (!spawns.isEmpty() && lobby != null && type != null);
	}
	
	/**
	 * Function that generates the admin inspect inventory of the Arena.
	 * @return The admin inspect inventory of the Arena.
	 */
    public Inventory generateInventory() {
		Inventory inv = Bukkit.getServer().createInventory(null, 27, "[Arena] " + this.name);
		
		ItemStack type = new ItemStack(Material.MAP, 1);
		ItemMeta tMeta = (ItemMeta) type.getItemMeta();
		tMeta.setDisplayName(ChatColor.DARK_PURPLE + "Game type: " + ChatColor.LIGHT_PURPLE + this.type.toString() + ChatColor.DARK_PURPLE + ".");
		type.setItemMeta(tMeta);
		inv.setItem(9, type);

		ItemStack world = new ItemStack(Material.GRASS, 1);
		ItemMeta wMeta = (ItemMeta) world.getItemMeta();
		wMeta.setDisplayName(ChatColor.DARK_PURPLE + "World: " + ChatColor.LIGHT_PURPLE + this.world.getName() + ChatColor.DARK_PURPLE + ".");
		wMeta.setLore(Arrays.asList("§r§fClick me for spawn info!"));
		world.setItemMeta(wMeta);
		inv.setItem(10, world);

		ItemStack current = new ItemStack(Material.TOTEM_OF_UNDYING, 1);
		ItemMeta cMeta = (ItemMeta) current.getItemMeta();
		cMeta.setDisplayName(ChatColor.DARK_PURPLE + "Current players: " + ChatColor.LIGHT_PURPLE + this.currentPlayers.size() + ChatColor.DARK_PURPLE + ".");
		cMeta.setLore(Arrays.asList("§r§fClick me to see the current players in the arena!"));
		current.setItemMeta(cMeta);
		inv.setItem(11, current);
		
		ItemStack active = new ItemStack(this.active ? Material.LIME_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE, 1);
		ItemMeta aMeta = (ItemMeta) active.getItemMeta();
		aMeta.setDisplayName(ChatColor.DARK_PURPLE + "Arena " + ChatColor.LIGHT_PURPLE + this.name + ChatColor.DARK_PURPLE + " is " + (this.active ? ChatColor.GREEN + "active" : ChatColor.RED + "disabled") + ChatColor.DARK_PURPLE + ".");
		active.setItemMeta(aMeta);
		inv.setItem(13, active);

		ItemStack max = new ItemStack(Material.BARRIER, 1);
		ItemMeta mMeta = (ItemMeta) max.getItemMeta();
		mMeta.setDisplayName(ChatColor.DARK_PURPLE + "Maximum players: " + ChatColor.LIGHT_PURPLE + this.maxPlayers + ChatColor.DARK_PURPLE + ".");
		max.setItemMeta(mMeta);
		inv.setItem(15, max);

		ItemStack level = new ItemStack(Material.EXPERIENCE_BOTTLE, 1);
		ItemMeta lMeta = (ItemMeta) level.getItemMeta();
		lMeta.setDisplayName(ChatColor.DARK_PURPLE + "Minimum level: " + ChatColor.LIGHT_PURPLE + this.minLevel + ChatColor.DARK_PURPLE + ".");
		level.setItemMeta(lMeta);
		inv.setItem(16, level);

		ItemStack displayItem = new ItemStack(this.displayItem, 1);
		ItemMeta dMeta = (ItemMeta) displayItem.getItemMeta();
		dMeta.setDisplayName(ChatColor.DARK_PURPLE + "This is the display item of this arena.");
		displayItem.setItemMeta(dMeta);
		inv.setItem(17, displayItem);

		return inv;
	}
    
    /**
     * Function that generates the admin inspect spawn inventory.
     * @param page The page on which the inventory should open.
     * @return The admin inspect spawn inventory.
     */
    public Inventory generateSpawnsInventory(int page) {
		int pageSize = 54;
		
		Inventory inv = Bukkit.getServer().createInventory(null, pageSize, "[Spawns] " + this.name + " Spawns, page " + (page + 1));
		
		for(int s = page * (pageSize - 9 * 2); s < this.spawns.size(); s++) {
			Spawn spawn = this.spawns.get(s);
			ItemStack sItem = new ItemStack(Material.DIRT, 1);
			ItemMeta sMeta = (ItemMeta) sItem.getItemMeta();
			sMeta.setDisplayName(ChatColor.GREEN + "Spawn " + (s + 1));
			sMeta.setLore(Arrays.asList("§r§fX: " + spawn.getLocation().getBlockX(), "§r§fY: " + spawn.getLocation().getBlockY(), "§r§fZ: " + spawn.getLocation().getBlockZ(), "§r§fMax spawns: " + spawn.getCount(), "§r§fSpawn index: " + spawn.getIndex(), "§r§fLast spawn:", "§r§8" + spawn.getPlayer()));
			sItem.setItemMeta(sMeta);
			inv.addItem(sItem);
		}
		
		if(page > 0) {
			ItemStack previous = new ItemStack(Material.REDSTONE_TORCH, 1);
			ItemMeta pMeta = (ItemMeta) previous.getItemMeta();
			pMeta.setDisplayName(ChatColor.BLUE + "Previous page.");
			previous.setItemMeta(pMeta);
			inv.setItem(inv.getSize() - 9, previous);
		}
		
		if((page + 1) * (pageSize - 9 * 2) < this.spawns.size()) {
			ItemStack next = new ItemStack(Material.FEATHER, 1);
			ItemMeta nMeta = (ItemMeta) next.getItemMeta();
			nMeta.setDisplayName(ChatColor.BLUE + "Next page.");
			next.setItemMeta(nMeta);
			inv.setItem(inv.getSize() - 1, next);
		}
		
		ItemStack add = new ItemStack(Material.BRICK, 1);
		ItemMeta aMeta = (ItemMeta) add.getItemMeta();
		aMeta.setDisplayName(ChatColor.BLUE + "Click me to add a spawn.");
		add.setItemMeta(aMeta);
		inv.setItem(inv.getSize() - 8, add);
		
		ItemStack lobby = new ItemStack(Material.GOLD_BLOCK, 1);
		ItemMeta lMeta = (ItemMeta) lobby.getItemMeta();
		lMeta.setDisplayName(ChatColor.GOLD + "Lobby");
		lMeta.setLore(Arrays.asList("§r§fX: " + this.lobby.getBlockX(), "§r§fY: " + this.lobby.getBlockY(), "§r§fZ: " + this.lobby.getBlockZ()));
		lobby.setItemMeta(lMeta);
		inv.setItem(inv.getSize() - 5, lobby);
		
		ItemStack back = new ItemStack(Material.BOOK, 1);
		ItemMeta bMeta = (ItemMeta) back.getItemMeta();
		bMeta.setDisplayName(ChatColor.BLUE + "Go back to the arena menu.");
		back.setItemMeta(bMeta);
		inv.setItem(inv.getSize() - 2 , back);
		
		return inv;
	}
    
    /**
     * Function that generates the admin inspect players inventory.
     * @param page The page on which the inventory should open.
     * @return The admin inspect players inventory.
     */
    public Inventory generatePlayersInventory(int page) {
		int pageSize = 54;
		
		Inventory inv = Bukkit.getServer().createInventory(null, pageSize, "[Players] " + this.name + " Players, page " + (page + 1));
		
		for(int p = page * (pageSize - 9 * 2); p < this.currentPlayers.size(); p++) {
			Player player = Bukkit.getServer().getPlayer(this.currentPlayers.get(p));
			ItemStack pItem = new ItemStack(Material.PLAYER_HEAD, 1);
			SkullMeta pMeta = (SkullMeta) pItem.getItemMeta();
			pMeta.setDisplayName("§b" + player.getPlayerListName());
			pMeta.setLore(Arrays.asList(ChatColor.DARK_GREEN + "Click to tp."));
			pMeta.setOwningPlayer(player.getPlayer());
			pItem.setItemMeta(pMeta);
			inv.addItem(pItem);
		}
		
		if(page > 0) {
			ItemStack previous = new ItemStack(Material.REDSTONE_TORCH, 1);
			ItemMeta pMeta = (ItemMeta) previous.getItemMeta();
			pMeta.setDisplayName(ChatColor.BLUE + "Previous page.");
			previous.setItemMeta(pMeta);
			inv.setItem(inv.getSize() - 9, previous);
		}
		
		if((page + 1) * (pageSize - 9 * 2) < this.currentPlayers.size()) {
			ItemStack next = new ItemStack(Material.FEATHER, 1);
			ItemMeta nMeta = (ItemMeta) next.getItemMeta();
			nMeta.setDisplayName(ChatColor.BLUE + "Next page.");
			next.setItemMeta(nMeta);
			inv.setItem(inv.getSize() - 1, next);
		}
		
		ItemStack back = new ItemStack(Material.BOOK, 1);
		ItemMeta bMeta = (ItemMeta) back.getItemMeta();
		bMeta.setDisplayName(ChatColor.BLUE + "Go back to the arena menu.");
		back.setItemMeta(bMeta);
		inv.setItem(inv.getSize() - 2 , back);
		
		return inv;
	}
}
