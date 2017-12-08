package com.onyx.android.sdk.data.converter;

import com.alibaba.fastjson.serializer.ValueFilter;
import com.onyx.android.sdk.data.SortBy;
import com.onyx.android.sdk.data.model.v2.CloudMetadata_Table;
import com.raizlabs.android.dbflow.sql.language.property.BaseProperty;

/**
 * Created by suicheng on 2017/5/5.
 */

public class QueryArgsFilter implements ValueFilter {

    @Override
    public Object process(Object source, String name, Object value) {
        if ("sortBy".equals(name)) {
            return getString((SortBy) value);
        }
        return value;
    }

    protected String getString(SortBy sortBy) {
        String defaultSortBy = getName(CloudMetadata_Table.name);
        switch (sortBy) {
            case Name:
                return defaultSortBy;
            case CreationTime:
                return getName(CloudMetadata_Table.createdAt);
            case BookTitle:
                return getName(CloudMetadata_Table.title);
            case Author:
                return getName(CloudMetadata_Table.authors);
            case Publisher:
                return getName(CloudMetadata_Table.publisher);
            case Size:
                return getName(CloudMetadata_Table.size);
            default:
                return sortBy.name();
        }
    }

    public static String getName(BaseProperty property) {
        return property.getNameAlias().newBuilder().shouldAddIdentifierToName(false).build().name();
    }
}