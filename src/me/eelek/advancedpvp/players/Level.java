package me.eelek.advancedpvp.players;

import org.bukkit.ChatColor;

public class Level {
	
	private int level;
	private int minimunKills;
	private String prefix;
	
	public Level(int level, int minimunKills, String prefix) {
		this.level = level;
		this.minimunKills = minimunKills;
		this.prefix = prefix;
	}
	
	public int getLevel() {
		return level;
	}
	
	public int getMinimunKills() {
		return minimunKills;
	}
	
	public String getPrefix() {
		return ChatColor.translateAlternateColorCodes('&', prefix);
	}

}
