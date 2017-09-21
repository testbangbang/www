package com.onyx.android.dr.activity;

import android.graphics.Bitmap;
import android.support.v7.widget.DividerItemDecoration;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.common.references.CloseableReference;
import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.adapter.SearchResultListAdapter;
import com.onyx.android.dr.bean.SearchResultBean;
import com.onyx.android.dr.common.ActivityManager;
import com.onyx.android.dr.event.BookDetailEvent;
import com.onyx.android.dr.holder.LibraryDataHolder;
import com.onyx.android.dr.interfaces.SearchBookView;
import com.onyx.android.dr.presenter.SearchBookPresenter;
import com.onyx.android.dr.view.CustomSearchView;
import com.onyx.android.sdk.data.LibraryDataModel;
import com.onyx.android.sdk.data.LibraryViewInfo;
import com.onyx.android.sdk.data.QueryResult;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.StringUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by hehai on 17-8-14.
 */

public class SearchBookActivity extends BaseActivity implements SearchBookView {
    @Bind(R.id.image_view_back)
    ImageView imageViewBack;
    @Bind(R.id.image)
    ImageView image;
    @Bind(R.id.title_bar_title)
    TextView titleBarTitle;
    @Bind(R.id.menu_back)
    LinearLayout menuBack;
    @Bind(R.id.title_bar_right_menu)
    TextView titleBarRightMenu;
    @Bind(R.id.custom_search_view)
    CustomSearchView customSearchView;
    @Bind(R.id.search_book_recycler)
    PageRecyclerView searchBookRecycler;
    @Bind(R.id.empty_result)
    TextView emptyResult;
    private SearchBookPresenter searchBookPresenter;
    private LibraryDataHolder dataHolder;
    private SearchResultListAdapter searchResultListAdapter;
    private int row = DRApplication.getInstance().getResources().getInteger(R.integer.search_result_row);
    private int col = DRApplication.getInstance().getResources().getInteger(R.integer.search_result_col);

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_search_book;
    }

    @Override
    protected void initConfig() {
        searchBookPresenter = new SearchBookPresenter(this);
    }

    @Override
    protected void initView() {
        searchResultListAdapter = new SearchResultListAdapter();
        searchBookRecycler.setLayoutManager(new DisableScrollGridManager(DRApplication.getInstance()));
        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(DRApplication.getInstance(), DividerItemDecoration.VERTICAL);
        searchBookRecycler.addItemDecoration(dividerItemDecoration);
        searchBookRecycler.setAdapter(searchResultListAdapter);
        searchBookRecycler.setFocusable(true);
        searchBookRecycler.setFocusableInTouchMode(true);
        customSearchView.setSearchViewListener(new CustomSearchView.SearchViewListener() {
            @Override
            public void onRefreshAutoComplete(String text) {
                if (StringUtils.isNullOrEmpty(text)) {
                    searchBookPresenter.getHistory();
                } else {
                    searchBookPresenter.searchBook(getDataHolder(), text, false);
                }
            }

            @Override
            public void onSearch(String text) {
                if (StringUtils.isNotBlank(text)) {
                    searchBookPresenter.searchBook(getDataHolder(), text, true);
                    searchBookPresenter.insertHistory(text);
                }
                searchBookRecycler.requestFocus();
            }

            @Override
            public void clearHistory() {
                searchBookPresenter.clearHistory();
            }
        });
    }

    @Override
    protected void initData() {
        titleBarTitle.setText(getString(R.string.search));
        customSearchView.setEditTextHint(getString(R.string.search_hint));
    }

    @OnClick(R.id.menu_back)
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.menu_back:
                finish();
                break;
        }
    }

    @Override
    public void setResult(List<SearchResultBean> result) {
        searchResultListAdapter.setList(result);
        emptyResult.setVisibility(CollectionUtils.isNullOrEmpty(result) ? View.VISIBLE : View.GONE);
    }

    private LibraryDataModel getLibraryDataModel(QueryResult<Metadata> result, Map<String, CloseableReference<Bitmap>> map) {
        return LibraryViewInfo.buildLibraryDataModel(result, map);
    }

    @Override
    public void setHint(List<String> books) {
        customSearchView.setSearchResult(books);
    }

    @Override
    public void setHistory(List<String> books) {
        customSearchView.setSearchHistory(books);
    }

    private LibraryDataHolder getDataHolder() {
        if (dataHolder == null) {
            dataHolder = new LibraryDataHolder(this);
            dataHolder.setCloudManager(DRApplication.getCloudStore().getCloudManager());
        }
        return dataHolder;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (customSearchView.getHintListStatus()) {
                customSearchView.showHintList(false);
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBookDetailEvent(BookDetailEvent event) {
        ActivityManager.startBookDetailActivity(this, event.getBookId());
    }
}
