package com.onyx.reader.host.navigation;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhuzeng on 10/19/15.
 */
public class NavigationArgs {

    private Type type;
    private Map<Type, SubScreenList> map = new HashMap();

    public static enum Type {
        ALL, ODD, EVEN,
    }

    public NavigationArgs(final Type t, final SubScreenList list) {
        type = t;
        map.put(type, list);
    }

    public SubScreenList getList() {
        return map.get(type);
    }

    public float getActualScale() {
        return getList().getActualScale();
    }


}
