package com.onyx.einfo.dialog;

import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.onyx.einfo.R;
import com.onyx.android.sdk.ui.dialog.OnyxAlertDialog;
import com.onyx.android.sdk.utils.StringUtils;

/**
 * Created by suicheng on 2016/8/8.
 */
public class DialogAddEdit extends OnyxAlertDialog {
    private static final String DEFAULT_SYMBOL = "&*&";
    private Context context;
    private String dialogTitle;
    private String[] editTitle;
    private int editCount;
    private EditText[] editTextGroup;
    private OnCreatedListener listener;

    public DialogAddEdit() {
        super();
    }

    /**
     * @param editTitle 可以传null进来，表示这个edit无左边标题, 如果不为空，长度最好和editCount匹配
     * @param editCount 表示editText的数量
     */
    public DialogAddEdit(Context context, String dialogTitle, String[] editTitle, int editCount) {
        this.context = context;
        this.dialogTitle = dialogTitle;
        this.editTitle = editTitle;
        this.editCount = editCount;
        this.editTextGroup = new EditText[editCount];
    }

    public interface OnCreatedListener {
        void onCreate(String combineString, String spitSymbol);
    }

    public void setOnCreatedListener(OnCreatedListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setParams(new Params().setTittleString(dialogTitle)
                .setCustomContentLayoutResID(R.layout.alert_dialog_content_edit_group)
                .setCustomViewAction(new CustomViewAction() {
                    @Override
                    public void onCreateCustomView(View customView, TextView pageIndicator) {
                        TableLayout tableLayout = (TableLayout) customView.findViewById(R.id.content_edit_group);
                        addEditToTableLayout(tableLayout, editTitle, editCount);
                    }
                })
                .setPositiveAction(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!isHasContent(editTextGroup)) {
                            dismiss();
                            return;
                        }
                        if (listener != null) {
                            StringBuilder stringBuilder = new StringBuilder();
                            String symbol = DEFAULT_SYMBOL;
                            for (EditText editText : editTextGroup) {
                                stringBuilder.append(editText.getText().toString()).append(symbol);
                            }
                            String combineString = stringBuilder.toString();
                            if (combineString.length() > 0) {
                                combineString = combineString.substring(0, combineString.length() - symbol.length());
                            }
                            listener.onCreate(combineString, symbol);
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

    private void addEditToTableLayout(TableLayout tableLayout, String[] editTitle, int editCount) {
        for (int i = 0; i < editCount; i++) {
            TableRow tableRow = (TableRow) LayoutInflater.from(context).inflate(R.layout.alert_dialog_content_edit, null);
            if (editTitle != null && i < editTitle.length && StringUtils.isNotBlank(editTitle[i])) {
                ((TextView) tableRow.findViewById(R.id.edit_title)).setText(editTitle[i]);
            }
            editTextGroup[i] = (EditText) tableRow.findViewById(R.id.edit_text);
            tableLayout.addView(tableRow);
        }
    }

    private boolean isHasContent(EditText[] editTextGroup) {
        boolean result = false;
        for (EditText editText : editTextGroup) {
            result = !StringUtils.isNullOrEmpty(editText.getText().toString());
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
