package ua.gwm.sponge_plugin.crates.util;

import com.sun.istack.internal.NotNull;
import javax.annotation.Nullable;
import java.util.Arrays;

public class Version implements Comparable<Version> {

    private final @Nullable String prefix;
    private final int[] array;

    public static Version parse(String string) {
        if (string.contains("-")) {
            String[] splited = string.split("-");
            String prefix = splited[0];
            String[] splited2 = splited[1].split("\\.");
            int[] array = new int[splited2.length];
            for (int i = 0; i < splited2.length; i++) {
                array[i] = Integer.valueOf(splited2[i]);
            }
            return new Version(prefix, array);
        } else {
            String[] splited = string.split("\\.");
            int[] array = new int[splited.length];
            for (int i = 0; i < splited.length; i++) {
                array[i] = Integer.valueOf(splited[i]);
            }
            return new Version(null, array);
        }
    }

    public Version(@Nullable String prefix, int... array) {
        this.prefix = prefix;
        this.array = array;
    }

    @Override
    public int compareTo(@NotNull Version version) {
        int[] array = version.getArray();
        for (int i = 0; i < this.array.length && i < array.length; i++) {
            int compared = Integer.compare(this.array[i], array[i]);
            if (compared != 0) {
                return compared;
            }
        }
        return Integer.compare(this.array.length, array.length);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Version version = (Version) o;

        if (prefix != null ? !prefix.equals(version.prefix) : version.prefix != null) return false;
        return Arrays.equals(array, version.array);
    }

    @Override
    public int hashCode() {
        int result = prefix != null ? prefix.hashCode() : 0;
        result = 31 * result + Arrays.hashCode(array);
        return result;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (prefix != null && !prefix.equals("")) {
            builder.append(prefix).append('-');
        }
        for (int i = 0; i < array.length; i++) {
            builder.append(array[i]);
            if (i != array.length - 1) {
                builder.append('.');
            }
        }
        return builder.toString();
    }

    public @Nullable String getPrefix() {
        return prefix;
    }

    public int[] getArray() {
        return array;
    }
}