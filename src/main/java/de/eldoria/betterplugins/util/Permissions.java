package de.eldoria.betterplugins.util;

public class Permissions {
    private static final String BASE = "betterplugins";


    private static String perm(String... vals) {
        return String.join(".", vals);
    }

    public static class Info {
        private static final String BASE = perm(Permissions.BASE, "info");
        public static final String UPDATE_NOTIFY = perm(BASE, "updatenotify");

        public static class Visibility {
            private static final String BASE = perm(Info.BASE, "visibility");
            public static final String HIDDEN = perm(BASE, "hidden");
            public static final String DEPENDS = perm(BASE, "depends");
            public static final String SOFT_DEPENDS = perm(BASE, "softdepends");
            public static final String USAGE = perm(BASE, "usage");
            public static final String DOWNLOAD = perm(BASE, "download");
            public static final String INFO = perm(BASE, "info");
        }
    }

    public static class Commands {
        private static final String BASE = perm(Permissions.BASE, "commands");
        public static final String ADMIN = perm(BASE, "admin");
        public static final String PLUGINS = perm(BASE, "plugins");
        public static final String UPDATES = perm(BASE, "updates");
    }
}
