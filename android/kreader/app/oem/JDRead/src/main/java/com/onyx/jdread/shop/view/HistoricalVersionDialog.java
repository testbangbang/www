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
import com.onyx.jdread.reader.ui.view.PageTextView;
import com.onyx.jdread.shop.model.DialogBookInfoViewModel;
import com.onyx.jdread.util.Utils;

/**
 * Created by lmb on 2018/3/22.
 */

public class HistoricalVersionDialog extends Dialog {

    private DialogBookInfoViewModel infoViewModel = new DialogBookInfoViewModel();
    private View.OnClickListener closeListener;

    public HistoricalVersionDialog(@NonNull Context context) {
        this(context, 0);
    }

    public HistoricalVersionDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCanceledOnTouchOutside(false);

        DialogBookInfoBinding infoBinding = DialogBookInfoBinding.inflate(LayoutInflater.from(getContext()), null, false);
        infoBinding.pagesNumber.setVisibility(View.GONE);
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

    public void setTitle(String title) {
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
        int screenHeight = Utils.getScreenHeight(JDReadApplication.getInstance());
        attributes.width = (int) (screenWidth * Utils.getValuesFloat(R.integer.historical_version_dialog_width));
        attributes.height = (int) (screenHeight * Utils.getValuesFloat(R.integer.historical_version_dialog_height));
        window.setAttributes(attributes);
        window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        super.show();
    }
}
