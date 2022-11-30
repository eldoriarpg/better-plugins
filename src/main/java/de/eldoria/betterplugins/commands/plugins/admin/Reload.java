package de.eldoria.betterplugins.commands.plugins.admin;

import de.eldoria.betterplugins.configuration.Configuration;
import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.eldoutilities.commands.executor.ITabExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class Reload extends AdvancedCommand implements ITabExecutor {

    private final Configuration configuration;

    public Reload(Plugin plugin, Configuration configuration) {
        super(plugin, CommandMeta.builder("reload").build());
        this.configuration = configuration;
    }

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        configuration.reload();
        messageSender().sendMessage(sender, "Reloaded configuration");
    }
}
