package com.neverland.engbook.forpublic;

public class AlRect {

    public int					x0;
    public int					y0;
    public int					x1;
    public int					y1;

    public void set(int vx, int vy) {
        x0 = vx;
        y0 = vy;
    }

    public void set(int vx0, int vy0, int vx1, int vy1) {
        x0 = vx0;
        y0 = vy0;
        x1 = vx1;
        y1 = vy1;
    }

    public AlRect() {
        set(-1, -1, -1, -1);
    }

}
