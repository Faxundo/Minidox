package net.faxu.minidox.entity.variant;

import java.util.Arrays;
import java.util.Comparator;

public enum MinidoxVariant {
    LEVEL_ONE(0),
    LEVEL_TWO(1),
    LEVEL_THREE(2),
    EVIL(3);

    private static final MinidoxVariant[] BY_ID = Arrays.stream(values()).sorted(Comparator.
            comparingInt(MinidoxVariant::getId)).toArray(MinidoxVariant[]::new);
    private final int id;

    MinidoxVariant(int id) {
        this.id = id;
    }

    public int getId () {
        return this.id;
    }

    public static MinidoxVariant byId (int id) {
        return BY_ID[id % BY_ID.length];
    }
}
