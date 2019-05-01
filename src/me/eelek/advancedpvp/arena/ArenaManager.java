package me.eelek.advancedpvp.arena;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import me.eelek.advancedpvp.game.GameManager.GameType;
import net.minecraft.server.v1_12_R1.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_12_R1.PacketPlayOutChat;

public class ArenaManager implements Listener {
	
	private static ArenaManager instance = null;
	
	private ArrayList<Arena> arenas = new ArrayList<Arena>();
	
	//Singleton
	protected ArenaManager() {
		
	}
	
	//Singleton
	public static ArenaManager getInstance() {
		if(instance == null) {
			instance = new ArenaManager();
		}
		
		return instance;
	}

	public Inventory getInventory(Arena a) {
		Inventory inv = Bukkit.getServer().createInventory(null, 27, "Arena " + a.getName());
		
		ItemStack type = new ItemStack(Material.EMPTY_MAP, 1);
		ItemMeta tMeta = (ItemMeta) type.getItemMeta();
		tMeta.setDisplayName(ChatColor.DARK_PURPLE + "Game type: " + ChatColor.LIGHT_PURPLE + a.getType().toString() + ChatColor.DARK_PURPLE + ".");
		type.setItemMeta(tMeta);
		inv.setItem(9, type);

		ItemStack world = new ItemStack(Material.GRASS, 1);
		ItemMeta gMeta = (ItemMeta) world.getItemMeta();
		gMeta.setDisplayName(ChatColor.DARK_PURPLE + "World: " + ChatColor.LIGHT_PURPLE + a.getWorld().getName() + ChatColor.DARK_PURPLE + ".");
		gMeta.setLore(Arrays.asList("§r§fClick me for spawn info!"));
		world.setItemMeta(gMeta);
		inv.setItem(10, world);
		
		ItemStack current = new ItemStack(Material.TOTEM, 1);
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
		mMeta.setDisplayName(ChatColor.DARK_PURPLE + "Maximum players: " + ChatColor.LIGHT_PURPLE + a.getMaxPlayers() + ChatColor.DARK_PURPLE + ".");
		max.setItemMeta(mMeta);
		inv.setItem(15, max);
		
		ItemStack level = new ItemStack(Material.EXP_BOTTLE, 1);
		ItemMeta lMeta = (ItemMeta) level.getItemMeta();
		lMeta.setDisplayName(ChatColor.DARK_PURPLE + "Minimum level: " + ChatColor.LIGHT_PURPLE + a.getMinimumLevel() + ChatColor.DARK_PURPLE + ".");
		level.setItemMeta(lMeta);
		inv.setItem(16, level);
		
		return inv;
	}
	
	Inventory getWorldInventory(Arena a) {
		int size = (int) a.getAmountOfSpawns() / 9;
		if(size <= 0) {
			size = 1;
		}
		size = size * 9 + 9;
		
		if(size < 9) {
			size = 18;
		}
		
		Inventory wInv = Bukkit.getServer().createInventory(null, size, "Spawns in arena " + a.getName());
		
		for(int count = 0; count < a.getAmountOfSpawns(); count++) {
			ItemStack spawn = new ItemStack(Material.DIRT, 1);
			ItemMeta sMeta = (ItemMeta) spawn.getItemMeta();
			sMeta.setDisplayName(ChatColor.GREEN + "Spawn " + (count + 1));
			sMeta.setLore(Arrays.asList("§r§fX: " + a.getSpawn(count).getLocation().getBlockX(), "§r§fY: " + a.getSpawn(count).getLocation().getBlockY(), "§r§fZ: " + a.getSpawn(count).getLocation().getBlockZ(), "§r§fMax spawns: " + a.getSpawn(count).getCount(), "§r§fSpawn index: " + a.getSpawn(count).getIndex()));
			spawn.setItemMeta(sMeta);
			wInv.setItem(count, spawn);
		}
		
		ItemStack lobby = new ItemStack(Material.GOLD_BLOCK, 1);
		ItemMeta lMeta = (ItemMeta) lobby.getItemMeta();
		lMeta.setDisplayName(ChatColor.GOLD + "Lobby");
		lMeta.setLore(Arrays.asList("§r§fX: " + a.getLobbyLocation().getBlockX(), "§r§fY: " + a.getLobbyLocation().getBlockY(), "§r§fZ: " + a.getLobbyLocation().getBlockZ()));
		lobby.setItemMeta(lMeta);
		wInv.setItem(wInv.getSize() - 2, lobby);
		
		ItemStack back = new ItemStack(Material.BOOK, 1);
		ItemMeta bMeta = (ItemMeta) back.getItemMeta();
		bMeta.setDisplayName(ChatColor.DARK_GREEN + "Go back to the main menu.");
		back.setItemMeta(bMeta);
		wInv.setItem(wInv.getSize() - 1, back);
		
		return wInv;
	}
	
	Inventory getPlayerInventory(Arena a) {
		int size = a.getMaxPlayers() / 9;
		if(size % 10 >= 0.5) {
			size = (int) Math.ceil(size);
		} else {
			size = (int) Math.floor(size);
		}
		
		if(size < 9) {
			size = 18;
		}
		
		Inventory pInv = Bukkit.getServer().createInventory(null, size, "Players in arena " + a.getName());
		
		if(a.getCurrentPlayers() != null) {
			for(String p : a.getCurrentPlayers()) {
				ItemStack player = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
				SkullMeta pMeta = (SkullMeta) player.getItemMeta();
				pMeta.setDisplayName(ChatColor.GREEN + p);
				pMeta.setLore(Arrays.asList(ChatColor.DARK_GREEN + "Click to tp."));
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
	
	public Inventory getArenasInventory(String searchQuery) {
		int size = arenas.size() / 9;
		if(size % 10 >= 0.5) {
			size = (int) Math.ceil(size);
		} else {
			size = (int) Math.floor(size);
		}
		
		if(size < 9) {
			size = 18;
		} else if(size >= 54) {
			size = 45;
		}
		
		Inventory allInv = Bukkit.getServer().createInventory(null, size, searchQuery.isEmpty() ?  "All the things" : "Search results for: " + searchQuery);
		
		for(Arena a : arenas) {
			if(a.getName().contains(searchQuery) || searchQuery.equals("all")) {
				ItemStack arena = new ItemStack(Material.WOOL, 1, a.isActive() ? (short) 5 : (short) 14);
				ItemMeta aMeta = (ItemMeta) arena.getItemMeta();
				aMeta.setDisplayName(a.isActive() ? ChatColor.GREEN + a.getName() : ChatColor.RED + a.getName());
				arena.setItemMeta(aMeta);
				allInv.addItem(arena);
			}
		}
		
		ItemStack search = new ItemStack(Material.BOOK_AND_QUILL, 1);
		ItemMeta sMeta = (ItemMeta) search.getItemMeta();
		sMeta.setDisplayName(ChatColor.RESET + "Search with /arena search <query>.");
		search.setItemMeta(sMeta);
		allInv.setItem(allInv.getSize() - 1, search);
		
		return allInv;
	}
	
	public Inventory getCreateInventory(Player p, Arena a) {
        Inventory inv = Bukkit.getServer().createInventory(null, 27, "Arena " + a.getName());
		
		ItemStack type = new ItemStack(Material.ACTIVATOR_RAIL, 1);
		ItemMeta tMeta = (ItemMeta) type.getItemMeta();
		tMeta.setDisplayName(ChatColor.RED + "Click to set game type.");
		type.setItemMeta(tMeta);
		inv.setItem(10, type);
		
		ItemStack max = new ItemStack(Material.FENCE_GATE, 1);
		ItemMeta mMeta = (ItemMeta) max.getItemMeta();
		mMeta.setDisplayName(ChatColor.RED + "Click to set max. players.");
		max.setItemMeta(mMeta);
		inv.setItem(12, max);
		
		ItemStack level = new ItemStack(Material.GLASS_BOTTLE, 1);
		ItemMeta lMeta = (ItemMeta) level.getItemMeta();
		lMeta.setDisplayName(ChatColor.RED + "Click to set minumum level.");
		level.setItemMeta(lMeta);
		inv.setItem(14, level);
		
		ItemStack current = new ItemStack(Material.GOLD_HOE, 1);
		ItemMeta cMeta = (ItemMeta) current.getItemMeta();
		cMeta.setDisplayName(ChatColor.RED + "Click to set lobby location.");
		current.setItemMeta(cMeta);
		inv.setItem(16, current);
		
		ItemStack world = new ItemStack(Material.DIAMOND_HOE, 1);
		ItemMeta gMeta = (ItemMeta) world.getItemMeta();
		gMeta.setDisplayName(ChatColor.RED + "Click to set spawns.");
		world.setItemMeta(gMeta);
		inv.setItem(17, world);
		
		return inv;
	}
	
	public void addArena(Arena arena) {
		if(!arena.hasId()) {
			arena.setId(arenas.size());
		}
		arenas.add(arena);
	}
	
	public int getLastID() {
		return (arenas.size()-1);
	}

	public ArrayList<Arena> getArenas() {
		return arenas;
	}
	
	public ArrayList<String> getArenaNames() {
		ArrayList<String> r = new ArrayList<String>();
		for(Arena a : arenas) {
			r.add(a.getName());
		}
		
		return r;
	}
	
	public Arena getArena(String name) {
		for(Arena m : arenas) {
			if(m.getName().equals(name)) {
				return m;
			}
		}

		return null;
	}
	
	void removeMap(String name) {
		arenas.remove(getArena(name));
	}
	
	public boolean isArena(String name) {
		boolean r = false;
		
		for(Arena a : arenas) {
			if(a.getName().equals(name)) {
				r = true;
			}
		}
		
		return r;
	}
	
	@EventHandler
	void onSignChange(SignChangeEvent e) {
		if(e.getLine(0).equals("[arena]")) {
			if(e.getLine(1).equals("join")) {
				if(isArena(e.getLine(2))) {
					Arena a = getArena(e.getLine(2));
					if(a.isActive()) {
						if(a.getType() != GameType.DUEL) {
							e.setLine(0, "§6§l[§4§l" + a.getType().toString().substring(0, 5) + "§6§l]");
							e.setLine(1, a.getName());
							e.setLine(2, "§7§l" + a.getCurrentPlayers().size() + "§0§l/§8§l" + a.getMaxPlayers());
							e.setLine(3, "§5§lLevel: §a§l" + a.getMinimumLevel());
							e.getPlayer().sendMessage(ChatColor.BLUE + "Sign has been created.");
							Sign sign = (Sign) e.getBlock().getState();
							if(sign != null) {
								a.setSign(sign);
							}
						} else {
							e.setLine(0, "§6§l[§4§l" + a.getType().toString().substring(0, 5) + "§6§l]");
							e.setLine(1, a.getName());
							e.setLine(2, "§7§l" + a.getCurrentPlayers().size() + "§0§l/§8§l" + a.getMaxPlayers());
							e.setLine(3, "§5§lLevel: §a§l" + a.getMinimumLevel());
							e.getPlayer().sendMessage(ChatColor.BLUE + "Sign has been created.");
							Sign sign = (Sign) e.getBlock().getState();
							if(sign != null) {
								a.setSign(sign);
							}
						}
					} else {
						if(a.getType() != GameType.DUEL) {
							e.setLine(0, "§6§l[§4§l" + a.getType().toString().substring(0, 5) + "§6§l]");
							e.setLine(1, a.getName());
							e.setLine(2, "§4§lClosed.");
							e.setLine(3, "§5§lLevel: §a§l" + a.getMinimumLevel());
							e.getPlayer().sendMessage(ChatColor.BLUE + "Sign has been created.");
							Sign sign = (Sign) e.getBlock().getState();
							if(sign != null) {
								a.setSign(sign);
							}
						} else {
							e.setLine(0, "§6§l[§4§l" + a.getType().toString().substring(0, 4) + "§6§l]");
							e.setLine(1, a.getName());
							e.setLine(2, "§4§lClosed.");
							e.setLine(3, "§5§lLevel: §a§l" + a.getMinimumLevel());
							e.getPlayer().sendMessage(ChatColor.BLUE + "Sign has been created.");
							Sign sign = (Sign) e.getBlock().getState();
							if(sign != null) {
								a.setSign(sign);
							}
						}
					}
				}
			} else if(e.getLine(1).equals("leave")) {
				e.setLine(0, "§3§l[§2§lArena§3§l]");
				e.setLine(1, "§2§o§lLeave");
				e.setLine(3, "§5§oReturn to lobby.");
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	void onInventoryClick(InventoryClickEvent e) {
		if(e.getInventory().getName().contains("Arena") || e.getInventory().getName().contains("arena")) {
			if(e.getCurrentItem() != null) {
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
					} else if(e.getCurrentItem().getType() == Material.TOTEM) {
						e.getWhoClicked().closeInventory();
						e.getWhoClicked().openInventory(getPlayerInventory(a));
					} else if(e.getCurrentItem().getType() == Material.SKULL_ITEM) {
						e.getWhoClicked().closeInventory();
						SkullMeta meta = (SkullMeta) e.getCurrentItem().getItemMeta();
						Player p = Bukkit.getServer().getPlayer(meta.getOwner());
						
						e.getWhoClicked().teleport(p);
					} else if(e.getCurrentItem().getType() == Material.BOOK) {
						e.getWhoClicked().closeInventory();
						e.getWhoClicked().openInventory(getInventory(a));
					} else if(e.getCurrentItem().getType() == Material.DIRT) {
						e.getWhoClicked().closeInventory();
						List<String> lore = e.getCurrentItem().getItemMeta().getLore();
						Location tpLoc = new Location(e.getWhoClicked().getLocation().getWorld(), Integer.parseInt(lore.get(0).split(" ")[1]), Integer.parseInt(lore.get(1).split(" ")[1]), Integer.parseInt(lore.get(2).split(" ")[1]));
						e.getWhoClicked().teleport(tpLoc);
					} else if(e.getCurrentItem().getType() == Material.ACTIVATOR_RAIL) {
						PacketPlayOutChat p = new PacketPlayOutChat(ChatSerializer.a("{\"text\":\"Click here to set the game type.\",\"color\":\"aqua\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"/arena "+a.getName()+" set type <type>\"}}"));
						((CraftPlayer) e.getWhoClicked()).getHandle().playerConnection.sendPacket(p);
					} else if(e.getCurrentItem().getType() == Material.FENCE_GATE) {
						PacketPlayOutChat p = new PacketPlayOutChat(ChatSerializer.a("{\"text\":\"Click here to set the max. players.\",\"color\":\"aqua\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"/arena "+a.getName()+" set maxplayers <maxplayers>\"}}"));
						((CraftPlayer) e.getWhoClicked()).getHandle().playerConnection.sendPacket(p);
					} else if(e.getCurrentItem().getType() == Material.GLASS_BOTTLE) {
						PacketPlayOutChat p = new PacketPlayOutChat(ChatSerializer.a("{\"text\":\"Click here to start the minumum level.\",\"color\":\"aqua\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"/arena "+a.getName()+" set level <level>\"}}"));
						((CraftPlayer) e.getWhoClicked()).getHandle().playerConnection.sendPacket(p);
					} else if(e.getCurrentItem().getType() == Material.GOLD_HOE) {
						Player p = (Player) e.getWhoClicked();
						p.performCommand("/arena select lobby "+a.getName());
						p.sendMessage(ChatColor.AQUA + "Left click on the block beneath you.");
						p.sendMessage(ChatColor.BLUE + "To get back to the edit menu, use /arena edit "+a.getName());
					} else if(e.getCurrentItem().getType() == Material.DIAMOND_HOE) {
						Player p = (Player) e.getWhoClicked();
						p.performCommand("/arena select spawns "+a.getName());
						p.sendMessage(ChatColor.AQUA + "Left click on the block beneath you.");
						p.sendMessage(ChatColor.BLUE + "To get back to the edit menu, use /arena edit "+a.getName());
					}
				}
			}
		} else if(e.getInventory().getName().equals("All the things") || e.getInventory().getName().contains("Search results for")) {
			if(e.getCurrentItem() != null) {
				if(e.getCurrentItem().getType() != Material.AIR) {
					e.setCancelled(true);
					if(e.getCurrentItem().getType() != Material.BOOK_AND_QUILL) {
						e.getWhoClicked().closeInventory();
						e.getWhoClicked().openInventory(getInventory(getArena(e.getCurrentItem().getItemMeta().getDisplayName().substring(2))));
					}
				}
			}
		}
	}
	
	public Arena getDuelArena() {
		for(Arena a : getArenas()) {
			if(a.getType() == GameType.DUEL && a.getCurrentPlayers().size() < 2) {
				return a;
			}
		}
		
		return null;
	}
}