package me.eelek.advancedkits;

import java.sql.Connection;
import java.util.ArrayList;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.eelek.advancedkits.kits.Kit;
import me.eelek.advancedkits.kits.KitManager;
import me.eelek.advancedkits.players.GamePlayer;
import me.eelek.advancedkits.players.Levels;
import me.eelek.advancedkits.players.PlayerHandler;

public class ConfigDataManager {
	
	static Connection connection;
	
	static void loadOnServerData(AKitsMain plugin) {
		//Load Kits
		if(CustomConfigHandler.getKits(plugin).contains("kits") == false) {
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
				
				KitManager.addKit(new Kit(kitName.replaceAll("_", " "), content, armor, kitItem, effects));
			}
		}
		
		//Load arenas
		
		
	}
	
	public static void getPlayerDataFromServer(Player p, AKitsMain plugin) {
		if(CustomConfigHandler.getPlayers(plugin).getConfigurationSection("players").getKeys(false).contains(p.getPlayerListName())) {
			String player = p.getPlayerListName();
			int kills = CustomConfigHandler.getPlayers(plugin).getInt("players." + player + ".kills");
			int deaths = CustomConfigHandler.getPlayers(plugin).getInt("players." + player + ".deaths");
			int points = CustomConfigHandler.getPlayers(plugin).getInt("players." + player + ".points");
			int level = CustomConfigHandler.getPlayers(plugin).getInt("players." + player + ".level");
			
			PlayerHandler.inputData(p, kills, deaths, points, level);
		} else {
			PlayerHandler.inputData(p, 0, 0, 0, 0);
		}
	}
	
	public static void saveDataToServer(AKitsMain plugin) {
		//Save playerdata
		if(!PlayerHandler.getAllPlayerData().isEmpty()) {
			for(GamePlayer p : PlayerHandler.getAllPlayerData()) {
				Player player = p.getPlayer();
				CustomConfigHandler.getPlayers(plugin).set("players." + player.getPlayerListName() + ".kills", p.getKills());
				CustomConfigHandler.getPlayers(plugin).set("players." + player.getPlayerListName() + ".deaths", p.getDeaths());
				CustomConfigHandler.getPlayers(plugin).set("players." + player.getPlayerListName() + ".points", p.getPoints());
				
				CustomConfigHandler.getPlayers(plugin).addDefault("players." + player.getPlayerListName() + ".kills", p.getKills());
				CustomConfigHandler.getPlayers(plugin).addDefault("players." + player.getPlayerListName() + ".deaths", p.getDeaths());
				CustomConfigHandler.getPlayers(plugin).addDefault("players." + player.getPlayerListName() + ".points", p.getPoints());
			}
		} else {
			AKitsMain.log.warning("[AdvancedKits] No player data could be saved to the server.");
		}
	}
	
	public static void getLevels(AKitsMain plugin) {
		for(String iLevel : CustomConfigHandler.getLevels(plugin).getStringList("levels")) {
			Integer level = Integer.parseInt(iLevel);
			Integer minimun = CustomConfigHandler.getLevels(plugin).getInt("levels." + iLevel + ".minimun_kills");
			String prefix = CustomConfigHandler.getLevels(plugin).getString("levels." + iLevel + ".prefix");
			
			Levels.addLevel(level, minimun, prefix);
		}
	}
}