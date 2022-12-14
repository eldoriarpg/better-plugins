package de.eldoria.betterplugins.commands.plugins;

import de.eldoria.betterplugins.BetterPlugins;
import de.eldoria.betterplugins.configuration.Configuration;
import de.eldoria.betterplugins.configuration.elements.ConfPlugin;
import de.eldoria.betterplugins.util.Permissions;
import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.command.util.CommandAssertions;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.eldoutilities.commands.executor.ITabExecutor;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Info extends AdvancedCommand implements ITabExecutor {
    private final BetterPlugins plugin;
    private final Configuration configuration;
    private final BukkitAudiences audience;
    private final MiniMessage miniMessage;

    public Info(BetterPlugins plugin, Configuration configuration) {
        super(plugin, CommandMeta.builder("info")
                .addUnlocalizedArgument("plugin", true)
                .withPermission(Permissions.Commands.PLUGINS)
                .build());
        this.plugin = plugin;
        this.configuration = configuration;
        audience = BukkitAudiences.create(plugin);
        miniMessage = MiniMessage.miniMessage();
    }

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        Optional<ConfPlugin> plugin = configuration.getPlugin(args.get(0).asString());
        CommandAssertions.isTrue(plugin.isPresent(), "Invalid Plugin.");

        String details;
        if (sender instanceof Player player) {
            CommandAssertions.isFalse(plugin.get().isHidden(player), "Invalid Plugin.");
            details = plugin.get().detailComponent(player, this.plugin);
        } else {
            details = plugin.get().detailComponent(null, this.plugin);
        }
        audience.sender(sender).sendMessage(miniMessage.deserialize(details));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        if (args.sizeIs(1)) {
            return configuration.completePlugin(args.asString(0));
        }
        return Collections.emptyList();
    }
}
