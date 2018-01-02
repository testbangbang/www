package com.onyx.jdread.reader.actions;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.utils.PreferenceManager;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.library.action.BaseAction;
import com.onyx.jdread.library.model.DataBundle;
import com.onyx.jdread.library.ui.LibraryFragment;
import com.onyx.jdread.main.common.ViewConfig;
import com.onyx.jdread.main.model.FunctionBarItem;
import com.onyx.jdread.main.model.FunctionBarModel;
import com.onyx.jdread.personal.ui.PersonalFragment;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.setting.ui.SettingFragment;
import com.onyx.jdread.shop.ui.ShopFragment;

/**
 * Created by hehai on 17-12-11.
 */

public class InitReaderViewFunctionBarAction extends BaseReaderAction {
    private FunctionBarModel functionBarModel;
    private RxCallback callback;

    public InitReaderViewFunctionBarAction(FunctionBarModel functionBarModel,RxCallback callback) {
        this.functionBarModel = functionBarModel;
        this.callback = callback;
    }

    @Override
    public void execute(ReaderDataHolder readerDataHolder) {
        functionBarModel.itemModels.clear();
        functionBarModel.itemModels.add(new FunctionBarItem(ViewConfig.FunctionModule.LIBRARY,LibraryFragment.class.getName(), JDReadApplication.getInstance().getString(R.string.reader_menu_catalog), R.mipmap.ic_read_list));
        functionBarModel.itemModels.add(new FunctionBarItem(ViewConfig.FunctionModule.SHOP,ShopFragment.class.getName(), JDReadApplication.getInstance().getString(R.string.reader_menu_progress), R.mipmap.ic_read_pace));
        if (PreferenceManager.getBooleanValue(JDReadApplication.getInstance(), R.string.show_back_tab_key, false)) {
            functionBarModel.itemModels.add(new FunctionBarItem(ViewConfig.FunctionModule.BACK,"back", JDReadApplication.getInstance().getString(R.string.back_name), R.mipmap.ic_undo));
        }
        functionBarModel.itemModels.add(new FunctionBarItem(ViewConfig.FunctionModule.SETTING,SettingFragment.class.getName(), JDReadApplication.getInstance().getString(R.string.reader_menu_brightness), R.mipmap.ic_read_light));
        functionBarModel.itemModels.add(new FunctionBarItem(ViewConfig.FunctionModule.PERSONAL,PersonalFragment.class.getName(), JDReadApplication.getInstance().getString(R.string.setting_name), R.mipmap.ic_read_font));
        if (callback != null) {
            callback.onNext(functionBarModel);
        }
    }
}
