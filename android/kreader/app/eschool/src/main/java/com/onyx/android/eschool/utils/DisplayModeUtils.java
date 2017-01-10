package com.onyx.android.eschool.utils;

import com.onyx.android.eschool.R;
import com.onyx.android.sdk.data.model.Product;
import com.onyx.android.sdk.utils.TestUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by suicheng on 2017/1/3.
 */
public class DisplayModeUtils {

    private static int[] auxiliaryCovers = new int[]{R.drawable.teaching_auxiliary_sample_cover1,
            R.drawable.teaching_auxiliary_sample_cover2, R.drawable.teaching_auxiliary_sample_cover3,
            R.drawable.teaching_auxiliary_sample_cover4, R.drawable.teaching_auxiliary_sample_cover5,
            R.drawable.teaching_auxiliary_sample_cover6, R.drawable.teaching_auxiliary_sample_cover7,
            R.drawable.teaching_auxiliary_sample_cover8, R.drawable.teaching_auxiliary_sample_cover9,
            R.drawable.teaching_auxiliary_sample_cover10, R.drawable.teaching_auxiliary_sample_cover11};

    public static int getTeachingAuxiliaryCover(int position) {
        return auxiliaryCovers[position % auxiliaryCovers.length];
    }

    public static List<Product> getRandomProductList(int count) {
        List<Product> productList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Product product = new Product();
            product.setGuid(TestUtils.randString());
            productList.add(product);
        }
        return productList;
    }
}
