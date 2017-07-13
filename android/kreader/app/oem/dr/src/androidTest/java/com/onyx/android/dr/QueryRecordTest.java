package com.onyx.android.dr;

import android.test.ApplicationTestCase;

import com.onyx.android.dr.data.database.QueryRecordEntity;
import com.onyx.android.dr.request.local.QueryRecordDelete;
import com.onyx.android.dr.request.local.QueryRecordInsert;
import com.onyx.android.dr.request.local.QueryRecordQueryAll;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.DataManager;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by zhouzhiming on 2017/7/5.
 */

public class QueryRecordTest extends ApplicationTestCase<DRApplication> {
    private long timeMillis = System.currentTimeMillis();
    private long time = timeMillis/1000;
    private String word = "book";

    public QueryRecordTest() {
        super(DRApplication.class);
    }

    public QueryRecordEntity addData() {
        QueryRecordEntity queryRecordEntity = new QueryRecordEntity();
        queryRecordEntity.time = time;
        queryRecordEntity.word = word;
        return queryRecordEntity;
    }

    public void testQueryRecordInsert() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        QueryRecordEntity queryRecordEntity = addData();
        QueryRecordInsert insert = new QueryRecordInsert(queryRecordEntity);
        new DataManager().submit(DRApplication.getInstance(), insert, new BaseCallback() {
            @Override
            public void done(BaseRequest baseRequest, Throwable throwable) {
                assertNull(throwable);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }

    public void testQueryRecordQuery() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final QueryRecordQueryAll query = new QueryRecordQueryAll();
        new DataManager().submit(DRApplication.getInstance(), query, new BaseCallback() {
            @Override
            public void done(BaseRequest baseRequest, Throwable throwable) {
                assertNull(throwable);
                List<QueryRecordEntity> list = query.getList();
                assertQueryResult(list);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }

    public void testQueryRecordDelete() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        QueryRecordDelete delete = new QueryRecordDelete();
        new DataManager().submit(DRApplication.getInstance(), delete, new BaseCallback() {
            @Override
            public void done(BaseRequest baseRequest, Throwable throwable) {
                assertNull(throwable);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }

    private void assertQueryResult(List<QueryRecordEntity> list) {
        assertNotNull(list);
        assertTrue(list.size() > 0);
        for (int i = 0; i < list.size(); i++) {
            QueryRecordEntity bean = list.get(i);
            assertEquals(word, bean.word);
        }
    }
}
