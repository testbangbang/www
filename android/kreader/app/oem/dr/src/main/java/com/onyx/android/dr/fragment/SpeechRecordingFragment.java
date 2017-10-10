package com.onyx.android.dr.fragment;

import android.support.v7.widget.DividerItemDecoration;
import android.view.View;
import android.widget.TextView;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.adapter.SpeechRecordingAdapter;
import com.onyx.android.dr.data.database.InformalEssayEntity;
import com.onyx.android.dr.event.SearchKeywordEvent;
import com.onyx.android.dr.interfaces.SpeechRecordingView;
import com.onyx.android.dr.presenter.SpeechRecordingPresenter;
import com.onyx.android.dr.util.DRPreferenceManager;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Created by zhouzhiming on 2017/9/6.
 */
public class SpeechRecordingFragment extends BaseFragment implements SpeechRecordingView {
    @Bind(R.id.fragment_speech_recording_recycler_view)
    PageRecyclerView recyclerView;
    @Bind(R.id.fragment_speech_recording_all_number)
    TextView allNumber;
    private DividerItemDecoration dividerItemDecoration;
    private SpeechRecordingAdapter speechRecordingAdapter;
    private SpeechRecordingPresenter presenter;
    private List<InformalEssayEntity> informalEssayList;
    private int jumpSource = 0;

    @Override
    protected void initListener() {
    }

    @Override
    protected int getRootView() {
        return R.layout.fragment_speech_recording;
    }

    @Override
    protected void initView(View rootView) {
        initRecyclerView();
    }

    private void initRecyclerView() {
        dividerItemDecoration =
                new DividerItemDecoration(DRApplication.getInstance(), DividerItemDecoration.VERTICAL);
        speechRecordingAdapter = new SpeechRecordingAdapter();
        recyclerView.setLayoutManager(new DisableScrollGridManager(DRApplication.getInstance()));
        recyclerView.addItemDecoration(dividerItemDecoration);
    }

    @Override
    protected void loadData() {
        EventBus.getDefault().register(this);
        loadInformalEssay();
        initEvent();
    }

    private void loadInformalEssay() {
        presenter = new SpeechRecordingPresenter(getActivity(), this);
        presenter.getAllInformalEssayData();
        informalEssayList = new ArrayList<>();
    }

    @Override
    public void setInformalEssayData(List<InformalEssayEntity> dataList, ArrayList<Boolean> checkList) {
        showData(dataList);
    }

    @Override
    public void setInformalEssayByTitle(List<InformalEssayEntity> dataList) {
        showData(dataList);
    }

    private void showData(List<InformalEssayEntity> dataList) {
        if (dataList != null && !dataList.isEmpty()) {
            allNumber.setText(getString(R.string.fragment_speech_recording_all_number) + dataList.size() + getString(R.string.data_unit));
            informalEssayList.clear();
            informalEssayList = dataList;
        }
        speechRecordingAdapter.setDataList(informalEssayList);
        recyclerView.setAdapter(speechRecordingAdapter);
        speechRecordingAdapter.notifyDataSetChanged();
    }

    public void initEvent() {
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSearchKeywordEvent(SearchKeywordEvent event) {
        String searchKeyword = DRPreferenceManager.getSearchKeyword(getActivity(), "");
        if (!StringUtils.isNullOrEmpty(searchKeyword)) {
            presenter.getInformalEssayQueryByTitle(searchKeyword);
        }
    }

    @Override
    public boolean onKeyBack() {
        return false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }
}
