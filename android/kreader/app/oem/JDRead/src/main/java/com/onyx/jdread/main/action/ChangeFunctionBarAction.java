package com.onyx.jdread.main.action;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.R;
import com.onyx.jdread.main.common.JDPreferenceManager;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.main.common.ViewConfig;
import com.onyx.jdread.main.model.FunctionBarItem;
import com.onyx.jdread.main.model.FunctionBarModel;
import com.onyx.jdread.main.model.MainBundle;

/**
 * Created by hehai on 17-12-11.
 */

public class ChangeFunctionBarAction extends BaseAction<MainBundle> {
    private FunctionBarModel functionBarModel;

    public ChangeFunctionBarAction(FunctionBarModel functionBarModel) {
        this.functionBarModel = functionBarModel;
    }

    @Override
    public void execute(MainBundle mainBundle, RxCallback baseCallback) {
        int index = -1;
        for (int i = 0; i < functionBarModel.itemModels.size() - 1; i++) {
            if (ViewConfig.FunctionModule.BACK == functionBarModel.itemModels.get(i).functionModule.get()) {
                index = i;
            }
        }
        if (JDPreferenceManager.getBooleanValue(R.string.show_back_tab_key, false) && index == -1) {
            functionBarModel.itemModels.add(ResManager.getInteger(R.integer.back_tab_index), new FunctionBarItem(ViewConfig.FunctionModule.BACK, "back", mainBundle.getAppContext().getString(R.string.back_name), R.mipmap.ic_undo));
        } else if (!JDPreferenceManager.getBooleanValue(R.string.show_back_tab_key, false) && index != -1) {
            functionBarModel.itemModels.remove(index);
        }
        if (baseCallback != null) {
            baseCallback.onNext(functionBarModel);
        }
    }
}
