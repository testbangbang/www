package com.onyx.jdread.personal.action;

import android.graphics.Bitmap;

import com.facebook.common.references.CloseableReference;
import com.onyx.android.sdk.data.DataManagerHelper;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.rxrequest.data.db.RxMetadataRequest;
import com.onyx.android.sdk.data.utils.JSONObjectParseUtils;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.personal.cloud.entity.jdbean.PersonalBookBean;
import com.onyx.jdread.personal.model.PersonalDataBundle;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookExtraInfoBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by li on 2018/3/12.
 */

public class ImportAction extends BaseAction {
    private QueryArgs queryArgs;
    private long count;
    private List<PersonalBookBean> books = new ArrayList<>();

    public ImportAction(QueryArgs queryArgs) {
        this.queryArgs = queryArgs;
    }

    @Override
    public void execute(PersonalDataBundle dataBundle, final RxCallback rxCallback) {
        if (books.size() > 0) {
            books.clear();
        }
        final RxMetadataRequest rq = new RxMetadataRequest(dataBundle.getDataManager(), queryArgs, true);
        rq.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                count = rq.getCount();
                List<Metadata> list = rq.getList();
                Map<String, CloseableReference<Bitmap>> thumbnailBitmap = rq.getThumbnailBitmap();
                appendBitmap(list, thumbnailBitmap);
                RxCallback.invokeNext(rxCallback, ImportAction.this);
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                RxCallback.invokeError(rxCallback, throwable);
            }
        });
    }

    private void appendBitmap(List<Metadata> list, Map<String, CloseableReference<Bitmap>> thumbnailBitmap) {
        for (Metadata metadata : list) {
            PersonalBookBean bookBean = new PersonalBookBean();
            BookExtraInfoBean bean = new BookExtraInfoBean();
            bean.percentage = 100;
            metadata.setDownloadInfo(JSONObjectParseUtils.toJson(bean));
            bookBean.metadata = metadata;
            CloseableReference<Bitmap> bitmap = thumbnailBitmap.get(metadata.getAssociationId());
            if (bitmap != null) {
                bookBean.bitmap = bitmap;
            }
            bookBean.total = count;
            books.add(bookBean);
        }
    }

    public List<PersonalBookBean> getBooks() {
        return books;
    }
}
