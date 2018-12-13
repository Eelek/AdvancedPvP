package me.eelek.advancedpvp.cmds;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.eelek.advancedpvp.arena.Arena;
import me.eelek.advancedpvp.arena.ArenaManager;
import me.eelek.advancedpvp.arena.Spawn;
import me.eelek.advancedpvp.game.GameManager;

public class ArenaCmd implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String commandlabel, String[] args) {
		if (cmd.getName().equalsIgnoreCase("arena")) {
			if (sender instanceof Player) {
				Player p = (Player) sender;

				if (args.length == 1) {
					if (args[0].equalsIgnoreCase("help")) {
						p.sendMessage(ChatColor.GOLD + "----------" + ChatColor.DARK_GREEN + "< Arena Help Menu >" + ChatColor.GOLD + "----------");
						p.sendMessage(ChatColor.BOLD + "- " + ChatColor.DARK_GRAY + "/arena help " + ChatColor.GRAY + "opens this.");
						p.sendMessage(ChatColor.BOLD + "- " + ChatColor.DARK_GRAY + "/arena list " + ChatColor.GRAY + "get a list (and search through) all the arenas.");
						p.sendMessage(ChatColor.BOLD + "- " + ChatColor.DARK_GRAY + "/arena create <arena> " + ChatColor.GRAY + "create an arena.");
						p.sendMessage(ChatColor.BOLD + "- " + ChatColor.DARK_GRAY + "/arena search <query> " + ChatColor.GRAY + "search for an arena.");
						p.sendMessage(ChatColor.BOLD + "- " + ChatColor.DARK_GRAY + "/arena <arena> info " + ChatColor.GRAY + "see the arena info.");
						p.sendMessage(ChatColor.BOLD + "- " + ChatColor.DARK_GRAY + "/arena <arena> inspect " + ChatColor.GRAY + "inspect the settings of an arena.");
						p.sendMessage(ChatColor.BOLD + "- " + ChatColor.DARK_GRAY + "/arena <arena> set maxplayers <maxplayers> " + ChatColor.GRAY + "set the arena's maximum players.");
						p.sendMessage(ChatColor.BOLD + "- " + ChatColor.DARK_GRAY + "/arena <arena> set level <level> " + ChatColor.GRAY + "set the arena's minimum level.");
						p.sendMessage(ChatColor.BOLD + "- " + ChatColor.DARK_GRAY + "/arena <arena> set spawn " + ChatColor.GRAY + "Add your current position as a spawn.");
						p.sendMessage(ChatColor.BOLD + "- " + ChatColor.DARK_GRAY + "/arena <arena> set lobby " + ChatColor.GRAY + "Set your current position as the lobby.");
						p.sendMessage(ChatColor.BOLD + "- " + ChatColor.DARK_GRAY + "/arena <arena> spawn <spawn> set maxspaws <maxspawns> " + ChatColor.GRAY + "Set the max spwans possible on a spawn.");
						p.sendMessage(ChatColor.BOLD + "- " + ChatColor.DARK_GRAY + "/arena <arena> spawn <spawn> remove " + ChatColor.GRAY + "Remove a spawn.");
					} else if (args[0].equalsIgnoreCase("list")) {
						p.openInventory(ArenaManager.getInstance().getArenasInventory(""));
					}
				} else if (args.length == 2) {
					if (args[0].equalsIgnoreCase("info")) {
						if (ArenaManager.getInstance().isArena(args[1])) {
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
							p.sendMessage(ChatColor.BLUE + "Gametype: " + ChatColor.AQUA + a.getType().toString());
						} else {
							p.sendMessage(ChatColor.DARK_RED + args[1] + ChatColor.RED + " isn't an arena.");
						}
					} else if (args[0].equalsIgnoreCase("inspect")) {
						if (ArenaManager.getInstance().isArena(args[1])) {
							p.openInventory(ArenaManager.getInstance().getInventory(ArenaManager.getInstance().getArena(args[1])));
						} else {
							p.sendMessage(ChatColor.DARK_RED + args[1] + ChatColor.RED + " isn't an arena.");
						}
					} else if (args[0].equalsIgnoreCase("create")) {
						if (!args[1].isEmpty()) {
							ArenaManager.getInstance().addArena(new Arena(args[1], p.getWorld(), 0, 0));
							p.sendMessage(ChatColor.BLUE + "Arena " + ChatColor.AQUA + args[1] + ChatColor.BLUE + " has been created.\nPlease check /arena help.\nYou can further configure your arena in the arenas.json file.");
						}
					}
				} else if (args.length == 3) {
					if (args[0].equalsIgnoreCase("select")) {
						if (args[1].equalsIgnoreCase("spawns")) {
							if (ArenaManager.getInstance().isArena(args[2])) {
								ArenaManager.getInstance().getArena(args[2]).addSpawn(p.getEyeLocation());
								p.sendMessage(ChatColor.BLUE + "Your current position has been added as a " + ChatColor.AQUA + "spawn " + ChatColor.BLUE + " for arena " + ChatColor.DARK_AQUA + args[2] + ChatColor.BLUE + ".");
							} else {
								p.sendMessage(ChatColor.DARK_AQUA + args[2] + ChatColor.BLUE + " isn't an arena.");
							}
						} else if (args[1].equalsIgnoreCase("lobby")) {
							if (ArenaManager.getInstance().isArena(args[2])) {
								if (ArenaManager.getInstance().getArena(args[2]).hasLobby() == false) {
									ArenaManager.getInstance().getArena(args[2]).setLobbyLocation(p.getEyeLocation());
									p.sendMessage(ChatColor.BLUE + "Select the " + ChatColor.GOLD + "lobby " + ChatColor.BLUE + "of arena " + ChatColor.AQUA + args[2] + ChatColor.BLUE + ".");
								} else {
									p.sendMessage(ChatColor.DARK_RED + args[2] + ChatColor.RED + " already has a lobby.");
								}
							} else {
								p.sendMessage(ChatColor.DARK_RED + args[1] + ChatColor.RED + " isn't an arena.");
							}
						}
					}
				} else if (args.length == 4) {
					if (ArenaManager.getInstance().isArena(args[0])) {
						Arena a = ArenaManager.getInstance().getArena(args[0]);
						if (args[1].equalsIgnoreCase("set")) {
							if (args[2].equalsIgnoreCase("maxplayers")) {
								try {
									a.setMaxPlayers(Integer.parseInt(args[3]));
									p.sendMessage(ChatColor.BLUE + "Succesfully set " + ChatColor.AQUA + args[0] + ChatColor.BLUE + "'s max players to: " + ChatColor.AQUA + args[3]);
								} catch (NumberFormatException e) {
									p.sendMessage(ChatColor.AQUA + args[3] + ChatColor.BLUE + " isn't a valid input.");
								}
							} else if (args[2].equalsIgnoreCase("level")) {
								try {
									a.setMinimumLevel(Integer.parseInt(args[3]));
									p.sendMessage(ChatColor.BLUE + "Succesfully set " + ChatColor.AQUA + args[0] + ChatColor.BLUE + "'s level to: " + ChatColor.AQUA + args[3]);
								} catch (NumberFormatException e) {
									p.sendMessage(ChatColor.AQUA + args[3] + ChatColor.BLUE + " isn't a valid input.");
								}
							} else if (args[2].equalsIgnoreCase("type")) {
								if (GameManager.getInstance().getType(args[3]) != null) {
									a.setType(GameManager.getInstance().getType(args[3]));
									p.sendMessage(ChatColor.BLUE + "Succesfully set " + ChatColor.AQUA + args[0] + ChatColor.BLUE + "'s game type to: " + ChatColor.AQUA + args[3]);
								} else {
									p.sendMessage(ChatColor.AQUA + args[3] + ChatColor.BLUE + " isn't a valid input.");
								}
							}
						} else if(args[1].equalsIgnoreCase("spawn")) {
							if(a.getSpawn(Integer.parseInt(args[2])) != null) {
								Spawn s = a.getSpawn(Integer.parseInt(args[2]));
								if(args[3].equalsIgnoreCase("remove")) {
									a.removeSpawn(s);
									p.sendMessage(ChatColor.BLUE + "Spawn " + ChatColor.AQUA + args[2] + ChatColor.BLUE + " has been removed from arena " + ChatColor.DARK_AQUA + args[0] + ChatColor.BLUE + ".");
								}
							} else {
								p.sendMessage(ChatColor.DARK_RED + args[2] + ChatColor.RED + " isn't a spawn.");
							}
						}
					} else {
						p.sendMessage(ChatColor.DARK_RED + args[1] + ChatColor.RED + " isn't an arena.");
					}
				} else if(args.length == 6) {
					if (ArenaManager.getInstance().isArena(args[0])) {
						Arena a = ArenaManager.getInstance().getArena(args[0]);
						if(args[1].equalsIgnoreCase("spawn")) {
							try {
								if(a.getSpawn(Integer.parseInt(args[2])) != null) {
									Spawn s = a.getSpawn(Integer.parseInt(args[2]));
									if(args[3].equalsIgnoreCase("set")) {
										if(args[4].equalsIgnoreCase("maxspawns")) {
											try {
												s.setCount(Integer.parseInt(args[5]));
											} catch(NumberFormatException e) {
												p.sendMessage(ChatColor.DARK_RED + args[5] + ChatColor.RED + " isn't a number.");
											}
										}
									}
								} else {
									p.sendMessage(ChatColor.DARK_RED + args[2] + ChatColor.RED + " isn't a spawn in arena " + ChatColor.RED + args[0] + ChatColor.RED + ".");
								}	
							} catch(NumberFormatException e) {
								p.sendMessage(ChatColor.DARK_RED + args[2] + ChatColor.RED + " isn't a number.");
							}
						}
					} else {
						p.sendMessage(ChatColor.DARK_RED + args[0] + ChatColor.RED + " isn't an arena.");
					}
				}
			}
		}
		return true;
	}
}