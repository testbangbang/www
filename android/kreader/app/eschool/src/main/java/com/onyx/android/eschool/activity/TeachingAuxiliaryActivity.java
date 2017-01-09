package com.onyx.android.eschool.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.model.FileDownloadStatus;
import com.onyx.android.eschool.R;
import com.onyx.android.eschool.SchoolApp;
import com.onyx.android.eschool.model.AppConfig;
import com.onyx.android.eschool.utils.DisplayModeUtils;
import com.onyx.android.eschool.utils.StudentPreferenceManager;
import com.onyx.android.sdk.utils.ViewDocumentUtils;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.CloudStore;
import com.onyx.android.sdk.data.OnyxDownloadManager;
import com.onyx.android.sdk.data.model.Category;
import com.onyx.android.sdk.data.model.Link;
import com.onyx.android.sdk.data.model.Product;
import com.onyx.android.sdk.data.model.ProductQuery;
import com.onyx.android.sdk.data.model.ProductSearch;
import com.onyx.android.sdk.data.request.cloud.ContainerListRequest;
import com.onyx.android.sdk.data.request.cloud.ProductListRequest;
import com.onyx.android.sdk.data.request.cloud.ProductRequest;
import com.onyx.android.sdk.data.utils.CloudUtils;
import com.onyx.android.sdk.data.utils.StoreUtils;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.ui.wifi.NetworkHelper;
import com.onyx.android.sdk.utils.ActivityUtil;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.InputMethodUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.android.sdk.utils.TestUtils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by suicheng on 2016/11/21.
 */

public class TeachingAuxiliaryActivity extends BaseActivity {
    private static final String PRODUCT_QUERY_KEY = "auxiliary_activity_query_key";
    private static final String CATEGORY_KEY = "auxiliary_activity_category_key";

    private static boolean notifyWifiRequest = false;

    @Bind(R.id.category_page_view)
    PageRecyclerView categoryPageView;
    @Bind(R.id.product_page_view)
    PageRecyclerView productPageView;
    @Bind(R.id.editText_search_box)
    EditText searchEdit;

    private List<Product> productList = new ArrayList<>();
    private List<Category> categoryList = new ArrayList<>();
    private int categoryCurrentIndex;
    private boolean isLoadData = true;

    private ProductQuery productQuery = new ProductQuery();

    private boolean displayMode = false;
    private String displayDocumentPath = "/mnt/sdcard/slide/PL-TEST-2.pdf";
    private int displayCount = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isLoadData) {
            loadProductList();
            isLoadData = false;
        }
        if (!notifyWifiRequest) {
            isLoadData = !NetworkHelper.requestWifi(this);
            notifyWifiRequest = true;
        }
    }

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_teaching_auxiliary;
    }

    @Override
    protected void initConfig() {
        loadCloudConf();
        initDisplayModeConfig();
    }

    private void initDisplayModeConfig() {
        displayMode = AppConfig.sharedInstance(this).isForDisplayHomeLayout();
        if (displayMode) {
            String displayPath = AppConfig.sharedInstance(this).getTeachingAuxiliaryDocumentDisplayFilePath();
            if (StringUtils.isNotBlank(displayPath)) {
                displayDocumentPath = displayPath;
            }
            displayCount = TestUtils.randInt(11, 17);
        }
    }

    private void loadCloudConf() {
        final String value = StudentPreferenceManager.getStringValue(this, PRODUCT_QUERY_KEY, "");
        if (StringUtils.isNotBlank(value)) {
            ProductQuery query = JSON.parseObject(value, ProductQuery.class);
            if (query != null) {
                productQuery = query;
            }
        }
        categoryCurrentIndex = StudentPreferenceManager.getIntValue(this, CATEGORY_KEY, 0);
        if (StringUtils.isNullOrEmpty(productQuery.category)) {
            categoryCurrentIndex = 0;
        }
    }

    private void saveCloudConf() {
        StudentPreferenceManager.setStringValue(this, PRODUCT_QUERY_KEY, JSON.toJSONString(productQuery));
        StudentPreferenceManager.setIntValue(this, CATEGORY_KEY, categoryCurrentIndex);
    }

    @Override
    protected void initView() {
        initEditView();
        initCategoryPageView();
        initProductPageView();
    }

    private void initEditView() {
        searchEdit.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        searchEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    startSearch(v.getText().toString().trim());
                }
                return true;
            }
        });
        searchEdit.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode != KeyEvent.KEYCODE_BACK) {
                    return false;
                } else {
                    processBackRequest();
                    return true;
                }
            }
        });
    }

    private void initCategoryPageView() {
        categoryPageView.setLayoutManager(new DisableScrollGridManager(this));
        categoryPageView.setAdapter(new PageRecyclerView.PageAdapter<AuxiliaryCategoryHolder>() {
            @Override
            public int getRowCount() {
                return 4;
            }

            @Override
            public int getColumnCount() {
                return 4;
            }

            @Override
            public int getDataCount() {
                return CollectionUtils.isNullOrEmpty(categoryList) ? 0 : categoryList.size();
            }

            @Override
            public AuxiliaryCategoryHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
                return new AuxiliaryCategoryHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.teaching_category_item, parent, false));
            }

            @Override
            public void onPageBindViewHolder(AuxiliaryCategoryHolder viewHolder, int position) {
                viewHolder.itemView.setTag(position);

                viewHolder.titleTextView.setText(categoryList.get(position).name);
                viewHolder.chooseImageView.setImageResource(categoryCurrentIndex == position ?
                        R.drawable.delivery_dot_green : R.drawable.delivery_dot_grey);
            }
        });
    }

    private void initProductPageView() {
        productPageView.setLayoutManager(new DisableScrollGridManager(this));
        productPageView.setAdapter(new PageRecyclerView.PageAdapter<AuxiliaryProductHolder>() {
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
                return CollectionUtils.isNullOrEmpty(productList) ? 0 : productList.size();
            }

            @Override
            public AuxiliaryProductHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
                return new AuxiliaryProductHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.teaching_auxiliary_product_item, parent, false));
            }

            @Override
            public void onPageBindViewHolder(AuxiliaryProductHolder viewHolder, int position) {
                viewHolder.itemView.setTag(position);

                Product product = productList.get(position);
                viewHolder.getWidgetImage.setVisibility(isFileExists(product) ? View.VISIBLE : View.GONE);
                updateDownloadPanel(viewHolder, product);

                if (StringUtils.isNotBlank(product.coverUrl)) {
                    Picasso.with(SchoolApp.singleton())
                            .load(product.coverUrl).fit().centerCrop()
                            .placeholder(R.drawable.cloud_cover).error(R.drawable.cloud_cover)
                            .into(viewHolder.coverImage);
                } else {
                    viewHolder.coverImage.setImageResource(getDefaultProductCover(position));
                }
            }
        });
        productPageView.setOnPagingListener(new PageRecyclerView.OnPagingListener() {
            @Override
            public void onPageChange(int position, int itemCount, int pageSize) {
                if (!productPageView.getPaginator().hasNextPage()) {
                    loadMoreProductList();
                }
            }
        });
    }

    private int getDefaultProductCover(int position) {
        if (!displayMode) {
            return R.drawable.cloud_cover;
        }
        return DisplayModeUtils.getTeachingAuxiliaryCover(position);
    }

    private void updatePageIndicatorView() {
        int currentPage = productPageView.getPaginator().getCurrentPage() + 1;
        int totalPage = productPageView.getPaginator().pages();
        if (totalPage == 0) {
            totalPage = 1;
        }
    }

    private void updateDownloadPanel(AuxiliaryProductHolder holder, Product product) {
        boolean showProgress = true;
        BaseDownloadTask task = getDownLoaderManager().getTask(product.getGuid());
        if (task == null) {
            showProgress = false;
        } else {
            switch (task.getStatus()) {
                case FileDownloadStatus.started:
                case FileDownloadStatus.pending:
                case FileDownloadStatus.progress:
                    showProgress = true;
                    break;
                case FileDownloadStatus.completed:
                case FileDownloadStatus.error:
                    showProgress = false;
                    getDownLoaderManager().removeTask(product.getGuid());
                    break;
            }
        }
        holder.progressBar.setVisibility(showProgress ? View.VISIBLE : View.INVISIBLE);
        holder.progressBar.setProgress(showProgress ? getDownLoaderManager().getTaskProgress(task.getId()) : 0);
    }

    @Override
    protected void initData() {
        loadCategoryData();
    }

    private void loadProductList() {
        productQuery.resetOffset();
        final ProductListRequest listRequest = new ProductListRequest(productQuery, true, false);
        getCloudStore().submitRequest(this, listRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                dismissProgressDialog(listRequest);
                if (e != null) {
                    return;
                }
                List<Product> list = StoreUtils.getResultList(listRequest.getProductResult());
                processLoadProductList(list);
                if (CollectionUtils.isNullOrEmpty(list)) {
                    showToast(R.string.category_below_content_empty_tip, Toast.LENGTH_SHORT);
                }
                updateViewPanel(list);
            }
        });
        showProgressDialog(listRequest, null);
    }

    private void loadMoreProductList() {
        productQuery.next(productList);
        final ProductListRequest listRequest = new ProductListRequest(productQuery, false, true);
        getCloudStore().submitRequest(this, listRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                dismissProgressDialog(listRequest);
                if (e != null) {
                    return;
                }
                if (StoreUtils.isEmpty(listRequest.getProductResult())) {
                    showToast(R.string.no_more_items, Toast.LENGTH_SHORT);
                    return;
                }

                int originSize = productList.size();
                productList.addAll(listRequest.getProductResult().list);
                productPageView.getAdapter().notifyItemRangeInserted(originSize, productList.size() - originSize);
            }
        });
        showProgressDialog(listRequest, null);
    }

    private void loadCategoryData() {
        final ContainerListRequest listRequest = new ContainerListRequest();
        getCloudStore().submitRequest(this, listRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null) {
                    return;
                }
                addCategoryTagAll(categoryList = listRequest.getProductResult());
                categoryPageView.getAdapter().notifyDataSetChanged();
            }
        });
    }

    private void addCategoryTagAll(List<Category> list) {
        if (CollectionUtils.isNullOrEmpty(list)) {
            return;
        }
        Category category = new Category();
        category.name = getString(R.string.all);
        list.add(0, category);
    }

    private void updateViewPanel(List<Product> list) {
        productList = list;
        productPageView.notifyDataSetChanged();
        productPageView.getAdapter().notifyDataSetChanged();
    }

    private boolean isBlankSearchEdit() {
        return StringUtils.isBlank(searchEdit.getText().toString());
    }

    private void clearSearchEdit() {
        searchEdit.setText("");
    }

    private void processBackRequest() {
        if (isBlankSearchEdit()) {
            onBackPressed();
            return;
        }
        quitSearch();
    }

    private void quitSearch() {
        InputMethodUtils.hideInputKeyboard(this);
        if (isBlankSearchEdit()) {
            return;
        }
        clearSearchEdit();
        loadProductList();
    }

    private void startSearch(String query) {
        if (StringUtils.isBlank(query)) {
            return;
        }
        InputMethodUtils.hideInputKeyboard(this);
        searchInCloud(query);
    }

    private void searchInCloud(final String pattern) {
        final ProductSearch productSearch = new ProductSearch();
        productSearch.pattern = pattern;
    }

    @OnClick(R.id.imageView_back)
    void backOnclick() {
        quitSearch();
    }

    @OnClick(R.id.imageView_search)
    void searchOnclick() {
        startSearch(searchEdit.getText().toString().trim());
    }

    private void processLoadProductList(List<Product> list) {
        if (!displayMode) {
            return;
        }
        list.clear();
        list.addAll(DisplayModeUtils.getRandomProductList(displayCount));
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isPageViewVisible() && ev.getY() > getCategoryPageViewHeight()) {
                    toggleCategoryPageView();
                    return true;
                }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onBackPressed() {
        if (hideCategoryPageView()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
                toggleCategoryPageView();
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private boolean hideCategoryPageView() {
        boolean visible = isPageViewVisible();
        if (visible) {
            toggleCategoryPageView();
        }
        return visible;
    }

    private int getCategoryPageViewHeight() {
        int height = categoryPageView.getMeasuredHeight();
        int[] location = new int[2];
        categoryPageView.getLocationOnScreen(location);
        height += location[1];
        return height;
    }

    private boolean isPageViewVisible() {
        return categoryPageView.getVisibility() == View.VISIBLE;
    }

    private void toggleCategoryPageView() {
        categoryPageView.setVisibility(isPageViewVisible() ? View.GONE : View.VISIBLE);
    }

    private void notifyCategoryChange() {
        if (categoryCurrentIndex == 0) {
            productQuery.resetCategory();
        } else {
            productQuery.setCategory(categoryList.get(categoryCurrentIndex).getGuid());
        }
        saveCloudConf();
        loadProductList();
    }

    private void processCategoryItemClick(int position) {
        if (categoryCurrentIndex == position) {
            return;
        }
        categoryPageView.getAdapter().notifyItemChanged(categoryCurrentIndex);
        categoryPageView.getAdapter().notifyItemChanged(position);
        categoryCurrentIndex = position;
        notifyCategoryChange();
    }

    private BaseCallback baseCallback = new BaseCallback() {
        @Override
        public void progress(BaseRequest request, ProgressInfo info) {
            productPageView.notifyDataSetChanged();
        }

        @Override
        public void done(BaseRequest request, Throwable e) {
            productPageView.notifyDataSetChanged();
        }
    };

    private boolean isInvalidLink(Link link) {
        return link == null || StringUtils.isBlank(link.url);
    }

    private String getDataSaveFilePath(Product product, Link link) {
        String fileName = FileUtils.fixNotAllowFileName(link.displayName);
        if (StringUtils.isBlank(fileName)) {
            return null;
        }
        return new File(CloudUtils.dataCacheDirectory(this, product.getGuid()), fileName).getAbsolutePath();
    }

    private File getDataSaveFilePath(Product product) {
        if (product == null) {
            return null;
        }
        File dir = CloudUtils.dataCacheDirectory(this, product.getGuid());
        File[] files = dir.listFiles();
        if (files == null || files.length <= 0) {
            return null;
        }
        return files[0];
    }

    private boolean isFileExists(Product product) {
        File file = CloudUtils.dataCacheDirectory(this, product.getGuid());
        if (file.list() == null || file.list().length <= 0) {
            return false;
        }
        return !"temp".equalsIgnoreCase(FileUtils.getFileExtension(file.list()[0]));
    }

    private void startDownload(Product product, Link link) {
        if (isInvalidLink(link)) {
            showToast(R.string.download_link_invalid, Toast.LENGTH_SHORT);
            return;
        }
        String filePath = getDataSaveFilePath(product, link);
        if (StringUtils.isBlank(filePath)) {
            return;
        }

        BaseDownloadTask task = getDownLoaderManager().download(link.url,
                filePath, link.displayName, baseCallback);
        getDownLoaderManager().addTask(product.getGuid(), task);
        getDownLoaderManager().startDownload(task);
    }

    private void startDownload(final int position) {
        final ProductRequest productRequest = new ProductRequest(productList.get(position).getGuid());
        getCloudStore().submitRequest(this, productRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null) {
                    return;
                }
                Product product = productRequest.getProduct();
                if (product != null) {
                    Link link = productRequest.getDownloadLink();
                    if (link != null) {
                        product.name = link.displayName;
                    }
                    productList.get(position).name = product.name;
                    productList.get(position).formats = product.formats;
                    productPageView.getAdapter().notifyDataSetChanged();
                    startDownload(product, link);
                }
            }
        });
    }

    private void openCloudFile(final Product product) {
        File file = getDataSaveFilePath(product);
        if(displayMode){
            file = new File(displayDocumentPath);
        }
        if (file == null || !file.exists()) {
            return;
        }
        ActivityUtil.startActivitySafely(this,
                ViewDocumentUtils.viewActionIntentWithMimeType(file),
                ViewDocumentUtils.getReaderComponentName(this));
    }

    private void processProductItemClick(final int position) {
        Product product = productList.get(position);
        if (isFileExists(product) || displayMode) {
            openCloudFile(product);
            return;
        }
        if (!NetworkHelper.requestWifi(this)) {
            return;
        }
        startDownload(position);
    }

    class AuxiliaryCategoryHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.title_text)
        TextView titleTextView;
        @Bind(R.id.choose_image)
        ImageView chooseImageView;

        public AuxiliaryCategoryHolder(final View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    processCategoryItemClick((Integer) itemView.getTag());
                }
            });
            ButterKnife.bind(this, itemView);
            titleTextView.setTextColor(Color.BLACK);
        }
    }

    class AuxiliaryProductHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.image_cover)
        ImageView coverImage;
        @Bind(R.id.image_get_widget)
        ImageView getWidgetImage;
        @Bind(R.id.progress_line)
        ProgressBar progressBar;

        public AuxiliaryProductHolder(final View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    processProductItemClick((Integer) v.getTag());
                }
            });
            ButterKnife.bind(this, itemView);
        }
    }

    private CloudStore getCloudStore() {
        return SchoolApp.getCloudStore();
    }

    private OnyxDownloadManager getDownLoaderManager() {
        return OnyxDownloadManager.getInstance(this);
    }
}
