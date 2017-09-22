package com.onyx.android.dr.activity;

import android.support.v7.widget.DividerItemDecoration;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.adapter.MemorandumAdapter;
import com.onyx.android.dr.common.ActivityManager;
import com.onyx.android.dr.common.CommonNotices;
import com.onyx.android.dr.data.database.MemorandumEntity;
import com.onyx.android.dr.interfaces.MemorandumView;
import com.onyx.android.dr.presenter.MemorandumPresenter;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;


/**
 * Created by zhouzhiming on 17-7-11.
 */
public class MemorandumActivity extends BaseActivity implements MemorandumView {
    @Bind(R.id.memorandum_activity_recyclerview)
    PageRecyclerView recyclerView;
    @Bind(R.id.image_view_back)
    ImageView imageViewBack;
    @Bind(R.id.title_bar_title)
    TextView title;
    @Bind(R.id.image)
    ImageView image;
    @Bind(R.id.title_bar_right_icon_four)
    ImageView iconFour;
    @Bind(R.id.title_bar_right_icon_three)
    ImageView iconThree;
    @Bind(R.id.title_bar_right_icon_two)
    ImageView iconTwo;
    @Bind(R.id.memorandum_activity_all_check)
    CheckBox allCheck;
    @Bind(R.id.memorandum_activity_all_number)
    TextView allNumber;
    private DividerItemDecoration dividerItemDecoration;
    private MemorandumAdapter memorandumAdapter;
    private MemorandumPresenter memorandumPresenter;
    private ArrayList<Boolean> listCheck;
    private List<MemorandumEntity> memorandumList;

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_memorandum;
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
        memorandumAdapter = new MemorandumAdapter();
        recyclerView.setLayoutManager(new DisableScrollGridManager(DRApplication.getInstance()));
        recyclerView.addItemDecoration(dividerItemDecoration);
    }

    @Override
    protected void initData() {
        memorandumList = new ArrayList<MemorandumEntity>();
        listCheck = new ArrayList<>();
        initTitleData();
        initEvent();
    }

    private void initTitleData() {
        image.setImageResource(R.drawable.memorandum);
        title.setText(getString(R.string.memorandum));
        iconTwo.setVisibility(View.VISIBLE);
        iconFour.setVisibility(View.VISIBLE);
        iconThree.setVisibility(View.VISIBLE);
        iconFour.setImageResource(R.drawable.ic_reader_note_delet);
        iconThree.setImageResource(R.drawable.ic_reader_note_export);
        iconTwo.setImageResource(R.drawable.ic_reader_note_diary_set);
    }

    @Override
    protected void onResume() {
        super.onResume();
        memorandumPresenter = new MemorandumPresenter(getApplicationContext(), this);
        memorandumPresenter.getAllMemorandumData();
    }

    @Override
    public void setMemorandumData(List<MemorandumEntity> dataList, ArrayList<Boolean> checkList) {
        if (dataList == null || dataList.size() <= 0) {
            return;
        }
        memorandumList = dataList;
        listCheck = checkList;
        allNumber.setText(getString(R.string.informal_essay_activity_all_number) + memorandumList.size() + getString(R.string.data_unit));
        memorandumAdapter.setDataList(memorandumList, listCheck);
        recyclerView.setAdapter(memorandumAdapter);
    }

    public void initEvent() {
        memorandumAdapter.setOnItemListener(new MemorandumAdapter.OnItemClickListener() {
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
                    for (int i = 0, j = memorandumList.size(); i < j; i++) {
                        listCheck.set(i, true);
                    }
                } else {
                    for (int i = 0, j = memorandumList.size(); i < j; i++) {
                        listCheck.set(i, false);
                    }
                }
                memorandumAdapter.notifyDataSetChanged();
            }
        });
    }

    @OnClick({R.id.image_view_back,
            R.id.title_bar_right_icon_four,
            R.id.title_bar_right_icon_two,
            R.id.title_bar_right_icon_three})
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
                ActivityManager.startAddMemorandumActivity(this);
                break;
        }
    }

    private void exportData() {
        if (memorandumList.size() > 0) {
            ArrayList<String> htmlTitleData = memorandumPresenter.getHtmlTitle();
            memorandumPresenter.exportDataToHtml(this, listCheck, htmlTitleData, memorandumList);
        } else {
            CommonNotices.showMessage(this, getString(R.string.no_relevant_data));
        }
    }

    private void deleteCheckedData() {
        if (memorandumList.size() > 0) {
            memorandumPresenter.remoteAdapterData(listCheck, memorandumAdapter, memorandumList);
        } else {
            CommonNotices.showMessage(this, getString(R.string.no_relevant_data));
        }
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
