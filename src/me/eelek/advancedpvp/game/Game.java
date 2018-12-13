package me.eelek.advancedpvp.game;

@Deprecated
public class Game {
	
	/*
	Arena a;
	GameState state;
	int timeleft;
	HashMap<Team, ArrayList<GamePlayer>> teams;
	int i;
	HashMap<String, Integer> playerKills;
	HashMap<String, Integer> playerDeaths;
	HashMap<Team, Integer> teamKills; //Total kills the team has made, not team killed by another teammate
	HashMap<Team, Integer> teamDeaths;
	
	public Game(Arena a) {
		this.a = a;
		state = GameState.JOINING;
		timeleft = 60 * 3 * 20; //3 minutes in ticks
		teams = new HashMap<Team, ArrayList<GamePlayer>>();
		playerKills = new HashMap<String, Integer>();
		playerDeaths = new HashMap<String, Integer>();
		teamKills = new HashMap<Team, Integer>();
		teamDeaths = new HashMap<Team, Integer>();
	}
	
	public Arena getArena() {
		return a;
	}
	
	public GameState getState() {
		return state;
	}
	
	public void setState(GameState state) {
		this.state = state;
	}
	
	public int getTimeLeft() {
		return timeleft;
	}
	
	public HashMap<Team, ArrayList<GamePlayer>> getTeams() {
		return teams;
	}
	
	void start() {
		state = GameState.STARTING;
		i = 10;
		
		//game init
		BukkitRunnable runnable = new BukkitRunnable() {
			
			public void run() {
				if(i == 0) {
					cancel();
				}
				
				for(String player : a.getCurrentPlayers()) {
					PlayerManager.getInstance().getPlayer(player).getPlayer().sendMessage(ChatColor.GOLD + "Teleporting in " + ChatColor.RED + i + ChatColor.GOLD + " seconds.");
				}
				
				i--;
			}
			
		};
		
		runnable.runTaskTimer(AKitsMain.getPlugin(AKitsMain.class), 0L, 20L);
		
		
		sort();
		
		for(Entry<Team, ArrayList<GamePlayer>> e : teams.entrySet()) {
			for(GamePlayer p : e.getValue()) {
				a.getSpawnLocation(p.getPlayer().getPlayerListName(), e.getKey());
			    KitManager.getInstance().giveKit(p.getPlayer(), p.getSelectedKit());
			    p.getPlayer().setWalkSpeed(0F);
			    playerKills.put(p.getPlayer().getPlayerListName(), 0);
			    playerDeaths.put(p.getPlayer().getPlayerListName(), 0);
			    
			}
		}
		
		
		//countdown
		i = 5;
		
		BukkitRunnable gameStart = new BukkitRunnable() {
			
			public void run() {
				if(i == 0) {
					cancel();
				}
				
				for(String player : a.getCurrentPlayers()) {
					PlayerManager.getInstance().getPlayer(player).getPlayer().sendMessage(ChatColor.GOLD + "Starting in " + ChatColor.RED + i + ChatColor.GOLD + " seconds.");
				}
				
				i--;
			}
			
		};
		
		gameStart.runTaskTimer(AKitsMain.getPlugin(AKitsMain.class), 0L, 20L);
		
		for(Entry<Team, ArrayList<GamePlayer>> e : teams.entrySet()) {
			for(GamePlayer p : e.getValue()) {
			    p.getPlayer().setWalkSpeed(0.2F);
			}
		}
		
		state = GameState.PLAYING;
		
		BukkitRunnable game = new BukkitRunnable() {
			
			public void run() {
				if(timeleft == 0) {
					cancel();
				}
				
				if(timeleft == 120 || timeleft == 60 || timeleft == 30 || timeleft == 15 || timeleft == 10 || timeleft <= 5) {
					if(timeleft > 60) {
						int minutes = Integer.valueOf(timeleft / 60);
						int seconds = timeleft % 60;
						broadcast(ChatColor.BLUE + "Time left: " + ChatColor.AQUA + minutes + ChatColor.BLUE + " minute(s) and " + ChatColor.AQUA + seconds + ChatColor.BLUE + " seconds.");
					} else {
						broadcast(ChatColor.BLUE + "Time left: " + timeleft + ChatColor.BLUE + " seconds.");
					}
				}
				
				timeleft--;
				
			}
			
		};
		
		game.runTaskTimer(AKitsMain.getPlugin(AKitsMain.class), 0L, 20L);
		
		String winning = teamKills.get(Team.ALPHA) > teamKills.get(Team.BETA) ? ChatColor.GOLD + "Alpha" : ChatColor.RED + "Beta";
		
		for(Entry<Team, ArrayList<GamePlayer>> e : teams.entrySet()) {
			for(GamePlayer p : e.getValue()) {
				p.getPlayer().getInventory().clear();
				p.getPlayer().getActivePotionEffects().clear();
				p.getPlayer().teleport(a.getLobbyLocation());
				p.getPlayer().setWalkSpeed(0.0F);
				p.getPlayer().sendMessage(ChatColor.DARK_PURPLE + "Team " + winning + ChatColor.DARK_PURPLE + " has won!");
			}
		}
		
		BukkitRunnable end = new BukkitRunnable() {
			
			public void run() {
				for(Entry<Team, ArrayList<GamePlayer>> e : teams.entrySet()) {
					for(GamePlayer p : e.getValue()) {
						p.getPlayer().teleport(p.getPlayer().getWorld().getSpawnLocation());
						a.removePlayer(p.getPlayer());
					}
				}
			}
		};
		
		end.runTaskLater(AKitsMain.getPlugin(AKitsMain.class), 5 * 20L);
		
		//Game reset
		state = GameState.END;
		teams = new HashMap<Team, ArrayList<GamePlayer>>();
		playerKills = new HashMap<String, Integer>();
		playerDeaths = new HashMap<String, Integer>();
		teamKills = new HashMap<Team, Integer>();
		teamDeaths = new HashMap<Team, Integer>();
		
		state = GameState.JOINING;
	}
	
	void sort() {
		boolean even = a.getCurrentPlayers().size() % 2 == 0;
		
		ArrayList<GamePlayer> rated = new ArrayList<GamePlayer>();
		
		//Level sort
		for(String player : a.getCurrentPlayers()) {
			GamePlayer p = PlayerManager.getInstance().getPlayer(player);
			if(!rated.isEmpty()) {
				for(int r = 0; r < rated.size(); r++) {
				    if(p.getLevel() > rated.get(r).getLevel()) {
					    rated.add(r, p);
				    }	
				}	
			} else {
				rated.add(0, p);
			}
		}
		
		//Team sort
		if(even) {
			int team1Score = 0;
            int team2Score = 0;
            int swap = 0;
            
            for(int r = 0; r < rated.size(); r++) {
                if(swap < 2) {
                    if(r % 2 == 0) {
                        team1Score = team1Score + rated.get(r).getLevel();
                        if(teams.get(Team.ALPHA) != null) {
                        	ArrayList<GamePlayer> team = teams.get(Team.ALPHA);
                            team.add(rated.get(r));
                            teams.put(Team.ALPHA, team);	
                        } else {
                        	ArrayList<GamePlayer> team = new ArrayList<GamePlayer>();
                            team.add(rated.get(r));
                            teams.put(Team.ALPHA, team);
                        }
                        swap++;
                    } else {
                    	team2Score = team2Score + rated.get(r).getLevel();
                    	if(teams.get(Team.BETA) != null) {
                        	ArrayList<GamePlayer> team = teams.get(Team.BETA);
                            team.add(rated.get(r));
                            teams.put(Team.BETA, team);	
                        } else {
                        	ArrayList<GamePlayer> team = new ArrayList<GamePlayer>();
                            team.add(rated.get(r));
                            teams.put(Team.BETA, team);
                        }
                        swap++;
                    }
                } else {
                    if(r % 2 == 0) {
                    	team2Score = team2Score + rated.get(r).getLevel();
                    	if(teams.get(Team.BETA) != null) {
                        	ArrayList<GamePlayer> team = teams.get(Team.BETA);
                            team.add(rated.get(r));
                            teams.put(Team.BETA, team);	
                        } else {
                        	ArrayList<GamePlayer> team = new ArrayList<GamePlayer>();
                            team.add(rated.get(r));
                            teams.put(Team.BETA, team);
                        }
                        swap++;
                        if(swap == 4) {
                            swap = 0;
                        }
                    } else {
                    	team1Score = team1Score + rated.get(r).getLevel();
                    	if(teams.get(Team.ALPHA) != null) {
                        	ArrayList<GamePlayer> team = teams.get(Team.ALPHA);
                            team.add(rated.get(r));
                            teams.put(Team.ALPHA, team);	
                        } else {
                        	ArrayList<GamePlayer> team = new ArrayList<GamePlayer>();
                            team.add(rated.get(r));
                            teams.put(Team.ALPHA, team);
                        }
                        swap++;
                        if(swap == 4) {
                            swap = 0;
                        }
                    }
                }
            }
		} else {
			int team1Score = 0;
            int team2Score = 0;
            int swap = 0;
            int swap2 = 0;
            
            for(int r = 0; r < rated.size(); r++) {
                if(swap < 2) {
                    if(r % 2 == 0) {
                        team2Score = team2Score + rated.get(r).getLevel();
                        ArrayList<GamePlayer> team = teams.get(Team.BETA);
                        team.add(rated.get(r));
                        teams.put(Team.BETA, team);
			            swap++;
                    } else {
			            team1Score = team1Score + rated.get(r).getLevel();
                        ArrayList<GamePlayer> team = teams.get(Team.ALPHA);
                        team.add(rated.get(r));
                        teams.put(Team.ALPHA, team);
                        swap++;
                    }
                } else {
                    if(swap2 < 2) {
                        if(r % 2 == 0) {
                        	team2Score = team2Score + rated.get(r).getLevel();
                            ArrayList<GamePlayer> team = teams.get(Team.BETA);
                            team.add(rated.get(r));
                            teams.put(Team.BETA, team);
    			            swap++;
                            swap2++;
                            
                            if(swap == 4) {
                                swap = 0;
                            }
                        } else {
                        	team1Score = team1Score + rated.get(r).getLevel();
                            ArrayList<GamePlayer> team = teams.get(Team.ALPHA);
                            team.add(rated.get(r));
                            teams.put(Team.ALPHA, team);
                            swap++;
                            swap2++;
                            
                            if(swap == 4) {
                                swap = 0;
                            }
                        }   
                    } else {
                        if(r % 2 == 0) {
                        	team1Score = team1Score + rated.get(r).getLevel();
                            ArrayList<GamePlayer> team = teams.get(Team.ALPHA);
                            team.add(rated.get(r));
                            teams.put(Team.ALPHA, team);
                            swap++;
                            swap2++;
                            if(swap == 4) {
                                swap = 0;
                            }
                            
                            if(swap2 == 4) {
                                swap2 = 0;
                            }
                        } else {
                        	team2Score = team2Score + rated.get(r).getLevel();
                            ArrayList<GamePlayer> team = teams.get(Team.BETA);
                            team.add(rated.get(r));
                            teams.put(Team.BETA, team);
    			            swap++;
                            swap2++;
                            
                            if(swap == 4) {
                                swap = 0;
                            }
                            
                            if(swap2 == 4) {
                                swap2 = 0;
                            }
                        }   
                    }
                }
            }
		}
	}
	
	public Team getPlayerTeam(Player p) {
		for(Entry<Team, ArrayList<GamePlayer>> e : teams.entrySet()) {
			if(e.getValue().contains(PlayerManager.getInstance().getPlayer(p.getPlayerListName()))) {
				return e.getKey();
			}
		}
		
		return null;
	}
	
	public void addPlayerKill(Player p) {
		playerKills.put(p.getPlayerListName(), playerKills.get(p.getPlayerListName() + 1));
		teamKills.put(getPlayerTeam(p), teamKills.get(getPlayerTeam(p)) + 1);
		for(Entry<Team, ArrayList<GamePlayer>> e : teams.entrySet()) {
			for(GamePlayer player : e.getValue()) {
				
			}
		}
	}
	
	public void addPlayerDeath(Player p) {
		playerDeaths.put(p.getPlayerListName(), playerDeaths.get(p.getPlayerListName() + 1));
		teamDeaths.put(getPlayerTeam(p), teamDeaths.get(getPlayerTeam(p)) + 1);
		for(Entry<Team, ArrayList<GamePlayer>> e : teams.entrySet()) {
			for(GamePlayer player : e.getValue()) {
			}
		}
	}
	
	public int getPlayerKills(Player p) {
		return playerKills.get(p);
	}
	
	public int getPlayerDeaths(Player p) {
		return playerDeaths.get(p);
	}
	
	public int getTeamKills(Team t) {
		return teamKills.get(t);
	}
	
	public int getTeamDeaths(Team t) {
		return teamDeaths.get(t);
	}
	
	public void broadcast(String msg) {
		for(String player : a.getCurrentPlayers()) {
			PlayerManager.getInstance().getPlayer(player).getPlayer().sendMessage(msg);
		}
	}
	*/
}