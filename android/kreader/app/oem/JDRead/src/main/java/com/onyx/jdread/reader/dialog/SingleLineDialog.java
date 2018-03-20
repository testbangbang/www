package com.onyx.jdread.reader.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;

import com.onyx.jdread.R;
import com.onyx.jdread.databinding.ActivitySingleLineBinding;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.reader.event.TranslateDialogEventHandler;
import com.onyx.jdread.reader.event.UpdateViewPageEvent;
import com.onyx.jdread.reader.model.SingleNoteViewModel;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by huxiaomao on 2018/1/22.
 */

public class SingleLineDialog extends ReaderBaseDialog implements SinglelineViewCallBack {
    private static final String TAG = ReaderNoteDialog.class.getSimpleName();
    private ActivitySingleLineBinding binding;
    private SingleNoteViewModel signNoteViewModel;
    private String text;
    private EventBus eventBus;
    private TranslateDialogEventHandler handler;
    private RectF rect;
    private int windowHeight;
    private int windowWidth;
    private boolean isShowPage = false;

    public SingleLineDialog(@NonNull Activity activity, String text, EventBus eventBus,
                            RectF rect, int height, int width) {
        super(activity, android.R.style.Theme_NoTitleBar);
        this.text = text;
        this.eventBus = eventBus;
        this.rect = rect;
        this.windowHeight = height;
        this.windowWidth = width;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    private void initView() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.activity_single_line, null, false);
        setContentView(binding.getRoot());

        signNoteViewModel = new SingleNoteViewModel(eventBus);
        signNoteViewModel.setIsShowPage(isShowPage);
        binding.setSingleNoteViewModel(signNoteViewModel);
        signNoteViewModel.setBinding(binding);
        signNoteViewModel.setSinglelineViewCallBack(this);
    }

    @Override
    public void show() {
        setDialogParams(this);
        super.show();
    }

    private void initData() {
        signNoteViewModel.setText(text);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        eventBus.post(new UpdateViewPageEvent());
    }

    @Override
    public Dialog getContent() {
        return this;
    }

    public void setDialogParams(Dialog dialog) {
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams params = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.LEFT | Gravity.TOP);
        params.width = (int) getContext().getResources().getDimension(R.dimen.reader_select_menu_width);
        params.height = getWindowHeight(params.width);

        params.x = (windowWidth - params.width) / 2;

        float y = rect.bottom;
        if(y + params.height > windowHeight){
            y = rect.top - params.height;
        }
        params.y = (int)y;
        dialogWindow.setBackgroundDrawableResource(R.drawable.rectangle_stroke_corners);
        dialogWindow.setAttributes(params);


    }

    private int getWindowHeight(int dialogWidth){
        Paint paint = new Paint();
        Rect rect = new Rect();
        paint.setTextSize(ResManager.getDimens(R.dimen.level_two_heading_font));

        paint.getTextBounds(text, 0, text.length(), rect);

        int marginWidth = ResManager.getDimens(R.dimen.single_dialog_margin_left);
        int width = dialogWidth - marginWidth * 2;
        if(rect.width() >= width){
            isShowPage = true;
            return ResManager.getDimens(R.dimen.reader_multi_line_dialog_height);
        }
        return ResManager.getDimens(R.dimen.reader_single_line_dialog_height);
    }

    @Override
    public void updateWindowHeight() {

    }
}