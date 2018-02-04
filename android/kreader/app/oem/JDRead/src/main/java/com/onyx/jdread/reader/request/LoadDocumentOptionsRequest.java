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
    private Reader reader;
    private Metadata document;
    private String md5;

    public LoadDocumentOptionsRequest(Reader reader) {
        this.reader = reader;
    }

    @Override
    public LoadDocumentOptionsRequest call() throws Exception {
        md5 = reader.getReaderHelper().getDocumentMd5();
        if (StringUtils.isNullOrEmpty(md5)) {
            md5 = FileUtils.computeMD5(new File(reader.getDocumentInfo().getBookPath()));
        }
        document = ContentSdkDataUtils.getDataProvider().findMetadataByHashTag(reader.getReaderHelper().getContext(),
                reader.getDocumentInfo().getBookPath(),
                md5);
        document.setIdString(md5);
        return this;
    }

    public final BaseOptions getDocumentOptions() {
        final BaseOptions baseOptions = BaseOptions.optionsFromJSONString(document.getExtraAttributes());
        baseOptions.setMd5(md5);
        String currentPage = baseOptions.getCurrentPage();
        if(StringUtils.isNullOrEmpty(currentPage)){
            baseOptions.setCurrentPage("0");
        }
        return baseOptions;
    }
}
