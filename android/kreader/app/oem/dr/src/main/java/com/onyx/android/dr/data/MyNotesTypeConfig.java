package com.onyx.android.dr.data;

import android.content.Context;

import com.onyx.android.dr.R;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.device.DeviceConfig;
import com.onyx.android.dr.event.GoodSentenceNotebookEvent;
import com.onyx.android.dr.event.InfromalEssayEvent;
import com.onyx.android.dr.event.MemorandumEvent;
import com.onyx.android.dr.event.NewWordNotebookEvent;
import com.onyx.android.dr.event.PostilEvent;
import com.onyx.android.dr.event.ReadSummaryEvent;
import com.onyx.android.dr.event.ReaderResponseEvent;
import com.onyx.android.dr.event.ReadingRateEvent;
import com.onyx.android.dr.event.SketchEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouzhiming on 2017/7/11.
 */
public class MyNotesTypeConfig {
    public List<MenuBean> myTracksData = new ArrayList<>();
    public List<MenuBean> myThinkData = new ArrayList<>();
    public List<MenuBean> myCreationData = new ArrayList<>();

    public void loadDictInfo(Context context) {
        MenuBean dictData = new MenuBean(DeviceConfig.MyNotesInfo.MY_NOTES_NEW_WORD_NOTEBOOK, context.getResources().getString(R.string.new_word_notebook), R.drawable.new_word_notebook, new NewWordNotebookEvent());
        myTracksData.add(dictData);
        dictData = new MenuBean(DeviceConfig.MyNotesInfo.MY_NOTES_POSTIL, context.getResources().getString(R.string.postil), R.drawable.postil, new PostilEvent());
        myThinkData.add(dictData);
        dictData = new MenuBean(DeviceConfig.MyNotesInfo.MY_NOTES_READER_RESPONSE, context.getResources().getString(R.string.reader_response), R.drawable.reader_response, new ReaderResponseEvent());
        myCreationData.add(dictData);

        dictData = new MenuBean(DeviceConfig.MyNotesInfo.MY_NOTES_GOOD_SENTENCE_NOTEBOOK, context.getResources().getString(R.string.good_sentence_notebook), R.drawable.good_sentence_notebook, new GoodSentenceNotebookEvent(""));
        myTracksData.add(dictData);
        dictData = new MenuBean(DeviceConfig.MyNotesInfo.MY_NOTES_READ_SUMMARY, context.getResources().getString(R.string.read_summary), R.drawable.read_summary, new ReadSummaryEvent());
        myThinkData.add(dictData);
        dictData = new MenuBean(DeviceConfig.MyNotesInfo.MY_NOTES_INFORMAL_ESSAY, context.getResources().getString(R.string.informal_essay), R.drawable.informal_essay, new InfromalEssayEvent());
        myCreationData.add(dictData);

        dictData = new MenuBean(DeviceConfig.MyNotesInfo.MY_NOTES_READING_RATE, context.getResources().getString(R.string.reading_rate), R.drawable.reading_rate, new ReadingRateEvent());
        myTracksData.add(dictData);
        dictData = new MenuBean(DeviceConfig.MyNotesInfo.MY_NOTES_MEMORANDUM, context.getResources().getString(R.string.memorandum), R.drawable.memorandum, new MemorandumEvent());
        myThinkData.add(dictData);
        dictData = new MenuBean(DeviceConfig.MyNotesInfo.MY_NOTES_SKETCH, context.getResources().getString(R.string.sketch), R.drawable.sketch, new SketchEvent());
        myCreationData.add(dictData);
    }

    public List<MenuBean> getMenuData(int userType) {
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
