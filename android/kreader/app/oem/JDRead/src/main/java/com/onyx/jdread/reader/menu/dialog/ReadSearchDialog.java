package com.onyx.jdread.reader.menu.dialog;

import android.app.DialogFragment;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.OnyxPageDividerItemDecoration;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.DialogReadSearchBinding;
import com.onyx.jdread.reader.adapter.SearchAdapter;
import com.onyx.jdread.reader.menu.event.CloseSearchDialogEvent;
import com.onyx.jdread.reader.menu.event.SearchEvent;
import com.onyx.jdread.reader.menu.model.ReaderSearchModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by li on 2018/1/13.
 */

public class ReadSearchDialog extends DialogFragment {
    private DialogReadSearchBinding binding;
    private SearchAdapter searchAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = (DialogReadSearchBinding) DataBindingUtil.inflate(inflater, R.layout.dialog_read_search_layout, container, false);
        initData();
        initView();
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        window.setAttributes(params);
        EventBus.getDefault().register(this);
    }

    private void initData() {
        ReaderSearchModel model = new ReaderSearchModel();
        binding.setModel(model);
    }

    private void initView() {
        binding.dialogReadSearchRecycler.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
        OnyxPageDividerItemDecoration decoration = new OnyxPageDividerItemDecoration(JDReadApplication.getInstance(), OnyxPageDividerItemDecoration.VERTICAL);
        binding.dialogReadSearchRecycler.addItemDecoration(decoration);
        searchAdapter = new SearchAdapter();
        binding.dialogReadSearchRecycler.setAdapter(searchAdapter);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSearchEvent(SearchEvent event) {
        String content = event.getContent();
        // TODO: 2018/1/13 search action
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCloseSearchDialogEvent(CloseSearchDialogEvent event) {
        if (isVisible()) {
            dismiss();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
