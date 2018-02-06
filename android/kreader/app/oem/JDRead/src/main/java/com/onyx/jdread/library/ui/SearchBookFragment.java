package com.onyx.jdread.library.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.data.GPaginator;
import com.onyx.android.sdk.data.event.ItemClickEvent;
import com.onyx.android.sdk.data.model.DataModel;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.utils.PreferenceManager;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.JDReadApplication;
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
import com.onyx.jdread.library.view.DashLineItemDivider;
import com.onyx.jdread.main.common.BaseFragment;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.main.common.ToastUtil;
import com.onyx.jdread.reader.common.DocumentInfo;
import com.onyx.jdread.reader.common.OpenBookHelper;
import com.onyx.jdread.shop.action.SearchHotWordAction;
import com.onyx.jdread.shop.model.ShopDataBundle;
import com.onyx.jdread.shop.ui.BookDetailFragment;
import com.onyx.jdread.util.InputUtils;
import com.onyx.jdread.util.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.lang.reflect.Field;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSearchBookBinding.inflate(inflater, container, false);
        initView();
        initData();
        initEvent();
        return binding.getRoot();
    }

    private void initView() {
        binding.hotSearchRecycler.setLayoutManager(new DisableScrollGridManager(getContext().getApplicationContext()));
        hotSearchAdapter = new HotSearchAdapter();
        binding.hotSearchRecycler.setAdapter(hotSearchAdapter);

        binding.searchHintRecycler.setLayoutManager(new DisableScrollGridManager(getContext().getApplicationContext()));
        searchHintAdapter = new SearchHintAdapter();
        binding.searchHintRecycler.setAdapter(searchHintAdapter);

        binding.searchResultRecycler.setLayoutManager(new DisableScrollGridManager(getContext().getApplicationContext()));
        binding.searchResultRecycler.setPageTurningCycled(true);
        binding.searchResultRecycler.addItemDecoration(new DashLineItemDivider());
        searchResultAdapter = new SearchResultAdapter();
        binding.searchResultRecycler.setAdapter(searchResultAdapter);

        binding.searchHistoryRecycler.setLayoutManager(new DisableScrollGridManager(getContext().getApplicationContext()));
        searchHistoryAdapter = new SearchHistoryAdapter();
        binding.searchHistoryRecycler.setAdapter(searchHistoryAdapter);
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
        pagination = binding.searchResultRecycler.getPaginator();
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
        binding.setIndicatorModel(pageIndicatorModel);
    }

    private void initEvent() {
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                queryTextSubmit(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                queryTextChange(newText);
                return false;
            }
        });

        binding.searchResultRecycler.setOnPagingListener(new PageRecyclerView.OnPagingListener() {
            @Override
            public void onPageChange(int position, int itemCount, int pageSize) {
                updatePageIndicator();
            }
        });
    }

    private void queryTextChange(String newText) {
        searchBookModel.searchHint.clear();
        searchHintAdapter.notifyDataSetChanged();
        if (StringUtils.isNotBlank(newText) && InputUtils.getByteCount(newText) > ResManager.getInteger(R.integer.search_word_key_max_length)) {
            ToastUtil.showToast(ResManager.getString(R.string.the_input_has_exceeded_the_upper_limit));
            binding.searchView.setQuery(InputUtils.getEffectiveString(newText, ResManager.getInteger(R.integer.search_word_key_max_length)), false);
            return;
        }
        searchBookModel.isInputting.set(StringUtils.isNotBlank(newText));
        searchBookModel.searchKey.set(newText);
        checkView();
        SearchBookAction searchBookAction = new SearchBookAction(false);
        searchBookAction.execute(LibraryDataBundle.getInstance(), new RxCallback() {
            @Override
            public void onNext(Object o) {
                searchHintAdapter.notifyDataSetChanged();
            }
        });
    }

    private void queryTextSubmit(String query) {
        if (StringUtils.isNullOrEmpty(query)) {
            ToastUtil.showToast(ResManager.getString(R.string.empty_search_key_prompt));
        }
        if (StringUtils.isNotBlank(query) && InputUtils.getByteCount(query) > ResManager.getInteger(R.integer.search_word_key_max_length)) {
            ToastUtil.showToast(ResManager.getString(R.string.the_input_has_exceeded_the_upper_limit));
            return;
        }
        Utils.hideSoftWindow(getActivity());
        searchBookModel.isInputting.set(false);
        searchBookModel.searchKey.set(query);
        checkView();
        SearchBookAction searchBookAction = new SearchBookAction(true);
        searchBookAction.execute(LibraryDataBundle.getInstance(), new RxCallback() {
            @Override
            public void onNext(Object o) {
                searchResultAdapter.notifyDataSetChanged();
                updatePageIndicator();
                loadSearchHistory();
            }
        });
    }

    private void updatePageIndicator() {
        int totalCount = searchBookModel.searchResult.size();
        pagination.resize(searchResultAdapter.getRowCount(), searchResultAdapter.getColumnCount(), totalCount);
        pageIndicatorModel.updateCurrentPage(totalCount);
        pageIndicatorModel.setTotalFormat(getString(R.string.total));
        pageIndicatorModel.updateTotal(totalCount);
    }

    private void checkView() {
        binding.searchHotHistoryLayout.setVisibility(StringUtils.isNullOrEmpty(searchBookModel.searchKey.get()) ? View.VISIBLE : View.GONE);
        binding.searchHintLayout.setVisibility(searchBookModel.showHintList() ? View.VISIBLE : View.GONE);
        binding.searchResultLayout.setVisibility(searchBookModel.showResult() ? View.VISIBLE : View.GONE);
        updatePageIndicator();
    }

    private void initData() {
        searchBookModel = LibraryDataBundle.getInstance().getSearchBookModel();
        binding.setSearchModel(searchBookModel);
        loadHotSearchKey();
        loadSearchHistory();
        checkView();
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
                hotSearchAdapter.setSearchHotWords(searchHotWordAction.getHotWords());
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        getEventBus().unregister(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        getEventBus().register(this);
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
        binding.searchView.setQuery(event.getSearchKey(), true);
    }

    @Subscribe
    public void onSubmitSearchBookEvent(SubmitSearchBookEvent event) {
        CharSequence query = binding.searchView.getQuery();
        if (!TextUtils.isEmpty(query)) {
            binding.searchView.setQuery(query, true);
        }
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
        OpenBookHelper.openBook(getContext(), documentInfo);
    }

    private void gotoBookDetail(DataModel model) {
        PreferenceManager.setLongValue(JDReadApplication.getInstance(), Constants.SP_KEY_BOOK_ID, model.cloudId.get());
        if (getViewEventCallBack() != null) {
            getViewEventCallBack().gotoView(BookDetailFragment.class.getName());
        }
    }
}
