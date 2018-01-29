package com.onyx.android.eschool.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.onyx.android.eschool.R;
import com.onyx.android.eschool.model.MessageInfo;
import com.onyx.android.eschool.utils.BroadcastHelper;
import com.onyx.android.sdk.data.GPaginator;
import com.onyx.android.sdk.utils.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by suicheng on 2018/1/20.
 */
public class DialogMultiMessage extends AlertDialog.Builder {
    private AlertDialog alertDialog;

    private TextView pageSizeIndicator;
    private TextView tittleTextView;
    private TextView messageTextView;
    private Button negativeButton;
    private Button positiveButton;

    private int pageRow = 1;
    private int pageCol = 1;

    private List<MessageInfo> messageInfoList = new ArrayList<>();

    private GPaginator gPaginator = new GPaginator(1, 1, 0);

    private OnItemClickListener itemClickListener;

    public interface OnItemClickListener {
        void OnItemClick(int index);
    }

    public DialogMultiMessage(Context context, @NonNull List<MessageInfo> infoList) {
        super(context, R.style.AlertDialogTheme);
        messageInfoList = infoList;
        initGPaginator(CollectionUtils.getSize(infoList));
    }

    private View getContentView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.alert_dialog_content_multi_message, null);
        messageTextView = (TextView) view.findViewById(R.id.message);
        positiveButton = (Button) view.findViewById(R.id.button_positive);
        negativeButton = (Button) view.findViewById(R.id.button_negative);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.OnItemClick(gPaginator.getCurrentPage());
                } else {
                    alertDialog.dismiss();
                }
            }
        });
        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        return view;
    }

    private AlertDialog initDialog(AlertDialog dialog) {
        alertDialog = dialog;
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setView(getContentView());
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                BroadcastHelper.sendDialogOpenBroadcast(getContext(), "");
            }
        });
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                BroadcastHelper.sendDialogCloseBroadcast(getContext(), "");
            }
        });
        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        return alertDialog;
    }

    private void initBaseBuilder(View customTitleView) {
        setCustomTitle(customTitleView)
                .setCancelable(false);
    }

    @Override
    public AlertDialog create() {
        initCustomTitleView(LayoutInflater.from(getContext()).inflate(R.layout.alert_dialog_page_title_layout, null));
        AlertDialog dialog = super.create();
        initDialog(dialog);
        onPageChange(0);
        return dialog;
    }

    private void initCustomTitleView(View view) {
        tittleTextView = (TextView) view.findViewById(R.id.alert_title);
        pageSizeIndicator = (TextView) view.findViewById(R.id.page_size_indicator);
        setViewClickListener(view, R.id.button_previous, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPageChange(-1);
            }
        });
        setViewClickListener(view, R.id.button_next, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPageChange(1);
            }
        });
        initBaseBuilder(view);
    }

    private void setViewClickListener(View parentView, int id, @Nullable View.OnClickListener listener) {
        parentView.findViewById(id).setOnClickListener(listener);
    }

    private void initGPaginator(int count) {
        gPaginator.resize(1, 1, count);
        gPaginator.setCurrentPage(0);
    }

    public void addInfoList(@NonNull List<MessageInfo> infoList) {
        this.messageInfoList.addAll(infoList);
        gPaginator.resize(pageRow, pageCol, CollectionUtils.getSize(infoList));
        updatePageIndicator(gPaginator);
    }

    private void onPageChange(int diff) {
        int page = gPaginator.getCurrentPage() + diff;
        if (page < 0 || page >= gPaginator.pages()) {
            return;
        }
        gPaginator.setCurrentPage(page);
        updateInfo(page);
        updatePageIndicator(gPaginator);
    }

    private void updateInfo(int page) {
        if (page >= gPaginator.pages()) {
            return;
        }
        tittleTextView.setText(messageInfoList.get(page).title);
        messageTextView.setText(messageInfoList.get(page).info);
    }

    private void updatePageIndicator(GPaginator gPaginator) {
        pageSizeIndicator.setText(gPaginator.getCurrentPage() + 1 + "/" + gPaginator.pages());
    }

    public void setItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public MessageInfo removeIndex(int index) {
        MessageInfo messageInfo = messageInfoList.remove(index);
        if (CollectionUtils.isNullOrEmpty(messageInfoList)) {
            alertDialog.dismiss();
            return messageInfo;
        }
        int currentPage = gPaginator.getCurrentPage() - 1;
        if (currentPage < 0) {
            currentPage = 0;
        }
        gPaginator.resize(pageRow, pageCol, CollectionUtils.getSize(messageInfoList));
        gPaginator.setCurrentPage(currentPage);
        updatePageIndicator(gPaginator);
        updateInfo(gPaginator.getCurrentPage());
        return messageInfo;
    }

    public AlertDialog getAlertDialog() {
        return alertDialog;
    }
}
