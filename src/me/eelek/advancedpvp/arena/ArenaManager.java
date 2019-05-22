package me.eelek.advancedpvp.arena;

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
import me.eelek.advancedpvp.ItemStackMaker;
import me.eelek.advancedpvp.game.GameManager.GameType;
import me.eelek.advancedpvp.players.GamePlayer;
import me.eelek.advancedpvp.players.PlayerManager;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class ArenaManager implements Listener {

	private static ArenaManager instance = null;

	private ArrayList<Arena> arenas = new ArrayList<Arena>();

	// Singleton
	protected ArenaManager() {
	}

	// Singleton
	public static ArenaManager getInstance() {
		if (instance == null) {
			instance = new ArenaManager();
		}

		return instance;
	}

	/**
	 * Function that generates an Inventory which contains all Arenas.
	 * 
	 * @param page
	 *            The page which should be displayed
	 * @return An Inventory which contains all Arenas.
	 */
	public Inventory generateArenasInventory(int page, boolean type) {
		int pageSize = 54; // Amount of slots in the inventory. (Has to be a
							// multiple of 9).

		Inventory inv = Bukkit.getServer().createInventory(null, pageSize, "[Arenas] Page " + (page + 1));

		for (int a = page * (pageSize - 9 * 2); a < (page + 1) * (pageSize - 9 * 2) && a < getArenas().size(); a++) {
			Arena arena = getArenas().get(a);

			if(type) {
				if(arena.getType() == GameType.FFA_UNLOCKED) {
					inv.addItem(ItemStackMaker.start(Material.GOLDEN_SWORD, 1)
											  .setName(arena.isActive() ? ChatColor.GREEN + arena.getName() : ChatColor.RED + arena.getName())
											  .create());
				} else if(arena.getType() == GameType.FFA_RANK) {
					inv.addItem(ItemStackMaker.start(Material.GOLDEN_AXE, 1)
											  .setName(arena.isActive() ? ChatColor.GREEN + arena.getName() : ChatColor.RED + arena.getName())
											  .create());
				} else if(arena.getType() == GameType.DUEL) {
					inv.addItem(ItemStackMaker.start(Material.DIAMOND_SWORD, 1)
							  .setName(arena.isActive() ? ChatColor.GREEN + arena.getName() : ChatColor.RED + arena.getName())
							  .create());
				}
			} else {
				inv.addItem(ItemStackMaker.start(arena.isActive() ? Material.LIME_WOOL : Material.RED_WOOL, 1)
										  .setName(arena.isActive() ? ChatColor.GREEN + arena.getName() : ChatColor.RED + arena.getName())
										  .create());
			}
		}

		if (page > 0) {
			inv.setItem(inv.getSize() - 9, ItemStackMaker.start(Material.REDSTONE_TORCH, 1)
					 .setName(ChatColor.BLUE + "Previous page.")
					 .create());
		}

		if ((page + 1) * (pageSize - 9 * 2) < getArenas().size()) {
			inv.setItem(inv.getSize() - 1, ItemStackMaker.start(Material.FEATHER, 1)
					 .setName(ChatColor.BLUE + "Next page.")
					 .create());
		}

		return inv;
	}

	/**
	 * Function that generates an Inventory that contains all GameTypes.
	 * 
	 * @return An Inventory that contains all GameTypes.
	 */
	public Inventory generateSelectorInventory() {
		int pageSize = 27;

		Inventory inv = Bukkit.getServer().createInventory(null, pageSize, "[Select] Type");

		inv.setItem(11, ItemStackMaker.start(Material.GOLDEN_SWORD, 1)
									  .setName(ChatColor.RED + "FFA Unlocked")
									  .create());

		inv.setItem(15, ItemStackMaker.start(Material.GOLDEN_AXE, 1)
				  .setName(ChatColor.RED + "FFA Ranked")
				  .create());
		
		inv.setItem(13, ItemStackMaker.start(Material.DIAMOND_SWORD, 1)
				  .setName(ChatColor.RED + "Duel")
				  .create());

		return inv;
	}

	/**
	 * Function that generates an Inventory that contains all Arenas with a
	 * certain GameType.
	 * 
	 * @param page
	 *            The page that should be displayed.
	 * @param type
	 *            The GameType for which should be filtered.
	 * @return An Inventory contains all Arenas with specified GameType.
	 */
	public Inventory generateSelectorInventory(int page, GameType type) {
		int pageSize = 54;

		Inventory inv = Bukkit.getServer().createInventory(null, pageSize, "[Select] Arena, page " + (page + 1));

		for (int a = page * (pageSize - 9 * 2); a < (page + 1) * (pageSize - 9 * 2) && a < getArenas(type, true).size(); a++) {
			Arena arena = getArenas(type, true).get(a);
			
			inv.addItem(ItemStackMaker.start(arena.getDisplayItem(), 1)
									  .setName("§a" +arena.getName())
									  .setLore(Arrays.asList("§r§8Players: §r§f" + arena.getCurrentPlayers().size() + "/" + arena.getMaxPlayers(), 
											  				 type == GameType.FFA_RANK ? "§r§8Minimum level: §r§f" + arena.getMinimumLevel() : null))
									  .create());
		}

		if (page > 0) {
			inv.setItem(inv.getSize() - 9, ItemStackMaker.start(Material.REDSTONE_TORCH, 1)
					 .setName(ChatColor.BLUE + "Previous page.")
					 .create());
		}

		if ((page + 1) * (pageSize - 9 * 2) < getArenas(type, true).size()) {
			inv.setItem(inv.getSize() - 1, ItemStackMaker.start(Material.FEATHER, 1)
					 .setName(ChatColor.BLUE + "Next page.")
					 .create());
		}
		
		inv.setItem(inv.getSize() - 2 , ItemStackMaker.start(Material.BOOK, 1)
				  .setName(ChatColor.BLUE + "Go back to the arena menu.")
				  .create());

		return inv;
	}

	/**
	 * Add an Arena.
	 * 
	 * @param arena
	 *            The arena to be added.
	 */
	public void addArena(Arena arena) {
		arenas.add(arena);
	}

	/**
	 * Get a list of all Arenas.
	 * 
	 * @return A list containing all Arenas.
	 */
	public ArrayList<Arena> getArenas() {
		return arenas;
	}

	/**
	 * Get a list of all Arena names.
	 * 
	 * @return A list containing all Arena names.
	 */
	public ArrayList<String> getArenaNames() {
		ArrayList<String> r = new ArrayList<String>();
		for (Arena a : arenas) {
			r.add(a.getName());
		}

		return r;
	}

	/**
	 * Get a list of all Arenas with a certain GameType.
	 * 
	 * @param type
	 *            The GameType for which should be filtered.
	 * @param enabled
	 * 			  If the Arena has to be enabled.
	 * @return A list containing all Arenas with a certain GameType.
	 */
	public ArrayList<Arena> getArenas(GameType type, boolean enabled) {
		ArrayList<Arena> list = new ArrayList<Arena>();

		for (Arena a : arenas) {
			if (a.getType() == type && a.isActive() == enabled)
				list.add(a);
		}

		return list;
	}

	/**
	 * Get Arena by name.
	 * 
	 * @param name
	 *            The name for which should be filtered.
	 * @return The Arena with specified name.
	 */
	public Arena getArena(String name) {
		for (Arena m : arenas) {
			if (m.getName().equalsIgnoreCase(name)) {
				return m;
			}
		}

		return null;
	}

	/**
	 * Remove an Arena by name.
	 * 
	 * @param name
	 *            The name of the Arena.
	 */
	void removeArena(String name) {
		arenas.remove(getArena(name));
	}

	@EventHandler
	void onInventoryClick(InventoryClickEvent e) {
		if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;
		if(e.getCurrentItem().getType() == Material.REDSTONE_TORCH || e.getCurrentItem().getType() == Material.FEATHER) return;
		if (e.getView().getTitle().startsWith("[Arena]")) {
			e.setCancelled(true);

			GamePlayer player = PlayerManager.getInstance().getPlayer(e.getWhoClicked().getUniqueId());
			Arena a = getArena(player.getInventoryArgument());
				
			if (e.getCurrentItem().getType() == Material.RED_STAINED_GLASS_PANE) { //Change active state of the arena
				e.getWhoClicked().closeInventory();
				a.setActive(true);
				player.openInventory(a.generateInventory(), 0, a.getName());
            } else if(e.getCurrentItem().getType() == Material.LIME_STAINED_GLASS_PANE) { 
            	e.getWhoClicked().closeInventory();
            	a.setActive(false);
            	player.openInventory(a.generateInventory(), 0, a.getName());
            } else if (e.getCurrentItem().getType() == Material.GRASS) { //Open Spawns menu
				e.getWhoClicked().closeInventory();
				player.openInventory(a.generateSpawnsInventory(0), 0, a.getName());
			} else if (e.getCurrentItem().getType() == Material.TOTEM_OF_UNDYING) { //Open Players menu
				e.getWhoClicked().closeInventory();
				player.openInventory(a.generatePlayersInventory(0), 0, a.getName());
			} else if (e.getCurrentItem().getType() == Material.MAP) { //Set the game type
				e.getWhoClicked().closeInventory();
				
				TextComponent msg = new TextComponent("§bClick here to set the game type.");
				msg.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/arena " + a.getName() + " set type <type>"));
				e.getWhoClicked().spigot().sendMessage(msg);
			} else if (e.getCurrentItem().getType() == Material.BARRIER) { //Set the max players
				e.getWhoClicked().closeInventory();
				
				TextComponent msg = new TextComponent("§bClick here to set the max. amount of players.");
				msg.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/arena " + a.getName() + " set maxplayers <maxplayers>"));
				e.getWhoClicked().spigot().sendMessage(msg);
			} else if (e.getCurrentItem().getType() == Material.EXPERIENCE_BOTTLE) { //Set the minimum level
				e.getWhoClicked().closeInventory();
				
				TextComponent msg = new TextComponent("§bClick here to set the minimum level.");
				msg.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/arena " + a.getName() + " set level <level>"));
				e.getWhoClicked().spigot().sendMessage(msg);
			}
		} else if (e.getView().getTitle().startsWith("[Arenas]")) { //List of Arena's
			e.setCancelled(true);
			
			GamePlayer player = PlayerManager.getInstance().getPlayer(e.getWhoClicked().getUniqueId());
			Arena a = arenas.get(e.getSlot() + player.getOpenPage() * 36); //There are at max 36 arenas on one page.
			
			e.getWhoClicked().closeInventory();
			if(a.isSetup()) {
				player.openInventory(a.generateInventory(), 0, a.getName());
			} else {
				e.getWhoClicked().sendMessage(ChatColor.BLUE + "Arena " + ChatColor.AQUA + a.getName() + ChatColor.BLUE + " hasn't been setup yet. Use " + ChatColor.DARK_AQUA + "/arena help" + ChatColor.BLUE + ".");
			}
		} else if(e.getView().getTitle().equals("[Select] Type")) {
			e.setCancelled(true);
			
			GamePlayer player = PlayerManager.getInstance().getPlayer(e.getWhoClicked().getUniqueId());
			e.getWhoClicked().closeInventory();
			
			if(e.getCurrentItem().getType() == Material.GOLDEN_SWORD) {
				player.openInventory(generateSelectorInventory(0, GameType.FFA_UNLOCKED), 0, GameType.FFA_UNLOCKED.toString());
			} else if(e.getCurrentItem().getType() == Material.GOLDEN_AXE) {
				player.openInventory(generateSelectorInventory(0, GameType.FFA_RANK), 0, GameType.FFA_RANK.toString());
			} else if(e.getCurrentItem().getType() == Material.DIAMOND_SWORD) {
				player.openInventory(generateSelectorInventory(0, GameType.DUEL), 0, GameType.DUEL.toString());
			}
		} else if(e.getView().getTitle().startsWith("[Select] Arena")) {
			e.setCancelled(true);
			
			GamePlayer player = PlayerManager.getInstance().getPlayer(e.getWhoClicked().getUniqueId());
			
			if(e.getCurrentItem().getType() == Material.BOOK) {
				e.getWhoClicked().closeInventory();
				player.openInventory(generateSelectorInventory(), 0, "");
			} else {
				ArrayList<Arena> arenasWithType = getArenas(GameType.valueOf(player.getInventoryArgument().toUpperCase()), true);
				
				Arena a = arenasWithType.get(e.getSlot() + player.getOpenPage() * 36);
				
				if(a.getCurrentPlayers().size() < a.getMaxPlayers() && player.getLevel() >= a.getMinimumLevel()) {
					a.addPlayer(player);
					e.getWhoClicked().getInventory().clear();
					e.getWhoClicked().getInventory().setItem(4, ItemStackMaker.start(Material.COMPASS, 1).setName("" + ChatColor.GOLD + ChatColor.BOLD + "|" + ChatColor.DARK_RED + ChatColor.BOLD + " Select your kit! " + ChatColor.GOLD + ChatColor.BOLD + "|").create());
					e.getWhoClicked().getInventory().setItem(8, ItemStackMaker.start(Material.COAL_BLOCK, 1).setName(ChatColor.RED + "Go back to the lobby.").create());
					e.getWhoClicked().getInventory().setHeldItemSlot(4);	
				} else {
					e.getWhoClicked().sendMessage(a.getCurrentPlayers().size() >= a.getMaxPlayers() ? ChatColor.RED + "Arena " + ChatColor.DARK_RED + a.getName() + ChatColor.RED + " is full." : ChatColor.RED + "You do not have the level required (" + ChatColor.DARK_RED + a.getMinimumLevel() + ChatColor.RED + ") to enter this arena.");
				}
			}
		} else if(e.getView().getTitle().startsWith("[Spawns]")) {
			e.setCancelled(true);
			
			GamePlayer player = PlayerManager.getInstance().getPlayer(e.getWhoClicked().getUniqueId());
			Arena a = getArena(player.getInventoryArgument());
			
			if (e.getCurrentItem().getType() == Material.BOOK) { //Go back to Arena menu
				e.getWhoClicked().closeInventory();
				player.openInventory(a.generateInventory(), 0, a.getName());
			} else if (e.getCurrentItem().getType() == Material.DIRT) { //Teleport to Spawn
				e.getWhoClicked().closeInventory();

				Spawn s = a.getSpawn(e.getSlot() + player.getOpenPage() * 36); //There are at max 36 spawns on a page.
				e.getWhoClicked().teleport(s.getLocation());
			} else if(e.getCurrentItem().getType() == Material.GOLD_BLOCK) {
				e.getWhoClicked().closeInventory();
				
				e.getWhoClicked().teleport(a.getLobbyLocation());
			} else if(e.getCurrentItem().getType() == Material.BRICK) {
				e.getWhoClicked().closeInventory();
				
				TextComponent msg = new TextComponent("§bClick me to add a spawn.");
				msg.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/arena " + a.getName() + " set spawn"));
				e.getWhoClicked().spigot().sendMessage(msg);
			}
		} else if(e.getView().getTitle().startsWith("[Players]")) {
			e.setCancelled(true);
			
			GamePlayer player = PlayerManager.getInstance().getPlayer(e.getWhoClicked().getUniqueId());
			Arena a = getArena(player.getInventoryArgument());
			
			if (e.getCurrentItem().getType() == Material.BOOK) { //Go back to Arena menu
				e.getWhoClicked().closeInventory();
				player.openInventory(a.generateInventory(), 0, a.getName());
			} else if (e.getCurrentItem().getType() == Material.PLAYER_HEAD) { //Teleport to player
				e.getWhoClicked().closeInventory();
				Player p = Bukkit.getServer().getPlayer(a.getCurrentPlayers().get(e.getSlot() + player.getOpenPage() * 36));
				
				if(p == null) {
				    e.getWhoClicked().sendMessage(ChatColor.RED + "Player " + ChatColor.DARK_RED + e.getCurrentItem().getItemMeta().getDisplayName() + ChatColor.RED + " is not online.");
				    return;
				}
					
				e.getWhoClicked().teleport(p);
			}
		}
    }
	
	/**
	 * Get a DUEL Arena which has an open slot.
	 * 
	 * @return A DUEL Arena with an open slot.
	 */
	public Arena getDuelArena() {
		for (Arena a : getArenas()) {
			if (a.getType() == GameType.DUEL && a.getCurrentPlayers().size() < 2 && a.isActive()) {
				return a;
			}
		}

		return null;
	}
}