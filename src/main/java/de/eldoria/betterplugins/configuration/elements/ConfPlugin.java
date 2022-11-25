package de.eldoria.betterplugins.configuration.elements;

import de.eldoria.eldoutilities.localization.MessageComposer;
import de.eldoria.eldoutilities.serialization.SerializationUtil;
import de.eldoria.eldoutilities.serialization.TypeResolvingMap;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;

@SerializableAs("bpConfPlugin")
public class ConfPlugin implements ConfigurationSerializable {
    private final String name;
    private final String prettyName;
    private final String description;
    private final int spigotId;
    private final String infoUrl;
    private final String downloadUrl;
    private final UpdateCheck updateCheck;

    public ConfPlugin(String name, String infoUrl, int spigotId) {
        this.name = name;
        this.prettyName = null;
        this.description = null;
        this.infoUrl = infoUrl;
        this.downloadUrl = null;
        this.spigotId = spigotId;
        this.updateCheck = UpdateCheck.SPIGOT;
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
        spigotId = map.getValueOrDefault("spigotId", 0);
        updateCheck = map.getValueOrDefault("checkUpdates", UpdateCheck.SPIGOT, UpdateCheck.class);
    }

    public String name() {
        return name;
    }

    public int spigotId() {
        return spigotId;
    }

    public UpdateCheck updateCheck() {
        return updateCheck;
    }

    public String prettyName() {
        return prettyName;
    }

    public String description() {
        return description;
    }

    public String infoUrl() {
        return infoUrl;
    }

    public String downloadUrl() {
        return downloadUrl;
    }

    private MessageComposer baseInfo(Plugin plugin) {
        var descr = plugin.getServer().getPluginManager().getPlugin(name).getDescription();
        // Build hover
        MessageComposer info = MessageComposer.create()
                .text("%s - %s", Optional.ofNullable(prettyName).orElse(name), descr.getVersion())
                .newLine();
        if (descr.getDescription() != null) {
            var description = descr.getDescription();
            if (this.description != null) {
                description = this.description;
            }
            info.text(description).newLine();
        }

        HashSet<String> authors = new LinkedHashSet<>(descr.getAuthors());
        authors.addAll(descr.getContributors());
        if (!authors.isEmpty()) {
            info.text("%s: %s", authors.size() == 1 ? "Author" : "Authors", String.join(", ", descr.getAuthors()))
                .newLine();
        }
        return info;
    }

    public String nameComponent(Plugin plugin) {
        Plugin currPlugin = plugin.getServer().getPluginManager().getPlugin(name);
        var descr = currPlugin.getDescription();
        return "<click:run_command:'/betterplugins info %s'><hover:show_text:'%s'><%s>%s%s</hover></click>"
                .formatted(name, baseInfo(plugin).build(), plugin.isEnabled() ? "green" : "red", Optional.ofNullable(prettyName)
                                                                                                         .orElse(name)
                        , descr.getAPIVersion() == null || descr.getAPIVersion().isBlank() ? "*" : "");
    }

    public String detailComponent(Plugin plugin) {
        MessageComposer hover = baseInfo(plugin);

        if (infoUrl != null) {
            hover.text("<click:open_url:'%s'>[Info]<click>", infoUrl).space();
        }

        if (downloadUrl != null) {
            hover.text("<click:open_url:'%s'>[Download]<click>", downloadUrl);
        }

        return hover.build();
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
                                .add("spigotId", spigotId)
                                .add("checkUpdates", updateCheck.name())
                                .build();
    }

}
