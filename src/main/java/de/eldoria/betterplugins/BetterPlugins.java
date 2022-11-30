package de.eldoria.betterplugins;

import de.eldoria.betterplugins.commands.Plugins;
import de.eldoria.betterplugins.configuration.Configuration;
import de.eldoria.betterplugins.configuration.elements.ConfPlugin;
import de.eldoria.betterplugins.listener.CommandListener;
import de.eldoria.betterplugins.listener.JoinListener;
import de.eldoria.betterplugins.service.PluginService;
import de.eldoria.betterplugins.service.UpdateChecker;
import de.eldoria.eldoutilities.localization.ILocalizer;
import de.eldoria.eldoutilities.messages.MessageSender;
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
        ILocalizer.create(this, "en_US").setLocale("en_US");
        MessageSender.create(this, "§4[BP]");
        registerCommand(new Plugins(this, configuration, updateChecker));
        registerListener(new CommandListener(), new JoinListener(this, configuration, updateChecker));
    }

    @Override
    public void onPostStart() throws Throwable {
        pluginService.refreshPlugins();
    }

    public Configuration configuration() {
        return configuration;
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
