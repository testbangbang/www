package com.onyx.kreader.tests;

import android.test.ActivityInstrumentationTestCase2;
import com.onyx.kreader.dataprovider.DocumentOptions;
import com.onyx.kreader.dataprovider.DocumentOptionsProvider;
import com.onyx.kreader.host.options.BaseOptions;

import java.util.UUID;

/**
 * Created by zhuzeng on 6/3/16.
 */
public class DocumentOptionsTest  extends ActivityInstrumentationTestCase2<ReaderTestActivity> {

    public DocumentOptionsTest() {
        super(ReaderTestActivity.class);
    }

    public void testSave() {
        final String md5 = UUID.randomUUID().toString();
        BaseOptions origin = new BaseOptions();
        origin.setPassword(UUID.randomUUID().toString());
        assertTrue(DocumentOptionsProvider.saveDocumentOptions(getActivity(), "", md5, origin));

        final DocumentOptions result = DocumentOptionsProvider.loadDocumentOptions(getActivity(), "", md5);
        assertNotNull(result);
        assertEquals(origin.getPassword(), result.getBaseOptions().getPassword());
    }


    public void testSave2() {
        final String md5 = UUID.randomUUID().toString();
        BaseOptions origin = new BaseOptions();
        origin.setPassword(UUID.randomUUID().toString());
        assertTrue(DocumentOptionsProvider.saveDocumentOptions(getActivity(), "", md5, origin));

        final String wrongMd5 = UUID.randomUUID().toString();
        final DocumentOptions result = DocumentOptionsProvider.loadDocumentOptions(getActivity(), "", wrongMd5);
        assertNotNull(result);
        assertNotNull(origin.getPassword(), result.getBaseOptions().getPassword());
    }


}
