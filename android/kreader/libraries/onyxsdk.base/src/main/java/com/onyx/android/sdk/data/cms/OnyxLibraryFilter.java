package com.onyx.android.sdk.data.cms;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import com.alibaba.fastjson.JSON;
import com.onyx.android.sdk.data.util.CursorUtil;
import com.onyx.android.sdk.data.util.NotImplementedException;
import com.onyx.android.sdk.utils.StringUtils;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created by zhuzeng on 11/19/15.
 */
public class OnyxLibraryFilter {

    private final static String TAG = OnyxLibraryFilter.class.getSimpleName();
    public static final String DB_TABLE_NAME = "library_filter";
    public static final Uri CONTENT_URI = Uri.parse("content://" + OnyxCmsCenter.PROVIDER_AUTHORITY + "/" + DB_TABLE_NAME);

    public static class Columns implements BaseColumns {
        public static final String FILTER_UNIQUE_ID = "uniqueId";
        public static final String FILTER_NAME = "name";
        public static final String FILTER_DESCRIPTION = "desc";
        public static final String FILTER_QUERY_STRING = "queryString";
        public static final String FILTER_EXTRA_ATTRIBUTES = "attributes";
        public static final String FILTER_PARENT = "parent";
        public static final String CREATED_AT = "createdAt";
        public static final String UPDATED_AT = "updatedAt";

        private static boolean sColumnIndexesInitialized = false;
        private static int sColumnID = -1;
        private static int sColumnFilterUniqueId = -1;
        private static int sColumnFilterName = -1;
        private static int sColumnFilterDescription = -1;
        private static int sColumnFilterQueryString = -1;
        private static int sColumnFilterExtraAttributes = -1;
        private static int sColumnFilterParent = -1;
        private static int sColumnCreatedAt = -1;
        private static int sColumnUpdatedAt = -1;

        public static final String DEFAULT_ORDER_BY = Columns.FILTER_NAME;

        public static ContentValues createColumnData(final String uid, final String name, final String desc, final String queryString, final String attributes, final long parent) {
            ContentValues values = new ContentValues();
            values.put(FILTER_UNIQUE_ID, uid);
            values.put(FILTER_NAME, name);
            values.put(FILTER_DESCRIPTION, desc);
            values.put(FILTER_QUERY_STRING, queryString);
            values.put(FILTER_EXTRA_ATTRIBUTES, attributes);
            values.put(FILTER_PARENT, parent);
            return values;
        }

        public static ContentValues createColumnData(final OnyxLibraryFilter filter) {
            return createColumnData(filter.getUniqueId(), filter.getName(), filter.getDescription(), filter.getQueryString(), filter.getExtraAttributes(), filter.getParentId());
        }

        public static OnyxLibraryFilter readColumnData(ContentValues columnData) {
            throw new NotImplementedException();
        }

        public static void readColumnData(Cursor c, OnyxLibraryFilter item) {
            if (!sColumnIndexesInitialized) {
                sColumnID = c.getColumnIndex(_ID);
                sColumnFilterUniqueId = c.getColumnIndex(FILTER_UNIQUE_ID);
                sColumnFilterName = c.getColumnIndex(FILTER_NAME);
                sColumnFilterDescription = c.getColumnIndex(FILTER_DESCRIPTION);
                sColumnFilterQueryString = c.getColumnIndex(FILTER_QUERY_STRING);
                sColumnFilterExtraAttributes = c.getColumnIndex(FILTER_EXTRA_ATTRIBUTES);
                sColumnFilterParent = c.getColumnIndex(FILTER_PARENT);
                sColumnIndexesInitialized = true;
            }

            long id = CursorUtil.getLong(c, sColumnID);
            final String uid = CursorUtil.getString(c, sColumnFilterUniqueId);
            final String name = CursorUtil.getString(c, sColumnFilterName);
            final String desc = CursorUtil.getString(c, sColumnFilterDescription);
            final String queryString = CursorUtil.getString(c, sColumnFilterQueryString);
            final String attributes = CursorUtil.getString(c, sColumnFilterExtraAttributes);
            long pid = CursorUtil.getLong(c, sColumnFilterParent);

            item.setUniqueId(uid);
            item.setId(id);
            item.setName(name);
            item.setDescription(desc);
            item.setQueryString(queryString);
            item.setExtraAttributes(attributes);
            item.setParentId(pid);
        }

        public static OnyxLibraryFilter readColumnData(Cursor c)  {
            OnyxLibraryFilter filter = new OnyxLibraryFilter();
            readColumnData(c, filter);
            return filter;
        }
    }

    private long id = 0;
    private String uniqueId = null;
    private String name = null;
    private String description = null;
    private String queryString = null;
    private String extraAttributes = null;
    private long parentId = 0;

    public OnyxLibraryFilter() {
    }

    public OnyxLibraryFilter(final String uniqueId, final String name, final String desc, final String queryString, final String attributes, final long pid) {
        setUniqueId(uniqueId);
        setName(name);
        setDescription(desc);
        setQueryString(queryString);
        setExtraAttributes(attributes);
        setParentId(pid);
    }

    public long getId() {
        return id;
    }

    public void setId(long value) {
        id = value;
    }

    public String generateUniqueId() {
        if (StringUtils.isNullOrEmpty(uniqueId)) {
            uniqueId = UUID.randomUUID().toString();
        }
        return uniqueId;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(final String value) {
        uniqueId = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String desc) {
        description = desc;
    }

    public void setQueryString(final String string) {
        queryString = string;
    }

    /**
     * get query json string.
     * @return
     */
    public String getQueryString() {
        return queryString;
    }

    public QueryCriteria getQueryCriteria() {
        return QueryCriteria.fromQueryString(getQueryString());
    }

    public void setQueryCriteria(final QueryCriteria criteria) {
        if (criteria != null) {
            queryString = QueryCriteria.toQueryString(criteria);
        }
    }

    public void setExtraAttributes(final String string) {
        extraAttributes = string;
    }

    public String getExtraAttributes() {
        return extraAttributes;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(final long pid) {
        parentId = pid;
    }

    public static class QueryCriteria {
        public Set<String> fileType;
        public Set<String> author;
        public Set<String> title;
        public Set<String> tags;
        public Set<String> series;

        public QueryCriteria() {

        }

        static public final QueryCriteria fromQueryString(final String string) {
            QueryCriteria criteria = null;
            try {
                criteria = JSON.parseObject(string, QueryCriteria.class);
            } catch (Exception e) {
            } finally {
                return criteria;
            }
        }

        static public final String toQueryString(final QueryCriteria criteria) {
            return JSON.toJSONString(criteria);
        }
    }




}
