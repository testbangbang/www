package com.onyx.jdread.library.ui;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jingdong.app.reader.data.DrmTools;
import com.onyx.android.sdk.data.GPaginator;
import com.onyx.android.sdk.data.event.ItemClickEvent;
import com.onyx.android.sdk.data.model.DataModel;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.FragmentSearchBookBinding;
import com.onyx.jdread.library.action.ClearSearchHistoryAction;
import com.onyx.jdread.library.action.LoadSearchHistoryAction;
import com.onyx.jdread.library.action.SearchBookAction;
import com.onyx.jdread.library.adapter.HotSearchAdapter;
import com.onyx.jdread.library.adapter.SearchHintAdapter;
import com.onyx.jdread.library.adapter.SearchHistoryAdapter;
import com.onyx.jdread.library.adapter.SearchResultAdapter;
import com.onyx.jdread.library.event.BackToLibraryFragmentEvent;
import com.onyx.jdread.library.event.ClearSearchHistoryEvent;
import com.onyx.jdread.library.event.SearchBookKeyEvent;
import com.onyx.jdread.library.event.SubmitSearchBookEvent;
import com.onyx.jdread.library.model.LibraryDataBundle;
import com.onyx.jdread.library.model.PageIndicatorModel;
import com.onyx.jdread.library.model.SearchBookModel;
import com.onyx.jdread.library.view.CustomSearchView;
import com.onyx.jdread.library.view.DashLineItemDivider;
import com.onyx.jdread.main.common.BaseFragment;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.main.common.ToastUtil;
import com.onyx.jdread.main.event.KeyCodeEnterEvent;
import com.onyx.jdread.reader.common.DocumentInfo;
import com.onyx.jdread.reader.common.OpenBookHelper;
import com.onyx.jdread.shop.action.SearchBookListAction;
import com.onyx.jdread.shop.action.SearchHotWordAction;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.event.HideAllDialogEvent;
import com.onyx.jdread.shop.event.LoadingDialogEvent;
import com.onyx.jdread.shop.model.ShopDataBundle;
import com.onyx.jdread.shop.ui.BookDetailFragment;
import com.onyx.jdread.util.InputUtils;
import com.onyx.jdread.util.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;

import static com.onyx.jdread.shop.common.CloudApiContext.CategoryLevel2BookList.PAGE_SIZE_DEFAULT_VALUES;

/**
 * Created by hehai on 18-1-17.
 */

public class SearchBookFragment extends BaseFragment {

    private SearchBookModel searchBookModel;
    private FragmentSearchBookBinding binding;
    private HotSearchAdapter hotSearchAdapter;
    private SearchHintAdapter searchHintAdapter;
    private SearchResultAdapter searchResultAdapter;
    private PageIndicatorModel pageIndicatorModel;
    private GPaginator pagination;
    private SearchHistoryAdapter searchHistoryAdapter;

    private boolean isBookSearching = false;
    private String lastString;
    private String hintQueryPageSize = PAGE_SIZE_DEFAULT_VALUES;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSearchBookBinding.inflate(inflater, container, false);
        initConfig();
        initView();
        initData();
        initEvent();
        return binding.getRoot();
    }

    private void initConfig() {
        hintQueryPageSize = String.valueOf(ResManager.getInteger(R.integer.hint_search_size_per_page));
    }

    private void initView() {
        binding.hotSearchRecycler.setLayoutManager(new DisableScrollGridManager(getContext().getApplicationContext()));
        hotSearchAdapter = new HotSearchAdapter();
        binding.hotSearchRecycler.setAdapter(hotSearchAdapter);

        binding.searchHintRecycler.setLayoutManager(new DisableScrollGridManager(getContext().getApplicationContext()));
        searchHintAdapter = new SearchHintAdapter();
        binding.searchHintRecycler.setAdapter(searchHintAdapter);
        binding.searchHintRecycler.setCanTouchPageTurning(false);

        binding.searchResultRecycler.setLayoutManager(new DisableScrollGridManager(getContext().getApplicationContext()));
        binding.searchResultRecycler.setPageTurningCycled(true);
        binding.searchResultRecycler.addItemDecoration(new DashLineItemDivider());
        searchResultAdapter = new SearchResultAdapter();
        binding.searchResultRecycler.setAdapter(searchResultAdapter);

        binding.searchHistoryRecycler.setLayoutManager(new DisableScrollGridManager(getContext().getApplicationContext()));
        searchHistoryAdapter = new SearchHistoryAdapter();
        binding.searchHistoryRecycler.setAdapter(searchHistoryAdapter);
        binding.searchView.setMaxByte(ResManager.getInteger(R.integer.search_word_key_max_length));
        initPageIndicator();
        hideSearchViewLine();
    }

    private void hideSearchViewLine() {
        if (binding.searchView != null) {
            try {
                Class<?> argClass = binding.searchView.getClass();
                Field ownField = argClass.getDeclaredField("mSearchPlate");
                ownField.setAccessible(true);
                View mView = (View) ownField.get(binding.searchView);
                mView.setBackgroundColor(Color.TRANSPARENT);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void initPageIndicator() {
        pageIndicatorModel = initPageIndicatorModel(binding.searchResultRecycler.getPaginator());
        pagination = pageIndicatorModel.getPaginator();
        binding.searchResultRecycler.setPaginator(pagination);
        binding.setIndicatorModel(pageIndicatorModel);
    }

    private void clearPageIndicator() {
        pagination.resize(pagination.getRows(), pagination.getColumns(), 0);
        pagination.setCurrentPage(0);
        pageIndicatorModel.updateCurrentPage(0);
    }

    private void updatePageIndicator() {
        int totalCount = searchBookModel.searchResult.size();
        pagination.resize(searchResultAdapter.getRowCount(), searchResultAdapter.getColumnCount(), totalCount);
        pageIndicatorModel.updateCurrentPage(totalCount);
        pageIndicatorModel.setTotalFormat(getString(R.string.total));
        pageIndicatorModel.updateTotal(totalCount);
    }

    private void initEvent() {
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                queryTextSubmit(getSearchQueryOrHint(query));
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                queryTextChange(newText);
                return false;
            }
        });
        binding.searchView.setCustomSearchListener(new CustomSearchView.SearchListener() {
            @Override
            public void onQuerySearch(String query) {
                if (StringUtils.isNullOrEmpty(query)) {
                    doSearchQueryOrHint(query);
                    return;
                }
                queryTextSubmit(getSearchQueryOrHint(query));
            }
        });
        binding.searchResultRecycler.setOnPagingListener(new PageRecyclerView.OnPagingListener() {
            @Override
            public void onPageChange(int position, int itemCount, int pageSize) {
                updatePageIndicator();
            }
        });
    }

    private boolean isEmptySearchResults() {
        return CollectionUtils.isNullOrEmpty(getSearchBookModel().searchResult);
    }

    private boolean isShowSearchResult() {
        return getSearchBookModel().showResult() && !isEmptySearchResults();
    }

    private boolean checkSubmitQueryValid(String query) {
        if (StringUtils.isNullOrEmpty(query)) {
            ToastUtil.showToast(ResManager.getString(R.string.empty_search_key_prompt));
            return false;
        }
        if (StringUtils.isNotBlank(query) && InputUtils.getByteCount(query) > ResManager.getInteger(R.integer.search_word_key_max_length)) {
            ToastUtil.showToast(ResManager.getString(R.string.the_input_has_exceeded_the_upper_limit));
            return false;
        }
        if (!InputUtils.isHaveAvailableCharacters(query)) {
            checkResultView(false);
            return false;
        }
        return true;
    }

    private void queryTextChangeImpl(String newText) {
        searchBookModel.searchHint.clear();
        searchHintAdapter.notifyDataSetChanged();
        searchBookModel.isInputting.set(StringUtils.isNotBlank(newText));
        searchBookModel.searchKey.set(newText);
        checkView();
        searchBook(false, null);
    }

    private void queryTextChange(String newText) {
        if (StringUtils.isNullOrEmpty(newText)) {
            queryTextChangeImpl(newText);
            return;
        }
        if (isShowSearchResult()) {
            return;
        }
        queryTextChangeImpl(newText);
    }

    private void queryTextSubmit(String query) {
        if (isBookSearching()) {
            return;
        }
        if (!checkSubmitQueryValid(query)) {
            return;
        }
        Utils.hideSoftWindow(getActivity());
        binding.searchView.clearFocus();
        searchBookModel.isInputting.set(false);
        searchBookModel.searchKey.set(query);
        checkView();
        setBookSearching(true);
        clearPageIndicator();
        searchBook(true, new RxCallback() {
            @Override
            public void onNext(Object o) {
                gotoPage(getValidPage());
                updatePageIndicator();
                loadSearchHistory();
            }

            @Override
            public void onFinally() {
                setBookSearching(false);
                checkSearchResult();
            }
        });
    }

    private void checkSearchResult() {
        boolean empty = isEmptySearchResults();
        checkResultView(!empty);
        if (empty) {
            checkWifi(getSearchBookModel().searchKey.get());
        }
    }

    private String getSearchQueryOrHint(CharSequence query) {
        if (TextUtils.isEmpty(query)) {
            CharSequence hint = binding.searchView.getQueryHint();
            if (hint != null && !ResManager.getString(R.string.search_view_hint).equals(hint.toString())) {
                query = hint.toString();
            }
        }
        return query == null ? null : query.toString();
    }

    private void doSearchQueryOrHint(CharSequence query) {
        query = getSearchQueryOrHint(query);
        if (TextUtils.isEmpty(query)) {
            ToastUtil.showToast(R.string.empty_search_key_prompt);
            return;
        }
        binding.searchView.setQuery(query, true);
    }

    private void searchBook(final boolean submit, final RxCallback callback) {
        searchBookFromLocal(submit, new RxCallback() {
            @Override
            public void onNext(Object o) {
                searchBookFromCloud(submit, callback);
            }
        });
    }

    private void searchBookFromLocal(final boolean submit, final RxCallback rxCallback) {
        SearchBookAction searchBookAction = new SearchBookAction(submit);
        searchBookAction.execute(LibraryDataBundle.getInstance(), new RxCallback() {
            @Override
            public void onNext(Object o) {
                if (submit) {
                    searchResultAdapter.notifyDataSetChanged();
                } else {
                    searchHintAdapter.notifyDataSetChanged();
                }
                RxCallback.invokeNext(rxCallback, o);
            }
        });
    }

    private void searchBookFromCloud(final boolean submit, final RxCallback callback) {
        SearchBookListAction booksAction = new SearchBookListAction("", 1,
                CloudApiContext.CategoryLevel2BookList.SORT_KEY_DEFAULT_VALUES,
                CloudApiContext.CategoryLevel2BookList.SORT_TYPE_DEFAULT_VALUES,
                searchBookModel.searchKey.get(), CloudApiContext.SearchBook.FILTER_DEFAULT);
        booksAction.setPageSize(getPageSize(submit));
        booksAction.setMapToDataModel(true);
        booksAction.setLoadCover(submit);
        booksAction.execute(ShopDataBundle.getInstance(), new RxCallback<SearchBookListAction>() {
            @Override
            public void onNext(SearchBookListAction action) {
                if (submit) {
                    LibraryDataBundle.getInstance().getSearchBookModel().searchResult.addAll(action.getDataModelList());
                    searchResultAdapter.notifyDataSetChanged();
                } else {
                    LibraryDataBundle.getInstance().getSearchBookModel().searchHint.addAll(action.getDataModelList());
                    searchHintAdapter.notifyDataSetChanged();
                }
                RxCallback.invokeNext(callback, action);
            }

            @Override
            public void onFinally() {
                invokeFinally(callback);
            }
        });
    }

    private String getPageSize(boolean submit) {
        if (submit) {
            return PAGE_SIZE_DEFAULT_VALUES;
        }
        return hintQueryPageSize;
    }

    private boolean isBookSearching() {
        return isBookSearching;
    }

    private void setBookSearching(boolean searching) {
        this.isBookSearching = searching;
    }

    private void gotoPage(int page) {
        if (binding == null || binding.searchResultRecycler == null) {
            return;
        }
        binding.searchResultRecycler.gotoPage(page);
    }

    private void checkView() {
        binding.searchHotHistoryLayout.setVisibility(StringUtils.isNullOrEmpty(searchBookModel.searchKey.get()) ? View.VISIBLE : View.GONE);
        binding.searchHintLayout.setVisibility(searchBookModel.showHintList() ? View.VISIBLE : View.GONE);
        binding.searchResultLayout.setVisibility(searchBookModel.showResult() ? View.VISIBLE : View.GONE);
        binding.emptyResultLayout.setVisibility(View.GONE);
        updatePageIndicator();
    }

    private void checkResultView(boolean show) {
        binding.searchResultLayout.setVisibility(show ? View.VISIBLE : View.GONE);
        binding.emptyResultLayout.setVisibility(!show ? View.VISIBLE : View.GONE);
    }

    private void initData() {
        LibraryDataBundle.getInstance().setSearchBookModel(getSearchBookModel());
        binding.setSearchModel(searchBookModel);
        loadHotSearchKey();
        loadSearchHistory();
        checkView();
        updateHotSearchView(searchBookModel.getHotWords(), searchBookModel.getDefaultHotWord());
        restorePreData();
    }

    private void restorePreData() {
        if (isShowSearchResult()) {
            gotoPage(getValidPage());
        }
    }

    private void loadSearchHistory() {
        LoadSearchHistoryAction historyAction = new LoadSearchHistoryAction(ResManager.getInteger(R.integer.history_limit));
        historyAction.execute(LibraryDataBundle.getInstance(), new RxCallback() {
            @Override
            public void onNext(Object o) {
                searchHistoryAdapter.setSearchHistories(LibraryDataBundle.getInstance().getSearchBookModel().searchHistory);
            }
        });
    }

    private void loadHotSearchKey() {
        final SearchHotWordAction searchHotWordAction = new SearchHotWordAction();
        searchHotWordAction.execute(ShopDataBundle.getInstance(), new RxCallback() {
            @Override
            public void onNext(Object o) {
                updateHotSearchResult(searchHotWordAction.getHotWords(), searchHotWordAction.getDefaultKeyWord());
            }
        });
    }

    private void updateHotSearchResult(List<String> hotWords, String defaultKeyword) {
        searchBookModel.reAddHotWords(hotWords);
        if (StringUtils.isNotBlank(defaultKeyword)) {
            searchBookModel.setDefaultHotWord(defaultKeyword);
        }
        updateHotSearchView(hotWords, defaultKeyword);
    }

    private void updateHotSearchView(List<String> hotWords, String defaultKeyword) {
        if (!CollectionUtils.isNullOrEmpty(hotWords)) {
            hotSearchAdapter.setSearchHotWords(hotWords);
        }
        if (!TextUtils.isEmpty(defaultKeyword)) {
            binding.searchView.setQueryHint(defaultKeyword);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hideLoadingDialog();
    }

    @Override
    public void onStop() {
        super.onStop();
        getEventBus().unregister(this);
        ShopDataBundle.getInstance().getEventBus().unregister(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        getEventBus().register(this);
        ShopDataBundle.getInstance().getEventBus().register(this);
        if (getBundle() != null) {
            String searchKey = getBundle().getString(getString(R.string.search_name_key));
            if (StringUtils.isNotBlank(searchKey)) {
                binding.searchView.setQuery(searchKey, true);
            }
        }
    }

    public EventBus getEventBus() {
        return LibraryDataBundle.getInstance().getEventBus();
    }

    @Subscribe
    public void onBackToLibraryFragmentEvent(BackToLibraryFragmentEvent event) {
        binding.searchView.setQuery("", false);
        viewEventCallBack.viewBack();
    }

    @Subscribe
    public void onSearchBookKeyEvent(SearchBookKeyEvent event) {
        doSearchQueryOrHint(event.getSearchKey());
    }

    @Subscribe
    public void onKeyCodeEnterEvent(KeyCodeEnterEvent event) {
        doSearchQueryOrHint(binding.searchView.getQuery());
    }

    @Subscribe
    public void onSubmitSearchBookEvent(SubmitSearchBookEvent event) {
        doSearchQueryOrHint(binding.searchView.getQuery());
    }

    @Subscribe
    public void onClearSearchHistoryEvent(ClearSearchHistoryEvent event) {
        ClearSearchHistoryAction action = new ClearSearchHistoryAction();
        action.execute(LibraryDataBundle.getInstance(), new RxCallback() {
            @Override
            public void onNext(Object o) {

            }
        });
    }

    @Subscribe
    public void onItemClickEvent(ItemClickEvent event) {
        DataModel model = event.getModel();
        if (StringUtils.isNullOrEmpty(model.absolutePath.get())) {
            gotoBookDetail(model);
        } else {
            openBook(model);
        }
    }

    @Subscribe
    public void onLoadingDialogEvent(LoadingDialogEvent event) {
        showLoadingDialog(getString(event.getResId()));
    }

    @Subscribe
    public void onHideAllDialogEvent(HideAllDialogEvent event) {
        hideLoadingDialog();
    }

    private void openBook(DataModel model) {
        String filePath = model.absolutePath.get();
        if (StringUtils.isNullOrEmpty(filePath)) {
            return;
        }
        File file = new File(filePath);
        if (!file.exists()) {
            return;
        }
        DocumentInfo documentInfo = new DocumentInfo();
        documentInfo.setBookPath(filePath);
        documentInfo.setBookName(model.title.get());
        DocumentInfo.SecurityInfo securityInfo = new DocumentInfo.SecurityInfo();
        securityInfo.setKey(model.key.get());
        securityInfo.setRandom(model.random.get());
        securityInfo.setUuId(DrmTools.getHardwareId(Build.SERIAL));
        documentInfo.setSecurityInfo(securityInfo);
        documentInfo.setWholeBookDownLoad(model.isWholeBookDownLoad.get());
        if(StringUtils.isNotBlank(model.cloudId.get())) {
            documentInfo.setCloudId(Integer.parseInt(model.cloudId.get()));
        }
        OpenBookHelper.openBook(getContext(), documentInfo);
    }

    private void gotoBookDetail(DataModel model) {
        if (getViewEventCallBack() != null) {
            Bundle bundle = new Bundle();
            bundle.putLong(Constants.SP_KEY_BOOK_ID, Long.valueOf(model.cloudId.get()));
            getViewEventCallBack().gotoView(BookDetailFragment.class.getName(), bundle);
        }
    }

    private SearchBookModel getSearchBookModel() {
        if (searchBookModel == null) {
            searchBookModel = new SearchBookModel(LibraryDataBundle.getInstance().getEventBus());
            updateHotSearchResult(LibraryDataBundle.getInstance().getSearchBookModel().getHotWords(),
                    LibraryDataBundle.getInstance().getSearchBookModel().getDefaultHotWord());
        }
        return searchBookModel;
    }

    private PageIndicatorModel initPageIndicatorModel(GPaginator pagination) {
        if (pageIndicatorModel == null) {
            pageIndicatorModel = new PageIndicatorModel(pagination, new PageIndicatorModel.PageChangedListener() {
                @Override
                public void prev() {
                }

                @Override
                public void next() {
                }

                @Override
                public void gotoPage(int currentPage) {
                }

                @Override
                public void onRefresh() {
                }
            });
        }
        return pageIndicatorModel;
    }

    private int getValidPage() {
        return pagination.getCurrentPage();
    }
}
