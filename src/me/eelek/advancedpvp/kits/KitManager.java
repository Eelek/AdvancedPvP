package me.eelek.advancedpvp.kits;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;
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

import me.eelek.advancedpvp.arena.Arena;
import me.eelek.advancedpvp.arena.ArenaManager;
import me.eelek.advancedpvp.game.GameManager.GameType;
import me.eelek.advancedpvp.players.GamePlayer;
import me.eelek.advancedpvp.players.PlayerManager;

public class KitManager implements Listener {
	
	ArrayList<Kit> kits = new ArrayList<Kit>();
	
	private static KitManager instance;
	
	//Singleton
	protected KitManager() {
		
	}
	
	//Singleton
	public static KitManager getInstance() {
		if(instance == null) {
			instance = new KitManager();
		}
		
		return instance;
	}
	
	public void addKit(Kit kit) {
		kits.add(kit);
	}
	
	void removeKit(Kit kit) {
		kits.remove(kit);
	}
	
	public ArrayList<Kit> getAllKits() {
		return kits;
	}
	
	public Inventory getSelectInventory(GamePlayer p, Arena a) {
		if(a.getType() == GameType.FFA_RANK) {
			Inventory kitSelect = Bukkit.getServer().createInventory(null, 45, "Select your kit.");
			
			for(Kit k : a.getKitSet()) {
				ItemStack display = k.getDisplayItem();
				ItemMeta dMeta = display.getItemMeta();
				if(p.getLevel() >= k.getMinimumLevel()) {
					dMeta.setLore(Arrays.asList("§r§fYou need atleast level", "§r§a" + k.getMinimumLevel() + "§f.", "§r§fUse left click to select.", "§r§fUse right click to preview kit."));
				} else {
					dMeta.setLore(Arrays.asList("§r§fYou need atleast level", "§r§4" + k.getMinimumLevel() + "§f.", "§r§fUse left click to select.", "§r§fUse right click to preview kit."));
				}
				display.setItemMeta(dMeta);
				kitSelect.addItem(display);
			}
			
			return kitSelect;
		} else {
			Inventory kitSelect = Bukkit.getServer().createInventory(null, 45, "Select your kit.");
			
			for(Kit k : a.getKitSet()) {
				ItemStack display = k.getDisplayItem();
				ItemMeta dMeta = display.getItemMeta();
				if(p.getLevel() >= k.getMinimumLevel()) {
					dMeta.setLore(Arrays.asList("§r§fUse left click to select.", "§r§fUse right click to preview kit."));
				} else {
					dMeta.setLore(Arrays.asList("§r§fUse left click to select.", "§r§fUse right click to preview kit."));
				}
				display.setItemMeta(dMeta);
				kitSelect.addItem(display);
			}
			
			return kitSelect;
		}
	}
	
	Inventory getSelectInventory(Player p) {
		Inventory kitSelect = Bukkit.getServer().createInventory(null, 45, "Select your kit.");
		
		for(Kit k : kits) {
			kitSelect.addItem(k.getDisplayItem());
		}
		
		return kitSelect;
	}

	boolean isKit(Kit kit) {
		return kits.contains(kit);
	}
	
	public boolean isKit(String kitName) {
		boolean r = false;
		
		for(Kit kit : kits) {
			if(kit.getName().equals(kitName)) {
				r = true;
			}
		}
		
		return r;
	}
	
	public Kit getKit(String name) {
		Kit r = null;
		
		for(Kit kit : kits) {
			if(kit.getName().equals(name)) {
				r = kit;
			}
		}
		
		return r;
	}
	
	public boolean giveKit(GamePlayer p, Kit kit) {
		if(ArenaManager.getInstance().getArena(p.getCurrentArena()).getType() == GameType.FFA_RANK) {
			if(p.getLevel() >= kit.getMinimumLevel()) {
				p.getPlayer().getInventory().clear();
				
				for(ItemStack i : kit.getContent()) {
					p.getPlayer().getInventory().addItem(i);
				}
				
				for(ItemStack i : kit.getArmor()) {
					if(i.getType().toString().toLowerCase().contains("helmet")) {
						p.getPlayer().getInventory().setHelmet(i);
					} else if(i.getType().toString().toLowerCase().contains("chestplate") || i.getType().toString().toLowerCase().contains("elytra") || i.getType().toString().toLowerCase().contains("shield")) {
						p.getPlayer().getInventory().setChestplate(i);
					} else if(i.getType().toString().toLowerCase().contains("leggings")) {
						p.getPlayer().getInventory().setLeggings(i);
					} else if(i.getType().toString().toLowerCase().contains("boots")) {
						p.getPlayer().getInventory().setBoots(i);
					}
				}
				
				if(kit.getPotionEffects() != null) {
					for(PotionEffect pE : kit.getPotionEffects()) {
						p.getPlayer().addPotionEffect(pE);
					}
				}
				
				return true;
			} else {
				return false;
			}
		} else {
			p.getPlayer().getInventory().clear();
			
			for(ItemStack i : kit.getContent()) {
				p.getPlayer().getInventory().addItem(i);
			}
			
			for(ItemStack i : kit.getArmor()) {
				if(i.getType().toString().toLowerCase().contains("helmet")) {
					p.getPlayer().getInventory().setHelmet(i);
				} else if(i.getType().toString().toLowerCase().contains("chestplate") || i.getType().toString().toLowerCase().contains("elytra") || i.getType().toString().toLowerCase().contains("shield")) {
					p.getPlayer().getInventory().setChestplate(i);
				} else if(i.getType().toString().toLowerCase().contains("leggings")) {
					p.getPlayer().getInventory().setLeggings(i);
				} else if(i.getType().toString().toLowerCase().contains("boots")) {
					p.getPlayer().getInventory().setBoots(i);
				}
			}
			
			if(kit.getPotionEffects() != null) {
				for(PotionEffect pE : kit.getPotionEffects()) {
					p.getPlayer().addPotionEffect(pE);
				}
			} 
			
			return true;
		}
	}
	
	@EventHandler
	void onInventoryClick(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		GamePlayer player = PlayerManager.getInstance().getPlayer(p.getPlayerListName());
		Arena a = ArenaManager.getInstance().getArena(player.getCurrentArena());
		
		if(e.getInventory().getName().equals("Select your kit.")) {
			e.setCancelled(true);
			
			if(e.getCurrentItem() != null) {
				if(e.getCurrentItem().hasItemMeta()) {
					for(Kit k : getAllKits()) {
						if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.DARK_PURPLE + k.getName())) {
							if(e.getClick().isLeftClick()) {
								if(a.getType() != GameType.TDM_RANK) {
									if(giveKit(player, k)) {
										p.closeInventory();
										
										p.teleport(a.getSpawnLocation(p.getPlayerListName()));
										p.sendMessage(ChatColor.BLUE + "You have received the " + ChatColor.AQUA + k.getName() + ChatColor.BLUE + " kit.");
									} else {
										p.sendMessage(ChatColor.BLUE + "You don't have the level required for this kit.");
									}
								} else {
									p.sendMessage(ChatColor.BLUE + "You have selected the " + ChatColor.AQUA + k.getName() + ChatColor.BLUE + " kit.");
									p.sendMessage(ChatColor.BLUE + "You will receive this kit " + ChatColor.AQUA + "when the game starts or after dying" + ChatColor.BLUE + ".");
									player.setSelectedKit(k);
								}
							} else if(e.getClick().isRightClick()) {
								Inventory check = Bukkit.getServer().createInventory(null, 45, "Preview of kit: " + k.getName());
								
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
				if(isKit(e.getInventory().getName().split(": ")[1])) {
					Kit k = getKit(e.getInventory().getName().split(": ")[1]);
					if(a.getType() != GameType.TDM_RANK) {
						if(giveKit(player, k)) {
							p.closeInventory();
							
							p.teleport(a.getSpawnLocation(p.getPlayerListName()));
							p.sendMessage(ChatColor.BLUE + "You have received the " + ChatColor.AQUA + k.getName() + ChatColor.BLUE + " kit.");
						} else {
							p.sendMessage(ChatColor.BLUE + "You don't have the level required for this kit.");
						}
					} else {
						p.sendMessage(ChatColor.BLUE + "You have selected the " + ChatColor.AQUA + k.getName() + ChatColor.BLUE + " kit.");
						p.sendMessage(ChatColor.BLUE + "You will receive this kit " + ChatColor.AQUA + "when the game starts or after dying" + ChatColor.BLUE + ".");
						player.setSelectedKit(k);
					}
				}
			} else if(e.getCurrentItem().getType() == Material.REDSTONE_BLOCK) {
				p.closeInventory();
				p.openInventory(getSelectInventory(player, a));
			}
		}
	}
}