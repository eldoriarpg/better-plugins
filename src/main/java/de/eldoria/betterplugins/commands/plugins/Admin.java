package de.eldoria.betterplugins.commands.plugins;

import de.eldoria.betterplugins.commands.plugins.admin.Description;
import de.eldoria.betterplugins.commands.plugins.admin.DownloadUrl;
import de.eldoria.betterplugins.commands.plugins.admin.Hide;
import de.eldoria.betterplugins.commands.plugins.admin.Info;
import de.eldoria.betterplugins.commands.plugins.admin.InfoUrl;
import de.eldoria.betterplugins.commands.plugins.admin.Name;
import de.eldoria.betterplugins.commands.plugins.admin.Reload;
import de.eldoria.betterplugins.commands.plugins.admin.Show;
import de.eldoria.betterplugins.commands.plugins.admin.ToggleHidden;
import de.eldoria.betterplugins.commands.plugins.admin.Update;
import de.eldoria.betterplugins.commands.plugins.admin.UpdateIdentifier;
import de.eldoria.betterplugins.configuration.Configuration;
import de.eldoria.betterplugins.util.Permissions;
import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import org.bukkit.plugin.Plugin;

public class Admin extends AdvancedCommand {
    public Admin(Plugin plugin, Configuration configuration) {
        super(plugin, CommandMeta.builder("admin")
                .withPermission(Permissions.Commands.ADMIN)
                .buildSubCommands((cmds, meta) -> {
                    Info info = new Info(plugin, configuration);
                    cmds.add(new Description(plugin, configuration, info));
                    cmds.add(new DownloadUrl(plugin, configuration, info));
                    cmds.add(new Hide(plugin, configuration, info));
                    cmds.add(info);
                    cmds.add(new InfoUrl(plugin, configuration, info));
                    cmds.add(new Name(plugin, configuration, info));
                    cmds.add(new Reload(plugin, configuration));
                    cmds.add(new Show(plugin, configuration, info));
                    cmds.add(new UpdateIdentifier(plugin, configuration, info));
                    cmds.add(new ToggleHidden(plugin, configuration, info));
                    cmds.add(new Update(plugin, configuration, info));
                })
                .build());
    }
}
