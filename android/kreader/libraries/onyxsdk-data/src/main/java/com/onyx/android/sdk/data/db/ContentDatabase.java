package com.onyx.android.sdk.data.db;

import android.support.annotation.NonNull;

import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.model.Metadata_Table;
import com.raizlabs.android.dbflow.annotation.Database;
import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.SQLiteType;
import com.raizlabs.android.dbflow.sql.language.property.IndexProperty;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;
import com.raizlabs.android.dbflow.sql.migration.IndexMigration;
import com.raizlabs.android.dbflow.sql.migration.IndexPropertyMigration;

/**
 * Created by zhuzeng on 6/1/16.
 */
@Database(name = ContentDatabase.NAME, version = ContentDatabase.VERSION)
public class ContentDatabase {

    public static final String NAME = "ContentDatabase";
    public static final int VERSION = 2;

    @Migration(version = 2, database = ContentDatabase.class)
    public static class Version2Migration extends AlterTableMigration<Metadata> {
        public Version2Migration(Class<Metadata> table) {
            super(table);
        }

        @Override
        public void onPreMigrate() {
            addColumn(SQLiteType.INTEGER, Metadata_Table.readingStatus.getNameAlias().name());
            addColumn(SQLiteType.TEXT, Metadata_Table.hashTag.getNameAlias().name());
            addColumn(SQLiteType.TEXT, Metadata_Table.storageId.getNameAlias().name());
        }
    }

    @Migration(version = 2, priority = 0, database = ContentDatabase.class)
    public static class IndexMigration2 extends IndexMigration<Metadata> {

        public IndexMigration2(@NonNull Class<Metadata> onTable) {
            super(onTable);
        }

        @NonNull
        @Override
        public String getName() {
            return Metadata_Table.nativeAbsolutePath.getNameAlias().name();
        }
    }


}
