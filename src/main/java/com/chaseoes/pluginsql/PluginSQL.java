package com.chaseoes.pluginsql;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.chaseoes.pluginsql.utilities.SQLUtilities;

public class PluginSQL extends JavaPlugin {

	public void onEnable() {
		getConfig().options().copyDefaults(true);
		saveConfig();
		SQLUtilities.getUtilities().setup(this);
		update();
	}

	public void onDisable() {
		reloadConfig();
		saveConfig();
	}
	
	public void update() {
		getServer().getScheduler().runTaskLater(this, new Runnable() {
			public void run() {
				for (Plugin p : getServer().getPluginManager().getPlugins()) {
					SQLUtilities.getUtilities().execStatement("INSERT INTO plugins (name, version) VALUES ('" + p.getDescription().getName() + "', '" + p.getDescription().getVersion() + "');");
				}
			}
		}, 60L);
	}

}
