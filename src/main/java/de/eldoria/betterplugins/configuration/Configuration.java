package de.eldoria.betterplugins.configuration;

import de.eldoria.betterplugins.configuration.elements.ConfPlugin;
import de.eldoria.eldoutilities.configuration.EldoConfig;
import de.eldoria.eldoutilities.simplecommands.TabCompleteUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class Configuration extends EldoConfig {
    private Map<String, ConfPlugin> activePlugins;
    private Map<String, ConfPlugin> inactivePlugins;
    private boolean checkUpdates = true;

    public Configuration(Plugin plugin) {
        super(plugin);
    }

    @Override
    protected void reloadConfigs() {
        FileConfiguration plugins = loadConfig("plugins", null, true);
        activePlugins = new HashMap<>();
        inactivePlugins = new HashMap<>();
        for (ConfPlugin plugin : (List<ConfPlugin>) plugins.getList("activePlugins", new ArrayList<ConfPlugin>())) {
            setActive(plugin);
        }

        for (ConfPlugin plugin : (List<ConfPlugin>) plugins.getList("inactivePlugins", new ArrayList<ConfPlugin>())) {
            setInactive(plugin);
        }

        checkUpdates = getConfig().getBoolean("checkUpdates");
    }

    public Collection<ConfPlugin> activePlugins() {
        return Collections.unmodifiableCollection(activePlugins.values());
    }

    public Collection<ConfPlugin> inactivePlugins() {
        return Collections.unmodifiableCollection(inactivePlugins.values());
    }

    public void setActive(ConfPlugin plugin) {
        getPlugin().getLogger().info("Marked plugin " + plugin.name() + " as active");
        inactivePlugins.remove(plugin.name().toLowerCase(Locale.ROOT));
        activePlugins.put(plugin.name().toLowerCase(Locale.ROOT), plugin);
    }

    public boolean isActive(String name) {
        return activePlugins.get(name.toLowerCase(Locale.ROOT)) != null;
    }

    public void setInactive(ConfPlugin plugin) {
        getPlugin().getLogger().info("Marked plugin " + plugin.name() + " as inactive");
        activePlugins.remove(plugin.name().toLowerCase(Locale.ROOT));
        inactivePlugins.put(plugin.name().toLowerCase(Locale.ROOT), plugin);
    }

    public boolean checkUpdates() {
        return checkUpdates;
    }

    @Override
    protected void saveConfigs() {
        FileConfiguration plugins = loadConfig("plugins", null, false);
        plugins.set("activePlugins", new ArrayList<>(activePlugins()));
        plugins.set("inactivePlugins", new ArrayList<>(inactivePlugins()));
        getConfig().set("checkUpdates", checkUpdates);
    }

    public Optional<ConfPlugin> getPlugin(String name) {
        return Optional.ofNullable(activePlugins.getOrDefault(name.toLowerCase(Locale.ROOT), inactivePlugins.get(name.toLowerCase(Locale.ROOT))));
    }

    public List<String> completePlugin(String string) {
        Stream<String> stringStream = Stream.concat(activePlugins().stream(), inactivePlugins().stream())
                                            .map(ConfPlugin::name);
        return TabCompleteUtil.complete(string, stringStream);
    }
}
