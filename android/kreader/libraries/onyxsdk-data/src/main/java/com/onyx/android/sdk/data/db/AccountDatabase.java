package com.onyx.android.sdk.data.db;

import com.onyx.android.sdk.data.model.v2.EduAccount;
import com.onyx.android.sdk.data.model.v2.EduAccount_Table;
import com.raizlabs.android.dbflow.annotation.Database;
import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.SQLiteType;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;

/**
 * Created by zhuzeng on 9/2/16.
 */
@Database(name = AccountDatabase.NAME, version = AccountDatabase.VERSION)
public class AccountDatabase {

    public static final String NAME = "AccountDatabase";

    public static final int VERSION = 2;

    @Migration(version = 2, database = AccountDatabase.class)
    public static class Version2Migration extends AlterTableMigration<EduAccount> {
        public Version2Migration(Class<EduAccount> table) {
            super(table);
        }

        @Override
        public void onPreMigrate() {
            addColumn(SQLiteType.TEXT, EduAccount_Table._id.getNameAlias().name());
        }
    }
}
