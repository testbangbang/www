package com.onyx.android.sdk.data.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.onyx.android.sdk.data.db.OnyxCloudDatabase;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;


/**
 * Created by zhuzeng on 11/19/15.
 */
@Table(database = OnyxCloudDatabase.class)
public class Product extends BaseData {

    @Column
    public String title;
    @Column
    public String name;
    @Column
    public String summary;
    @Column
    public String description;
    @Column
    public String company;
    @Column
    public String officialComment;
    @Column
    public String textContent;
    @Column
    public int rs;
    @Column
    public float rating;
    @Column
    public String ownerId;
    @Column
    public String distributeChannel;

    public Set<People> publishers = new HashSet<People>();

    public Set<People> authors = new HashSet<People>();

    @Column
    public String authorString;

    public Set<String> tags = new HashSet<String>();

    public List<Integer> domains = new ArrayList<Integer>();

    public List<String> category = new ArrayList<String>();
    @Column
    public String categoryString;

    public Set<String> formats = new HashSet<String>();

    public Map<String, Map<String, Link>> storage = new HashMap<String, Map<String, Link>>();
    @Column
    public String storageString;

    public String coverUrl;

    public Map<String, Map<String, Link>> covers = new HashMap<String, Map<String, Link>>();
    @Column
    public String coversString;

    public Product() {

    }

    private String toStorageString() {
        if (storage == null) {
            return null;
        }
        return JSON.toJSONString(storage);
    }

    private String toAuthorString() {
        String string = "";
        if (authors == null || authors.size() <= 0) {
            return string;
        }

        final String pattern = ", ";
        for (People people : authors) {
            string += people.fullName;
            string += pattern;
        }

        return string.substring(0, string.length() - pattern.length());
    }

    private String toCategoryString() {
        if (category == null) {
            return "";
        }
        return JSON.toJSONString(category);
    }

    private String toCoversString() {
        if (covers == null) {
            return "";
        }
        return JSON.toJSONString(covers);
    }

    public final String getAuthorString() {
        return authorString;
    }

    private final Map<String, Map<String, Link>> toStorageObject() {
        if ((storage == null || storage.size() <= 0) && storageString != null) {
            storage = JSON.parseObject(storageString, new TypeReference<Map<String, Map<String, Link>>>() {
            });
        }
        return storage;
    }

    private final List<String> toCategoryObject() {
        if ((category == null || category.size() <= 0) && categoryString != null) {
            category = JSON.parseArray(categoryString, String.class);
        }
        return category;
    }

    private final Map<String, Map<String, Link>> toCoversObject() {
        if ((covers == null || covers.size() <= 0) && coversString != null) {
            covers = JSON.parseObject(coversString, new TypeReference<HashMap<String, Map<String, Link>>>() {
            });
        }
        return covers;
    }

    public void beforeSave() {
        authorString = toAuthorString();
        storageString = toStorageString();
        categoryString = toCategoryString();
        coversString = toCoversString();

    }

    public void afterLoad() {
        toStorageObject();
        toCategoryObject();
        toCoversObject();
    }

    /**
     * In order to save in transaction :
     * TransactionManager.getInstance().saveOnSaveQueue(Collection<T> models);
     */
    @Override
    public void save() {
        beforeSave();
        super.save();
    }

    public Link getDownloadLink(final String type, final String provider) {
        if (CollectionUtils.isNullOrEmpty(storage)) {
            return null;
        }
        Map<String, Link> map = storage.get(type);
        if (CollectionUtils.isNullOrEmpty(map)) {
            return null;
        }
        return map.get(provider);
    }

    public Link getFirstDownloadLink() {
        if (CollectionUtils.isNullOrEmpty(storage)) {
            return null;
        }
        Map.Entry<String, Map<String, Link>> entry = storage.entrySet().iterator().next();
        final Map<String, Link> map = entry.getValue();
        if (CollectionUtils.isNullOrEmpty(map)) {
            return null;
        }
        return map.entrySet().iterator().next().getValue();
    }

}
