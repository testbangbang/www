package com.onyx.android.dr.presenter;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.data.PencilSketchData;
import com.onyx.android.dr.interfaces.PencilSketchView;
import com.onyx.android.dr.request.local.PencilSketchQueryAll;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.data.NoteModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouzhiming on 2017/7/12.
 */
public class PencilSketchPresenter {
    private final PencilSketchView pencilSketchView;
    private PencilSketchData pencilSketchData;
    public List<NoteModel> allData;

    public PencilSketchPresenter(PencilSketchView pencilSketchView) {
        this.pencilSketchView = pencilSketchView;
        pencilSketchData = new PencilSketchData();
    }

    public void getAllPencilSketchData() {
        final PencilSketchQueryAll req = new PencilSketchQueryAll();
        pencilSketchData.getAllPencilSketch(DRApplication.getInstance(), req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                allData = req.getAllData();
                ArrayList<Boolean> checkList = req.getCheckList();
                pencilSketchView.setPencilSketchData(allData, checkList);
            }
        });
    }
}
