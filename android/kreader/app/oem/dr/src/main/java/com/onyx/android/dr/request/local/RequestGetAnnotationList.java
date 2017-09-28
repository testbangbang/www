package com.onyx.android.dr.request.local;

import com.onyx.android.dr.bean.AnnotationStatisticsBean;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.db.ContentDatabase;
import com.onyx.android.sdk.data.model.Annotation;
import com.onyx.android.sdk.data.model.Annotation_Table;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.model.v2.CloudLibrary;
import com.onyx.android.sdk.data.model.v2.CloudLibrary_Table;
import com.onyx.android.sdk.data.model.v2.CloudMetadata;
import com.onyx.android.sdk.data.model.v2.CloudMetadataCollection;
import com.onyx.android.sdk.data.model.v2.CloudMetadataCollection_Table;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.FileUtils;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hehai on 17-9-28.
 */

public class RequestGetAnnotationList extends BaseDataRequest {
    private List<AnnotationStatisticsBean> list = new ArrayList<>();

    public List<AnnotationStatisticsBean> getList() {
        return list;
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        getAnnotation();
    }

    private void getAnnotation() {
        list.clear();
        DatabaseWrapper database = FlowManager.getDatabase(ContentDatabase.NAME).getWritableDatabase();
        database.beginTransaction();
        List<Annotation> annotations = new Select().from(Annotation.class).groupBy(Annotation_Table.idString).queryList();
        if (!CollectionUtils.isNullOrEmpty(annotations)) {
            for (Annotation annotation : annotations) {
                String idString = annotation.getIdString();
                List<Annotation> buffer = new Select().from(Annotation.class).where(Annotation_Table.idString.eq(idString)).queryList();
                if (!CollectionUtils.isNullOrEmpty(buffer)) {
                    AnnotationStatisticsBean bean = new AnnotationStatisticsBean();
                    bean.setTime(buffer.get(buffer.size() - 1).getUpdatedAt());
                    bean.setBook(getBookName(idString));
                    bean.setIdString(idString);
                    bean.setCount(buffer.size());
                    bean.setLibrary(getLibrary(bean.getBook().getGuid()));
                    list.add(bean);
                }
            }
        }
        database.setTransactionSuccessful();
        database.endTransaction();
    }

    private Library getLibrary(String guid) {
        CloudMetadataCollection metadataCollection = new Select().from(CloudMetadataCollection.class).where(CloudMetadataCollection_Table.documentUniqueId.eq(guid)).querySingle();
        if (metadataCollection != null) {
            return new Select().from(CloudLibrary.class).where(CloudLibrary_Table.idString.eq(metadataCollection.getLibraryUniqueId())).querySingle();
        }
        return new CloudLibrary();
    }

    private Metadata getBookName(String idString) {
        List<CloudMetadata> cloudMetadatas = new Select().from(CloudMetadata.class).queryList();
        for (CloudMetadata cloudMetadata : cloudMetadatas) {
            try {
                if (idString.equals(FileUtils.computeMD5(new File(cloudMetadata.getNativeAbsolutePath())))) {
                    return cloudMetadata;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new Metadata();
    }
}
