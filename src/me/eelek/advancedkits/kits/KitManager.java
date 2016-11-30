package me.eelek.advancedkits.kits;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public class KitManager implements Listener {
	
	public static Inventory kitSelect = Bukkit.createInventory(null, 45, "Select your kit.");
	
	public static ArrayList<Kit> kits = new ArrayList<Kit>();
	
	public static void addKit(Kit kit) {
		kits.add(kit);
		kitSelect.addItem(kit.getDisplayItem());
	}
	
	public static void removeKit(Kit kit) {
		kits.remove(kit);
		kitSelect.remove(kit.getDisplayItem());
	}
	
	public static ArrayList<Kit> getAllKits() {
		return kits;
	}
	
	public static Inventory getSelectInventory() {
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
	
	public static void giveKit(Player p, Kit kit) {
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
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		
		if(e.getInventory().getName() == "Select your kit.") {
			e.setCancelled(true);
			
			if(e.getCurrentItem() != null) {
				if(e.getCurrentItem().hasItemMeta()) {
					for(Kit k : getAllKits()) {
						if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.DARK_PURPLE + k.getName())) {
							giveKit(p, k);
							p.sendMessage(ChatColor.BLUE + "You have recieved the " + ChatColor.AQUA + k.getName() + ChatColor.BLUE + " kit.");
							p.closeInventory();
						}
					}
				}
			}
		}
	}
}