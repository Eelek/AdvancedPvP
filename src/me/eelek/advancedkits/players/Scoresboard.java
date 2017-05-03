package me.eelek.advancedkits.players;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import me.eelek.advancedkits.AKitsMain;

public class Scoresboard {

	public static void runName(AKitsMain plugin, Objective show) {
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
		scheduler.scheduleSyncRepeatingTask(plugin, new Runnable() {
			int currentState = 0;

			public void run() {
				if (currentState == 0) {
					show.setDisplayName("§9§lBlue§b§lCraft");
					currentState++;
				} else if (currentState == 1) {
					show.setDisplayName("§f§lB§9§llue§b§lCraft");
					currentState++;
				} else if (currentState == 2) {
					show.setDisplayName("§f§lBl§9§lue§b§lCraft");
					currentState++;
				} else if (currentState == 3) {
					show.setDisplayName("§f§lBlu§9§le§b§lCraft");
					currentState++;
				} else if (currentState == 4) {
					show.setDisplayName("§f§lBlue§b§lCraft");
					currentState++;
				} else if (currentState == 5) {
					show.setDisplayName("§9§lB§f§llueC§b§lraft");
					currentState++;
				} else if (currentState == 6) {
					show.setDisplayName("§9§lBl§f§lueCr§b§laft");
					currentState++;
				} else if (currentState == 7) {
					show.setDisplayName("§9§lBlu§f§leCra§b§lft");
					currentState++;
				} else if (currentState == 8) {
					show.setDisplayName("§9§lBlue§f§lCraf§b§lt");
					currentState++;
				} else if (currentState == 9) {
					show.setDisplayName("§9§lBlue§b§lC§f§lraft");
					currentState++;
				} else if (currentState == 10) {
					show.setDisplayName("§9§lBlue§b§lCr§f§laft");
					currentState++;
				} else if (currentState == 11) {
					show.setDisplayName("§9§lBlue§b§lCra§f§lft");
					currentState++;
				} else if (currentState == 12) {
					show.setDisplayName("§9§lBlue§b§lCraf§f§lt");
					currentState++;
				} else if (currentState == 13) {
					show.setDisplayName("§9§lBlue§b§lCraft");
					currentState++;
				} else if (currentState > 13 && currentState < 28) {
					currentState++;
				} else if (currentState == 28) {
					show.setDisplayName("§d§l§k|§4§lKit-PvP§d§l§k|");
					currentState++;
				} else if (currentState > 28 && currentState < 35) {
					currentState++;
				} else if (currentState == 35) {
					show.setDisplayName("             ");
					currentState++;
				} else if (currentState > 35 && currentState < 40) {
					currentState++;
				} else if (currentState == 40) {
					show.setDisplayName("§6§l|* §4§lKit-PvP §6§l*§l|");
					currentState++;
				} else if (currentState > 40 && currentState < 45) {
					currentState++;
				} else {
					currentState = 0;
				}
			}

		}, 0, 2);
	}

	public static void setScoreboard(AKitsMain plugin, Player p) {

		Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
		Objective show = board.registerNewObjective("scores", "dummy");
		show.setDisplaySlot(DisplaySlot.SIDEBAR);

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

		Score kills = show.getScore("" + ChatColor.GREEN + PlayerHandler.getInstance().getPlayer(p.getPlayerListName()).getKills());
		kills.setScore(10);

		Score empty1 = show.getScore("   ");
		empty1.setScore(9);

		Score yourDeaths = show.getScore("" + ChatColor.WHITE + ChatColor.BOLD + "Your deaths:");
		yourDeaths.setScore(8); 

		Score deaths = show.getScore("" + ChatColor.RED + PlayerHandler.getInstance().getPlayer(p.getPlayerListName()).getDeaths() + ChatColor.WHITE);
		deaths.setScore(7);

		Score empty2 = show.getScore("    ");
		empty2.setScore(6);

		Score yourPoints = show.getScore("" + ChatColor.WHITE + ChatColor.BOLD + "Your points: ");
		yourPoints.setScore(5);

		Score points = show.getScore("" + ChatColor.GOLD + PlayerHandler.getInstance().getPlayer(p.getPlayerListName()).getPoints());
		points.setScore(4);

		Score empty3 = show.getScore("     ");
		empty3.setScore(3);
		
		Score yourLevel = show.getScore("" + ChatColor.WHITE + ChatColor.BOLD + "Your level: ");
		yourLevel.setScore(2);
		
		Score level = show.getScore("" + ChatColor.AQUA + PlayerHandler.getInstance().getPlayer(p.getPlayerListName()).getLevel());
		level.setScore(1);
		
		Score empty4 = show.getScore("            ");
		empty4.setScore(0);

		p.setScoreboard(board);

		runName(plugin, show);
	}
}