package com.onyx.android.eschool.holder;

import android.content.Context;

import com.onyx.android.eschool.R;
import com.onyx.android.eschool.model.LibraryViewInfo;
import com.onyx.android.sdk.data.BookFilter;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.SortBy;
import com.onyx.android.sdk.data.SortOrder;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.model.Metadata_Table;
import com.onyx.android.sdk.data.utils.QueryBuilder;
import com.onyx.android.sdk.device.EnvironmentUtil;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.raizlabs.android.dbflow.sql.language.ConditionGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by suicheng on 2017/4/15.
 */

public class LibraryDataHolder extends BaseDataHolder {

    private DataManager dataManager = new DataManager();
    private LibraryViewInfo libraryViewInfo = new LibraryViewInfo();

    public LibraryDataHolder(Context context) {
        super(context);
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public LibraryViewInfo getLibraryViewInfo() {
        return libraryViewInfo;
    }

    public void setLibraryViewInfo(LibraryViewInfo libraryViewInfo) {
        this.libraryViewInfo = libraryViewInfo;
    }
}
