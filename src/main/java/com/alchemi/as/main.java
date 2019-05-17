package com.alchemi.as;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;

import com.alchemi.al.configurations.Messenger;
import com.alchemi.al.configurations.SexyConfiguration;
import com.alchemi.al.objects.base.PluginBase;
import com.alchemi.al.objects.handling.UpdateChecker;
import com.alchemi.as.listeners.commands.CommandAdmin;
import com.alchemi.as.listeners.commands.CommandBid;
import com.alchemi.as.listeners.commands.CommandPlayer;
import com.alchemi.as.listeners.events.AdminTabComplete;
import com.alchemi.as.listeners.events.BaseTabComplete;
import com.alchemi.as.listeners.events.BidTabComplete;
import com.alchemi.as.listeners.events.UserLoginHandler;
import com.alchemi.as.objects.AuctionMessenger;
import com.alchemi.as.objects.Config;
import com.alchemi.as.objects.GiveQueue;
import com.alchemi.as.objects.Logging;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

public class main extends PluginBase implements Listener {
	public String pluginname;
	public Economy econ;
	private Permission perm;
	
	public Config config;
	
	private static main instance;
	public static Logging logger;
	public static GiveQueue gq;
	public SexyConfiguration giveQueue;
	
	public UpdateChecker uc;
	
	public static List<Material> banned_items = new ArrayList<Material>();

	@Override
	public void onEnable() {
		instance = this;
		pluginname = getDescription().getName();
		
		SPIGOT_ID = 62778;
		
		setMessenger(new AuctionMessenger(this));
		messenger.print("Enabling AuctionStorm...");
		
		try {
			config = new Config();
			messenger.print("Configs enabled.");
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
			getServer().getPluginManager().disablePlugin(this);
			Messenger.printStatic("Configs enabling errored, disabling plugin.", "&4[DodgeChallenger]");
		}
		
		if (Config.ConfigEnum.CONFIG.getConfig().getBoolean("update-checker", true)) uc = new UpdateChecker(this);
		
		if (!new File(getDataFolder(), "giveQueue.yml").exists()) saveResource("giveQueue.yml", false);
		giveQueue = SexyConfiguration.loadConfiguration(new File(getDataFolder(), "giveQueue.yml"));
		
		if (Config.AUCTION.LOGAUCTIONS.asBoolean()) logger = new Logging("log.yml");
		
		if (!setupEconomy() ) {
			messenger.print("[%s] - Disabled due to no Vault dependency found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
		if (!setupPermission()) {
			messenger.print("No Vault dependency found, silence command disabled!");
		}
		
		gq = new GiveQueue(giveQueue);
		
		//registry
		registerCommands();
		getServer().getPluginManager().registerEvents(new UserLoginHandler(), this);
		
		messenger.print("&1Vworp vworp vworp");
	}
	
	@Override
	public void onDisable() {
		
		if (Queue.getQueueLength() != 0) {
			Queue.clearQueue(true, "a server restart");
		}
		
		messenger.print("&4I don't wanna go...");
		
	}
	
	private void registerCommands() {
		getCommand("auc").setExecutor(new CommandPlayer());
		getCommand("bid").setExecutor(new CommandBid());
		getCommand("asadmin").setExecutor(new CommandAdmin());

		getCommand("auc").setTabCompleter(new BaseTabComplete());
		getCommand("bid").setTabCompleter(new BidTabComplete());
		getCommand("asadmin").setTabCompleter(new AdminTabComplete());
	}
	
	public static boolean hasPermission(Player player, String perm) {
		
		return (player.isOp() || player.hasPermission(perm));
	}
	
	public static boolean hasPermission(CommandSender sender, String perm) {
		return ( !(sender instanceof Player) || sender.isOp() || sender.hasPermission(perm));
	}
	
	private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) return false;
        
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return false;
        
        econ = rsp.getProvider();
        return econ != null;
    }
	
	private boolean setupPermission() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) return false;
		
		RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
		
		if (rsp == null) return false;
		
		perm = rsp.getProvider();
		return perm != null;
	}
	
	public boolean permsEnabled() {
		return perm != null;
	}
	
	public static main getInstance() {
		return instance;
	}
	
	@Override
	public Messenger getMessenger() {
		return messenger;
	}
}
