package me.eelek.advancedkits;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class CustomConfigHandler {

	// This class is only used when MySQL is disabled.
	// It can be enabled as a backup.

	// This is for the players.yml file.
	private static FileConfiguration players = null;
	private static File playersFile = null;

	public static void reloadPlayers(AKitsMain plugin) {
		if (playersFile == null) {
			playersFile = new File(plugin.getDataFolder(), "players.yml");
		}

		players = YamlConfiguration.loadConfiguration(playersFile);

		Reader defConfigStream;
		try {
			defConfigStream = new InputStreamReader(plugin.getResource("players.yml"), "UTF8");
			if (defConfigStream != null) {
				YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
				players.setDefaults(defConfig);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public static FileConfiguration getPlayers(AKitsMain plugin) {
		if (players == null) {
			reloadPlayers(plugin);
		}

		return players;
	}

	public static void savePlayers(AKitsMain plugin) {
		if (players == null || playersFile == null) {
			return;
		}

		try {
			getPlayers(plugin).save(playersFile);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public static void saveDefaultPlayers(AKitsMain plugin) {
		if (playersFile == null) {
			playersFile = new File(plugin.getDataFolder(), "players.yml");
		}

		if (!playersFile.exists()) {
			plugin.saveResource("players.yml", false);
		}
	}

	// This is for the kits.yml file.
	private static FileConfiguration kits = null;
	private static File kitsFile = null;

	public static void reloadKits(AKitsMain plugin) {
		if (kitsFile == null) {
			kitsFile = new File(plugin.getDataFolder(), "kits.yml");
		}

		kits = YamlConfiguration.loadConfiguration(kitsFile);

		Reader defConfigStream;
		try {
			defConfigStream = new InputStreamReader(plugin.getResource("kits.yml"), "UTF8");
			if (defConfigStream != null) {
				YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
				kits.setDefaults(defConfig);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public static FileConfiguration getKits(AKitsMain plugin) {
		if (kits == null) {
			reloadKits(plugin);
		}

		return kits;
	}

	public static void saveKits(AKitsMain plugin) {
		if (kits == null || kitsFile == null) {
			return;
		}

		try {
			getKits(plugin).save(kitsFile);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public static void saveDefaultKits(AKitsMain plugin) {
		if (kitsFile == null) {
			kitsFile = new File(plugin.getDataFolder(), "kits.yml");
		}

		if (!kitsFile.exists()) {
			plugin.saveResource("kits.yml", false);
		}
	}

}
