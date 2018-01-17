package com.onyx.jdread.reader.actions;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.utils.PreferenceManager;
import com.onyx.jdread.R;
import com.onyx.jdread.library.ui.LibraryFragment;
import com.onyx.jdread.main.common.ViewConfig;
import com.onyx.jdread.main.model.FunctionBarModel;
import com.onyx.jdread.personal.ui.PersonalFragment;
import com.onyx.jdread.reader.common.ReaderFunctionBarItem;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.menu.event.ReaderFunctionItemBackEvent;
import com.onyx.jdread.reader.menu.event.ReaderFunctionItemBrightnessEvent;
import com.onyx.jdread.reader.menu.event.ReaderFunctionItemCatalogEvent;
import com.onyx.jdread.reader.menu.event.ReaderFunctionItemProgressEvent;
import com.onyx.jdread.reader.menu.event.ReaderFunctionItemSettingEvent;
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
        ReaderFunctionBarItem catalog = new ReaderFunctionBarItem(ViewConfig.FunctionModule.LIBRARY,LibraryFragment.class.getName(),
                readerDataHolder.getAppContext().getString(R.string.reader_menu_catalog), R.mipmap.ic_read_list,new ReaderFunctionItemCatalogEvent());
        functionBarModel.itemModels.add(catalog);
        ReaderFunctionBarItem progress = new ReaderFunctionBarItem(ViewConfig.FunctionModule.SHOP, ShopFragment.class.getName(),
                readerDataHolder.getAppContext().getString(R.string.reader_menu_progress), R.mipmap.ic_read_pace,new ReaderFunctionItemProgressEvent());
        progress.setSelected(true);
        functionBarModel.itemModels.add(progress);
        if (PreferenceManager.getBooleanValue(readerDataHolder.getAppContext(), R.string.show_back_tab_key, false)) {
            ReaderFunctionBarItem back = new ReaderFunctionBarItem(ViewConfig.FunctionModule.BACK,"back",
                    readerDataHolder.getAppContext().getString(R.string.back_name), R.mipmap.ic_undo,new ReaderFunctionItemBackEvent());
            functionBarModel.itemModels.add(back);
        }
        ReaderFunctionBarItem brightness = new ReaderFunctionBarItem(ViewConfig.FunctionModule.SETTING,SettingFragment.class.getName(),
                readerDataHolder.getAppContext().getString(R.string.reader_menu_brightness), R.mipmap.ic_read_light,new ReaderFunctionItemBrightnessEvent());
        functionBarModel.itemModels.add(brightness);
        ReaderFunctionBarItem setting = new ReaderFunctionBarItem(ViewConfig.FunctionModule.PERSONAL,PersonalFragment.class.getName(),
                readerDataHolder.getAppContext().getString(R.string.reader_format), R.mipmap.ic_read_font,new ReaderFunctionItemSettingEvent());
        functionBarModel.itemModels.add(setting);
        if (callback != null) {
            callback.onNext(functionBarModel);
        }
    }
}
