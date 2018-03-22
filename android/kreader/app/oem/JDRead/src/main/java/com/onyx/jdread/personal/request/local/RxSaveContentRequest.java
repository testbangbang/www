package com.onyx.jdread.personal.request.local;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.rxrequest.data.db.RxBaseDBRequest;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.jdread.personal.cloud.entity.jdbean.NoteBean;
import com.onyx.jdread.reader.menu.common.ReaderConfig;

import java.io.File;
import java.util.List;

/**
 * Created by li on 2018/3/14.
 */

public class RxSaveContentRequest extends RxBaseDBRequest {
    private List<NoteBean> noteBeans;
    private File file;
    private boolean result;

    public RxSaveContentRequest(DataManager dm, File file, List<NoteBean> noteBeans) {
        super(dm);
        this.file = file;
        this.noteBeans = noteBeans;
    }

    @Override
    public Object call() throws Exception {
        if (file.exists()) {
            file.delete();
        }
        for (NoteBean noteBean : noteBeans) {
            result = FileUtils.appendContentToFile(noteBean.ebook.info, file);
            FileUtils.appendContentToFile(ReaderConfig.BR + ReaderConfig.BR,file);
        }
        return this;
    }

    public boolean getResult() {
        return result;
    }
}
