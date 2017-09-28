package com.onyx.android.dr.activity;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.resource.gifbitmap.GifBitmapWrapper;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.CloudStore;
import com.onyx.android.sdk.data.GPaginator;
import com.onyx.android.sdk.data.OnyxDownloadManager;
import com.onyx.android.sdk.data.SortOrder;
import com.onyx.android.sdk.data.model.AppProduct;
import com.onyx.android.sdk.data.model.Category;
import com.onyx.android.sdk.data.model.Link;
import com.onyx.android.sdk.data.model.ProductQuery;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;
import com.onyx.android.sdk.data.request.cloud.ContainerRequest;
import com.onyx.android.sdk.data.request.cloud.MarketAppListRequest;
import com.onyx.android.sdk.data.request.cloud.MarketAppRequest;
import com.onyx.android.sdk.data.request.cloud.MarketAppSearchRequest;
import com.onyx.android.sdk.device.EnvironmentUtil;
import com.onyx.android.sdk.ui.activity.OnyxAppCompatActivity;
import com.onyx.android.sdk.ui.dialog.DialogSortBy;
import com.onyx.android.sdk.ui.view.DisableScrollLinearManager;
import com.onyx.android.sdk.ui.view.OnyxPageDividerItemDecoration;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.ui.wifi.NetworkHelper;
import com.onyx.android.sdk.utils.BitmapUtils;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.InputMethodUtils;
import com.onyx.android.sdk.utils.PackageUtils;
import com.onyx.android.sdk.utils.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class APPMarketActivity extends OnyxAppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String CATEGORY_APPS_GUID = "ed21e3b0ab744025b6447e82315b9398";
    private static final int INVALID_VALUE = -1;

    private OnyxPageDividerItemDecoration itemDecoration;

    @Bind(R.id.content_pageView)
    PageRecyclerView contentPageView;
    @Bind(R.id.editText_search_box)
    EditText searchText;
    @Bind(R.id.total_tv)
    TextView totalView;
    @Bind(R.id.page_indicator)
    TextView pageIndicator;

    private ProductQuery productQuery;
    private List<AppProduct> productList = new ArrayList<>();
    private int currentCategoryIndex = 0;

    private int pageViewRowCount = 5;
    private int pageViewColCount = 1;
    private int blankCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_market);
        ButterKnife.bind(this);

        initConfig();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAppList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    private void initConfig() {
        initProductQuery();
        initPageViewConfig();
        NetworkHelper.requestWifi(this);
    }

    private void initProductQuery() {
        productQuery = new ProductQuery();
    }

    private void initPageViewConfig() {
        TypedValue outValue = new TypedValue();
        getResources().getValue(R.dimen.activity_content_pageView_row_count, outValue, true);
        pageViewRowCount = (int) outValue.getFloat();
    }

    private void initView() {
        initTitle();
        initSearchEditView();
        initPageView();
    }

    private void initTitle() {
        ImageView imageFilter = (ImageView) findViewById(R.id.title_bar_right_icon_one);
        ImageView titleImage = (ImageView) findViewById(R.id.image);
        TextView title = (TextView) findViewById(R.id.title_bar_title);
        imageFilter.setImageResource(R.drawable.type_category);
        titleImage.setImageResource(R.drawable.market_logo);
        titleImage.setVisibility(View.VISIBLE);
        imageFilter.setVisibility(View.VISIBLE);
        title.setText(getString(R.string.app_market));
    }

    private void initSearchEditView() {
        searchText.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    startSearch();
                }
                return true;
            }
        });
    }

    private void initPageView() {
        Drawable divider = getResources().getDrawable(R.drawable.recycleview_normal_black_divider);
        itemDecoration = new OnyxPageDividerItemDecoration(this, OnyxPageDividerItemDecoration.VERTICAL).setDivider(divider);
        itemDecoration.setActualChildCount(pageViewRowCount);
        contentPageView.setLayoutManager(new DisableScrollLinearManager(this));
        contentPageView.addItemDecoration(itemDecoration);
        contentPageView.setItemDecorationHeight(divider.getIntrinsicHeight());
        contentPageView.setOnPagingListener(new PageRecyclerView.OnPagingListener() {
            @Override
            public void onPageChange(int position, int itemCount, int pageSize) {
                updatePageIndicator();
            }
        });
        contentPageView.setAdapter(new PageRecyclerView.PageAdapter<AppViewHolder>() {
            @Override
            public int getRowCount() {
                return pageViewRowCount;
            }

            @Override
            public int getColumnCount() {
                return pageViewColCount;
            }

            @Override
            public int getDataCount() {
                return CollectionUtils.getSize(productList);
            }

            @Override
            public AppViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
                return new AppViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_app_market_item, null, false));
            }

            @Override
            public void onPageBindViewHolder(AppViewHolder holder, int position) {
                holder.itemView.setTag(position);
                updateItemDecoration(contentPageView, itemDecoration);

                AppProduct product = productList.get(position);

                holder.nameLabel.setText(product.name);
                holder.descLabel.setText(StringUtils.isNullOrEmpty(product.summary) ? product.description : product.summary);
                holder.typeLabel.setText(product.type);
                holder.sizeLabel.setText(getString(R.string.size_of_app, product.size * 1.0f / 1024 / 1024));
                readerIconImageView(holder.iconImageLabel, product);
                renderButton(holder.downloadButton, product);
            }
        });
    }

    private int getAppVersionCode(Context context, String packageName) {
        int versionCode = -1;
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
            versionCode = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return versionCode;
    }

    private boolean checkInstalledLatest(AppProduct product) {
        return getAppVersionCode(this, product.packageName) >= product.versionCode;
    }

    private boolean checkDownloadedApk(AppProduct product) {
        File file = getApkFilePath(product);
        return file != null && file.exists();
    }

    private void readerIconImageView(ImageView imageView, final AppProduct product) {
        Glide.with(this).load(product.coverUrl)
                .error(R.mipmap.ic_launcher)
                .placeholder(R.mipmap.ic_launcher)
                .transform(new Transformation<GifBitmapWrapper>() {
                    @Override
                    public Resource<GifBitmapWrapper> transform(Resource<GifBitmapWrapper> resource, int outWidth, int outHeight) {
                        File coverFile = getApkIconFilePath(product);
                        if (!coverFile.exists()) {
                            BitmapUtils.saveBitmap(resource.get().getBitmapResource().get(), coverFile.getAbsolutePath());
                        }
                        return resource;
                    }

                    @Override
                    public String getId() {
                        return product.packageName;
                    }
                })
                .into(imageView);
    }

    private void renderButton(Button button, AppProduct product) {
        button.setText(R.string.apk_download);
        if (checkInstalledLatest(product)) {
            button.setText(R.string.apk_updated);
            return;
        }

        button.setText(checkDownloadedApk(product) ? R.string.install : R.string.download);
    }

    private void loadAppCategory() {
        final ContainerRequest containerRequest = new ContainerRequest(CATEGORY_APPS_GUID);
        getCloudStore().submitRequest(this, containerRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null) {
                    e.printStackTrace();
                }
                Category category = containerRequest.getCategory();
                if (category == null || CollectionUtils.isNullOrEmpty(category.children)) {
                    showWhiteToast(R.string.no_category_found, Toast.LENGTH_SHORT);
                    return;
                }
                showCategoryDialog(category);
            }
        });
    }

    private void searchAppList(ProductQuery query) {
        final MarketAppSearchRequest appSearchRequest = new MarketAppSearchRequest(query);
        loadData(appSearchRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null) {
                    return;
                }
                productList = appSearchRequest.getProductList();
                notifyDataChanged();
            }
        });
    }

    private void loadAppList() {
        final MarketAppListRequest appListRequest = new MarketAppListRequest(productQuery);
        loadData(appListRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null) {
                    return;
                }
                productList = appListRequest.getProductList();
                notifyDataChanged();
            }
        });
    }

    private <T extends BaseCloudRequest> void loadData(final T cloudRequest, final BaseCallback callback) {
        getCloudStore().submitRequest(this, cloudRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                dismissProgressDialog(request);
                if (e != null) {
                    showWhiteToast(R.string.get_empty_content, Toast.LENGTH_SHORT);
                    e.printStackTrace();
                }
                BaseCallback.invoke(callback, cloudRequest, e);
            }
        });
        showProgressDialog(cloudRequest, null);
    }

    private void notifyDataChanged() {
        contentPageView.notifyDataSetChanged();
        updatePageViewBlankCount(CollectionUtils.getSize(productList));
        updatePageIndicator();
    }

    private void updatePageViewBlankCount(int dataSize) {
        int itemCountOfPage = pageViewRowCount * pageViewColCount;
        int remainder = dataSize % itemCountOfPage;
        int blank = 0;
        if (remainder > 0) {
            blank = itemCountOfPage - remainder;
        }
        blankCount = blank;
    }

    private void updateItemDecoration(PageRecyclerView pageRecyclerView, OnyxPageDividerItemDecoration itemDecoration) {
        GPaginator pageIndicator = pageRecyclerView.getPaginator();
        if (pageIndicator.isLastPage()) {
            itemDecoration.setBlankCount(pageIndicator.itemsInCurrentPage() == pageViewRowCount ? 0 : blankCount);
        } else {
            itemDecoration.setBlankCount(0);
        }
    }

    private void updatePageIndicator() {
        totalView.setText(getString(R.string.total, contentPageView.getPaginator().getSize()));
        int currentPage = contentPageView.getPaginator().getCurrentPage() + 1;
        int totalPage = contentPageView.getPaginator().pages();
        if (totalPage == 0) {
            totalPage = 1;
        }
        pageIndicator.setText(currentPage + "/" + totalPage);
    }

    private void shiftSearchEditFocus() {
        searchText.clearFocus();
        contentPageView.requestFocus();
    }

    @OnClick(R.id.prev)
    void onPrevPageClick() {
        contentPageView.prevPage();
    }

    @OnClick(R.id.next)
    void onNextPageClick() {
        contentPageView.nextPage();
    }

    @OnClick(R.id.button_search)
    void onSearchClick() {
        startSearch();
    }

    @OnClick(R.id.menu_back)
    void onBack() {
        finish();
    }

    private void startSearch() {
        InputMethodUtils.hideInputKeyboard(this);
        String search = searchText.getText().toString();
        productQuery.resetCategory();
        resetCategoryIndex();
        if (StringUtils.isNullOrEmpty(search)) {
            loadAppList();
            return;
        }
        productQuery.key = search;
        searchAppList(productQuery);
    }

    @OnClick(R.id.title_bar_right_icon_one)
    void onTypeFilterClick() {
        loadAppCategory();
    }

    private void resetCategoryIndex() {
        currentCategoryIndex = INVALID_VALUE;
    }

    private void showCategoryDialog(final Category category) {
        Category allCategory = new Category();
        allCategory.name = getString(R.string.all);
        category.children.add(0, allCategory);

        List<String> categoryList = new ArrayList<>();
        for (Category child : category.children) {
            categoryList.add(child.name);
        }
        if (currentCategoryIndex >= categoryList.size()) {
            resetCategoryIndex();
        }
        DialogSortBy dialogSortBy = new DialogSortBy(null, categoryList);
        dialogSortBy.setShowSortOrderLayout(false);
        dialogSortBy.setCurrentSortBySelectedIndex(currentCategoryIndex);
        dialogSortBy.setOnSortByListener(new DialogSortBy.OnSortByListener() {
            @Override
            public void onSortBy(int position, String sortBy, SortOrder sortOrder) {
                currentCategoryIndex = position;
                productQuery.setCategory(category.children.get(currentCategoryIndex).getGuid());
                productQuery.resetKey();
                loadAppList();
            }
        });
        dialogSortBy.show(getFragmentManager());
    }

    public File getApkFilePath(AppProduct product) {
        if (StringUtils.isNullOrEmpty(product.name)) {
            return null;
        }
        String fileName = product.name.replaceAll(".apk", "") + "-" + product.versionCode + ".apk";
        fileName = FileUtils.fixNotAllowFileName(fileName);
        if (StringUtils.isNullOrEmpty(fileName)) {
            return null;
        }
        File dir = new File(EnvironmentUtil.getExternalStorageDirectory() + "/Download/AppMarket/");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return new File(dir, fileName);
    }

    public File getApkIconFilePath(AppProduct product) {
        File dir = new File(EnvironmentUtil.getExternalStorageDirectory() + "/Download/AppMarket/icon");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String fileName = product.packageName.replaceAll("\\.", "_") + ".png";
        return new File(dir, fileName);
    }

    private boolean startDownload(final AppProduct product, Link link) {
        final File file = getApkFilePath(product);
        if (file == null) {
            return false;
        }
        String url = link.url;
        if (StringUtils.isNullOrEmpty(url)) {
            return false;
        }
        BaseDownloadTask task = OnyxDownloadManager.getInstance().download(this, url, file.getAbsolutePath(),
                product.getGuid(), new BaseCallback() {

                    @Override
                    public void start(BaseRequest request) {
                        showDownloadingDialog(product);
                    }

                    @Override
                    public void progress(BaseRequest request, ProgressInfo info) {
                        setProgressDialogProgressMessage(product.getGuid(), String.valueOf((int) info.progress) + "%");
                    }

                    @Override
                    public void done(BaseRequest request, Throwable e) {
                        if (e != null) {
                            e.printStackTrace();
                        }
                        dismissProgressDialog(product.getGuid());
                        showWhiteToast(e == null ? R.string.download_success : R.string.apk_download_fail, Toast.LENGTH_SHORT);
                        getDownLoaderManager().removeTask(product.getGuid());
                        notifyDataChanged();
                        PackageUtils.installNormal(APPMarketActivity.this, file.getAbsolutePath());
                    }
                });
        task.setForceReDownload(true);
        getDownLoaderManager().addTask(product.getGuid(), task);
        return getDownLoaderManager().startDownload(task) != 0;
    }

    private void processDownloadClick(final int position) {
        final AppProduct product = productList.get(position);
        if (checkInstalledLatest(product)) {
            return;
        }
        if (checkDownloadedApk(product)) {
            PackageUtils.installNormal(this, getApkFilePath(product).getAbsolutePath());
            return;
        }
        final MarketAppRequest appRequest = new MarketAppRequest(product.getGuid());
        getCloudStore().submitRequest(this, appRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null) {
                    dismissProgressDialog(product.getGuid());
                    return;
                }
                Link link = appRequest.getDownloadLink();
                if (link == null) {
                    dismissProgressDialog(product.getGuid());
                    showWhiteToast(R.string.get_empty_download_link, Toast.LENGTH_SHORT);
                    return;
                }
                product.storage = appRequest.getAppProduct().storage;
                startDownload(product, link);
            }
        });
        showDownloadingDialog(product);
    }

    private void showDownloadingDialog(AppProduct product) {
        showProgressDialog(product.getGuid(), null);
        File file = getApkFilePath(product);
        if (file != null) {
            setProgressDialogToastMessage(product.getGuid(), FileUtils.getFileName(file.getAbsolutePath()) +
                    " " + getString(R.string.apk_downloading));
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_PAGE_UP || event.getKeyCode() == KeyEvent.KEYCODE_PAGE_DOWN ||
                event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP || event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN) {
            shiftSearchEditFocus();
        }
        return super.dispatchKeyEvent(event);
    }

    private CloudStore getCloudStore() {
        return DRApplication.getCloudStore();
    }

    private OnyxDownloadManager getDownLoaderManager() {
        return OnyxDownloadManager.getInstance();
    }

    class AppViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.app_icon_label)
        ImageView iconImageLabel;
        @Bind(R.id.app_name_label)
        TextView nameLabel;
        @Bind(R.id.app_type_label)
        TextView typeLabel;
        @Bind(R.id.app_size_label)
        TextView sizeLabel;
        @Bind(R.id.app_desc_label)
        TextView descLabel;

        @Bind(R.id.app_start_download)
        Button downloadButton;

        public AppViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.findViewById(R.id.app_start_download).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    processDownloadClick((int) itemView.getTag());
                }
            });
        }
    }
}
