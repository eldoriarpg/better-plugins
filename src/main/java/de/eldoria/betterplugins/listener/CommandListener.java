package de.eldoria.betterplugins.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.List;

public class CommandListener implements Listener {
    private final List<String> names = List.of("plugins", "pl", "?", "bukkit:plugins", "bukkit:pl", "bukkit:?");

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        String message = event.getMessage();
        for (String name : names) {
            if (!message.startsWith("/" + name)) continue;
            message = message.replace("/" + name, "/betterplugins");
        }
        event.setCancelled(false);
        event.setMessage(message);
    }
}
