package com.onyx.android.dr.data;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.QueryResult;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.request.cloud.v2.CloudChildLibraryListLoadRequest;
import com.onyx.android.sdk.data.request.cloud.v2.CloudContentListRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hehai on 17-8-2.
 */

public class EBookStoreData {
    private Map<String, List<Metadata>> languageCategoryMap = new HashMap<>();

    public void getRootLibraryList(CloudChildLibraryListLoadRequest request, BaseCallback baseCallback) {
        DRApplication.getCloudStore().submitRequest(DRApplication.getInstance(), request, baseCallback);
    }

    public void getLanguageBooks(final CloudContentListRequest req, final BaseCallback baseCallback) {
        DRApplication.getCloudStore().submitRequest(DRApplication.getInstance(), req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                QueryResult<Metadata> productResult = req.getProductResult();
                organizeData(languageCategoryMap,productResult);
                invoke(baseCallback, request, e);
            }
        });
    }

    public static void organizeData(Map<String, List<Metadata>> languageCategoryMap,  QueryResult<Metadata> productResult) {
        List<Metadata> chineseList = new ArrayList<>();
        List<Metadata> englishList = new ArrayList<>();
        List<Metadata> smallList = new ArrayList<>();
        languageCategoryMap.clear();
        if (productResult != null && productResult.list != null) {
            List<Metadata> list = productResult.list;
            for (Metadata metadata : list) {
                if (Constants.CHINESE.equals(metadata.getLanguage())) {
                    chineseList.add(metadata);
                } else if (Constants.ENGLISH.equals(metadata.getLanguage())) {
                    englishList.add(metadata);
                } else {
                    smallList.add(metadata);
                }
            }
            if (chineseList.size() > 0) {
                languageCategoryMap.put(Constants.CHINESE, chineseList);
            }
            if (englishList.size() > 0) {
                languageCategoryMap.put(Constants.ENGLISH, englishList);
            }
            if (smallList.size() > 0) {
                languageCategoryMap.put(Constants.SMALL_LANGUAGE, smallList);
            }
        }
    }

    public Map<String, List<Metadata>> getLanguageCategoryMap() {
        return languageCategoryMap;
    }
}
