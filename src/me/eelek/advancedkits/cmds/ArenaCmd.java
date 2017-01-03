package me.eelek.advancedkits.cmds;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.eelek.advancedkits.arena.Arena;
import me.eelek.advancedkits.arena.ArenaManager;

public class ArenaCmd implements CommandExecutor {
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandlabel, String[] args) {
		if (cmd.getName().equalsIgnoreCase("arena")) {
			if (sender instanceof Player) {
				Player p = (Player) sender;

				if (args.length == 0) {
					p.sendMessage(ChatColor.GOLD + "----------" + ChatColor.DARK_GREEN + "< Arena Help Menu >" + ChatColor.GOLD + "----------");
					p.sendMessage(ChatColor.BOLD + "- " + ChatColor.DARK_GRAY + "/arena select <arena name> " + ChatColor.GRAY + "select the corners of an arena.");
				} else if (args.length == 1) {
					if (args[0].equalsIgnoreCase("help")) {
						p.sendMessage(ChatColor.GOLD + "----------" + ChatColor.DARK_GREEN + "< Arena Help Menu >" + ChatColor.GOLD + "----------");
						p.sendMessage(ChatColor.BOLD + "- " + ChatColor.DARK_GRAY + "/arena select <arena name> " + ChatColor.GRAY + "select the corners of an arena.");
					}
				} else if (args.length == 2) {
					if (args[0].equalsIgnoreCase("info")) {
						if (ArenaManager.isArena(args[1])) {
							Arena a = ArenaManager.getArena(args[1]);
							p.sendMessage(ChatColor.GOLD + "Info for arena " + ChatColor.RED + args[1]);
							p.sendMessage(ChatColor.BLUE + "Name: " + ChatColor.AQUA + a.getName());
							p.sendMessage(ChatColor.BLUE + "Max Players: " + ChatColor.AQUA + a.getMaxPlayers());
							p.sendMessage(ChatColor.BLUE + "Level: " + ChatColor.AQUA + a.getLevel());
							if (a.getAmountOfSpawns() != 0) {
								p.sendMessage(ChatColor.BLUE + "Spawns: " + ChatColor.AQUA + a.getAmountOfSpawns());
							} else {
								p.sendMessage(ChatColor.BLUE + "Spawns: " + ChatColor.AQUA + "0");
							}
							p.sendMessage(ChatColor.BLUE + "Active: " + ChatColor.AQUA + a.isActive());
						}
					} else if (args[0].equalsIgnoreCase("inspect")) {
						if (ArenaManager.isArena(args[1])) {
							p.openInventory(ArenaManager.getInventory(ArenaManager.getArena(args[1])));
						}
					} 
				} else if (args.length == 3) {
					if (args[0].equalsIgnoreCase("select")) {
						if (args[1].equalsIgnoreCase("spawns")) {
							if (ArenaManager.isArena(args[2])) {
								p.getInventory().addItem(giveSpawnAxe(args[2]));
								p.sendMessage(ChatColor.BLUE + "Select the " + ChatColor.AQUA + "spawns " + ChatColor.BLUE + "of arena " + ChatColor.AQUA + args[2] + ChatColor.BLUE + ".");
							}
						}
					}
				} else if (args.length == 4) {
					if (ArenaManager.isArena(args[0])) {
						if (args[1].equalsIgnoreCase("set")) {
							if (args[2].equalsIgnoreCase("maxplayers")) {
								try {
									ArenaManager.getArena(args[0]).setMaxPlayers(Integer.parseInt(args[3]));
									p.sendMessage(ChatColor.BLUE + "Succesfully set " + ChatColor.AQUA + args[0] + ChatColor.BLUE + "'s max players to: " + ChatColor.AQUA + args[3]);
								} catch (NumberFormatException e) {
									p.sendMessage(ChatColor.AQUA + args[3] + ChatColor.BLUE + " isn't a valid input.");
								}
							} else if (args[2].equalsIgnoreCase("level")) {
								try {
									ArenaManager.getArena(args[0]).setLevel(Integer.parseInt(args[3]));
									p.sendMessage(ChatColor.BLUE + "Succesfully set " + ChatColor.AQUA + args[0] + ChatColor.BLUE + "'s level to: " + ChatColor.AQUA + args[3]);
									System.out.println(args[0] + " level " + ArenaManager.getArena(args[0]).getLevel());
								} catch (NumberFormatException e) {
									p.sendMessage(ChatColor.AQUA + args[3] + ChatColor.BLUE + " isn't a valid input.");
								}
							}
						}
					}
				}
			}
		}
		
		return true;
	}
	
	public ItemStack giveSpawnAxe(String name) {
		ItemStack select = new ItemStack(Material.DIAMOND_HOE, 1);
		ItemMeta meta = (ItemMeta) select.getItemMeta();
		meta.setDisplayName(ChatColor.BLUE + "Select spawns " + ChatColor.AQUA + name);
		select.setItemMeta(meta);
		return select;
	}

}