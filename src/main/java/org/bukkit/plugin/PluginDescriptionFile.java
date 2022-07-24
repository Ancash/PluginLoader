package org.bukkit.plugin;

import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.plugin.java.JavaPlugin;
import de.ancash.libs.org.yaml.snakeyaml.Yaml;
import de.ancash.libs.org.yaml.snakeyaml.constructor.AbstractConstruct;
import de.ancash.libs.org.yaml.snakeyaml.constructor.SafeConstructor;
import de.ancash.libs.org.yaml.snakeyaml.nodes.Node;
import de.ancash.libs.org.yaml.snakeyaml.nodes.Tag;

/**
 * This type is the runtime-container for the information in the plugin.yml.
 * All plugins must have a respective plugin.yml. For plugins written in java
 * using the standard plugin loader, this file must be in the root of the jar
 * file.
 * <p>
 * When Bukkit loads a plugin, it needs to know some basic information about
 * it. It reads this information from a YAML file, 'plugin.yml'. This file
 * consists of a set of attributes, each defined on a new line and with no
 * indentation.
 * <p>
 * Every (almost* every) method corresponds with a specific entry in the
 * plugin.yml. These are the <b>required</b> entries for every plugin.yml:
 * <ul>
 * <li>{@link #getName()} - <code>name</code>
 * <li>{@link #getVersion()} - <code>version</code>
 * <li>{@link #getMain()} - <code>main</code>
 * </ul>
 * <p>
 * Failing to include any of these items will throw an exception and cause the
 * server to ignore your plugin.
 * <p>
 * This is a list of the possible yaml keys, with specific details included in
 * the respective method documentations:
 * <table border=1>
 * <caption>The description of the plugin.yml layout</caption>
 * <tr>
 *     <th>Node</th>
 *     <th>Method</th>
 *     <th>Summary</th>
 * </tr><tr>
 *     <td><code>name</code></td>
 *     <td>{@link #getName()}</td>
 *     <td>The unique name of plugin</td>
 * </tr><tr>
 *     <td><code>version</code></td>
 *     <td>{@link #getVersion()}</td>
 *     <td>A plugin revision identifier</td>
 * </tr><tr>
 *     <td><code>main</code></td>
 *     <td>{@link #getMain()}</td>
 *     <td>The plugin's initial class file</td>
 * </tr><tr>
 *     <td><code>author</code><br><code>authors</code></td>
 *     <td>{@link #getAuthors()}</td>
 *     <td>The plugin contributors</td>
 * </tr><tr>
 *     <td><code>description</code></td>
 *     <td>{@link #getDescription()}</td>
 *     <td>Human readable plugin summary</td>
 * </tr><tr>
 *     <td><code>website</code></td>
 *     <td>{@link #getWebsite()}</td>
 *     <td>The URL to the plugin's site</td>
 * </tr><tr>
 *     <td><code>prefix</code></td>
 *     <td>{@link #getPrefix()}</td>
 *     <td>The token to prefix plugin log entries</td>
 * </tr><tr>
 *     <td><code>database</code></td>
 *     <td>{@link #isDatabaseEnabled()}</td>
 *     <td>Indicator to enable database support</td>
 * </tr><tr>
 *     <td><code>load</code></td>
 *     <td>{@link #getLoad()}</td>
 *     <td>The phase of server-startup this plugin will load during</td>
 * </tr><tr>
 *     <td><code>depend</code></td>
 *     <td>{@link #getDepend()}</td>
 *     <td>Other required plugins</td>
 * </tr><tr>
 *     <td><code>softdepend</code></td>
 *     <td>{@link #getSoftDepend()}</td>
 *     <td>Other plugins that add functionality</td>
 * </tr><tr>
 *     <td><code>loadbefore</code></td>
 *     <td>{@link #getLoadBefore()}</td>
 *     <td>The inverse softdepend</td>
 * </tr><tr>
 *     <td><code>commands</code></td>
 *     <td>{@link #getCommands()}</td>
 *     <td>The commands the plugin will register</td>
 * </tr><tr>
 *     <td><code>permissions</code></td>
 *     <td>{@link #getPermissions()}</td>
 *     <td>The permissions the plugin will register</td>
 * </tr><tr>
 *     <td><code>default-permission</code></td>
 *     <td>{@link #getPermissionDefault()}</td>
 *     <td>The default {@link Permission#getDefault() default} permission
 *         state for defined {@link #getPermissions() permissions} the plugin
 *         will register</td>
 * </tr><tr>
 *     <td><code>awareness</code></td>
 *     <td>{@link #getAwareness()}</td>
 *     <td>The concepts that the plugin acknowledges</td>
 * </tr>
 * </table>
 * <p>
 * A plugin.yml example:<blockquote><pre>
 *name: Inferno
 *version: 1.4.1
 *description: This plugin is so 31337. You can set yourself on fire.
 *# We could place every author in the authors list, but chose not to for illustrative purposes
 *# Also, having an author distinguishes that person as the project lead, and ensures their
 *# name is displayed first
 *author: CaptainInflamo
 *authors: [Cogito, verrier, EvilSeph]
 *website: http://www.curse.com/server-mods/minecraft/myplugin
 *
 *main: com.captaininflamo.bukkit.inferno.Inferno
 *database: false
 *depend: [NewFire, FlameWire]
 *
 *commands:
 *  flagrate:
 *    description: Set yourself on fire.
 *    aliases: [combust_me, combustMe]
 *    permission: inferno.flagrate
 *    usage: Syntax error! Simply type /&lt;command&gt; to ignite yourself.
 *  burningdeaths:
 *    description: List how many times you have died by fire.
 *    aliases: [burning_deaths, burningDeaths]
 *    permission: inferno.burningdeaths
 *    usage: |
 *      /&lt;command&gt; [player]
 *      Example: /&lt;command&gt; - see how many times you have burned to death
 *      Example: /&lt;command&gt; CaptainIce - see how many times CaptainIce has burned to death
 *
 *permissions:
 *  inferno.*:
 *    description: Gives access to all Inferno commands
 *    children:
 *      inferno.flagrate: true
 *      inferno.burningdeaths: true
 *      inferno.burningdeaths.others: true
 *  inferno.flagrate:
 *    description: Allows you to ignite yourself
 *    default: true
 *  inferno.burningdeaths:
 *    description: Allows you to see how many times you have burned to death
 *    default: true
 *  inferno.burningdeaths.others:
 *    description: Allows you to see how many times others have burned to death
 *    default: op
 *    children:
 *      inferno.burningdeaths: true
 *</pre></blockquote>
 */
public final class PluginDescriptionFile {
    private static final ThreadLocal<Yaml> YAML = new ThreadLocal<Yaml>() {
        @Override
        protected Yaml initialValue() {
            return new Yaml(new SafeConstructor() {
                {
                    yamlConstructors.put(null, new AbstractConstruct() {
                        @Override
                        public Object construct(final Node node) {
                            if (!node.getTag().startsWith("!@")) {
                                // Unknown tag - will fail
                                return SafeConstructor.undefinedConstructor.construct(node);
                            }
                            // Unknown awareness - provide a graceful substitution
                            return new PluginAwareness() {
                                @Override
                                public String toString() {
                                    return node.toString();
                                }
                            };
                        }
                    });
                    for (final PluginAwareness.Flags flag : PluginAwareness.Flags.values()) {
                        yamlConstructors.put(new Tag("!@" + flag.name()), new AbstractConstruct() {
                            @Override
                            public PluginAwareness.Flags construct(final Node node) {
                                return flag;
                            }
                        });
                    }
                }
            });
        }
    };
    String rawName = null;
    private String name = null;
    private String main = null;
    private String classLoaderOf = null;
    private String version = null;
    private String description = null;
    private String website = null;
    private String prefix = null;
    
    public PluginDescriptionFile(final InputStream stream) throws InvalidDescriptionException {
        loadMap(asMap(YAML.get().load(stream)));
    }

    /**
     * Loads a PluginDescriptionFile from the specified reader
     *
     * @param reader The reader
     * @throws InvalidDescriptionException If the PluginDescriptionFile is
     *     invalid
     */
    public PluginDescriptionFile(final Reader reader) throws InvalidDescriptionException {
        loadMap(asMap(YAML.get().load(reader)));
    }

    /**
     * Creates a new PluginDescriptionFile with the given detailed
     *
     * @param pluginName Name of this plugin
     * @param pluginVersion Version of this plugin
     * @param mainClass Full location of the main class of this plugin
     */
    public PluginDescriptionFile(final String pluginName, final String pluginVersion, final String mainClass) {
        name = pluginName.replace(' ', '_');
        version = pluginVersion;
        main = mainClass;
    }

    /**
     * Gives the name of the plugin. This name is a unique identifier for
     * plugins.
     * <ul>
     * <li>Must consist of all alphanumeric characters, underscores, hyphon,
     *     and period (a-z,A-Z,0-9, _.-). Any other character will cause the
     *     plugin.yml to fail loading.
     * <li>Used to determine the name of the plugin's data folder. Data
     *     folders are placed in the ./plugins/ directory by default, but this
     *     behavior should not be relied on. {@link Plugin#getDataFolder()}
     *     should be used to reference the data folder.
     * <li>It is good practice to name your jar the same as this, for example
     *     'MyPlugin.jar'.
     * <li>Case sensitive.
     * <li>The is the token referenced in {@link #getDepend()}, {@link
     *     #getSoftDepend()}, and {@link #getLoadBefore()}.
     * <li>Using spaces in the plugin's name is deprecated.
     * </ul>
     * <p>
     * In the plugin.yml, this entry is named <code>name</code>.
     * <p>
     * Example:<blockquote><pre>name: MyPlugin</pre></blockquote>
     *
     * @return the name of the plugin
     */
    public String getName() {
        return name;
    }

    /**
     * Gives the version of the plugin.
     * <ul>
     * <li>Version is an arbitrary string, however the most common format is
     *     MajorRelease.MinorRelease.Build (eg: 1.4.1).
     * <li>Typically you will increment this every time you release a new
     *     feature or bug fix.
     * <li>Displayed when a user types <code>/version PluginName</code>
     * </ul>
     * <p>
     * In the plugin.yml, this entry is named <code>version</code>.
     * <p>
     * Example:<blockquote><pre>version: 1.4.1</pre></blockquote>
     *
     * @return the version of the plugin
     */
    public String getVersion() {
        return version;
    }

    /**
     * Gives the fully qualified name of the main class for a plugin. The
     * format should follow the {@link ClassLoader#loadClass(String)} syntax
     * to successfully be resolved at runtime. For most plugins, this is the
     * class that extends {@link JavaPlugin}.
     * <ul>
     * <li>This must contain the full namespace including the class file
     *     itself.
     * <li>If your namespace is <code>org.bukkit.plugin</code>, and your class
     *     file is called <code>MyPlugin</code> then this must be
     *     <code>org.bukkit.plugin.MyPlugin</code>
     * <li>No plugin can use <code>org.bukkit.</code> as a base package for
     *     <b>any class</b>, including the main class.
     * </ul>
     * <p>
     * In the plugin.yml, this entry is named <code>main</code>.
     * <p>
     * Example:
     * <blockquote><pre>main: org.bukkit.plugin.MyPlugin</pre></blockquote>
     *
     * @return the fully qualified main class for the plugin
     */
    public String getMain() {
        return main;
    }

    /**
     * Gives a human-friendly description of the functionality the plugin
     * provides.
     * <ul>
     * <li>The description can have multiple lines.
     * <li>Displayed when a user types <code>/version PluginName</code>
     * </ul>
     * <p>
     * In the plugin.yml, this entry is named <code>description</code>.
     * <p>
     * Example:
     * <blockquote><pre>description: This plugin is so 31337. You can set yourself on fire.</pre></blockquote>
     *
     * @return description of this plugin, or null if not specified
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gives the plugin's or plugin's author's website.
     * <ul>
     * <li>A link to the Curse page that includes documentation and downloads
     *     is highly recommended.
     * <li>Displayed when a user types <code>/version PluginName</code>
     * </ul>
     * <p>
     * In the plugin.yml, this entry is named <code>website</code>.
     * <p>
     * Example:
     * <blockquote><pre>website: http://www.curse.com/server-mods/minecraft/myplugin</pre></blockquote>
     *
     * @return description of this plugin, or null if not specified
     */
    public String getWebsite() {
        return website;
    }

    /**
     * Gives the token to prefix plugin-specific logging messages with.
     * <ul>
     * <li>This includes all messages using {@link Plugin#getLogger()}.
     * <li>If not specified, the server uses the plugin's {@link #getName()
     *     name}.
     * <li>This should clearly indicate what plugin is being logged.
     * </ul>
     * <p>
     * In the plugin.yml, this entry is named <code>prefix</code>.
     * <p>
     * Example:<blockquote><pre>prefix: ex-why-zee</pre></blockquote>
     *
     * @return the prefixed logging token, or null if not specified
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * Returns the name of a plugin, including the version. This method is
     * provided for convenience; it uses the {@link #getName()} and {@link
     * #getVersion()} entries.
     *
     * @return a descriptive name of the plugin and respective version
     */
    public String getFullName() {
        return name + " v" + version;
    }

    /**
     * @return unused
     * @deprecated unused
     */
    @Deprecated
    public String getClassLoaderOf() {
        return classLoaderOf;
    }

    /**
     * Saves this PluginDescriptionFile to the given writer
     *
     * @param writer Writer to output this file to
     */
    public void save(Writer writer) {
        YAML.get().dump(saveMap(), writer);
    }

    private void loadMap(Map<?, ?> map) throws InvalidDescriptionException {
        try {
            name = rawName = map.get("name").toString();

            if (!name.matches("^[A-Za-z0-9 _.-]+$")) {
                throw new InvalidDescriptionException("name '" + name + "' contains invalid characters.");
            }
            name = name.replace(' ', '_');
        } catch (NullPointerException ex) {
            throw new InvalidDescriptionException(ex, "name is not defined");
        } catch (ClassCastException ex) {
            throw new InvalidDescriptionException(ex, "name is of wrong type");
        }

        try {
            version = map.get("version").toString();
        } catch (NullPointerException ex) {
            throw new InvalidDescriptionException(ex, "version is not defined");
        } catch (ClassCastException ex) {
            throw new InvalidDescriptionException(ex, "version is of wrong type");
        }

        try {
            main = map.get("main").toString();
            if (main.startsWith("org.bukkit.")) {
                throw new InvalidDescriptionException("main may not be within the org.bukkit namespace");
            }
        } catch (NullPointerException ex) {
            throw new InvalidDescriptionException(ex, "main is not defined");
        } catch (ClassCastException ex) {
            throw new InvalidDescriptionException(ex, "main is of wrong type");
        }

        if (map.get("class-loader-of") != null) {
            classLoaderOf = map.get("class-loader-of").toString();
        }

        if (map.get("website") != null) {
            website = map.get("website").toString();
        }

        if (map.get("description") != null) {
            description = map.get("description").toString();
        }

        if (map.get("prefix") != null) {
            prefix = map.get("prefix").toString();
        }
    }

    private Map<String, Object> saveMap() {
        Map<String, Object> map = new HashMap<String, Object>();

        map.put("name", name);
        map.put("main", main);
        map.put("version", version);

        if (website != null) {
            map.put("website", website);
        }
        if (description != null) {
            map.put("description", description);
        }

        
        if (classLoaderOf != null) {
            map.put("class-loader-of", classLoaderOf);
        }

        if (prefix != null) {
            map.put("prefix", prefix);
        }

        return map;
    }

    private Map<?,?> asMap(Object object) throws InvalidDescriptionException {
        if (object instanceof Map) {
            return (Map<?,?>) object;
        }
        throw new InvalidDescriptionException(object + " is not properly structured.");
    }

    /**
     * @return internal use
     * @deprecated Internal use
     */
    @Deprecated
    public String getRawName() {
        return rawName;
    }
}
