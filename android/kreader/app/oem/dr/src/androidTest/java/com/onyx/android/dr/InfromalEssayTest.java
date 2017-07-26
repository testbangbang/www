package com.onyx.android.dr;

import android.test.ApplicationTestCase;

import com.onyx.android.dr.data.database.InformalEssayEntity;
import com.onyx.android.dr.request.local.InformalEssayDelete;
import com.onyx.android.dr.request.local.InformalEssayInsert;
import com.onyx.android.dr.request.local.InformalEssayQueryAll;
import com.onyx.android.dr.util.TimeUtils;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.DataManager;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by zhouzhiming on 2017/7/5.
 */

public class InfromalEssayTest extends ApplicationTestCase<DRApplication> {
    private String title = "book";
    private String wordNumber = "786 word";
    private String content = "princekin";

    public InfromalEssayTest() {
        super(DRApplication.class);
    }

    public InformalEssayEntity addInfromalEssay() {
        InformalEssayEntity bean = new InformalEssayEntity();
        bean.currentTime = TimeUtils.getCurrentTimeMillis();
        bean.title = title;
        bean.wordNumber = wordNumber;
        bean.content = content;
        return bean;
    }

    public void testInfromalEssayInsert() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        InformalEssayEntity bean = addInfromalEssay();
        InformalEssayInsert insert = new InformalEssayInsert(bean);
        new DataManager().submit(DRApplication.getInstance(), insert, new BaseCallback() {
            @Override
            public void done(BaseRequest baseRequest, Throwable throwable) {
                assertNull(throwable);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }

    public void testInfromalEssayQuery() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final InformalEssayQueryAll queryAll = new InformalEssayQueryAll();
        new DataManager().submit(DRApplication.getInstance(), queryAll, new BaseCallback() {
            @Override
            public void done(BaseRequest baseRequest, Throwable throwable) {
                assertNull(throwable);
                List<InformalEssayEntity> newWordList = queryAll.getAllDatas();
                assertQueryResult(newWordList);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }

    public void testInfromalEssayDelete() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        InformalEssayDelete delete = new InformalEssayDelete();
        new DataManager().submit(DRApplication.getInstance(), delete, new BaseCallback() {
            @Override
            public void done(BaseRequest baseRequest, Throwable throwable) {
                assertNull(throwable);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }

    private void assertQueryResult(List<InformalEssayEntity> newWordList) {
        assertNotNull(newWordList);
        assertTrue(newWordList.size() > 0);
        for (int i = 0; i < newWordList.size(); i++) {
            InformalEssayEntity bean = newWordList.get(i);
            assertEquals(title, bean.title);
            assertEquals(wordNumber, bean.wordNumber);
            assertEquals(content, bean.content);
        }
    }
}
