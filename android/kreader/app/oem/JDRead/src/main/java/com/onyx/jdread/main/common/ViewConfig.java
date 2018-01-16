package com.onyx.jdread.main.common;

import com.onyx.jdread.library.ui.LibraryFragment;
import com.onyx.jdread.library.ui.WiFiPassBookFragment;
import com.onyx.jdread.main.model.StackList;
import com.onyx.jdread.personal.ui.ConsumptionRecordFragment;
import com.onyx.jdread.personal.ui.GiftCenterFragment;
import com.onyx.jdread.personal.ui.LoginFragment;
import com.onyx.jdread.personal.ui.PersonalAccountFragment;
import com.onyx.jdread.personal.ui.PersonalBookFragment;
import com.onyx.jdread.personal.ui.PersonalExperienceFragment;
import com.onyx.jdread.personal.ui.PersonalFragment;
import com.onyx.jdread.personal.ui.PersonalNoteFragment;
import com.onyx.jdread.personal.ui.PersonalTaskFragment;
import com.onyx.jdread.personal.ui.PointsForFragment;
import com.onyx.jdread.personal.ui.ReadPreferenceFragment;
import com.onyx.jdread.personal.ui.TopUpRecordFragment;
import com.onyx.jdread.setting.ui.BrightnessFragment;
import com.onyx.jdread.setting.ui.ContactUsFragment;
import com.onyx.jdread.setting.ui.DeviceConfigFragment;
import com.onyx.jdread.setting.ui.DeviceInformationFragment;
import com.onyx.jdread.setting.ui.DictionaryFragment;
import com.onyx.jdread.setting.ui.FeedbackFragment;
import com.onyx.jdread.setting.ui.HelpFragment;
import com.onyx.jdread.setting.ui.LaboratoryFragment;
import com.onyx.jdread.setting.ui.LockScreenFragment;
import com.onyx.jdread.setting.ui.ManualFragment;
import com.onyx.jdread.setting.ui.PasswordSettingFragment;
import com.onyx.jdread.setting.ui.ReadingToolsFragment;
import com.onyx.jdread.setting.ui.RefreshFragment;
import com.onyx.jdread.setting.ui.ScreensaversFragment;
import com.onyx.jdread.setting.ui.SettingFragment;
import com.onyx.jdread.setting.ui.SystemUpdateFragment;
import com.onyx.jdread.setting.ui.TranslateFragment;
import com.onyx.jdread.setting.ui.WifiFragment;
import com.onyx.jdread.shop.ui.AllCategoryFragment;
import com.onyx.jdread.shop.ui.BookDetailFragment;
import com.onyx.jdread.shop.ui.BookRankFragment;
import com.onyx.jdread.shop.ui.CategoryBookListFragment;
import com.onyx.jdread.shop.ui.CommentFragment;
import com.onyx.jdread.shop.ui.ShopCartFragment;
import com.onyx.jdread.shop.ui.ShopFragment;
import com.onyx.jdread.shop.ui.ViewAllBooksFragment;

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
        childViewInfo.put(BookDetailFragment.class.getName(), FunctionModule.SHOP);
        childViewInfo.put(CommentFragment.class.getName(), FunctionModule.SHOP);
        childViewInfo.put(AllCategoryFragment.class.getName(), FunctionModule.SHOP);
        childViewInfo.put(CategoryBookListFragment.class.getName(), FunctionModule.SHOP);
        childViewInfo.put(BookRankFragment.class.getName(), FunctionModule.SHOP);
        childViewInfo.put(ShopCartFragment.class.getName(), FunctionModule.SHOP);
        childViewInfo.put(ViewAllBooksFragment.class.getName(), FunctionModule.SHOP);
        //setting
        childViewInfo.put(SettingFragment.class.getName(), FunctionModule.SETTING);
        childViewInfo.put(DeviceConfigFragment.class.getName(),FunctionModule.SETTING);
        childViewInfo.put(LockScreenFragment.class.getName(),FunctionModule.SETTING);
        childViewInfo.put(RefreshFragment.class.getName(),FunctionModule.SETTING);
        childViewInfo.put(SystemUpdateFragment.class.getName(),FunctionModule.SETTING);
        childViewInfo.put(WifiFragment.class.getName(),FunctionModule.SETTING);
        childViewInfo.put(LaboratoryFragment.class.getName(),FunctionModule.SETTING);
        childViewInfo.put(HelpFragment.class.getName(),FunctionModule.SETTING);
        childViewInfo.put(ContactUsFragment.class.getName(),FunctionModule.SETTING);
        childViewInfo.put(FeedbackFragment.class.getName(),FunctionModule.SETTING);
        childViewInfo.put(ManualFragment.class.getName(),FunctionModule.SETTING);
        childViewInfo.put(ScreensaversFragment.class.getName(),FunctionModule.SETTING);
        childViewInfo.put(DeviceInformationFragment.class.getName(),FunctionModule.SETTING);
        childViewInfo.put(PasswordSettingFragment.class.getName(),FunctionModule.SETTING);
        childViewInfo.put(ReadingToolsFragment.class.getName(),FunctionModule.SETTING);
        childViewInfo.put(BrightnessFragment.class.getName(),FunctionModule.SETTING);
        childViewInfo.put(TranslateFragment.class.getName(),FunctionModule.SETTING);
        childViewInfo.put(DictionaryFragment.class.getName(),FunctionModule.SETTING);
        //personal
        childViewInfo.put(PersonalFragment.class.getName(), FunctionModule.PERSONAL);
        childViewInfo.put(PersonalExperienceFragment.class.getName(), FunctionModule.PERSONAL);
        childViewInfo.put(PersonalAccountFragment.class.getName(), FunctionModule.PERSONAL);
        childViewInfo.put(GiftCenterFragment.class.getName(), FunctionModule.PERSONAL);
        childViewInfo.put(LoginFragment.class.getName(), FunctionModule.PERSONAL);
        childViewInfo.put(ConsumptionRecordFragment.class.getName(), FunctionModule.PERSONAL);
        childViewInfo.put(TopUpRecordFragment.class.getName(), FunctionModule.PERSONAL);
        childViewInfo.put(PointsForFragment.class.getName(), FunctionModule.PERSONAL);
        childViewInfo.put(PersonalTaskFragment.class.getName(), FunctionModule.PERSONAL);
        childViewInfo.put(ReadPreferenceFragment.class.getName(), FunctionModule.PERSONAL);
        childViewInfo.put(PersonalNoteFragment.class.getName(), FunctionModule.PERSONAL);
        childViewInfo.put(PersonalBookFragment.class.getName(), FunctionModule.PERSONAL);
    }

    public static FunctionModule findChildViewParentId(String childViewName) {
        return childViewInfo.get(childViewName);
    }

    public static void initStackByName(StackList stackList, String fragmentName) {
        stackList.push(fragmentName);
    }
}
