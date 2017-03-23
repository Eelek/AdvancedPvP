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

				if(p.hasPermission("bluecraft.staff")) {
					if (args.length == 0) {
						p.sendMessage(ChatColor.GOLD + "----------" + ChatColor.DARK_GREEN + "< Arena Help Menu >" + ChatColor.GOLD + "----------");
						p.sendMessage(ChatColor.BOLD + "- " + ChatColor.DARK_GRAY + "/arena help " + ChatColor.GRAY + "opens this.");
						if(p.hasPermission("bluecraft.kits.managearena")) {
							p.sendMessage(ChatColor.BOLD + "- " + ChatColor.DARK_GRAY + "/arena info " + ChatColor.GRAY + "see the arena info.");
							p.sendMessage(ChatColor.BOLD + "- " + ChatColor.DARK_GRAY + "/arena inspect " + ChatColor.GRAY + "inspect and open an arena.");
							p.sendMessage(ChatColor.BOLD + "- " + ChatColor.DARK_GRAY + "/arena list " + ChatColor.GRAY + "get a list (and search through) all the arenas.");
							p.sendMessage(ChatColor.BOLD + "- " + ChatColor.DARK_GRAY + "/arena create <arena> " + ChatColor.GRAY + "create an arena.");
							p.sendMessage(ChatColor.BOLD + "- " + ChatColor.DARK_GRAY + "/arena select spawns <arena> " + ChatColor.GRAY + "select the arena spawns.");
							p.sendMessage(ChatColor.BOLD + "- " + ChatColor.DARK_GRAY + "/arena select lobby <arena>  " + ChatColor.GRAY + "select the arena lobby location.");
							p.sendMessage(ChatColor.BOLD + "- " + ChatColor.DARK_GRAY + "/arena <arena> set maxplayers <maxplayers> " + ChatColor.GRAY + "set the arena's maximum players.");
							p.sendMessage(ChatColor.BOLD + "- " + ChatColor.DARK_GRAY + "/arena <arena> set level <level> " + ChatColor.GRAY + "set the arena's minimum level.");
						}
					} else if (args.length == 1) {
						if (args[0].equalsIgnoreCase("help")) {
							p.sendMessage(ChatColor.GOLD + "----------" + ChatColor.DARK_GREEN + "< Arena Help Menu >" + ChatColor.GOLD + "----------");
							p.sendMessage(ChatColor.BOLD + "- " + ChatColor.DARK_GRAY + "/arena help " + ChatColor.GRAY + "opens this.");
							if(p.hasPermission("bluecraft.kits.managearena")) {
								p.sendMessage(ChatColor.BOLD + "- " + ChatColor.DARK_GRAY + "/arena info " + ChatColor.GRAY + "see the arena info.");
								p.sendMessage(ChatColor.BOLD + "- " + ChatColor.DARK_GRAY + "/arena inspect " + ChatColor.GRAY + "inspect and open an arena.");
								p.sendMessage(ChatColor.BOLD + "- " + ChatColor.DARK_GRAY + "/arena list " + ChatColor.GRAY + "get a list (and search through) all the arenas.");
								p.sendMessage(ChatColor.BOLD + "- " + ChatColor.DARK_GRAY + "/arena create <arena> " + ChatColor.GRAY + "create an arena.");
								p.sendMessage(ChatColor.BOLD + "- " + ChatColor.DARK_GRAY + "/arena select spawns <arena> " + ChatColor.GRAY + "select the arena spawns.");
								p.sendMessage(ChatColor.BOLD + "- " + ChatColor.DARK_GRAY + "/arena select lobby <arena>  " + ChatColor.GRAY + "select the arena lobby location.");
								p.sendMessage(ChatColor.BOLD + "- " + ChatColor.DARK_GRAY + "/arena <arena> set maxplayers <maxplayers> " + ChatColor.GRAY + "set the arena's maximum players.");
								p.sendMessage(ChatColor.BOLD + "- " + ChatColor.DARK_GRAY + "/arena <arena> set level <level> " + ChatColor.GRAY + "set the arena's minimum level.");
							}
						} else if(args[0].equalsIgnoreCase("list")) {
							if(p.hasPermission("bluecraft.kits.managearena")) {
								p.openInventory(ArenaManager.getInstance().getArenasInventory("all"));
							} else {
								p.sendMessage(ChatColor.RED + "Use /arena help");
							}
						}
					} else if (args.length == 2) {
						if (args[0].equalsIgnoreCase("info")) {
							if (ArenaManager.getInstance().isArena(args[1])) {
								if(p.hasPermission("bluecraft.staff")) {
									Arena a = ArenaManager.getInstance().getArena(args[1]);
									p.sendMessage(ChatColor.GOLD + "Info for arena " + ChatColor.RED + args[1]);
									p.sendMessage(ChatColor.BLUE + "Name: " + ChatColor.AQUA + a.getName());
									p.sendMessage(ChatColor.BLUE + "Max Players: " + ChatColor.AQUA + a.getMaxPlayers());
									p.sendMessage(ChatColor.BLUE + "Level: " + ChatColor.AQUA + a.getMinimumLevel());
									if (a.getAmountOfSpawns() != 0) {
										p.sendMessage(ChatColor.BLUE + "Spawns: " + ChatColor.AQUA + a.getAmountOfSpawns());
									} else {
										p.sendMessage(ChatColor.BLUE + "Spawns: " + ChatColor.AQUA + "0");
									}
									p.sendMessage(ChatColor.BLUE + "Active: " + ChatColor.AQUA + a.isActive());
									p.sendMessage(ChatColor.BLUE + "Current Players: " + ChatColor.AQUA + a.getCurrentPlayers().size());
									p.sendMessage(ChatColor.BLUE + "Kit set: " + ChatColor.AQUA + a.getKitSetName());
									p.sendMessage(ChatColor.BLUE + "Gametype: " + ChatColor.AQUA + a.getType().toString());
								}
							}
						} else if (args[0].equalsIgnoreCase("inspect")) {
							if(p.hasPermission("bluecraft.kits.managearena")) {
								if (ArenaManager.getInstance().isArena(args[1])) {
									p.openInventory(ArenaManager.getInstance().getInventory(ArenaManager.getInstance().getArena(args[1])));
								} else {
									p.sendMessage(ChatColor.DARK_RED + args[1] + ChatColor.RED +  " isn't an arena.");
								}
							} else {
								p.sendMessage(ChatColor.RED + "Use /arena help");
							}
						} else if(args[0].equalsIgnoreCase("create")) {
							if(!args[1].isEmpty()) {
								ArenaManager.getInstance().addArena(new Arena(args[1], p.getWorld(), 0, 0));
								p.sendMessage(ChatColor.BLUE + "Arena " + ChatColor.AQUA + args[1] + ChatColor.BLUE + " has been created.\nPlease specify the spawns, the lobby location, the max players and the level.");
							}
						}
					} else if (args.length == 3) {
						if(p.hasPermission("bluecraft.kits.managearena")) {
							if (args[0].equalsIgnoreCase("select")) {
								if (args[1].equalsIgnoreCase("spawns")) {
									if (ArenaManager.getInstance().isArena(args[2])) {
										p.getInventory().addItem(giveSpawnAxe(args[2]));
										p.sendMessage(ChatColor.BLUE + "Select the " + ChatColor.AQUA + "spawns " + ChatColor.BLUE + "of arena " + ChatColor.AQUA + args[2] + ChatColor.BLUE + ".");
									}
								} else if(args[1].equalsIgnoreCase("lobby")) {
									if(ArenaManager.getInstance().isArena(args[2])) {
										if(ArenaManager.getInstance().getArena(args[2]).hasLobby() == false) {
											p.getInventory().addItem(giveLobbyAxe(args[2]));
											p.sendMessage(ChatColor.BLUE + "Select the " + ChatColor.GOLD + "lobby " + ChatColor.BLUE + "of arena " + ChatColor.AQUA + args[2] + ChatColor.BLUE + ".");
										} else {
											p.sendMessage(ChatColor.DARK_RED + args[2] + ChatColor.RED +  " already has a lobby.");
										}
									} else {
										p.sendMessage(ChatColor.DARK_RED + args[1] + ChatColor.RED +  " isn't an arena.");
									}
								} else {
									p.sendMessage(ChatColor.RED + "Use /arena help");
								}
							} else {
								p.sendMessage(ChatColor.RED + "Use /arena help");
							}
						} else {
							p.sendMessage(ChatColor.RED + "Use /arena help");
						}
					} else if (args.length == 4) {
						if(p.hasPermission("bluecraft.kits.managearena")) {
							if (ArenaManager.getInstance().isArena(args[0])) {
								if (args[1].equalsIgnoreCase("set")) {
									if (args[2].equalsIgnoreCase("maxplayers")) {
										try {
											ArenaManager.getInstance().getArena(args[0]).setMaxPlayers(Integer.parseInt(args[3]));
											p.sendMessage(ChatColor.BLUE + "Succesfully set " + ChatColor.AQUA + args[0] + ChatColor.BLUE + "'s max players to: " + ChatColor.AQUA + args[3]);
										} catch (NumberFormatException e) {
											p.sendMessage(ChatColor.AQUA + args[3] + ChatColor.BLUE + " isn't a valid input.");
										}
									} else if (args[2].equalsIgnoreCase("level")) {
										try {
											ArenaManager.getInstance().getArena(args[0]).setMinimumLevel(Integer.parseInt(args[3]));
											p.sendMessage(ChatColor.BLUE + "Succesfully set " + ChatColor.AQUA + args[0] + ChatColor.BLUE + "'s level to: " + ChatColor.AQUA + args[3]);
											System.out.println(args[0] + " level " + ArenaManager.getInstance().getArena(args[0]).getMinimumLevel());
										} catch (NumberFormatException e) {
											p.sendMessage(ChatColor.AQUA + args[3] + ChatColor.BLUE + " isn't a valid input.");
										}
									} else {
										p.sendMessage(ChatColor.RED + "Use /arena help");
									}
								} else {
									p.sendMessage(ChatColor.RED + "Use /arena help");
								}
							} else {
								p.sendMessage(ChatColor.DARK_RED + args[1] + ChatColor.RED +  " isn't an arena.");
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
	
	public ItemStack giveLobbyAxe(String name) {
		ItemStack select = new ItemStack(Material.GOLD_HOE, 1);
		ItemMeta meta = (ItemMeta) select.getItemMeta();
		meta.setDisplayName(ChatColor.BLUE + "Select lobby " + ChatColor.AQUA + name);
		select.setItemMeta(meta);
		return select;
	}

}