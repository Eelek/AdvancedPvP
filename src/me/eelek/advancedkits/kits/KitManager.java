package me.eelek.advancedkits.kits;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;

import me.eelek.advancedkits.AKitsMain;
import me.eelek.advancedkits.arena.Arena;
import me.eelek.advancedkits.arena.ArenaManager;
import me.eelek.advancedkits.arena.GameManager.GameType;
import me.eelek.advancedkits.players.PlayerHandler;

public class KitManager implements Listener {
	
	public static ArrayList<Kit> kits = new ArrayList<Kit>();
	
	static AKitsMain plugin;
	
	public KitManager(AKitsMain plugin) {
		KitManager.plugin = plugin;
	}
	
	public static void addKit(Kit kit) {
		kits.add(kit);
	}
	
	public static void removeKit(Kit kit) {
		kits.remove(kit);
	}
	
	public static ArrayList<Kit> getAllKits() {
		return kits;
	}
	
	public static Inventory getSelectInventory(Player p, Arena a) {
		if(a.getType() == GameType.FFA_RANK) {
			Inventory kitSelect = plugin.getServer().createInventory(null, 45, "Select your kit.");
			
			for(Kit k : kits) {
				ItemStack display = k.getDisplayItem();
				ItemMeta dMeta = display.getItemMeta();
				if(PlayerHandler.getPlayer(p.getPlayerListName()).getLevel() >= k.getMinimumLevel()) {
					dMeta.setLore(Arrays.asList("§r§fYou need atleast level", "§r§a" + k.getMinimumLevel() + "§f."));
				} else {
					dMeta.setLore(Arrays.asList("§r§fYou need atleast level", "§r§4" + k.getMinimumLevel() + "§f."));
				}
				display.setItemMeta(dMeta);
				kitSelect.addItem(display);
			}
			
			return kitSelect;
		} else {
			Inventory kitSelect = plugin.getServer().createInventory(null, 45, "Select your kit.");
			
			for(Kit k : kits) {
				kitSelect.addItem(k.getDisplayItem());
			}
			
			return kitSelect;
		}
	}
	
	public static Inventory getSelectInventory(Player p) {
		Inventory kitSelect = plugin.getServer().createInventory(null, 45, "Select your kit.");
		
		for(Kit k : kits) {
			kitSelect.addItem(k.getDisplayItem());
		}
		
		return kitSelect;
	}

	public static boolean isKit(Kit kit) {
		return kits.contains(kit);
	}
	
	public static boolean isKit(String kitName) {
		boolean r = false;
		
		for(Kit kit : kits) {
			if(kit.getName().equals(kitName)) {
				r = true;
			}
		}
		
		return r;
	}
	
	public static Kit getKit(String name) {
		Kit r = null;
		
		for(Kit kit : kits) {
			if(kit.getName().equals(name)) {
				r = kit;
			}
		}
		
		return r;
	}
	
	public static boolean giveKit(Player p, Kit kit) {
		if(ArenaManager.getArena(PlayerHandler.getPlayer(p.getPlayerListName()).getCurrentArena()).getType() == GameType.FFA_RANK) {
			if(PlayerHandler.getPlayer(p.getPlayerListName()).getLevel() >= kit.getMinimumLevel()) {
				p.getInventory().clear();
				
				for(ItemStack i : kit.getContent()) {
					p.getInventory().addItem(i);
				}
				
				for(ItemStack i : kit.getArmor()) {
					if(i.getType().toString().toLowerCase().contains("helmet")) {
						p.getInventory().setHelmet(i);
					} else if(i.getType().toString().toLowerCase().contains("chestplate") || i.getType().toString().toLowerCase().contains("elytra") || i.getType().toString().toLowerCase().contains("shield")) {
						p.getInventory().setChestplate(i);
					} else if(i.getType().toString().toLowerCase().contains("leggings")) {
						p.getInventory().setLeggings(i);
					} else if(i.getType().toString().toLowerCase().contains("boots")) {
						p.getInventory().setBoots(i);
					}
				}
				
				if(kit.getPotionEffects() != null) {
					for(PotionEffect pE : kit.getPotionEffects()) {
						p.addPotionEffect(pE);
					}
				}
				
				return true;
			} else {
				return false;
			}
		} else {
			p.getInventory().clear();
			
			for(ItemStack i : kit.getContent()) {
				p.getInventory().addItem(i);
			}
			
			for(ItemStack i : kit.getArmor()) {
				if(i.getType().toString().toLowerCase().contains("helmet")) {
					p.getInventory().setHelmet(i);
				} else if(i.getType().toString().toLowerCase().contains("chestplate") || i.getType().toString().toLowerCase().contains("elytra") || i.getType().toString().toLowerCase().contains("shield")) {
					p.getInventory().setChestplate(i);
				} else if(i.getType().toString().toLowerCase().contains("leggings")) {
					p.getInventory().setLeggings(i);
				} else if(i.getType().toString().toLowerCase().contains("boots")) {
					p.getInventory().setBoots(i);
				}
			}
			
			if(kit.getPotionEffects() != null) {
				for(PotionEffect pE : kit.getPotionEffects()) {
					p.addPotionEffect(pE);
				}
			} 
			
			return true;
		}
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		
		if(e.getInventory().getName().equals("Select your kit.")) {
			e.setCancelled(true);
			
			if(e.getCurrentItem() != null) {
				if(e.getCurrentItem().hasItemMeta()) {
					for(Kit k : getAllKits()) {
						if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.DARK_PURPLE + k.getName())) {
							if(e.getClick().isLeftClick()) {
								boolean b = giveKit(p, k);
								if(b) {
									p.sendMessage(ChatColor.BLUE + "You have recieved the " + ChatColor.AQUA + k.getName() + ChatColor.BLUE + " kit.");
									p.closeInventory();
									p.teleport(ArenaManager.getArena(PlayerHandler.getPlayer(p.getPlayerListName()).getCurrentArena()).getSpawnLocation(p.getPlayerListName()));
								} else {
									p.sendMessage(ChatColor.BLUE + "You don't have the level required for this kit.");
								}
							} else if(e.getClick().isRightClick()) {
								Inventory check = plugin.getServer().createInventory(null, 45, "Preview of kit: " + k.getName());
								
								for(ItemStack item : k.getContent()) {
									check.addItem(item);
								}
								
								for(ItemStack armor : k.getArmor()) {
									check.addItem(armor);
								}
								
								ItemStack accept = new ItemStack(Material.EMERALD_BLOCK, 1);
								ItemMeta aMeta = (ItemMeta) accept.getItemMeta();
								aMeta.setDisplayName(ChatColor.GREEN + "Take this kit!");
								accept.setItemMeta(aMeta);
								check.setItem(39, accept);
								
								ItemStack back = new ItemStack(Material.REDSTONE_BLOCK, 1);
								ItemMeta bMeta = (ItemMeta) accept.getItemMeta();
								bMeta.setDisplayName(ChatColor.RED + "Go back to the select menu.");
								back.setItemMeta(bMeta);
								check.setItem(41, back);
								
								p.closeInventory();
								p.openInventory(check);
							}
						}
					}
				}
			}
		} else if(e.getInventory().getName().contains("Preview of kit: ")) {
			if(e.getCurrentItem().getType() == Material.EMERALD_BLOCK) {
				if(KitManager.isKit(e.getInventory().getName().split(": ")[1])) {
					Kit k = KitManager.getKit(e.getInventory().getName().split(": ")[1]);
					giveKit(p, k);
					p.sendMessage(ChatColor.BLUE + "You have recieved the " + ChatColor.AQUA + k.getName() + ChatColor.BLUE + " kit.");
					p.closeInventory();
					p.teleport(ArenaManager.getArena(PlayerHandler.getPlayer(p.getPlayerListName()).getCurrentArena()).getSpawnLocation(p.getPlayerListName()));
				}
			} else if(e.getCurrentItem().getType() == Material.REDSTONE_BLOCK) {
				p.closeInventory();
				p.openInventory(getSelectInventory(p, ArenaManager.getArena(PlayerHandler.getPlayer(p.getPlayerListName()).getCurrentArena())));
			}
		}
	}
}