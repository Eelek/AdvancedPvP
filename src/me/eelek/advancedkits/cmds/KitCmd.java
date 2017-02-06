package me.eelek.advancedkits.cmds;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import me.eelek.advancedkits.kits.Kit;
import me.eelek.advancedkits.kits.KitManager;

public class KitCmd implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String commandlabel, String[] args) {
		if(cmd.getName().equalsIgnoreCase("kit")) {
			if(sender instanceof Player) {
				Player p = (Player) sender;
				
				if(args.length == 1) {
					if(args[0].equalsIgnoreCase("list")) {
						p.sendMessage(ChatColor.GOLD + "[" + ChatColor.DARK_GREEN + "Advanced Kits" + ChatColor.GOLD + "] " + ChatColor.BLUE + "Possible kits:");
						for(Kit kit : KitManager.getAllKits()) {
							p.sendMessage(ChatColor.GOLD + "- " + ChatColor.GREEN + kit.getName());
						}
					} else if(args[0].equalsIgnoreCase("help")) {
						p.sendMessage(ChatColor.GOLD + "------------" + ChatColor.DARK_GREEN + "< Kit Help Menu >" + ChatColor.GOLD + "------------");
						p.sendMessage(ChatColor.BLACK + "- " + ChatColor.DARK_GRAY + "/kit" + ChatColor.GRAY + " Default command. Links you to here.");
						p.sendMessage(ChatColor.BLACK + "- " + ChatColor.DARK_GRAY + "/kit list" + ChatColor.GRAY + " get a list of all the existing kits.");
						if(p.hasPermission("bluecraft.staff")) {
							p.sendMessage(ChatColor.BLACK + "- " + ChatColor.DARK_GRAY + "/kit help" + ChatColor.GRAY + " opens this.");
							p.sendMessage(ChatColor.BLACK + "- " + ChatColor.DARK_GRAY + "/kit clear" + ChatColor.GRAY + " clears your kit.");
						}
					} else if(args[0].equalsIgnoreCase("clear")) {
						p.getInventory().clear();
						for(PotionEffect pE : p.getActivePotionEffects()) {
							p.removePotionEffect(pE.getType());
						}
						p.setHealth(20.0);
						p.sendMessage(ChatColor.GOLD + "[" + ChatColor.DARK_GREEN + "Advanced Kits" + ChatColor.GOLD + "] " + ChatColor.BLUE + "You have been " + ChatColor.AQUA + "healed" + ChatColor.BLUE + ".");
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
