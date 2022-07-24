package org.bukkit.plugin;

import java.io.File;
import java.util.regex.Pattern;

/**
 * Represents a plugin loader, which handles direct access to specific types
 * of plugins
 */
public interface PluginLoader {
	
    /**
     * Loads the plugin contained in the specified file
     *
     * @param file File to attempt to load
     * @return Plugin that was contained in the specified file, or null if
     *     unsuccessful
     * @throws InvalidPluginException Thrown when the specified file is not a
     *     plugin
     * @throws UnknownDependencyException If a required dependency could not
     *     be found
     */
    public Plugin loadPlugin(File file) throws InvalidPluginException;

    /**
     * Loads a PluginDescriptionFile from the specified file
     *
     * @param file File to attempt to load from
     * @return A new PluginDescriptionFile loaded from the plugin.yml in the
     *     specified file
     * @throws InvalidDescriptionException If the plugin description file
     *     could not be created
     */
    public PluginDescriptionFile getPluginDescription(File file) throws InvalidDescriptionException;

    /**
     * Returns a list of all filename filters expected by this PluginLoader
     *
     * @return The filters
     */
    public Pattern[] getPluginFileFilters();

    /**
     * Enables the specified plugin
     * <p>
     * Attempting to enable a plugin that is already enabled will have no
     * effect
     *
     * @param plugin Plugin to enable
     */
    public void enablePlugin(Plugin plugin);
}