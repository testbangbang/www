package com.onyx.android.sdk.data.model;

import com.onyx.android.sdk.data.db.OnyxStatisticsDatabase;
import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.SQLiteType;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;

/**
 * Created by ming on 2017/2/20.
 */

@Migration(version = 2, database = OnyxStatisticsDatabase.class)
public class Migration_2_OnyxStatisticsModel extends AlterTableMigration<OnyxStatisticsModel> {


    public Migration_2_OnyxStatisticsModel(Class<OnyxStatisticsModel> table) {
        super(table);
    }

    @Override
    public void onPreMigrate() {
        addColumn(SQLiteType.TEXT, OnyxStatisticsModel_Table.comment.getNameAlias().name());
        addColumn(SQLiteType.INTEGER, OnyxStatisticsModel_Table.score.getNameAlias().name());
    }
}


