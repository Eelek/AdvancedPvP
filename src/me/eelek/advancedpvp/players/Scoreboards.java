package me.eelek.advancedpvp.players;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

public class Scoreboards {

	public static Scoreboard setFFAScoreboard(Player p) {
		Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
		Objective show = board.registerNewObjective("show", "dummy");
		show.setDisplaySlot(DisplaySlot.SIDEBAR);
		show.setDisplayName("§d§l§k|§4§lKit-PvP§d§l§k|");

		Score top = show.getScore(" ");
		top.setScore(15);

		Score welcome = show.getScore("" + ChatColor.WHITE + ChatColor.BOLD + "Hello there,");
		welcome.setScore(14);

		Score player = show.getScore("" + ChatColor.GOLD + ChatColor.BOLD + p.getPlayerListName() + ChatColor.BLUE + ChatColor.BOLD + "!");
		player.setScore(13);

		Score empty = show.getScore("  ");
		empty.setScore(12);

		Score yourKills = show.getScore("" + ChatColor.WHITE + ChatColor.BOLD + "Your kills: ");
		yourKills.setScore(11);

		Score kills = show.getScore("" + ChatColor.GREEN + PlayerManager.getInstance().getPlayer(p.getPlayerListName()).getKills());
		kills.setScore(10);

		Score empty1 = show.getScore("   ");
		empty1.setScore(9);

		Score yourDeaths = show.getScore("" + ChatColor.WHITE + ChatColor.BOLD + "Your deaths:");
		yourDeaths.setScore(8); 

		Score deaths = show.getScore("" + ChatColor.RED + PlayerManager.getInstance().getPlayer(p.getPlayerListName()).getDeaths());
		deaths.setScore(7);

		Score empty2 = show.getScore("    ");
		empty2.setScore(6);

		Score yourPoints = show.getScore("" + ChatColor.WHITE + ChatColor.BOLD + "Your points: ");
		yourPoints.setScore(5);

		Score points = show.getScore("" + ChatColor.GOLD + PlayerManager.getInstance().getPlayer(p.getPlayerListName()).getPoints());
		points.setScore(4);

		Score empty3 = show.getScore("     ");
		empty3.setScore(3);
		
		Score yourLevel = show.getScore("" + ChatColor.WHITE + ChatColor.BOLD + "Your level: ");
		yourLevel.setScore(2);
		
		Score level = show.getScore("" + ChatColor.AQUA + PlayerManager.getInstance().getPlayer(p.getPlayerListName()).getLevel());
		level.setScore(1);
		
		Score empty4 = show.getScore("            ");
		empty4.setScore(0);

		return board;
	}
	
	/*
	public static void setTDMSCoreboard(Player p) {
		Scoreboard board = null;//plugin.getServer().getScoreboardManager().getNewScoreboard();
		Objective show = board.registerNewObjective(p.getPlayerListName(), "dummy");
		show.setDisplaySlot(DisplaySlot.SIDEBAR);

		Score top = show.getScore(" ");
		top.setScore(15);

		Score welcome = show.getScore("" + ChatColor.WHITE + ChatColor.BOLD + "Welcome to TDM,");
		welcome.setScore(14);

		Score player = show.getScore("" + ChatColor.GOLD + ChatColor.BOLD + p.getPlayerListName() + ChatColor.BLUE + ChatColor.BOLD + "!");
		player.setScore(13);

		Score empty = show.getScore("  ");
		empty.setScore(12);

		Score yourKills = show.getScore("" + ChatColor.WHITE + ChatColor.BOLD + "Your team: ");
		yourKills.setScore(11);

		Score kills = show.getScore(g.getPlayerTeam(p) == Team.ALPHA ? "" + ChatColor.GOLD + "ALPHA" : ChatColor.RED + "BETA");
		kills.setScore(10);

		Score empty1 = show.getScore("   ");
		empty1.setScore(9);

		Score yourDeaths = show.getScore("" + ChatColor.WHITE + ChatColor.BOLD + "Your kills");
		yourDeaths.setScore(8); 

		Score deaths = show.getScore("" + ChatColor.GREEN + g.getPlayerKills(p) + ChatColor.WHITE);
		deaths.setScore(7);

		Score empty2 = show.getScore("    ");
		empty2.setScore(6);

		Score yourPoints = show.getScore("" + ChatColor.WHITE + ChatColor.BOLD + "Your deaths: ");
		yourPoints.setScore(5);

		Score points = show.getScore("" + ChatColor.RED + PlayerHandler.getInstance().getPlayer(p.getPlayerListName()).getPoints());
		points.setScore(4);

		Score empty3 = show.getScore("     ");
		empty3.setScore(3);
		
		Score yourLevel = show.getScore(g.getPlayerTeam(p) == Team.ALPHA ? "" + ChatColor.YELLOW + ChatColor.BOLD + "ALPHA kills " + ChatColor.WHITE + ChatColor.BOLD + "/ " + ChatColor.DARK_RED + "BETA kills" : "" + ChatColor.RED + ChatColor.BOLD + "BETA kills " + ChatColor.WHITE + ChatColor.BOLD + "/ " + ChatColor.GOLD + "ALPHA kills");
		yourLevel.setScore(2);
		
		Score level = show.getScore(g.getPlayerTeam(p) == Team.ALPHA ? "" + ChatColor.GOLD + g.getTeamKills(Team.ALPHA) + ChatColor.WHITE + " / " + ChatColor.RED + g.getTeamKills(Team.BETA) : "" + ChatColor.RED + g.getTeamKills(Team.BETA) + ChatColor.WHITE + " / " + ChatColor.GOLD + g.getTeamKills(Team.ALPHA));
		level.setScore(1);
		
		Score empty4 = show.getScore("            ");
		empty4.setScore(0);
		
		if(PlayerHandler.getInstance().getPlayer(p.getPlayerListName()).getBoard() != null) {
			tasks.remove(p.getPlayerListName());
			p.setScoreboard(board);
			PlayerHandler.getInstance().getPlayer(p.getPlayerListName()).setBoard(board);
			tasks.add(p.getPlayerListName());
		} else {
			tasks.add(p.getPlayerListName());
			p.setScoreboard(board);
			PlayerHandler.getInstance().getPlayer(p.getPlayerListName()).setBoard(board);	
		}

		//runName(show);
	}
    */
}