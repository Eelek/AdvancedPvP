package me.eelek.advancedpvp.players;

import org.bukkit.ChatColor;

public class Level {
	
	private int level;
	private int minimunKills;
	private String prefix;
	
	/**
	 * Level object
	 * @param level The level.
	 * @param minimunKills The minimum amount of kills required for this level.
	 * @param prefix The prefix associated with this level.
	 */
	public Level(int level, int minimunKills, String prefix) {
		this.level = level;
		this.minimunKills = minimunKills;
		this.prefix = prefix;
	}
	
	/**
	 * Get the level.
	 * @return The level.
	 */
	public int getLevel() {
		return level;
	}
	
	/**
	 * Get the minimum amount of kills required for this level. 
	 * @return The minimum amount of kills required for this level.
	 */
	public int getMinimunKills() {
		return minimunKills;
	}
	
	/**
	 * Get the prefix associated with this level.
	 * @return The prefix associated with this level.
	 */
	public String getPrefix() {
		return ChatColor.translateAlternateColorCodes('&', prefix);
	}

}