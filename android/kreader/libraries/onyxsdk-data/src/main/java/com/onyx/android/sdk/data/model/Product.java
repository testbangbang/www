package com.onyx.android.sdk.data.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.onyx.android.sdk.data.converter.ListIntegerConverter;
import com.onyx.android.sdk.data.converter.ListStringConverter;
import com.onyx.android.sdk.data.converter.SetPeopleConverter;
import com.onyx.android.sdk.data.converter.SetStringConverter;
import com.onyx.android.sdk.data.converter.StorageConverter;
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

    @Column(typeConverter = SetPeopleConverter.class)
    public Set<People> publishers = new HashSet<People>();

    @Column(typeConverter = SetPeopleConverter.class)
    public Set<People> authors = new HashSet<People>();

    @Column(typeConverter = SetStringConverter.class)
    public Set<String> tags = new HashSet<String>();

    @Column(typeConverter = ListIntegerConverter.class)
    public List<Integer> domains = new ArrayList<Integer>();

    @Column(typeConverter = ListStringConverter.class)
    public List<String> category = new ArrayList<String>();

    @Column(typeConverter = SetStringConverter.class)
    public Set<String> formats = new HashSet<String>();

    @Column(typeConverter = StorageConverter.class)
    public Map<String, Map<String, Link>> storage = new HashMap<String, Map<String, Link>>();

    @Column(typeConverter = StorageConverter.class)
    public Map<String, Map<String, Link>> covers = new HashMap<String, Map<String, Link>>();
    @Column
    public String coverUrl;

    public Product() {

    }

    public String getAuthorString() {
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

    /**
     * In order to save in transaction :
     * TransactionManager.getInstance().saveOnSaveQueue(Collection<T> models);
     */
    @Override
    public void save() {
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
