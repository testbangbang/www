package com.onyx.android.dr.data.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by hehai on 17-8-1.
 */

public class AddressDBHelper extends SQLiteOpenHelper {
    private static final String NAME = "AddressDatabase.db";
    private static final int VERSION = 1;

    public AddressDBHelper(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
