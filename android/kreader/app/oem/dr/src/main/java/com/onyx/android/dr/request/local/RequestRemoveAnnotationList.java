package com.onyx.android.dr.request.local;

import com.onyx.android.dr.bean.AnnotationStatisticsBean;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.db.ContentDatabase;
import com.onyx.android.sdk.data.model.Annotation;
import com.onyx.android.sdk.data.model.Annotation_Table;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;

import java.util.List;

/**
 * Created by hehai on 17-9-28.
 */

public class RequestRemoveAnnotationList extends BaseDataRequest {
    private List<AnnotationStatisticsBean> list;

    public RequestRemoveAnnotationList(List<AnnotationStatisticsBean> list) {
        this.list = list;
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        removeAnnotation();
    }

    private void removeAnnotation() {
        DatabaseWrapper database = FlowManager.getDatabase(ContentDatabase.NAME).getWritableDatabase();
        database.beginTransaction();
        for (AnnotationStatisticsBean annotationStatisticsBean : list) {
            new Delete().from(Annotation.class).where(Annotation_Table.idString.eq(annotationStatisticsBean.getIdString())).query();
        }
        database.setTransactionSuccessful();
        database.endTransaction();
    }
}
