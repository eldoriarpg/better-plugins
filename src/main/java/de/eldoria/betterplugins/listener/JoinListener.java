package de.eldoria.betterplugins.listener;

import de.eldoria.betterplugins.configuration.Configuration;
import de.eldoria.betterplugins.configuration.elements.ConfPlugin;
import de.eldoria.betterplugins.service.UpdateChecker;
import de.eldoria.betterplugins.util.Permissions;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class JoinListener implements Listener {

    private final Plugin plugin;
    private final Configuration configuration;
    private final UpdateChecker updateChecker;
    private final BukkitAudiences audience;
    private final MiniMessage miniMessage;


    public JoinListener(Plugin plugin, Configuration configuration, UpdateChecker updateChecker) {
        this.plugin = plugin;
        this.configuration = configuration;
        this.updateChecker = updateChecker;
        audience = BukkitAudiences.create(plugin);
        miniMessage = MiniMessage.miniMessage();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (!event.getPlayer().hasPermission(Permissions.Info.UPDATE_NOTIFY)) return;
        List<String> updates = new ArrayList<>();
        for (var update : updateChecker.updates().entrySet()) {
            ConfPlugin plugin = configuration.getPlugin(update.getKey()).get();
            String component = plugin.nameComponent(this.plugin);
            if (plugin.downloadUrl() != null) {
                updates.add("%s<red> %s <gray>-> <hover:show_text:'<dark_green>Click to download'><click:open_url:'%s'><dark_green>%s</click></hover>"
                        .formatted(component, update.getValue().current(), plugin.downloadUrl(), update.getValue()
                                                                                                       .latest()));
            } else {
                updates.add("%s <red>%s <gray>-> <dark_green>%s"
                        .formatted(component, update.getValue().current(), update.getValue().latest()));
            }
        }
        if (updates.isEmpty()) return;
        audience.sender(event.getPlayer()).sendMessage(miniMessage.deserialize(String.join("\n", updates)));
    }
}
