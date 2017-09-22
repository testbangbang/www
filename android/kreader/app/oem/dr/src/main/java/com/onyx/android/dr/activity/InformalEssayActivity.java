package com.onyx.android.dr.activity;

import android.support.v7.widget.DividerItemDecoration;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.adapter.InformalEssayAdapter;
import com.onyx.android.dr.common.ActivityManager;
import com.onyx.android.dr.common.CommonNotices;
import com.onyx.android.dr.data.database.InformalEssayEntity;
import com.onyx.android.dr.event.ExportHtmlFailedEvent;
import com.onyx.android.dr.event.ExportHtmlSuccessEvent;
import com.onyx.android.dr.interfaces.InformalEssayView;
import com.onyx.android.dr.presenter.InformalEssayPresenter;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by zhouzhiming on 17-7-11.
 */
public class InformalEssayActivity extends BaseActivity implements InformalEssayView {
    @Bind(R.id.infromal_essay_activity_recyclerview)
    PageRecyclerView recyclerView;
    @Bind(R.id.image)
    ImageView image;
    @Bind(R.id.image_view_back)
    ImageView imageViewBack;
    @Bind(R.id.title_bar_title)
    TextView title;
    @Bind(R.id.title_bar_right_icon_four)
    ImageView iconFour;
    @Bind(R.id.title_bar_right_icon_three)
    ImageView iconThree;
    @Bind(R.id.title_bar_right_icon_two)
    ImageView iconTwo;
    @Bind(R.id.good_sentence_activity_all_check)
    CheckBox allCheck;
    @Bind(R.id.good_sentence_activity_all_number)
    TextView allNumber;
    private DividerItemDecoration dividerItemDecoration;
    private InformalEssayAdapter informalEssayAdapter;
    private InformalEssayPresenter informalEssayPresenter;
    private List<InformalEssayEntity> informalEssayList;
    private ArrayList<Boolean> listCheck;

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_infromal_essay;
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
        informalEssayAdapter = new InformalEssayAdapter();
        recyclerView.setLayoutManager(new DisableScrollGridManager(DRApplication.getInstance()));
        recyclerView.addItemDecoration(dividerItemDecoration);
    }

    @Override
    protected void initData() {
        informalEssayList = new ArrayList<>();
        listCheck = new ArrayList<>();
        getIntentData();
        initEvent();
    }

    private void getIntentData() {
        image.setImageResource(R.drawable.informal_essay);
        title.setText(getString(R.string.informal_essay));
        iconFour.setVisibility(View.VISIBLE);
        iconThree.setVisibility(View.VISIBLE);
        iconTwo.setVisibility(View.VISIBLE);
        iconFour.setImageResource(R.drawable.ic_reader_note_delet);
        iconThree.setImageResource(R.drawable.ic_reader_note_export);
        iconTwo.setImageResource(R.drawable.ic_reader_note_diary_set);
    }

    @Override
    protected void onResume() {
        super.onResume();
        informalEssayPresenter = new InformalEssayPresenter(getApplicationContext(), this);
        informalEssayPresenter.getAllInformalEssayData();
    }

    @Override
    public void setInformalEssayData(List<InformalEssayEntity> dataList, ArrayList<Boolean> checkList) {
        if (dataList == null || dataList.size() <= 0) {
            return;
        }
        informalEssayList = dataList;
        listCheck = checkList;
        allNumber.setText(getString(R.string.informal_essay_activity_all_number) + informalEssayList.size() + getString(R.string.data_unit));
        informalEssayAdapter.setDataList(informalEssayList, listCheck);
        recyclerView.setAdapter(informalEssayAdapter);
    }

    public void initEvent() {
        informalEssayAdapter.setOnItemListener(new InformalEssayAdapter.OnItemClickListener() {
            @Override
            public void setOnItemClick(int position, boolean isCheck) {
                listCheck.set(position, isCheck);
            }

            @Override
            public void setOnItemCheckedChanged(int position, boolean isCheck) {
                listCheck.set(position, isCheck);
            }
        });
        allCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isCheck) {
                if (isCheck) {
                    for (int i = 0, j = informalEssayList.size(); i < j; i++) {
                        listCheck.set(i, true);
                    }
                } else {
                    for (int i = 0, j = informalEssayList.size(); i < j; i++) {
                        listCheck.set(i, false);
                    }
                }
                informalEssayAdapter.notifyDataSetChanged();
            }
        });
    }

    @OnClick({R.id.title_bar_right_icon_four,
            R.id.image_view_back,
            R.id.title_bar_right_icon_three,
            R.id.title_bar_right_icon_two})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_view_back:
                finish();
                break;
            case R.id.title_bar_right_icon_four:
                deleteCheckedData();
                break;
            case R.id.title_bar_right_icon_three:
                exportData();
                break;
            case R.id.title_bar_right_icon_two:
                ActivityManager.startAddInformalEssayActivity(this);
                break;
        }
    }

    private void exportData() {
        if (informalEssayList.size() > 0) {
            ArrayList<String> htmlTitleData = informalEssayPresenter.getHtmlTitleData();
            informalEssayPresenter.exportDataToHtml(this, listCheck, htmlTitleData, informalEssayList);
        } else {
            CommonNotices.showMessage(this, getString(R.string.no_relevant_data));
        }
    }

    private void deleteCheckedData() {
        if (informalEssayList.size() > 0) {
            informalEssayPresenter.remoteAdapterData(listCheck, informalEssayAdapter, informalEssayList);
        } else {
            CommonNotices.showMessage(this, getString(R.string.no_relevant_data));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onExportHtmlSuccessEvent(ExportHtmlSuccessEvent event) {
        CommonNotices.showMessage(this, getString(R.string.export_success));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onExportHtmlFailedEvent(ExportHtmlFailedEvent event) {
        CommonNotices.showMessage(this, getString(R.string.export_failed));
    }

    @Override
    public void setInformalEssayByTime(List<InformalEssayEntity> dataList) {
    }

    @Override
    public void setInformalEssayByTitle(List<InformalEssayEntity> dataList) {
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
