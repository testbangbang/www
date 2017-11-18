package com.neverland.engbook.util;

public class AlOneMultiFile {
    public int						level1_start;
    public int						level2_start;

    public AlOneMultiFile() {
        level1_start = 0;
        level2_start = 0;
    }

    public static String outString(AlOneMultiFile a) {
        StringBuilder w = new StringBuilder();
        w.setLength(0);
        w.append(String.format("0x%08x-0x%08x",
                a.level1_start, a.level2_start));
        return w.toString();
    }

    public static AlOneMultiFile add(int l1s, int l2s) {
        AlOneMultiFile a = new AlOneMultiFile();
        a.level1_start = l1s;
        a.level2_start = l2s;
        return a;
    }
}
