package com.onyx.jdread.library.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.FragmentSearchBookBinding;
import com.onyx.jdread.library.action.SearchBookAction;
import com.onyx.jdread.library.adapter.HotSearchAdapter;
import com.onyx.jdread.library.adapter.SearchHintAdapter;
import com.onyx.jdread.library.adapter.SearchResultAdapter;
import com.onyx.jdread.library.event.BackToLibraryFragmentEvent;
import com.onyx.jdread.library.event.SearchBookKeyEvent;
import com.onyx.jdread.library.event.SubmitSearchBookEvent;
import com.onyx.jdread.library.model.LibraryDataBundle;
import com.onyx.jdread.library.model.SearchBookModel;
import com.onyx.jdread.library.view.DashLineItemDivider;
import com.onyx.jdread.main.common.BaseFragment;
import com.onyx.jdread.shop.action.SearchHotWordAction;
import com.onyx.jdread.shop.model.ShopDataBundle;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by hehai on 18-1-17.
 */

public class SearchBookFragment extends BaseFragment {

    private SearchBookModel searchBookModel;
    private FragmentSearchBookBinding binding;
    private HotSearchAdapter hotSearchAdapter;
    private SearchHintAdapter searchHintAdapter;
    private SearchResultAdapter searchResultAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSearchBookBinding.inflate(inflater, container, false);
        initView();
        initData(getBundle());
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
        binding.searchResultRecycler.addItemDecoration(new DashLineItemDivider());
        searchResultAdapter = new SearchResultAdapter();
        binding.searchResultRecycler.setAdapter(searchResultAdapter);
    }

    private void initEvent() {
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchBookModel.isInputting.set(false);
                searchBookModel.searchKey.set(query);
                checkView();
                SearchBookAction searchBookAction = new SearchBookAction(true);
                searchBookAction.execute(LibraryDataBundle.getInstance(), new RxCallback() {
                    @Override
                    public void onNext(Object o) {
                        searchResultAdapter.notifyDataSetChanged();
                    }
                });
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
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
                return false;
            }
        });
    }

    private void checkView() {
        binding.searchHotHistoryLayout.setVisibility(StringUtils.isNullOrEmpty(searchBookModel.searchKey.get()) ? View.VISIBLE : View.GONE);
        binding.searchHintLayout.setVisibility(searchBookModel.showHintList() ? View.VISIBLE : View.GONE);
        binding.searchResultLayout.setVisibility(searchBookModel.showResult() ? View.VISIBLE : View.GONE);
    }

    private void initData(Bundle savedInstanceState) {
        searchBookModel = LibraryDataBundle.getInstance().getSearchBookModel();
        binding.setSearchModel(searchBookModel);
        if (savedInstanceState != null) {
            String searchKey = savedInstanceState.getString(getString(R.string.search_name_key));
            if (StringUtils.isNotBlank(searchKey)) {
                binding.searchView.setQuery(searchKey, true);
            }
        }
        loadHotSearchKey();
        loadSearchHistory();
    }

    private void loadSearchHistory() {

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
    }

    public EventBus getEventBus() {
        return LibraryDataBundle.getInstance().getEventBus();
    }

    @Subscribe
    public void onBackToLibraryFragmentEvent(BackToLibraryFragmentEvent event) {
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
}
