package com.onyx.jdread.reader.menu.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.utils.PreferenceManager;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.ReaderSettingMenuBinding;
import com.onyx.jdread.main.adapter.FunctionBarAdapter;
import com.onyx.jdread.main.model.FunctionBarModel;
import com.onyx.jdread.main.model.SystemBarModel;
import com.onyx.jdread.reader.actions.InitReaderViewFunctionBarAction;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.menu.model.ReaderTitleBarModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;


/**
 * Created by huxiaomao on 17/5/10.
 */

public class ReaderSettingMenuDialog extends Dialog implements View.OnClickListener {
    private static final String TAG = ReaderSettingMenuDialog.class.getSimpleName();
    private ReaderSettingMenuBinding binding;
    private ReaderDataHolder readerDataHolder;
    private FunctionBarModel functionBarModel;
    private FunctionBarAdapter functionBarAdapter;

    public ReaderSettingMenuDialog(ReaderDataHolder readerDataHolder, @NonNull Activity activity) {
        super(activity, android.R.style.Theme_Translucent_NoTitleBar);
        this.readerDataHolder = readerDataHolder;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCanceledOnTouchOutside(false);
        initView();
        initThirdLibrary();
        initData();
    }

    private void initView() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),R.layout.reader_setting_menu,null,false);
        setContentView(binding.getRoot());
    }

    private void initSystemBar() {
        binding.readerSettingSystemBar.setSystemBarModel(new SystemBarModel());
    }

    private void initReaderTitleBar(){
        binding.readerSettingTitleBar.setReaderTitleBarModel(new ReaderTitleBarModel());
    }

    private void initFunctionBar() {
        functionBarModel = new FunctionBarModel();
        binding.readerSettingFunctionBar.setFunctionBarModel(functionBarModel);
        PageRecyclerView functionBarRecycler = getFunctionBarRecycler();
        functionBarRecycler.setLayoutManager(new DisableScrollGridManager(getContext()));
        functionBarAdapter = new FunctionBarAdapter();
        setFunctionAdapter(functionBarRecycler);
    }

    private void setFunctionAdapter( PageRecyclerView functionBarRecycler) {
        boolean show = PreferenceManager.getBooleanValue(JDReadApplication.getInstance(), R.string.show_back_tab_key, false);
        PreferenceManager.setBooleanValue(JDReadApplication.getInstance(), R.string.show_back_tab_key, show);
        int col = getContext().getResources().getInteger(R.integer.function_bar_col);
        functionBarAdapter.setRowAndCol(functionBarAdapter.getRowCount(), show ? col : col - 1);
        functionBarRecycler.setAdapter(functionBarAdapter);
        updateFunctionBar();
    }

    private void updateFunctionBar() {
        InitReaderViewFunctionBarAction initReaderViewFunctionBarAction = new InitReaderViewFunctionBarAction(functionBarModel,new RxCallback() {
            @Override
            public void onNext(Object o) {
                updateFunctionBarView();
            }
        });
        initReaderViewFunctionBarAction.execute(readerDataHolder);
    }

    private void updateFunctionBarView() {
        PageRecyclerView barRecycler = getFunctionBarRecycler();
        if (barRecycler == null) {
            return;
        }
        barRecycler.getAdapter().notifyDataSetChanged();
    }

    private PageRecyclerView getFunctionBarRecycler() {
        return binding.readerSettingFunctionBar.functionBarRecycler;
    }

    private void initData() {
        initFunctionBar();
        initSystemBar();
        initReaderTitleBar();
    }

    private void initThirdLibrary() {
        EventBus.getDefault().register(this);
    }

    @Override
    public void onClick(View v) {

    }

    @Subscribe
    public void onReaderSettingEvent(Object object) {

    }
}
