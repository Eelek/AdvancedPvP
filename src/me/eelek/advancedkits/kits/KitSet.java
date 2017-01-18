package me.eelek.advancedkits.kits;

import java.util.ArrayList;
import java.util.HashMap;

public class KitSet {
	
	static HashMap<String, ArrayList<Kit>> sets = new HashMap<String, ArrayList<Kit>>();
	
	public static ArrayList<Kit> getSet(String s) {
		return sets.get(s);
	}
	
	public static void addSet(String name, ArrayList<Kit> kits) {
		sets.put(name, kits);
	}
	
	public static void removeSet(String name) {
		sets.remove(name);
	}
	
	public static HashMap<String, ArrayList<Kit>> getSets() {
		return sets;
	}

}
