package me.eelek.advancedpvp.cmds;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import me.eelek.advancedpvp.arena.Arena;
import me.eelek.advancedpvp.arena.ArenaManager;
import me.eelek.advancedpvp.arena.Spawn;
import me.eelek.advancedpvp.game.GameManager;
import me.eelek.advancedpvp.game.GameManager.GameType;
import me.eelek.advancedpvp.kits.KitManager;
import me.eelek.advancedpvp.players.GamePlayer;
import me.eelek.advancedpvp.players.PlayerManager;

public class ArenaCmd implements CommandExecutor {

	/**
	 * Arena Command.
	 */
	public boolean onCommand(CommandSender sender, Command cmd, String commandlabel, String[] args) {
		if (cmd.getName().equalsIgnoreCase("arena")) {
			if (sender instanceof Player) {
				Player p = (Player) sender;
				if (p.hasPermission("advancedpvp.arenas") || p.hasPermission("advancedpvp.*")) {
					
					if (args.length == 1) 
					{
						if (args[0].equalsIgnoreCase("help"))
						{
							p.sendMessage(ChatColor.GOLD + "----------" + ChatColor.DARK_GREEN + "< Arena Help Menu >" + ChatColor.GOLD + "----------");
							p.sendMessage(ChatColor.BOLD + "- " + ChatColor.DARK_GRAY + "/arena help " + ChatColor.GRAY + "opens this.");
							p.sendMessage(ChatColor.BOLD + "- " + ChatColor.DARK_GRAY + "/arena list " + ChatColor.GRAY + "get a list (and search through) all the arenas marked with their active state.");
							p.sendMessage(ChatColor.BOLD + "- " + ChatColor.DARK_GRAY + "/arena list type " + ChatColor.GRAY + "get a list (and search through) all the arenas marked with their type.");
							p.sendMessage(ChatColor.BOLD + "- " + ChatColor.DARK_GRAY + "/arena leave " + ChatColor.GRAY + "leave your current arena.");
							p.sendMessage(ChatColor.BOLD + "- " + ChatColor.DARK_GRAY + "/arena create <arena> " + ChatColor.GRAY + "create an arena.");
							p.sendMessage(ChatColor.BOLD + "- " + ChatColor.DARK_GRAY + "/arena inspect <arena> " + ChatColor.GRAY + "inspect the settings of an arena.");
							p.sendMessage(ChatColor.BOLD + "- " + ChatColor.DARK_GRAY + "/arena <arena> set maxplayers <maxplayers> " + ChatColor.GRAY + "set the arena's maximum players.");
							p.sendMessage(ChatColor.BOLD + "- " + ChatColor.DARK_GRAY + "/arena <arena> set level <level> " + ChatColor.GRAY + "set the arena's minimum level.");
							p.sendMessage(ChatColor.BOLD + "- " + ChatColor.DARK_GRAY + "/arena <arena> set type <type> " + ChatColor.GRAY + "Set your current position as the lobby.");
							p.sendMessage(ChatColor.BOLD + "- " + ChatColor.DARK_GRAY + "/arena <arena> set spawn " + ChatColor.GRAY + "Add your current position as a spawn.");
							p.sendMessage(ChatColor.BOLD + "- " + ChatColor.DARK_GRAY + "/arena <arena> set lobby " + ChatColor.GRAY + "Set your current position as the lobby.");
						    p.sendMessage(ChatColor.BOLD + "- " + ChatColor.DARK_GRAY + "/arena <arena> set item " + ChatColor.GRAY + "Set the arena's display item to the item you are holding.");
							p.sendMessage(ChatColor.BOLD + "- " + ChatColor.DARK_GRAY + "/arena <arena> add <kit> " + ChatColor.GRAY + "Add a kit to the arena.");
							p.sendMessage(ChatColor.BOLD + "- " + ChatColor.DARK_GRAY + "/arena <arena> spawn <spawn> set maxspawns <maxspawns> " + ChatColor.GRAY + "Set the max spwans possible on a spawn.");
							p.sendMessage(ChatColor.BOLD + "- " + ChatColor.DARK_GRAY + "/arena <arena> spawn <spawn> remove " + ChatColor.GRAY + "Remove a spawn.");
						}
						else if (args[0].equalsIgnoreCase("list"))
						{
							PlayerManager.getInstance().getPlayer(p.getUniqueId()).openInventory(ArenaManager.getInstance().generateArenasInventory(0, false), 0, "");
						}
						else if (args[0].equalsIgnoreCase("leave"))
						{
							if(PlayerManager.getInstance().getPlayer(p.getUniqueId()).isPlaying()) {
								GamePlayer player = PlayerManager.getInstance().getPlayer(p.getUniqueId());
								Arena a = ArenaManager.getInstance().getArena(player.getCurrentArena());
								p.teleport(p.getWorld().getSpawnLocation());
								a.getSpawn(p.getPlayerListName()).resetSpawn();
								player.setPlaying(false);
								player.setCurrentArena(null);
								a.removePlayer(player);
								p.getInventory().clear();
								p.getInventory().setItem(4, PlayerManager.getInstance().getArenaCompass());
						        p.getInventory().setHeldItemSlot(4);
								for (PotionEffect pE : p.getActivePotionEffects()) {
									p.removePotionEffect(pE.getType());
								}

								if (!player.getChatChannel().equals("staff") && !player.getChatChannel().equals("broad")) {
									player.setChatChannel("lobby");
								}
	
							}
							else
							{
								p.sendMessage(ChatColor.RED + "You are currently not in an arena.");
							}
						}
						else
						{
							p.sendMessage(ChatColor.GOLD + "Did you mean: " + ChatColor.YELLOW + "/arena (help" + ChatColor.GOLD + " or " + ChatColor.YELLOW + "list)" + ChatColor.GOLD + "?");
						}
					} 
					else if (args.length == 2)
					{
						if (args[0].equalsIgnoreCase("inspect"))
						{
							if (ArenaManager.getInstance().getArena(args[1]) != null)
							{
								if(ArenaManager.getInstance().getArena(args[1]).isSetup() == true)
								{
									PlayerManager.getInstance().getPlayer(p.getUniqueId()).openInventory(ArenaManager.getInstance().getArena(args[1]).generateInventory(), 0, args[1]);
								}
								else
								{
								    p.sendMessage(ChatColor.BLUE + "Arena " + ChatColor.AQUA + args[1] + ChatColor.BLUE + " hasn't been setup yet. Use " + ChatColor.DARK_AQUA + "/arena help" + ChatColor.BLUE + ".");	
								} 
							}
							else
							{
								p.sendMessage(ChatColor.DARK_RED + args[1] + ChatColor.RED + " isn't an arena.");
							}
						}
						else if (args[0].equalsIgnoreCase("create"))
						{
							if (ArenaManager.getInstance().getArena(args[1]) == null)
							{
								ArenaManager.getInstance().addArena(new Arena(args[1], p.getWorld(), 0, 0));
								p.sendMessage(ChatColor.BLUE + "Arena " + ChatColor.AQUA + args[1] + ChatColor.BLUE + " has been created.");
							}
							else
							{
								p.sendMessage(ChatColor.RED + "This name is already taken.");
							}
						}
						else if (args[0].equalsIgnoreCase("list"))
						{
							if(args[1].equalsIgnoreCase("type")) {
								PlayerManager.getInstance().getPlayer(p.getUniqueId()).openInventory(ArenaManager.getInstance().generateArenasInventory(0, true), 0, "type");
							}
							else
							{
								p.sendMessage(ChatColor.RED + "Use: "+ ChatColor.DARK_RED + "/arena list type" + ChatColor.RED + ".");
							}
						}
						else
						{
							p.sendMessage(ChatColor.RED + "Unknown command. Please use "+ ChatColor.DARK_RED + "/arena help" + ChatColor.RED + ".");
						}
					}
					else if (args.length == 3)
					{
						if (ArenaManager.getInstance().getArena(args[0]) != null)
						{
							Arena a = ArenaManager.getInstance().getArena(args[0]);
							if (args[1].equalsIgnoreCase("set"))
							{
								if (args[2].equalsIgnoreCase("spawn"))
								{
									a.addSpawn(p.getLocation());
									p.sendMessage(ChatColor.BLUE + "Your current position has been added as a " + ChatColor.AQUA + "spawn " + ChatColor.BLUE + "for arena " + ChatColor.DARK_AQUA + args[0] + ChatColor.BLUE + ".");
								} 
								else if (args[2].equalsIgnoreCase("lobby"))
								{
									if (!a.hasLobby()) 
									{
										a.setLobbyLocation(p.getLocation());
										p.sendMessage(ChatColor.BLUE + "Your current position is now the " + ChatColor.GOLD + "lobby " + ChatColor.BLUE + "of arena " + ChatColor.AQUA + args[0] + ChatColor.BLUE + ".");
									} 
									else
									{
										p.sendMessage(ChatColor.DARK_RED + args[0] + ChatColor.RED + " already has a lobby.");
									}
								}
								else if(args[2].equalsIgnoreCase("item")) 
								{
								  a.setDisplayItem(p.getInventory().getItemInMainHand().getType());
								  p.sendMessage(ChatColor.BLUE + "The display item of arena " + ChatColor.DARK_AQUA + a.getName() + ChatColor.BLUE + " is now: " + ChatColor.AQUA + p.getInventory().getItemInMainHand().getType().toString().toLowerCase() + ChatColor.BLUE + ".");
								}
								else
								{
									p.sendMessage(ChatColor.GOLD + "Did you mean: " + ChatColor.YELLOW + "/arena " + args[0] + " set" + ChatColor.GOLD + "?");
								}
							}
							else if(args[1].equalsIgnoreCase("add"))
							{
								if (KitManager.getInstance().getKit(args[2]) != null)
								{
									a.getKitSet().add(KitManager.getInstance().getKit(args[2]));
									p.sendMessage(ChatColor.BLUE + "Kit " + ChatColor.AQUA + args[2] + ChatColor.BLUE + " was added to arena " + ChatColor.DARK_AQUA + args[0] + ChatColor.BLUE + ".");
								}
								else
								{
									p.sendMessage(ChatColor.RED + "Unknown kit " + ChatColor.DARK_RED + args[2] + ChatColor.RED + ". Use /kit list.");
								}
						    } 
							else
							{
						    	p.sendMessage(ChatColor.GOLD + "Did you mean: " + ChatColor.YELLOW + "/arena " + args[0] + " (set" + ChatColor.GOLD + " or " + ChatColor.YELLOW + "add)" + ChatColor.GOLD + "?");
							}
						} 
						else
						{
							p.sendMessage(ChatColor.DARK_RED + args[0] + ChatColor.RED + " isn't an arena.");
						}
					}
					else if (args.length == 4)
					{
						if (ArenaManager.getInstance().getArena(args[0]) != null)
						{
							Arena a = ArenaManager.getInstance().getArena(args[0]);
							if (args[1].equalsIgnoreCase("set"))
							{
								if (args[2].equalsIgnoreCase("maxplayers"))
								{
									try {
										a.setMaxPlayers(Integer.parseInt(args[3]));
										p.sendMessage(ChatColor.BLUE + "Succesfully set " + ChatColor.AQUA + args[0] + ChatColor.BLUE + "'s max players to: " + ChatColor.AQUA + args[3]);
									} catch (NumberFormatException e) {
										p.sendMessage(ChatColor.AQUA + args[3] + ChatColor.BLUE + " isn't a valid input.");
									}
								}
								else if (args[2].equalsIgnoreCase("level"))
								{
									try {
										a.setMinimumLevel(Integer.parseInt(args[3]));
										p.sendMessage(ChatColor.BLUE + "Succesfully set " + ChatColor.AQUA + args[0] + ChatColor.BLUE + "'s level to: " + ChatColor.AQUA + args[3]);
									} catch (NumberFormatException e) {
										p.sendMessage(ChatColor.AQUA + args[3] + ChatColor.BLUE + " isn't a valid input.");
									}
								}
								else if (args[2].equalsIgnoreCase("type"))
								{
									if (GameManager.getInstance().getType(args[3]) != null)
									{
										a.setType(GameManager.getInstance().getType(args[3]));
										p.sendMessage(ChatColor.BLUE + "Succesfully set " + ChatColor.AQUA + args[0] + ChatColor.BLUE + "'s game type to: " + ChatColor.AQUA + args[3]);
									}
									else
									{
										p.sendMessage(ChatColor.AQUA + args[3] + ChatColor.BLUE + " isn't a valid input. Valid inputs are:\n");
										for (GameType type : GameType.values()) {
											p.sendMessage(ChatColor.GRAY + "- " + ChatColor.DARK_AQUA + type.toString() + "\n");
										}
									}
								}
								else
								{
									p.sendMessage(ChatColor.RED + "Unknown command. Please use "+ ChatColor.DARK_RED + "/arena help" + ChatColor.RED + ".");
								}
							} 
							else if (args[1].equalsIgnoreCase("spawn"))
							{
								if (a.getSpawn(Integer.parseInt(args[2])) != null)
								{
									Spawn s = a.getSpawn(Integer.parseInt(args[2]));
									if (args[3].equalsIgnoreCase("remove"))
									{
										a.removeSpawn(s);
										p.sendMessage(ChatColor.BLUE + "Spawn " + ChatColor.AQUA + args[2] + ChatColor.BLUE + " has been removed from arena " + ChatColor.DARK_AQUA + args[0] + ChatColor.BLUE + ".");
									}
								}
								else
								{
									p.sendMessage(ChatColor.DARK_RED + args[2] + ChatColor.RED + " isn't a spawn.");
								}
							} 
							else
							{
								p.sendMessage(ChatColor.GOLD + "Did you mean: " + ChatColor.YELLOW + "/arena " + args[0] + " (set" + ChatColor.GOLD + " or " + ChatColor.YELLOW + "spawn)" + ChatColor.GOLD + "?");
							}
						} 
						else
						{
							p.sendMessage(ChatColor.DARK_RED + args[1] + ChatColor.RED + " isn't an arena.");
						}
					}
					else if (args.length == 5) 
					{ 
						p.sendMessage(ChatColor.RED + "Unknown command. Please use "+ ChatColor.DARK_RED + "/arena help" + ChatColor.RED + "."); 
					}
					else if (args.length == 6)
					{
						if (ArenaManager.getInstance().getArena(args[0]) != null) 
						{
							Arena a = ArenaManager.getInstance().getArena(args[0]);
							if (args[1].equalsIgnoreCase("spawn"))
							{
								try {
									if (a.getSpawn(Integer.parseInt(args[2])-1) != null)
									{
										Spawn s = a.getSpawn(Integer.parseInt(args[2])-1);
										if (args[3].equalsIgnoreCase("set")) 
										{
											if (args[4].equalsIgnoreCase("maxspawns"))
											{
												try {
													s.setCount(Integer.parseInt(args[5]));
													p.sendMessage(ChatColor.BLUE + "The spawn has been updated.");
												} catch (NumberFormatException e) {
													p.sendMessage(ChatColor.DARK_RED + args[5] + ChatColor.RED + " isn't a number.");
												}
											}
											else
											{
												p.sendMessage(ChatColor.GOLD + "Did you mean: " + ChatColor.YELLOW + "/arena " + args[0] + " spawn " + args[2] + "set maxspawns" + ChatColor.GOLD + "?");
											}
										} 
										else
										{
											p.sendMessage(ChatColor.GOLD + "Did you mean: " + ChatColor.YELLOW + "/arena " + args[0] + " spawn " + args[2] + "set" + ChatColor.GOLD + "?");
										}
									} 
									else
									{
										p.sendMessage(ChatColor.DARK_RED + args[2] + ChatColor.RED + " isn't a spawn in arena " + ChatColor.RED + args[0] + ChatColor.RED + ".");
									}
								} catch (NumberFormatException e) {
									p.sendMessage(ChatColor.DARK_RED + args[2] + ChatColor.RED + " isn't a number.");
								}
							} 
							else
							{
								p.sendMessage(ChatColor.GOLD + "Did you mean: " + ChatColor.YELLOW + "/arena " + args[0] + " spawn" + ChatColor.GOLD + "?");
							}
						} 
						else
						{
							p.sendMessage(ChatColor.DARK_RED + args[0] + ChatColor.RED + " isn't an arena.");
						}
					} 
					else
					{
						p.sendMessage(ChatColor.RED + "Unknown command. Please use "+ ChatColor.DARK_RED + "/arena help" + ChatColor.RED + ".");
					}
				} 
				else
				{
					p.sendMessage(ChatColor.RED + "You do not have access to this command.");
				}
			} 
			else
			{
				sender.sendMessage("This command is player-only.");
			}
		}
		
		return true;
	}
}