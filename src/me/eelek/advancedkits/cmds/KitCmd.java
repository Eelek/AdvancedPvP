package me.eelek.advancedkits.cmds;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import me.eelek.advancedkits.arena.Arena;
import me.eelek.advancedkits.arena.ArenaManager;
import me.eelek.advancedkits.kits.Kit;
import me.eelek.advancedkits.kits.KitManager;
import me.eelek.advancedkits.players.PlayerHandler;

public class KitCmd implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String commandlabel, String[] args) {
		if(cmd.getName().equalsIgnoreCase("kit")) {
			if(sender instanceof Player) {
				Player p = (Player) sender;
				
				if(args.length == 1) {
					if(args[0].equalsIgnoreCase("list")) {
						if(p.hasPermission("bluecraft.staff")) {
							p.sendMessage(ChatColor.GOLD + "[" + ChatColor.DARK_GREEN + "Advanced Kits" + ChatColor.GOLD + "] " + ChatColor.BLUE + "Possible kits:");
							for(Kit kit : KitManager.getInstance().getAllKits()) {
								p.sendMessage(ChatColor.GOLD + "- " + ChatColor.GREEN + kit.getName());
							}
						} else {
							p.sendMessage(ChatColor.RED + "Use /kit help");
						}
					} else if(args[0].equalsIgnoreCase("help")) {
						p.sendMessage(ChatColor.GOLD + "------------" + ChatColor.DARK_GREEN + "< Kit Help Menu >" + ChatColor.GOLD + "------------");
						p.sendMessage(ChatColor.BLACK + "- " + ChatColor.DARK_GRAY + "/kit" + ChatColor.GRAY + " Default command. Links you to here.");
						if(p.hasPermission("bluecraft.staff")) {
							p.sendMessage(ChatColor.BLACK + "- " + ChatColor.DARK_GRAY + "/kit list" + ChatColor.GRAY + " get a list of all the existing kits.");
							p.sendMessage(ChatColor.BLACK + "- " + ChatColor.DARK_GRAY + "/kit help" + ChatColor.GRAY + " opens this.");
							p.sendMessage(ChatColor.BLACK + "- " + ChatColor.DARK_GRAY + "/kit clear" + ChatColor.GRAY + " clears your kit.");
							p.sendMessage(ChatColor.BLACK + "- " + ChatColor.DARK_GRAY + "/kit channel" + ChatColor.GRAY + " see your current chat channel.");
							p.sendMessage(ChatColor.BLACK + "- " + ChatColor.DARK_GRAY + "/kit channel list" + ChatColor.GRAY + " see all possible chat channels.");
							p.sendMessage(ChatColor.BLACK + "- " + ChatColor.DARK_GRAY + "/kit channel <channel>" + ChatColor.GRAY + " set your chat channel.");
						}
					} else if(args[0].equalsIgnoreCase("clear")) {
						p.getInventory().clear();
						for(PotionEffect pE : p.getActivePotionEffects()) {
							p.removePotionEffect(pE.getType());
						}
						p.setHealth(20.0);
						p.sendMessage(ChatColor.GOLD + "[" + ChatColor.DARK_GREEN + "Advanced Kits" + ChatColor.GOLD + "] " + ChatColor.BLUE + "You have been " + ChatColor.AQUA + "healed" + ChatColor.BLUE + ".");
					} else if(args[0].equalsIgnoreCase("channel")) {
						p.sendMessage(ChatColor.RED  + PlayerHandler.getPlayer(p.getPlayerListName()).getChatChannel() + ChatColor.GOLD + " is your current channel.");
					} else if(args[0].equalsIgnoreCase("kd")) {
						p.sendMessage(ChatColor.DARK_RED + "" + PlayerHandler.getPlayer(p.getPlayerListName()).calculateKDR());
					} else {
						p.sendMessage(ChatColor.RED + "Use /kit help");
					}
				} else if(args.length == 2) {
					if(p.hasPermission("bluecraft.staff")) {
						if(args[0].equalsIgnoreCase("channel")) {
							if(args[1].equalsIgnoreCase("list")) {
								p.sendMessage(ChatColor.GOLD + "Chat Channels: ");
								p.sendMessage(ChatColor.RED + "- lobby");
								for(Arena a : ArenaManager.getInstance().getArenas()) {
									p.sendMessage(ChatColor.RED + "- " + a.getName());
								}
								
								p.sendMessage("");
								p.sendMessage(ChatColor.DARK_PURPLE + "-----------STAFF-----------");
								p.sendMessage("");
								
								p.sendMessage(ChatColor.RED + "- broad " + ChatColor.DARK_RED + "(You hear and receive everything).");
								p.sendMessage(ChatColor.RED + "- staff " + ChatColor.DARK_RED + "(Staff private channel).");
							} else if(args[1].equalsIgnoreCase("lobby") || args[1].equalsIgnoreCase("staff") || ArenaManager.getInstance().getArenaNames().contains(args[1]) || args[1].equalsIgnoreCase("broad")) {
								PlayerHandler.getPlayer(p.getPlayerListName()).setChatChannel(args[1]);
								p.sendMessage(ChatColor.GOLD + "You're now in channel " + ChatColor.RED + args[1] + ChatColor.GOLD + ".");
							} else {
								p.sendMessage(ChatColor.RED + "Use /kit help (" + ChatColor.GOLD + args[1] + ChatColor.RED + " isn't a channel).");
							}
						} else {
							p.sendMessage(ChatColor.RED + "Use /kit help");
						}
					} else {
						p.sendMessage(ChatColor.RED + "Use /kit help");
					}
				} else {
					p.sendMessage(ChatColor.RED + "Use /kit help");
				}
			}
		}
		
		return true;
	}
}