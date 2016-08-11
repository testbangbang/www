package com.onyx.cloud.model;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.util.List;

/**
 * Created by zhuzeng on 11/19/15.
 */
public class ProductResult<T extends BaseObject> {

    public List<T> list;
    public long count;

    static public  ProductResult<Product> parseProduct(final String string) {
        ProductResult<Product> result = JSON.parseObject(string, new TypeReference<ProductResult<Product>>() {
        });
        return result;
    }

    static public ProductResult<ProductContainer> parseCategory(final String string) {
        ProductResult<ProductContainer> result = JSON.parseObject(string, new TypeReference<ProductResult<ProductContainer>>(){});
        return result;
    }

    static public ProductResult<Dictionary> parseDictionary(final String string) {
        ProductResult<Dictionary> result = JSON.parseObject(string, new TypeReference<ProductResult<Dictionary>>() {
        });
        return result;
    }
}
