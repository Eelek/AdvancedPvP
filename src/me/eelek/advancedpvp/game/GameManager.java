package me.eelek.advancedpvp.game;

public class GameManager {
	
	public enum GameType {
		FFA_UNLOCKED, FFA_RANK, DUEL, TDM_RANK
	}
	
	/*
	public enum GameState {
		JOINING, STARTING, PLAYING, END
	}
	
	public enum Team {
		ALPHA, BETA
	}
	*/
	
	private static GameManager instance;
	
	protected GameManager() {
		
	}
	
	public static GameManager getInstance() {
		if(instance == null) {
			instance = new GameManager();
		}
		
		return instance;
	}
	
	//ArrayList<Game> games = new ArrayList<Game>();
	
	public GameType getType(String type) {
		if(type.equalsIgnoreCase("duel")) {
			return GameType.DUEL;
		} else if(type.equalsIgnoreCase("ffa_unlocked")) {
			return GameType.FFA_UNLOCKED;
		} else if(type.equalsIgnoreCase("ffa_rank")) {
			return GameType.FFA_RANK;
		} else if(type.equalsIgnoreCase("tdm_rank")) {
			return GameType.TDM_RANK;
		}
		
		return null;
	}
	
	/*
	public Team getTeam(String t) {
		if(t.equalsIgnoreCase("1") || t.equalsIgnoreCase("alpha")) {
			return Team.ALPHA;
		} else if(t.equalsIgnoreCase("2") || t.equalsIgnoreCase("beta")) {
			return Team.BETA;
		} else {
			return null;
		}
	}
	
	public void startGame(Arena a) {
		for(Game g : games) {
			if(g.getArena().getName().equals(a.getName())) {
				g.start();
			}
		}
	}
	
	public Game getGame(Arena a) {
		for(Game g : games) {
			if(g.getArena().getName().equals(a.getName())) {
				return g;
			}
		}
		
		return null;
	}
	
	public void addGame(Game g) {
		games.add(g);
	}
	
	public void removeGame(Game g) {
		games.remove(g);
	}
	*/
}