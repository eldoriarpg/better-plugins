package de.eldoria.betterplugins;

import de.eldoria.betterplugins.commands.Plugins;
import de.eldoria.betterplugins.configuration.Configuration;
import de.eldoria.betterplugins.configuration.elements.ConfPlugin;
import de.eldoria.betterplugins.service.PluginService;
import de.eldoria.betterplugins.service.UpdateChecker;
import de.eldoria.eldoutilities.plugin.EldoPlugin;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.List;

public class BetterPlugins extends EldoPlugin {
    private PluginService pluginService;
    private Configuration configuration;

    @Override
    public void onPluginEnable() throws Throwable {
        configuration = new Configuration(this);
        pluginService = new PluginService(this, configuration);
        UpdateChecker updateChecker = new UpdateChecker(this, configuration);
        registerCommand(new Plugins(this, configuration, updateChecker));
    }

    @Override
    public void onPostStart() throws Throwable {
        pluginService.refreshPlugins();
    }

    @Override
    public List<Class<? extends ConfigurationSerializable>> getConfigSerialization() {
        return List.of(ConfPlugin.class);
    }

    @Override
    public void onPluginDisable() throws Throwable {
        configuration.save();
    }
}
