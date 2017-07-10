package com.onyx.android.dr;

import android.test.ApplicationTestCase;

import com.onyx.android.dr.data.database.GoodSentenceNoteEntity;
import com.onyx.android.dr.request.local.GoodSentenceExcerptDelete;
import com.onyx.android.dr.request.local.GoodSentenceExcerptInsert;
import com.onyx.android.dr.request.local.GoodSentenceExcerptQuery;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.DataManager;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by zhouzhiming on 2017/7/5.
 */

public class ExcerptGoodSentenceTest extends ApplicationTestCase<DRApplication> {
    private String month = "November";
    private String week = "three weeks";
    private String day = "thirty day";
    private String details = "It is a beautiful dog.";
    private String readingMatter = "an English-Chinese dictionary";
    private String pageNumber = "fifth page";
    private String recitation = "yes";

    public ExcerptGoodSentenceTest() {
        super(DRApplication.class);
    }

    public GoodSentenceNoteEntity addGoodSentence() {
        GoodSentenceNoteEntity goodSentence = new GoodSentenceNoteEntity();
        goodSentence.month = month;
        goodSentence.week = week;
        goodSentence.day = day;
        goodSentence.details = details;
        goodSentence.readingMatter = readingMatter;
        goodSentence.pageNumber = pageNumber;
        goodSentence.recitation = recitation;
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
        final GoodSentenceExcerptQuery queryGoodSentence = new GoodSentenceExcerptQuery();
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
            assertEquals(month, goodSentenceNoteEntity.month);
            assertEquals(week, goodSentenceNoteEntity.week);
            assertEquals(day, goodSentenceNoteEntity.day);
            assertEquals(details, goodSentenceNoteEntity.details);
            assertEquals(readingMatter, goodSentenceNoteEntity.readingMatter);
            assertEquals(pageNumber, goodSentenceNoteEntity.pageNumber);
            assertEquals(recitation, goodSentenceNoteEntity.recitation);
        }
    }
}
