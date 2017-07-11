package com.onyx.android.dr.data;

import android.content.Context;

import com.onyx.android.dr.R;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.device.DeviceConfig;
import com.onyx.android.dr.event.ChineseQueryEvent;
import com.onyx.android.dr.event.EnglishQueryEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouzhiming on 17-6-28.
 */

public class ＭyNotesTypeConfig {
    public List<MenuData> myTracksData = new ArrayList<>();
    public List<MenuData> myThinkData = new ArrayList<>();
    public List<MenuData> myCreationData = new ArrayList<>();

    public void loadDictInfo(Context context) {
        MenuData dictData = new MenuData(DeviceConfig.MyNotesInfo.MY_NOTES_NEW_WORD_NOTEBOOK, context.getResources().getString(R.string.english_query), R.drawable.ic_books, new EnglishQueryEvent());
        myTracksData.add(dictData);
        dictData = new MenuData(DeviceConfig.MyNotesInfo.MY_NOTES_POSTIL, context.getResources().getString(R.string.chinese_query), R.drawable.ic_books, new ChineseQueryEvent());
        myThinkData.add(dictData);
        dictData = new MenuData(DeviceConfig.MyNotesInfo.MY_NOTES_READER_RESPONSE, context.getResources().getString(R.string.chinese_query), R.drawable.ic_books, new ChineseQueryEvent());
        myCreationData.add(dictData);

        dictData = new MenuData(DeviceConfig.MyNotesInfo.MY_NOTES_GOOD_SENTENCE_NOTEBOOK, context.getResources().getString(R.string.chinese_query), R.drawable.ic_books, new ChineseQueryEvent());
        myTracksData.add(dictData);
        dictData = new MenuData(DeviceConfig.MyNotesInfo.MY_NOTES_READ_SUMMARY, context.getResources().getString(R.string.chinese_query), R.drawable.ic_books, new ChineseQueryEvent());
        myThinkData.add(dictData);
        dictData = new MenuData(DeviceConfig.MyNotesInfo.MY_NOTES_INFORMAL_ESSAY, context.getResources().getString(R.string.chinese_query), R.drawable.ic_books, new ChineseQueryEvent());
        myCreationData.add(dictData);

        dictData = new MenuData(DeviceConfig.MyNotesInfo.MY_NOTES_READING_RATE, context.getResources().getString(R.string.chinese_query), R.drawable.ic_books, new ChineseQueryEvent());
        myTracksData.add(dictData);
        dictData = new MenuData(DeviceConfig.MyNotesInfo.MY_NOTES_MEMORANDUM, context.getResources().getString(R.string.chinese_query), R.drawable.ic_books, new ChineseQueryEvent());
        myThinkData.add(dictData);
        dictData = new MenuData(DeviceConfig.MyNotesInfo.MY_NOTES_SKETCH, context.getResources().getString(R.string.chinese_query), R.drawable.ic_books, new ChineseQueryEvent());
        myCreationData.add(dictData);
    }

    public List<MenuData> getMenuData(int userType) {
        switch (userType) {
            case Constants.ACCOUNT_TYPE_MY_TRACKS:
                return myTracksData;
            case Constants.ACCOUNT_TYPE_MY_THINK:
                return myThinkData;
            case Constants.ACCOUNT_TYPE_MY_CREATION:
                return myCreationData;
        }
        return myTracksData;
    }
}
