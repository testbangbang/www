package com.onyx.android.dr.fragment;

import android.support.v7.widget.DividerItemDecoration;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.adapter.BookshelfGroupAdapter;
import com.onyx.android.dr.data.MainTabMenuConfig;
import com.onyx.android.dr.event.BackToMainViewEvent;
import com.onyx.android.dr.reader.view.DisableScrollGridManager;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.SortBy;
import com.onyx.android.sdk.data.SortOrder;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.utils.QueryBuilder;
import com.onyx.android.sdk.ui.view.PageRecyclerView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by hehai on 17-7-11.
 */

public class BookshelfFragment extends BaseFragment {
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
    List<QueryArgs> queryArgses = new ArrayList<>();
    private String title;
    private int mode = LIBRARY_MODE;
    private Library library;
    private List<String> languages;
    private List<Library> libraryList;
    private String language;

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
    }

    @Override
    protected void loadData() {
        titleBarTitle.setText(title);
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
        queryArgses.clear();
        for (Library library : libraryList) {
            QueryArgs queryArgs = QueryBuilder.allBooksQuery(SortBy.CreationTime, SortOrder.Desc);
      //todo queryArgs.conditionGroup = ConditionGroup.clause().and(CloudMetadata_Table.language.eq(language));
            queryArgs.libraryUniqueId = library.getIdString();
            queryArgs.query = MainTabMenuConfig.languageBookshelf.get(language) + "-" + library.getName();
            queryArgses.add(queryArgs);
        }
        adapter.setGroups(mode, queryArgses);
        adapter.notifyDataSetChanged();
    }

    private void loadLibrary() {
        queryArgses.clear();
        for (String language : languages) {
            QueryArgs queryArgs = QueryBuilder.allBooksQuery(SortBy.CreationTime, SortOrder.Desc);
      //todo  queryArgs.conditionGroup = ConditionGroup.clause().and(CloudMetadata_Table.language.eq(language));
            queryArgs.libraryUniqueId = library.getIdString();
            queryArgs.query = library.getName() + "-" + MainTabMenuConfig.languageBookshelf.get(language);
            queryArgses.add(queryArgs);
        }
        adapter.setGroups(mode, queryArgses);
        adapter.notifyDataSetChanged();
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
        EventBus.getDefault().post(new BackToMainViewEvent());
    }

    public void setData(Library library, List<String> languages) {
        this.library = library;
        this.languages = languages;
        mode = LIBRARY_MODE;
        title = library.getName();
        loadData();
    }

    public void setData(String language, List<Library> libraryList) {
        this.libraryList = libraryList;
        this.language = language;
        mode = BOOKSHELF_MODE;
        title = MainTabMenuConfig.languageBookshelf.get(language);
        loadData();
    }

    @OnClick({R.id.menu_back, R.id.bookshelf_book_search, R.id.bookshelf_author_search, R.id.enter_bookstore})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.menu_back:
                back();
                break;
            case R.id.bookshelf_book_search:
                break;
            case R.id.bookshelf_author_search:
                break;
            case R.id.enter_bookstore:
                break;
        }
    }
}
