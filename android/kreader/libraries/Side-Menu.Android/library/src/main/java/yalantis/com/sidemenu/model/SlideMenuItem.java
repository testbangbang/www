package yalantis.com.sidemenu.model;

import yalantis.com.sidemenu.interfaces.Resourceble;

/**
 * Created by Konstantin on 23.12.2014.
 */
public class SlideMenuItem implements Resourceble {
    private String name;
    private int imageRes;
    private Object tag;

    public SlideMenuItem(String name, int imageRes, Object tag) {
        this.name = name;
        this.imageRes = imageRes;
        this.tag = tag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImageRes() {
        return imageRes;
    }

    public void setImageRes(int imageRes) {
        this.imageRes = imageRes;
    }

    public Object getTag() {
        return tag;
    }
}
