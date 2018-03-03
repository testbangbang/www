package com.onyx.jdread.reader.model;

import android.databinding.ObservableField;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.onyx.jdread.databinding.ActivityDictBinding;
import com.onyx.jdread.reader.dialog.ViewCallBack;
import com.onyx.jdread.reader.ui.view.AutoPagedWebView;

/**
 * Created by huxiaomao on 2018/1/22.
 */

public class DictViewModel {
    public static final String BAIDU_BAIKE =  "http://wapbaike.baidu.com/search/word?word=";
    public static final String ENCODE = "&pic=1&enc-utf8";
    private ActivityDictBinding binding;
    private ViewCallBack callBack;
    public ObservableField<String> pageNumber = new ObservableField<>();

    public ObservableField<String> getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(String pageNumber) {
        this.pageNumber.set(pageNumber);
    }

    public void setCallBack(ViewCallBack callBack) {
        this.callBack = callBack;
    }

    public void setBinding(ActivityDictBinding binding) {
        this.binding = binding;
    }

    public void loadUrl(String inputWord) {
        String url = BAIDU_BAIKE + inputWord + ENCODE;
        binding.dictView.loadUrl(url);
        binding.dictView.setWebViewClient(new WebViewClient(){

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        binding.dictView.setPageChangedListener(new AutoPagedWebView.PageChangedListener() {
            @Override
            public void onPageChanged(int currentPage, int totalPage) {
                setPageNumber(currentPage + "/" + totalPage);
            }
        });
    }

    public void backClick() {
        callBack.getContent().dismiss();
    }
}
