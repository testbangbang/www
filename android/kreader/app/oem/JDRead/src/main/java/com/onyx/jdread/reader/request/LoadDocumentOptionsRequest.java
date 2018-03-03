package com.onyx.jdread.reader.request;

import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.reader.dataprovider.ContentSdkDataUtils;
import com.onyx.android.sdk.reader.host.options.BaseOptions;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.reader.data.Reader;

import java.io.File;

/**
 * Created by huxiaomao on 2018/1/13.
 */

public class LoadDocumentOptionsRequest extends ReaderBaseRequest {
    private Metadata document;
    private String md5;

    public LoadDocumentOptionsRequest(Reader reader) {
        super(reader);
    }

    @Override
    public LoadDocumentOptionsRequest call() throws Exception {
        md5 = getReader().getReaderHelper().getDocumentMd5();
        if (StringUtils.isNullOrEmpty(md5)) {
            md5 = FileUtils.computeMD5(new File(getReader().getDocumentInfo().getBookPath()));
        }
        document = ContentSdkDataUtils.getDataProvider().findMetadataByHashTag(getReader().getReaderHelper().getContext(),
                getReader().getDocumentInfo().getBookPath(),
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
