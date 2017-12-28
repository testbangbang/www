package com.onyx.jdread.personal.model;

import android.content.res.TypedArray;

import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.personal.event.GiftCenterEvent;
import com.onyx.jdread.personal.event.PersonalAccountEvent;
import com.onyx.jdread.personal.event.PersonalBookEvent;
import com.onyx.jdread.personal.event.PersonalNoteEvent;
import com.onyx.jdread.personal.event.PersonalTaskEvent;
import com.onyx.jdread.personal.event.ReadPreferenceEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by li on 2017/12/28.
 */

public class PersonalModel {
    private List<PersonalData> data= new ArrayList<>();
    private Map<String, Object> events = new HashMap<String, Object>(){
        {
            put(JDReadApplication.getInstance().getResources().getString(R.string.personal_task), new PersonalTaskEvent());
            put(JDReadApplication.getInstance().getResources().getString(R.string.personal_account), new PersonalAccountEvent());
            put(JDReadApplication.getInstance().getResources().getString(R.string.personal_notes), new PersonalNoteEvent());
            put(JDReadApplication.getInstance().getResources().getString(R.string.personal_books), new PersonalBookEvent());
            put(JDReadApplication.getInstance().getResources().getString(R.string.read_preference), new ReadPreferenceEvent());
            put(JDReadApplication.getInstance().getResources().getString(R.string.gift_center), new GiftCenterEvent());
        }
    };

    public List<PersonalData> getPersonalData() {
        return data;
    }

    public Map<String, Object> getEvents() {
        return events;
    }

    public void loadPersonalData() {
        String[] personDatas = JDReadApplication.getInstance().getResources().getStringArray(R.array.personal_items);
        TypedArray typedArray = JDReadApplication.getInstance().getResources().obtainTypedArray(R.array.personal_drawables);
        int length = typedArray.length();
        int[] images = new int[length];
        for (int i = 0; i < length; i++) {
            images[i] = typedArray.getResourceId(i, 0);
        }
        typedArray.recycle();
        for (int i = 0; i < personDatas.length; i++) {
            PersonalData personalData = new PersonalData();
            personalData.setTitle(personDatas[i]);
            personalData.setIconImage(images[i]);
            data.add(personalData);
        }
    }
}
