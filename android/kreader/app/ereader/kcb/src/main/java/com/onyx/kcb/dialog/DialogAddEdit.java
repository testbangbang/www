package com.onyx.kcb.dialog;

import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TableLayout;
import android.widget.TextView;

import com.onyx.android.sdk.ui.dialog.OnyxAlertDialog;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.kcb.R;
import com.onyx.kcb.databinding.AlertDialogContentEditBinding;
import com.onyx.kcb.model.DialogAddEditModel;
import com.onyx.kcb.model.TabRowModel;

/**
 * Created by suicheng on 2016/8/8.
 */
public class DialogAddEdit extends OnyxAlertDialog {
    private static final String DEFAULT_SYMBOL = "&";
    private Context context;
    private DialogAddEditModel model;
    private OnCreatedListener listener;

    public DialogAddEdit() {
        super();
    }

    @SuppressLint("ValidFragment")
    public DialogAddEdit(Context context, DialogAddEditModel model) {
        this.context = context;
        this.model = model;
    }

    public interface OnCreatedListener {
        void onCreate();
    }

    public void setOnCreatedListener(OnCreatedListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setParams(new Params().setTittleString(model.contentTitle.get())
                .setCustomContentLayoutResID(R.layout.alert_dialog_content_edit_group)
                .setCustomViewAction(new CustomViewAction() {
                    @Override
                    public void onCreateCustomView(View customView, TextView pageIndicator) {
                        TableLayout tableLayout = (TableLayout) customView.findViewById(R.id.content_edit_group);
                        addEditToTableLayout(tableLayout);
                    }
                })
                .setPositiveAction(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!isHasContent()) {
                            dismiss();
                            return;
                        }
                        if (listener != null) {
                            StringBuilder stringBuilder = new StringBuilder();
                            String symbol = DEFAULT_SYMBOL;
                            for (TabRowModel tabRowModel : model.edits) {
                                stringBuilder.append(tabRowModel.text.get()).append(symbol);
                            }
                            String combineString = stringBuilder.toString();
                            if (combineString.length() > 0) {
                                combineString = combineString.substring(0, combineString.length() - symbol.length());
                            }
                            model.combine.set(combineString);
                            model.spitSymbol.set(symbol);
                            listener.onCreate();
                        }
                        dismiss();
                    }
                })
        );
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        super.onActivityCreated(savedInstanceState);
    }

    private void addEditToTableLayout(TableLayout tableLayout) {
        for (int i = 0; i < model.getEditCount(); i++) {
            View inflate = LayoutInflater.from(context).inflate(R.layout.alert_dialog_content_edit, null);
            AlertDialogContentEditBinding bind = DataBindingUtil.bind(inflate);
            final TabRowModel tabRowModel = new TabRowModel();
            bind.setTabRow(tabRowModel);
            tabRowModel.title.set(model.titles.get(i));
            model.edits.add(tabRowModel);
            tableLayout.addView(bind.getRoot());
        }
    }

    private boolean isHasContent() {
        boolean result = false;
        for (TabRowModel tabRowModel : model.edits) {
            result = !StringUtils.isNullOrEmpty(tabRowModel.text.get());
            if (result) {
                break;
            }
        }
        return result;
    }

    public void show(FragmentManager fm) {
        super.show(fm, this.getClass().getSimpleName());
    }
}
