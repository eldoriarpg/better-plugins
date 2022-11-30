package de.eldoria.betterplugins.util;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record Version(List<Integer> nums) implements Comparable<Version> {
    private static final Pattern NUMBER = Pattern.compile("([0-9]+)");


    public static Version parse(String version) {
        List<Integer> nums = new ArrayList<>();

        Matcher matcher = NUMBER.matcher(version);
        while (matcher.find()) {
            nums.add(Integer.parseInt(matcher.group(1)));
        }

        return new Version(nums);
    }

    @Override
    public List<Integer> nums() {
        return Collections.unmodifiableList(nums);
    }

    public boolean isOlder(Version version) {
        return compareTo(version) < 0;
    }

    public boolean isNewer(Version version) {
        return compareTo(version) > 0;
    }

    @Override
    public int compareTo(@NotNull Version version) {
        int numbers = Math.min(version.nums().size(), nums().size());
        for (int i = 0; i < numbers; i++) {
            int compare = Integer.compare(nums().get(i), version.nums().get(i));
            if (compare != 0) return compare;
        }
        return 0;
    }
}
