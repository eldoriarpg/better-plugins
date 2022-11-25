package de.eldoria.betterplugins.commands;

import de.eldoria.betterplugins.commands.plugins.List;
import de.eldoria.betterplugins.commands.plugins.Info;
import de.eldoria.betterplugins.commands.plugins.Updates;
import de.eldoria.betterplugins.configuration.Configuration;
import de.eldoria.betterplugins.service.UpdateChecker;
import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import org.bukkit.plugin.Plugin;

public class Plugins extends AdvancedCommand {

    public Plugins(Plugin plugin, Configuration configuration, UpdateChecker updateChecker) {
        super(plugin, CommandMeta.builder("betterplugins")
                .buildSubCommands((advancedCommands, commandMetaBuilder) -> {
                    List list = new List(plugin, configuration);
                    commandMetaBuilder.withSubCommand(list)
                                      .withSubCommand(new Info(plugin, configuration))
                                      .withSubCommand(new Updates(plugin, configuration, updateChecker))
                                      .withDefaultCommand(list);

                })
                .build());
    }
}
