package com.onyx.android.dr;

import android.test.ApplicationTestCase;

import com.onyx.android.dr.data.database.NewWordNoteBookEntity;
import com.onyx.android.dr.request.local.NewWordDelete;
import com.onyx.android.dr.request.local.NewWordInsert;
import com.onyx.android.dr.request.local.NewWordQuery;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.DataManager;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by zhouzhiming on 2017/7/5.
 */

public class NewWordNoteBookTest extends ApplicationTestCase<DRApplication> {
    private String month = "November";
    private String week = "three weeks";
    private String day = "thirty day";
    private String newWord = "book";
    private String dictionaryLookup = "an English-Chinese dictionary";
    private String readingMatter = "princekin";

    public NewWordNoteBookTest() {
        super(DRApplication.class);
    }

    public NewWordNoteBookEntity addNewWord() {
        NewWordNoteBookEntity newWordEntity = new NewWordNoteBookEntity();
        newWordEntity.month = month;
        newWordEntity.week = week;
        newWordEntity.day = day;
        newWordEntity.newWord = newWord;
        newWordEntity.readingMatter = readingMatter;
        newWordEntity.dictionaryLookup = dictionaryLookup;
        return newWordEntity;
    }

    public void testNewWordInsert() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        NewWordNoteBookEntity newWordEntity = addNewWord();
        NewWordInsert insertNewWord = new NewWordInsert(newWordEntity);
        new DataManager().submit(DRApplication.getInstance(), insertNewWord, new BaseCallback() {
            @Override
            public void done(BaseRequest baseRequest, Throwable throwable) {
                assertNull(throwable);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }

    public void testNewWordQuery() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final NewWordQuery queryNewWord = new NewWordQuery();
        new DataManager().submit(DRApplication.getInstance(), queryNewWord, new BaseCallback() {
            @Override
            public void done(BaseRequest baseRequest, Throwable throwable) {
                assertNull(throwable);
                List<NewWordNoteBookEntity> newWordList = queryNewWord.getNewWordList();
                assertQueryResult(newWordList);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }

    public void testNewWordDelete() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        NewWordDelete newWordDelete = new NewWordDelete();
        new DataManager().submit(DRApplication.getInstance(), newWordDelete, new BaseCallback() {
            @Override
            public void done(BaseRequest baseRequest, Throwable throwable) {
                assertNull(throwable);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }

    private void assertQueryResult(List<NewWordNoteBookEntity> newWordList) {
        assertNotNull(newWordList);
        assertTrue(newWordList.size() > 0);
        for (int i = 0; i < newWordList.size(); i++) {
            NewWordNoteBookEntity newWordNoteBookEntity = newWordList.get(i);
            assertEquals(month, newWordNoteBookEntity.month);
            assertEquals(week, newWordNoteBookEntity.week);
            assertEquals(day, newWordNoteBookEntity.day);
            assertEquals(newWord, newWordNoteBookEntity.newWord);
            assertEquals(readingMatter, newWordNoteBookEntity.readingMatter);
            assertEquals(dictionaryLookup, newWordNoteBookEntity.dictionaryLookup);
        }
    }
}
