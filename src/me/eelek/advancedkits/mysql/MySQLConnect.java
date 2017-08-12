package me.eelek.advancedkits.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.entity.Player;

import me.eelek.advancedkits.AKitsMain;
import me.eelek.advancedkits.players.GamePlayer;
import me.eelek.advancedkits.players.PlayerHandler;

public class MySQLConnect {
	
	private static Connection connection;
	
	public static void establishMySQLConnection(String ip, String user, String pass, String dbname) {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			String url = "jdbc:mysql://" + ip + "/" + dbname;
			connection = DriverManager.getConnection(url, user, pass);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Connection getConnection() {
		return connection;
	}
	
	public static void closeConnection() {
		try {
			connection.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static boolean createTable(String host, String user, String pass, String dbname, String tablename, AKitsMain plugin) {
		MySQLConnect.establishMySQLConnection(host, user, pass, dbname);
		try {
			PreparedStatement statement = getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS `" + plugin.getMySQLData("database") + "`.`" + plugin.getMySQLData("table") + "` ( `player_name` VARCHAR(17) NOT NULL , `player_uuid` VARCHAR(37) NOT NULL , `kills` INT(255) NOT NULL , `deaths` INT(255) NOT NULL , `points` INT(255) NOT NULL );");
			statement.execute();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeConnection();
		}
		
		return false;
	}
	
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
				player = new GamePlayer(p, resultSet.getInt(3), resultSet.getInt(4), resultSet.getInt(5), resultSet.getInt(6), plugin.getConfig().getString("default-channel"));
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
			PreparedStatement statement = MySQLConnect.getConnection().prepareStatement("INSERT INTO `" + plugin.getMySQLData("table") + "` values(?,?,0,0,0,0);");
			statement.setString(1, p.getPlayerListName());
			statement.setString(2, PlayerHandler.getUUID(p).toString());
			statement.execute();
			statement.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
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