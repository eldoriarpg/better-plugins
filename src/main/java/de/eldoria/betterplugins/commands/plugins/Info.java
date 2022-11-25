package de.eldoria.betterplugins.commands.plugins;

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

import java.util.Optional;

public class Info extends AdvancedCommand implements ITabExecutor {
    private final Configuration configuration;
    private final BukkitAudiences audience;
    private final MiniMessage miniMessage;

    public Info(Plugin plugin, Configuration configuration) {
        super(plugin, CommandMeta.builder("info")
                .addUnlocalizedArgument("plugin", true)
                .build());
        this.configuration = configuration;
        audience = BukkitAudiences.create(plugin);
        miniMessage = MiniMessage.miniMessage();
    }

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        Optional<ConfPlugin> plugin = configuration.getPlugin(args.get(0).asString());
        CommandAssertions.isTrue(plugin.isPresent(), "Invalid Plugin.");

        String details = plugin.get().detailComponent(plugin());
        audience.sender(sender).sendMessage(miniMessage.deserialize(details));
    }
}
