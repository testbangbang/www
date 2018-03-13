package com.onyx.jdread.personal.ui;

import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.evernote.client.android.EvernoteSession;
import com.onyx.android.sdk.data.GPaginator;
import com.onyx.android.sdk.device.EnvironmentUtil;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.android.sdk.utils.ZipUtils;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.PersonalNoteBinding;
import com.onyx.jdread.library.view.DashLineItemDivider;
import com.onyx.jdread.main.common.BaseFragment;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.main.common.JDPreferenceManager;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.main.common.ToastUtil;
import com.onyx.jdread.main.model.TitleBarModel;
import com.onyx.jdread.manager.EvernoteManager;
import com.onyx.jdread.personal.action.ExportAction;
import com.onyx.jdread.personal.action.ExportNoteAction;
import com.onyx.jdread.personal.action.GetPersonalNotesAction;
import com.onyx.jdread.personal.adapter.PersonalNoteAdapter;
import com.onyx.jdread.personal.cloud.entity.jdbean.BookBean;
import com.onyx.jdread.personal.cloud.entity.jdbean.ExportNoteBean;
import com.onyx.jdread.personal.cloud.entity.jdbean.ExportNoteResultBean;
import com.onyx.jdread.personal.cloud.entity.jdbean.NoteBean;
import com.onyx.jdread.personal.dialog.ExportDialog;
import com.onyx.jdread.personal.event.ExportToEmailEvent;
import com.onyx.jdread.personal.event.ExportToImpressionEvent;
import com.onyx.jdread.personal.event.ExportToNativeEvent;
import com.onyx.jdread.personal.model.PersonalDataBundle;
import com.onyx.jdread.setting.common.AssociateDialogHelper;
import com.onyx.jdread.setting.common.ExportHelper;
import com.onyx.jdread.setting.event.AssociatedEmailToolsEvent;
import com.onyx.jdread.setting.event.BackToSettingFragmentEvent;
import com.onyx.jdread.setting.event.BindEmailEvent;
import com.onyx.jdread.setting.event.UnBindEmailEvent;
import com.onyx.jdread.setting.model.SettingBundle;
import com.onyx.jdread.setting.view.AssociatedEmailDialog;
import com.onyx.jdread.util.Utils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by li on 2018/1/3.
 */

public class PersonalNoteFragment extends BaseFragment {
    private PersonalNoteBinding binding;
    private PersonalNoteAdapter personalNoteAdapter;
    private GPaginator paginator;
    private ExportHelper exportHelper;

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
        exportHelper = new ExportHelper(getActivity(), PersonalDataBundle.getInstance().getEventBus());

        final GetPersonalNotesAction action = new GetPersonalNotesAction();
        action.execute(PersonalDataBundle.getInstance(), new RxCallback() {
            @Override
            public void onNext(Object o) {
                List<NoteBean> notes = action.getNotes();
                setSelectAllText(notes == null ? 0 : notes.size());
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
        binding.personalNoteCheckAllText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<NoteBean> data = personalNoteAdapter.getData();
                if (data != null && data.size() > 0) {
                    for (NoteBean bean : data) {
                        bean.checked = !bean.checked;
                    }
                    setExportText(data.get(0).checked);
                    binding.personalNoteCheckAll.setChecked(data.get(0).checked);
                    personalNoteAdapter.notifyDataSetChanged();
                }
            }
        });

        binding.personalNoteCheckAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                List<NoteBean> data = personalNoteAdapter.getData();
                if (data != null && data.size() > 0) {
                    for (NoteBean bean : data) {
                        bean.checked = isChecked;
                    }
                    setExportText(isChecked);
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

        personalNoteAdapter.setOnItemClickListener(new PageRecyclerView.PageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                List<NoteBean> data = personalNoteAdapter.getData();
                List<Boolean> list = new ArrayList<>();
                if (data != null && data.size() > 0) {
                    for (NoteBean bean : data) {
                        if (bean.checked) {
                            list.add(bean.checked);
                        }
                    }
                }
                binding.personalNoteCheckAll.setChecked(data.size() == list.size());
                setExportText(list.size() != 0);
            }
        });
    }

    private void setExportText(boolean enable) {
        binding.personalNoteExport.setEnabled(enable);
        binding.personalNoteExport.setTextColor(enable ? ResManager.getColor(R.color.normal_black) : ResManager.getColor(R.color.text_gray_color));
    }

    private void setSelectAllText(int size) {
        binding.personalNoteCheckAll.setEnabled(size != 0);
        binding.personalNoteCheckAll.setTextColor(size != 0 ? ResManager.getColor(R.color.normal_black) : ResManager.getColor(R.color.text_gray_color));
        binding.personalNoteCheckAllText.setEnabled(size != 0);
        binding.personalNoteCheckAllText.setTextColor(size != 0 ? ResManager.getColor(R.color.normal_black) : ResManager.getColor(R.color.text_gray_color));
    }

    private void showExportDialog() {
        ExportDialog dialog = new ExportDialog();
        dialog.setEventBus(PersonalDataBundle.getInstance().getEventBus());
        dialog.show(getActivity().getFragmentManager(), "");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBackToSettingFragmentEvent(BackToSettingFragmentEvent event) {
        viewEventCallBack.viewBack();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onExportToNativeEvent(ExportToNativeEvent event) {
        exportNote(ExportHelper.TYPE_NATIVE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onExportToEmailEvent(ExportToEmailEvent event) {
        exportNote(ExportHelper.TYPE_EMAIL);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onExportToImpressionEvent(ExportToImpressionEvent event) {
        exportNote(ExportHelper.TYPE_EVERNOTE);
    }

    private void exportNote(int exportType) {
        if (personalNoteAdapter != null) {
            List<NoteBean> data = personalNoteAdapter.getData();
            ExportAction action = new ExportAction(exportHelper, exportType, data);
            action.execute(PersonalDataBundle.getInstance(), null);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBindEmailEvent(BindEmailEvent event) {
        AssociateDialogHelper.dismissEmailDialog();
        ToastUtil.showToast(R.string.bind_email_success);
        showExportDialog();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAssociatedEmailToolsEvent(AssociatedEmailToolsEvent event) {
        AssociatedEmailDialog.DialogModel model = new AssociatedEmailDialog.DialogModel(PersonalDataBundle.getInstance().getEventBus());
        AssociateDialogHelper.showBindEmailDialog(model, getActivity());
    }
}
