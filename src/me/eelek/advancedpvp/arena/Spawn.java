package me.eelek.advancedpvp.arena;

import org.bukkit.Location;

public class Spawn {
	
	private int id;
	private Location loc;
	private int count;
	private int index;
	private String player;
	
	public Spawn(int id, Location loc) {
		this.id = id;
		this.loc = loc;
		this.count = 0;
		this.index = 0;
		this.player = "";
	}
	
	public Spawn(int id, Location loc, int count) {
		this.id = id;
		this.loc = loc;
		this.count = count;
		this.index = 0;
		this.player = "";
	}
	
	public int getId() {
		return id;
	}
	
	public Location getLocation() {
		return loc;
	}
	
	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	
	public int getIndex() {
		return index;
	}
	
	public String getPlayer() {
		return player;
	}
	
	public void setId(int id){
		this.id = id;
	}
	
	public void setLocation(Location loc) {
		this.loc = loc;
	}
	
	public void addToIndex(int i) {
		this.index += i;
	}
	
	public void resetIndex() {
		this.index = 0;
	}
	
	public void setPlayer(String player) {
		this.player = player;
	}
	
	public void resetPlayer() {
		this.player = "";
	}
	
	public void resetSpawn() {
		this.index = 0;
		this.player = "";
	}
}
