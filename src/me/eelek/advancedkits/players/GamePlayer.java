package me.eelek.advancedkits.players;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

public class GamePlayer {
	
	private Player p;
	
	private int kills;
	private int deaths;
	private int points;
	private int level;
	
	private boolean playing;
	
	private String arena;
	
	private String chatChannel;
	
	private Scoreboard sB;
	
	public GamePlayer(Player player) {
		this.p = player;
	}
	
	public GamePlayer(Player player, int kills, int deaths, int points, int level, String defaultChannel) {
		this.p = player;
		this.kills = kills;
		this.deaths = deaths;
		this.points = points;
		this.level = level;
		
		this.playing = false;
		
		this.arena = "";
		this.chatChannel = defaultChannel;
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
		if(deaths == 0 && kills == 0) {
			return 0;
		} else {
			double calc = kills / deaths;
			return calc;
		}
	}
	
	public int getLevel() {
		return level;
	}
	
	public void levelUp() {
		this.level = level + 1;
	}
	
	public boolean isPlaying() {
		return playing;
	}
	
	public void setPlaying(boolean set) {
		playing = set;
	}
	
	public String getCurrentArena() {
		return arena;
	}
	
	public void setCurrentArena(String a) {
		this.arena = a;
	}
	
	public String getChatChannel() {
		return chatChannel;
	}
	
	public void setChatChannel(String c) {
		this.chatChannel = c;
	}
	
	public Scoreboard getBoard() {
		return this.sB;
	}
	
	public void setBoard(Scoreboard b) {
		this.sB = b;
	}

}
