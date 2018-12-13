package me.eelek.advancedpvp.players;

import java.util.ArrayList;

import org.bukkit.ChatColor;

public class Levels {
	
	ArrayList<Level> levels = new ArrayList<Level>();
	
	private static Levels instance;
	
	//Singleton
	protected Levels() {
		
	}
	
	//Singleton
	public static Levels getInstance() {
		if(instance == null) {
			instance = new Levels();
		}
		
		return instance;
	}

	Level getLevel(int level) {
		for(Level l : levels) {
			if(l.getLevel() == level) {
				return l;
			}
		}
		
		return null;
	}
	
	public void addLevel(int level, int minimun, String prefix) {
		levels.add(new Level(level, minimun, prefix));
	}
	
	void removeLevel(int level) {
		levels.remove(getLevel(level));
	}
	
	void levelUp(GamePlayer p) {
		if(p.getKills() >= getLevel(p.getLevel() + 1).getMinimunKills()) {
			p.levelUp();
			p.getPlayer().sendMessage(ChatColor.BLUE + "You have ranked up!");
			p.getPlayer().setDisplayName(ChatColor.RESET + p.getPlayer().getPlayerListName());
			p.getBoard().resetScores("" + ChatColor.AQUA + (p.getLevel() - 1));
			p.getBoard().getObjective("show").getScore("" + ChatColor.AQUA + p.getLevel()).setScore(1);
		}
	}

}