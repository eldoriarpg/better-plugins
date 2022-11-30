package de.eldoria.betterplugins.commands.plugins.admin;

import de.eldoria.betterplugins.configuration.Configuration;
import de.eldoria.betterplugins.configuration.elements.ConfPlugin;
import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.command.util.CommandAssertions;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.eldoutilities.commands.executor.ITabExecutor;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Info extends AdvancedCommand implements ITabExecutor {
    private final Configuration configuration;
    private final MiniMessage miniMessage;
    private final BukkitAudiences builder;

    public Info(Plugin plugin, Configuration configuration) {
        super(plugin, CommandMeta.builder("info")
                .addUnlocalizedArgument("plugin", true)
                .build());
        this.configuration = configuration;
        miniMessage = MiniMessage.miniMessage();
        builder = BukkitAudiences.create(plugin);
    }

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        Optional<ConfPlugin> plugin = configuration.getPlugin(args.asString(0));
        CommandAssertions.isTrue(plugin.isPresent(), "Invalid plugin");

        show(plugin.get(), sender);
    }

    public void show(ConfPlugin plugin, CommandSender sender) {
        builder.sender(sender).sendMessage(miniMessage.deserialize(plugin.info()));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        if (args.sizeIs(1)) {
            return configuration.completePlugin(args.asString(0));
        }
        return Collections.emptyList();
    }

}
