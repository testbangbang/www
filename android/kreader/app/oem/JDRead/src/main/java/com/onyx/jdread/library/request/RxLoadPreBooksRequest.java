package com.onyx.jdread.library.request;

import android.graphics.Bitmap;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.db.ContentDatabase;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.rxrequest.data.db.RxBaseDBRequest;
import com.onyx.android.sdk.data.utils.ThumbnailUtils;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.R;
import com.onyx.jdread.library.model.PreBookBean;
import com.onyx.jdread.main.common.ResManager;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;

import java.io.File;
import java.util.List;

/**
 * Created by hehai on 18-3-14.
 */

public class RxLoadPreBooksRequest extends RxBaseDBRequest {
    private String preBooksPath;

    public RxLoadPreBooksRequest(DataManager dm, String preBooksPath) {
        super(dm);
        this.preBooksPath = preBooksPath;
    }

    @Override
    public RxLoadPreBooksRequest call() throws Exception {
        File file = new File(preBooksPath);
        if (StringUtils.isNullOrEmpty(preBooksPath) || !file.exists() || file.isFile()) {
            return this;
        }
        File config = new File(preBooksPath, "preBooksConfig.txt");
        if (config.exists()) {
            String s = FileUtils.readContentOfFile(config);
            List<PreBookBean> beans = JSONObject.parseArray(s, PreBookBean.class);
            DatabaseWrapper database = FlowManager.getDatabase(ContentDatabase.NAME).getWritableDatabase();
            database.beginTransaction();
            try {
                for (PreBookBean bean : beans) {
                    String filePath = preBooksPath + bean.fileName;
                    if (FileUtils.fileExist(filePath)) {
                        Metadata metadata = Metadata.createFromFile(filePath);
                        metadata.setName(bean.name);
                        getDataProvider().saveMetadata(getAppContext(), metadata);
                        Bitmap bitmap = Glide.with(getAppContext()).load(new File(preBooksPath + bean.cover)).asBitmap().into(
                                ResManager.getInteger(R.integer.cloud_book_cover_width), ResManager.getInteger(R.integer.cloud_book_cover_height)).get();
                        ThumbnailUtils.insertThumbnail(getAppContext(), getDataProvider(), metadata.getNativeAbsolutePath(), metadata.getAssociationId(), bitmap);
                    }
                }
                database.setTransactionSuccessful();
            } finally {
                database.endTransaction();
            }
        }
        return this;
    }
}
