package com.onyx.jdread.reader.request;

import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.reader.dataprovider.ContentSdkDataUtils;
import com.onyx.android.sdk.reader.host.options.BaseOptions;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.reader.data.ReaderDataHolder;

import java.io.File;

/**
 * Created by huxiaomao on 2018/1/13.
 */

public class LoadDocumentOptionsRequest extends ReaderBaseRequest {
    private ReaderDataHolder readerDataHolder;
    private Metadata document;
    private String md5;

    public LoadDocumentOptionsRequest(ReaderDataHolder readerDataHolder) {
        this.readerDataHolder = readerDataHolder;
    }

    @Override
    public LoadDocumentOptionsRequest call() throws Exception {
        md5 = readerDataHolder.getReader().getReaderHelper().getDocumentMd5();
        if (StringUtils.isNullOrEmpty(md5)) {
            md5 = FileUtils.computeMD5(new File(readerDataHolder.getReader().getDocumentInfo().getBookPath()));
        }
        document = ContentSdkDataUtils.getDataProvider().findMetadataByHashTag(JDReadApplication.getInstance().getApplicationContext(),
                readerDataHolder.getReader().getDocumentInfo().getBookPath(),
                md5);
        document.setIdString(md5);
        return this;
    }

    public final BaseOptions getDocumentOptions() {
        final BaseOptions baseOptions = BaseOptions.optionsFromJSONString(document.getExtraAttributes());
        baseOptions.setMd5(md5);
        return baseOptions;
    }
}
