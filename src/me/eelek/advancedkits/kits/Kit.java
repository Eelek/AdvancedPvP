package me.eelek.advancedkits.kits;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;

public class Kit {
	
	private String name;
	private ArrayList<ItemStack> content;
	private ArrayList<ItemStack> armor;
	private ItemStack kitItem;
	private ArrayList<PotionEffect> potionEffect;
	private int minimumLevel;
	
	public Kit(String name, ArrayList<ItemStack> content, ArrayList<ItemStack> armor, ItemStack kitItem, int minimumLevel) {
		this.name = name;
		this.content = content;
		this.armor = armor;
		this.kitItem = kitItem;
		this.potionEffect = null;
		this.minimumLevel = minimumLevel;
	}
	
	public Kit(String name, ArrayList<ItemStack> content, ArrayList<ItemStack> armor, ItemStack kitItem, ArrayList<PotionEffect> potionEffect, int minimumLevel) {
		this.name = name;
		this.content = content;
		this.armor = armor;
		this.kitItem = kitItem;
		this.potionEffect = potionEffect;
		this.minimumLevel = minimumLevel;
	}
	
	public String getName() {
		return name;
	}
	
	public ArrayList<ItemStack> getContent() {
		return content;
	}
	
	public ItemStack getItem(int index) {
		return content.get(index);
	}
	
	public ArrayList<ItemStack> getArmor() {
		return armor;
	}
	
	public ItemStack getDisplayItem() {
		ItemMeta kMeta = (ItemMeta) kitItem.getItemMeta();
		kMeta.setDisplayName(ChatColor.DARK_PURPLE + name);
		kMeta.setLore(Arrays.asList("§r§fLeft click to select", "§r§fRight click to see content."));
		kitItem.setItemMeta(kMeta);
		return kitItem;
	}
	
	public ArrayList<PotionEffect> getPotionEffects() {
		return potionEffect;
	}
	
	public int getMinimumLevel() {
		return minimumLevel;
	}
	
	public void setMinimumLevel(int level) {
		this.minimumLevel = level;
	}

}
