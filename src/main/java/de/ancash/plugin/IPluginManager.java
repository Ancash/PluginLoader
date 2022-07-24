package de.ancash.plugin;

import java.io.File;

import de.ancash.libs.org.apache.commons.lang3.Validate;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

public final class IPluginManager {

	private final File pluginDir;
	private final PluginManager pluginManager = new SimplePluginManager();
	
	private Plugin[] plugins;
	
	public IPluginManager(String path) {
		pluginDir = new File(path);
		pluginManager.registerInterface(JavaPluginLoader.class);
	}
	
	public void loadPlugins() {
		if (pluginDir.exists()) {
			this.plugins = pluginManager.loadPlugins(pluginDir);
			for(Plugin plugin : plugins) {
				try {
					plugin.onLoad();
				} catch(Exception ex) {
					System.err.println("Could not load: " + plugin.getDescription().getFullName());
					ex.printStackTrace();
				}
			}
        } else {
        	pluginDir.mkdirs();
            this.plugins = new Plugin[0];
        }
	}
	
	public Plugin[] getPlugins() {
		return plugins;
	}
	
	public void enablePlugins() {
		Validate.notNull(plugins, "Plugins haven't been loaded yet!");
		
		for (Plugin plugin : plugins) {
			try {
				JavaPlugin jplugin = (JavaPlugin) plugin;
                System.out.println(String.format("Enabling %s", plugin.getDescription().getFullName()));
                jplugin.setEnabled(true);
            } catch (Throwable ex) {
                System.err.println(ex.getMessage() + " initializing " + plugin.getDescription().getFullName() + " (Is it up to date?): " + ex);
                ex.printStackTrace();
            }
        }
	}
	
	public void disablePlugins() {
		Validate.notNull(plugins, "Plugins haven't been loaded yet!");
		for(Plugin plugin : plugins) {
			System.out.println(String.format("Disabling %s", plugin.getDescription().getFullName()));
			try {
				((JavaPlugin) plugin).setEnabled(false);
			} catch(Exception ex) {
				System.err.println("Error while disabling: " + plugin.getDescription().getFullName());
				ex.printStackTrace();
			}
		}
	}
}