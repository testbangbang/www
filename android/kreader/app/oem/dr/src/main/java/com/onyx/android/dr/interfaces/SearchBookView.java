package com.onyx.android.dr.interfaces;


import com.onyx.android.dr.bean.SearchResultBean;

import java.util.List;

/**
 * Created by hehai on 2016/12/16.
 */
public interface SearchBookView {
    void setResult(List<SearchResultBean> result);

    void setHint(List<String> books);

    void setHistory(List<String> books);
}
