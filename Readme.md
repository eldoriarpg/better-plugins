# Better Plugins

The better plugin list. More information about the plugin authors, shows the description and can additionally check 
for plugin updates via spigot.

Allows to change plugin names and descriptions.

Heavily WIP and mostly used to detect plugin updates rn.

## Features

### Update Checking

The plugin can perform update checks via the spigot api. It recognizes new functions by comparing the values of the 
version string instead of just checking if the version is different. That avoids update notices when running 
snapshots or dev builds.

`/bp updates` will give you a list of updateable plugins. If you have the `betterplugins.updatenotify` permission 
your will get an update notification on join.

### Better plugin list

The plugin replaces your usual plugin list. Every plugin entry is clickable and will revel some additiona 
information on hover and even more when you click on it.

### Plugin override

You can change the names of plugins with a pretty name, alter the description and set a download and info url which 
will be displayed in the detailed plugin info. You can also completely hide plugins away. All of it can be 
configured by a configuration files or individually by commands ingame.

### Dependency view
You can directly see which plugins depend on a plugin, and you can also see which plugins are actually using another 
plugin. Never uninstall a plugin which is still needed by others! 
