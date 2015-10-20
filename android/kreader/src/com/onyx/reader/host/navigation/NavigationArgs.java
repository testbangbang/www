package com.onyx.reader.host.navigation;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhuzeng on 10/19/15.
 */
public class NavigationArgs {

    private Type type;
    private Map<Type, NavigationList> map = new HashMap();

    public static enum Type {
        ALL, ODD, EVEN,
    }

    public NavigationArgs(final Type t, final NavigationList list) {
        type = t;
        map.put(type, list);
    }

    public NavigationList getList() {
        return map.get(type);
    }



}
