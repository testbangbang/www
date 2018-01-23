package com.onyx.jdread.reader.model;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.databinding.ActivityTranslateBinding;
import com.onyx.jdread.reader.dialog.ViewCallBack;
import com.onyx.jdread.reader.ui.view.HTMLReaderWebView;
import com.onyx.jdread.setting.action.TranslateAction;
import com.onyx.jdread.setting.model.SettingBundle;

/**
 * Created by huxiaomao on 2018/1/22.
 */

public class TranslateViewModel {
    private ObservableField<String> sourceLanguage = new ObservableField<>();
    private ObservableField<String> targetLanguage = new ObservableField<>();
    private ObservableBoolean currentLanguage = new ObservableBoolean(true);
    private ActivityTranslateBinding binding;
    private ViewCallBack viewCallBack;
    private String text;
    private ObservableField<String> page = new ObservableField<>();

    public ObservableField<String> getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page.set(page);
    }

    public ObservableBoolean getCurrentLanguage() {
        return currentLanguage;
    }

    public void setCurrentLanguage(boolean currentLanguage) {
        this.currentLanguage.set(currentLanguage);
    }

    public ObservableField<String> getSourceLanguage() {
        return sourceLanguage;
    }

    public void setSourceLanguage(String sourceLanguage) {
        this.sourceLanguage.set(sourceLanguage);
    }

    public ObservableField<String> getTargetLanguage() {
        return targetLanguage;
    }

    public void setTargetLanguage(String targetLanguage) {
        this.targetLanguage.set(targetLanguage);
    }

    public void setText(String text) {
        this.text = text;
        currentLanguage.set(StringUtils.isChinese(text));
        translate();
    }

    public void setBinding(ActivityTranslateBinding binding) {
        this.binding = binding;
        binding.translateView.registerOnOnPageChangedListener(new HTMLReaderWebView.OnPageChangedListener() {
            @Override
            public void onPageChanged(int totalPage, int curPage) {
                updatePageNumber(totalPage, curPage);
            }
        });
    }

    private void updatePageNumber(int totalPage, int curPage) {
        setPage(curPage + "/" + totalPage);
    }

    public void setViewCallBack(ViewCallBack viewCallBack) {
        this.viewCallBack = viewCallBack;
    }

    private void translate() {
        final TranslateAction action = new TranslateAction(text);
        action.execute(SettingBundle.getInstance(), new RxCallback() {
            @Override
            public void onNext(Object o) {
                updateTranslateResult(action.getTranslateResult());
            }
        });
    }

    public void updateTranslateResult(String result) {
        binding.translateView.loadData(result, "text/html; charset=UTF-8", null);
    }
}
