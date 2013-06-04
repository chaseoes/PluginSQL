package com.chaseoes.pluginsql.utilities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

import com.chaseoes.pluginsql.PluginSQL;

public class SQLUtilities {

	static SQLUtilities instance = new SQLUtilities();
	private PluginSQL plugin;
	Connection conn;
	boolean connected = false;

	private SQLUtilities() {

	}

	public static SQLUtilities getUtilities() {
		return instance;
	}

	public void setup(PluginSQL p) {
		plugin = p;
		final String username = p.getConfig().getString("database.username");
		final String password = p.getConfig().getString("database.password");
		final String url = "jdbc:mysql://" + p.getConfig().getString("database.hostname") + ":" + p.getConfig().getInt("database.port") + "/" + p.getConfig().getString("database.database_name");
		p.getServer().getScheduler().runTaskAsynchronously(p, new Runnable() {
			public void run() {
				try {
					conn = DriverManager.getConnection(url, username, password);
					Statement st = conn.createStatement();
					String table = "CREATE TABLE IF NOT EXISTS plugins(id INT NOT NULL AUTO_INCREMENT, PRIMARY KEY(id), name TEXT, version TEXT)";
					String table1 = "TRUNCATE TABLE plugins";
					st.executeUpdate(table);
					st.executeUpdate(table1);
					connected = true;
				} catch (Exception e) {
					connected = false;
					e.printStackTrace();
					plugin.getLogger().log(Level.SEVERE, "Could not connect to database! Verify your database details in the configuration are correct.");
					plugin.getServer().getPluginManager().disablePlugin(plugin);
				}

			}
		});
	}

	public void execStatement(final String statement) {
		plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
			public void run() {
				Statement st;
				try {
					st = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
					st.execute(statement);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void execUpdate(final String statement) {
		plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
			public void run() {
				Statement st;
				try {
					st = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
					st.executeUpdate(statement);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		});
	}

}
