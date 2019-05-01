package me.eelek.advancedpvp.arena;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import me.eelek.advancedpvp.game.GameManager.GameType;
import me.eelek.advancedpvp.kits.Kit;

public class Arena {
	
	private World world;
	private ArrayList<Spawn> spawns = new ArrayList<Spawn>();
	private ArrayList<String> currentPlayers = new ArrayList<String>();
	private int maxPlayers;
	private int minLevel;
	private ArrayList<Kit> kitSet;
	private String name;
	private GameType type;
	private Location lobby;
	private boolean created = false;
	private int id;
	
	boolean active;
	
	Sign sign;
	
	public Arena(int id, String name, World world, int maxPlayers, int minLevel, ArrayList<Spawn> spawns, GameType type, ArrayList<Kit> kitSet, Location lobby) {
		this.name = name;
		this.world = world;
		this.maxPlayers = maxPlayers;
		this.minLevel = minLevel;
		this.spawns = spawns;
		this.type = type;
		this.kitSet = kitSet;
		this.lobby = lobby;
		
		this.active = false;
		
		this.sign = null;
	}
	
	public Arena(int id, String name, World world, int maxPlayers, int minLevel, ArrayList<Spawn> spawns, GameType type, ArrayList<Kit> kitSet) {
		this.name = name;
		this.world = world;
		this.maxPlayers = maxPlayers;
		this.minLevel = minLevel;
		this.spawns = spawns;
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
		this.created = true;

		this.sign = null;
	}
	
	public int getAmountOfSpawns() {
		return spawns.size();
	}
	
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
	
	public Spawn getSpawn(int id) {
		for(Spawn s: spawns) {
			if(s.getId() == id) {
				return s;
			}
		}
		
		return null;
	}
	
	public Spawn getSpawn(String player) {
		for(Spawn s : spawns) {
			if(s.getPlayer().equals(player)) {
				return s;
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
	
	public ArrayList<Spawn> getSpawns() {
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
				getSpawn(p.getPlayerListName()).resetSpawn();
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
				getSpawn(p.getPlayerListName()).resetSpawn();
			}
		}
				
	}
	
	public void removeSpawn(Spawn s) {
		spawns.remove(s);
	}
	
	public ArrayList<String> getCurrentPlayers() {
		return currentPlayers;
	}
	
	public void addSpawn(Location loc) {
		spawns.add(new Spawn(spawns.size(), loc, 0));
	}
	
	public void setSign(Sign sign) {
		this.sign = sign;
	}
	
	public Sign getSign() {
		return sign;
	}
	
	public GameType getType() {
		return type;
	}
	
	public void setType(GameType t) {
		type = t;
	}
	
	public ArrayList<Kit> getKitSet() {
		return kitSet;
	}
	
	public Location getLobbyLocation() { 
		return lobby;
	}
	
	public void setLobbyLocation(Location loc) {
		lobby = loc;
	}
	
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
	
	public boolean isCreated() {
		return created;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int newId) {
		this.id = newId;
	}
	
	public boolean hasId() {
		try {
			return id > 0;
		} catch (NullPointerException e) {
			return false;
		}
	}
}
