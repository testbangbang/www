package com.onyx.jdread.common;

import com.onyx.jdread.library.ui.LibraryFragment;
import com.onyx.jdread.model.StackList;
import com.onyx.jdread.personal.ui.MyFragment;
import com.onyx.jdread.setting.ui.SettingFragment;
import com.onyx.jdread.shop.ui.StoreFragment;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by huxiaomao on 2017/12/8.
 */

public class ViewConfig {
    private static Map<String, FunctionModule> childViewInfo = new HashMap<>();

    public enum FunctionModule {
        LIBRARY, SHOP, SETTING, PERSONAL
    }

    static {
        //library
        childViewInfo.put(LibraryFragment.class.getName(), FunctionModule.LIBRARY);
        //shop
        childViewInfo.put(StoreFragment.class.getName(), FunctionModule.SHOP);
        //setting
        childViewInfo.put(SettingFragment.class.getName(), FunctionModule.SETTING);
        //personal
        childViewInfo.put(MyFragment.class.getName(), FunctionModule.PERSONAL);
    }

    public static FunctionModule findChildViewParentId(String childViewName) {
        return childViewInfo.get(childViewName);
    }

    public static void initLibraryStack(StackList stackList){
        stackList.push(LibraryFragment.class.getName());
    }

    public static void initShoppingStack(StackList stackList){
        stackList.push(StoreFragment.class.getName());
    }
}
