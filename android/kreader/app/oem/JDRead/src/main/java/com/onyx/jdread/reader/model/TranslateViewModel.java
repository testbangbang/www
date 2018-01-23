package com.onyx.jdread.reader.model;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;

import com.onyx.jdread.databinding.ActivityTranslateBinding;
import com.onyx.jdread.reader.dialog.ViewCallBack;

/**
 * Created by huxiaomao on 2018/1/22.
 */

public class TranslateViewModel {
    private ObservableField<String> sourceLanguage = new ObservableField<>();
    private ObservableField<String> targetLanguage = new ObservableField<>();
    private ObservableBoolean srcLanguage = new ObservableBoolean(false);
    private ActivityTranslateBinding binding;
    private ViewCallBack viewCallBack;
    private String text;

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
    }

    public void setBinding(ActivityTranslateBinding binding) {
        this.binding = binding;
    }

    public void setViewCallBack(ViewCallBack viewCallBack) {
        this.viewCallBack = viewCallBack;
    }

    public void exchangeLanguage(){
        if(srcLanguage.get()){

        }else{

        }
    }

    public void translate() {

    }
}
