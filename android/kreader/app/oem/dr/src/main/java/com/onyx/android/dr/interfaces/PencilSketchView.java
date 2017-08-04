package com.onyx.android.dr.interfaces;

import com.onyx.android.sdk.scribble.data.NoteModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouzhiming on 2017/8/3.
 */
public interface PencilSketchView {
    void setPencilSketchData(List<NoteModel> dataList, ArrayList<Boolean> listCheck);
}
