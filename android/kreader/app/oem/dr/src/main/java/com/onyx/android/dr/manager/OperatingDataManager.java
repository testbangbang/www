package com.onyx.android.dr.manager;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.bean.GoodSentenceBean;
import com.onyx.android.dr.bean.NewWordBean;
import com.onyx.android.dr.common.CommonNotices;
import com.onyx.android.dr.data.BookReportData;
import com.onyx.android.dr.data.GoodSentenceData;
import com.onyx.android.dr.data.InformalEssayData;
import com.onyx.android.dr.data.NewWordData;
import com.onyx.android.dr.data.database.GoodSentenceNoteEntity;
import com.onyx.android.dr.data.database.InformalEssayEntity;
import com.onyx.android.dr.data.database.NewWordNoteBookEntity;
import com.onyx.android.dr.data.database.ReadBookEntity;
import com.onyx.android.dr.data.database.ReaderResponseEntity;
import com.onyx.android.dr.reader.data.ReadSummaryData;
import com.onyx.android.dr.reader.requests.RequestReadBookInsert;
import com.onyx.android.dr.request.local.GoodSentenceInsert;
import com.onyx.android.dr.request.local.GoodSentenceQueryByPageNumber;
import com.onyx.android.dr.request.local.GoodSentenceQueryByReadingMatter;
import com.onyx.android.dr.request.local.InformalEssayInsert;
import com.onyx.android.dr.request.local.NewWordInsert;
import com.onyx.android.dr.request.local.NewWordQueryByPageNumber;
import com.onyx.android.dr.request.local.NewWordQueryByReadingMatter;
import com.onyx.android.dr.request.local.ReaderResponseInsert;
import com.onyx.android.dr.util.TimeUtils;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.model.CreateInformalEssayBean;
import com.onyx.android.sdk.data.model.v2.GetBookReportListBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouzhiming on 2017/8/10.
 */
public class OperatingDataManager {
    private static OperatingDataManager instance = null;
    private List<NewWordNoteBookEntity> newWordList = new ArrayList<NewWordNoteBookEntity>();
    private List<GoodSentenceNoteEntity> goodSentenceList = new ArrayList<GoodSentenceNoteEntity>();
    private List<GetBookReportListBean> data;

    private OperatingDataManager() {
    }

    public static OperatingDataManager getInstance() {
        if (instance == null) {
            synchronized (OperatingDataManager.class) {
                if (instance == null) {
                    instance = new OperatingDataManager();
                }
            }
        }
        return instance;
    }

    public void insertGoodSentence(GoodSentenceBean goodSentenceBean) {
        GoodSentenceData goodSentenceData = new GoodSentenceData();
        GoodSentenceNoteEntity bean = new GoodSentenceNoteEntity();
        bean.currentTime = TimeUtils.getCurrentTimeMillis();
        bean.details = goodSentenceBean.getDetails();
        bean.readingMatter = goodSentenceBean.getReadingMatter();
        bean.pageNumber = goodSentenceBean.getPageNumber();
        bean.goodSentenceType = goodSentenceBean.getGoodSentenceType();
        GoodSentenceInsert req = new GoodSentenceInsert(bean);
        if (req.whetherInsert()) {
            CommonNotices.showMessage(DRApplication.getInstance(), DRApplication.getInstance().getString(R.string.good_sentence_already_exist));
        } else {
            CommonNotices.showMessage(DRApplication.getInstance(), DRApplication.getInstance().getString(R.string.already_add_good_sentence_notebook));
        }
        goodSentenceData.insertGoodSentence(DRApplication.getInstance(), req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
            }
        });
    }

    public void insertNewWord(NewWordBean newWordBean) {
        NewWordData newWordData = new NewWordData();
        NewWordNoteBookEntity bean = new NewWordNoteBookEntity();
        bean.currentTime = TimeUtils.getCurrentTimeMillis();
        bean.newWord = newWordBean.getNewWord();
        bean.dictionaryLookup = newWordBean.getDictionaryLookup();
        bean.readingMatter = newWordBean.getReadingMatter();
        bean.newWordType = newWordBean.getNewWordType();
        bean.pageNumber = newWordBean.getPageNumber();
        bean.paraphrase = newWordBean.getParaphrase();
        final NewWordInsert req = new NewWordInsert(bean);
        if (req.whetherInsert()) {
            CommonNotices.showMessage(DRApplication.getInstance(), DRApplication.getInstance().getString(R.string.new_word_notebook_already_exist));
        } else {
            CommonNotices.showMessage(DRApplication.getInstance(), DRApplication.getInstance().getString(R.string.already_add_new_word_notebook));
        }
        newWordData.insertNewWord(DRApplication.getInstance(), req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
            }
        });
    }

    public void insertReadBook(int speed, String md5short) {
        ReadSummaryData readSummaryData = new ReadSummaryData();
        ReadBookEntity readBookEntity = new ReadBookEntity();
        readBookEntity.currentTime = TimeUtils.getCurrentTimeMillis();
        readBookEntity.averageSpeed = speed;
        readBookEntity.md5short = md5short;
        RequestReadBookInsert req = new RequestReadBookInsert(readBookEntity);
        readSummaryData.saveReadBook(req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
            }
        });
    }

    public void insertReaderResponse(GetBookReportListBean bean) {
        ReaderResponseEntity entity = new ReaderResponseEntity();
        BookReportData bookReportData = new BookReportData();
        entity.bookName = bean.name;
        entity.wordNumber = bean.content.length();
        entity.bookId = bean.book;
        entity.createdAt = bean.createdAt;
        entity.updatedAt = bean.updatedAt;
        entity.cloudId = bean._id;
        entity.content = bean.content;
        entity.pageNumber = bean.pageNumber;
        entity.user = bean.user;
        entity.title = bean.title;
        final ReaderResponseInsert req = new ReaderResponseInsert(entity);
        bookReportData.insertReaderResponse(DRApplication.getInstance(), req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
            }
        });
    }

    public void insertInformalEssay(CreateInformalEssayBean bean) {
        InformalEssayData informalEssayData = new InformalEssayData();
        InformalEssayEntity entity = new InformalEssayEntity();
        entity.currentTime = bean.currentTime;
        entity.title = bean.title;
        entity.wordNumber = bean.wordNumber;
        entity.content = bean.content;
        final InformalEssayInsert req = new InformalEssayInsert(entity);
        informalEssayData.insertInformalEssay(DRApplication.getInstance(), req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
            }
        });
    }

    public void getAllNewWordByReadingMatter(String readingMatter, final BaseCallback callback) {
        NewWordData newWordData = new NewWordData();
        final NewWordQueryByReadingMatter req = new NewWordQueryByReadingMatter(readingMatter);
        newWordData.getAllNewWordByReadingMatter(DRApplication.getInstance(), req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                newWordList = req.getNewWordList();
                invoke(callback, request, e);
            }
        });
    }

    public void getAllNewWordByPageNumber(String pageNumber, final BaseCallback callback) {
        NewWordData newWordData = new NewWordData();
        final NewWordQueryByPageNumber req = new NewWordQueryByPageNumber(pageNumber);
        newWordData.getAllNewWordByPageNumber(DRApplication.getInstance(), req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                newWordList = req.getNewWordList();
                invoke(callback, request, e);
            }
        });
    }

    public void getAllGoodSentenceByReadingMatter(String readingMatter, final BaseCallback callback) {
        GoodSentenceData goodSentenceData = new GoodSentenceData();
        final GoodSentenceQueryByReadingMatter req = new GoodSentenceQueryByReadingMatter(readingMatter);
        goodSentenceData.getGoodSentenceByReadingMatter(DRApplication.getInstance(), req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                goodSentenceList = req.getGoodSentenceList();
                invoke(callback, request, e);
            }
        });
    }

    public void getAllGoodSentenceByPageNumber(String pageNumber, final BaseCallback callback) {
        GoodSentenceData goodSentenceData = new GoodSentenceData();
        final GoodSentenceQueryByPageNumber req = new GoodSentenceQueryByPageNumber(pageNumber);
        goodSentenceData.getGoodSentenceByPageNumber(DRApplication.getInstance(), req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                goodSentenceList = req.getGoodSentenceList();
                invoke(callback, request, e);
            }
        });
    }

    public List<NewWordNoteBookEntity> getNewWordList() {
        return newWordList;
    }

    public List<GoodSentenceNoteEntity> getGoodSentenceList() {
        return goodSentenceList;
    }
}
