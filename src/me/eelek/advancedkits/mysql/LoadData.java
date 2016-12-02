package me.eelek.advancedkits.mysql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.bukkit.entity.Player;

import me.eelek.advancedkits.AKitsMain;
import me.eelek.advancedkits.players.GamePlayer;
import me.eelek.advancedkits.players.PlayerHandler;

public class LoadData {
	
	public static boolean doesPlayerHaveData(Player p, AKitsMain plugin) {
		try {
			PreparedStatement statement = MySQLConnect.getConnection().prepareStatement("SELECT * FROM `" + plugin.getMySQLData("table")  + "` WHERE player_name=?;");
			statement.setString(1, p.getPlayerListName());
			ResultSet resultSet = statement.executeQuery();
			boolean containsPlayer = resultSet.next();
			
			statement.close();
			resultSet.close();
			
			return containsPlayer;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static GamePlayer getPlayerData(Player p, AKitsMain plugin) {
		GamePlayer player = null;
		try {
			PreparedStatement statement = MySQLConnect.getConnection().prepareStatement("SELECT * FROM `" + plugin.getMySQLData("table") + "` WHERE player_name=?;");
			statement.setString(1, p.getPlayerListName());
			ResultSet resultSet = statement.executeQuery();
			
			if(resultSet.next()) {
					player = new GamePlayer(p, resultSet.getInt(3), resultSet.getInt(4), resultSet.getInt(5));
			}
			
		statement.close();
		resultSet.close();
		return player;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void addNewPlayer(Player p, AKitsMain plugin) {
		try {
			PreparedStatement statement = MySQLConnect.getConnection().prepareStatement("INSERT INTO `" + plugin.getMySQLData("table") + "` values(?,?,0,0,0);");
			statement.setString(1, p.getPlayerListName());
			statement.setString(2, PlayerHandler.getUUID(p).toString());
			statement.execute();
			statement.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
