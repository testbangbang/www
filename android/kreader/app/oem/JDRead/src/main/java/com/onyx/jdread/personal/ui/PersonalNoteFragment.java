package com.onyx.jdread.personal.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.data.GPaginator;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.PersonalNoteBinding;
import com.onyx.jdread.library.view.DashLineItemDivider;
import com.onyx.jdread.main.common.BaseFragment;
import com.onyx.jdread.main.model.TitleBarModel;
import com.onyx.jdread.personal.action.GetPersonalNotesAction;
import com.onyx.jdread.personal.adapter.PersonalNoteAdapter;
import com.onyx.jdread.personal.cloud.entity.jdbean.NoteBean;
import com.onyx.jdread.personal.dialog.ExportDialog;
import com.onyx.jdread.personal.event.ExportToEmailEvent;
import com.onyx.jdread.personal.event.ExportToImpressionEvent;
import com.onyx.jdread.personal.event.ExportToNativeEvent;
import com.onyx.jdread.personal.model.PersonalDataBundle;
import com.onyx.jdread.setting.event.BackToSettingFragmentEvent;
import com.onyx.jdread.util.Utils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * Created by li on 2018/1/3.
 */

public class PersonalNoteFragment extends BaseFragment {
    private PersonalNoteBinding binding;
    private PersonalNoteAdapter personalNoteAdapter;
    private GPaginator paginator;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = (PersonalNoteBinding) DataBindingUtil.inflate(inflater, R.layout.fragment_personal_note, container, false);
        initView();
        initData();
        initListener();
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        Utils.ensureRegister(PersonalDataBundle.getInstance().getEventBus(), this);
    }

    @Override
    public void onStop() {
        super.onStop();
        Utils.ensureUnregister(PersonalDataBundle.getInstance().getEventBus(), this);
    }

    private void initView() {
        binding.personalNoteRecycler.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
        DashLineItemDivider decoration = new DashLineItemDivider();
        binding.personalNoteRecycler.addItemDecoration(decoration);
        personalNoteAdapter = new PersonalNoteAdapter();
        binding.personalNoteRecycler.setAdapter(personalNoteAdapter);
        paginator = binding.personalNoteRecycler.getPaginator();
    }

    private void initData() {
        TitleBarModel titleModel = PersonalDataBundle.getInstance().getTitleModel();
        titleModel.title.set(JDReadApplication.getInstance().getResources().getString(R.string.personal_notes));
        titleModel.backEvent.set(new BackToSettingFragmentEvent());
        binding.personalNoteTitle.setTitleModel(titleModel);

        final GetPersonalNotesAction action = new GetPersonalNotesAction();
        action.execute(PersonalDataBundle.getInstance(), new RxCallback() {
            @Override
            public void onNext(Object o) {
                List<NoteBean> notes = action.getNotes();
                if (notes != null) {
                    personalNoteAdapter.setData(notes);
                    binding.setTotal(String.valueOf(notes.size()));
                    paginator.resize(personalNoteAdapter.getRowCount(), personalNoteAdapter.getColumnCount(), notes.size());
                    binding.setPageText(paginator.getProgressText());
                }
            }
        });
    }

    private void initListener() {
        binding.personalNoteCheckAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<NoteBean> data = personalNoteAdapter.getData();
                if (data != null && data.size() > 0) {
                    for (NoteBean bean :data) {
                        bean.checked = true;
                    }
                    personalNoteAdapter.notifyDataSetChanged();
                }
            }
        });

        binding.personalNoteExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showExportDialog();
            }
        });

        binding.personalNoteRecycler.setOnPagingListener(new PageRecyclerView.OnPagingListener() {
            @Override
            public void onPageChange(int position, int itemCount, int pageSize) {
                binding.setPageText(paginator.getProgressText());
            }
        });
    }

    private void showExportDialog() {
        ExportDialog dialog = new ExportDialog();
        dialog.show(getActivity().getFragmentManager(), "");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBackToSettingFragmentEvent(BackToSettingFragmentEvent event) {
        viewEventCallBack.viewBack();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onExportToNativeEvent(ExportToNativeEvent event) {
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onExportToEmailEvent(ExportToEmailEvent event) {
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onExportToImpressionEvent(ExportToImpressionEvent event) {
    }
}
