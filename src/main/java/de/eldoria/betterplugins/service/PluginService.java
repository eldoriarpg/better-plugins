package de.eldoria.betterplugins.service;

import de.eldoria.betterplugins.configuration.Configuration;
import de.eldoria.betterplugins.configuration.elements.ConfPlugin;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class PluginService {
    public static final Pattern PLUGIN_ID = Pattern.compile("spigotmc.org/resources/.*?(?<id>[0-9]+)/?$");
    private final Plugin plugin;
    private final Configuration configuration;

    public PluginService(Plugin plugin, Configuration configuration) {
        this.plugin = plugin;
        this.configuration = configuration;
    }

    public void refreshPlugins() {
        PluginManager pm = plugin.getServer().getPluginManager();


        // find inactive plugins marked as active
        var inactive = configuration.activePlugins()
                                    .stream()
                                    .filter(plugin -> pm.getPlugin(plugin.name()) == null)
                                    .toList();

        // find active plugins marked as inactive
        var active = configuration.inactivePlugins()
                                  .stream()
                                  .filter(plugin -> pm.getPlugin(plugin.name()) != null)
                                  .toList();

        inactive.forEach(configuration::setInactive);
        active.forEach(configuration::setActive);

        // Add new plugins
        for (Plugin plugin : pm.getPlugins()) {
            if (configuration.getPlugin(plugin.getName()).isPresent()) continue;
            this.plugin.getLogger().info("Detected new plugin " + plugin.getName());
            var descr = plugin.getDescription();

            String updateIdentifier = null;

            if (descr.getWebsite() != null) {
                Matcher matcher = PLUGIN_ID.matcher(descr.getWebsite());
                if (matcher.find()) {
                    this.plugin.getLogger().info("Detected spigot id for " + plugin.getName());
                    updateIdentifier = matcher.group("id");
                }
            }
            configuration.setActive(new ConfPlugin(plugin.getName(), descr.getWebsite(), updateIdentifier));
        }

        plugin.getLogger().info("Discovered " + configuration.activePlugins().size() + " active plugins");
        plugin.getLogger().info("Active: " + configuration.activePlugins().stream().map(ConfPlugin::name).collect(Collectors.joining(", ")));
        plugin.getLogger().info("Backed up " + configuration.inactivePlugins().size() + " inactive plugins");
        plugin.getLogger().info("Inactive: " + configuration.inactivePlugins().stream().map(ConfPlugin::name).collect(Collectors.joining(", ")));
    }
}
