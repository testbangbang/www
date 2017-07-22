package com.onyx.android.dr.reader.dialog;

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
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.onyx.android.dr.R;
import com.onyx.android.dr.reader.common.ReadPageInfo;
import com.onyx.android.dr.reader.event.GetSearchHistoryEvent;
import com.onyx.android.dr.reader.event.GotoPositionActionResultEvent;
import com.onyx.android.dr.reader.presenter.ReaderPresenter;
import com.onyx.android.dr.reader.view.PageRecyclerView;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.model.SearchHistory;
import com.onyx.android.sdk.reader.api.ReaderSelection;
import com.onyx.android.sdk.reader.host.impl.ReaderTextSplitterImpl;
import com.onyx.android.sdk.ui.view.OnyxCustomEditText;
import com.onyx.android.sdk.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ming on 16/7/21.
 */
public class DialogSearch extends Dialog {

    private static final String TAG = DialogSearch.class.getSimpleName();
    private static final int SEARCH_HISTORY_COUNT = 10;
    private static final int SEARCH_PAGE_ONE_TIME = 20;
    private String selectionText;
    private ReaderPresenter readerPresenter;

    private int currentPagePosition = 0;
    private int nextRequestPage = SEARCH_PAGE_ONE_TIME;
    private int startPage = 0;
    private boolean nextPageAction = false;

    private PageRecyclerView pageRecyclerView;
    private RecyclerView historyRecyclerView;
    private TextView totalPage;
    private TextView pageIndicator;
    private TextView searchingText;
    private LinearLayout backLayout;
    private OnyxCustomEditText searchEditText;
    private ImageButton preIcon;
    private ImageButton nextIcon;
    private ImageButton closeSearch;
    private LinearLayout searchContent;
    private LinearLayout loadingLayout;
    private RelativeLayout searchHistory;
    private RelativeLayout searchEditLayout;
    private RelativeLayout searchInputLayout;
    private TextView deleteHistory;
    private TextView closeHistory;
    private RelativeLayout floatToolBar;
    private ImageButton back;
    private ImageButton prev;
    private ImageButton next;
    private ImageButton close;
    private View dividerLine;

    private List<ReaderSelection> searchList = new ArrayList<>();
    private List<ReaderSelection> showSearchList = new ArrayList<>();
    private List<SearchHistory> historyList = new ArrayList<>();
    private String searchText;
    private int searchRows = 0;
    private int searchChineseContentLength = 0;
    private int searchAlphaContentLength = 0;
    private int currentSearchIndex = 0;

    public DialogSearch(final ReaderPresenter readerPresenter, String selectionText) {
        super(readerPresenter.getReaderView().getViewContext(), android.R.style.Theme_Translucent_NoTitleBar);

        setContentView(R.layout.dialog_search);
        this.readerPresenter = readerPresenter;
        this.selectionText = selectionText;
        searchRows = getContext().getResources().getInteger(R.integer.search_row);
        searchChineseContentLength = getContext().getResources().getInteger(R.integer.search_chinese_content_length);
        searchAlphaContentLength = getContext().getResources().getInteger(R.integer.search_alpha_content_length);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        EventBus.getDefault().register(this);
        init();
    }

    private void init() {
        pageRecyclerView = (PageRecyclerView) findViewById(R.id.search_recycler_view);
        backLayout = (LinearLayout) findViewById(R.id.back_layout);
        totalPage = (TextView) findViewById(R.id.total_page);
        pageIndicator = (TextView) findViewById(R.id.page_indicator);
        searchingText = (TextView) findViewById(R.id.textview_message);
        searchEditText = (OnyxCustomEditText) findViewById(R.id.edit_view_search);
        searchContent = (LinearLayout) findViewById(R.id.search_content);
        loadingLayout = (LinearLayout) findViewById(R.id.loading_layout);
        searchHistory = (RelativeLayout) findViewById(R.id.search_history_layout);
        searchEditLayout = (RelativeLayout) findViewById(R.id.search_edit_layout);
        searchInputLayout = (RelativeLayout) findViewById(R.id.search_input_layout);
        preIcon = (ImageButton) findViewById(R.id.pre_icon);
        nextIcon = (ImageButton) findViewById(R.id.next_icon);
        closeSearch = (ImageButton) findViewById(R.id.close_search);
        historyRecyclerView = (RecyclerView) findViewById(R.id.search_history_list);
        deleteHistory = (TextView) findViewById(R.id.delete_history);
        closeHistory = (TextView) findViewById(R.id.close_history);
        searchContent.setVisibility(View.INVISIBLE);
        deleteHistory.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        closeHistory.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        floatToolBar = (RelativeLayout) findViewById(R.id.search_float_layout);
        back = (ImageButton) findViewById(R.id.search_back);
        prev = (ImageButton) findViewById(R.id.search_prev);
        next = (ImageButton) findViewById(R.id.search_next);
        close = (ImageButton) findViewById(R.id.search_close);
        dividerLine = findViewById(R.id.divider_line);

        searchEditLayout.post(new Runnable() {
            @Override
            public void run() {
                showSearchHistoryView();
            }
        });

        setViewListener();
        initPageRecyclerView();
        updateSearchingText(0);
    }

    private void setViewListener() {
        if (!StringUtils.isNullOrEmpty(selectionText)) {
            searchEditText.setText(selectionText);
        }

        closeSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopSearch();
            }
        });

        backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDialog();
            }
        });

        preIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageRecyclerView.prevPage();
            }
        });

        nextIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pageRecyclerView.getPaginator().hasNextPage()) {
                    pageRecyclerView.nextPage();
                } else {
                    nextSearch();
                }
            }
        });

        closeHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchHistory.setVisibility(View.GONE);
                hideSoftInputWindow();
            }
        });

        deleteHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readerPresenter.getBookOperate().onToggleSearchHistory("", false);
                searchHistory.setVisibility(View.GONE);
            }
        });

        searchEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopSearch();
                loadSearchHistoryData();
            }
        });

        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    hideSoftInputWindow();
                    loadSearchData();
                }
                return true;
            }
        });

        searchEditText.setOnKeyPreImeListener(new OnyxCustomEditText.onKeyPreImeListener() {
            @Override
            public void onKeyPreIme(int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    searchHistory.setVisibility(View.GONE);
                }
            }
        });
        searchEditText.requestFocus();

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDialog();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideFloatToolBar();
                pageRecyclerView.gotoPageByIndex(currentSearchIndex);
            }
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentSearchIndex == 0) {
                    Toast.makeText(getContext(), getContext().getString(R.string.no_more_search_results), Toast.LENGTH_SHORT).show();
                    return;
                }
                gotoSearchPage(currentSearchIndex - 1, null);
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentSearchIndex == (showSearchList.size() - 1)) {
                    Toast.makeText(getContext(), getContext().getString(R.string.no_more_search_results), Toast.LENGTH_SHORT).show();
                    return;
                }
                gotoSearchPage(currentSearchIndex + 1, null);
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDialog();
            }
        });
    }

    private void showSearchHistoryView() {
        showSoftInputWindow();
        int left = searchEditLayout.getLeft();
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(searchEditLayout.getMeasuredWidth(), ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(left, 0, 0, 0);
        searchHistory.setLayoutParams(lp);
        initSearchHistoryList();
    }

    private void initPageRecyclerView() {
        pageRecyclerView.setAdapter(new PageRecyclerView.PageAdapter() {
            @Override
            public int getRowCount() {
                return searchRows;
            }

            @Override
            public int getColumnCount() {
                return 1;
            }

            @Override
            public int getDataCount() {
                return showSearchList.size();
            }

            @Override
            public RecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
                return new SearchViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_search_list_item_view, parent, false));
            }

            @Override
            public void onPageBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                ((SearchViewHolder) holder).bindView(showSearchList.get(position), searchText, position);
            }
        });
        pageRecyclerView.setOnPagingListener(new PageRecyclerView.OnPagingListener() {
            @Override
            public void onPageChange(int position, int itemCount, int pageSize) {
                updatePageIndicator(position, itemCount);
            }
        });
        updatePageIndicator(0, 0);
    }

    private void initSearchHistoryList() {
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        historyRecyclerView.setAdapter(new RecyclerView.Adapter() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new HistoryViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_search_history_list_item_view, parent, false));
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                ((HistoryViewHolder) holder).bindView(historyList.get(position));
            }

            @Override
            public int getItemCount() {
                return historyList.size();
            }
        });

        loadSearchHistoryData();
    }

    private void loadSearchHistoryData() {
        readerPresenter.getBookOperate().getSearchHistory(SEARCH_HISTORY_COUNT);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetSearchHistoryEvent(GetSearchHistoryEvent event) {
        historyList = event.getSearchHistoryList();
        if (historyList != null && historyList.size() > 0) {
            searchHistory.setVisibility(View.VISIBLE);
            historyRecyclerView.getAdapter().notifyDataSetChanged();
        } else {
            searchHistory.setVisibility(View.GONE);
        }
    }

    private void loadSearchData() {
        searchHistory.setVisibility(View.GONE);
        searchContent.setVisibility(View.VISIBLE);
        stopSearch();
        searchText = searchEditText.getText().toString();
        if (StringUtils.isNullOrEmpty(searchText)) {
            return;
        }

        readerPresenter.getBookOperate().onToggleSearchHistory(searchText, true);
        showLoadingLayout();
        reset();
        int contentLength = ReaderTextSplitterImpl.isAlpha(searchText.charAt(0)) ? searchAlphaContentLength : searchChineseContentLength;
        readerPresenter.getBookOperate().searchParam();
        readerPresenter.getBookOperate().searchContent(searchText, contentLength, startPage, SEARCH_PAGE_ONE_TIME * searchRows, new ReaderPresenter.OnSearchContentCallBack() {
            @Override
            public void OnNext(List<ReaderSelection> results, int page) {
                updateSearchingText(page);
                if (results == null || results.size() < 1) {
                    return;
                }

                nextIcon.setEnabled(true);
                searchList.addAll(results);
                mergeSearchList();
                updatePageIndicator(currentPagePosition, pageRecyclerView.getAdapter().getItemCount());
                pageRecyclerView.getAdapter().notifyDataSetChanged();
                if (nextPageAction && pageRecyclerView.getPaginator().hasNextPage()) {
                    nextPageAction = false;
                    pageRecyclerView.nextPage();
                }
            }

            @Override
            public void OnFinishedSearch(int endPage) {
                startPage = endPage;
                nextIcon.setEnabled(true);
                hideLoadingLayout();
                finishSearchTips();
            }
        });
    }

    private void reset() {
        searchList.clear();
        showSearchList.clear();
        nextRequestPage = SEARCH_PAGE_ONE_TIME;
        updatePageIndicator(0, 0);
        pageRecyclerView.setCurrentPage(0);
        startPage = 0;
        readerPresenter.getReaderUserDataInfo().saveSearchResults(null);
    }

    private void mergeSearchList() {
        int maxCount = nextRequestPage * searchRows;
        if (showSearchList.size() < searchList.size()) {
            for (int i = showSearchList.size(); i < maxCount && i < searchList.size(); i++) {
                showSearchList.add(searchList.get(i));
            }
        }
    }

    private void stopSearch() {
        readerPresenter.getBookOperate().stopSearch();
    }

    private void hideSoftInputWindow() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
    }

    private void showSoftInputWindow() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(searchEditText, 0);
    }

    private void gotoSearchPage(final int searchIndex, final BaseCallback callback) {
        if (showSearchList == null || searchIndex >= showSearchList.size() || searchIndex < 0) {
            return;
        }
        ReaderSelection selection = showSearchList.get(searchIndex);
        if (selection == null) {
            return;
        }
        List<ReaderSelection> readerSelections = new ArrayList<>();
        readerSelections.add(selection);
        readerPresenter.getBookOperate().gotoSearchPage(selection.getPagePosition(), readerSelections, new BaseCallback() {
            @Override
            public void done(BaseRequest baseRequest, Throwable throwable) {
                currentSearchIndex = searchIndex;
                if (callback != null) {
                    callback.done(baseRequest, throwable);
                }
            }
        });
    }

    private class SearchViewHolder extends RecyclerView.ViewHolder {

        private TextView contentTextView;
        private TextView contentPage;
        private ReaderSelection readerSelection;

        public SearchViewHolder(View itemView) {
            super(itemView);
            contentTextView = (TextView) itemView.findViewById(R.id.search_content);
            contentPage = (TextView) itemView.findViewById(R.id.search_page);
        }

        public void bindView(ReaderSelection selection, String search, final int position) {
            readerSelection = selection;
            String leftText = StringUtils.deleteNewlineSymbol(StringUtils.leftTrim(selection.getLeftText()));
            String rightText = StringUtils.deleteNewlineSymbol(StringUtils.rightTrim(selection.getRightText()));
            leftText = removeUselessLetters(search, leftText, true);
            rightText = removeUselessLetters(search, rightText, false);
            String content = leftText + search + rightText;
            SpannableStringBuilder style = new SpannableStringBuilder(content);
            int start = leftText.length();
            if (start < 0) {
                return;
            }
            int length = search.length();
            style.setSpan(new BackgroundColorSpan(Color.BLACK), start, start + length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            style.setSpan(new ForegroundColorSpan(Color.WHITE), start, start + length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            contentTextView.setText(style);
            int pageNumber = Integer.valueOf(readerSelection.getPageName());
            String page = String.format(getContext().getString(R.string.page_search), pageNumber + 1);
            contentPage.setText(page);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    stopSearch();
                    gotoSearchPage(position, new BaseCallback() {
                        @Override
                        public void done(BaseRequest request, Throwable e) {
                            showFloatToolBar();
                        }
                    });
                }
            });
        }
    }

    private class HistoryViewHolder extends RecyclerView.ViewHolder {

        private TextView historyTextView;

        public HistoryViewHolder(View itemView) {
            super(itemView);
            historyTextView = (TextView) itemView.findViewById(R.id.search_history);
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

        public void bindView(SearchHistory history) {
            historyTextView.setText(history.getContent());
        }
    }

    private void updatePageIndicator(int position, int itemCount) {
        currentPagePosition = position;
        int page = itemCount / searchRows;
        int currentPage = page > 0 ? position / searchRows + 1 : 1;
        page = Math.max(page, 1);
        String indicator = String.format("%d/%d", currentPage, page);
        String total = String.format(getContext().getString(R.string.total_page), showSearchList.size());
        pageIndicator.setText(indicator);
        totalPage.setText(total);
    }

    private void updateSearchingText(int page) {
        String str = String.format(getContext().getString(R.string.searching), page, ReadPageInfo.getTotalPage(readerPresenter));
        searchingText.setText(str);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        stopSearch();
        EventBus.getDefault().unregister(this);
    }


    private void closeDialog() {
        readerPresenter.getBookOperate().GotoPositionAction(readerPresenter.getCurrentPagePosition(), false);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGotoPositionActionResultEvent(GotoPositionActionResultEvent event) {
        DialogSearch.this.dismiss();
    }

    private void showLoadingLayout() {
        loadingLayout.setVisibility(View.VISIBLE);
    }

    private void hideLoadingLayout() {
        loadingLayout.setVisibility(View.GONE);
    }

    private void showFloatToolBar() {
        floatToolBar.setVisibility(View.VISIBLE);
        searchInputLayout.setVisibility(View.INVISIBLE);
        searchContent.setVisibility(View.GONE);
        searchHistory.setVisibility(View.GONE);
        loadingLayout.setVisibility(View.GONE);
        dividerLine.setVisibility(View.INVISIBLE);
    }

    private void hideFloatToolBar() {
        floatToolBar.setVisibility(View.GONE);
        searchInputLayout.setVisibility(View.VISIBLE);
        dividerLine.setVisibility(View.VISIBLE);
        searchContent.setVisibility(View.VISIBLE);
    }

    private void nextSearch() {
        int currentPage = currentPagePosition / searchRows + 1;
        if (currentPage == nextRequestPage) {
            nextIcon.setEnabled(false);
            nextRequestPage = nextRequestPage + SEARCH_PAGE_ONE_TIME;
            showLoadingLayout();
            nextPageAction = true;
            readerPresenter.getBookOperate().proceedSearch(startPage);
        }
        finishSearchTips();
    }

    private void finishSearchTips() {
        if (startPage >= ReadPageInfo.getTotalPage(readerPresenter)) {
            Toast.makeText(getContext(), getContext().getString(R.string.search_end), Toast.LENGTH_SHORT).show();
        }
    }

    private String removeUselessLetters(String search, String content, boolean first) {
        if (!ReaderTextSplitterImpl.isAlpha(search.charAt(0))) {
            return content;
        }

        if (first) {
            content = StringUtils.leftTrim(content);
            int firstSpaceIndex = content.indexOf(' ');
            if (firstSpaceIndex >= 0) {
                content = content.substring(firstSpaceIndex);
            }
        } else {
            content = StringUtils.rightTrim(content);
            int endSpaceIndex = content.lastIndexOf(' ');
            if (endSpaceIndex >= 0) {
                content = content.substring(0, endSpaceIndex);
            }
        }

        return content;
    }
}
