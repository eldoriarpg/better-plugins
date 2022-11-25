package de.eldoria.betterplugins.commands.plugins;

import de.eldoria.betterplugins.configuration.Configuration;
import de.eldoria.betterplugins.configuration.elements.ConfPlugin;
import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.eldoutilities.commands.executor.ITabExecutor;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.stream.Collectors;

public class List extends AdvancedCommand implements ITabExecutor {

    private final Configuration configuration;
    private final BukkitAudiences audience;
    private final MiniMessage miniMessage;

    public List(Plugin plugin, Configuration configuration) {
        super(plugin, CommandMeta.builder("list")
                .build());
        this.configuration = configuration;
        audience = BukkitAudiences.create(plugin);
        miniMessage = MiniMessage.miniMessage();
    }

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        String plugins = configuration.activePlugins().stream()
                                      .sorted(Comparator.comparing(ConfPlugin::name, String.CASE_INSENSITIVE_ORDER))
                                      .map(plugin -> plugin.nameComponent(plugin()))
                                      .collect(Collectors.joining(", "));

        audience.sender(sender)
                .sendMessage(miniMessage.deserialize("Plugins (%d): %s"
                        .formatted(configuration.activePlugins().size(), plugins)));
    }
}
