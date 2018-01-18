package com.onyx.jdread.main.action;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.utils.PreferenceManager;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.library.model.LibraryDataBundle;
import com.onyx.jdread.main.common.ViewConfig;
import com.onyx.jdread.library.ui.LibraryFragment;
import com.onyx.jdread.main.model.FunctionBarItem;
import com.onyx.jdread.main.model.FunctionBarModel;
import com.onyx.jdread.main.model.MainBundle;
import com.onyx.jdread.personal.ui.PersonalFragment;
import com.onyx.jdread.setting.ui.SettingFragment;
import com.onyx.jdread.shop.ui.ShopFragment;

/**
 * Created by hehai on 17-12-11.
 */

public class InitMainViewFunctionBarAction extends BaseAction<MainBundle> {
    private FunctionBarModel functionBarModel;

    public InitMainViewFunctionBarAction(FunctionBarModel functionBarModel) {
        this.functionBarModel = functionBarModel;
    }

    @Override
    public void execute(MainBundle mainBundle, RxCallback baseCallback) {
        functionBarModel.itemModels.clear();
        functionBarModel.itemModels.add(new FunctionBarItem(ViewConfig.FunctionModule.LIBRARY,LibraryFragment.class.getName(), mainBundle.getAppContext().getString(R.string.library_name), R.mipmap.ic_shelf));
        functionBarModel.itemModels.add(new FunctionBarItem(ViewConfig.FunctionModule.SHOP,ShopFragment.class.getName(), mainBundle.getAppContext().getString(R.string.shop_name), R.mipmap.ic_shop));
        if (PreferenceManager.getBooleanValue(JDReadApplication.getInstance(), R.string.show_back_tab_key, false)) {
            functionBarModel.itemModels.add(new FunctionBarItem(ViewConfig.FunctionModule.BACK,"back", mainBundle.getAppContext().getString(R.string.back_name), R.mipmap.ic_undo));
        }
        functionBarModel.itemModels.add(new FunctionBarItem(ViewConfig.FunctionModule.SETTING,SettingFragment.class.getName(), mainBundle.getAppContext().getString(R.string.setting_name), R.mipmap.ic_setting));
        functionBarModel.itemModels.add(new FunctionBarItem(ViewConfig.FunctionModule.PERSONAL,PersonalFragment.class.getName(), mainBundle.getAppContext().getString(R.string.personal_name), R.mipmap.ic_me));
        if (baseCallback != null) {
            baseCallback.onNext(functionBarModel);
        }
    }
}
