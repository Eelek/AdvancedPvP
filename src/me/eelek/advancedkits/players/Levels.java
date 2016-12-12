package me.eelek.advancedkits.players;

import java.util.ArrayList;

import org.bukkit.ChatColor;

public class Levels {
	
	static ArrayList<Level> levels = new ArrayList<Level>();
	
	public static Level getLevel(int level) {
		for(Level l : levels) {
			if(l.getLevel() == level) {
				return l;
			}
		}
		
		return null;
	}
	
	public static void addLevel(int level, int minimun, String prefix) {
		levels.add(new Level(level, minimun, prefix));
	}
	
	public static void removeLevel(int level) {
		levels.remove(getLevel(level));
	}
	
	public static void levelUp(GamePlayer p) {
		if(p.getKills() >= getLevel(p.getLevel() + 1).getMinimunKills()) {
			p.levelUp();
			p.getPlayer().sendMessage(ChatColor.BLUE + "You have ranked up!");
		}
	}

}
