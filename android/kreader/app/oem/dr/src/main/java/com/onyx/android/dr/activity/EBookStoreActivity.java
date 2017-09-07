package com.onyx.android.dr.activity;

import android.graphics.Bitmap;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.common.references.CloseableReference;
import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.adapter.EBookGroupAdapter;
import com.onyx.android.dr.adapter.EBookLanguageGroupAdapter;
import com.onyx.android.dr.adapter.EBookListAdapter;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.event.DownloadSucceedEvent;
import com.onyx.android.dr.event.EBookChildLibraryEvent;
import com.onyx.android.dr.event.EBookListEvent;
import com.onyx.android.dr.holder.LibraryDataHolder;
import com.onyx.android.dr.interfaces.EBookStoreView;
import com.onyx.android.dr.presenter.EBookStorePresenter;
import com.onyx.android.dr.reader.view.DisableScrollGridManager;
import com.onyx.android.dr.util.DRPreferenceManager;
import com.onyx.android.sdk.data.DataManagerHelper;
import com.onyx.android.sdk.data.LibraryDataModel;
import com.onyx.android.sdk.data.LibraryViewInfo;
import com.onyx.android.sdk.data.QueryResult;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.ui.view.PageRecyclerView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by hehai on 17-8-2.
 */

public class EBookStoreActivity extends BaseActivity implements EBookStoreView {
    @Bind(R.id.image_view_back)
    ImageView imageViewBack;
    @Bind(R.id.image)
    ImageView image;
    @Bind(R.id.title_bar_title)
    TextView titleBarTitle;
    @Bind(R.id.menu_back)
    LinearLayout menuBack;
    @Bind(R.id.ebook_store_groups_recycler)
    PageRecyclerView ebookStoreGroupsRecycler;
    @Bind(R.id.title_bar_right_menu)
    TextView titleBarRightMenu;
    @Bind(R.id.ebook_store_tab)
    TabLayout ebookStoreTab;
    private EBookStorePresenter eBookStorePresenter;
    private List<Library> libraryList;
    private EBookGroupAdapter adapter;
    private LibraryDataHolder dataHolder;
    private EBookListAdapter listAdapter;
    private EBookLanguageGroupAdapter eBookLanguageGroupAdapter;

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_ebook_store;
    }

    @Override
    protected void initConfig() {

    }

    @Override
    protected void initView() {
        titleBarTitle.setText(getString(R.string.ebook_store));
        adapter = new EBookGroupAdapter();
        ebookStoreGroupsRecycler.setLayoutManager(new DisableScrollGridManager(DRApplication.getInstance()));
        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(DRApplication.getInstance(), DividerItemDecoration.VERTICAL);
        ebookStoreGroupsRecycler.addItemDecoration(dividerItemDecoration);
        ebookStoreGroupsRecycler.setAdapter(adapter);

        listAdapter = new EBookListAdapter(getDataHolder());

        eBookLanguageGroupAdapter = new EBookLanguageGroupAdapter();
    }

    @Override
    protected void initData() {
        eBookStorePresenter = new EBookStorePresenter(this);
        eBookStorePresenter.getRootLibraryList(getParentLibraryId());
    }

    @OnClick(R.id.menu_back)
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.menu_back:
                back();
                break;
        }
    }

    private void back() {
        if (ebookStoreGroupsRecycler.getAdapter() instanceof EBookListAdapter) {
            ebookStoreGroupsRecycler.setAdapter(eBookLanguageGroupAdapter);
        } else if (ebookStoreGroupsRecycler.getAdapter() instanceof EBookLanguageGroupAdapter) {
            ebookStoreGroupsRecycler.setAdapter(adapter);
        } else {
            finish();
        }
    }

    @Override
    public void setLibraryList(List<Library> list) {
        libraryList = list;
        adapter.setGroups(libraryList);
    }

    @Override
    public void setBooks(List<Metadata> result) {
        ebookStoreGroupsRecycler.setAdapter(listAdapter);
        QueryResult<Metadata> queryResult = new QueryResult<>();
        queryResult.list = result;
        queryResult.count = result.size();
        Map<String, CloseableReference<Bitmap>> bitmaps = DataManagerHelper.loadCloudThumbnailBitmapsWithCache(this, DRApplication.getCloudStore().getCloudManager(), queryResult.list);
        listAdapter.updateContentView(getLibraryDataModel(queryResult, bitmaps));
    }

    @Override
    public void setLanguageCategory(String name, Map<String, List<Metadata>> map) {
        ebookStoreGroupsRecycler.setAdapter(eBookLanguageGroupAdapter);
        eBookLanguageGroupAdapter.setMap(name, map);
    }

    private String getParentLibraryId() {
        return DRPreferenceManager.loadLibraryParentId(this, Constants.EMPTY_STRING);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEBookChildLibraryEvent(EBookChildLibraryEvent event) {
        eBookStorePresenter.getLanguageCategoryBooks(event.getLibrary());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEBookListEvent(EBookListEvent event) {
        eBookStorePresenter.getBooks(event.getLanguage());
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onDownloadSucceedEvent(DownloadSucceedEvent event) {
        getDataHolder().getCloudManager().getCloudDataProvider().saveMetadata(DRApplication.getInstance(), event.getMetadata());
    }

    private LibraryDataHolder getDataHolder() {
        if (dataHolder == null) {
            dataHolder = new LibraryDataHolder(this);
            dataHolder.setCloudManager(DRApplication.getCloudStore().getCloudManager());
        }
        return dataHolder;
    }

    private LibraryDataModel getLibraryDataModel(QueryResult<Metadata> result, Map<String, CloseableReference<Bitmap>> map) {
        return LibraryViewInfo.buildLibraryDataModel(result, map);
    }
}
