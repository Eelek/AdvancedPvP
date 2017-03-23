package me.eelek.advancedkits.kits;

import java.util.ArrayList;
import java.util.HashMap;

public class KitSet {
	
	HashMap<String, ArrayList<Kit>> sets = new HashMap<String, ArrayList<Kit>>();
	
	private static KitSet instance;
	
	protected KitSet() {
		
	}
	
	public static KitSet getInstance() {
		if(instance == null) {
			instance = new KitSet();
		}
		
		return instance;
	}
	
	ArrayList<Kit> getSet(String s) {
		return sets.get(s);
	}
	
	public void addSet(String name, ArrayList<Kit> kits) {
		sets.put(name, kits);
	}
	
	void removeSet(String name) {
		sets.remove(name);
	}
	
	public HashMap<String, ArrayList<Kit>> getSets() {
		return sets;
	}

}
