package com.onyx.android.dr.request.local;

import com.onyx.android.dr.bean.ReadingRateBean;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.model.BaseStatisticsModel;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.model.OnyxStatisticsModel;
import com.onyx.android.sdk.data.model.OnyxStatisticsModel_Table;
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
    private List<ReadingRateBean> readingRateList = new ArrayList<>();
    private ArrayList<Boolean> listCheck = new ArrayList<>();
    private long divisor = 1000*60;

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
            List<OnyxStatisticsModel> statisticsModels = new Select().from(OnyxStatisticsModel.class).where(OnyxStatisticsModel_Table.type.
                    eq(BaseStatisticsModel.DATA_TYPE_PAGE_CHANGE)).and(OnyxStatisticsModel_Table.md5.eq(onyxStatisticsModel.getMd5())).queryList();
            for (OnyxStatisticsModel statisticsModel : statisticsModels) {

                readTimes += statisticsModel.getDurationTime();
            }
            Metadata metadata = getBookName(onyxStatisticsModel.getMd5short());
            String time = DateTimeUtil.formatDate(onyxStatisticsModel.getEventTime(), DateTimeUtil.DATE_FORMAT_YYYYMMDD_HHMM);
            long readMinute = readTimes/divisor;
            ReadingRateBean bean = new ReadingRateBean();
            bean.setBookName(metadata.getTitle());
            bean.setMd5(onyxStatisticsModel.getMd5());
            bean.setTime(time);
            bean.setTimeHorizon(String.valueOf(readMinute));
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
}
