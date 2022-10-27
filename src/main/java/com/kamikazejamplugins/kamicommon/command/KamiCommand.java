package com.kamikazejamplugins.kamicommon.command;

import com.kamikazejamplugins.kamicommon.KamiCommon;
import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class KamiCommand implements TabExecutor {
	@Getter public final JavaPlugin plugin;
	@Getter public final KamiSubCommands kamiSubCommands;
	public KamiCommand(JavaPlugin plugin, KamiSubCommands kamiSubCommands) {
		KamiCommon.setupPlugin(plugin);
		this.plugin = plugin;
		this.kamiSubCommands = kamiSubCommands;
	}

	public abstract String getAdminPermission();

	public abstract void onFailedAdmin(CommandSender sender, String label, String[] args);
	public abstract void onFailedPlayerOnly(CommandSender sender, String label, String[] args);


	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		String arg = (args.length > 0) ? args[0] : "none";
		KamiSubCommand kamiSubCommand = kamiSubCommands.fromName(arg);
		if (kamiSubCommand.requiresAdmin() && !sender.hasPermission(getAdminPermission())) {
			onFailedAdmin(sender, label, args);
			return true;
		}

		//If admin isn't required, check normal permission
		if (!kamiSubCommand.requiresAdmin()) {
			if (!kamiSubCommand.hasPermission(sender)) {
				kamiSubCommands.getNoneSubCommand().performCommand(sender, label, args);
				return false;
			}
		}

		if (kamiSubCommand.requiresPlayer()) {
			if (sender instanceof Player) {
				return kamiSubCommand.performCommand(sender, label, args);
			}else {
				onFailedPlayerOnly(sender, label, args);
				return false;
			}
		}else {
			return kamiSubCommand.performCommand(sender, label, args);
		}
	}
}
