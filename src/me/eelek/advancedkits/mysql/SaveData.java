package me.eelek.advancedkits.mysql;

import java.sql.PreparedStatement;

import org.bukkit.entity.Player;

import me.eelek.advancedkits.AKitsMain;
import me.eelek.advancedkits.players.GamePlayer;
import me.eelek.advancedkits.players.PlayerHandler;

public class SaveData {
	
	public static void savePlayerDataToDatabase(GamePlayer p, AKitsMain plugin) {
		try {
			PreparedStatement statement = MySQLConnect.getConnection().prepareStatement("UPDATE `" + plugin.getMySQLData("table") + "`  SET kills = ?, deaths = ?, points = ?, level = ? WHERE player_name = ? AND player_uuid = ?;");
			statement.setInt(1, p.getKills());
			statement.setInt(2, p.getDeaths());
			statement.setInt(3, p.getPoints());
			statement.setInt(4, p.getLevel());
			statement.setString(5, p.getPlayer().getPlayerListName());
			statement.setString(6, PlayerHandler.getUUID(p.getPlayer()).toString());
			statement.execute();
			statement.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void updateOnDisable(AKitsMain plugin) {
		for(Player p : plugin.getServer().getOnlinePlayers()) {
			GamePlayer player = PlayerHandler.getPlayer(p.getPlayerListName());
			try {
				PreparedStatement statement = MySQLConnect.getConnection().prepareStatement("UPDATE `" + plugin.getMySQLData("table") + "`  SET kills = ?, deaths = ?, points = ?, level = ? WHERE player_name = ? AND player_uuid = ?;");
				statement.setInt(1, player.getKills());
				statement.setInt(2, player.getDeaths());
				statement.setInt(3, player.getPoints());
				statement.setInt(4, player.getLevel());
				statement.setString(5, p.getPlayer().getPlayerListName());
				statement.setString(6, PlayerHandler.getUUID(p.getPlayer()).toString());
				statement.execute();
				statement.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}