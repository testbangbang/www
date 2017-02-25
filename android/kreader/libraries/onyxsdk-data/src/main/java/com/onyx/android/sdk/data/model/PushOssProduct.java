package com.onyx.android.sdk.data.model;

import com.onyx.android.sdk.utils.FileUtils;

import java.io.Serializable;

/**
 * Created by suicheng on 2017/1/21.
 */
public class PushOssProduct implements Serializable {

    public OssProduct data;
    public String type;

    public void setResourceData(String resourceFilePath, String resourceKey) {
        PushOssProduct.OssProduct ossProduct = new PushOssProduct.OssProduct();
        ossProduct.name = FileUtils.getBaseName(resourceFilePath);
        ossProduct.title = ossProduct.name;
        ossProduct.resourceType = FileUtils.getFileExtension(resourceFilePath);
        ossProduct.resourceKey = resourceKey;
        ossProduct.resourceDisplayName = FileUtils.getFileName(resourceFilePath);
        data = ossProduct;
    }

    public static class OssProduct extends Product {
        public String resourceType;
        public String resourceKey;
        public String resourceDisplayName;
    }
}
