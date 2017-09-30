package com.onyx.android.dr.request.local;

import com.onyx.android.dr.data.database.ReaderResponseEntity;
import com.onyx.android.dr.data.database.ReaderResponseEntity_Table;
import com.onyx.android.dr.data.database.ReadingRateEntity;
import com.onyx.android.dr.reader.data.ReadSummaryEntity;
import com.onyx.android.dr.reader.data.ReadSummaryEntity_Table;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.model.BaseStatisticsModel;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.model.OnyxStatisticsModel;
import com.onyx.android.sdk.data.model.OnyxStatisticsModel_Table;
import com.onyx.android.sdk.data.model.v2.CloudMetadata;
import com.onyx.android.sdk.data.model.v2.CloudMetadata_Table;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;
import com.onyx.android.sdk.utils.FileUtils;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouzhiming on 2017/7/6.
 */
public class ReadingRateQueryAll extends BaseDataRequest {
    private List<ReadingRateEntity> readingRateList = new ArrayList<>();
    private ArrayList<Boolean> listCheck = new ArrayList<>();
    private long divisor = 1000*60;

    @Override
    public void execute(DataManager dataManager) throws Exception {
        super.execute(dataManager);
        queryInformalEssayList();
    }

    public List<ReadingRateEntity> getAllData() {
        return readingRateList;
    }

    public ArrayList<Boolean> getCheckList() {
        return listCheck;
    }

    public void queryInformalEssayList() {
        List<OnyxStatisticsModel> list = new Select().from(OnyxStatisticsModel.class).groupBy(OnyxStatisticsModel_Table.md5).queryList();
        for (OnyxStatisticsModel onyxStatisticsModel : list) {
            long readTimes = 0;
            List<OnyxStatisticsModel> statisticsModels = new Select().from(OnyxStatisticsModel.class).where(OnyxStatisticsModel_Table.type.
                    eq(BaseStatisticsModel.DATA_TYPE_PAGE_CHANGE)).and(OnyxStatisticsModel_Table.md5.eq(onyxStatisticsModel.getMd5())).queryList();
            for (OnyxStatisticsModel statisticsModel : statisticsModels) {
                readTimes += statisticsModel.getDurationTime();
            }
            Metadata metadata = getBookName(onyxStatisticsModel.getMd5short());
            long readMinute = readTimes/divisor;
            String bookName = metadata.getTitle();
            int readSummaryPiece = getReadSummaryPiece(bookName);
            CloudMetadata typeByBookName = getTypeByBookName(bookName);
            Integer readerResponsePiece = getReaderResponsePiece(bookName);
            Integer readerResponseWordNumber = getReaderResponseWordNumber(bookName);
            ReadingRateEntity bean = new ReadingRateEntity();
            bean.bookName = bookName;
            bean.md5 = onyxStatisticsModel.getMd5();
            bean.time = onyxStatisticsModel.getEventTime();
            bean.timeHorizon = String.valueOf(readMinute);
            bean.readSummaryPiece = readSummaryPiece;
            bean.language = typeByBookName.getLanguage();
            bean.readerResponsePiece = readerResponsePiece;
            bean.readerResponseNumber = readerResponseWordNumber;
            readingRateList.add(bean);
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
