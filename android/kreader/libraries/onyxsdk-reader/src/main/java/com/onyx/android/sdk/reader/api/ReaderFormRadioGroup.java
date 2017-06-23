package com.onyx.android.sdk.reader.api;

import com.onyx.android.sdk.utils.Debug;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by joy on 5/22/17.
 */

public class ReaderFormRadioGroup extends ReaderFormField {

    public enum GroupType { Single, Multiple }

    private GroupType groupType = GroupType.Single;
    private ArrayList<ReaderFormRadioButton> buttons = new ArrayList<>();

    private ReaderFormRadioGroup(String name) {
        super(name);
    }

    public static ReaderFormRadioGroup create(String name, ReaderFormRadioButton[] buttons) {
        ReaderFormRadioGroup group = new ReaderFormRadioGroup(name);
        group.buttons.addAll(Arrays.asList(buttons));
        return group;
    }

    public GroupType getGroupType() {
        return groupType;
    }

    public List<ReaderFormRadioButton> getButtons() {
        return Collections.unmodifiableList(buttons);
    }
}
