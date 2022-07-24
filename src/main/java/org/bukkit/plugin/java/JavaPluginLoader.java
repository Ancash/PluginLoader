package org.bukkit.plugin.java;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

import de.ancash.libs.org.apache.commons.lang3.Validate;
import de.ancash.libs.org.yaml.snakeyaml.error.YAMLException;

import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;

/**
 * Represents a Java plugin loader, allowing plugins in the form of .jar
 */
public final class JavaPluginLoader implements PluginLoader {
	

	private final Pattern[] fileFilters = new Pattern[] { Pattern.compile("\\.jar$"), };
    private final Map<String, PluginClassLoader> loaders = new LinkedHashMap<String, PluginClassLoader>();

    public Plugin loadPlugin(final File file) throws InvalidPluginException {
        Validate.notNull(file, "File cannot be null");

        if (!file.exists()) {
            throw new InvalidPluginException(new FileNotFoundException(file.getPath() + " does not exist"));
        }

        final PluginDescriptionFile description;
        try {
            description = getPluginDescription(file);
        } catch (InvalidDescriptionException ex) {
            throw new InvalidPluginException(ex);
        }

        final File parentFile = file.getParentFile();
        final File dataFolder = new File(parentFile, description.getName());

        if (dataFolder.exists() && !dataFolder.isDirectory()) {
            throw new InvalidPluginException(String.format(
                "Projected datafolder: `%s' for %s (%s) exists and is not a directory",
                dataFolder,
                description.getFullName(),
                file
            ));
        }

        final PluginClassLoader loader;
        try {
            loader = new PluginClassLoader(this, getClass().getClassLoader(), description, dataFolder, file);
        } catch (InvalidPluginException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new InvalidPluginException(ex);
        }

        loaders.put(description.getName(), loader);

        return loader.plugin;
    }

    public PluginDescriptionFile getPluginDescription(File file) throws InvalidDescriptionException {
        Validate.notNull(file, "File cannot be null");

        JarFile jar = null;
        InputStream stream = null;

        try {
            jar = new JarFile(file);
            JarEntry entry = jar.getJarEntry("plugin.yml");

            if (entry == null) {
                throw new InvalidDescriptionException(new FileNotFoundException("Jar does not contain plugin.yml"));
            }

            stream = jar.getInputStream(entry);

            return new PluginDescriptionFile(stream);

        } catch (IOException ex) {
            throw new InvalidDescriptionException(ex);
        } catch (YAMLException ex) {
            throw new InvalidDescriptionException(ex);
        } finally {
            if (jar != null) {
                try {
                    jar.close();
                } catch (IOException e) {
                }
            }
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public Pattern[] getPluginFileFilters() {
        return fileFilters.clone();
    }

    
    public void enablePlugin(final Plugin plugin) {
        Validate.isTrue(plugin instanceof JavaPlugin, "Plugin is not associated with this PluginLoader");

        if (!plugin.isEnabled()) {
        	System.out.println("Enabling " + plugin.getDescription().getFullName());

            JavaPlugin jPlugin = (JavaPlugin) plugin;

            String pluginName = jPlugin.getDescription().getName();

            if (!loaders.containsKey(pluginName)) {
                loaders.put(pluginName, (PluginClassLoader) jPlugin.getClassLoader());
            }

            try {
                jPlugin.setEnabled(true);
            } catch (Throwable ex) {
            	System.err.println("Error occurred while enabling " + plugin.getDescription().getFullName() + " (Is it up to date?): " + ex);
            }
        }
    }
}
