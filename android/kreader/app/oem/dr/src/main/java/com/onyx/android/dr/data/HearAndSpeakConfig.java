package com.onyx.android.dr.data;

import android.content.Context;

import com.onyx.android.dr.R;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.device.DeviceConfig;
import com.onyx.android.dr.event.ArticleRepeatAfterEvent;
import com.onyx.android.dr.event.SpeechRecordingEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouzhiming on 2017/7/11.
 */
public class HearAndSpeakConfig {
    public List<MenuBean> hearAndSpeakData = new ArrayList<>();

    public void loadDictInfo(Context context) {
        MenuBean dictData = new MenuBean(DeviceConfig.MyNotesInfo.MY_NOTES_NEW_WORD_NOTEBOOK, context.getResources().getString(R.string.article_repeat_after), R.drawable.article_repeat_after, new ArticleRepeatAfterEvent());
        hearAndSpeakData.add(dictData);
        dictData = new MenuBean(DeviceConfig.MyNotesInfo.MY_NOTES_POSTIL, context.getResources().getString(R.string.speech_recording), R.drawable.speech_recording, new SpeechRecordingEvent());
        hearAndSpeakData.add(dictData);
    }

    public List<MenuBean> getMenuData(int userType) {
        switch (userType) {
            case Constants.ACCOUNT_HEAR_AND_SPEAK:
                return hearAndSpeakData;
        }
        return hearAndSpeakData;
    }
}
