package de.eldoria.betterplugins.util;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VersionTest {

    @Test
    void parse() {
        Version version = Version.parse("2.4.11-SNAPSHOT-318;eda5bce");
        assertEquals(List.of(2,4,11,318,5),version.nums());

        version = Version.parse("5.0-BUILD-195");
        assertEquals(List.of(5,0,195),version.nums());
    }

    @Test
    void isOlder() {
        Version oldVersion = Version.parse("0.9.0");
        Version newVersion = Version.parse("0.9.2");

        assertTrue(oldVersion.isOlder(newVersion));
        assertFalse(newVersion.isOlder(oldVersion));
        assertFalse(oldVersion.isOlder(oldVersion));

        newVersion = Version.parse("2.4.11-SNAPSHOT-318;eda5bce");
        oldVersion = Version.parse("2.4.10");

        assertTrue(oldVersion.isOlder(newVersion));

        newVersion = Version.parse("5.0-BUILD-195");
        oldVersion = Version.parse("5.0-build-192");

        assertTrue(oldVersion.isOlder(newVersion));

        newVersion = Version.parse("5.0.0-SNAPSHOT-b603");
        oldVersion = Version.parse("4.8.0");

        assertTrue(oldVersion.isOlder(newVersion));
    }

    @Test
    void isNewer() {
        Version oldVersion = Version.parse("0.9.0");
        Version newVersion = Version.parse("0.9.2");

        assertTrue(newVersion.isNewer(oldVersion));
        assertFalse(oldVersion.isNewer(newVersion));
        assertFalse(oldVersion.isNewer(oldVersion));
    }
}
