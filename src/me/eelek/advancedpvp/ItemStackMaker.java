package me.eelek.advancedpvp;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemStackMaker {
	
	private ItemStack i;
	
	public ItemStackMaker(ItemStack i) {
		this.i = i;
	}
	
	public static ItemStackMaker start(Material m, int a) {
		return new ItemStackMaker(new ItemStack(m, a));
	}
	
	public ItemStackMaker setName(String name) {
		ItemMeta iMeta = (ItemMeta) i.getItemMeta();
		iMeta.setDisplayName(name);
		i.setItemMeta(iMeta);
		
		return this;
	}
	
	public ItemStackMaker setLore(List<String> lore) {
		ItemMeta iMeta = (ItemMeta) i.getItemMeta();
		iMeta.setLore(lore);
		i.setItemMeta(iMeta);
		
		return this;
	}
	
	public ItemStack create() {
		return i;
	}

}
