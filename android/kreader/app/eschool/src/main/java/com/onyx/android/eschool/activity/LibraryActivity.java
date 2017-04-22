package com.onyx.android.eschool.activity;

import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.onyx.android.eschool.SchoolApp;
import com.onyx.android.eschool.action.LibraryDeleteAction;
import com.onyx.android.eschool.action.LibraryRemoveFromAction;
import com.onyx.android.eschool.custom.PageIndicator;
import com.onyx.android.eschool.holder.LibraryDataHolder;
import com.onyx.android.eschool.R;
import com.onyx.android.eschool.action.LibraryBuildAction;
import com.onyx.android.eschool.action.FilterByAction;
import com.onyx.android.eschool.action.MetadataLoadAction;
import com.onyx.android.eschool.action.LibraryMoveToAction;
import com.onyx.android.eschool.action.SortByAction;
import com.onyx.android.eschool.events.LoadFinishEvent;
import com.onyx.android.eschool.glide.ThumbnailLoader;
import com.onyx.android.eschool.utils.StudentPreferenceManager;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.BookFilter;
import com.onyx.android.sdk.data.SortBy;
import com.onyx.android.sdk.data.SortOrder;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.ui.utils.SelectionMode;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.utils.ActivityUtil;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.android.sdk.utils.ViewDocumentUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by suicheng on 2017/4/10.
 */

public class LibraryActivity extends BaseActivity {

    @Bind(R.id.content_pageView)
    PageRecyclerView contentPageView;
    PageIndicator pageIndicator;

    @Bind(R.id.parent_library_ref)
    LinearLayout parentLibraryRefLayout;

    private LibraryDataHolder dataHolder = new LibraryDataHolder(LibraryActivity.this);

    private List<Metadata> chosenItemsList = new ArrayList<>();
    private int selectionMode = SelectionMode.NORMAL_MODE;

    private boolean longClickMode = false;
    private int currentChosenItemIndex = 0;

    private ThumbnailLoader thumbnailLoader;

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_library;
    }

    @Override
    protected void initConfig() {
        loadQueryArgsConf();
        thumbnailLoader = new ThumbnailLoader(LibraryActivity.this, SchoolApp.getDataManager()
                .getRemoteContentProvider());
    }

    @Override
    protected void initView() {
        initSupportActionBarWithCustomBackFunction();
        getSupportActionBar().addOnMenuVisibilityListener(new ActionBar.OnMenuVisibilityListener() {
            @Override
            public void onMenuVisibilityChanged(boolean isVisible) {
                if (!isVisible) {
                    quitLongClickMode();
                }
            }
        });
        initContentPageView();
        initPageIndicator();
    }

    private TextView getLibraryTextView(Library library) {
        TextView tv = (TextView) LayoutInflater.from(this).inflate(R.layout.parent_library_ref_item, null);
        tv.setText(library.getName());
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processLibraryRefViewClick(v);
            }
        });
        return tv;
    }

    private void processLibraryRefViewClick(View v) {
        int index = parentLibraryRefLayout.indexOfChild(v);
        if (index == parentLibraryRefLayout.getChildCount() - 1) {
            return;
        }
        int removeCount = parentLibraryRefLayout.getChildCount() - 1 - index;
        for (int i = 0; i < removeCount; i++) {
            removeLastParentLibrary();
        }
        loadData();
    }

    private void initPageIndicator() {
        pageIndicator = new PageIndicator(findViewById(R.id.page_indicator_layout), contentPageView.getPaginator());
        pageIndicator.setTotalFormat(getString(R.string.total_format));
        pageIndicator.setPageChangedListener(new PageIndicator.PageChangedListener() {
            @Override
            public void prev() {
                contentPageView.prevPage();
            }

            @Override
            public void next() {
                contentPageView.nextPage();
            }
        });
    }

    private void initContentPageView() {
        contentPageView.setLayoutManager(new DisableScrollGridManager(this));
        contentPageView.setOnPagingListener(new PageRecyclerView.OnPagingListener() {
            @Override
            public void onPageChange(int position, int itemCount, int pageSize) {
                updatePageIndicator();
                if (!contentPageView.getPaginator().hasNextPage()) {
                    loadMoreData();
                }
            }
        });
        contentPageView.setAdapter(new PageRecyclerView.PageAdapter<LibraryItemViewHolder>() {

            @Override
            public int getItemViewType(int position) {
                return super.getItemViewType(position);
            }

            @Override
            public int getRowCount() {
                return 3;
            }

            @Override
            public int getColumnCount() {
                return 3;
            }

            @Override
            public int getDataCount() {
                return getLibraryListSize() + getBookListSize();
            }

            @Override
            public LibraryItemViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
                return new LibraryItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.library_item, parent, false));
            }

            @Override
            public void onPageBindViewHolder(LibraryItemViewHolder holder, int position) {
                holder.itemView.setTag(position);
                holder.checkBox.setVisibility(View.GONE);
                holder.titleView.setVisibility(View.VISIBLE);
                String title;
                if (position < getLibraryListSize()) {
                    Library library = getLibraryList().get(position);
                    title = library.getName();
                    holder.imageCover.setImageResource(R.drawable.library_sub_cover);
                    holder.progressBar.setVisibility(View.INVISIBLE);
                } else {
                    Metadata metadata = getBookList().get(getBookItemPosition(position));
                    title = metadata.getTitle();
                    if (StringUtils.isNullOrEmpty(title)) {
                        title = metadata.getName();
                    }
                    holder.progressBar.setVisibility(View.VISIBLE);
                    renderBookCoverImage(holder, metadata);
                    renderBookProgress(holder, metadata);
                    renderMultiCheckBox(holder, metadata);
                }
                holder.titleView.setText(title);
            }
        });
    }

    private void renderBookProgress(LibraryItemViewHolder holder, Metadata metadata) {
        holder.progressBar.setVisibility(metadata == null ? View.INVISIBLE : View.VISIBLE);
        if (metadata != null) {
            holder.progressBar.setProgress(metadata.getProgressPercent());
        }
    }

    private void renderMultiCheckBox(LibraryItemViewHolder holder, Metadata metadata) {
        if (isMultiSelectionMode()) {
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.checkBox.setChecked(chosenItemsList.contains(metadata));
        }
    }

    private void renderBookCoverImage(final LibraryItemViewHolder holder, final Metadata metadata) {
        if (StringUtils.isNullOrEmpty(metadata.getHashTag())) {
            holder.imageCover.setImageResource(R.drawable.library_book_cover);
            return;
        }
        Glide.with(this).using(thumbnailLoader).load(metadata).dontAnimate()
                .placeholder(R.drawable.library_book_cover)
                .error(R.drawable.library_book_cover)
                .listener(new RequestListener<Metadata, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, Metadata meta, Target<GlideDrawable> target, boolean isFirstResource) {
                        holder.titleView.setVisibility(View.VISIBLE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, Metadata meta, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        holder.titleView.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(holder.imageCover);
    }

    private void updateContentView() {
        contentPageView.getAdapter().notifyDataSetChanged();
        updatePageIndicator();
    }

    private void updatePageIndicator() {
        int totalCount = getTotalCount();
        pageIndicator.updateTotal(totalCount);
        pageIndicator.updateCurrentPage(totalCount);
    }

    @Override
    protected void initData() {
    }

    @Override
    protected void onStart() {
        super.onStart();
        dataHolder.getEventBus().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        dataHolder.getEventBus().unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        int limit = getBookListSize() > dataHolder.getQueryLimit() ? getBookListSize() : dataHolder.getQueryLimit();
        loadData(limit, 0);
    }

    private void loadData() {
        loadData(dataHolder.getQueryLimit(), 0);
    }

    private void loadData(int limit, int offset) {
        MetadataLoadAction loadAction = new MetadataLoadAction(
                dataHolder.getQueryArgs(limit, offset));
        loadAction.execute(dataHolder, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null) {
                    return;
                }
                updateContentView();
            }
        });
    }

    private void loadMoreData() {
        MetadataLoadAction loadAction = new MetadataLoadAction(
                dataHolder.getQueryArgs(dataHolder.getQueryLimit(), getBookListSize()), true);
        loadAction.execute(dataHolder, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null) {
                    return;
                }
                updateContentView();
            }
        });
    }

    private void loadQueryArgsConf() {
        String sortBy = StudentPreferenceManager.getStringValue(LibraryActivity.this,
                R.string.library_activity_sort_by_key, SortBy.Name.toString());
        String filterBy = StudentPreferenceManager.getStringValue(LibraryActivity.this,
                R.string.library_activity_book_filter_key, BookFilter.ALL.toString());
        SortOrder sortOrder = SortOrder.values()[StudentPreferenceManager.getIntValue(LibraryActivity.this,
                R.string.library_activity_asc_order_key, 0)];
        dataHolder.updateSortBy(SortBy.valueOf(sortBy), sortOrder);
        dataHolder.updateFilterBy(BookFilter.valueOf(filterBy), sortOrder);
    }

    private void processSortBy() {
        SortByAction sortByAction = new SortByAction(this);
        sortByAction.execute(dataHolder, null);
    }

    private void processFilterByBy() {
        FilterByAction filterByAction = new FilterByAction(this);
        filterByAction.execute(dataHolder, null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoadFinishEvent(LoadFinishEvent event) {
        updateContentView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = this.getMenuInflater();
        inflater.inflate(R.menu.library_option_menu, menu);
        return true;
    }

    private void prepareNormalOptionsMenu(Menu menu) {
        for (int i = 0; i < menu.size(); i++) {
            switch (menu.getItem(i).getItemId()) {
                case R.id.menu_remove_from_library:
                    if (CollectionUtils.isNullOrEmpty(dataHolder.getParentLibraryList())) {
                        menu.getItem(i).setVisible(false);
                    } else {
                        menu.getItem(i).setVisible(isMultiSelectionMode());
                    }
                    break;
                case R.id.menu_add_to_library:
                    menu.getItem(i).setVisible(isMultiSelectionMode());
                    break;
                case R.id.menu_delete_library:
                case R.id.menu_properties:
                    menu.getItem(i).setVisible(false);
                    break;
                default:
                    menu.getItem(i).setVisible(true);
                    break;
            }
        }
    }

    private void prepareLongClickOptionsMenu(Menu menu) {
        boolean isLibraryItem = isLibraryItem(currentChosenItemIndex);
        for (int i = 0; i < menu.size(); i++) {
            switch (menu.getItem(i).getItemId()) {
                case R.id.menu_properties:
                    menu.getItem(i).setVisible(true);
                    break;
                case R.id.menu_remove_from_library:
                    if (isLibraryItem) {
                        menu.getItem(i).setVisible(!CollectionUtils.isNullOrEmpty(dataHolder.getParentLibraryList()));
                    }
                    break;
                case R.id.menu_delete_library:
                    menu.getItem(i).setVisible(isLibraryItem);
                    break;
                default:
                    menu.getItem(i).setVisible(!isLibraryItem);
                    break;
            }
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (isLongClickMode()) {
            prepareLongClickOptionsMenu(menu);
        } else {
            prepareNormalOptionsMenu(menu);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sort_by:
                processSortBy();
                return true;
            case R.id.menu_filter_by:
                processFilterByBy();
                return true;
            case R.id.menu_multi_select:
                getIntoMultiSelectMode();
                return true;
            case R.id.menu_build_library:
                processBuildLibrary();
                return true;
            case R.id.menu_add_to_library:
                if (isLongClickMode() && !isMultiSelectionMode()) {
                    chosenItemsList.clear();
                    chosenItemsList.add(getBookList().get(getBookItemPosition(currentChosenItemIndex)));
                }
                processAddToLibrary();
                return true;
            case R.id.menu_remove_from_library:
                if (isLongClickMode() && !isMultiSelectionMode()) {
                    chosenItemsList.clear();
                    chosenItemsList.add(getBookList().get(getBookItemPosition(currentChosenItemIndex)));
                }
                processRemoveFromLibrary();
            case R.id.menu_delete_library:
                processDeleteLibrary();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void processBuildLibrary() {
        new LibraryBuildAction(this, dataHolder.getLibraryIdString()).execute(dataHolder, null);
    }

    private void processAddToLibrary() {
        Library currentLibrary = new Library();
        currentLibrary.setIdString(dataHolder.getLibraryIdString());
        LibraryMoveToAction moveToLibraryAction = new LibraryMoveToAction(this, getLibraryList(), chosenItemsList);
        moveToLibraryAction.execute(dataHolder, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null) {
                    return;
                }
                quitMultiSelectionMode();
                loadData(CollectionUtils.getSize(getBookList()), 0);
            }
        });
    }

    private void processRemoveFromLibrary() {
        Library fromLibrary = dataHolder.getParentLibraryList().get(CollectionUtils.getSize(
                dataHolder.getParentLibraryList()) - 1);
        LibraryRemoveFromAction removeFromAction = new LibraryRemoveFromAction(fromLibrary, chosenItemsList);
        removeFromAction.execute(dataHolder, null);
    }

    private void processDeleteLibrary() {
        new LibraryDeleteAction(this, dataHolder.getLibraryList().get(currentChosenItemIndex))
                .execute(dataHolder, null);
    }

    private void getIntoMultiSelectMode() {
        selectionMode = SelectionMode.MULTISELECT_MODE;
        chosenItemsList.clear();
        updateContentView();
    }

    @Override
    public void onBackPressed() {
        processBackRequest();
    }

    private void processBackRequest() {
        if (isMultiSelectionMode()) {
            quitMultiSelectionMode();
            updateContentView();
            return;
        }
        if (CollectionUtils.isNullOrEmpty(dataHolder.getParentLibraryList())) {
            super.onBackPressed();
            return;
        }
        removeLastParentLibrary();
        loadData();
    }

    private void removeLastParentLibrary() {
        parentLibraryRefLayout.removeViewAt(parentLibraryRefLayout.getChildCount() - 1);
        dataHolder.getParentLibraryList().remove(dataHolder.getParentLibraryList().size() - 1);
    }

    private int getBookItemPosition(int originPosition) {
        return originPosition - getLibraryListSize();
    }

    private int getTotalCount() {
        return (int) (getLibraryListSize() + dataHolder.getBookCount());
    }

    private int getBookListSize() {
        return CollectionUtils.getSize(getBookList());
    }

    private int getLibraryListSize() {
        return CollectionUtils.getSize(getLibraryList());
    }

    private List<Metadata> getBookList() {
        return dataHolder.getBookList();
    }

    private List<Library> getLibraryList() {
        return dataHolder.getLibraryList();
    }

    private void processBookItemOpen(int position) {
        if (getBookListSize() == 0) {
            return;
        }
        String filePath = getBookList().get(position).getNativeAbsolutePath();
        if (StringUtils.isNullOrEmpty(filePath)) {
            showToast(R.string.file_path_null, Toast.LENGTH_SHORT);
            return;
        }
        File file = new File(filePath);
        if (!file.exists()) {
            showToast(R.string.file_not_exist, Toast.LENGTH_SHORT);
            return;
        }
        ActivityUtil.startActivitySafely(this,
                ViewDocumentUtils.viewActionIntentWithMimeType(file),
                ViewDocumentUtils.getReaderComponentName(this));
    }


    private void addLibraryToParentRefList(Library library) {
        dataHolder.getParentLibraryList().add(library);
        parentLibraryRefLayout.addView(getLibraryTextView(library));
    }

    private void processLibraryItemClick(int position) {
        Library library = getLibraryList().get(position);
        addLibraryToParentRefList(library);
        loadData();
    }

    private void processNormalModeItemClick(int position) {
        if (position < getLibraryListSize()) {
            processLibraryItemClick(position);
            return;
        }
        processBookItemOpen(getBookItemPosition(position));
    }

    private void processMultiModeItemClick(int position) {
        if (position < getLibraryListSize()) {
            return;
        }
        Metadata metadata = getBookList().get(getBookItemPosition(position));
        if (chosenItemsList.contains(metadata)) {
            chosenItemsList.remove(metadata);
        } else {
            chosenItemsList.add(metadata);
        }
        updateContentView();
    }

    private void processItemClick(int position) {
        if (isMultiSelectionMode()) {
            processMultiModeItemClick(position);
            return;
        }
        processNormalModeItemClick(position);
    }

    private void processItemLongClick(int position) {
        if (isMultiSelectionMode()) {
            return;
        }
        getIntoLongClickMode(position);
        getSupportActionBar().openOptionsMenu();
    }

    private boolean isLibraryItem(int originPosition) {
        return originPosition < getLibraryListSize();
    }

    private void getIntoLongClickMode(int index) {
        longClickMode = true;
        currentChosenItemIndex = index;
    }

    private boolean isLongClickMode() {
        return longClickMode;
    }

    private void quitLongClickMode() {
        longClickMode = false;
    }

    private boolean isMultiSelectionMode() {
        return selectionMode == SelectionMode.MULTISELECT_MODE;
    }

    private void quitMultiSelectionMode() {
        selectionMode = SelectionMode.NORMAL_MODE;
        chosenItemsList.clear();
    }

    class LibraryItemViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.image_cover)
        ImageView imageCover;
        @Bind(R.id.textView_title)
        TextView titleView;
        @Bind(R.id.progress_line)
        ProgressBar progressBar;
        @Bind(R.id.multi_select_check_box)
        CheckBox checkBox;


        public LibraryItemViewHolder(final View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    processItemClick((Integer) itemView.getTag());
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    processItemLongClick((Integer) v.getTag());
                    return true;
                }
            });
            ButterKnife.bind(this, itemView);
        }
    }
}
