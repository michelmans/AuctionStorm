package com.alchemi.as.cmds;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.alchemi.al.CarbonDating;
import com.alchemi.al.Library;
import com.alchemi.al.Messenger;
import com.alchemi.as.AuctionStorm;
import com.alchemi.as.Queue;

public class CommandAdmin implements CommandExecutor{
	
	public final String return_usage = AuctionStorm.instance.getCommand("asadmin return").getUsage();
	public final String admin_usage = AuctionStorm.instance.getCommand("asadmin").getUsage();
	public final String info_usage = AuctionStorm.instance.getCommand("asadmin info").getUsage();
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (Library.checkCmdPermission(cmd, sender, "as.admin", "asadmin")) {
			CarbonDating datetime = null;
			
			if (args.length == 0) {
				if (sender instanceof Player) Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Command.Wrong-Format") + admin_usage, (Player)sender, ((Player) sender).getDisplayName(), cmd.getName());
				else AuctionStorm.instance.messenger.print(AuctionStorm.instance.messenger.getMessage("Command.Wrong-Format") + admin_usage, AuctionStorm.instance.pluginname, cmd.getName());
				return false;
			}
			
			if (args[0].equalsIgnoreCase("return")) { //return command
				
				if (!sender.hasPermission("as.return")) {
					Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Command.No-Permission"), (Player)sender, ((Player) sender).getDisplayName(), cmd.getName());
					return false;
				
				} else if (!AuctionStorm.config.getBoolean("Auction.LogAuctions")) {
					if (sender instanceof Player) Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Command.Admin.Logging-Disabled"), (Player)sender, ((Player) sender).getDisplayName(), cmd.getName());
					else AuctionStorm.instance.messenger.print(AuctionStorm.instance.messenger.getMessage("Command.Admin.Logging-Disabled"), AuctionStorm.instance.pluginname, cmd.getName());
					return true;
				}
				
				if (args.length < 4) {
					if (sender instanceof Player) Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Command.Wrong-Format") + return_usage, (Player)sender, ((Player) sender).getDisplayName(), cmd.getName());
					else AuctionStorm.instance.messenger.print(AuctionStorm.instance.messenger.getMessage("Command.Wrong-Format") + return_usage, AuctionStorm.instance.pluginname, cmd.getName());
					return false;
				}
				
				if (args.length == 4) datetime = new CarbonDating(args[3]);
				else if (args.length == 8){
					datetime = new CarbonDating(args[3], args[4], args[5], args[6], args[7]);
				}
				
				if (datetime == null || datetime.getCarbonDate() == null) {
					if (sender instanceof Player) Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Command.Wrong-Format") + return_usage, (Player)sender, ((Player) sender).getDisplayName(), cmd.getName());
					else AuctionStorm.instance.messenger.print(AuctionStorm.instance.messenger.getMessage("Command.Wrong-Format") + return_usage, AuctionStorm.instance.pluginname, cmd.getName());
					return false;
				}
				
				if (args[1].equalsIgnoreCase("all")) {
					AuctionStorm.logger.readLog(args[2], datetime).returnAll();
				}
				else if (args[1].equalsIgnoreCase("item")) {
					AuctionStorm.logger.readLog(args[2], datetime).returnItemToSeller();
				}
				else if (args[1].equalsIgnoreCase("money")) {
					AuctionStorm.logger.readLog(args[2], datetime).returnMoneyToBuyer();
				} 
				else {
					if (sender instanceof Player) Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Command.Wrong-Format") + return_usage, (Player)sender, ((Player) sender).getDisplayName(), cmd.getName());
					else AuctionStorm.instance.messenger.print(AuctionStorm.instance.messenger.getMessage("Command.Wrong-Format") + return_usage, AuctionStorm.instance.pluginname, cmd.getName());
					return false;
				}
			}
			
			else if (args[0].equalsIgnoreCase("info")) { //info command
				if (!AuctionStorm.config.getBoolean("Auction.LogAuctions")) {
					if (sender instanceof Player) Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Command.Admin.Logging-Disabled"), (Player)sender, ((Player) sender).getDisplayName(), cmd.getName());
					else AuctionStorm.instance.messenger.print(AuctionStorm.instance.messenger.getMessage("Command.Admin.Logging-Disabled"), AuctionStorm.instance.pluginname, cmd.getName());
					return true;
				}
				
				if (args.length < 3) {
					if (sender instanceof Player) Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Command.Wrong-Format") + info_usage, (Player)sender, ((Player) sender).getDisplayName(), cmd.getName());
					else AuctionStorm.instance.messenger.print(AuctionStorm.instance.messenger.getMessage("Command.Wrong-Format") + info_usage, AuctionStorm.instance.pluginname, cmd.getName());
					return false;
				}
				
				if (args.length == 3) datetime = new CarbonDating(args[2]);
				else if (args.length == 7){
					datetime = new CarbonDating(args[2], args[3], args[4], args[5], args[6]);
				}
				
				if (datetime == null || datetime.getCarbonDate() == null) {
					if (sender instanceof Player) Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Command.Wrong-Format") + info_usage, (Player)sender, ((Player) sender).getDisplayName(), cmd.getName());
					else AuctionStorm.instance.messenger.print(AuctionStorm.instance.messenger.getMessage("Command.Wrong-Format") + info_usage, AuctionStorm.instance.pluginname, cmd.getName());
					return false;
				}
				
				AuctionStorm.logger.readLog(args[1], datetime).getInfo(sender);
			}
			
			else if (args[0].equalsIgnoreCase("reload")) { //reload command
				if (!sender.hasPermission("as.reload")) {
					Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Command.No-Permission"), (Player)sender, ((Player) sender).getDisplayName(), cmd.getName());
					return false;
				} else if (args.length == 2 && AuctionStorm.instance.getFileManager().hasConfig(args[1])) {
					AuctionStorm.instance.getFileManager().reloadConfig(args[1]);
					if (args[1].equalsIgnoreCase("config.yml")) {
						AuctionStorm.config = AuctionStorm.instance.getConfig();
						
						if (Queue.getQueueLength() != 0) {
							Queue.clearQueue(true, "server restarting");
						}
					}
				} else {
					for (String file : AuctionStorm.instance.getFileManager().getFiles().keySet()) {
						AuctionStorm.instance.getFileManager().reloadConfig(file);
						AuctionStorm.config = AuctionStorm.instance.getConfig();
						
						if (Queue.getQueueLength() != 0) {
							Queue.clearQueue(true, "config reload");
						}
					}
				}
				
			}
			
		
		}
		return true;
	}

}
