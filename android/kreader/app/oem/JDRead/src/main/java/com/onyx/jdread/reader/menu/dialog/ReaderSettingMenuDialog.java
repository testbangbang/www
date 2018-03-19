package com.onyx.jdread.reader.menu.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.RatingBar;
import android.widget.SeekBar;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.ui.dialog.OnyxBaseDialog;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.utils.DeviceUtils;
import com.onyx.android.sdk.utils.PreferenceManager;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.ReaderSettingMenuBinding;
import com.onyx.jdread.main.adapter.FunctionBarAdapter;
import com.onyx.jdread.main.model.FunctionBarModel;
import com.onyx.jdread.main.model.MainBundle;
import com.onyx.jdread.reader.actions.InitReaderViewFunctionBarAction;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.menu.actions.UpdatePageInfoAction;
import com.onyx.jdread.reader.menu.event.GotoPageEvent;
import com.onyx.jdread.reader.menu.event.ReaderSettingMenuDialogHandler;
import com.onyx.jdread.reader.menu.model.ReaderImageModel;
import com.onyx.jdread.reader.menu.model.ReaderMarginModel;
import com.onyx.jdread.reader.menu.model.ReaderPageInfoModel;
import com.onyx.jdread.reader.menu.model.ReaderSettingModel;
import com.onyx.jdread.reader.menu.model.ReaderTextModel;
import com.onyx.jdread.reader.menu.model.ReaderTitleBarModel;
import com.onyx.jdread.reader.utils.ReaderViewUtil;
import com.onyx.jdread.setting.model.BrightnessModel;


/**
 * Created by huxiaomao on 17/5/10.
 */

public class ReaderSettingMenuDialog extends OnyxBaseDialog implements ReaderSettingViewBack{
    private static final String TAG = ReaderSettingMenuDialog.class.getSimpleName();
    private ReaderSettingMenuBinding binding;
    private ReaderDataHolder readerDataHolder;
    private FunctionBarModel functionBarModel;
    private BrightnessModel brightnessModel;
    private FunctionBarAdapter functionBarAdapter;
    private ReaderSettingMenuDialogHandler readerSettingMenuDialogHandler;
    private boolean inSystemBar = false;

    public ReaderSettingMenuDialog(ReaderDataHolder readerDataHolder, @NonNull Activity activity) {
        super(activity, android.R.style.Theme_Translucent_NoTitleBar);
        this.readerDataHolder = readerDataHolder;

        readerSettingMenuDialogHandler = new ReaderSettingMenuDialogHandler(readerDataHolder,this);
    }

    public ReaderSettingMenuDialogHandler getReaderSettingMenuDialogHandler() {
        return readerSettingMenuDialogHandler;
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
    }

    @Override
    public void show() {
        super.show();
        DeviceUtils.adjustFullScreenStatus(this.getWindow(),true);
        ReaderViewUtil.applyFastModeByConfig();
    }

    private void initView() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.reader_setting_menu, null, false);
        setContentView(binding.getRoot());
        readerSettingMenuDialogHandler.setBinding(binding);
    }

    private void initReaderSettingMenu(){
        binding.setReaderSettingModel(new ReaderSettingModel(readerDataHolder.getEventBus()));
    }

    private void initReaderPageInfoBar(){
        binding.readerSettingPageInfoBar.setReaderPageInfoModel(new ReaderPageInfoModel(readerDataHolder));
        new UpdatePageInfoAction(binding,readerDataHolder.getReaderViewInfo(),true).execute(readerDataHolder,null);
        initReaderPageInfoEvent();
    }

    private void initReaderPageInfoEvent(){
        binding.readerSettingPageInfoBar.readerPageInfoMenuReadProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Drawable drawable = readerDataHolder.getAppContext().getResources().getDrawable(R.drawable.seekbar_thumb_transparent);
                seekBar.setThumb(drawable);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Drawable drawable = readerDataHolder.getAppContext().getResources().getDrawable(R.drawable.seekbar_thumb);
                seekBar.setThumb(drawable);
                gotoPage(seekBar.getProgress());
            }
        });
    }

    private void gotoPage(int progress){
        if(readerDataHolder.getReaderViewInfo().isLoadComplete()) {
            updateProgress(progress);
            GotoPageEvent event = new GotoPageEvent(Math.max(progress - 1, 0));
            readerDataHolder.getEventBus().post(event);
        }
    }

    private void updateProgress(int readProgress){
        binding.readerSettingPageInfoBar.getReaderPageInfoModel().setCurrentPage(readProgress);
    }

    private void initSystemBar() {
        MainBundle.getInstance().getSystemBarModel().setIsShow(true);
        binding.readerSettingSystemBar.setSystemBarModel(MainBundle.getInstance().getSystemBarModel());
    }

    private void initTextBar(){
        binding.readerSettingTextSettingBar.setReaderTextModel(new ReaderTextModel(readerDataHolder.getEventBus(),
                readerDataHolder.getStyleCopy(),readerDataHolder.getSettingInfo()));
    }

    private void initImageBar(){
        binding.readerSettingImageSettingBar.setReaderImageModel(new ReaderImageModel(readerDataHolder.getEventBus(),readerDataHolder.getGammaInfo()));
    }

    private void initCustomizeBar(){
        binding.readerSettingCustomizeFormatBar.setReaderMarginModel(new ReaderMarginModel(readerDataHolder.getEventBus(),readerDataHolder.getSettingInfo()));
        initCustomizeEvent();
    }

    private void initCustomizeEvent() {
    }

    private void initBrightnessBar(){
        brightnessModel = new BrightnessModel();
        binding.readerSettingBrightnessBar.setBrightnessModel(brightnessModel);
        initEvent();
    }

    private void initEvent() {
        binding.readerSettingBrightnessBar.ratingbarLightSettings.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if(fromUser) {
                    brightnessModel.setBrightness(ratingBar.getProgress());
                }
            }
        });
    }

    private void initReaderTitleBar() {
        binding.readerSettingTitleBar.setReaderTitleBarModel(new ReaderTitleBarModel(readerDataHolder.getEventBus(),
                readerDataHolder.supportFontSizeAdjustment(),readerDataHolder.getDocumentInfo().isWholeBookDownLoad()));
        updateBookmarkState();
    }

    public void updateBookmarkState(){
        boolean isBookmark = readerSettingMenuDialogHandler.hasBookmark();
        binding.readerSettingTitleBar.getReaderTitleBarModel().setBookMarkImageId(isBookmark);
        readerSettingMenuDialogHandler.updatePageInfo();
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

    @Override
    public void setFunction() {
        setFunctionAdapter(getFunctionBarRecycler());
    }

    private void updateFunctionBar() {
        InitReaderViewFunctionBarAction initReaderViewFunctionBarAction = new InitReaderViewFunctionBarAction(functionBarModel, new RxCallback() {
            @Override
            public void onNext(Object o) {
                updateFunctionBarView();
            }
        });
        initReaderViewFunctionBarAction.execute(readerDataHolder,null);
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
        ReaderViewUtil.clearFastModeByConfig();
    }

    @Override
    public Dialog getContent() {
        return this;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            inSystemBar = event.getY() < binding.readerSettingSystemBar.getRoot().getHeight();
        }

        if (event.getAction() == MotionEvent.ACTION_MOVE && inSystemBar) {
            event.setAction(MotionEvent.ACTION_UP);
        }
        return super.dispatchTouchEvent(event);
    }

    public BrightnessModel getBrightnessModel() {
        return brightnessModel;
    }
}
