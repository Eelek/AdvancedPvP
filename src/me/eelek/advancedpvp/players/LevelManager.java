package me.eelek.advancedpvp.players;

import java.util.ArrayList;

import org.bukkit.ChatColor;

public class LevelManager {
	
	ArrayList<Level> levels = new ArrayList<Level>();
	
	private static LevelManager instance;
	
	//Singleton
	protected LevelManager() {
		
	}
	
	//Singleton
	public static LevelManager getInstance() {
		if(instance == null) {
			instance = new LevelManager();
		}
		
		return instance;
	}

	/**
	 * Get a level <i>object</i> by the level <i>integer</i>.
	 * @param level The level <i>integer</i>.
	 * @return The level <i>object</i>.
	 */
	Level getLevel(int level) {
		for(Level l : levels) {
			if(l.getLevel() == level) {
				return l;
			}
		}
		
		return null;
	}
	
	/**
	 * Add a level.
	 * @param level The level to be added.
	 * @param minimun The minimum amount of kills required for the level.
	 * @param prefix The prefix associated with the level.
	 */
	public void addLevel(int level, int minimun, String prefix) {
		levels.add(new Level(level, minimun, prefix));
	}
	
	/**
	 * Remove a level.
	 * @param level The level to be removed.
	 */
	void removeLevel(int level) {
		levels.remove(getLevel(level));
	}
	
	/**
	 * Function that checks if a GamePlayer should level up.
	 * @param p The GamePlayer that might be leveled up.
	 */
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
