package de.eldoria.betterplugins.service;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import de.eldoria.betterplugins.configuration.Configuration;
import de.eldoria.betterplugins.configuration.elements.ConfPlugin;
import de.eldoria.betterplugins.util.Version;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
            if (plugin.updateIdentifier() == null || plugin.updateIdentifier().isBlank()) return;
            switch (plugin.updateCheck()) {
                case NONE -> {
                }
                case SPIGOT -> checkSpigot(plugin);
                case GITHUB_RELEASES -> checkGithubReleases(plugin);
                case GITHUB_TAGS -> checkGithubTags(plugin);
            }
        }
    }

    private void checkSpigot(ConfPlugin plugin) {
        var url = "https://api.spigotmc.org/legacy/update.php?resource=" + plugin.updateIdentifier();

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

    private void checkGithubReleases(ConfPlugin plugin) {
        var url = "https://api.github.com/repos/%s/releases/latest".formatted(plugin.updateIdentifier());

        HttpRequest request = HttpRequest.newBuilder(URI.create(url)).GET()
                                         .header("accept", "application/vnd.github+json").build();
        GithubRelease response;
        try {
            response = new Gson().fromJson(client.send(request, HttpResponse.BodyHandlers.ofString())
                                                 .body(), GithubRelease.class);
        } catch (IOException e) {
            log().log(Level.WARNING, "Could not check updates for " + plugin.name(), e);
            return;
        } catch (InterruptedException e) {
            log().log(Level.WARNING, "Could not check updates for " + plugin.name(), e);
            return;
        }

        if (response.draft || response.preRelease) return;

        var currentVersion = getBukkitPlugin(plugin).getDescription().getVersion();

        if (!Version.parse(currentVersion).isOlder(Version.parse(response.tagName))) return;

        updates.put(plugin.name(), new Update(currentVersion, response.tagName));
    }

    private void checkGithubTags(ConfPlugin plugin) {
        var url = "https://api.github.com/repos/%s/tags".formatted(plugin.updateIdentifier());

        HttpRequest request = HttpRequest.newBuilder(URI.create(url)).GET()
                                         .header("accept", "application/vnd.github+json").build();
        List<GithubTag> response;
        try {
            Type type = new TypeToken<ArrayList<GithubTag>>() {
            }.getType();
            response = new Gson().fromJson(client.send(request, HttpResponse.BodyHandlers.ofString())
                                                 .body(), type);
        } catch (IOException e) {
            log().log(Level.WARNING, "Could not check updates for " + plugin.name(), e);
            return;
        } catch (InterruptedException e) {
            log().log(Level.WARNING, "Could not check updates for " + plugin.name(), e);
            return;
        }

        if (response.isEmpty()) return;

        var currentVersion = getBukkitPlugin(plugin).getDescription().getVersion();

        var latest = response.get(0);

        if (!Version.parse(currentVersion).isOlder(Version.parse(latest.name))) return;

        updates.put(plugin.name(), new Update(currentVersion, latest.name));
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

    public static class GithubRelease {
        @SerializedName("tag_name")
        String tagName;
        @SerializedName("prerelease")
        boolean preRelease;
        boolean draft;
    }

    public static class GithubTag {
        String name;
    }
}
