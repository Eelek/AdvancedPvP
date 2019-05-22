package me.eelek.advancedpvp.kits;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import me.eelek.advancedpvp.ItemStackMaker;

public class Kit {
	
	private String name;
	private String author;
	private ArrayList<ItemStack> content;
	private ArrayList<ItemStack> armor;
	private ItemStack kitItem;
	private ArrayList<PotionEffect> potionEffects;
	private int minimumLevel;
	
	/**
	 * Kit Object.
	 * @param name The name of the Kit.
	 * @param author  The author of the Kit.
	 * @param content A list containing all items that the Kit contains.
	 * @param armor A list containing all the armor that the Kit contains.
	 * @param kitItem The display item of the Kit.
	 * @param minimumLevel The minimum level required for this Kit.
	 */
	public Kit(String name, String author, ArrayList<ItemStack> content, ArrayList<ItemStack> armor, Material kitItem, int minimumLevel) {
		this.name = name;
		this.author = author;
		this.content = content;
		this.armor = armor;
		this.kitItem = ItemStackMaker.start(kitItem, 1).setName("§5" +name).setLore(Arrays.asList("§r§fLeft click to select", "§r§fRight click to see content.")).create();
		this.potionEffects = null;
		this.minimumLevel = minimumLevel;
	}
	
	/**
	 * Kit Object.
	 * @param name The name of the Kit.
	 * @param author  The author of the Kit.
	 * @param content A list containing all items that the Kit contains.
	 * @param armor A list containing all the armor that the Kit contains.
	 * @param kitItem The display item of the Kit.
	 * @param potionEffects A list containing all potion effects associated with the Kit.
	 * @param minimumLevel The minimum level required for this Kit.
	 */
	public Kit(String name, String author, ArrayList<ItemStack> content, ArrayList<ItemStack> armor, Material kitItem, ArrayList<PotionEffect> potionEffects, int minimumLevel) {
		this.name = name;
		this.author = author;
		this.content = content;
		this.armor = armor;
		this.kitItem = ItemStackMaker.start(kitItem, 1).setName("§5" +name).setLore(Arrays.asList("§r§fLeft click to select", "§r§fRight click to see content.")).create();
		this.potionEffects = potionEffects;
		this.minimumLevel = minimumLevel;
	}
	
	/**
	 * Get the name of the Kit.
	 * @return The name of the Kit.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Get a list of all the items in the Kit.
	 * @return A list of all the items in the Kit.
	 */
	public ArrayList<ItemStack> getContent() {
		return content;
	}
	
	/**
	 * Get an item by index.
	 * @param index The index.
	 * @return The item at the specified index.
	 */
	public ItemStack getItem(int index) {
		return content.get(index);
	}
	
	/**
	 * Get the armor that the Kit contains.
	 * @return A list with all the armor that the Kit contains.
	 */
	public ArrayList<ItemStack> getArmor() {
		return armor;
	}
	
	/**
	 * Get the display item of the Kit.
	 * @return The display item of the Kit.
	 */
	public ItemStack getDisplayItem() {
		return kitItem;
	}
	
	/**
	 * Get the potion effects of the Kit.
	 * @return The potion effects of the Kit.
	 */
	public ArrayList<PotionEffect> getPotionEffects() {
		return potionEffects;
	}
	
	/**
	 * Get the minimum level required for this Kit.
	 * @return The minimum level required for this Kit.
	 */
	public int getMinimumLevel() {
		return minimumLevel;
	}
	
	/**
	 * Set the minimum level required for this Kit.
	 * @param level The new minimum level required for this Kit.
	 */
	public void setMinimumLevel(int level) {
		this.minimumLevel = level;
	}
	
	/**
	 * Get the author of the Kit.
	 * @return The author of the Kit.
	 */
	public String getAuthor() {
		return author;
	}
	
	/**
	 * Function that generates a preview inventory for the Kit.
	 * @return A preview inventory for the Kit.
	 */
	public Inventory generatePreviewInventory(boolean showBack) {
		int pageSize = (int) (18 + (Math.ceil((double) this.content.size() / 9) * 9));
		Inventory inv = Bukkit.getServer().createInventory(null, pageSize, "[Kit] " + this.name);
		
		for (ItemStack i : this.armor) {
			if (i.getType().toString().toLowerCase().contains("helmet")) {
				inv.setItem(0, i);
			} else if (i.getType().toString().toLowerCase().contains("chestplate") || i.getType().toString().toLowerCase().contains("elytra") || i.getType().toString().toLowerCase().contains("shield")) {
				inv.setItem(1, i);
			} else if (i.getType().toString().toLowerCase().contains("leggings")) {
				inv.setItem(2, i);
			} else if (i.getType().toString().toLowerCase().contains("boots")) {
				inv.setItem(3,  i);
			}
		}
		
		for(int c = 0; c < this.content.size(); c++) {
			if(c < 8) {
				inv.setItem(pageSize - 9 + c, this.content.get(c));
			} else {
				inv.setItem(18 + c, this.content.get(this.content.size() - c - 1));
			}
		}

		if(showBack) {
			inv.setItem(8, ItemStackMaker.start(Material.REDSTONE_BLOCK, 1)
										  .setName(ChatColor.RED + "Go back to the select menu.")
										  .create());	
		} else {
			inv.setItem(8, this.kitItem);
		}

		return inv;
	}
}
