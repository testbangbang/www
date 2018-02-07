package com.onyx.jdread.shop.cloud.entity.jdbean;

import android.databinding.BaseObservable;

/**
 * Created by hehai on 17-3-31.
 */

public class ResultBookBean extends BaseObservable{
    public int ebook_id;
    public String name;
    public String author;
    public String image_url;
    public String large_image_url;
    public String info;
    public String bookType;
    public float file_size;
}
