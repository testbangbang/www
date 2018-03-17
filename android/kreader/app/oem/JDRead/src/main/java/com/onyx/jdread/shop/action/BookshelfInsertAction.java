package com.onyx.jdread.shop.action;

import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.utils.JSONObjectParseUtils;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.main.common.ClientUtils;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookDetailResultBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookExtraInfoBean;
import com.onyx.jdread.shop.model.ShopDataBundle;
import com.onyx.jdread.shop.request.db.RxRequestBookshelfInsert;
import com.onyx.jdread.shop.request.db.RxSaveCloudBookThumbnailRequest;

import jd.wjlogin_sdk.common.WJLoginHelper;

/**
 * Created by jackdeng on 2017/12/21.
 */

public class BookshelfInsertAction extends BaseAction<ShopDataBundle> {

    private String localPath;
    private BookDetailResultBean.DetailBean bookDetailBean;

    public BookshelfInsertAction(BookDetailResultBean.DetailBean bookDetailBean, String localPath) {
        this.bookDetailBean = bookDetailBean;
        this.localPath = localPath;
    }

    @Override
    public void execute(ShopDataBundle dataBundle, final RxCallback rxCallback) {
        Metadata metadata = convertBookDetailEntityToMetadata(bookDetailBean);
        final RxRequestBookshelfInsert rq = new RxRequestBookshelfInsert(dataBundle.getDataManager(), metadata);
        RxRequestBookshelfInsert.setAppContext(JDReadApplication.getInstance());
        rq.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                invokeNext(rxCallback, BookshelfInsertAction.this);
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                invokeError(rxCallback, throwable);
            }

            @Override
            public void onComplete() {
                super.onComplete();
                invokeComplete(rxCallback);
            }
        });

        RxSaveCloudBookThumbnailRequest rxSaveCloudBookThumbnail = new RxSaveCloudBookThumbnailRequest(dataBundle.getDataManager(), metadata);
        rxSaveCloudBookThumbnail.execute(null);
    }

    private Metadata convertBookDetailEntityToMetadata(BookDetailResultBean.DetailBean detailBean) {
        Metadata metadata = new Metadata();
        metadata.setName(detailBean.name);
        metadata.setAuthors(detailBean.author);
        metadata.setPublisher(detailBean.publisher);
        metadata.setISBN(detailBean.isbn);
        metadata.setDescription(detailBean.info);
        metadata.setNativeAbsolutePath(localPath);
        metadata.setCloudId(String.valueOf(detailBean.ebook_id));
        metadata.setLocation(StringUtils.isNullOrEmpty(detailBean.downLoadUrl) ? detailBean.try_url : detailBean.downLoadUrl);
        metadata.setCoverUrl(detailBean.image_url);
        metadata.setSize((long) detailBean.file_size);
        metadata.setHashTag(localPath);
        metadata.setType(detailBean.format);
        BookExtraInfoBean extraInfo = detailBean.bookExtraInfoBean;
        if (extraInfo != null) {
            extraInfo.key = extraInfo.isWholeBookDownLoad ? detailBean.key : "";
            extraInfo.random = extraInfo.isWholeBookDownLoad ? detailBean.random : "";
            metadata.setSize((long) extraInfo.totalSize);
            metadata.setDownloadInfo(JSONObjectParseUtils.toJson(extraInfo));
            metadata.setFetchSource(extraInfo.isWholeBookDownLoad ? Metadata.FetchSource.CLOUD : Metadata.FetchSource.CLOUD_TRY_READ);
            if (extraInfo.isWholeBookDownLoad) {
                WJLoginHelper wjLoginHelper = ClientUtils.getWJLoginHelper();
                metadata.setExtension(wjLoginHelper.getPin());
            }
        }
        metadata.setIdString(localPath);
        return metadata;
    }
}
