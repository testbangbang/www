package com.onyx.edu.reader.ui.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.request.data.fs.GetFileMd5Request;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;

/**
 * Created by ming on 2017/6/1.
 */

public class GetFileFullMd5Action extends BaseAction {

    private String filePath;
    private String fullFileMd5;

    public GetFileFullMd5Action(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public void execute(ReaderDataHolder readerDataHolder, final BaseCallback baseCallback) {
        final GetFileMd5Request fileMd5Request = new GetFileMd5Request(filePath);
        readerDataHolder.getDataManager().submit(readerDataHolder.getContext(), fileMd5Request, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                fullFileMd5 = fileMd5Request.getMd5();
                BaseCallback.invoke(baseCallback, request, e);
            }
        });
    }

    public String getFullFileMd5() {
        return fullFileMd5;
    }
}