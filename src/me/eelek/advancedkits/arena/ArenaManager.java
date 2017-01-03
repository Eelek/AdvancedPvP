package me.eelek.advancedkits.arena;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import me.eelek.advancedkits.AKitsMain;

public class ArenaManager implements Listener {
	
	static ArrayList<Arena> arenas = new ArrayList<Arena>();
	
	private static AKitsMain plugin;
	
	public ArenaManager(AKitsMain plugin) {
		ArenaManager.plugin = plugin;
	}

	public static Inventory getInventory(Arena a) {
		Inventory inv = plugin.getServer().createInventory(null, 27, "Arena " + a.getName());

		ItemStack world = new ItemStack(Material.GRASS, 1);
		ItemMeta gMeta = (ItemMeta) world.getItemMeta();
		gMeta.setDisplayName(ChatColor.DARK_PURPLE + "World: " + ChatColor.LIGHT_PURPLE + a.getWorld().getName() + ChatColor.DARK_PURPLE + ".");
		gMeta.setLore(Arrays.asList("§r§fClick me for spawn info!"));
		world.setItemMeta(gMeta);
		inv.setItem(10, world);
		
		ItemStack current = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		ItemMeta cMeta = (ItemMeta) current.getItemMeta();
		cMeta.setDisplayName(ChatColor.DARK_PURPLE + "Current players: " + ChatColor.LIGHT_PURPLE + a.getCurrentPlayers().size() + ChatColor.DARK_PURPLE + ".");
		gMeta.setLore(Arrays.asList("§r§fClick me to see the current players in the arena!"));
		current.setItemMeta(cMeta);
		inv.setItem(11, current);
		
		if(a.isActive()) {
			ItemStack active = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 5);
			ItemMeta aMeta = (ItemMeta) active.getItemMeta();
			aMeta.setDisplayName(ChatColor.DARK_PURPLE + "Arena " + ChatColor.LIGHT_PURPLE + a.getName() + ChatColor.DARK_PURPLE + " is " + ChatColor.GREEN + "active" + ChatColor.DARK_PURPLE + ".");;
			active.setItemMeta(aMeta);
			inv.setItem(13, active);
		} else { 
			ItemStack disabled = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14);
			ItemMeta dMeta = (ItemMeta) disabled.getItemMeta();
			dMeta.setDisplayName(ChatColor.DARK_PURPLE + "Arena " + ChatColor.LIGHT_PURPLE + a.getName() + ChatColor.DARK_PURPLE + " is " + ChatColor.RED + "disabled" + ChatColor.DARK_PURPLE + ".");;
			disabled.setItemMeta(dMeta);
			inv.setItem(13, disabled);
		}
		
		ItemStack max = new ItemStack(Material.BARRIER, 1);
		ItemMeta mMeta = (ItemMeta) max.getItemMeta();
		mMeta.setDisplayName(ChatColor.DARK_PURPLE + "Maximun players: " + ChatColor.LIGHT_PURPLE + a.getMaxPlayers() + ChatColor.DARK_PURPLE + ".");
		max.setItemMeta(mMeta);
		inv.setItem(15, max);
		
		ItemStack level = new ItemStack(Material.EXP_BOTTLE, 1);
		ItemMeta lMeta = (ItemMeta) level.getItemMeta();
		lMeta.setDisplayName(ChatColor.DARK_PURPLE + "Minimun level: " + ChatColor.LIGHT_PURPLE + a.getLevel() + ChatColor.DARK_PURPLE + ".");
		level.setItemMeta(lMeta);
		inv.setItem(16, level);
		
		return inv;
	}
	
	public static Inventory getWorldInventory(Arena a) {
		int size = a.getMaxPlayers() / 9;
		if(size % 10 >= 0.5) {
			size = (int) Math.ceil(size);
		} else {
			size = (int) Math.floor(size);
		}
		
		if(size < 9) {
			size = 18;
		}
		
		Inventory wInv = plugin.getServer().createInventory(null, size, "Spawns in arena " + a.getName());
		
		for(int count = 0; count < a.getAmountOfSpawns(); count++) {
			ItemStack spawn = new ItemStack(Material.DIRT, 1);
			ItemMeta sMeta = (ItemMeta) spawn.getItemMeta();
			sMeta.setDisplayName(ChatColor.GREEN + "Spawn " + (count + 1));
			sMeta.setLore(Arrays.asList("§r§fX:" + a.getSpawn(count).getBlockX(), "§r§fY: " + a.getSpawn(count).getBlockY(), "§r§fZ: " + a.getSpawn(count).getBlockZ(), "§r§fMax spawns: " + a.getSpawnCount(a.getSpawn(count))));
			spawn.setItemMeta(sMeta);
			wInv.setItem(count, spawn);
		}
		
		ItemStack back = new ItemStack(Material.BOOK, 1);
		ItemMeta bMeta = (ItemMeta) back.getItemMeta();
		bMeta.setDisplayName(ChatColor.DARK_GREEN + "Go back to the main menu.");
		back.setItemMeta(bMeta);
		wInv.setItem(wInv.getSize() - 1, back);
		
		return wInv;
	}
	
	public static Inventory getPlayerInventory(Arena a) {
		int size = a.getMaxPlayers() / 9;
		if(size % 10 > 0.5) {
			size = (int) Math.ceil(size);
		} else {
			size = (int) Math.floor(size);
		}
		
		if(size < 9) {
			size = 18;
		}
		
		Inventory pInv = plugin.getServer().createInventory(null, size, "Players in arena " + a.getName());
		
		if(a.getCurrentPlayers() != null) {
			for(String p : a.getCurrentPlayers()) {
				ItemStack player = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
				SkullMeta pMeta = (SkullMeta) player.getItemMeta();
				pMeta.setOwner(p);
				pMeta.setDisplayName(ChatColor.GREEN + p);
				player.setItemMeta(pMeta);
				pInv.addItem(player);
			}
		}
		
		ItemStack back = new ItemStack(Material.BOOK, 1);
		ItemMeta bMeta = (ItemMeta) back.getItemMeta();
		bMeta.setDisplayName(ChatColor.DARK_GREEN + "Go back to the main menu.");
		back.setItemMeta(bMeta);
		pInv.setItem(pInv.getSize() - 1, back);
		
		return pInv;
	}
	
	public static void addArena(Arena arena) {
		arenas.add(arena);
	}

	public static ArrayList<Arena> getArenas() {
		return arenas;
	}
	
	public static Arena getArena(String name) {
		for(Arena m : arenas) {
			if(m.getName().equals(name)) {
				return m;
			}
		}

		return null;
	}
	
	public static void removeMap(String name) {
		arenas.remove(getArena(name));
	}
	
	public static boolean isArena(String name) {
		boolean r = false;
		
		for(Arena a : arenas) {
			if(a.getName().equals(name)) {
				r = true;
			}
		}
		
		return r;
	}
	
	@EventHandler
	public void onSignChange(SignChangeEvent e) {
		if(e.getLine(0).equals("[arena]")) {
			if(e.getLine(1).equals("join")) {
				if(isArena(e.getLine(2))) {
					Arena a = getArena(e.getLine(2));
					if(a.isActive()) {
						e.setLine(0, "§6§l[§4§lArena§6§l]");
						e.setLine(1, a.getName());
						e.setLine(2, "§7§l" + a.getCurrentPlayers().size() + "§0§l/§8§l" + a.getMaxPlayers());
						e.setLine(3, "§5§lLevel: §a§l" + a.getLevel());
						e.getPlayer().sendMessage(ChatColor.BLUE + "Sign has been created.");
						Sign sign = (Sign) e.getBlock().getState();
						if(sign != null) {
							a.setSign(sign);
						}
					} else {
						e.setLine(0, "§6§l[§4§lArena§6§l]");
						e.setLine(1, a.getName());
						e.setLine(2, "§4§lClosed.");
						e.setLine(3, "§5§lLevel: §a§l" + a.getLevel());
						e.getPlayer().sendMessage(ChatColor.BLUE + "Sign has been created.");
						Sign sign = (Sign) e.getBlock().getState();
						if(sign != null) {
							a.setSign(sign);
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		if(e.getInventory().getName().contains("Arena") || e.getInventory().getName().contains("arena")) {
			if(e.getCurrentItem().getType() != Material.AIR) {
				e.setCancelled(true);
				Arena a = null;
				if(e.getInventory().getName().contains("Arena")) {
					a = getArena(e.getInventory().getName().split(" ")[1]);
				} else {
					a = getArena(e.getInventory().getName().split(" ")[3]);
				}
				if(e.getCurrentItem().getType() == Material.STAINED_GLASS_PANE) {
					if(e.getCurrentItem().getDurability() == (short) 5) {
						ItemStack disabled = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14);
						ItemMeta dMeta = (ItemMeta) disabled.getItemMeta();
						dMeta.setDisplayName(ChatColor.DARK_PURPLE + "Arena " + ChatColor.LIGHT_PURPLE + a.getName() + ChatColor.DARK_PURPLE + " is " + ChatColor.RED + "disabled" + ChatColor.DARK_PURPLE + ".");;
						disabled.setItemMeta(dMeta);
						e.setCurrentItem(disabled);
						
						a.setActive(false);
					} else {
						ItemStack active = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 5);
						ItemMeta aMeta = (ItemMeta) active.getItemMeta();
						aMeta.setDisplayName(ChatColor.DARK_PURPLE + "Arena " + ChatColor.LIGHT_PURPLE + a.getName() + ChatColor.DARK_PURPLE + " is " + ChatColor.GREEN + "active" + ChatColor.DARK_PURPLE + ".");;
						active.setItemMeta(aMeta);
						e.setCurrentItem(active);
						
						a.setActive(true);
					}
				} else if(e.getCurrentItem().getType() == Material.GRASS) {
					e.getWhoClicked().closeInventory();
					e.getWhoClicked().openInventory(getWorldInventory(a));
				} else if(e.getCurrentItem().getType() == Material.SKULL_ITEM) {
					e.getWhoClicked().closeInventory();
					e.getWhoClicked().openInventory(getPlayerInventory(a));
				} else if(e.getCurrentItem().getType() == Material.BOOK) {
					e.getWhoClicked().closeInventory();
					e.getWhoClicked().openInventory(getInventory(a));
				}
			}
		}
	}
}