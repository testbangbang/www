package com.onyx.android.dr.reader.requests;

import com.onyx.android.dr.data.database.ReadBookEntity;
import com.onyx.android.dr.data.database.ReadBookEntity_Table;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;
import com.raizlabs.android.dbflow.sql.language.Select;

/**
 * Created by hehai on 17-8-24.
 */
public class RequestReadBookInsert extends BaseDataRequest {
    private ReadBookEntity readBookEntity;

    public RequestReadBookInsert(ReadBookEntity readBookEntity) {
        this.readBookEntity = readBookEntity;
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        ReadBookEntity entity = new Select().from(ReadBookEntity.class).where(ReadBookEntity_Table.md5short.eq(readBookEntity.md5short)).querySingle();
        if (entity != null) {
            entity.averageSpeed = (entity.averageSpeed + readBookEntity.averageSpeed) / 2;
            entity.currentTime = readBookEntity.currentTime;
            entity.update();
        } else {
            readBookEntity.insert();
        }
    }
}
