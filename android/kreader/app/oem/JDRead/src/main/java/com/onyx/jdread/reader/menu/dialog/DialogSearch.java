package com.onyx.jdread.reader.menu.dialog;

import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.onyx.android.sdk.common.request.WakeLockHolder;
import com.onyx.android.sdk.data.model.SearchHistory;
import com.onyx.android.sdk.reader.api.ReaderSelection;
import com.onyx.android.sdk.reader.host.impl.ReaderTextSplitterImpl;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.ui.dialog.OnyxBaseDialog;
import com.onyx.android.sdk.ui.view.OnyxCustomEditText;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.DialogSearchBinding;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.main.common.ToastUtil;
import com.onyx.jdread.reader.actions.GotoPositionAction;
import com.onyx.jdread.reader.common.ToastMessage;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.dialog.DialogSearchViewCallBack;
import com.onyx.jdread.reader.epd.ReaderEpdHelper;
import com.onyx.jdread.reader.event.UpdateViewPageEvent;
import com.onyx.jdread.reader.menu.actions.GetSearchHistoryAction;
import com.onyx.jdread.reader.menu.actions.GotoSearchPageAction;
import com.onyx.jdread.reader.menu.actions.SearchContentAction;
import com.onyx.jdread.reader.menu.event.DialogSearchHandler;
import com.onyx.jdread.reader.menu.model.DialogSearchModel;

import java.util.ArrayList;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * Created by huxiaomao on 16/7/21.
 */
public class DialogSearch extends OnyxBaseDialog implements DialogSearchViewCallBack {

    private static final String TAG = DialogSearch.class.getSimpleName();
    private static final int SEARCH_HISTORY_COUNT = 10;
    private static final int SEARCH_PAGE_ONE_TIME = 20;
    private DialogSearchBinding binding;
    private DialogSearchModel dialogSearchModel;

    private ReaderDataHolder readerDataHolder;
    private SearchContentAction searchContentAction;
    private int currentPagePosition = 0;
    private int nextRequestPage = SEARCH_PAGE_ONE_TIME;
    private int startPage = 0;
    private boolean nextPageAction = false;

    private List<ReaderSelection> searchList = new ArrayList<>();
    private List<ReaderSelection> showSearchList = new ArrayList<>();
    private List<SearchHistory> historyList = new ArrayList<>();
    private String searchText;
    private int searchRows = 0;
    private int searchChineseContentLength = 0;
    private int searchAlphaContentLength = 0;
    private int currentSearchIndex = 0;
    private DialogSearchHandler dialogSearchHandler;
    private WakeLockHolder wakeLockHolder;

    public DialogSearch(final Context context, final ReaderDataHolder readerDataHolder) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        
        this.readerDataHolder = readerDataHolder;
        dialogSearchHandler = new DialogSearchHandler(this, readerDataHolder.getEventBus());
        searchRows = getContext().getResources().getInteger(R.integer.search_row);
        searchChineseContentLength = getContext().getResources().getInteger(R.integer.search_chinese_content_length);
        searchAlphaContentLength = getContext().getResources().getInteger(R.integer.search_alpha_content_length);
        wakeLockHolder = new WakeLockHolder();
        wakeLockHolder.acquireWakeLock(context,TAG);
    }

    @Override
    public Dialog getContent() {
        return this;
    }

    public ReaderDataHolder getReaderDataHolder() {
        return readerDataHolder;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerListener();
        initView();
        initData();
    }

    private void registerListener() {
        dialogSearchHandler.registerListener();
    }

    private void initView() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_search, null, false);
        setContentView(binding.getRoot());

        dialogSearchModel = new DialogSearchModel(readerDataHolder.getEventBus());
        binding.setDialogSearchModel(dialogSearchModel);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        init();
    }

    private void initData() {

    }

    private void init() {
        binding.deleteHistory.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        binding.closeHistory.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        binding.searchEditLayout.post(new Runnable() {
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
        binding.editViewSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopSearch();
                loadSearchHistoryData();
                showSoftInputWindow();
            }
        });

        binding.editViewSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    hideSoftInputWindow();
                    loadSearchData();
                }
                return true;
            }
        });

        binding.editViewSearch.setOnKeyPreImeListener(new OnyxCustomEditText.onKeyPreImeListener() {
            @Override
            public void onKeyPreIme(int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dialogSearchModel.setSearchHistory(false);
                }
            }
        });
        binding.editViewSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                if(StringUtils.isNotBlank(text)){
                    dialogSearchModel.setDeleteInputWord(true);
                }else{
                    dialogSearchModel.setDeleteInputWord(false);
                }
            }
        });
        binding.editViewSearch.requestFocus();
    }

    private void showSearchHistoryView() {
        showSoftInputWindow();
        int left = binding.searchEditLayout.getLeft();
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(binding.searchEditLayout.getMeasuredWidth(), ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(left, 0, 0, 0);
        binding.searchHistoryLayout.setLayoutParams(lp);
        initSearchHistoryList();
    }

    private void initPageRecyclerView() {
        binding.searchRecyclerView.setPageTurningCycled(true);
        binding.searchRecyclerView.setAdapter(new PageRecyclerView.PageAdapter() {
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
        binding.searchRecyclerView.setOnPagingListener(new PageRecyclerView.OnPagingListener() {
            @Override
            public void onPageChange(int position, int itemCount, int pageSize) {
                updatePageIndicator(position, itemCount);
            }
        });
        updatePageIndicator(0, 0);
    }

    private void initSearchHistoryList() {
        binding.searchHistoryList.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.searchHistoryList.setAdapter(new RecyclerView.Adapter() {
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
        new GetSearchHistoryAction(SEARCH_HISTORY_COUNT).execute(readerDataHolder, new RxCallback() {
            @Override
            public void onNext(Object o) {

            }
        });
    }

    private void loadSearchData() {
        searchText = binding.editViewSearch.getText().toString();
        if (StringUtils.isNullOrEmpty(searchText)) {
            ToastMessage.showMessageCenter(readerDataHolder.getAppContext(), ResManager.getString(R.string.search_view_hint));
            return;
        }
        dialogSearchModel.setSearchHistory(false);
        dialogSearchModel.setSearchContent(true);
        dialogSearchModel.setTotalPageShow(false);
        stopSearch();

        showLoadingLayout();
        reset();
        int contentLength = ReaderTextSplitterImpl.isAlpha(searchText.charAt(0)) ? searchAlphaContentLength : searchChineseContentLength;
        searchContentAction = new SearchContentAction(searchText, contentLength, startPage, SEARCH_PAGE_ONE_TIME * searchRows);
        searchContentAction.setOnSearchContentCallBack(contentCallBack);
        searchContentAction.execute(readerDataHolder, new RxCallback() {
            @Override
            public void onNext(Object o) {

            }
        });
    }

    private SearchContentAction.OnSearchContentCallBack contentCallBack = new SearchContentAction.OnSearchContentCallBack() {
        @Override
        public void OnNext(final List<ReaderSelection> results, int page) {
            updateSearchingText(page);
            if (results == null || results.size() < 1) {
                return;
            }

            searchList.addAll(results);
            mergeSearchList();
            dialogSearchModel.setTotalPageShow(true);
            updatePageIndicator(currentPagePosition, binding.searchRecyclerView.getAdapter().getItemCount());
            binding.searchRecyclerView.getAdapter().notifyDataSetChanged();
            if (nextPageAction && binding.searchRecyclerView.getPaginator().hasNextPage()) {
                nextPageAction = false;
                binding.searchRecyclerView.nextPage();
            }
        }

        @Override
        public void OnFinishedSearch(int endPage) {
            startPage = endPage;
            hideLoadingLayout();
            ReaderEpdHelper.applyGCUpdate(binding.searchRecyclerView);
            finishSearchTips();
        }
    };

    private void reset() {
        searchList.clear();
        showSearchList.clear();
        nextRequestPage = SEARCH_PAGE_ONE_TIME;
        updatePageIndicator(0, 0);
        binding.searchRecyclerView.setCurrentPage(0);
        startPage = 0;
        readerDataHolder.getReaderUserDataInfo().saveSearchResults(null);
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
        if (searchContentAction != null) {
            searchContentAction.stopSearch();
        }
    }

    private void hideSoftInputWindow() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(binding.editViewSearch.getWindowToken(), 0);
    }

    private void showSoftInputWindow() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(binding.editViewSearch, 0);
    }

    private void gotoSearchPage(final int searchIndex, final RxCallback callback) {
        if (showSearchList == null || searchIndex >= showSearchList.size() || searchIndex < 0) {
            return;
        }
        ReaderSelection selection = showSearchList.get(searchIndex);
        if (selection == null) {
            return;
        }
        List<ReaderSelection> readerSelections = new ArrayList<>();
        readerSelections.add(selection);
        new GotoSearchPageAction(selection.getPagePosition(), readerSelections).execute(readerDataHolder, new RxCallback() {
            @Override
            public void onNext(Object o) {
                currentSearchIndex = searchIndex;
                if (callback != null) {
                    callback.onNext(o);
                }
            }
        });
    }

    private class SearchViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView contentTextView;
        private TextView contentPage;
        private ReaderSelection readerSelection;

        public SearchViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.search_title);
            contentTextView = (TextView) itemView.findViewById(R.id.search_content);
            contentPage = (TextView) itemView.findViewById(R.id.search_page);
        }

        public void bindView(ReaderSelection selection, String search, final int position) {
            readerSelection = selection;
            String leftText = StringUtils.deleteNewlineSymbol(StringUtils.leftTrim(selection.getLeftText()));
            String rightText = StringUtils.deleteNewlineSymbol(StringUtils.rightTrim(selection.getRightText()));
            leftText = removeUselessLetters(search, leftText, true);
            rightText = removeUselessLetters(search, rightText, false);
            String content = leftText + selection.getText() + rightText;
            SpannableStringBuilder style = new SpannableStringBuilder(content);
            int start = leftText.length();
            if (start < 0) {
                return;
            }
            int length = search.length();
            style.setSpan(new BackgroundColorSpan(Color.BLACK), start, start + length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            style.setSpan(new ForegroundColorSpan(Color.WHITE), start, start + length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            contentTextView.setText(style);
            title.setText(selection.chapterName);
            int pageNumber = Integer.valueOf(readerSelection.getPageName());
            String page = String.format(getContext().getString(R.string.page), pageNumber + 1);
            contentPage.setText(page);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) contentPage.getLayoutParams();
            params.height = MATCH_PARENT;
            contentPage.setLayoutParams(params);
            contentPage.setGravity(Gravity.RIGHT | Gravity.CENTER);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    stopSearch();
                    gotoSearchPage(position, new RxCallback() {
                        @Override
                        public void onNext(Object o) {
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
                    dialogSearchModel.setSearchHistory(false);
                    binding.editViewSearch.setText(historyTextView.getText());
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
        dialogSearchModel.setPageIndicator(indicator);
        dialogSearchModel.setTotalPage(total);
    }

    private void updateSearchingText(int page) {
        String str = String.format(getContext().getString(R.string.searching), page, readerDataHolder.getPageCount());
        dialogSearchModel.setTextViewMessage(str);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        hideSoftInputWindow();
        stopSearch();
        dialogSearchHandler.unregisterListener();
        readerDataHolder.getEventBus().post(new UpdateViewPageEvent());
        wakeLockHolder.releaseWakeLock();
    }


    private void closeDialog() {
        GotoPositionAction action = new GotoPositionAction(readerDataHolder.getCurrentPagePosition());
        action.execute(readerDataHolder, new RxCallback() {
            @Override
            public void onNext(Object o) {
                DialogSearch.this.dismiss();
            }
        });
    }

    private void showLoadingLayout() {
        dialogSearchModel.setLoadingLayout(true);
    }

    private void hideLoadingLayout() {
        dialogSearchModel.setLoadingLayout(false);
    }

    private void showFloatToolBar() {
        dialogSearchModel.setFloatToolBar(true);
        dialogSearchModel.setSearchInputLayout(false);
        dialogSearchModel.setSearchContent(false);
        dialogSearchModel.setSearchHistory(false);
        dialogSearchModel.setLoadingLayout(false);
        dialogSearchModel.setDividerLine(false);
    }

    private void hideFloatToolBar() {
        dialogSearchModel.setFloatToolBar(false);
        dialogSearchModel.setSearchInputLayout(true);
        dialogSearchModel.setDividerLine(true);
        dialogSearchModel.setSearchContent(true);
    }

    private void nextSearch() {
        int currentPage = currentPagePosition / searchRows + 1;
        if (currentPage == nextRequestPage) {
            nextRequestPage = nextRequestPage + SEARCH_PAGE_ONE_TIME;
            showLoadingLayout();
            nextPageAction = true;
            searchContentAction.proceedSearch(readerDataHolder, startPage);
        }
        finishSearchTips();
    }

    private void finishSearchTips() {
        if (startPage >= readerDataHolder.getPageCount()) {
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

    @Override
    public void nextSearchResult() {
        if (currentSearchIndex == (showSearchList.size() - 1)) {
            Toast.makeText(getContext(), getContext().getString(R.string.no_more_search_results), Toast.LENGTH_SHORT).show();
            return;
        }
        gotoSearchPage(currentSearchIndex + 1, null);
    }

    @Override
    public void preSearchResult() {
        if (currentSearchIndex == 0) {
            Toast.makeText(getContext(), getContext().getString(R.string.no_more_search_results), Toast.LENGTH_SHORT).show();
            return;
        }
        gotoSearchPage(currentSearchIndex - 1, null);
    }

    @Override
    public void searchBack() {
        hideFloatToolBar();
        binding.searchRecyclerView.gotoPageByIndex(currentSearchIndex);
    }

    @Override
    public void searchData() {
        hideSoftInputWindow();
        loadSearchData();
    }

    @Override
    public void deleteInputWord() {
        binding.editViewSearch.setText("");
        reset();
    }
}
