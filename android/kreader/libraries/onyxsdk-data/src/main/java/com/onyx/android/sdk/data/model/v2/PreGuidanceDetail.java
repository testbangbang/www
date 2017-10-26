package com.onyx.android.sdk.data.model.v2;

import com.onyx.android.sdk.data.model.BaseData;
import com.onyx.android.sdk.utils.CollectionUtils;

import java.util.List;

/**
 * Created by suicheng on 2017/10/23.
 */
public class PreGuidanceDetail extends BaseData {
    public String _id;
    public String title;
    public String info;
    public String subject;
    public boolean needReply;
    public List<CloudMetadata> resources;

    public CloudMetadata getResource(int index) {
        if (index >= CollectionUtils.getSize(resources)) {
            return null;
        }
        return resources.get(index);
    }
}
