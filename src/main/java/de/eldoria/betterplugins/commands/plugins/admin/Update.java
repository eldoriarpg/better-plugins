package de.eldoria.betterplugins.commands.plugins.admin;

import de.eldoria.betterplugins.configuration.Configuration;
import de.eldoria.betterplugins.configuration.elements.ConfPlugin;
import de.eldoria.betterplugins.configuration.elements.UpdateCheck;
import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.command.util.CommandAssertions;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.eldoutilities.commands.executor.ITabExecutor;
import de.eldoria.eldoutilities.simplecommands.TabCompleteUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Update extends AdvancedCommand implements ITabExecutor {

    private final Configuration configuration;
    private final Info info;

    public Update(Plugin plugin, Configuration configuration, Info info) {
        super(plugin, CommandMeta.builder("update").addUnlocalizedArgument("plugin", true).build());
        this.configuration = configuration;
        this.info = info;
    }

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        Optional<ConfPlugin> plugin = configuration.getPlugin(args.asString(0));
        CommandAssertions.isTrue(plugin.isPresent(), "Invalid plugin");

        plugin.get().updateCheck(args.asEnum(1, UpdateCheck.class));
        info.show(plugin.get(), sender);
        configuration.save();
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        if (args.sizeIs(1)) {
            return configuration.completePlugin(args.asString(0));
        }
        if (args.sizeIs(2)) {
            return TabCompleteUtil.complete(args.asString(1), UpdateCheck.class);
        }
        return Collections.emptyList();
    }
}
