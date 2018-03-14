package com.onyx.jdread.shop.view;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.DialogBookInfoBinding;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.reader.ui.view.PageTextView;
import com.onyx.jdread.shop.model.DialogBookInfoViewModel;
import com.onyx.jdread.util.Utils;

/**
 * Created by jackdeng on 2018/2/5.
 */

public class BookInfoDialog extends Dialog {

    private DialogBookInfoViewModel infoViewModel = new DialogBookInfoViewModel();
    private View.OnClickListener closeListener;

    public BookInfoDialog(@NonNull Context context) {
        this(context, 0);
    }

    public BookInfoDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCanceledOnTouchOutside(false);

        DialogBookInfoBinding infoBinding = DialogBookInfoBinding.inflate(LayoutInflater.from(getContext()), null, false);
        infoBinding.setViewModel(infoViewModel);
        infoBinding.bookInfoWebView.setOnPagingListener(new PageTextView.OnPagingListener() {
            @Override
            public void onPageChange(int currentPage, int totalPage) {
                infoViewModel.currentPage.set(currentPage);
                infoViewModel.totalPage.set(totalPage);
            }
        });
        infoBinding.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (closeListener != null) {
                    closeListener.onClick(v);
                }
            }
        });
        setView(infoBinding.getRoot());
    }

    public void setView(View view) {
        this.setContentView(view);
    }

    public void setTitle(String title){
        infoViewModel.title.set(title);
    }

    public void setContent(String content) {
        infoViewModel.content.set(content);
    }

    public void setCloseListener(View.OnClickListener listener) {
        this.closeListener = listener;
    }

    @Override
    public void show() {
        Window window = getWindow();
        window.setGravity(Gravity.CENTER_VERTICAL);
        WindowManager.LayoutParams attributes = window.getAttributes();
        int screenWidth = Utils.getScreenWidth(JDReadApplication.getInstance());
        attributes.width = (int) (screenWidth * Utils.getValuesFloat(R.integer.login_dialog_width_rate));
        attributes.y = ResManager.getInteger(R.integer.associated_email_dialog_offset_y);
        attributes.x = ResManager.getInteger(R.integer.associated_email_dialog_offset_x);
        window.setAttributes(attributes);
        window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        super.show();
    }
}
