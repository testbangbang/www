package com.onyx.jdread.reader.menu.model;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableInt;

import com.onyx.jdread.R;
import com.onyx.jdread.reader.menu.event.ReaderSettingMenuItemBackPdfEvent;
import com.onyx.jdread.reader.menu.event.ReaderSettingMenuItemCustomizeEvent;

import org.greenrobot.eventbus.EventBus;

import static com.onyx.jdread.reader.menu.model.ReaderTextModel.ReaderTypeface.arialTypeface;
import static com.onyx.jdread.reader.menu.model.ReaderTextModel.ReaderTypeface.boldFaceType;
import static com.onyx.jdread.reader.menu.model.ReaderTextModel.ReaderTypeface.italicsTypeface;
import static com.onyx.jdread.reader.menu.model.ReaderTextModel.ReaderTypeface.roundBodyTypeface;

/**
 * Created by huxiaomao on 2018/1/4.
 */

public class ReaderTextModel {
    private ObservableBoolean isShow = new ObservableBoolean(false);
    private ObservableBoolean isPdf = new ObservableBoolean(false);
    private ObservableInt boldfaceTypefaceBackground = new ObservableInt(R.drawable.reader_text_item_left_selected);
    private ObservableInt arialTypefaceBackground = new ObservableInt(R.drawable.reader_text_item_center_default);
    private ObservableInt italicsTypefaceBackground = new ObservableInt(R.drawable.reader_text_item_center_default);
    private ObservableInt roundBodyTypefaceBackground = new ObservableInt(R.drawable.reader_text_item_center_default);

    public enum ReaderTypeface {
        boldFaceType, arialTypeface, italicsTypeface, roundBodyTypeface
    }

    public ObservableBoolean getIsShow() {
        return isShow;
    }

    public void setIsShow(boolean isShow) {
        this.isShow.set(isShow);
    }

    public ObservableBoolean getIsPdf() {
        return isPdf;
    }

    public void setIsPdf(boolean isPdf) {
        this.isPdf.set(isPdf);
    }

    public void onBackPDFClick() {
        EventBus.getDefault().post(new ReaderSettingMenuItemBackPdfEvent());
    }

    public void onCustomizeItemClick() {
        EventBus.getDefault().post(new ReaderSettingMenuItemCustomizeEvent());
    }

    public void onBoldfaceTypefaceClick() {
        changeTypeface(boldFaceType);
    }

    public void onArialTypefaceClick() {
        changeTypeface(arialTypeface);
    }

    public void onItalicsTypefaceClick() {
        changeTypeface(italicsTypeface);
    }

    public void onRoundBodyTypefaceClick() {
        changeTypeface(roundBodyTypeface);
    }

    private void changeTypeface(ReaderTypeface readerTypeface) {
        setBoldfaceTypefaceBackground(readerTypeface.equals(boldFaceType) ? true : false);
        setArialTypefaceBackground(readerTypeface.equals(arialTypeface) ? true : false);
        setItalicsTypefaceBackground(readerTypeface.equals(italicsTypeface) ? true : false);
        setRoundBodyTypefaceBackground(readerTypeface.equals(roundBodyTypeface) ? true : false);
    }

    public ObservableInt getBoldfaceTypefaceBackground() {
        return boldfaceTypefaceBackground;
    }

    public void setBoldfaceTypefaceBackground(boolean isSelected) {
        if (isSelected) {
            this.boldfaceTypefaceBackground.set(R.drawable.reader_text_item_left_selected);
        } else {
            this.boldfaceTypefaceBackground.set(R.drawable.reader_text_item_left_default);
        }
    }

    public ObservableInt getArialTypefaceBackground() {
        return arialTypefaceBackground;
    }

    public void setArialTypefaceBackground(boolean isSelected) {
        if (isSelected) {
            this.arialTypefaceBackground.set(R.drawable.reader_text_item_center_selected);
        } else {
            this.arialTypefaceBackground.set(R.drawable.reader_text_item_center_default);
        }
    }

    public ObservableInt getItalicsTypefaceBackground() {
        return italicsTypefaceBackground;
    }

    public void setItalicsTypefaceBackground(boolean isSelected) {
        if (isSelected) {
            this.italicsTypefaceBackground.set(R.drawable.reader_text_item_center_selected);
        } else {
            this.italicsTypefaceBackground.set(R.drawable.reader_text_item_center_default);
        }
    }

    public ObservableInt getRoundBodyTypefaceBackground() {
        return roundBodyTypefaceBackground;
    }

    public void setRoundBodyTypefaceBackground(boolean isSelected) {
        if (isSelected) {
            this.roundBodyTypefaceBackground.set(R.drawable.reader_text_item_center_selected);
        } else {
            this.roundBodyTypefaceBackground.set(R.drawable.reader_text_item_center_default);
        }
    }
}
