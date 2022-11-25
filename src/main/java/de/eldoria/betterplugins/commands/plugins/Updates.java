package de.eldoria.betterplugins.commands.plugins;

import de.eldoria.betterplugins.configuration.Configuration;
import de.eldoria.betterplugins.configuration.elements.ConfPlugin;
import de.eldoria.betterplugins.service.UpdateChecker;
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

import java.util.ArrayList;
import java.util.List;

public class Updates extends AdvancedCommand implements ITabExecutor {

    private final Configuration configuration;
    private final UpdateChecker updateChecker;
    private final BukkitAudiences audience;
    private final MiniMessage miniMessage;


    public Updates(Plugin plugin, Configuration configuration, UpdateChecker updateChecker) {
        super(plugin, CommandMeta.builder("updates")
                .build());
        this.configuration = configuration;
        this.updateChecker = updateChecker;
        audience = BukkitAudiences.create(plugin);
        miniMessage = MiniMessage.miniMessage();
    }

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        List<String> updates = new ArrayList<>();
        for (var update : updateChecker.updates().entrySet()) {
            ConfPlugin plugin = configuration.getPlugin(update.getKey()).get();
            String component = plugin.nameComponent(plugin());
            updates.add("%s %s -> %s".formatted(component, update.getValue().current(), update.getValue().latest()));
        }
        if (updates.isEmpty()) {
            messageSender().sendMessage(sender, "No updates available");
            return;
        }
        audience.sender(sender).sendMessage(miniMessage.deserialize(String.join("\n", updates)));
    }
}
