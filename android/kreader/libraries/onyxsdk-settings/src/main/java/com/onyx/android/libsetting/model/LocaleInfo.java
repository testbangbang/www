package com.onyx.android.libsetting.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.support.annotation.NonNull;

import com.onyx.android.libsetting.BR;

import java.text.Collator;
import java.util.Locale;


/**
 * Created by solskjaer49 on 2016/12/5 17:22.
 */

public class LocaleInfo extends BaseObservable implements Comparable<LocaleInfo> {

    private static final Collator sCollator = Collator.getInstance();
    private String label;
    private Locale locale;

    public LocaleInfo(String label, Locale locale) {
        this.label = label;
        this.locale = locale;
    }

    @Bindable
    public String getLabel() {
        return label;
    }

    @Bindable
    public Locale getLocale() {
        return locale;
    }

    @Override
    public String toString() {
        return this.label;
    }

    @Override
    public int compareTo(@NonNull LocaleInfo another) {
        return sCollator.compare(this.label, another.label);
    }

    public LocaleInfo setLocale(Locale locale) {
        this.locale = locale;
        notifyPropertyChanged(BR.locale);
        return this;
    }

    public LocaleInfo setLabel(String label) {
        this.label = label;
        notifyPropertyChanged(BR.label);
        return this;
    }
}
