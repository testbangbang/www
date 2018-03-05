package com.onyx.jdread.util;

import android.graphics.Bitmap;

import com.facebook.common.references.CloseableReference;
import com.onyx.android.sdk.data.model.DataModel;
import com.onyx.jdread.shop.cloud.entity.jdbean.ResultBookBean;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Map;

/**
 * Created by suicheng on 2018/2/26.
 */
public class DataModelTranslateUtils {

    public static void cloudBookMapToDataModel(EventBus eventBus, List<DataModel> dataModels, List<ResultBookBean> items,
                                               Map<String, CloseableReference<Bitmap>> thumbnailMap) {
        for (ResultBookBean item : items) {
            DataModel dataModel = new DataModel(eventBus);
            dataModel.title.set(item.name);
            dataModel.author.set(item.author);
            dataModel.id.set(item.ebook_id);
            dataModel.cloudId.set(String.valueOf(item.ebook_id));
            dataModel.size.set(item.file_size);
            dataModel.format.set(item.bookType);
            dataModel.desc.set(item.info);
            dataModel.coverBitmap.set(thumbnailMap.get(String.valueOf(item.ebook_id)));
            dataModels.add(dataModel);
        }
    }
}
