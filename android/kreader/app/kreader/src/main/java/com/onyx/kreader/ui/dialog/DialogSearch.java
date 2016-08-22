package com.onyx.kreader.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.onyx.android.sdk.ui.view.OnyxCustomEditText;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.kreader.R;
import com.onyx.kreader.api.ReaderSelection;
import com.onyx.kreader.dataprovider.SearchHistory;
import com.onyx.kreader.ui.actions.GetSearchHistoryAction;
import com.onyx.kreader.ui.actions.GotoPageAction;
import com.onyx.kreader.ui.actions.SearchContentAction;
import com.onyx.kreader.ui.actions.ToggleSearchHistoryAction;
import com.onyx.kreader.ui.data.ReaderDataHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ming on 16/7/21.
 */
public class DialogSearch extends Dialog implements View.OnClickListener, TextView.OnEditorActionListener {

    private static final String TAG = DialogSearch.class.getSimpleName();
    private static int SEARCH_CONTENT_ROW = 8;
    private static int SEARCH_HISTORY_COUNT = 5;
    public static final int SEARCH_CONTENT_LENGTH = 30;

    private ReaderDataHolder readerDataHolder;
    private SearchContentAction searchContentAction;
    private int currentPagePosition = 0;

    private PageRecyclerView pageRecyclerView;
    private RecyclerView historyRecyclerView;
    private ImageView backView;
    private TextView backText;
    private TextView totalPage;
    private TextView pageIndicator;
    private OnyxCustomEditText searchEditText;
    private ImageButton preIcon;
    private ImageButton nextIcon;
    private LinearLayout searchContent;
    private LinearLayout loadingLayout;
    private RelativeLayout searchHistory;
    private RelativeLayout searchEditLayout;
    private TextView deleteHistory;
    private TextView closeHistory;

    private List<ReaderSelection> searchList = new ArrayList<>();
    private List<SearchHistory> historyList = new ArrayList<>();
    private String searchText;

    public DialogSearch(final ReaderDataHolder readerDataHolder) {
        super(readerDataHolder.getContext(), android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);

        setContentView(R.layout.dialog_search);
        this.readerDataHolder = readerDataHolder;
        init();
    }

    private void init(){
        pageRecyclerView = (PageRecyclerView)findViewById(R.id.search_recycler_view);
        backView = (ImageView) findViewById(R.id.image_view_back);
        backText = (TextView) findViewById(R.id.text_view_back);
        totalPage = (TextView)findViewById(R.id.total_page);
        pageIndicator = (TextView)findViewById(R.id.page_indicator);
        searchEditText = (OnyxCustomEditText)findViewById(R.id.edit_view_search);
        searchContent = (LinearLayout) findViewById(R.id.search_content);
        loadingLayout = (LinearLayout) findViewById(R.id.loading_layout);
        searchHistory = (RelativeLayout) findViewById(R.id.search_history_layout);
        searchEditLayout = (RelativeLayout) findViewById(R.id.search_edit_layout);
        preIcon = (ImageButton) findViewById(R.id.pre_icon);
        nextIcon = (ImageButton) findViewById(R.id.next_icon);
        historyRecyclerView = (RecyclerView) findViewById(R.id.search_history_list);
        deleteHistory = (TextView) findViewById(R.id.delete_history);
        closeHistory = (TextView) findViewById(R.id.close_history);
        searchContent.setVisibility(View.INVISIBLE);
        deleteHistory.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        closeHistory.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

        backView.setOnClickListener(this);
        backText.setOnClickListener(this);
        preIcon.setOnClickListener(this);
        nextIcon.setOnClickListener(this);
        deleteHistory.setOnClickListener(this);
        closeHistory.setOnClickListener(this);
        searchEditText.setOnClickListener(this);
        searchEditText.setOnEditorActionListener(this);

        searchEditLayout.post(new Runnable() {
            @Override
            public void run() {
                showSearchHistoryView();
            }
        });
        searchEditText.setOnKeyPreImeListener(new OnyxCustomEditText.onKeyPreImeListener() {
            @Override
            public void onKeyPreIme(int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK){
                    searchHistory.setVisibility(View.GONE);
                }
            }
        });

        initPageRecyclerView();
    }

    private void showSearchHistoryView(){
        showSoftInputWindow();
        int left = searchEditLayout.getLeft();
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(searchEditLayout.getMeasuredWidth(), ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(left,0,0,0);
        searchHistory.setLayoutParams(lp);
        initSearchHistoryList();
    }

    private void initPageRecyclerView(){
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
                ((SearchViewHolder)holder).bindView(searchList.get(position),searchText);
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

    private void initSearchHistoryList(){
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        historyRecyclerView.setAdapter(new RecyclerView.Adapter() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new HistoryViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_search_history_list_item_view, parent, false));
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                ((HistoryViewHolder)holder).bindView(historyList.get(position));
            }

            @Override
            public int getItemCount() {
                return historyList.size();
            }
        });

        loadSearchHistoryData();
    }

    private void loadSearchHistoryData(){
        new GetSearchHistoryAction(SEARCH_HISTORY_COUNT, new GetSearchHistoryAction.CallBack() {
            @Override
            public void loadFinished(List<SearchHistory> searchHistoryList) {
                historyList = searchHistoryList;
                if (historyList != null && historyList.size() > 0){
                    searchHistory.setVisibility(View.VISIBLE);
                    historyRecyclerView.getAdapter().notifyDataSetChanged();
                }else {
                    searchHistory.setVisibility(View.GONE);
                }

            }
        }).execute(readerDataHolder);
    }

    private void loadSearchData(){
        searchHistory.setVisibility(View.GONE);
        searchContent.setVisibility(View.VISIBLE);
        stopSearch();
        searchText = searchEditText.getText().toString();
        if (StringUtils.isNullOrEmpty(searchText)){
            return;
        }

        showLoadingLayout();
        new ToggleSearchHistoryAction(searchText, true).execute(readerDataHolder);
        pageRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                searchList.clear();
                updatePageIndicator(0,0);
                pageRecyclerView.setCurrentPage(0);

                searchContentAction = new SearchContentAction(searchText,SEARCH_CONTENT_LENGTH);
                searchContentAction.execute(readerDataHolder, new SearchContentAction.OnSearchContentCallBack() {
                    @Override
                    public void OnNext(final List<ReaderSelection> results) {
                        if (results == null || results.size() < 1){
                            return;
                        }
                        hideLoadingLayout();
                        searchList.addAll(results);
                        updatePageIndicator(currentPagePosition,pageRecyclerView.getAdapter().getItemCount());
                        pageRecyclerView.getAdapter().notifyDataSetChanged();
                    }

                    @Override
                    public void OnFinishedSearch() {
                        hideLoadingLayout();
                    }
                });
            }
        },500);

    }

    @Override
    public void onClick(View v) {
        if (v.equals(backView) || v.equals(backText)){
            hideSearchDialog();
        }else if (v.equals(nextIcon)){
            pageRecyclerView.nextPage();
        }else if (v.equals(preIcon)){
            pageRecyclerView.prevPage();
        }else if (v.equals(closeHistory)){
            searchHistory.setVisibility(View.GONE);
            hideSoftInputWindow();
        }else if (v.equals(deleteHistory)){
            new ToggleSearchHistoryAction("", false).execute(readerDataHolder);
            searchHistory.setVisibility(View.GONE);
        }else if (v.equals(searchEditText)){
            stopSearch();
            loadSearchHistoryData();
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE){
            hideSoftInputWindow();
            loadSearchData();
        }
        return true;
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

    private void showSoftInputWindow(){
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(searchEditText,0);
    }

    private class SearchViewHolder extends RecyclerView.ViewHolder{

        private TextView contentTextView;
        private TextView contentPage;
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
            contentPage = (TextView)itemView.findViewById(R.id.search_page);
        }

        public void bindView(ReaderSelection selection,String search){
            readerSelection = selection;
            String leftText = StringUtils.deleteNewlineSymbol(selection.getLeftText().trim());
            String rightText = StringUtils.deleteNewlineSymbol(selection.getRightText().trim());
            String content = leftText + search + rightText;
            SpannableStringBuilder style = new SpannableStringBuilder(content);
            int start = leftText.length();
            if (start < 0){
                return;
            }
            int length = search.length();
            style.setSpan(new BackgroundColorSpan(Color.BLACK),start, start + length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            style.setSpan(new ForegroundColorSpan(Color.WHITE),start, start + length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            contentTextView.setText(style);
            int pagePosition = Integer.valueOf(readerSelection.getPagePosition());
            String page = String.format(getContext().getString(R.string.page), pagePosition + 1);
            contentPage.setText(page);
        }
    }

    private class HistoryViewHolder extends RecyclerView.ViewHolder{

        private TextView historyTextView;

        public HistoryViewHolder(View itemView) {
            super(itemView);
            historyTextView = (TextView)itemView.findViewById(R.id.search_history);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    searchHistory.setVisibility(View.GONE);
                    searchEditText.setText(historyTextView.getText());
                    hideSoftInputWindow();
                    loadSearchData();
                }
            });
        }

        public void bindView(SearchHistory history){
            historyTextView.setText(history.getContent());
        }
    }

    private void updatePageIndicator(int position, int itemCount){
        currentPagePosition = position;
        int page = itemCount / SEARCH_CONTENT_ROW;
        int currentPage = page > 0 ? position / SEARCH_CONTENT_ROW + 1 : 0;
        String indicator = String.format("%d/%d",currentPage,page);
        String total = String.format(readerDataHolder.getContext().getString(R.string.total_page),searchList.size());
        pageIndicator.setText(indicator);
        totalPage.setText(total);
    }

    private void hideSearchDialog(){
        hide();
        stopSearch();
    }

    private void showLoadingLayout(){
        loadingLayout.setVisibility(View.VISIBLE);
    }

    private void hideLoadingLayout(){
        loadingLayout.setVisibility(View.GONE);
    }
}
