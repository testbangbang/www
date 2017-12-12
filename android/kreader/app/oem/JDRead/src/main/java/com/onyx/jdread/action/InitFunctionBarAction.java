package com.onyx.jdread.action;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.utils.PreferenceManager;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.library.action.BaseAction;
import com.onyx.jdread.library.model.DataBundle;
import com.onyx.jdread.library.ui.LibraryFragment;
import com.onyx.jdread.model.FunctionBarModel;
import com.onyx.jdread.model.FunctionBarTabModel;
import com.onyx.jdread.personal.ui.MyFragment;
import com.onyx.jdread.setting.ui.SettingFragment;
import com.onyx.jdread.shop.ui.StoreFragment;

/**
 * Created by hehai on 17-12-11.
 */

public class InitFunctionBarAction extends BaseAction<DataBundle> {
    private FunctionBarModel functionBarModel;

    public InitFunctionBarAction(FunctionBarModel functionBarModel) {
        this.functionBarModel = functionBarModel;
    }

    @Override
    public void execute(DataBundle dataBundle, RxCallback baseCallback) {
        functionBarModel.itemModels.clear();
        functionBarModel.itemModels.add(new FunctionBarTabModel(LibraryFragment.class.getName(), dataBundle.getAppContext().getString(R.string.library_name), R.mipmap.ic_shelf));
        functionBarModel.itemModels.add(new FunctionBarTabModel(StoreFragment.class.getName(), dataBundle.getAppContext().getString(R.string.shop_name), R.mipmap.ic_shop));
        if (PreferenceManager.getBooleanValue(JDReadApplication.getInstance(), R.string.show_back_tab_key, true)) {
            functionBarModel.itemModels.add(new FunctionBarTabModel("back", dataBundle.getAppContext().getString(R.string.back_name), R.mipmap.ic_undo));
        }
        functionBarModel.itemModels.add(new FunctionBarTabModel(SettingFragment.class.getName(), dataBundle.getAppContext().getString(R.string.setting_name), R.mipmap.ic_setting));
        functionBarModel.itemModels.add(new FunctionBarTabModel(MyFragment.class.getName(), dataBundle.getAppContext().getString(R.string.personal_name), R.mipmap.ic_me));
        if (baseCallback != null) {
            baseCallback.onNext(functionBarModel);
        }
    }
}
