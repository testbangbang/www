package com.onyx.jdread.setting.request;

import android.content.Intent;
import android.content.pm.PackageInfo;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.rxrequest.data.fs.RxBaseFSRequest;
import com.onyx.jdread.setting.model.DictionaryModel;
import com.onyx.jdread.setting.utils.Constants;

import java.util.List;

/**
 * Created by hehai on 2018/1/16.
 */
public class RxDictionaryListLoadRequest extends RxBaseFSRequest {

    private DictionaryModel dictionaryModel;

    private boolean testAppExist = false;

    public RxDictionaryListLoadRequest(DataManager dataManager, DictionaryModel dictionaryModel) {
        super(dataManager);
        this.dictionaryModel = dictionaryModel;
    }

    public boolean isTestAppExist() {
        return testAppExist;
    }

    @Override
    public RxDictionaryListLoadRequest call() throws Exception {
        dictionaryModel.list.clear();
        processLoadApplication();
        return this;
    }

    private void processLoadApplication() throws Exception {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<PackageInfo> allInstalledPackageList = getAppContext().getPackageManager().getInstalledPackages(0);
        for (PackageInfo packageInfo : allInstalledPackageList) {
            final String packageName = packageInfo.applicationInfo.packageName;
            if (Constants.dictionaryPackageSet.contains(packageName)) {
                DictionaryModel.DictionaryItem dictionaryItem = new DictionaryModel.DictionaryItem(packageInfo.applicationInfo.loadLabel(getAppContext().getPackageManager()).toString(), packageName);
                dictionaryModel.list.add(dictionaryItem);
            }
        }
    }
}
