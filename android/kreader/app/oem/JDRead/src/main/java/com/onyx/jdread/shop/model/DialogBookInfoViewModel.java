package com.onyx.jdread.shop.model;

import android.databinding.ObservableField;
import android.databinding.ObservableInt;

/**
 * Created by jackdeng on 2018/1/19.
 */

public class DialogBookInfoViewModel {
    public final ObservableField<String> title = new ObservableField<>();
    public final ObservableField<String> content = new ObservableField<>();
    public final ObservableInt currentPage = new ObservableInt();
    public final ObservableInt totalPage = new ObservableInt();
}
