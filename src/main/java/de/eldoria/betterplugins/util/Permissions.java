package de.eldoria.betterplugins.util;

public class Permissions {
    private static final String BASE = "betterplugins";

    public static final String SEE_HIDDEN = perm(BASE, "seehidden");
    public static final String UPDATE_NOTIFY = perm(BASE, "updatenotify");
    public static final String DEPENDENCIES = perm(BASE, "dependencies");

    private static String perm(String... vals) {
        return String.join(".", vals);
    }

    public static class Commands {
        private static final String BASE = perm(Permissions.BASE, "commands");
        public static final String ADMIN = perm(BASE, "admin");
        public static final String PLUGINS = perm(BASE, "plugins");
        public static final String UPDATES = perm(BASE, "updates");
    }
}
