package com.onyx.jdread.reader.menu.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;

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
import com.onyx.jdread.reader.menu.actions.UpdatePageInfoAction;
import com.onyx.jdread.reader.menu.event.ReaderSettingMenuDialogHandler;
import com.onyx.jdread.reader.menu.model.ReaderBrightnessModel;
import com.onyx.jdread.reader.menu.model.ReaderCustomizeModel;
import com.onyx.jdread.reader.menu.model.ReaderImageModel;
import com.onyx.jdread.reader.menu.model.ReaderPageInfoModel;
import com.onyx.jdread.reader.menu.model.ReaderSettingModel;
import com.onyx.jdread.reader.menu.model.ReaderTextModel;
import com.onyx.jdread.reader.menu.model.ReaderTitleBarModel;


/**
 * Created by huxiaomao on 17/5/10.
 */

public class ReaderSettingMenuDialog extends Dialog implements ReaderSettingViewBack{
    private static final String TAG = ReaderSettingMenuDialog.class.getSimpleName();
    private ReaderSettingMenuBinding binding;
    private ReaderDataHolder readerDataHolder;
    private FunctionBarModel functionBarModel;
    private FunctionBarAdapter functionBarAdapter;
    private ReaderSettingMenuDialogHandler readerSettingMenuDialogHandler;

    public ReaderSettingMenuDialog(ReaderDataHolder readerDataHolder, @NonNull Activity activity) {
        super(activity, android.R.style.Theme_Translucent_NoTitleBar);
        this.readerDataHolder = readerDataHolder;
        readerSettingMenuDialogHandler = new ReaderSettingMenuDialogHandler(readerDataHolder,this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCanceledOnTouchOutside(false);
        initView();
        registerListener();
        initData();
    }

    private void initData() {
        initReaderSettingMenu();
        initFunctionBar();
        initSystemBar();
        initReaderPageInfoBar();
        initReaderTitleBar();
        initBrightnessBar();
        initTextBar();
        initImageBar();
        initCustomizeBar();

        readerSettingMenuDialogHandler.setBinding(binding);
    }

    private void initView() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.reader_setting_menu, null, false);
        setContentView(binding.getRoot());
    }

    private void initReaderSettingMenu(){
        binding.setReaderSettingModel(new ReaderSettingModel());
    }

    private void initReaderPageInfoBar(){
        binding.readerSettingPageInfoBar.setReaderPageInfoModel(new ReaderPageInfoModel());
        new UpdatePageInfoAction(binding).execute(readerDataHolder);
    }

    private void initSystemBar() {
        binding.readerSettingSystemBar.setSystemBarModel(new SystemBarModel());
    }

    private void initTextBar(){
        binding.readerSettingTextSettingBar.setReaderTextModel(new ReaderTextModel());
    }

    private void initImageBar(){
        binding.readerSettingImageSettingBar.setReaderImageModel(new ReaderImageModel());
    }

    private void initCustomizeBar(){
        binding.readerSettingCustomizeFormatBar.setReaderCustomizeModel(new ReaderCustomizeModel());
    }

    private void initBrightnessBar(){
        binding.readerSettingBrightnessBar.setReaderBrightnessModel(new ReaderBrightnessModel());
    }

    private void initReaderTitleBar() {
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

    private void setFunctionAdapter(PageRecyclerView functionBarRecycler) {
        boolean show = PreferenceManager.getBooleanValue(JDReadApplication.getInstance(), R.string.show_back_tab_key, false);
        PreferenceManager.setBooleanValue(JDReadApplication.getInstance(), R.string.show_back_tab_key, show);
        int col = getContext().getResources().getInteger(R.integer.function_bar_col);
        functionBarAdapter.setRowAndCol(functionBarAdapter.getRowCount(), show ? col : col - 1);
        functionBarRecycler.setAdapter(functionBarAdapter);
        updateFunctionBar();
    }

    private void updateFunctionBar() {
        InitReaderViewFunctionBarAction initReaderViewFunctionBarAction = new InitReaderViewFunctionBarAction(functionBarModel, new RxCallback() {
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

    private void registerListener() {
        readerSettingMenuDialogHandler.registerListener();
    }

    @Override
    public void dismiss() {
        readerSettingMenuDialogHandler.unregisterListener();
        super.dismiss();
    }

    @Override
    public Dialog getContent() {
        return this;
    }
}
