package me.eelek.advancedkits;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.eelek.advancedkits.arena.Arena;
import me.eelek.advancedkits.arena.ArenaManager;
import me.eelek.advancedkits.arena.GameManager;
import me.eelek.advancedkits.arena.GameManager.GameType;
import me.eelek.advancedkits.kits.Kit;
import me.eelek.advancedkits.kits.KitManager;
import me.eelek.advancedkits.kits.KitSet;
import me.eelek.advancedkits.players.GamePlayer;
import me.eelek.advancedkits.players.Levels;
import me.eelek.advancedkits.players.PlayerHandler;

public class ConfigDataManager {
	
	static Connection connection;
	
	static void loadOnServerData(AKitsMain plugin) {
		//Load Kits
		if(CustomConfigHandler.getKits(plugin).getConfigurationSection("kits") == null) {
			AKitsMain.log.warning("[AdvancedKits] No kits were found in the on-server storage.");
		} else {
			for(String kitName : CustomConfigHandler.getKits(plugin).getConfigurationSection("kits").getKeys(false)) {
				
				ArrayList<ItemStack> content = new ArrayList<ItemStack>();
				ArrayList<ItemStack> armor = new ArrayList<ItemStack>();
				
				for(String item : CustomConfigHandler.getKits(plugin).getStringList("kits." + kitName + ".content.normal")) {
					String[] split = item.split(",");
					Material m = Material.getMaterial(split[0]);
					int amount = Integer.parseInt(split[1]);
					
					if(split.length == 3) {
						int damage = Integer.parseInt(split[2]);
						ItemStack i = new ItemStack(m, amount, (short) damage);
						content.add(i);
					} else if(split.length < 3) {
						ItemStack i = new ItemStack(m, amount);
						content.add(i);
					} else if(split.length == 5) {
						int damage = Integer.parseInt(split[2]);
						
						Enchantment ench = Enchantment.getByName(split[3]);
						int level = Integer.parseInt(split[4]);
						
						ItemStack i = new ItemStack(m, amount, (short) damage);
						
						i.addEnchantment(ench, level);
						
						content.add(i);
					}
					
				}
				
				for (String armorItem : CustomConfigHandler.getKits(plugin).getStringList("kits." + kitName + ".content.armor")) {
					String[] split = armorItem.split(",");
					if (split.length == 3) {
						Material m = Material.getMaterial(split[0]);
						ItemStack i = new ItemStack(m, 1);

						Enchantment ench = Enchantment.getByName(split[1]);
						int level = Integer.parseInt(split[2]);

						i.addEnchantment(ench, level);

						armor.add(i);
					} else if (split.length == 4) {
						Material m = Material.getMaterial(split[0]);
						if (m.toString().toLowerCase().contains("leather")) {
							ItemStack i = new ItemStack(m, 1);

							int r = Integer.parseInt(split[1]);
							int g = Integer.parseInt(split[2]);
							int b = Integer.parseInt(split[3]);

							LeatherArmorMeta meta = (LeatherArmorMeta) i.getItemMeta();
							meta.setColor(Color.fromRGB(r, g, b));
							i.setItemMeta(meta);

							armor.add(i);
						} else {
							ItemStack i = new ItemStack(m, 1);

							Enchantment ench = Enchantment.getByName(split[1]);
							int level = Integer.parseInt(split[2]);

							i.addEnchantment(ench, level);

							armor.add(i);
						}
					} else if (split.length == 6) {
						Material m = Material.getMaterial(split[0]);
						if (m.toString().toLowerCase().contains("leather")) {
							ItemStack i = new ItemStack(m, 1);

							int r = Integer.parseInt(split[1]);
							int g = Integer.parseInt(split[2]);
							int b = Integer.parseInt(split[3]);

							LeatherArmorMeta meta = (LeatherArmorMeta) i.getItemMeta();
							meta.setColor(Color.fromRGB(r, g, b));
							i.setItemMeta(meta);

							Enchantment ench = Enchantment.getByName(split[4]);
							int level = Integer.parseInt(split[5]);

							i.addEnchantment(ench, level);

							armor.add(i);
						}
					} else {
						Material m = Material.getMaterial(split[0]);
						ItemStack i = new ItemStack(m, 1);
						armor.add(i);
					}
				} 
				
				ItemStack kitItem = new ItemStack(Material.getMaterial(CustomConfigHandler.getKits(plugin).getString("kits." + kitName + ".displayItem")));
				
				ArrayList<PotionEffect> effects = new ArrayList<PotionEffect>();
				for(String s : CustomConfigHandler.getKits(plugin).getStringList("kits." + kitName + ".effects")) {
					String[] split = s.split(","); 
					PotionEffectType pType = PotionEffectType.getByName(split[0]);
					int duration = Integer.parseInt(split[1]);
					int amp = Integer.parseInt(split[2]);
						
					PotionEffect pE = new PotionEffect(pType, duration, amp);
						
					effects.add(pE);
				}
				
				int level = CustomConfigHandler.getKits(plugin).getInt("kits." + kitName + ".minimum_level");
				
				KitManager.addKit(new Kit(kitName.replaceAll("_", " "), content, armor, kitItem, effects, level));
			}
		}
		
		//Load arenas
		
		
	}
	
	public static void getPlayerDataFromServer(Player p, AKitsMain plugin) {
		if(CustomConfigHandler.getPlayers(plugin).getConfigurationSection("players") != null) {
			String channel = plugin.getConfig().getString("default-channel");
			if(CustomConfigHandler.getPlayers(plugin).getConfigurationSection("players").getKeys(false).contains(p.getPlayerListName())) {
				String player = p.getPlayerListName();
				int kills = CustomConfigHandler.getPlayers(plugin).getInt("players." + player + ".kills");
				int deaths = CustomConfigHandler.getPlayers(plugin).getInt("players." + player + ".deaths");
				int points = CustomConfigHandler.getPlayers(plugin).getInt("players." + player + ".points");
				int level = CustomConfigHandler.getPlayers(plugin).getInt("players." + player + ".level");
				
				PlayerHandler.inputData(p, kills, deaths, points, level, channel);
			} else {
				PlayerHandler.inputData(p, 0, 0, 0, 0, channel);
			}
		} else {
			AKitsMain.log.warning("[AdvancedKits] No player data was found in the on-server storage.");
		}
	}
	
	
	public static void savePlayer(GamePlayer p, AKitsMain plugin) {
		Player player = p.getPlayer();
		CustomConfigHandler.getPlayers(plugin).set("players." + player.getPlayerListName() + ".kills", p.getKills());
		CustomConfigHandler.getPlayers(plugin).set("players." + player.getPlayerListName() + ".deaths", p.getDeaths());
		CustomConfigHandler.getPlayers(plugin).set("players." + player.getPlayerListName() + ".points", p.getPoints());
		CustomConfigHandler.getPlayers(plugin).set("players." + player.getPlayerListName() + ".level", p.getLevel());
		
		CustomConfigHandler.getPlayers(plugin).addDefault("players." + player.getPlayerListName() + ".kills", p.getKills());
		CustomConfigHandler.getPlayers(plugin).addDefault("players." + player.getPlayerListName() + ".deaths", p.getDeaths());
		CustomConfigHandler.getPlayers(plugin).addDefault("players." + player.getPlayerListName() + ".points", p.getPoints());
		CustomConfigHandler.getPlayers(plugin).addDefault("players." + player.getPlayerListName() + ".level", p.getLevel());
	}
	
	public static void saveDataToServer(AKitsMain plugin) {
		//Save playerdata
		if(!PlayerHandler.getAllPlayerData().isEmpty()) {
			for(GamePlayer p : PlayerHandler.getAllPlayerData()) {
				Player player = p.getPlayer();
				CustomConfigHandler.getPlayers(plugin).set("players." + player.getPlayerListName() + ".kills", p.getKills());
				CustomConfigHandler.getPlayers(plugin).set("players." + player.getPlayerListName() + ".deaths", p.getDeaths());
				CustomConfigHandler.getPlayers(plugin).set("players." + player.getPlayerListName() + ".points", p.getPoints());
				CustomConfigHandler.getPlayers(plugin).set("players." + player.getPlayerListName() + ".level", p.getLevel());
				
				CustomConfigHandler.getPlayers(plugin).addDefault("players." + player.getPlayerListName() + ".kills", p.getKills());
				CustomConfigHandler.getPlayers(plugin).addDefault("players." + player.getPlayerListName() + ".deaths", p.getDeaths());
				CustomConfigHandler.getPlayers(plugin).addDefault("players." + player.getPlayerListName() + ".points", p.getPoints());
				CustomConfigHandler.getPlayers(plugin).addDefault("players." + player.getPlayerListName() + ".level", p.getLevel());
			}
		} else {
			AKitsMain.log.warning("[AdvancedKits] No player data could be saved to the server.");
		}
	}
	
	public static void getLevels(AKitsMain plugin) {
		if(CustomConfigHandler.getLevels(plugin).getConfigurationSection("levels") != null) {
			for(String iLevel : CustomConfigHandler.getLevels(plugin).getConfigurationSection("levels").getKeys(false)) {
				Integer level = Integer.parseInt(iLevel.replace("level", ""));
				Integer minimun = CustomConfigHandler.getLevels(plugin).getInt("levels." + iLevel + ".minimum_kills");
				String prefix = ChatColor.translateAlternateColorCodes('&', CustomConfigHandler.getLevels(plugin).getString("levels." + iLevel + ".prefix"));
				
				Levels.addLevel(level, minimun, prefix);
				
				System.out.println("Level " + level + " min kills " + minimun + " prefix " + prefix);
			}
		} else {
			AKitsMain.log.warning("[AdvancedKits] No level data could be saved to the server.");
		}
	}
	
	public static void loadArenas(AKitsMain plugin) {
		if(CustomConfigHandler.getArenas(plugin).getConfigurationSection("arenas") != null) {
			for(String name : CustomConfigHandler.getArenas(plugin).getConfigurationSection("arenas").getKeys(false)) {
				int maxPlayers = CustomConfigHandler.getArenas(plugin).getInt("arenas." + name + ".max_players");
				int minLevel = CustomConfigHandler.getArenas(plugin).getInt("arenas." + name + ".minimum_level");
				World world = plugin.getServer().getWorld(CustomConfigHandler.getArenas(plugin).getString("arenas." + name + ".world"));
				GameType type = GameManager.getType(CustomConfigHandler.getArenas(plugin).getString("arenas." + name + ".type"));
				String kitSet = CustomConfigHandler.getArenas(plugin).getString("arenas." + name + ".kit_set");
				String[] loc = CustomConfigHandler.getArenas(plugin).getString("arenas." + name + ".lobby").split(",");
				Location lobby = new Location(world, Integer.parseInt(loc[0]), Integer.parseInt(loc[1]), Integer.parseInt(loc[2]));
				
				ArrayList<Location> spawns = new ArrayList<Location>();
				HashMap<Location, Integer> spawnCount = new HashMap<Location, Integer>();
				HashMap<Location, Integer> spawnIndex = new HashMap<Location, Integer>();
				for(String spawn : CustomConfigHandler.getArenas(plugin).getStringList("arenas." + name + ".spawns")) {
					String[] s = spawn.split(",");
					Location spawnloc = new Location(plugin.getServer().getWorld(s[0]), Integer.parseInt(s[1]), Integer.parseInt(s[2]), Integer.parseInt(s[3]), Float.parseFloat(s[4]), Float.parseFloat(s[5]));
					spawns.add(spawnloc);
					spawnCount.put(spawnloc, Integer.parseInt(s[6]));
					spawnIndex.put(spawnloc, 0);
				}
				
				for(String kitSetName : CustomConfigHandler.getArenas(plugin).getConfigurationSection("sets").getKeys(false)) {
					ArrayList<Kit> kits = new ArrayList<Kit>();
					for(String kit : CustomConfigHandler.getArenas(plugin).getStringList("sets." + kitSetName)) {
						if(KitManager.isKit(kit.replaceAll("_", " "))) {
							kits.add(KitManager.getKit(kit.replaceAll("_", " ")));
						} else if(kit.equalsIgnoreCase("all")) {
							kits.addAll(KitManager.getAllKits());
						} else {
							AKitsMain.log.warning("[AKitsMain] Couldn't load kit " + kit + " in kit set " + kitSetName + ".");
						}
					}
					
					KitSet.addSet(kitSetName, kits);
				}
				
				if(spawns.isEmpty() && type == null) {
					ArenaManager.addArena(new Arena(name, world, maxPlayers, minLevel));
				} else if(spawns.isEmpty() && type != null) {
					ArenaManager.addArena(new Arena(name, world, maxPlayers, minLevel, spawns, spawnCount, spawnIndex, type, kitSet));
				} else {
					ArenaManager.addArena(new Arena(name, world, maxPlayers, minLevel, spawns, spawnCount, spawnIndex, type, kitSet, lobby));
				}
				
				System.out.println("Loaded " + name);
			}
		} else {
			AKitsMain.log.warning("[AdvancedKits] No arena data could be saved to the server.");
		}
	}
	
	public static void saveArenas(AKitsMain plugin) {
		for(Arena a : ArenaManager.getArenas()) {
			CustomConfigHandler.getArenas(plugin).set("arenas." + a.getName() + ".max_players", a.getMaxPlayers());
			CustomConfigHandler.getArenas(plugin).set("arenas." + a.getName() + ".minimum_level", a.getMinimumLevel());
			CustomConfigHandler.getArenas(plugin).set("arenas." + a.getName() + ".world", a.getWorld().getName());
			if(a.getType() == null) {
				CustomConfigHandler.getArenas(plugin).set("arenas." + a.getName() + ".type", "null");
			} else {
				CustomConfigHandler.getArenas(plugin).set("arenas." + a.getName() + ".type", a.getType().toString().toLowerCase());
			}
			CustomConfigHandler.getArenas(plugin).set("arenas." + a.getName() + ".kit_set", a.getKitSetName());
			if(a.getLobbyLocation() == null) {
				CustomConfigHandler.getArenas(plugin).set("arenas." + a.getName() + ".lobby", "0,0,0");
			} else {
				String lobbyLoc = a.getLobbyLocation().getBlockX() + "," + a.getLobbyLocation().getBlockY() + "," + a.getLobbyLocation().getBlockZ();
				CustomConfigHandler.getArenas(plugin).set("arenas." + a.getName() + ".lobby", lobbyLoc);
			}

			ArrayList<String> list = new ArrayList<String>();
			for(Location l : a.getSpawnLocations()) {
				String loc = l.getWorld().getName() + "," + l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ() + "," + l.getPitch() + "," + l.getYaw() + "," + a.getSpawnCount(l);
				list.add(loc);
			}
			
			CustomConfigHandler.getArenas(plugin).set("arenas." + a.getName() + ".spawns", list);
			
			CustomConfigHandler.getArenas(plugin).addDefault("arenas." + a.getName() + ".max_players", a.getMaxPlayers());
			CustomConfigHandler.getArenas(plugin).addDefault("arenas." + a.getName() + ".minimum_level", a.getMinimumLevel());
			CustomConfigHandler.getArenas(plugin).addDefault("arenas." + a.getName() + ".world", a.getWorld().getName());
			if(a.getType() == null) {
				CustomConfigHandler.getArenas(plugin).addDefault("arenas." + a.getName() + ".type", "null");
			} else {
				CustomConfigHandler.getArenas(plugin).addDefault("arenas." + a.getName() + ".type", a.getType().toString().toLowerCase());
			}
			CustomConfigHandler.getArenas(plugin).addDefault("arenas." + a.getName() + ".spawns", list);
			if(a.getLobbyLocation() == null) {
				CustomConfigHandler.getArenas(plugin).addDefault("arenas." + a.getName() + ".lobby", "0,0,0");
			} else {
				String lobbyLoc = a.getLobbyLocation().getBlockX() + "," + a.getLobbyLocation().getBlockY() + "," + a.getLobbyLocation().getBlockZ();
				CustomConfigHandler.getArenas(plugin).addDefault("arenas." + a.getName() + ".lobby", lobbyLoc);
			}
			
			System.out.println("Saved arena " + a.getName());
		}
		
		for(Entry<String, ArrayList<Kit>> sets : KitSet.getSets().entrySet()) {
			ArrayList<String> kitNames = new ArrayList<String>();
			for(Kit k : sets.getValue()) {
				kitNames.add(k.getName().replaceAll(" ", "_"));
			}
			CustomConfigHandler.getArenas(plugin).set("sets." + sets.getKey(), kitNames);
			
			CustomConfigHandler.getArenas(plugin).addDefault("sets." + sets.getKey(), kitNames);
			
		}
	}
}