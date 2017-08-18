package com.onyx.android.dr.fragment;

import android.graphics.Bitmap;
import android.support.v7.widget.DividerItemDecoration;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.common.references.CloseableReference;
import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.adapter.BookListAdapter;
import com.onyx.android.dr.adapter.BookshelfGroupAdapter;
import com.onyx.android.dr.common.ActivityManager;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.event.BackToMainViewEvent;
import com.onyx.android.dr.event.EBookListEvent;
import com.onyx.android.dr.holder.LibraryDataHolder;
import com.onyx.android.dr.interfaces.BookshelfView;
import com.onyx.android.dr.presenter.BookshelfPresenter;
import com.onyx.android.dr.reader.view.DisableScrollGridManager;
import com.onyx.android.sdk.data.CloudQueryBuilder;
import com.onyx.android.sdk.data.DataManagerHelper;
import com.onyx.android.sdk.data.LibraryDataModel;
import com.onyx.android.sdk.data.LibraryViewInfo;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.QueryResult;
import com.onyx.android.sdk.data.SortBy;
import com.onyx.android.sdk.data.SortOrder;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.model.common.FetchPolicy;
import com.onyx.android.sdk.data.model.v2.CloudMetadata_Table;
import com.onyx.android.sdk.data.utils.QueryBuilder;
import com.onyx.android.sdk.ui.view.PageRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by hehai on 17-7-11.
 */

public class BookshelfFragment extends BaseFragment implements BookshelfView {
    public static final int LIBRARY_MODE = 0;
    public static final int BOOKSHELF_MODE = 1;
    @Bind(R.id.image_view_back)
    ImageView imageViewBack;
    @Bind(R.id.image)
    ImageView image;
    @Bind(R.id.menu_back)
    LinearLayout menuBack;
    @Bind(R.id.title_bar_title)
    TextView titleBarTitle;
    @Bind(R.id.title_bar_right_menu)
    TextView titleBarRightMenu;
    @Bind(R.id.bookshelf_book_search)
    ImageView bookshelfBookSearch;
    @Bind(R.id.bookshelf_author_search)
    ImageView bookshelfAuthorSearch;
    @Bind(R.id.bookshelf_groups_recycler)
    PageRecyclerView bookshelfGroupsRecycler;
    @Bind(R.id.enter_bookstore)
    TextView enterBookstore;
    private BookshelfGroupAdapter adapter;
    private int mode = LIBRARY_MODE;
    private String language;
    private Library library;
    private BookshelfPresenter bookshelfPresenter;
    private LibraryDataHolder dataHolder;
    private BookListAdapter listAdapter;

    @Override
    protected void initListener() {

    }

    @Override
    protected void initView(View rootView) {
        adapter = new BookshelfGroupAdapter(getActivity());
        bookshelfGroupsRecycler.setLayoutManager(new DisableScrollGridManager(DRApplication.getInstance()));
        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(DRApplication.getInstance(), DividerItemDecoration.VERTICAL);
        bookshelfGroupsRecycler.addItemDecoration(dividerItemDecoration);
        bookshelfGroupsRecycler.setAdapter(adapter);
        menuBack = (LinearLayout) rootView.findViewById(R.id.menu_back);

        listAdapter = new BookListAdapter(getActivity(), getDataHolder());
    }

    @Override
    protected void loadData() {
        if (bookshelfPresenter == null) {
            bookshelfPresenter = new BookshelfPresenter(this);
        }
        loadDataWithMode(mode);
    }

    private void loadDataWithMode(int mode) {
        switch (mode) {
            case LIBRARY_MODE:
                loadLibrary();
                break;
            case BOOKSHELF_MODE:
                loadBookshelf();
                break;
        }

    }

    private void loadBookshelf() {
        if (titleBarTitle != null) {
            titleBarTitle.setText(String.format(getString(R.string.bookshelf), language));
            bookshelfPresenter.getBookshelf(language, getDataHolder());
        }
    }

    private void loadLibrary() {
        if (library != null) {
            if (titleBarTitle != null) {
                titleBarTitle.setText(library.getName());
            }
            QueryArgs queryArgs = getDataHolder().getCloudViewInfo().buildLibraryQuery(library.getIdString());
            queryArgs.fetchPolicy = FetchPolicy.DB_ONLY;
            queryArgs.libraryUniqueId = library.getIdString();
            queryArgs.conditionGroup.and(CloudMetadata_Table.nativeAbsolutePath.isNotNull());
            bookshelfPresenter.getLibrary(queryArgs);
        }
    }

    @Override
    protected int getRootView() {
        return R.layout.fragment_bookshelf;
    }

    @Override
    public boolean onKeyBack() {
        back();
        return true;
    }

    private void back() {
        if (bookshelfGroupsRecycler.getAdapter() instanceof BookListAdapter) {
            bookshelfGroupsRecycler.setAdapter(adapter);
        } else {
            EventBus.getDefault().post(new BackToMainViewEvent());
        }
    }

    public void setData(String language) {
        this.language = language;
        mode = BOOKSHELF_MODE;
        loadData();
    }

    public void setData(Library library) {
        this.library = library;
        mode = LIBRARY_MODE;
        loadData();
    }

    @OnClick({R.id.menu_back, R.id.bookshelf_book_search, R.id.bookshelf_author_search, R.id.enter_bookstore})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.menu_back:
                back();
                break;
            case R.id.bookshelf_book_search:
                search(Constants.NAME_SEARCH);
                break;
            case R.id.bookshelf_author_search:
                search(Constants.AUTHOR_SEARCH);
                break;
            case R.id.enter_bookstore:
                enterToBookstore();
                break;
        }
    }

    private void search(String type) {
        ActivityManager.startSearchBookActivity(getActivity(), type);
    }

    private void enterToBookstore() {
        ActivityManager.startEBookStoreActivity(getActivity());
    }

    @Override
    public void setBooks(List<Metadata> result) {
        bookshelfGroupsRecycler.setAdapter(listAdapter);
        QueryResult<Metadata> queryResult = new QueryResult<>();
        queryResult.list = result;
        queryResult.count = result.size();
        Map<String, CloseableReference<Bitmap>> bitmaps = DataManagerHelper.loadCloudThumbnailBitmapsWithCache(DRApplication.getInstance(), DRApplication.getCloudStore().getCloudManager(), queryResult.list);
        listAdapter.updateContentView(getLibraryDataModel(queryResult, bitmaps));
    }

    @Override
    public void setLanguageCategory(Map<String, List<Metadata>> map) {
        adapter.setMap(map);
    }

    private LibraryDataHolder getDataHolder() {
        if (dataHolder == null) {
            dataHolder = new LibraryDataHolder(getActivity());
            dataHolder.setCloudManager(DRApplication.getCloudStore().getCloudManager());
        }
        return dataHolder;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEBookListEvent(EBookListEvent event) {
        bookshelfPresenter.getBooks(event.getLanguage());
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    private LibraryDataModel getLibraryDataModel(QueryResult<Metadata> result, Map<String, CloseableReference<Bitmap>> map) {
        return LibraryViewInfo.buildLibraryDataModel(result, map);
    }
}
