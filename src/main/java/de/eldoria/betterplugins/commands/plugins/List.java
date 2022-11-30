package de.eldoria.betterplugins.commands.plugins;

import de.eldoria.betterplugins.configuration.Configuration;
import de.eldoria.betterplugins.configuration.elements.ConfPlugin;
import de.eldoria.betterplugins.util.Permissions;
import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.eldoutilities.commands.executor.ITabExecutor;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;

public class List extends AdvancedCommand implements ITabExecutor {

    private final Configuration configuration;
    private final BukkitAudiences audience;
    private final MiniMessage miniMessage;

    public List(Plugin plugin, Configuration configuration) {
        super(plugin, CommandMeta.builder("list")
                .withPermission(Permissions.Commands.PLUGINS)
                .build());
        this.configuration = configuration;
        audience = BukkitAudiences.create(plugin);
        miniMessage = MiniMessage.miniMessage();
    }

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        Player player = sender instanceof Player p ? p : null;

        var plugins = Arrays.stream(plugin().getServer().getPluginManager().getPlugins())
                            .map(Plugin::getName)
                            .map(configuration::getPlugin)
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .filter(plugin -> !plugin.isHidden(player))
                            .sorted(Comparator.comparing(ConfPlugin::prettyName, String.CASE_INSENSITIVE_ORDER))
                            .map(plugin -> plugin.nameComponent(plugin()))
                            .toList();

        audience.sender(sender).sendMessage(miniMessage.deserialize("Plugins (%d): %s"
                .formatted(plugins.size(), String.join(", ", plugins))));
    }
}
