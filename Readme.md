[![wakatime](https://wakatime.com/badge/github/eldoriarpg/better-plugins.svg)](https://wakatime.com/badge/github/eldoriarpg/better-plugins)

# Better Plugins

The better plugin list. More information about the plugin authors, shows the description and can additionally check
for plugin updates via spigot.

## Features

### Update Checking

The plugin can perform update checks via the Spigot api, GitHub releases and GitHub tags. It recognizes new
functions by comparing the values of the version string instead of just checking if the version is different. That
avoids update notices when running snapshots or dev builds.

`/bp updates` will give you a list of updateable plugins. If you have the `betterplugins.info.updatenotify` permission
your will get an update notification on join.

### Better plugin list

The plugin replaces your usual plugin list. Every plugin entry is clickable and will reveal some additional
information on hover and even more when you click on it.

### Plugin override

You can change the names of plugins with a pretty name, alter the description and set a download and info url which
will be displayed in the detailed plugin info. You can also completely hide plugins away. All of it can be
configured by a configuration files or individually by commands ingame.

### Dependency view

You can directly see which plugins depend on a plugin, and you can also see which plugins are actually using another
plugin. Never uninstall a plugin which is still needed by others!

### Fine grained permissions

Nearly everything in the information can be hidden based on permissions

## Permissions

| Permission                                | Meaning                                  |
|-------------------------------------------|------------------------------------------|
| betterplugins.info.updatenotify           | Send a update message on join            |
| betterplugins.info.visibility.hidden      | Shows hidden plugins                     |
| betterplugins.info.visibility.depends     | Shows the depends                        |
| betterplugins.info.visibility.softdepends | Shows the soft depends                   |
| betterplugins.info.visibility.usage       | Shows the plugins which use a plugin     |
| betterplugins.info.visibility.download    | Shows the download link                  |
| betterplugins.info.visibility.info        | Shows the info link                      |
| betterplugins.commands.admin              | Access to the admin command              |
| betterplugins.commands.plugins            | Allows to see the plugins list and info. |
| betterplugins.commands.updates            | Allows to see available updates.         |

## Commands

### Plugins

Aliases: `bp, betterplugins`

The `plugins` command replaces the bukkit plugin command for players.

### Info

`/bp info <name>`

This shows detailed information about a plugin. This can be also achieved by clicking on any plugin name in all panels.

### Admin

`/bp admin`

The admin command allows to change everything related to plugins. You can change description, name, hide plugins and 
more. You can also reload the configuration.

### Updates

`/bp updates`

This command will show currently available plugin updates.
