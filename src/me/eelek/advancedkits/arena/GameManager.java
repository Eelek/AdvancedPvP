package me.eelek.advancedkits.arena;

public class GameManager {
	
	public enum GameType {
		FFA_UNLOCKED, FFA_RANK, DUEL
	}
	
	public static GameType getType(String type) {
		if(type.equalsIgnoreCase("duel")) {
			return GameType.DUEL;
		} else if(type.equalsIgnoreCase("ffa_unlocked")) {
			return GameType.FFA_UNLOCKED;
		} else if(type.equalsIgnoreCase("ffa_rank")) {
			return GameType.FFA_RANK;
		}
		
		return null;
	}

}
