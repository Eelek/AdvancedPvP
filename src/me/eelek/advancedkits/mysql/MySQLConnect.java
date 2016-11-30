package me.eelek.advancedkits.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import me.eelek.advancedkits.AKitsMain;

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
	
	public static void createTable(String host, String user, String pass, String dbname, String tablename, AKitsMain plugin) {
		MySQLConnect.establishMySQLConnection(host, user, pass, dbname);
		try {
			PreparedStatement statement = getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS `" + plugin.getMySQLData()[1] + "`.`" + plugin.getMySQLData()[4] + "` ( `player_name` VARCHAR(17) NOT NULL , `player_uuid` VARCHAR(37) NOT NULL , `kills` INT(255) NOT NULL , `deaths` INT(255) NOT NULL , `points` INT(255) NOT NULL );");
			statement.execute();
			System.out.println("Made table.");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeConnection();
		}
	}

}
