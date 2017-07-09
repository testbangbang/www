package com.onyx.edu.manager.pinyin;

import com.onyx.edu.manager.model.ContactEntity;

import java.util.Comparator;

public class PinyinComparator implements Comparator<ContactEntity> {

    @Override
    public int compare(ContactEntity o1, ContactEntity o2) {
        return o1.sortLetter.compareTo(o2.sortLetter);
    }
}
