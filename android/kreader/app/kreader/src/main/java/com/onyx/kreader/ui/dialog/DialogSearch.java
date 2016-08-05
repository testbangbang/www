package com.onyx.kreader.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.kreader.R;
import com.onyx.kreader.api.ReaderSelection;
import com.onyx.kreader.ui.actions.GotoPageAction;
import com.onyx.kreader.ui.actions.SearchContentAction;
import com.onyx.kreader.ui.data.ReaderDataHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ming on 16/7/21.
 */
public class DialogSearch extends Dialog implements View.OnClickListener {

    private static final String TAG = DialogSearch.class.getSimpleName();
    private static int SEARCH_CONTENT_ROW = 4;

    private ReaderDataHolder readerDataHolder;
    private SearchContentAction searchContentAction;
    private int currentPagePosition = 0;

    private Button btnSearch;
    private PageRecyclerView pageRecyclerView;
    private ImageView back;
    private TextView totalPage;
    private TextView pageIndicator;
    private TextView title;
    private EditText searchEditText;

    private List<ReaderSelection> searchList = new ArrayList<>();

    public DialogSearch(final ReaderDataHolder readerDataHolder) {
        super(readerDataHolder.getContext(), R.style.dialog_no_title);

        setContentView(R.layout.dialog_search);
        fitDialogToWindow();
        this.readerDataHolder = readerDataHolder;
        init();
    }

    private void fitDialogToWindow() {
        Window mWindow = getWindow();
        WindowManager.LayoutParams mParams = mWindow.getAttributes();
        mParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        mParams.gravity = Gravity.BOTTOM;
        mWindow.setAttributes(mParams);
        //force use all space in the screen.
        mWindow.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    private void init(){
        btnSearch = (Button)findViewById(R.id.search_button);
        pageRecyclerView = (PageRecyclerView)findViewById(R.id.search_recycler_view);
        back = (ImageView) findViewById(R.id.image_view_back);
        totalPage = (TextView)findViewById(R.id.total_page);
        pageIndicator = (TextView)findViewById(R.id.page_indicator);
        title = (TextView)findViewById(R.id.title);
        searchEditText = (EditText)findViewById(R.id.edit_view_search);

        back.setOnClickListener(this);
        btnSearch.setOnClickListener(this);

        title.setText(readerDataHolder.getBookName());
        pageRecyclerView.setAdapter(new PageRecyclerView.PageAdapter() {
            @Override
            public int getRowCount() {
                return SEARCH_CONTENT_ROW;
            }

            @Override
            public int getColumnCount() {
                return 1;
            }

            @Override
            public int getDataCount() {
                return searchList.size();
            }

            @Override
            public RecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
                return new SearchViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_search_list_item_view, parent, false));
            }

            @Override
            public void onPageBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                ((SearchViewHolder)holder).bindView(searchList.get(position),searchEditText.getText().toString());
            }
        });
        pageRecyclerView.setOnPagingListener(new PageRecyclerView.OnPagingListener() {
            @Override
            public void onPrevPage(int prevPosition, int itemCount, int pageSize) {
                updatePageIndicator(prevPosition,itemCount);
            }

            @Override
            public void onNextPage(int nextPosition, int itemCount, int pageSize) {
                updatePageIndicator(nextPosition,itemCount);
            }
        });
        updatePageIndicator(0,0);
    }

    private void loadSearchData(){
        searchList.clear();
        final String query = searchEditText.getText().toString();
        if (StringUtils.isNullOrEmpty(query)){
            return;
        }

        searchContentAction = new SearchContentAction(query);
        searchContentAction.execute(readerDataHolder, new SearchContentAction.OnSearchContentCallBack() {
            @Override
            public void OnNext(final List<ReaderSelection> results) {
                if (results == null || results.size() < 1){
                    return;
                }
                final int hasCount = searchList.size();
                searchList.addAll(results);
                updatePageIndicator(currentPagePosition,pageRecyclerView.getAdapter().getItemCount());
                pageRecyclerView.getAdapter().notifyItemRangeInserted(hasCount,results.size());
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.equals(back)){
            hideSearchDialog();
        }else if (v.equals(btnSearch)){
            hideSoftInputWindow();
            loadSearchData();
        }
    }

    private void stopSearch(){
        if (searchContentAction != null){
            searchContentAction.stopSearch();
        }
    }

    private void hideSoftInputWindow(){
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchEditText.getWindowToken(),0);
    }

    private class SearchViewHolder extends RecyclerView.ViewHolder{

        private TextView contentTextView;
        private ReaderSelection readerSelection;

        public SearchViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideSearchDialog();
                    new GotoPageAction(readerSelection.getPagePosition()).execute(readerDataHolder);
                }
            });
            contentTextView = (TextView)itemView.findViewById(R.id.search_content);
        }

        public void bindView(ReaderSelection selection,String search){
            readerSelection = selection;
            String content = selection.getLeftText().trim() + search + selection.getRightText().trim();
            SpannableStringBuilder style = new SpannableStringBuilder(content);
            int start = content.indexOf(search);
            if (start < 0){
                return;
            }
            int length = search.length();
            style.setSpan(new BackgroundColorSpan(Color.BLACK),start, start + length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            style.setSpan(new ForegroundColorSpan(Color.WHITE),start, start + length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            contentTextView.setText(style);
        }
    }

    private void updatePageIndicator(int position, int itemCount){
        currentPagePosition = position;
        int page = itemCount / SEARCH_CONTENT_ROW;
        int currentPage = page > 0 ? position / SEARCH_CONTENT_ROW + 1 : 0;
        String indicator = String.format(readerDataHolder.getContext().getString(R.string.page_indicator),currentPage,page);
        String total = String.format(readerDataHolder.getContext().getString(R.string.total_page),searchList.size());
        pageIndicator.setText(indicator);
        totalPage.setText(total);
    }

    private void hideSearchDialog(){
        hide();
        stopSearch();
    }
}
