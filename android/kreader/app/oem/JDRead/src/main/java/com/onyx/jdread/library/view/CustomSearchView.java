package com.onyx.jdread.library.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onyx.jdread.R;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.main.common.ToastUtil;
import com.onyx.jdread.util.InputUtils;

/**
 * Created by hehai on 18-3-10.
 */

public class CustomSearchView extends LinearLayout implements TextWatcher {
    private CharSequence temp;
    private int selectionStart;
    private int selectionEnd;
    private int maxByte = Integer.MAX_VALUE;
    private EditText etInput;
    private SearchView.OnQueryTextListener onQueryTextListener;
    private SearchListener customSearchListener;

    public interface SearchListener {
        void onQuerySearch(String query);
    }

    public CustomSearchView(Context context) {
        super(context);
    }

    public CustomSearchView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.search_layout, this);
        initView();
        initListener();
    }

    private void initView() {
        etInput = (EditText) findViewById(R.id.search_et_input);
    }

    private void initListener() {
        etInput.addTextChangedListener(this);
        etInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH && onQueryTextListener != null) {
                    onQueryTextListener.onQueryTextSubmit(textView.getText().toString());
                }
                return true;
            }
        });
        findViewById(R.id.search_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                doSearch();
            }
        });
        findViewById(R.id.search_clear).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                etInput.setText("");
            }
        });
    }

    private void doSearch() {
        String text = etInput.getText().toString();
        if (customSearchListener != null) {
            customSearchListener.onQuerySearch(text);
            return;
        }
        if (onQueryTextListener != null) {
            onQueryTextListener.onQueryTextSubmit(text);
        }
    }

    public void setCustomSearchListener(SearchListener listener) {
        this.customSearchListener = listener;
    }

    public void setOnQueryTextListener(SearchView.OnQueryTextListener onQueryTextListener) {
        this.onQueryTextListener = onQueryTextListener;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        temp = s;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        selectionStart = etInput.getSelectionStart();
        selectionEnd = etInput.getSelectionEnd();
        if (InputUtils.getByteCount(temp.toString()) > maxByte) {
            ToastUtil.showOffsetToast(ResManager.getString(R.string.the_input_has_exceeded_the_upper_limit), ResManager.getInteger(R.integer.toast_offset_y));
            s.delete(selectionStart - 1, selectionEnd);
            int tempSelection = selectionStart;
            etInput.setText(s);
            etInput.setSelection(tempSelection);
        }
        if (onQueryTextListener != null) {
            onQueryTextListener.onQueryTextChange(s.toString());
        }
    }

    public void setQuery(CharSequence query, boolean submit) {
        etInput.setText(query);
        if (submit && onQueryTextListener != null) {
            onQueryTextListener.onQueryTextSubmit(query.toString());
        }
    }

    public CharSequence getQueryHint() {
        return etInput.getHint();
    }

    public void setQueryHint(String hint) {
        etInput.setHint(hint);
    }

    public CharSequence getQuery() {
        return etInput.getText();
    }

    public void setMaxByte(int maxByte) {
        this.maxByte = maxByte;
    }
}
