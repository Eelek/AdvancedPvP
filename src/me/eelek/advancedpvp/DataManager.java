package me.eelek.advancedpvp;

import java.io.FileNotFoundException;
import java.io.FileReader;
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
import me.eelek.advancedpvp.kits.Kit;
import me.eelek.advancedpvp.kits.KitManager;
import me.eelek.advancedpvp.players.GamePlayer;
import me.eelek.advancedpvp.players.Levels;
import me.eelek.advancedpvp.players.PlayerManager;

public class DataManager {

	private Connection c;

	private static DataManager instance;

	protected DataManager() {

	}

	public static DataManager getInstance() {
		if (instance == null) {
			instance = new DataManager();
		}

		return instance;
	}

	public void loadKits(APvPMain plugin) {
		JSONParser parser = new JSONParser();

		try {
			Object object = parser.parse(new FileReader(plugin.getDataFolder() + "kits.json"));
			JSONObject kitsObj = (JSONObject) object;

			JSONArray kits = (JSONArray) kitsObj.get("kits");

			if (kits.isEmpty()) {
				System.out.println("[AdvancedKits] No kits were found in the on-server storage.");
				return;
			}

			for (int kitIndex = 0; kitIndex < kits.size(); kitIndex++) {
				JSONObject kit = (JSONObject) kits.get(kitIndex);

				String name = (String) kit.get("name");
				String author = (String) kit.get("author");
				JSONArray contentArray = (JSONArray) kit.get("content");
				ArrayList<ItemStack> content = new ArrayList<ItemStack>();

				for (int cIndex = 0; cIndex < contentArray.size(); cIndex++) {
					JSONObject contentObj = (JSONObject) contentArray.get(cIndex);
					Material m = Material.getMaterial(contentObj.get("item").toString());
					int count = Integer.parseInt(contentObj.get("count").toString());
					int data = 0;

					if (contentObj.get("data") != null) {
						data = Integer.parseInt(contentObj.get("data").toString());
					}

					ItemStack item = new ItemStack(m, count, (byte) data);

					if (contentObj.get("ench") != null) {
						JSONArray enchArray = (JSONArray) contentObj.get("ench");
						for (int enchIndex = 0; enchIndex < enchArray.size(); enchIndex++) {
							JSONObject enchObj = (JSONObject) enchArray.get(enchIndex);

							Enchantment ench = Enchantment.getByName(enchObj.get("name").toString());
							int amp = Integer.parseInt(enchObj.get("amp").toString());

							item.addEnchantment(ench, amp);
						}
					}

					content.add(item);
				}

				JSONArray armorArray = (JSONArray) kit.get("armor");
				ArrayList<ItemStack> armor = new ArrayList<ItemStack>();

				for (int aIndex = 0; aIndex < armorArray.size(); aIndex++) {
					JSONObject armorObj = (JSONObject) armorArray.get(aIndex);
					Material m = Material.getMaterial(armorObj.get("item").toString());

					ItemStack item = new ItemStack(m, 1);

					if ((m == Material.LEATHER_HELMET || m == Material.LEATHER_CHESTPLATE || m == Material.LEATHER_LEGGINGS || m == Material.LEATHER_BOOTS) && armorObj.get("data") != null) {
						JSONArray itemData = (JSONArray) armorObj.get("data");

						LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
						meta.setColor(Color.fromRGB(Integer.parseInt(itemData.get(0).toString()), Integer.parseInt(itemData.get(1).toString()), Integer.parseInt(itemData.get(2).toString())));

						item.setItemMeta(meta);
					}

					if (armorObj.get("ench") != null) {
						JSONArray enchArray = (JSONArray) armorObj.get("ench");
						for (int enchIndex = 0; enchIndex < enchArray.size(); enchIndex++) {
							JSONObject enchObj = (JSONObject) enchArray.get(enchIndex);

							Enchantment ench = Enchantment.getByName(enchObj.get("name").toString());
							int amp = Integer.parseInt(enchObj.get("amp").toString());

							item.addEnchantment(ench, amp);
						}
					}

					armor.add(item);
				}

				Material display = Material.getMaterial(kit.get("displayItem").toString());

				JSONArray effectsArray = (JSONArray) kit.get("effects");
				ArrayList<PotionEffect> effects = new ArrayList<PotionEffect>();

				for (int eIndex = 0; eIndex < effectsArray.size(); eIndex++) {
					JSONObject effectObj = (JSONObject) effectsArray.get(eIndex);
					
					PotionEffectType eType = PotionEffectType.getByName(effectObj.get("name").toString());
					int duration = Integer.parseInt(effectObj.get("duration").toString());
					int amp = Integer.parseInt(effectObj.get("amp").toString());
					
					PotionEffect effect = new PotionEffect(eType, duration, amp);
					
					effects.add(effect);
				}
				
				int minimumLevel = Integer.parseInt(kit.get("minimumLevel").toString());
				
				KitManager.getInstance().addKit(new Kit(name, author, content, armor, new ItemStack(display, 1), minimumLevel));
			}
		} catch (FileNotFoundException e) {
			System.out.println("The kits.json file could not be found.\n------------------\n");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("An error occured whilst opening kits.json.\n------------------\n");
			e.printStackTrace();
		} catch (ParseException e) {
			System.out.println("There was an error whilst trying to parse the json in kits.json.\n------------------\n");
			e.printStackTrace();
		} catch(NumberFormatException e) {
			System.out.println("There was an error whilst trying to parse the json in kits.json.\n------------------\n");
			e.printStackTrace();
		}
	}

	public void startConnection(String host, String port, String db, String user, String pass) {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			String url = "jdbc:mysql://" + host + ":" + port + "/" + db;
			c = DriverManager.getConnection(url, user, pass);
		} catch (SQLException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			boolean found = false;
			if (host.isEmpty() || host == null) {
				System.out.println("MySQL §6Host§r isn't set in config.");
				found = true;
			}

			if (port.isEmpty() || port == null) {
				System.out.println("MySQL §6Port§r isn't set in config.");
				found = true;
			}

			if (db.isEmpty() || db == null) {
				System.out.println("MySQL §6Database§r isn't set in config.");
				found = true;
			}

			if (user.isEmpty() || user == null) {
				System.out.println("MySQL §6Username§r isn't set in config.");
				found = true;
			}

			if (pass.isEmpty() || pass == null) {
				System.out.println("MySQL §6Password§r isn't set in config.");
				found = true;
			}

			if (!found) {
				System.out.println("Plugin error. Please contact the developer at dev@eelekweb.tk and send the following stacktrace.\n----------------------\n");
				e.printStackTrace();
			}
		}
	}

	public void closeConnection() {
		try {
			c.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public Connection getConnection() {
		return c;
	}

	public void loadItemsFromDatabase() {
		// Load Arena data
		try {
			PreparedStatement s = c.prepareStatement("SELECT * FROM `arena_data`");
			ResultSet set = s.executeQuery();

			while (set.next() & !set.isAfterLast()) {
				s = c.prepareStatement("SELECT * FROM `arena_spawns` WHERE `arena_id` = ?");
				s.setInt(1, set.getInt("arena_id"));
				ResultSet spawnData = s.executeQuery();

				s = c.prepareStatement("SELECT * FROM `arena_kits` WHERE `arena_id` = ?");
				s.setInt(1, set.getInt("arena_id"));
				ResultSet kitData = s.executeQuery();

				ArrayList<Spawn> spawns = new ArrayList<Spawn>();

				while (spawnData.next() && !spawnData.isAfterLast()) {
					Spawn spawn = new Spawn(spawns.size(), new Location(Bukkit.getServer().getWorld(spawnData.getString("world")), spawnData.getInt("x"), spawnData.getInt("y"), spawnData.getInt("z"), spawnData.getInt("pitch"), spawnData.getInt("yaw")), spawnData.getInt("spawns"));
					spawns.add(spawn);
				}

				ArrayList<Kit> kitSet = new ArrayList<Kit>();

				while (kitData.next() && !kitData.isAfterLast()) {
					kitSet.add(KitManager.getInstance().getKit(kitData.getString("kit").replaceAll("_", " ")));
				}

				Location lobby = new Location(Bukkit.getServer().getWorld(set.getString("world")), set.getInt("lobby_x"), set.getInt("lobby_y"), set.getInt("lobby_z"));

				ArenaManager.getInstance().addArena(new Arena(set.getInt("arena_id"), set.getString("name"), Bukkit.getServer().getWorld(set.getString("world")), set.getInt("max_players"), set.getInt("minimum_level"), spawns, GameManager.getInstance().getType(set.getString("type")), kitSet, lobby));
			}

			set.close();
			s.close();
		} catch (SQLException e) {
			System.out.println("Error whilst trying to load in arena data. If this problem persists, please contact the developer at dev@eelekweb.tk and send the following stacktrace.\n----------------------\n");
			e.printStackTrace();
		}

		// Load Level data
		try {
			PreparedStatement s = c.prepareStatement("SELECT * FROM `level_data`");
			ResultSet set = s.executeQuery();

			while (set.next() && !set.isAfterLast()) {
				Levels.getInstance().addLevel(set.getInt(1), set.getInt(2), set.getString(3));
			}

			set.close();
			s.close();
		} catch (SQLException e) {
			System.out.println("Error whilst trying to load in level data. If this problem persists, please contact the developer at dev@eelekweb.tk and send the following stacktrace.\n----------------------\n");
			e.printStackTrace();
		}
	}

	public void saveData() {
		// Save arena's
		try {
			for (Arena a : ArenaManager.getInstance().getArenas()) {
				if (a.isCreated()) {
					PreparedStatement s = c.prepareStatement("INSERT INTO `arena_data`(`name`, `max_players`, `minimum_level`, `world`, `lobby_x`, `lobby_y`, `lobby_z`, `type`) VALUES (?,?,?,?,?,?,?,?)");
					s.setString(1, a.getName());
					s.setInt(2, a.getMaxPlayers());
					s.setInt(3, a.getMinimumLevel());
					s.setString(4, a.getWorld().getName());
					s.setInt(5, a.getLobbyLocation().getBlockX());
					s.setInt(6, a.getLobbyLocation().getBlockY());
					s.setInt(7, a.getLobbyLocation().getBlockZ());
					s.setString(8, a.getType().toString());
					s.execute();
					s.close();

					for (Spawn spawn : a.getSpawns()) {
						s = c.prepareStatement("INSERT INTO `arena_spawns`(`arena_id`, `world`, `x`, `y`, `z`, `pitch`, `yaw`, `spawns`) VALUES (?,?,?,?,?,?,?,?)");
						s.setInt(1, a.getId());
						s.setString(2, spawn.getLocation().getWorld().getName());
						s.setInt(3, spawn.getLocation().getBlockX());
						s.setInt(4, spawn.getLocation().getBlockY());
						s.setInt(5, spawn.getLocation().getBlockZ());
						s.setFloat(6, spawn.getLocation().getPitch());
						s.setFloat(7, spawn.getLocation().getYaw());
						s.setInt(8, spawn.getCount());
						s.execute();
						s.close();
					}
				}
			}
		} catch (SQLException e) {
			System.out.println("Error whilst trying to save arena data. If this problem persists, please contact the developer at dev@eelekweb.tk and send the following stacktrace.\n----------------------\n");
			e.printStackTrace();
		}
	}

	public void getPlayerData(Player p, String defaultChannel) {
		try {
			PreparedStatement s = c.prepareStatement("SELECT * FROM `player_data` WHERE `player_uuid` = ? ;");
			s.setString(1, PlayerManager.getInstance().getUUID(p.getPlayer()).toString());
			ResultSet set = s.executeQuery();

			if (set.next()) {
				PlayerManager.getInstance().inputData(p, set.getInt(2), set.getInt(3), set.getInt(4), set.getInt(5), defaultChannel);
			}

			set.close();
			s.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}

	public void createPlayerData(Player p) {
		try {
			PreparedStatement s = c.prepareStatement("INSERT INTO `player_data` values(?,0,0,0,0);");
			s.setString(1, PlayerManager.getInstance().getUUID(p).toString());
			s.execute();
			s.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}

	public void updateOnLeave(GamePlayer leave) {
		try {
			PreparedStatement s = c.prepareStatement("UPDATE `player_data` SET kills = ?, deaths = ?, points = ?, level = ? WHERE player_uuid = ?");
			s.setInt(1, leave.getKills());
			s.setInt(2, leave.getDeaths());
			s.setInt(3, leave.getPoints());
			s.setInt(4, leave.getLevel());
			s.setString(5, PlayerManager.getInstance().getUUID(leave.getPlayer()).toString());
			s.execute();
			s.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	public void updateOnDisable() {
		for (GamePlayer p : PlayerManager.getInstance().getAllPlayerData()) {
			try {
				PreparedStatement s = c.prepareStatement("UPDATE `player_data` SET kills = ?, deaths = ?, points = ?, level = ? WHERE player_uuid = ?");
				s.setInt(1, p.getKills());
				s.setInt(2, p.getDeaths());
				s.setInt(3, p.getPoints());
				s.setInt(4, p.getLevel());
				s.setString(5, p.getPlayer().getPlayerListName());
				s.setString(6, PlayerManager.getInstance().getUUID(p.getPlayer()).toString());
				s.execute();
				s.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}