package de.eldoria.betterplugins.configuration.elements;

import de.eldoria.betterplugins.BetterPlugins;
import de.eldoria.betterplugins.configuration.Configuration;
import de.eldoria.betterplugins.util.Permissions;
import de.eldoria.eldoutilities.localization.MessageComposer;
import de.eldoria.eldoutilities.serialization.SerializationUtil;
import de.eldoria.eldoutilities.serialization.TypeResolvingMap;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SerializableAs("bpConfPlugin")
public class ConfPlugin implements ConfigurationSerializable {
    private final String name;
    private String prettyName;
    private String description;
    private String updateIdentifier;
    private String infoUrl;
    private String downloadUrl;
    private UpdateCheck updateCheck;
    private boolean hidden;

    public ConfPlugin(String name, String infoUrl, String updateIdentifier) {
        this.name = name;
        this.prettyName = null;
        this.description = null;
        this.infoUrl = infoUrl;
        this.downloadUrl = null;
        this.updateIdentifier = updateIdentifier;
        this.updateCheck = UpdateCheck.SPIGOT;
        this.hidden = false;
    }

    /**
     * Constructor required by {@link ConfigurationSerializable} in order to deserialize the object.
     */
    @SuppressWarnings("unused")
    public ConfPlugin(Map<String, Object> objectMap) {
        TypeResolvingMap map = SerializationUtil.mapOf(objectMap);
        name = map.getValue("name");
        prettyName = map.getValue("prettyName");
        description = map.getValue("description");
        infoUrl = map.getValue("infoUrl");
        downloadUrl = map.getValue("downloadUrl");
        updateIdentifier = map.getValueOrDefault("updateIdentifier", "");
        updateCheck = map.getValueOrDefault("checkUpdates", UpdateCheck.SPIGOT, UpdateCheck.class);
        hidden = map.getValueOrDefault("hidden", false);
    }

    public String name() {
        return name;
    }

    public String updateIdentifier() {
        return updateIdentifier;
    }

    public UpdateCheck updateCheck() {
        return updateCheck;
    }

    public String prettyName() {
        if (prettyName == null || prettyName.isBlank()) return name;
        return prettyName;
    }

    @Nullable
    public String description() {
        return description;
    }

    @Nullable
    public String infoUrl() {
        return infoUrl;
    }

    @Nullable
    public String downloadUrl() {
        return downloadUrl;
    }

    @Nullable
    public String infoUrl(@Nullable Player player) {
        return player == null || player.hasPermission(Permissions.Info.Visibility.DOWNLOAD) ? infoUrl : null;
    }

    @Nullable
    public String downloadUrl(@Nullable Player player) {
        return player == null || player.hasPermission(Permissions.Info.Visibility.DOWNLOAD) ? downloadUrl : null;
    }

    public void prettyName(String prettyName) {
        this.prettyName = prettyName;
    }

    public void description(String description) {
        this.description = description;
    }

    public void updateIdentifier(String updateIdentifier) {
        this.updateIdentifier = updateIdentifier;
    }

    public void infoUrl(String infoUrl) {
        this.infoUrl = infoUrl;
    }

    public void downloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public void updateCheck(UpdateCheck updateCheck) {
        this.updateCheck = updateCheck;
    }

    public void hidden(boolean hidden) {
        this.hidden = hidden;
    }

    private MessageComposer baseInfo(Plugin plugin) {
        var descr = plugin.getServer().getPluginManager().getPlugin(name).getDescription();
        // Build hover
        MessageComposer info = MessageComposer.create()
                .text("<green><bold>%s</bold> <gray>- <gold>%s", prettyName(), descr.getVersion());
        if (descr.getDescription() != null && !descr.getDescription().isBlank()) {
            var description = descr.getDescription();
            if (this.description != null) {
                description = this.description;
            }
            if (!description.isBlank()) info.newLine().text("<aqua>%s", description);
        }

        HashSet<String> authors = new LinkedHashSet<>(descr.getAuthors());
        authors.addAll(descr.getContributors());
        if (!authors.isEmpty()) {
            info.newLine()
                .text("<dark_green>%s: <aqua>%s", authors.size() == 1 ? "Author" : "Authors", String.join(", ", descr.getAuthors()));
        }
        return info;
    }

    public String nameComponent(Plugin plugin) {
        Plugin currPlugin = plugin.getServer().getPluginManager().getPlugin(name);
        String color;
        if (hidden) {
            color = currPlugin.isEnabled() ? "dark_green" : "dark_red";
        } else {
            color = currPlugin.isEnabled() ? "green" : "red";
        }

        var descr = currPlugin.getDescription();
        return "<click:run_command:'/betterplugins info %s'><hover:show_text:'%s'><%s>%s%s</hover></click>"
                .formatted(name, baseInfo(plugin).build(), color, prettyName()
                        , descr.getAPIVersion() == null || descr.getAPIVersion().isBlank() ? "*" : "");
    }

    public String detailComponent(@Nullable Player player, BetterPlugins plugin) {
        MessageComposer base = baseInfo(plugin);
        PluginManager pm = plugin.getPluginManager();
        Plugin currPlugin = pm.getPlugin(name);

        if (player == null || player.hasPermission(Permissions.Info.Visibility.DEPENDS)) {
            List<String> depend = new ArrayList<>(currPlugin.getDescription().getDepend());
            depend.remove("ViaVersion");
            if (!depend.isEmpty()) {
                base.newLine().text(buildPluginDepend(plugin, depend, player, "Depends"));
            }
        }

        if (player == null || player.hasPermission(Permissions.Info.Visibility.SOFT_DEPENDS)) {
            List<String> softDepend = new ArrayList<>(currPlugin.getDescription().getSoftDepend());
            softDepend.remove("ViaVersion");
            if (!softDepend.isEmpty()) {
                base.newLine().text(buildPluginDepend(plugin, softDepend, player, "Soft Depends"));
            }
        }

        if (player == null || player.hasPermission(Permissions.Info.Visibility.USAGE)) {
            var usedBy = plugin.configuration().activePlugins().stream()
                               .map(pl -> pm.getPlugin(pl.name()))
                               .filter(Objects::nonNull)
                               .filter(pl -> {
                                   var descr = pl.getDescription();
                                   // This is for plugins like FAWE which are usually not referenced as a depend but provide the dependend WorldEdit.
                                   for (String provide : currPlugin.getDescription().getProvides()) {
                                       if (descr.getSoftDepend().contains(provide) || descr.getDepend()
                                                                                           .contains(provide))
                                           return true;
                                   }

                                   return descr.getSoftDepend().contains(name) || descr.getDepend()
                                                                                       .contains(name);
                               })
                               .map(pl -> plugin.configuration().getPlugin(pl.getName()))
                               .filter(Optional::isPresent)
                               .map(Optional::get)
                               .filter(pl -> !pl.isHidden(player))
                               .sorted(Comparator.comparing(ConfPlugin::prettyName, String.CASE_INSENSITIVE_ORDER))
                               .map(pl -> pl.nameComponent(plugin))
                               .toList();

            if (!usedBy.isEmpty()) {
                base.newLine().text("<dark_green>Used by: %s", String.join(", ", usedBy));
            }
        }

        if (infoUrl(player) != null && downloadUrl(player) != null) {
            base.newLine().text("<click:open_url:'%s'><dark_green>[Info]</click>", infoUrl).space()
                .text("<click:open_url:'%s'><gold>[Download]</click>", downloadUrl);
        } else if (infoUrl(player) != null) {
            base.newLine().text("<click:open_url:'%s'><gold>[Info]</click>", infoUrl).space();
        } else if (downloadUrl(player) != null) {
            base.newLine().text("<click:open_url:'%s'><gold>[Download]</click>", downloadUrl);
        }

        return base.build();
    }

    private String buildPluginDepend(BetterPlugins plugin, List<String> depends, @Nullable Player player, String title) {
        PluginManager pm = plugin.getPluginManager();
        Configuration configuration = plugin.configuration();
        String activeDepends = depends.stream()
                                      .map(pm::getPlugin)
                                      .filter(Objects::nonNull)
                                      .map(Plugin::getName)
                                      .map(configuration::getPlugin)
                                      .filter(Optional::isPresent)
                                      .map(Optional::get)
                                      .filter(pl -> !pl.isHidden(player))
                                      .sorted(Comparator.comparing(ConfPlugin::prettyName, String.CASE_INSENSITIVE_ORDER))
                                      .map(pl -> pl.nameComponent(plugin))
                                      .collect(Collectors.joining("<gray>, "));

        var inactiveDepends = depends.stream()
                                     .filter(name -> pm.getPlugin(name) == null)
                                     .collect(Collectors.toList());
        var activeHidden = depends.stream()
                                  .map(pm::getPlugin)
                                  .filter(Objects::nonNull)
                                  .map(Plugin::getName)
                                  .map(configuration::getPlugin)
                                  .filter(Optional::isPresent)
                                  .map(Optional::get)
                                  .filter(pl -> pl.isHidden(player))
                                  .map(ConfPlugin::name)
                                  .toList();

        inactiveDepends = Stream.concat(inactiveDepends.stream(), activeHidden.stream())
                                .sorted(Comparator.comparing(s -> s, String.CASE_INSENSITIVE_ORDER))
                                .map("<gray>%s"::formatted)
                                .toList();

        if (inactiveDepends.isEmpty()) {
            return "<dark_green>%s: %s".formatted(title, activeDepends);
        }

        if (activeDepends.isBlank()) {
            return "<dark_green>%s: <gray><hover:show_text:'%s'>%d inactive depends</hover>"
                    .formatted(title, String.join(", ", inactiveDepends), inactiveDepends.size());
        }

        return "<dark_green>%s: %s and <gray><hover:show_text:'%s'>%d inactive depends.</hover>"
                .formatted(title, activeDepends, String.join(", ", inactiveDepends), inactiveDepends.size());
    }

    public boolean hidden() {
        return hidden;
    }

    public boolean isHidden(@Nullable Player player) {
        if (!hidden || player == null) return false;
        return !player.hasPermission(Permissions.Info.Visibility.HIDDEN);
    }


    @Override
    @NotNull
    public Map<String, Object> serialize() {
        return SerializationUtil.newBuilder()
                                .add("name", name)
                                .add("prettyName", prettyName)
                                .add("description", description)
                                .add("infoUrl", infoUrl)
                                .add("downloadUrl", downloadUrl)
                                .add("updateIdentifier", updateIdentifier)
                                .add("checkUpdates", updateCheck.name())
                                .add("hidden", hidden)
                                .build();
    }

    public String info() {
        return """
               <dark_green>Display Name: <aqua>%s <click:suggest_command:'/betterplugins admin name '><yellow>[Change]</click>
               <dark_green>Description: <aqua>%s <click:suggest_command:'/betterplugins admin description'><yellow>[Change]</click>
               <dark_green>Info Url: <aqua>%s <click:suggest_command:'/betterplugins admin infourl'><yellow>[Change]</click>
               <dark_green>Download Url: <aqua>%s <click:suggest_command:'/betterplugins admin downloadurl'><yellow>[Change]</click>
               <dark_green>Update Identifier: <aqua>%s <click:suggest_command:'/betterplugins admin updateIdentifier'><yellow>[Change]</click>
               <dark_green>Check Updates: <aqua>%s <click:suggest_command:'/betterplugins admin update'><yellow>[Change]</click>
               <dark_green>hidden: <aqua>%s <click:run_command:'/betterplugins admin togglehidden'><yellow>[Change]</click>
               """.stripIndent()
                  .formatted(prettyName(), description, infoUrl, downloadUrl, updateIdentifier, updateCheck, hidden);
    }
}
