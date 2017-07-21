package com.onyx.android.dr;

import android.test.ApplicationTestCase;

import com.onyx.android.dr.data.database.GoodSentenceNoteEntity;
import com.onyx.android.dr.request.local.GoodSentenceExcerptDelete;
import com.onyx.android.dr.request.local.GoodSentenceExcerptInsert;
import com.onyx.android.dr.request.local.GoodSentenceExcerptQueryAll;
import com.onyx.android.dr.util.TimeUtils;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.DataManager;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by zhouzhiming on 2017/7/5.
 */

public class ExcerptGoodSentenceTest extends ApplicationTestCase<DRApplication> {
    private String details = "It is a beautiful dog.";
    private String readingMatter = "an English-Chinese dictionary";
    private String pageNumber = "fifth page";

    public ExcerptGoodSentenceTest() {
        super(DRApplication.class);
    }

    public GoodSentenceNoteEntity addGoodSentence() {
        GoodSentenceNoteEntity goodSentence = new GoodSentenceNoteEntity();
        goodSentence.currentTime = TimeUtils.getCurrentTimeMillis();
        goodSentence.details = details;
        goodSentence.readingMatter = readingMatter;
        goodSentence.pageNumber = pageNumber;
        return goodSentence;
    }

    public void testGoodSentenceInsert() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        GoodSentenceNoteEntity goodSentence = addGoodSentence();
        GoodSentenceExcerptInsert insertGoodSentence = new GoodSentenceExcerptInsert(goodSentence);
        new DataManager().submit(DRApplication.getInstance(), insertGoodSentence, new BaseCallback() {
            @Override
            public void done(BaseRequest baseRequest, Throwable throwable) {
                assertNull(throwable);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }

    public void testGoodSentenceQuery() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final GoodSentenceExcerptQueryAll queryGoodSentence = new GoodSentenceExcerptQueryAll();
        new DataManager().submit(DRApplication.getInstance(), queryGoodSentence, new BaseCallback() {
            @Override
            public void done(BaseRequest baseRequest, Throwable throwable) {
                assertNull(throwable);
                List<GoodSentenceNoteEntity> goodSentenceList = queryGoodSentence.getGoodSentenceList();
                assertQueryResult(goodSentenceList);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }

    public void testGoodSentenceDelete() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        GoodSentenceExcerptDelete goodSentenceDelete = new GoodSentenceExcerptDelete();
        new DataManager().submit(DRApplication.getInstance(), goodSentenceDelete, new BaseCallback() {
            @Override
            public void done(BaseRequest baseRequest, Throwable throwable) {
                assertNull(throwable);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }

    private void assertQueryResult(List<GoodSentenceNoteEntity> goodSentenceList) {
        assertNotNull(goodSentenceList);
        assertTrue(goodSentenceList.size() > 0);
        for (int i = 0; i < goodSentenceList.size(); i++) {
            GoodSentenceNoteEntity goodSentenceNoteEntity = goodSentenceList.get(i);
            assertEquals(details, goodSentenceNoteEntity.details);
            assertEquals(readingMatter, goodSentenceNoteEntity.readingMatter);
            assertEquals(pageNumber, goodSentenceNoteEntity.pageNumber);
        }
    }
}
