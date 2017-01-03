package me.eelek.advancedkits.arena;

import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

public class Arena {
	
	World world;
	ArrayList<Location> spawns = new ArrayList<Location>();
	HashMap<Location, Integer> spawnCount = new HashMap<Location, Integer>();
	HashMap<Location, Integer> spawnIndex = new HashMap<Location, Integer>();
	ArrayList<String> currentPlayers = new ArrayList<String>();
	int maxPlayers;
	int level;
	String name;
	
	boolean active;
	
	Sign sign;
	
	public Arena(String name, World world, int maxPlayers, int level, ArrayList<Location> spawns, HashMap<Location, Integer> spawnCount, HashMap<Location, Integer> spawnIndex) {
		this.name = name;
		this.world = world;
		this.maxPlayers = maxPlayers;
		this.level = level;
		this.spawns = spawns;
		this.spawnCount = spawnCount;
		this.spawnIndex = spawnIndex;
		
		this.active = false;
		
		this.sign = null;
	}
	
	public Arena(String name, World world, int maxPlayers, int level) {
		this.name = name;
		this.world = world;
		this.level = level;
		this.maxPlayers = maxPlayers;
		
		this.active = false;

		this.sign = null;
	}
	
	public int getAmountOfSpawns() {
		return spawns.size();
	}
	
	public Location getSpawnLocation() {
		for(Location loc : spawns) {
			if(spawnCount.get(loc) != 0) {
				if(spawnIndex.get(loc) < spawnCount.get(loc)) {
					spawnIndex.put(loc, spawnIndex.get(loc) + 1);
					return loc;
				}
			} else {
				return loc;
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
		if(b) {
			getSign().setLine(0, "§6§l[§4§lArena§6§l]");
			getSign().setLine(1, getName());
			getSign().setLine(2, "§7§l" + getCurrentPlayers().size() + "§0§l/§8§l" + getMaxPlayers());
			getSign().setLine(3, "§5§lLevel: §a§l" + getLevel());
			getSign().update();
		} else {
			getSign().setLine(0, "§6§l[§4§lArena§6§l]");
			getSign().setLine(1, getName());
			getSign().setLine(2, "§4§lClosed.");
			getSign().setLine(3, "§5§lLevel: §a§l" + getLevel());
			getSign().update();
		}
	}
	
	public void setLevel(int le) {
		this.level = le;
	}
	
	public int getMaxPlayers() {
		return maxPlayers;
	}
	
	public int getLevel() {
		return level;
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
			getSign().setLine(0, "§6§l[§4§lArena§6§l]");
			getSign().setLine(1, getName());
			getSign().setLine(2, "§7§l" + getCurrentPlayers().size() + "§0§l/§8§l" + getMaxPlayers());
			getSign().setLine(3, "§5§lLevel: §a§l" + getLevel());
			getSign().update();
		} else {
			currentPlayers.add(p.getPlayerListName());
		}
	}
	
	public void removePlayer(Player p) {
		if(isActive()) {
			currentPlayers.remove(p.getPlayerListName());
			getSign().setLine(0, "§6§l[§4§lArena§6§l]");
			getSign().setLine(1, getName());
			getSign().setLine(2, "§7§l" + getCurrentPlayers().size() + "§0§l/§8§l" + getMaxPlayers());
			getSign().setLine(3, "§5§lLevel: §a§l" + getLevel());
			getSign().update();
		} else {
			currentPlayers.remove(p.getPlayerListName());
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

}
