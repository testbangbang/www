package com.onyx.kreader.tests;

import android.test.ActivityInstrumentationTestCase2;
import com.alibaba.fastjson.JSON;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.provider.LocalDataProvider;
import com.onyx.android.sdk.reader.host.options.BaseOptions;

import java.util.UUID;

/**
 * Created by zhuzeng on 6/3/16.
 */
public class DocumentTest extends ActivityInstrumentationTestCase2<ReaderTestActivity> {

    public DocumentTest() {
        super(ReaderTestActivity.class);
    }

    public void testSave() {
        final String md5 = UUID.randomUUID().toString();
        BaseOptions origin = new BaseOptions();
        origin.setPassword(UUID.randomUUID().toString());
        LocalDataProvider localDataProvider = new LocalDataProvider();
        assertTrue(localDataProvider.saveDocumentOptions(getActivity(), "", md5, JSON.toJSONString(origin)));

        final Metadata result = localDataProvider.findMetadataByHashTag(getActivity(), "", md5);
        assertNotNull(result);
        final BaseOptions resultOptions = JSON.parseObject(result.getExtraAttributes(), BaseOptions.class);
        assertEquals(origin.getPassword(), resultOptions.getPassword());
    }


    public void testSave2() {
        final String md5 = UUID.randomUUID().toString();
        BaseOptions origin = new BaseOptions();
        origin.setPassword(UUID.randomUUID().toString());
        LocalDataProvider localDataProvider = new LocalDataProvider();
        assertTrue(localDataProvider.saveDocumentOptions(getActivity(), "", md5, JSON.toJSONString(origin)));

        final String wrongMd5 = UUID.randomUUID().toString();
        final Metadata result = localDataProvider.findMetadataByHashTag(getActivity(), "", wrongMd5);
        assertNotNull(result);
        final BaseOptions resultOptions = JSON.parseObject(result.getExtraAttributes(), BaseOptions.class);
        assertNotNull(origin.getPassword(), resultOptions.getPassword());
    }


}
