package me.eelek.advancedpvp.kits;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
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

	// Singleton
	protected KitManager() {}

	// Singleton
	public static KitManager getInstance() {
		if (instance == null) {
			instance = new KitManager();
		}

		return instance;
	}

	/**
	 * Add a Kit.
	 * @param kit The Kit to be added.
	 */
	public void addKit(Kit kit) {
		kits.add(kit);
	}

	/**
	 * Remove a Kit.
	 * @param kit The Kit to be removed.
	 */
	void removeKit(Kit kit) {
		kits.remove(kit);
	}

	/**
	 * Get a list containing all stored Kits.
	 * @return A list containing all stored Kits.
	 */
	public ArrayList<Kit> getAllKits() {
		return kits;
	}

	/**
	 * Function that generates an inventory with all Kits in an Arena's Kit set.
	 * @param a The Arena for which the inventory should be made.
	 * @param page The page that should be displayed.
	 * @return An invenntory with all Kits in an Arena's Kit Set.
	 */
	public Inventory generateSelectInventory(Arena a, int page) {
		int pageSize = 54;
		
		Inventory kitSelect = Bukkit.getServer().createInventory(null, pageSize, "[Kits] " + a.getName() + " Kits, Page " + (page + 1));

		for (int k = page * (pageSize - 9 * 2); k < (page + 1) * (pageSize - 9 * 2) && k < a.getKitSet().size(); k++) {
			ItemStack display = a.getKitSet().get(k).getDisplayItem();
			ItemMeta dMeta = (ItemMeta) display.getItemMeta();
			dMeta.setLore(Arrays.asList(a.getType() == GameType.FFA_RANK ? "§r§fYou need atleast level §r§5" + a.getKitSet().get(k).getMinimumLevel() + "§f." : "§r§aThis kit is available.", "§r§fUse left click to select.", "§r§fUse right click to preview kit."));
			display.setItemMeta(dMeta);
			kitSelect.addItem(display);
		}

		if(page > 0) {
			ItemStack previous = new ItemStack(Material.REDSTONE_TORCH, 1);
			ItemMeta pMeta = (ItemMeta) previous.getItemMeta();
			pMeta.setDisplayName(ChatColor.BLUE + "Previous page.");
			previous.setItemMeta(pMeta);
			kitSelect.setItem(kitSelect.getSize() - 9, previous);
		}
		
		if((page + 1) * (pageSize - 9 * 2) < a.getKitSet().size()) {
			ItemStack next = new ItemStack(Material.FEATHER, 1);
			ItemMeta nMeta = (ItemMeta) next.getItemMeta();
			nMeta.setDisplayName(ChatColor.BLUE + "Next page.");
			next.setItemMeta(nMeta);
			kitSelect.setItem(kitSelect.getSize() - 1, next);
		}

		return kitSelect;
	}

	/**
	 * Get if a Kit is a Kit.
	 * @param kit The Kit that should be checked.
	 * @return If the Kit is a valid Kit.
	 */
	boolean isKit(String kit) {
		boolean r = false;
		for(Kit k : kits) {
			if(k.getName().equalsIgnoreCase(kit)) {
				r = true;
			}
		}
		
		return r;
	}

	/**
	 * Get a Kit by name.
	 * @param name The name of the Kit.
	 * @return The Kit for which was searched.
	 */
	public Kit getKit(String name) {
		for (Kit kit : kits) {
			if (kit.getName().equalsIgnoreCase(name)) {
				return kit;
			}
		}

		return null;
	}

	/**
	 * Function that gives a player a Kit.
	 * @param p The player.
	 * @param kit The Kit that should be given.
	 * @return If the action was successful.
	 */
	public boolean giveKit(GamePlayer p, Kit kit) {
		if (ArenaManager.getInstance().getArena(p.getCurrentArena()).getType() == GameType.FFA_RANK && p.getLevel() < kit.getMinimumLevel()) return false;

		p.getPlayer().getInventory().clear();

		for (ItemStack i : kit.getContent()) {
			p.getPlayer().getInventory().addItem(i);
		}

		for (ItemStack i : kit.getArmor()) {
			if (i.getType().toString().toLowerCase().contains("helmet")) {
				p.getPlayer().getInventory().setHelmet(i);
			} else if (i.getType().toString().toLowerCase().contains("chestplate") || i.getType().toString().toLowerCase().contains("elytra") || i.getType().toString().toLowerCase().contains("shield")) {
				p.getPlayer().getInventory().setChestplate(i);
			} else if (i.getType().toString().toLowerCase().contains("leggings")) {
				p.getPlayer().getInventory().setLeggings(i);
			} else if (i.getType().toString().toLowerCase().contains("boots")) {
				p.getPlayer().getInventory().setBoots(i);
			}
		}

		if (!kit.getPotionEffects().isEmpty()) {
			for (PotionEffect pE : kit.getPotionEffects()) {
				p.getPlayer().addPotionEffect(pE);
			}
		}

		return true;
	}

	@EventHandler
	void onInventoryClick(InventoryClickEvent e) {
		if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;
		if(e.getCurrentItem().getType() == Material.REDSTONE_TORCH || e.getCurrentItem().getType() == Material.FEATHER) return;
		if (!(e.getView().getTitle().startsWith("[Kits]")) && !(e.getView().getTitle().startsWith("[Kit]"))) return;

		e.setCancelled(true);
		
		Player p = (Player) e.getWhoClicked();
		GamePlayer player = PlayerManager.getInstance().getPlayer(p.getUniqueId());
		Arena a = ArenaManager.getInstance().getArena(player.getCurrentArena());

		if (e.getView().getTitle().startsWith("[Kits]")) {
			Kit k = a.getKitSet().get(e.getSlot() + player.getOpenPage() * 36);

			if (e.getClick().isLeftClick()) {
				if (giveKit(player, k)) {
					p.closeInventory();
					
					Location loc = a.getSpawnLocation(p);
					if(loc != null) {
						p.teleport(loc);
						p.sendMessage(ChatColor.BLUE + "You have received the " + ChatColor.AQUA + k.getName() + ChatColor.BLUE + " kit.");
					} else {
						p.sendMessage(ChatColor.RED + "No (open) spawn could be found. Please try again.");
					}
				} else {
					p.sendMessage(ChatColor.BLUE + "You don't have the level required for this kit.");
				}
			} else if (e.getClick().isRightClick()) {
				p.closeInventory();
				p.openInventory(k.generatePreviewInventory(true));
			}
		} else {
			if (e.getCurrentItem().getType() != Material.REDSTONE_BLOCK) return;

			p.closeInventory();
			p.openInventory(generateSelectInventory(a, 0));
		}
	}
}