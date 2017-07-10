package com.onyx.android.dr.presenter;

import android.content.Context;

import com.onyx.android.dr.data.MainData;
import com.onyx.android.dr.data.DictFunctionConfig;
import com.onyx.android.dr.data.DictTypeConfig;
import com.onyx.android.dr.interfaces.DictResultShowView;

/**
 * Created by hehai on 17-6-28.
 */

public class DictFunctionPresenter {
    private final DictFunctionConfig functionConfig;
    private final DictTypeConfig dictTypeConfig;
    private DictResultShowView dictView;
    private MainData mainData;

    public DictFunctionPresenter(DictResultShowView dictView) {
        this.dictView = dictView;
        mainData = new MainData();
        functionConfig = new DictFunctionConfig();
        dictTypeConfig = new DictTypeConfig();
    }

    public void loadData(Context context) {
        functionConfig.loadDictInfo(context);
        dictTypeConfig.loadDictInfo(context);
    }

    public void loadTabMenu(int userType) {
        dictView.setDictResultData(functionConfig.getDictData(userType));
    }
    public void loadDictType(int userType) {
        dictView.setDictTypeData(dictTypeConfig.getDictTypeData(userType));
    }
}
