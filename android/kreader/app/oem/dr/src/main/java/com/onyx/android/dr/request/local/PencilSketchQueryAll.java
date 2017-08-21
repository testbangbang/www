package com.onyx.android.dr.request.local;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;
import com.onyx.android.sdk.scribble.data.NoteModel;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouzhiming on 2017/8/3.
 */
public class PencilSketchQueryAll extends BaseDataRequest {
    private List<NoteModel> pencilSketchList;
    private ArrayList<Boolean> listCheck = new ArrayList<>();

    @Override
    public void execute(DataManager dataManager) throws Exception {
        super.execute(dataManager);
        queryPencilSketchList();
    }

    public List<NoteModel> getAllData() {
        return pencilSketchList;
    }

    public void setAllData(List<NoteModel> pencilSketchList) {
        this.pencilSketchList = pencilSketchList;
    }

    public ArrayList<Boolean> getCheckList() {
        return listCheck;
    }

    public void queryPencilSketchList() {
        List<NoteModel> sketchList = new Select().from(NoteModel.class).queryList();
        if (sketchList != null && sketchList.size() > 0) {
            setAllData(sketchList);
            listCheck.clear();
            for (int i = 0; i < sketchList.size(); i++) {
                listCheck.add(false);
            }
        }
    }
}
