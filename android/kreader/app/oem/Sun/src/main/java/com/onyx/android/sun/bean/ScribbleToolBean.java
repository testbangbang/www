package com.onyx.android.sun.bean;

import com.onyx.android.sun.scribble.ScribbleSubMenuID;

/**
 * Created by hehai on 17-10-25.
 */

public class ScribbleToolBean {
    private @ScribbleSubMenuID.ScribbleSubMenuIDDef int scribbleSubMenuID;
    private int imageResource;

    public ScribbleToolBean(int scribbleSubMenuID, int imageResource) {
        this.scribbleSubMenuID = scribbleSubMenuID;
        this.imageResource = imageResource;
    }

    public int getScribbleSubMenuID() {
        return scribbleSubMenuID;
    }

    public void setScribbleSubMenuID(int scribbleSubMenuID) {
        this.scribbleSubMenuID = scribbleSubMenuID;
    }

    public int getImageResource() {
        return imageResource;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }
}
