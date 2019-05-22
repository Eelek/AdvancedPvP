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
import org.bukkit.inventory.meta.SkullMeta;

import me.eelek.advancedpvp.ItemStackMaker;
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
	public Location getSpawnLocation(Player p) {
		if(type == GameType.DUEL) {
			for(Spawn s : spawns) {
				if(s.getCount() != 0) {
					if(s.getIndex() < s.getCount()) {
						s.addToIndex(1);
						s.setPlayer(p.getUniqueId());
						return s.getLocation();
					}
				}
			}
		} else if(type == GameType.FFA_RANK || type == GameType.FFA_UNLOCKED) {
			Random r = new Random();
			int random = r.nextInt(spawns.size());
			Spawn s = getSpawn(random);
			
			if(s.getIndex() == 0) {
				s.addToIndex(1);
				s.setPlayer(p.getUniqueId());
				return s.getLocation();
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
	public Spawn getSpawn(UUID player) {
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
		
		inv.setItem(9, ItemStackMaker.start(Material.MAP, 1)
									 .setName(ChatColor.DARK_PURPLE + "Game type: " + ChatColor.LIGHT_PURPLE + this.type.toString() + ChatColor.DARK_PURPLE + ".")
									 .create());

		inv.setItem(10, ItemStackMaker.start(Material.GRASS, 1)
									  .setName(ChatColor.DARK_PURPLE + "World: " + ChatColor.LIGHT_PURPLE + this.world.getName() + ChatColor.DARK_PURPLE + ".")
									  .setLore(Arrays.asList("§r§fClick me for spawn info!"))
									  .create());

		inv.setItem(11, ItemStackMaker.start(Material.TOTEM_OF_UNDYING, 1)
									  .setName(ChatColor.DARK_PURPLE + "Current players: " + ChatColor.LIGHT_PURPLE + this.currentPlayers.size() + ChatColor.DARK_PURPLE + ".")
									  .setLore(Arrays.asList("§r§fClick me to see the current players in the arena!"))
									  .create());

		inv.setItem(13, ItemStackMaker.start(this.active ? Material.LIME_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE, 1)
									  .setName(ChatColor.DARK_PURPLE + "Arena " + ChatColor.LIGHT_PURPLE + this.name + ChatColor.DARK_PURPLE + " is " + (this.active ? ChatColor.GREEN + "active" : ChatColor.RED + "disabled") + ChatColor.DARK_PURPLE + ".")
									  .create());

		inv.setItem(15, ItemStackMaker.start(Material.BARRIER, 1)
									  .setName(ChatColor.DARK_PURPLE + "Maximum players: " + ChatColor.LIGHT_PURPLE + this.maxPlayers + ChatColor.DARK_PURPLE + ".")
									  .create());

		inv.setItem(16, ItemStackMaker.start(Material.EXPERIENCE_BOTTLE, 1)
									  .setName(ChatColor.DARK_PURPLE + "Minimum level: " + ChatColor.LIGHT_PURPLE + this.minLevel + ChatColor.DARK_PURPLE + ".")
									  .create());

		inv.setItem(17, ItemStackMaker.start(this.displayItem, 1)
									  .setName(ChatColor.DARK_PURPLE + "This is the display item of this arena.")
									  .create());

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
			inv.addItem(ItemStackMaker.start(Material.DIRT, 1)
									  .setName(ChatColor.GREEN + "Spawn " + (s + 1))
									  .setLore(Arrays.asList("§r§fX: " + spawn.getLocation().getBlockX(), 
											   				 "§r§fY: " + spawn.getLocation().getBlockY(), 
											   				 "§r§fZ: " + spawn.getLocation().getBlockZ(), 
											   				 "§r§fMax spawns: " + spawn.getCount(), 
											   				 "§r§fSpawn index: " + spawn.getIndex(), 
											   				 "§r§fLast spawn:", 
											   				 "§r§8" + spawn.getPlayer()))
									  .create());
		}
		
		if(page > 0) {
			inv.setItem(inv.getSize() - 9, ItemStackMaker.start(Material.REDSTONE_TORCH, 1)
														 .setName(ChatColor.BLUE + "Previous page.")
														 .create());
		}
		
		if((page + 1) * (pageSize - 9 * 2) < this.spawns.size()) {
			inv.setItem(inv.getSize() - 1, ItemStackMaker.start(Material.FEATHER, 1)
														 .setName(ChatColor.BLUE + "Next page.")
														 .create());
		}
		
		inv.setItem(inv.getSize() - 8, ItemStackMaker.start(Material.BRICK, 1)
													 .setName(ChatColor.BLUE + "Click me to add a spawn.")
													 .create());
		
		inv.setItem(inv.getSize() - 5, ItemStackMaker.start(Material.GOLD_BLOCK, 1)
													 .setName(ChatColor.GOLD + "Lobby")
													 .setLore(Arrays.asList("§r§fX: " + this.lobby.getBlockX(), 
															 				"§r§fY: " + this.lobby.getBlockY(), 
															 				"§r§fZ: " + this.lobby.getBlockZ()))
													 .create());
		
		inv.setItem(inv.getSize() - 2 , ItemStackMaker.start(Material.BOOK, 1)
													  .setName(ChatColor.BLUE + "Go back to the arena menu.")
													  .create());
		
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
		}
		
		if(page > 0) {
			inv.setItem(inv.getSize() - 9, ItemStackMaker.start(Material.REDSTONE_TORCH, 1)
					 .setName(ChatColor.BLUE + "Previous page.")
					 .create());
		}
		
		if((page + 1) * (pageSize - 9 * 2) < this.currentPlayers.size()) {
			inv.setItem(inv.getSize() - 1, ItemStackMaker.start(Material.FEATHER, 1)
					 .setName(ChatColor.BLUE + "Next page.")
					 .create());
		}
		
		inv.setItem(inv.getSize() - 2 , ItemStackMaker.start(Material.BOOK, 1)
				  .setName(ChatColor.BLUE + "Go back to the arena menu.")
				  .create());
		
		return inv;
	}
}
