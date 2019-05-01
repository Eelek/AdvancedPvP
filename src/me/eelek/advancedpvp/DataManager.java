package me.eelek.advancedpvp;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import me.eelek.advancedpvp.arena.Arena;
import me.eelek.advancedpvp.arena.ArenaManager;
import me.eelek.advancedpvp.arena.Spawn;
import me.eelek.advancedpvp.game.GameManager;
import me.eelek.advancedpvp.game.GameManager.GameType;
import me.eelek.advancedpvp.kits.Kit;
import me.eelek.advancedpvp.kits.KitManager;
import me.eelek.advancedpvp.players.GamePlayer;
import me.eelek.advancedpvp.players.LevelManager;
import me.eelek.advancedpvp.players.PlayerManager;

public class DataManager {

	private Connection c;

	private static DataManager instance;

	protected DataManager() {

	}
    
	//Singleton
	public static DataManager getInstance() {
		if (instance == null) {
			instance = new DataManager();
		}

		return instance;
	}

	/**
	 * Load all the data from the on-server storage and from the MySQL server.
	 * @param plugin An instance of the main class.
	 */
	public void boot(APvPMain plugin) {
		startConnection(plugin.getConfig().getString("MySQL-Host"), plugin.getConfig().getString("MySQL-Port"), plugin.getConfig().getString("MySQL-DB_Name"), plugin.getConfig().getString("MySQL-User"), plugin.getConfig().getString("MySQL-Password"));
		createTables(plugin);
		loadKits(plugin);
		loadArenas(plugin);
		loadLevels();
	}
	
	private void createTables(APvPMain plugin) {
		try {
			Connection c = DataManager.getInstance().getConnection();
			PreparedStatement s = c.prepareStatement("CREATE TABLE IF NOT EXISTS `" + plugin.getConfig().getString("MySQL-DB_Name") + "`.`player_data` ( `player_uuid` VARCHAR(37) NOT NULL , `kills` INT UNSIGNED NOT NULL , `deaths` INT UNSIGNED NOT NULL , `points` INT UNSIGNED NOT NULL , `level` INT UNSIGNED NOT NULL , UNIQUE `player_uuid` (`player_uuid`))");
			s.execute();
			s = c.prepareStatement("CREATE TABLE IF NOT EXISTS `" + plugin.getConfig().getString("MySQL-DB_Name") + "`.`level_data` ( `level` INT(255) NOT NULL AUTO_INCREMENT , `minimum_kills` INT(255) UNSIGNED NOT NULL , `prefix` VARCHAR(32) NOT NULL , PRIMARY KEY (`level`));");
			s.execute();
			s.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Save all the data
	 * @param plugin An instance of the main class.
	 * @param players if player data needs to be saved. (Can be skipped if there are no players online).
	 */
	public void shutdown(APvPMain plugin, boolean players) {
		if(players) {
			updateOnDisable();
		}
		
		saveArenas(plugin);
		closeConnection();
	}

	/**
	 * Function that loads and parses JSON data from the kits.json file.
	 * @param plugin An instance of the main class.
	 */
	private void loadKits(APvPMain plugin) {
		JSONParser parser = new JSONParser();

		try {
			Object object = parser.parse(new FileReader(plugin.getDataFolder() + "/kits.json"));
			JSONObject kitsObj = (JSONObject) object;

			JSONArray kits = (JSONArray) kitsObj.get("kits");

			if (kits.isEmpty()) {
				System.out.println("[AdvancedPvP] [ERROR] No kits were found in the on-server storage.");
				return;
			}

			for (int kitIndex = 0; kitIndex < kits.size(); kitIndex++) { //Iterate over all kits
				JSONObject kit = (JSONObject) kits.get(kitIndex);

				String name = (String) kit.get("name");
				String author = (String) kit.get("author");
				JSONArray contentArray = (JSONArray) kit.get("content");
				ArrayList<ItemStack> content = new ArrayList<ItemStack>();

				for (int cIndex = 0; cIndex < contentArray.size(); cIndex++) { //Kit Contents
					JSONObject contentObj = (JSONObject) contentArray.get(cIndex);
					Material m = Material.getMaterial(contentObj.get("item").toString().toUpperCase());
					if(m == null) { throw new ValueConversionException(contentObj.get("item").toString(), name); }
					
					int count = Integer.parseInt(contentObj.get("count").toString());
					ItemStack item = new ItemStack(m, count);

					if (contentObj.get("ench") != null) { //Enchantments
						JSONArray enchArray = (JSONArray) contentObj.get("ench");
						for (int enchIndex = 0; enchIndex < enchArray.size(); enchIndex++) {
							JSONObject enchObj = (JSONObject) enchArray.get(enchIndex);

							Enchantment ench = Enchantment.getByKey(NamespacedKey.minecraft(enchObj.get("name").toString().toLowerCase()));
							if(ench == null) { throw new ValueConversionException(enchObj.get("name").toString(), name); }
							
							int amp = Integer.parseInt(enchObj.get("amp").toString());

							item.addEnchantment(ench, amp);
						}
					}

					content.add(item);
				}

				JSONArray armorArray = (JSONArray) kit.get("armor");
				ArrayList<ItemStack> armor = new ArrayList<ItemStack>();

				for (int aIndex = 0; aIndex < armorArray.size(); aIndex++) { //Iterate over armor
					JSONObject armorObj = (JSONObject) armorArray.get(aIndex);
					Material m = Material.getMaterial(armorObj.get("item").toString().toUpperCase());
					if(m == null) { throw new ValueConversionException(armorObj.get("item").toString(), name); }
					
					ItemStack item = new ItemStack(m, 1);
					
					if ((m == Material.LEATHER_HELMET || m == Material.LEATHER_CHESTPLATE || m == Material.LEATHER_LEGGINGS || m == Material.LEATHER_BOOTS) && armorObj.get("data") != null) { //Check for leather armor, data becomes color
						JSONArray itemData = (JSONArray) armorObj.get("data");

						LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
						meta.setColor(Color.fromRGB(Integer.parseInt(itemData.get(0).toString()), Integer.parseInt(itemData.get(1).toString()), Integer.parseInt(itemData.get(2).toString())));

						item.setItemMeta(meta);
					}

					if (armorObj.get("ench") != null) { //Enchantments
						JSONArray enchArray = (JSONArray) armorObj.get("ench");
						for (int enchIndex = 0; enchIndex < enchArray.size(); enchIndex++) {
							JSONObject enchObj = (JSONObject) enchArray.get(enchIndex);

							Enchantment ench = Enchantment.getByKey(NamespacedKey.minecraft(enchObj.get("name").toString().toLowerCase()));
							if(ench == null) { throw new ValueConversionException(enchObj.get("name").toString(), name); }
							int amp = Integer.parseInt(enchObj.get("amp").toString());

							item.addEnchantment(ench, amp);
						}
					}

					armor.add(item);
				}

				Material display = Material.getMaterial(kit.get("displayItem").toString().toUpperCase());
				if(display == null) { throw new ValueConversionException(kit.get("displayItem").toString(), name); }

				JSONArray effectsArray = (JSONArray) kit.get("effects");
				ArrayList<PotionEffect> effects = new ArrayList<PotionEffect>();

				for (int eIndex = 0; eIndex < effectsArray.size(); eIndex++) {
					JSONObject effectObj = (JSONObject) effectsArray.get(eIndex);
					
					PotionEffectType eType = PotionEffectType.getByName(effectObj.get("name").toString().toUpperCase());
					if(eType == null) { throw new ValueConversionException(effectObj.get("name").toString(), name); }
					int duration = Integer.parseInt(effectObj.get("duration").toString());
					int amp = Integer.parseInt(effectObj.get("amp").toString());
					
					PotionEffect effect = new PotionEffect(eType, duration, amp);
					
					effects.add(effect);
				}
				
				int minimumLevel = Integer.parseInt(kit.get("minimumLevel").toString());
				
				KitManager.getInstance().addKit(new Kit(name, author, content, armor, new ItemStack(display, 1), minimumLevel));
			}
		} catch (FileNotFoundException e) {
			System.out.println("[AdvancedPvP] [ERROR] The kits.json file could not be found.\n------------------\n");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("[AdvancedPvP] [ERROR] An error occured whilst opening kits.json.\n------------------\n");
			e.printStackTrace();
		} catch (ParseException e) {
			System.out.println("[AdvancedPvP] [ERROR] There was an error whilst trying to parse the json in kits.json.\n------------------\n");
			e.printStackTrace();
		} catch(NumberFormatException e) {
			System.out.println("[AdvancedPvP] [ERROR] There was an error whilst trying to parse the json in kits.json.\n------------------\n");
			e.printStackTrace();
		} catch (ValueConversionException e) {
			System.out.println("[AdvancedPvP] [ERROR] There was an error whilst trying to parse the json in kits.json.\n------------------\n");
			e.printStackTrace();
		}
	}
	
	/**
	 * Function that loads and parses all the data from the arenas.json file.
	 * @param plugin An instance of the main class.
	 */
	private void loadArenas(APvPMain plugin) {
		JSONParser parser = new JSONParser();
		
		try {
			Object object = parser.parse(new FileReader(plugin.getDataFolder() + "/arenas.json"));
			JSONObject arenasObj = (JSONObject) object;

			JSONArray arenas = (JSONArray) arenasObj.get("arenas");
			
			if (arenas.isEmpty()) {
				System.out.println("[AdvancedPvP] [ERROR] No arenas were found in the on-server storage.");
				return;
			}
			
			for(int aIndex = 0; aIndex < arenas.size(); aIndex++) { //Iterate over the arenas
				JSONObject arena = (JSONObject) arenas.get(aIndex);
				
				String name = (String) arena.get("name");
				World world = Bukkit.getServer().getWorld(arena.get("world").toString());
				int maxPlayers = Integer.parseInt(arena.get("maxplayers").toString());
				int minimumLevel = Integer.parseInt(arena.get("minimumlevel").toString());
				Material displayItem = null;
				boolean active = (boolean) arena.get("active");
				
				if(arena.get("displayItem") != null) {
					displayItem = Material.getMaterial(arena.get("displayItem").toString().toUpperCase());
					
					if(displayItem == null) {
						displayItem = Material.GRASS; //In this case, no ValueConversionException is raised because here it makes sense to utilize a default value
					}
				} else {
					displayItem = Material.GRASS;
				}
				
				ArrayList<Spawn> spawns = new ArrayList<Spawn>();
				if(arena.get("spawns") != null) {
					JSONArray spawnsArray = (JSONArray) arena.get("spawns");
					for(int sIndex = 0; sIndex < spawnsArray.size(); sIndex++) {
						JSONObject spawn = (JSONObject) spawnsArray.get(sIndex);
						
						int x = Integer.parseInt(spawn.get("x").toString());
						int y = Integer.parseInt(spawn.get("y").toString());
						int z = Integer.parseInt(spawn.get("z").toString());
						int pitch = Integer.parseInt(spawn.get("pitch").toString());
						int yaw = Integer.parseInt(spawn.get("yaw").toString());
						int count = Integer.parseInt(spawn.get("maxspawns").toString());
						
						Location loc = new Location(world, x, y, z, pitch, yaw);
						spawns.add(new Spawn(sIndex, loc, count));
					}
				}	
				
				GameType type = null;
				if(arena.get("type") != null) {
					type = GameManager.getInstance().getType(arena.get("type").toString());
					if(type == null) { throw new ValueConversionException(arena.get("type").toString(), name); }
				}
				
				ArrayList<Kit> kitSet = new ArrayList<Kit>();
				if(arena.get("kits") != null) {
					JSONArray kitsArray = (JSONArray) arena.get("kits");
					
					for(int kIndex = 0; kIndex < kitsArray.size(); kIndex++) {
						kitSet.add(KitManager.getInstance().getKit(kitsArray.get(kIndex).toString()));
					}	
				}
				
				Location lobby = null;
				if(arena.get("lobby") != null) {
					JSONArray lobbyArray = (JSONArray) arena.get("lobby");
					lobby = new Location(world, Integer.parseInt(lobbyArray.get(0).toString()), Integer.parseInt(lobbyArray.get(1).toString()), Integer.parseInt(lobbyArray.get(2).toString()), Integer.parseInt(lobbyArray.get(3).toString()), Integer.parseInt(lobbyArray.get(4).toString()));
				}
				
				ArenaManager.getInstance().addArena(new Arena(name, world, maxPlayers, minimumLevel, spawns, type, kitSet, lobby, displayItem, active));
			}
		} catch (FileNotFoundException e) {
			System.out.println("[AdvancedPvP] [ERROR] The arenas.json file could not be found.\n------------------\n");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("[AdvancedPvP] [ERROR] An error occured whilst opening arenas.json.\n------------------\n");
			e.printStackTrace();
		} catch (ParseException e) {
			System.out.println("[AdvancedPvP] [ERROR] There was an error whilst trying to parse the json in arenas.json.\n------------------\n");
			e.printStackTrace();
		} catch(NumberFormatException e) {
			System.out.println("[AdvancedPvP] [ERROR] There was an error whilst trying to parse the json in arenas.json.\n------------------\n");
			e.printStackTrace();
		} catch(NullPointerException e) {
			System.out.println("[AdvancedPvP] [ERROR] There was an unknown variable in arenas.json.\n------------------\n");
			e.printStackTrace();
		} catch (ValueConversionException e) {
			System.out.println("[AdvancedPvP] [ERROR] There was an error whilst trying to parse the json in arenas.json.\n------------------\n");
			e.printStackTrace();
		}
	}

	/**
	 * Function that establishes a connection with the MySQL database.
	 * @param host The host address of the database
	 * @param port The port associated with the database
	 * @param db The database to be used
	 * @param user The username of the database
	 * @param pass The password of the database
	 */
	private void startConnection(String host, String port, String db, String user, String pass) {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			String url = "jdbc:mysql://" + host + ":" + port + "/" + db;
			c = DriverManager.getConnection(url, user, pass);
		} catch (SQLException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			boolean found = false;
			if (host.isEmpty() || host == null) {
				System.out.println("[AdvancedPvP] [ERROR] MySQL §6Host§r isn't set in config.");
				found = true;
			}

			if (port.isEmpty() || port == null) {
				System.out.println("[AdvancedPvP] [ERROR] MySQL §6Port§r isn't set in config.");
				found = true;
			}

			if (db.isEmpty() || db == null) {
				System.out.println("[AdvancedPvP] [ERROR] MySQL §6Database§r isn't set in config.");
				found = true;
			}

			if (user.isEmpty() || user == null) {
				System.out.println("[AdvancedPvP] [ERROR] MySQL §6Username§r isn't set in config.");
				found = true;
			}

			if (pass.isEmpty() || pass == null) {
				System.out.println("[AdvancedPvP] [ERROR] MySQL §6Password§r isn't set in config.");
				found = true;
			}

			if (!found) {
				System.out.println("[AdvancedPvP] [ERROR] Please contact the developer at dev@eelekweb.tk and send the following stacktrace.\n----------------------\n");
				e.printStackTrace();
			}
		}
	}

	/**
	 * Function that closes the connection with the database.
	 */
	private void closeConnection() {
		try {
			c.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			System.out.println("[ERROR] Connection couldn't be closed. (No open connection.)"); 
		}
	}

	/**
	 * Get the connection with the database.
	 * @return A Connection object, the connection the the database.
	 */
	public Connection getConnection() {
		return c;
	}

	/**
	 * Function that loads level data from the database.
	 */
	private void loadLevels() {
		try {
			PreparedStatement s = c.prepareStatement("SELECT * FROM `level_data`");
			ResultSet set = s.executeQuery();

			while (set.next() && !set.isAfterLast()) {
				LevelManager.getInstance().addLevel(set.getInt(1), set.getInt(2), set.getString(3));
			}

			set.close();
			s.close();
		} catch (SQLException e) {
			System.out.println("[AdvancedPvP] [ERROR] Error whilst trying to load in level data. If this problem persists, please contact the developer at dev@eelekweb.tk and send the following stacktrace.\n----------------------\n");
			e.printStackTrace();
		}
	}

	/**
	 * Function that saves arena data to the arena.json file.
	 * @param plugin An instance of the main class.
	 */
	@SuppressWarnings("unchecked")
	private void saveArenas(APvPMain plugin) {
		if(!ArenaManager.getInstance().getArenas().isEmpty()) {
			JSONObject jsonObj = new JSONObject();
			JSONArray arenas = new JSONArray();
			
			for(Arena a : ArenaManager.getInstance().getArenas()) {
				JSONObject arena = new JSONObject();
				arena.put("name", a.getName());
				arena.put("maxplayers", a.getMaxPlayers());	
				arena.put("minimumlevel", a.getMinimumLevel());
				arena.put("world", a.getWorld().getName());
				arena.put("displayItem", a.getDisplayItem().toString());
				arena.put("active", a.isActive());
				if(a.getType() != null) {
					arena.put("type", a.getType().toString());	
				}
				
				if(a.getLobbyLocation() != null) {
					JSONArray lobby = new JSONArray();
					lobby.add(a.getLobbyLocation().getBlockX());
					lobby.add(a.getLobbyLocation().getBlockY());
					lobby.add(a.getLobbyLocation().getBlockZ());
					lobby.add(Math.round(a.getLobbyLocation().getPitch()));
					lobby.add(Math.round(a.getLobbyLocation().getYaw()));
					arena.put("lobby", lobby);
				}
				
				if(a.getSpawns() != null) {
					JSONArray spawns = new JSONArray();
					for(Spawn s : a.getSpawns()) {
						JSONObject spawn = new JSONObject();
						spawn.put("x", s.getLocation().getBlockX());
						spawn.put("y", s.getLocation().getBlockY());
						spawn.put("z", s.getLocation().getBlockZ());
						spawn.put("pitch", Math.round(s.getLocation().getPitch()));
						spawn.put("yaw", Math.round(s.getLocation().getYaw()));
						spawn.put("maxspawns", s.getCount());
						spawns.add(spawn);
					}
					
					arena.put("spawns", spawns);	
				}
				
				if(a.getKitSet() != null) {
					JSONArray kits = new JSONArray();
					for(Kit k : a.getKitSet()) {
						kits.add(k.getName());
					}
					
					arena.put("kits", kits);
				}
				
				arenas.add(arena);
			}
			
			jsonObj.put("arenas", arenas);
			
			try(FileWriter arenaFile = new FileWriter(plugin.getDataFolder() + "/arenas.json")) {
				arenaFile.write(jsonObj.toJSONString());
			} catch (IOException e) {
				System.out.println("[AdvancedPvP] [ERROR] Error whilst trying to save arenas.json.\n----------------------\n");
				e.printStackTrace();
			}	
		}
	}

	/**
	 * Function that gets the player data from the MySQL server.
	 * @param p The username of the player
	 * @param defaultChannel The default chat channel
	 */
	public boolean getPlayerData(Player p, String defaultChannel) {
		boolean r = false;
		try {
			PreparedStatement s = c.prepareStatement("SELECT * FROM `player_data` WHERE `player_uuid` = ? ;");
			s.setString(1, p.getUniqueId().toString());
			ResultSet set = s.executeQuery();

			if (set.next()) {
				PlayerManager.getInstance().inputData(p, set.getInt(2), set.getInt(3), set.getInt(4), set.getInt(5), defaultChannel);
				r = true;
			}

			set.close();
			s.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		return r;
	}

	/**
	 * Function that creates a new player in the database.
	 * @param p The player that should be created.
	 */
	public void createPlayerData(Player p) {
		try {
			PreparedStatement s = c.prepareStatement("INSERT INTO `player_data` values(?,0,0,0,0);");
			s.setString(1, p.getUniqueId().toString());
			s.execute();
			s.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * Function that updates a player's data upon leaving the server.
	 * @param leave The GamePlayer object associated with the leaving player.
	 */
	public void updateOnLeave(GamePlayer leave) {
		try {
			PreparedStatement s = c.prepareStatement("UPDATE `player_data` SET kills = ?, deaths = ?, points = ?, level = ? WHERE player_uuid = ?");
			s.setInt(1, leave.getKills());
			s.setInt(2, leave.getDeaths());
			s.setInt(3, leave.getPoints());
			s.setInt(4, leave.getLevel());
			s.setString(5, leave.getPlayer().getUniqueId().toString());
			s.execute();
			s.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Function that updates player data upon server shutdown.
	 */
	private void updateOnDisable() {
		for (GamePlayer p : PlayerManager.getInstance().getAllPlayerData()) {
			try {
				PreparedStatement s = c.prepareStatement("UPDATE `player_data` SET kills = ?, deaths = ?, points = ?, level = ? WHERE player_uuid = ?");
				s.setInt(1, p.getKills());
				s.setInt(2, p.getDeaths());
				s.setInt(3, p.getPoints());
				s.setInt(4, p.getLevel());
				s.setString(5, p.getPlayer().getUniqueId().toString());
				s.execute();
				s.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}

/**
 * Custom Exception, thrown when a conversion from String (identifier in json file) to Spigot object goes wrong.
 * @author Eelek
 */

@SuppressWarnings("serial")
class ValueConversionException extends Exception {	
	public ValueConversionException() { super(); }
	public ValueConversionException(String message) { super(message); }
	public ValueConversionException(String value, String object) { super("Unknown value " + value + " in object " + object + "."); }
}