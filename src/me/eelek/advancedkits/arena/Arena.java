package me.eelek.advancedkits.arena;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import me.eelek.advancedkits.arena.GameManager.GameType;

public class Arena {
	
	World world;
	ArrayList<Location> spawns = new ArrayList<Location>();
	HashMap<Location, Integer> spawnCount = new HashMap<Location, Integer>();
	HashMap<Location, Integer> spawnIndex = new HashMap<Location, Integer>();
	HashMap<String, Location> spawnPlayer = new HashMap<String, Location>();
	ArrayList<String> currentPlayers = new ArrayList<String>();
	int maxPlayers;
	int minLevel;
	String kitSet;
	String name;
	GameType type;
	Location lobby;
	
	boolean active;
	
	Sign sign;
	
	public Arena(String name, World world, int maxPlayers, int minLevel, ArrayList<Location> spawns, HashMap<Location, Integer> spawnCount, HashMap<Location, Integer> spawnIndex, GameType type, String kitSet, Location lobby) {
		this.name = name;
		this.world = world;
		this.maxPlayers = maxPlayers;
		this.minLevel = minLevel;
		this.spawns = spawns;
		this.spawnCount = spawnCount;
		this.spawnIndex = spawnIndex;
		this.type = type;
		this.kitSet = kitSet;
		this.lobby = lobby;
		
		this.active = false;
		
		this.sign = null;
	}
	
	public Arena(String name, World world, int maxPlayers, int minLevel, ArrayList<Location> spawns, HashMap<Location, Integer> spawnCount, HashMap<Location, Integer> spawnIndex, GameType type, String kitSet) {
		this.name = name;
		this.world = world;
		this.maxPlayers = maxPlayers;
		this.minLevel = minLevel;
		this.spawns = spawns;
		this.spawnCount = spawnCount;
		this.spawnIndex = spawnIndex;
		this.type = type;
		this.kitSet = kitSet;
		this.lobby = null;
		
		this.active = false;
		
		this.sign = null;
	}
	
	public Arena(String name, World world, int maxPlayers, int minLevel) {
		this.name = name;
		this.world = world;
		this.minLevel = minLevel;
		this.maxPlayers = maxPlayers;
		
		this.active = false;

		this.sign = null;
	}
	
	public int getAmountOfSpawns() {
		return spawns.size();
	}
	
	public Location getSpawnLocation(String p) {
		if(type == GameType.DUEL) {
			for(Location loc : spawns) {
				if(spawnCount.get(loc) != 0) {
					if(spawnIndex.get(loc) < spawnCount.get(loc)) {
						spawnIndex.put(loc, spawnIndex.get(loc) + 1);
						spawnPlayer.put(p, loc);
						return loc;
					}
				}
			}
		} else if(type == GameType.FFA_RANK || type == GameType.FFA_UNLOCKED) {
			Random r = new Random();
			int random = r.nextInt(spawns.size());
			
			if(spawnIndex.get(getSpawn(random)) == 0) {
				for(Location l : spawnIndex.keySet()) {
					spawnIndex.put(l, 0);
				}
				spawnIndex.put(getSpawn(random), 1);
				return getSpawn(random);
			} else {
				return getSpawnLocation(p);
			}
		}
		return null;
	}
	
	public String getName() {
		return name;
	}
	
	public void setMaxPlayers(int max) {
		this.maxPlayers = max;
	}
	
	public void setActive(boolean b) {
		this.active = b;
		if(getType() != GameType.DUEL) {
			if(b) {
				getSign().setLine(0, "§6§l[§4§l" + getType().toString().substring(0, 5) + "§6§l]");
				getSign().setLine(1, getName());
				getSign().setLine(2, "§7§l" + getCurrentPlayers().size() + "§0§l/§8§l" + getMaxPlayers());
				getSign().setLine(3, "§5§lLevel: §a§l" + getMinimumLevel());
				getSign().update();
			} else {
				getSign().setLine(0, "§6§l[§4§l" + getType().toString().substring(0, 5) + "§6§l]");
				getSign().setLine(1, getName());
				getSign().setLine(2, "§4§lClosed.");
				getSign().setLine(3, "§5§lLevel: §a§l" + getMinimumLevel());
				getSign().update();
			}
		} else {
			if(b) {
				getSign().setLine(0, "§6§l[§4§l" + getType().toString().substring(0, 4) + "§6§l]");
				getSign().setLine(1, getName());
				getSign().setLine(2, "§b§lA DUEL?!?");
				getSign().setLine(3, "§5§lLevel: §a§l" + getMinimumLevel());
				getSign().update();
			} else {
				getSign().setLine(0, "§6§l[§4§l" + getType().toString().substring(0, 4) + "§6§l]");
				getSign().setLine(1, getName());
				getSign().setLine(2, "§4§lClosed.");
				getSign().setLine(3, "§5§lLevel: §a§l" + getMinimumLevel());
				getSign().update();
			}
		}
	}
	
	public void setMinimumLevel(int le) {
		this.minLevel = le;
	}
	
	public int getMaxPlayers() {
		return maxPlayers;
	}
	
	public int getMinimumLevel() {
		return minLevel;
	}
	
	public boolean isActive() {
		return active;
	}
	
	public World getWorld() {
		return world;
	}
	
	public ArrayList<Location> getSpawnLocations() {
		return spawns;
	}
	
	public void addPlayer(Player p) {
		if(isActive()) {
			currentPlayers.add(p.getPlayerListName());
			if(getType() != GameType.DUEL) {
				getSign().setLine(0, "§6§l[§4§l" + getType().toString().substring(0, 5) + "§6§l]");
				getSign().setLine(1, getName());
				getSign().setLine(2, "§7§l" + getCurrentPlayers().size() + "§0§l/§8§l" + getMaxPlayers());
				getSign().setLine(3, "§5§lLevel: §a§l" + getMinimumLevel());
				getSign().update();
			} else {
				getSign().setLine(0, "§6§l[§4§l" + getType().toString().substring(0, 4) + "§6§l]");
				getSign().setLine(1, getName());
				getSign().setLine(2, "§b§lA DUEL?!?");
				getSign().setLine(3, "§5§lLevel: §a§l" + getMinimumLevel());
				getSign().update();
			}
		} else {
			currentPlayers.add(p.getPlayerListName());
		}
	}
	
	public void removePlayer(Player p) {
		if(isActive()) {
			currentPlayers.remove(p.getPlayerListName());
			if(getType() == GameType.DUEL) {
				spawnIndex.put(spawnPlayer.get(p), 0);
				spawnPlayer.remove(p);
			}
			if(getType() != GameType.DUEL) {
				getSign().setLine(0, "§6§l[§4§l" + getType().toString().substring(0, 5) + "§6§l]");
				getSign().setLine(1, getName());
				getSign().setLine(2, "§7§l" + getCurrentPlayers().size() + "§0§l/§8§l" + getMaxPlayers());
				getSign().setLine(3, "§5§lLevel: §a§l" + getMinimumLevel());
				getSign().update();
			} else {
				getSign().setLine(0, "§6§l[§4§l" + getType().toString().substring(0, 4) + "§6§l]");
				getSign().setLine(1, getName());
				getSign().setLine(2, "§b§lA DUEL?!?");
				getSign().setLine(3, "§5§lLevel: §a§l" + getMinimumLevel());
				getSign().update();
			}
		} else {
			currentPlayers.remove(p.getPlayerListName());
			if(getType() == GameType.DUEL) {
				spawnIndex.put(spawnPlayer.get(p), 0);
				spawnPlayer.remove(p);
			}
		}
				
	}
	
	public void removeSpawnPlayer(String p) {
		if(getType() == GameType.DUEL) {
			spawnIndex.put(spawnPlayer.get(p), 0);
			spawnPlayer.remove(p);
		}
	}
	
	public ArrayList<String> getCurrentPlayers() {
		return currentPlayers;
	}
	
	public void addSpawn(Location loc) {
		spawns.add(new Location(loc.getWorld(), loc.getX(), loc.getY() + 1, loc.getZ()));
	}
	
	public void setSign(Sign sign) {
		this.sign = sign;
	}
	
	public Sign getSign() {
		return sign;
	}
	
	public int getSpawnCount(Location spawn) {
		if(!spawnCount.isEmpty()) {
			return spawnCount.get(spawn);
		} else {
			return 0;
		}
	}
	
	public Location getSpawn(int c) {
		return spawns.get(c);
	}

	public int getSpawnIndex(int c) {
		if(spawnIndex.get(getSpawn(c)) != null) {
			return spawnIndex.get(getSpawn(c));
		} else {
			return 0;
		}
	}
	
	public GameType getType() {
		return type;
	}
	
	public void setType(GameType t) {
		type = t;
	}
	
	public String getKitSetName() {
		return kitSet;
	}
	
	public Location getLobbyLocation() { 
		return lobby;
	}
	
	public void setLobbyLocation(Location loc) {
		lobby = loc;
	}
	
	public boolean hasLobby() {
		if(lobby != null) {
			return true;
		} else {
			return false;
		}
	}
}
