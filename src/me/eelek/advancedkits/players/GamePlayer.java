package me.eelek.advancedkits.players;

import org.bukkit.entity.Player;

public class GamePlayer {
	
	private Player p;
	
	private int kills;
	private int deaths;
	private int points;
	private int level;
	
	public GamePlayer(Player player) {
		this.p = player;
	}
	
	public GamePlayer(Player player, int kills, int deaths, int points, int level) {
		this.p = player;
		this.kills = kills;
		this.deaths = deaths;
		this.points = points;
		this.level = level;
	}
	
	public Player getPlayer() {
		return p;
	}
	
	public int getKills() {
		return kills;
	}
	
	public void addKill() {
		this.kills += 1;
		this.points += 1;
	}
	
	public int getDeaths() {
		return deaths;
	}
	
	public void addDeath() {
		this.deaths += 1;
	}
	
	public int getPoints() {
		return points;
	}
	
	public void setPoints(int newPoints) {
		this.points = newPoints;
	}
	
	public double calculateKDR() {
		return kills / deaths;
	}
	
	public int getLevel() {
		return level;
	}
	
	public void levelUp() {
		this.level = level + 1;
	}

}
