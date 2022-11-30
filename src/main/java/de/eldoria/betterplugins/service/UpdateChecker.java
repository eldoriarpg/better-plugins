package de.eldoria.betterplugins.service;

import de.eldoria.betterplugins.configuration.Configuration;
import de.eldoria.betterplugins.configuration.elements.ConfPlugin;
import de.eldoria.betterplugins.configuration.elements.UpdateCheck;
import de.eldoria.betterplugins.util.Version;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UpdateChecker implements Runnable {
    private final Plugin plugin;
    private final Configuration configuration;
    private final HttpClient client;
    private final Map<String, Update> updates = new HashMap<>();

    public UpdateChecker(Plugin plugin, Configuration configuration) {
        this.plugin = plugin;
        this.configuration = configuration;
        client = HttpClient.newHttpClient();
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, this, 20, 72000);
    }


    @Override
    public void run() {
        if (!configuration.checkUpdates()) return;

        for (ConfPlugin plugin : configuration.activePlugins()) {
            if (plugin.updateCheck() == UpdateCheck.NONE) continue;

            if (plugin.updateCheck() == UpdateCheck.SPIGOT) {
                checkSpigot(plugin);
            }
        }
    }

    private void checkSpigot(ConfPlugin plugin) {
        if (plugin.spigotId() <= 0) return;

        var url = "https://api.spigotmc.org/legacy/update.php?resource=" + plugin.spigotId();

        HttpRequest request = HttpRequest.newBuilder(URI.create(url)).GET().build();
        String latestVersion;
        try {
            latestVersion = client.send(request, HttpResponse.BodyHandlers.ofString()).body();
        } catch (IOException e) {
            log().log(Level.WARNING, "Could not check updates for " + plugin.name(), e);
            return;
        } catch (InterruptedException e) {
            log().log(Level.WARNING, "Could not check updates for " + plugin.name(), e);
            return;
        }

        var currentVersion = getBukkitPlugin(plugin).getDescription().getVersion();

        // We do not trust devs to always have the latest version on spigot.
        if (!Version.parse(currentVersion).isOlder(Version.parse(latestVersion))) return;

        updates.put(plugin.name(), new Update(currentVersion, latestVersion));
    }

    private Plugin getBukkitPlugin(ConfPlugin confPlugin) {
        return plugin.getServer().getPluginManager().getPlugin(confPlugin.name());
    }

    public Map<String, Update> updates() {
        return updates;
    }

    private Logger log() {
        return plugin.getLogger();
    }

    public record Update(String current, String latest) {
    }
}
