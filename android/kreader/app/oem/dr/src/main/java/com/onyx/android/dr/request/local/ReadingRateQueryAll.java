package com.onyx.android.dr.request.local;

import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.data.ReadingRateData;
import com.onyx.android.dr.data.database.ReadBookEntity;
import com.onyx.android.dr.data.database.ReadBookEntity_Table;
import com.onyx.android.dr.data.database.ReaderResponseEntity;
import com.onyx.android.dr.data.database.ReaderResponseEntity_Table;
import com.onyx.android.dr.reader.data.ReadSummaryEntity;
import com.onyx.android.dr.reader.data.ReadSummaryEntity_Table;
import com.onyx.android.dr.request.cloud.CreateReadingRateRequest;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.model.BaseStatisticsModel;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.model.OnyxStatisticsModel;
import com.onyx.android.sdk.data.model.OnyxStatisticsModel_Table;
import com.onyx.android.sdk.data.model.ReadingRateBean;
import com.onyx.android.sdk.data.model.v2.CloudMetadata;
import com.onyx.android.sdk.data.model.v2.CloudMetadata_Table;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;
import com.onyx.android.sdk.utils.DateTimeUtil;
import com.onyx.android.sdk.utils.FileUtils;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouzhiming on 2017/7/6.
 */
public class ReadingRateQueryAll extends BaseDataRequest {
    private final ReadingRateData readingRateData;
    private List<ReadingRateBean> readingRateList = new ArrayList<>();
    private ArrayList<Boolean> listCheck = new ArrayList<>();

    public ReadingRateQueryAll(ReadingRateData readingRateData) {
        this.readingRateData = readingRateData;
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        super.execute(dataManager);
        queryInformalEssayList();
    }

    public List<ReadingRateBean> getAllData() {
        return readingRateList;
    }

    public ArrayList<Boolean> getCheckList() {
        return listCheck;
    }

    public void queryInformalEssayList() {
        List<OnyxStatisticsModel> list = new Select().from(OnyxStatisticsModel.class).groupBy(OnyxStatisticsModel_Table.md5).queryList();
        for (OnyxStatisticsModel onyxStatisticsModel : list) {
            long readTimes = 0;
            String bookName = "";
            String language = "";
            String bookId = "";
            List<OnyxStatisticsModel> statisticsModels = new Select().from(OnyxStatisticsModel.class).where(OnyxStatisticsModel_Table.type.
                    eq(BaseStatisticsModel.DATA_TYPE_PAGE_CHANGE)).and(OnyxStatisticsModel_Table.md5.eq(onyxStatisticsModel.getMd5())).queryList();
            for (OnyxStatisticsModel statisticsModel : statisticsModels) {
                readTimes += statisticsModel.getDurationTime();
            }
            Metadata metadata = getBookName(onyxStatisticsModel.getMd5short());
            int bookSpeed = getBookSpeed(onyxStatisticsModel.getMd5short());
            long readMinute = readTimes/ Constants.divisor;
            if (readMinute == 0) {
                readMinute = 1;
            }
            if (metadata != null) {
                String name = metadata.getName();
                bookName = name.substring(0, name.indexOf("."));
            }
            CloudMetadata typeByBookName = getTypeByBookName(bookName);
            if (typeByBookName != null) {
                language = typeByBookName.getLanguage();
                bookId = typeByBookName.getCloudId();
            }
            int readSummaryPiece = getReadSummaryPiece(bookName);
            Integer readerResponsePiece = getReaderResponsePiece(bookName);
            Integer readerResponseWordNumber = getReaderResponseWordNumber(bookName);
            String time = DateTimeUtil.formatDate(onyxStatisticsModel.getEventTime(), DateTimeUtil.DATE_FORMAT_YYYYMMDD_HHMM);
            ReadingRateBean bean = new ReadingRateBean();
            bean.setName(bookName);
            bean.setBook(bookId);
            bean.setRecordDate(time);
            bean.setSpeed(bookSpeed);
            bean.setWordsCount((int) (bookSpeed * readMinute));
            bean.setReadTimeLong(String.valueOf(readMinute));
            bean.setSummaryCount(readSummaryPiece);
            bean.setLanguage(language);
            bean.setImpressionCount(readerResponsePiece);
            bean.setImpressionWordsCount(readerResponseWordNumber);
            readingRateList.add(bean);
            CreateReadingRateRequest request = new CreateReadingRateRequest(bean);
            readingRateData.createReadingRate(request, new BaseCallback() {
                @Override
                public void done(BaseRequest request, Throwable e) {
                }
            });
        }
    }

    private Metadata getBookName(String idString) {
        List<Metadata> metadataList = new Select().from(Metadata.class).queryList();
        for (Metadata metadata : metadataList) {
            try {
                if (idString.equals(FileUtils.computeMD5(new File(metadata.getNativeAbsolutePath())))) {
                    return metadata;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private int getBookSpeed(String idString) {
        ReadBookEntity readBookEntity = new Select().from(ReadBookEntity.class).where(ReadBookEntity_Table.md5short.eq(idString)).querySingle();
        if (readBookEntity != null) {
            int averageSpeed = readBookEntity.averageSpeed;
            return averageSpeed;
        }
        return 0;
    }

    private int getReadSummaryPiece(String bookName) {
        List<ReadSummaryEntity> notes = new Select().from(ReadSummaryEntity.class).where(ReadSummaryEntity_Table.bookName.eq(bookName)).queryList();
        return notes.size();
    }

    private CloudMetadata getTypeByBookName(String bookName) {
        CloudMetadata cloudMetadata = new Select().from(CloudMetadata.class).where(CloudMetadata_Table.title.eq(bookName)).querySingle();
        return cloudMetadata;
    }

    private Integer getReaderResponsePiece(String bookName) {
        List<ReaderResponseEntity> dataList = new Select().from(ReaderResponseEntity.class).where(ReaderResponseEntity_Table.bookName.eq(bookName)).queryList();
        if (dataList != null && dataList.size() > 0) {
            return dataList.size();
        }
        return 0;
    }

    private Integer getReaderResponseWordNumber(String bookName) {
        List<ReaderResponseEntity> dataList = new Select().from(ReaderResponseEntity.class).where(ReaderResponseEntity_Table.bookName.eq(bookName)).queryList();
        int number = 0;
        if (dataList != null && dataList.size() > 0) {
            for (int i = 0; i < dataList.size(); i++) {
                number += number + dataList.get(i).wordNumber;
            }
        }
        return number;
    }
}
