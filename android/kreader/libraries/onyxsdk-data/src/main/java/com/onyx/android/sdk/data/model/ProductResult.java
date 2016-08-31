package com.onyx.android.sdk.data.model;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.util.List;

/**
 * Created by zhuzeng on 11/19/15.
 */
public class ProductResult<T extends BaseData> {

    public List<T> list;
    public long count;

    static public  ProductResult<Product> parseProduct(final String string) {
        ProductResult<Product> result = JSON.parseObject(string, new TypeReference<ProductResult<Product>>() {
        });
        return result;
    }

    static public ProductResult<Category> parseCategory(final String string) {
        ProductResult<Category> result = JSON.parseObject(string, new TypeReference<ProductResult<Category>>(){});
        return result;
    }

    static public ProductResult<Dictionary> parseDictionary(final String string) {
        ProductResult<Dictionary> result = JSON.parseObject(string, new TypeReference<ProductResult<Dictionary>>() {
        });
        return result;
    }
}
