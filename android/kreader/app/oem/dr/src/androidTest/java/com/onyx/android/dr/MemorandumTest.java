package com.onyx.android.dr;

import android.test.ApplicationTestCase;

import com.onyx.android.dr.data.database.MemorandumEntity;
import com.onyx.android.dr.request.local.MemorandumDelete;
import com.onyx.android.dr.request.local.MemorandumInsert;
import com.onyx.android.dr.request.local.MemorandumQueryAll;
import com.onyx.android.dr.util.TimeUtils;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.DataManager;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by zhouzhiming on 2017/7/5.
 */

public class MemorandumTest extends ApplicationTestCase<DRApplication> {
    private String timeQuantum = "9:00-11:00";
    private String matter = "语文阅读";

    public MemorandumTest() {
        super(DRApplication.class);
    }

    public MemorandumEntity addMemorandum() {
        MemorandumEntity bean = new MemorandumEntity();
        bean.currentTime = TimeUtils.getCurrentTimeMillis();
        bean.timeQuantum = timeQuantum;
        bean.matter = matter;
        return bean;
    }

    public void testInsertData() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        MemorandumEntity bean = addMemorandum();
        MemorandumInsert insert = new MemorandumInsert(bean);
        new DataManager().submit(DRApplication.getInstance(), insert, new BaseCallback() {
            @Override
            public void done(BaseRequest baseRequest, Throwable throwable) {
                assertNull(throwable);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }

    public void testQueryAllData() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final MemorandumQueryAll queryAll = new MemorandumQueryAll();
        new DataManager().submit(DRApplication.getInstance(), queryAll, new BaseCallback() {
            @Override
            public void done(BaseRequest baseRequest, Throwable throwable) {
                assertNull(throwable);
                List<MemorandumEntity> list = queryAll.getAllDatas();
                assertQueryResult(list);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }

    public void testDeleteData() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        MemorandumDelete delete = new MemorandumDelete();
        new DataManager().submit(DRApplication.getInstance(), delete, new BaseCallback() {
            @Override
            public void done(BaseRequest baseRequest, Throwable throwable) {
                assertNull(throwable);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }

    private void assertQueryResult(List<MemorandumEntity> list) {
        assertNotNull(list);
        assertTrue(list.size() > 0);
        for (int i = 0; i < list.size(); i++) {
            MemorandumEntity bean = list.get(i);
            assertEquals(timeQuantum, bean.timeQuantum);
            assertEquals(matter, bean.matter);
        }
    }
}
