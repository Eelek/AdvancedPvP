package me.eelek.advancedkits.cmds;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import me.eelek.advancedkits.AKitsMain;
import me.eelek.advancedkits.kits.Kit;
import me.eelek.advancedkits.kits.KitManager;

public class KitCmd implements CommandExecutor {
	
	private AKitsMain plugin;
	
	public KitCmd(AKitsMain plugin) {
		this.plugin = plugin;
	}

	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command cmd, String commandlabel, String[] args) {
		if(cmd.getName().equalsIgnoreCase("kit")) {
			if(sender instanceof Player) {
				Player p = (Player) sender;
				
				if(args.length < 1) {
					p.openInventory(KitManager.getSelectInventory());
				} else if(args.length == 1) {
					if(args[0].equalsIgnoreCase("menu")) {
						p.openInventory(KitManager.getSelectInventory());
					} else if(args[0].equalsIgnoreCase("list")) {
						p.sendMessage(ChatColor.GOLD + "[" + ChatColor.DARK_GREEN + "Advanced Kits" + ChatColor.GOLD + "] " + ChatColor.BLUE + "Possible kits:");
						for(Kit kit : KitManager.getAllKits()) {
							String name = kit.getName();
							p.sendMessage(ChatColor.GOLD + "- " + ChatColor.GREEN + name);
						}
					} else if(args[0].equalsIgnoreCase("help")) {
						p.sendMessage(ChatColor.GOLD + "------------" + ChatColor.DARK_GREEN + "< Kit Help Menu >" + ChatColor.GOLD + "------------");
						p.sendMessage(ChatColor.BLACK + "- " + ChatColor.DARK_GRAY + "/kit" + ChatColor.GRAY + " Default command. Opens up the Kit Select Menu.");
						p.sendMessage(ChatColor.BLACK + "- " + ChatColor.DARK_GRAY + "/kit menu" + ChatColor.GRAY + " Opens up the Kit Select Menu.");
						p.sendMessage(ChatColor.BLACK + "- " + ChatColor.DARK_GRAY + "/kit list" + ChatColor.GRAY + " get a list of all the existing kits.");
						p.sendMessage(ChatColor.BLACK + "- " + ChatColor.DARK_GRAY + "/kit help" + ChatColor.GRAY + " opens this.");
						p.sendMessage(ChatColor.BLACK + "- " + ChatColor.DARK_GRAY + "/kit give <player> <kit>" + ChatColor.GRAY + " gives the selected player the selected kit.");
					} else if(args[0].equalsIgnoreCase("clear")) {
						p.getInventory().clear();
						for(PotionEffect pE : p.getActivePotionEffects()) {
							p.removePotionEffect(pE.getType());
						}
						p.setHealth(20.0);
						p.sendMessage(ChatColor.GOLD + "[" + ChatColor.DARK_GREEN + "Advanced Kits" + ChatColor.GOLD + "] " + ChatColor.BLUE + "You have been " + ChatColor.AQUA + "healed" + ChatColor.BLUE + ".");
					}
				} else if(args.length == 3) {
					if(args[0].equalsIgnoreCase("give")) {
						if(plugin.getServer().getOnlinePlayers().contains(plugin.getServer().getPlayer((args[1])))) {
							if(KitManager.isKit(args[2])) {
								KitManager.giveKit(plugin.getServer().getPlayer(args[1]), KitManager.getKit(args[2]));
								p.sendMessage(ChatColor.GOLD + "[" + ChatColor.DARK_GREEN + "Advanced Kits" + ChatColor.GOLD + "] " + ChatColor.BLUE + "You have given " + ChatColor.AQUA + args[1] + ChatColor.BLUE + " the " + ChatColor.AQUA + args[2] + ChatColor.BLUE + " kit.");
								plugin.getServer().getPlayer(args[1]).sendMessage(ChatColor.GOLD + "[" + ChatColor.DARK_GREEN + "Advanced Kits" + ChatColor.GOLD + "] " + ChatColor.AQUA + p.getPlayerListName() + ChatColor.BLUE + " has given you the " + ChatColor.AQUA + args[2] + ChatColor.BLUE + " kit.");
							} else {
								p.sendMessage(ChatColor.GOLD + "[" + ChatColor.DARK_GREEN + "Advanced Kits" + ChatColor.GOLD + "] " + ChatColor.AQUA + args[2] + ChatColor.BLUE + " isn't a valid kit.");
							}
						}
					}
				}
			}
		}
		
		return true;
	}

}
