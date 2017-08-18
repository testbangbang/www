package com.onyx.android.dr.view;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onyx.android.dr.R;
import com.onyx.android.dr.adapter.SearchHintListAdapter;
import com.onyx.android.dr.reader.view.PageRecyclerView;

import java.util.List;


/**
 * Created by hehai on 2016/12/16.
 */
public class CustomSearchView extends LinearLayout implements View.OnClickListener {
    private Context context;
    private EditText etInput;
    private ImageView ivDelete;
    private PageRecyclerView lvTips;
    private SearchViewListener listener;
    private TextView title;
    private TextView clearHistory;
    private View line;
    private LinearLayout listLayout;
    private SearchHintListAdapter adapter;
    private boolean isSetText;
    private ImageView ivSearch;

    public CustomSearchView(Context context) {
        super(context);
    }

    public CustomSearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.search_layout, this);
        initView();
        initListener();
    }

    private void initView() {
        etInput = (EditText) findViewById(R.id.search_et_input);
        etInput.requestFocus();
        ivDelete = (ImageView) findViewById(R.id.search_iv_delete);
        ivSearch = (ImageView) findViewById(R.id.search);
        lvTips = (PageRecyclerView) findViewById(R.id.search_lv_tips);
        adapter = new SearchHintListAdapter();
        lvTips.setAdapter(adapter);
        title = (TextView) findViewById(R.id.title);
        line = findViewById(R.id.line);
        clearHistory = (TextView) findViewById(R.id.clear_history);
        listLayout = (LinearLayout) findViewById(R.id.list_layout);
    }

    private void initListener() {
        ivDelete.setOnClickListener(this);
        ivSearch.setOnClickListener(this);

        etInput.addTextChangedListener(new EditChangedListener());
        etInput.setOnClickListener(this);
        etInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    listLayout.setVisibility(GONE);
                    notifyStartSearching(etInput.getText().toString());
                }
                return true;
            }
        });
        etInput.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (listener != null) {
                        listener.onRefreshAutoComplete(etInput.getText() + "");
                    }
                    listLayout.setVisibility(VISIBLE);
                } else {
                    listLayout.setVisibility(GONE);
                }
            }
        });
        clearHistory.setOnClickListener(this);

        adapter.setOnItemClickListener(new SearchHintListAdapter.OnItemClickListener() {
            @Override
            public void onClick(View v, String string) {
                //set edit text
                String text = string;
                isSetText = true;
                etInput.setText(text);
                etInput.setSelection(text.length());
                //hint list view gone and result list view show
                listLayout.setVisibility(View.GONE);
                notifyStartSearching(text);
                isSetText = false;
            }
        });
    }

    private void notifyStartSearching(String text) {
        if (listener != null) {
            listener.onSearch(text);
        }
        //hide soft keyboard
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * set history
     */
    public void setSearchHistory(List<String> books) {
        adapter.setList(books);
        adapter.notifyDataSetChanged();
        showHistory(true);
        listLayout.setVisibility(adapter.getList().size() == 0 ? GONE : VISIBLE);

    }

    /**
     * set hint
     */
    public void setSearchResult(List<String> books) {
        adapter.setList(books);
        adapter.notifyDataSetChanged();
        showHistory(false);
        listLayout.setVisibility(adapter.getList().size() == 0 ? GONE : VISIBLE);
    }

    private class EditChangedListener implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            if (listener != null && !isSetText) {
                listener.onRefreshAutoComplete(charSequence + "");
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    }

    public interface SearchViewListener {

        void onRefreshAutoComplete(String text);

        void onSearch(String text);

        void clearHistory();
    }

    public void setSearchViewListener(SearchViewListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_et_input:
                break;
            case R.id.search_iv_delete:
                etInput.setText("");
                break;
            case R.id.clear_history:
                listener.clearHistory();
                showHistory(false);
                break;
            case R.id.search:
                search();
                break;
        }
    }

    private void search() {
        listLayout.setVisibility(GONE);
        notifyStartSearching(etInput.getText().toString());
    }

    private void showHistory(boolean isShow) {
        title.setVisibility(isShow ? VISIBLE : GONE);
        clearHistory.setVisibility(isShow ? VISIBLE : GONE);
        line.setVisibility(isShow ? VISIBLE : GONE);
    }

    public boolean getHintListStatus() {
        return listLayout.getVisibility() == VISIBLE;
    }

    public void showHintList(boolean flag) {
        listLayout.setVisibility(flag ? VISIBLE : GONE);
    }

    public void setEditTextHint(CharSequence charSequence) {
        etInput.setHint(charSequence);
    }
}
