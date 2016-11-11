package com.neverland.engbook.util;

public class AlOneTableCell {
    public int		start;
    public int		stop;
    public int		colspan;
    public int		rowspan;

    public int		left;
    public int		width;
    public int		height;

    public boolean  isFull;

    public void clear(int sz) {
        start = sz;
        colspan = rowspan = 1;
    }

}
