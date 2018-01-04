package com.onyx.jdread.util;

/**
 * Created by hehai on 18-1-4.
 */

public enum SizeConverter {

    Arbitrary {
        @Override
        public String convert(float size) {
            while (size > 1024) {
                size /= 1024;
            }
            return String.format(FORMAT_F, size);
        }
    },

    B {
        @Override
        public String convert(float b) {
            return converter(0, b);
        }
    },

    KB {
        @Override
        public String convert(float kb) {
            return converter(1, kb);
        }
    },

    MB {
        @Override
        public String convert(float mb) {
            return converter(2, mb);
        }
    },

    GB {
        @Override
        public String convert(float gb) {
            return converter(3, gb);
        }
    },

    TB {
        @Override
        public String convert(float tb) {
            return converter(4, tb);
        }
    },

    ArbitraryTrim {
        @Override
        public String convert(float size) {
            while (size > 1024) {
                size /= 1024;
            }

            int sizeInt = (int) size;
            boolean isfloat = size - sizeInt > 0.0F;
            if (isfloat) {
                return String.format(FORMAT_F, size);
            }
            return String.format(FORMAT_D, sizeInt);
        }
    },

    BTrim {
        @Override
        public String convert(float b) {
            return trimConverter(0, b);
        }
    },

    KBTrim {
        @Override
        public String convert(float kb) {
            return trimConverter(1, kb);
        }
    },

    MBTrim {
        @Override
        public String convert(float mb) {
            return trimConverter(2, mb);
        }
    },

    GBTrim {
        @Override
        public String convert(float gb) {
            return trimConverter(3, gb);
        }
    },

    TBTrim {
        @Override
        public String convert(float tb) {
            return trimConverter(4, tb);
        }
    };

    abstract public String convert(float size);

    private static final String[] UNITS = new String[]{
            "B", "KB", "MB", "GB", "TB", "PB", "**"
    };

    private static final int LAST_IDX = UNITS.length - 1;

    private static final String FORMAT_F = "%1$-1.2f";
    private static final String FORMAT_F_UNIT = "%1$-1.2f%2$s";

    private static final String FORMAT_D = "%1$-1d";
    private static final String FORMAT_D_UNIT = "%1$-1d%2$s";

    private static String converter(int unit, float size) {
        int unitIdx = unit;
        while (size > 1024) {
            unitIdx++;
            size /= 1024;
        }
        int idx = unitIdx < LAST_IDX ? unitIdx : LAST_IDX;
        return String.format(FORMAT_F_UNIT, size, UNITS[idx]);
    }

    private static String trimConverter(int unit, float size) {
        int unitIdx = unit;
        while (size > 1024) {
            unitIdx++;
            size /= 1024;
        }

        int sizeInt = (int) size;
        boolean isfloat = size - sizeInt > 0.0F;
        int idx = unitIdx < LAST_IDX ? unitIdx : LAST_IDX;
        if (isfloat) {
            return String.format(FORMAT_F_UNIT, size, UNITS[idx]);
        }
        return String.format(FORMAT_D_UNIT, sizeInt, UNITS[idx]);
    }

    public static String convertBytes(float b, boolean trim) {
        return trim ? trimConvert(0, b, true) : convert(0, b, true);
    }

    public static String convertKB(float kb, boolean trim) {
        return trim ? trimConvert(1, kb, true) : convert(1, kb, true);
    }

    public static String convertMB(float mb, boolean trim) {
        return trim ? trimConvert(2, mb, true) : convert(2, mb, true);
    }

    private static String convert(int unit, float size, boolean withUnit) {
        int unitIdx = unit;
        while (size > 1024) {
            unitIdx++;
            size /= 1024;
        }
        if (withUnit) {
            int idx = unitIdx < LAST_IDX ? unitIdx : LAST_IDX;
            return String.format(FORMAT_F_UNIT, size, UNITS[idx]);
        }
        return String.format(FORMAT_F, size);
    }

    private static String trimConvert(int unit, float size, boolean withUnit) {
        int unitIdx = unit;
        while (size > 1024) {
            unitIdx++;
            size /= 1024;
        }

        int sizeInt = (int) size;
        boolean isfloat = size - sizeInt > 0.0F;
        if (withUnit) {
            int idx = unitIdx < LAST_IDX ? unitIdx : LAST_IDX;
            if (isfloat) {
                return String.format(FORMAT_F_UNIT, size, UNITS[idx]);
            }
            return String.format(FORMAT_D_UNIT, sizeInt, UNITS[idx]);
        }

        if (isfloat) {
            return String.format(FORMAT_F, size);
        }
        return String.format(FORMAT_D, sizeInt);
    }
}
