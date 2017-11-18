package com.neverland.engbook.forpublic;

public class AlPoint {
    public int					x;
    public int					y;
    public int					height;
    public int					position;

    public void set(int vx, int vy) {
        x = vx;
        y = vy;
    }

    public void set(int vx, int vy, int vh) {
        x = vx;
        y = vy;
        height = vh;
    }

    public AlPoint() {
        set(-1, -1, -1);
    }
}
