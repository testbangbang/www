package com.onyx.jdread.reader.data;

import com.onyx.android.sdk.reader.host.navigation.NavigationArgs;

/**
 * Created by huxiaomao on 2018/1/1.
 */

public class ChangeLayoutParameter {
    private String layout;
    private NavigationArgs navigationArgs;

    public ChangeLayoutParameter(String layout, NavigationArgs navigationArgs) {
        this.layout = layout;
        this.navigationArgs = navigationArgs;
    }

    public String getLayout() {
        return layout;
    }

    public NavigationArgs getNavigationArgs() {
        return navigationArgs;
    }
}
