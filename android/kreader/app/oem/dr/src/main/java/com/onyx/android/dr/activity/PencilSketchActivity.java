package com.onyx.android.dr.activity;

import android.support.v7.widget.DividerItemDecoration;
import android.view.View;
import android.widget.TextView;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.adapter.PencilSketchAdapter;
import com.onyx.android.dr.common.ActivityManager;
import com.onyx.android.dr.dialog.TimePickerDialog;
import com.onyx.android.dr.interfaces.PencilSketchView;
import com.onyx.android.dr.presenter.PencilSketchPresenter;
import com.onyx.android.sdk.scribble.data.NoteModel;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by zhouzhiming on 17-7-11.
 */
public class PencilSketchActivity extends BaseActivity implements PencilSketchView, TimePickerDialog.TimePickerDialogInterface {
    @Bind(R.id.sketch_activity_recycler_view)
    PageRecyclerView recyclerView;
    @Bind(R.id.sketch_activity_share)
    TextView sharePencilSketch;
    @Bind(R.id.sketch_activity_export)
    TextView exportPencilSketch;
    @Bind(R.id.sketch_activity_delete)
    TextView deletePencilSketch;
    @Bind(R.id.sketch_activity_new)
    TextView newPencilSketch;
    @Bind(R.id.sketch_activity_check_all)
    TextView checkAll;
    private DividerItemDecoration dividerItemDecoration;
    private PencilSketchAdapter pencilSketchAdapter;
    private PencilSketchPresenter pencilSketchPresenter;
    private List<NoteModel> pencilSketchList;
    private ArrayList<Boolean> listCheck;
    private TimePickerDialog timePickerDialog;

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_pencil_sketch;
    }

    @Override
    protected void initConfig() {
    }

    @Override
    protected void initView() {
        initRecyclerView();
    }

    private void initRecyclerView() {
        dividerItemDecoration =
                new DividerItemDecoration(DRApplication.getInstance(), DividerItemDecoration.VERTICAL);
        pencilSketchAdapter = new PencilSketchAdapter();
        recyclerView.setLayoutManager(new DisableScrollGridManager(DRApplication.getInstance()));
        recyclerView.addItemDecoration(dividerItemDecoration);
    }

    @Override
    protected void initData() {
        pencilSketchList = new ArrayList<NoteModel>();
        listCheck = new ArrayList<>();
        timePickerDialog = new TimePickerDialog(this);
        initEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        pencilSketchPresenter = new PencilSketchPresenter(this);
        pencilSketchPresenter.getAllPencilSketchData();
    }

    @Override
    public void setPencilSketchData(List<NoteModel> dataList, ArrayList<Boolean> checkList) {
        if (dataList == null || dataList.size() <= 0) {
            return;
        }
        pencilSketchList = dataList;
        listCheck = checkList;
        pencilSketchAdapter.setDataList(pencilSketchList, listCheck);
        recyclerView.setAdapter(pencilSketchAdapter);
    }

    public void initEvent() {
        pencilSketchAdapter.setOnItemListener(new PencilSketchAdapter.OnItemClickListener() {
            @Override
            public void setOnItemClick(int position, boolean isCheck) {
                listCheck.set(position, isCheck);
            }

            @Override
            public void setOnItemCheckedChanged(int position, boolean isCheck) {
                listCheck.set(position, isCheck);
            }
        });
    }

    @OnClick({R.id.sketch_activity_share,
            R.id.image_view_back,
            R.id.sketch_activity_delete,
            R.id.sketch_activity_new,
            R.id.sketch_activity_export})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_view_back:
                finish();
                break;
            case R.id.sketch_activity_delete:
                break;
            case R.id.sketch_activity_export:
                break;
            case R.id.sketch_activity_new:
                ActivityManager.startScribbleActivity(this);
                break;
            case R.id.sketch_activity_share:
                break;
        }
    }

    @Override
    public void positiveListener() {
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
