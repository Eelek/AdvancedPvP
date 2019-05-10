package me.eelek.advancedpvp.cmds;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import me.eelek.advancedpvp.arena.Arena;
import me.eelek.advancedpvp.arena.ArenaManager;
import me.eelek.advancedpvp.kits.Kit;
import me.eelek.advancedpvp.kits.KitManager;
import me.eelek.advancedpvp.players.PlayerManager;

public class KitCmd implements CommandExecutor {

	/**
	 * Kit command.
	 */
	public boolean onCommand(CommandSender sender, Command cmd, String commandlabel, String[] args) {
		if (!cmd.getName().equalsIgnoreCase("kit"))
			return false;
		if (!(sender instanceof Player))
			sender.sendMessage("This command is player-only.");
		
		Player p = (Player) sender;
		
		if (!p.hasPermission("advancedpvp.kits") && !p.hasPermission("advancedpvp.*"))
			p.sendMessage(ChatColor.RED + "You do not have access to this command.");

		if (args.length == 1) {
			if (args[0].equalsIgnoreCase("list")) {
				p.sendMessage(ChatColor.GOLD + "[" + ChatColor.DARK_GREEN + "Advanced PvP" + ChatColor.GOLD + "] " + ChatColor.BLUE + "Possible kits:");
				for (Kit kit : KitManager.getInstance().getAllKits()) {
					p.sendMessage(ChatColor.GOLD + "- " + ChatColor.GREEN + kit.getName() + ChatColor.DARK_GREEN + " by " + ChatColor.DARK_PURPLE + kit.getAuthor());
				}
			} else if (args[0].equalsIgnoreCase("help")) {
				p.sendMessage(ChatColor.GOLD + "------------" + ChatColor.DARK_GREEN + "< Kit Help Menu >" + ChatColor.GOLD + "------------");
				p.sendMessage(ChatColor.BLACK + "- " + ChatColor.DARK_GRAY + "/kit help" + ChatColor.GRAY + " opens this.");
				p.sendMessage(ChatColor.BLACK + "- " + ChatColor.DARK_GRAY + "/kit list" + ChatColor.GRAY + " get a list of all the existing kits.");
				p.sendMessage(ChatColor.BLACK + "- " + ChatColor.DARK_GRAY + "/kit inspect <kit>" + ChatColor.GRAY + " Inspect a kit.");
				p.sendMessage(ChatColor.BLACK + "- " + ChatColor.DARK_GRAY + "/kit clear" + ChatColor.GRAY + " clears your kit.");
				p.sendMessage(ChatColor.BLACK + "- " + ChatColor.DARK_GRAY + "/kit channel" + ChatColor.GRAY + " see your current chat channel.");
				p.sendMessage(ChatColor.BLACK + "- " + ChatColor.DARK_GRAY + "/kit channel list" + ChatColor.GRAY + " see all possible chat channels.");
				p.sendMessage(ChatColor.BLACK + "- " + ChatColor.DARK_GRAY + "/kit channel <channel>" + ChatColor.GRAY + " set your chat channel.");
			} else if (args[0].equalsIgnoreCase("clear")) {
				p.getInventory().clear();
				for (PotionEffect pE : p.getActivePotionEffects()) {
					p.removePotionEffect(pE.getType());
				}
				p.setHealth(20.0);
				p.sendMessage(ChatColor.GOLD + "[" + ChatColor.DARK_GREEN + "Advanced PvP"
						+ ""
						+ ""
						+ "" + ChatColor.GOLD + "] " + ChatColor.BLUE + "You have been " + ChatColor.AQUA + "healed" + ChatColor.BLUE + ".");
			} else if (args[0].equalsIgnoreCase("channel")) {
				p.sendMessage(ChatColor.RED + PlayerManager.getInstance().getPlayer(p.getUniqueId()).getChatChannel() + ChatColor.GOLD + " is your current channel.");
			} else {
				p.sendMessage(ChatColor.RED + "Unknown command. Please use " + ChatColor.DARK_RED + "/kit help" + ChatColor.RED + ".");
			}
		} else if (args.length == 2) {
			if (args[0].equalsIgnoreCase("channel")) {
				if (args[1].equalsIgnoreCase("list")) {
					p.sendMessage(ChatColor.GOLD + "Chat Channels: ");
					p.sendMessage(ChatColor.RED + "- lobby");
					for (Arena a : ArenaManager.getInstance().getArenas()) {
						p.sendMessage(ChatColor.RED + "- " + a.getName());
					}

					p.sendMessage("");
					p.sendMessage(ChatColor.DARK_PURPLE + "-----------STAFF-----------");
					p.sendMessage("");

					p.sendMessage(ChatColor.RED + "- broad " + ChatColor.DARK_RED + "(You hear and receive everything).");
					p.sendMessage(ChatColor.RED + "- staff " + ChatColor.DARK_RED + "(Staff private channel).");
				} else if (args[1].equalsIgnoreCase("lobby") || args[1].equalsIgnoreCase("staff") || ArenaManager.getInstance().getArenaNames().contains(args[1]) || args[1].equalsIgnoreCase("broad")) {
					PlayerManager.getInstance().getPlayer(p.getUniqueId()).setChatChannel(args[1]);
					p.sendMessage(ChatColor.GOLD + "You're now in channel " + ChatColor.RED + args[1] + ChatColor.GOLD + ".");
				} else {
					p.sendMessage(ChatColor.DARK_RED + args[1] + ChatColor.RED + " isn't a channel, or a valid command.");
				}
			} else if (args[0].equalsIgnoreCase("inspect")) {
				if (KitManager.getInstance().getKit(args[1]) != null) {
					PlayerManager.getInstance().getPlayer(p.getUniqueId()).openInventory(KitManager.getInstance().getKit(args[1]).generatePreviewInventory(false), 0, args[1]);
				} else {
					p.sendMessage(ChatColor.RED + "The kit " + ChatColor.DARK_RED + args[1] + ChatColor.RED + " doesn't exist.");
				}
			} else {
				p.sendMessage(ChatColor.GOLD + "Did you mean " + ChatColor.YELLOW + "/kit channel" + ChatColor.GOLD + "?");
			}
		} else {
			p.sendMessage(ChatColor.RED + "Unknown command. Please use " + ChatColor.DARK_RED + "/kit help" + ChatColor.RED + ".");
		}

		return true;
	}
}