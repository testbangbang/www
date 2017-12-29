package com.onyx.jdread.common;

import com.onyx.jdread.library.event.WifiPassBookEvent;
import com.onyx.jdread.library.ui.LibraryFragment;
import com.onyx.jdread.library.ui.WiFiPassBookFragment;
import com.onyx.jdread.model.StackList;
import com.onyx.jdread.personal.ui.PersonalFragment;
import com.onyx.jdread.setting.ui.DeviceConfigFragment;
import com.onyx.jdread.setting.ui.LockScreenFragment;
import com.onyx.jdread.setting.ui.RefreshFragment;
import com.onyx.jdread.setting.ui.SettingFragment;
import com.onyx.jdread.setting.ui.SystemUpdateFragment;
import com.onyx.jdread.setting.ui.WifiFragment;
import com.onyx.jdread.shop.ui.ShopFragment;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by huxiaomao on 2017/12/8.
 */

public class ViewConfig {
    private static Map<String, FunctionModule> childViewInfo = new HashMap<>();

    public enum FunctionModule {
        BACK,LIBRARY, SHOP, SETTING, PERSONAL
    }

    static {
        //library
        childViewInfo.put(LibraryFragment.class.getName(), FunctionModule.LIBRARY);
        childViewInfo.put(WiFiPassBookFragment.class.getName(),FunctionModule.LIBRARY);
        //shop
        childViewInfo.put(ShopFragment.class.getName(), FunctionModule.SHOP);
        //setting
        childViewInfo.put(SettingFragment.class.getName(), FunctionModule.SETTING);
        childViewInfo.put(DeviceConfigFragment.class.getName(),FunctionModule.SETTING);
        childViewInfo.put(LockScreenFragment.class.getName(),FunctionModule.SETTING);
        childViewInfo.put(RefreshFragment.class.getName(),FunctionModule.SETTING);
        childViewInfo.put(SystemUpdateFragment.class.getName(),FunctionModule.SETTING);
        childViewInfo.put(WifiFragment.class.getName(),FunctionModule.SETTING);
        //personal
        childViewInfo.put(PersonalFragment.class.getName(), FunctionModule.PERSONAL);
    }

    public static FunctionModule findChildViewParentId(String childViewName) {
        return childViewInfo.get(childViewName);
    }

    public static void initStackByName(StackList stackList, String fragmentName) {
        stackList.push(fragmentName);
    }
}
