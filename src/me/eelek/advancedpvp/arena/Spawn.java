package me.eelek.advancedpvp.arena;

import org.bukkit.Location;

public class Spawn {
	
	private int id;
	private Location loc;
	private int count;
	private int index;
	private String player;
	
	/**
	 * Spawn object.
	 * @param id The ID of the Spawn.
	 * @param loc The location of the Spawn.
	 */
	public Spawn(int id, Location loc) {
		this.id = id;
		this.loc = loc;
		this.count = 0;
		this.index = 0;
		this.player = "";
	}
	
	/**
	 * Spawn object.
	 * @param id The ID of the Spawn.
	 * @param loc The location of the Spawn.
	 * @param count The maximum amount of spawns allowed on the Spawn.
	 */
	public Spawn(int id, Location loc, int count) {
		this.id = id;
		this.loc = loc;
		this.count = count;
		this.index = 0;
		this.player = "";
	}
	
	/**
	 * Get the ID of the Spawn.
	 * @return The ID of the Spawn.
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * Get the location of the Spawn.
	 * @return The location of the Spawn.
	 */
	public Location getLocation() {
		return loc;
	}
	
	/**
	 * Get the maximum amount of spawns allowed on the Spawn.
	 * @return The maximum amount of spawns allowed on the Spawn.
	 */
	public int getCount() {
		return count;
	}

	/**
	 * Set the maximum amount of spawns allowed on the Spawn.
	 * @param count The new maximum amount of spawns allowed on the Spawn.
	 */
	public void setCount(int count) {
		this.count = count;
	}
	
	/**
	 * Get the number of spawns which happened on this Spawn.
	 * @return The number of spawns which happened on this Spawn.
	 */
	public int getIndex() {
		return index;
	}
	
	/**
	 * Get the player that last spawned on this Spawn.
	 * @return The player that last spawned on this Spawn.
	 */
	public String getPlayer() {
		return player;
	}
	
	/**
	 * Set the Spawn's ID.
	 * @param id The new ID of the Spawn.
	 */
	public void setId(int id){
		this.id = id;
	}
	
	/**
	 * Set the location of the Spawn.
	 * @param loc The new location of the Spawn.
	 */
	public void setLocation(Location loc) {
		this.loc = loc;
	}
	
	/**
	 * Add a number to the amount of spawns done.
	 * @param i The number to add to the amount of spawns done.
	 */
	public void addToIndex(int i) {
		this.index += i;
	}
	
	/**
	 * Function to reset the number of spawns done.
	 */
	public void resetIndex() {
		this.index = 0;
	}
	
	/**
	 * Set the player that last spawned on the Spawn.
	 * @param player The player that last spawned on the Spawn.
	 */
	public void setPlayer(String player) {
		this.player = player;
	}
	
	/**
	 * Function that resets the last player that spawned on the Spawn.
	 */
	public void resetPlayer() {
		this.player = "";
	}
	
	/**
	 * Function that resets the Spawn's data.
	 */
	public void resetSpawn() {
		this.index = 0;
		this.player = "";
	}
}
