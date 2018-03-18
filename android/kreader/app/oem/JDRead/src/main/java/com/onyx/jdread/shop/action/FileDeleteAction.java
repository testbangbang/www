package com.onyx.jdread.shop.action;

import com.onyx.android.sdk.data.rxrequest.data.fs.RxFileDeleteRequest;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.jdread.shop.model.ShopDataBundle;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jackdeng on 2018/3/17.
 */

public class FileDeleteAction extends BaseAction {

    private List<File> fileList;
    private String path;

    public FileDeleteAction(String path) {
        this.path = path;
    }

    public FileDeleteAction(List<File> fileList) {
        this.fileList = fileList;
    }

    @Override
    public void execute(ShopDataBundle dataBundle, final RxCallback rxCallback) {
        if (CollectionUtils.isNullOrEmpty(fileList)) {
            fileList = new ArrayList<>();
            fileList.add(new File(path));
        }
        RxFileDeleteRequest request = new RxFileDeleteRequest(dataBundle.getDataManager(), fileList);
        request.execute(new RxCallback<RxFileDeleteRequest>() {
            @Override
            public void onNext(RxFileDeleteRequest request) {
                invokeNext(rxCallback, FileDeleteAction.this);
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                invokeError(rxCallback, throwable);
            }
        });
    }
}
