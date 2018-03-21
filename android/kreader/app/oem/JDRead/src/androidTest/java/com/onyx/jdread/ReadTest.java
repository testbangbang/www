package com.onyx.jdread;

import android.test.ApplicationTestCase;

import com.alibaba.fastjson.JSON;
import com.onyx.android.sdk.device.EnvironmentUtil;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.personal.cloud.entity.jdbean.ExportNoteBean;
import com.onyx.jdread.personal.cloud.entity.jdbean.ExportNoteResultBean;
import com.onyx.jdread.personal.request.cloud.RxExportNoteRequest;
import com.onyx.jdread.reader.data.MarkerBean;
import com.onyx.jdread.reader.data.NotesDetailBean;
import com.onyx.jdread.reader.data.ReadingData;
import com.onyx.jdread.reader.data.ReadingDataResultBean;
import com.onyx.jdread.personal.request.cloud.RxSyncReadingDataRequest;
import com.onyx.jdread.reader.data.SyncNotesAndLineationRequestBean;
import com.onyx.jdread.reader.data.SyncNotesResultBean;
import com.onyx.jdread.reader.data.UpdateMakerRequestBean;
import com.onyx.jdread.reader.data.UpdateMarkerResultBean;
import com.onyx.jdread.reader.request.RxSyncNotesAndLineationRequest;
import com.onyx.jdread.reader.request.RxUpdateMarkerRequest;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.JDAppBaseInfo;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by li on 2018/2/28.
 */

public class ReadTest extends ApplicationTestCase<JDReadApplication> {
    public ReadTest() {
        super(JDReadApplication.class);
    }

    public void testReadingData() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        JDAppBaseInfo baseInfo = new JDAppBaseInfo();
        String signValue = baseInfo.getSignValue(CloudApiContext.User.READING_DATA);
        baseInfo.setSign(signValue);

        List<ReadingData> readingDataList = new ArrayList<>();
        ReadingData readingData = new ReadingData();
        readingData.length = "11";
        readingData.ebook_id = "4801444";
        readingData.end_chapter = "chapter1";
        readingData.end_para_idx = 7;
        readingData.end_time = "1514719078";
        readingData.start_chapter = "chapter00";
        readingData.start_para_idx = 0;
        readingData.start_time = "1514719066";
        readingDataList.add(readingData);
        String s = JSON.toJSONString(readingDataList);
        MediaType mediaType = MediaType.parse("application/octet-stream");
        RequestBody requestBody = RequestBody.create(mediaType, s);
        final RxSyncReadingDataRequest rq = new RxSyncReadingDataRequest();
        rq.setRequestData(baseInfo, readingDataList);
        rq.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                ReadingDataResultBean resultBean = rq.getResultBean();
                assertNotNull(resultBean);
                countDownLatch.countDown();
            }

            @Override
            public void onError(Throwable throwable) {
                assertNull(throwable);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }

    public void testExportNote() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        JDAppBaseInfo baseInfo = new JDAppBaseInfo();
        String signValue = baseInfo.getSignValue(CloudApiContext.User.EXPORT_NOTE);
        baseInfo.setSign(signValue);

        String path = EnvironmentUtil.getExternalStorageDirectory() + File.separator + "JDRead.zip";
        File file = new File(path);
        if (!file.exists()) {
            return;
        }

        ExportNoteBean noteBean = new ExportNoteBean();
        noteBean.fileCount = "1";
        noteBean.fileName = "JDRead";
        noteBean.fileSize = "171";
        noteBean.sendEmail = "li@onyx-international.com";
        noteBean.file = file;
        final RxExportNoteRequest rq = new RxExportNoteRequest(noteBean, baseInfo);
        rq.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                ExportNoteResultBean resultBean = rq.getResultBean();
                assertNotNull(resultBean);
                countDownLatch.countDown();
            }

            @Override
            public void onError(Throwable throwable) {
                assertNull(throwable);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }

    public void testSyncNotes() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        SyncNotesAndLineationRequestBean requestBean = new SyncNotesAndLineationRequestBean();
        JDAppBaseInfo baseInfo = new JDAppBaseInfo();
        String format = String.format(CloudApiContext.NewBookDetail.SYNC_NOTES, String.valueOf(30190477));
        baseInfo.setSign(baseInfo.getSignValue(format));
        List<NotesDetailBean> detailBeanList = new ArrayList<>();
        NotesDetailBean detailBean = new NotesDetailBean();
        detailBean.content = URLEncoder.encode("aaaaaaaaaaa", Constants.UTF_8);
        detailBean.quote_text = URLEncoder.encode("bbbbbbbbb", Constants.UTF_8);
        detailBean.from_offset_in_para = "10";
        detailBean.to_offset_in_para = "20";
        detailBean.action = "create";  //destroy,update
        detailBean.written_at = baseInfo.getTime();
        detailBean.note_type = 0;  //0 note 1 thought 2 lineation
        detailBean.chapter_name = "e-ink";
        detailBeanList.add(detailBean);

        String s = JSON.toJSONString(detailBeanList);
        RequestBody body = RequestBody.create(MediaType.parse(Constants.PARSE_JSON_TYPE), s);

        requestBean.baseInfoMap = baseInfo.getRequestParamsMap();
        requestBean.bookId = 30190477;
        requestBean.body = body;
        final RxSyncNotesAndLineationRequest rq = new RxSyncNotesAndLineationRequest(requestBean);
        rq.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                SyncNotesResultBean resultBean = rq.getResultBean();
                assertNotNull(resultBean);
                countDownLatch.countDown();
            }

            @Override
            public void onError(Throwable throwable) {
                assertNull(throwable);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }

    public void testUpdateMaker() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        UpdateMakerRequestBean requestBean = new UpdateMakerRequestBean();
        JDAppBaseInfo baseInfo = new JDAppBaseInfo();
        baseInfo.setSign(baseInfo.getSignValue(CloudApiContext.NewBookDetail.BOOK_MARKER));

        MarkerBean markerBean = new MarkerBean();
        markerBean.ebook_id = 30190477;
        markerBean.format = 1;
        markerBean.import_book_id = "";
        markerBean.version = (int) (System.currentTimeMillis() / 1000);

        MarkerBean.ListBean listBean = new MarkerBean.ListBean();
        listBean.action = "created";  //delete
        listBean.data_type = 0; //1,书签；0阅读进度
        listBean.created_at = baseInfo.getTime();
        listBean.offset_in_para = 10;
        listBean.force = 1;
        //listBean.offset = 20;//pdf
        listBean.para_idx = 0;
        listBean.epub_chapter_title = "idagja";
        listBean.percent = "6%";
        List<MarkerBean.ListBean> list = new ArrayList<>();
        list.add(listBean);
        markerBean.list = list;

        List<MarkerBean> markerBeanList = new ArrayList<>();
        markerBeanList.add(markerBean);
        String s = JSON.toJSONString(markerBeanList);
        RequestBody body = RequestBody.create(MediaType.parse(Constants.PARSE_JSON_TYPE), s);
        requestBean.body = body;
        requestBean.baseInfo = baseInfo.getRequestParamsMap();

        final RxUpdateMarkerRequest rq = new RxUpdateMarkerRequest(requestBean);
        rq.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                UpdateMarkerResultBean resultBean = rq.getResultBean();
                assertNotNull(resultBean);
                countDownLatch.countDown();
            }

            @Override
            public void onError(Throwable throwable) {
                assertNull(throwable);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }
}
