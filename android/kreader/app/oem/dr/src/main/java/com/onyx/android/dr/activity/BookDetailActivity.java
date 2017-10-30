package com.onyx.android.dr.activity;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.common.ActivityManager;
import com.onyx.android.dr.common.CommonNotices;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.device.DeviceConfig;
import com.onyx.android.dr.event.DownloadSucceedEvent;
import com.onyx.android.dr.holder.LibraryDataHolder;
import com.onyx.android.dr.interfaces.BookDetailView;
import com.onyx.android.dr.presenter.BookDetailPresenter;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.Constant;
import com.onyx.android.sdk.data.OnyxDownloadManager;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.model.v2.CloudMetadata;
import com.onyx.android.sdk.data.utils.CloudUtils;
import com.onyx.android.sdk.data.v2.ContentService;
import com.onyx.android.sdk.device.Device;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.NetworkUtil;
import com.onyx.android.sdk.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by hehai on 17-9-6.
 */

public class BookDetailActivity extends BaseActivity implements BookDetailView {

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
    @Bind(R.id.title_bar_right_shopping_cart)
    TextView shoppingCart;
    @Bind(R.id.book_detail_book_cover)
    ImageView bookDetailBookCover;
    @Bind(R.id.book_detail_book_name)
    TextView bookDetailBookName;
    @Bind(R.id.book_detail_book_publisher)
    TextView bookDetailBookPublisher;
    @Bind(R.id.book_detail_crowd)
    TextView bookDetailCrowd;
    @Bind(R.id.book_detail_language)
    TextView bookDetailLanguage;
    @Bind(R.id.book_detail_price)
    TextView bookDetailPrice;
    @Bind(R.id.book_detail_pay)
    TextView bookDetailPay;
    @Bind(R.id.book_detail_try_read)
    TextView bookDetailTryRead;
    @Bind(R.id.book_detail_add_to_cart)
    TextView bookDetailAddToCart;
    @Bind(R.id.book_detail_introduction)
    TextView bookDetailIntroduction;
    @Bind(R.id.book_detail_catalog)
    TextView bookDetailCatalog;
    @Bind(R.id.book_detail_buy_layout)
    LinearLayout bookDetailBuyLayout;
    @Bind(R.id.book_detail_read)
    TextView bookDetailRead;
    private String bookId;
    private BookDetailPresenter bookDetailPresenter;
    private LibraryDataHolder dataHolder;
    private CloudMetadata metadata;

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_book_detail;
    }

    @Override
    protected void initConfig() {
        Intent intent = getIntent();
        if (intent != null) {
            bookId = intent.getStringExtra(Constant.ID_TAG);
        }
    }

    @Override
    protected void initView() {
        titleBarTitle.setText(getString(R.string.bookstore));
        shoppingCart.setVisibility(View.VISIBLE);
        shoppingCart.setText(String.format(getString(R.string.shopping_cart_count_format), DRApplication.getInstance().getCartCount()));
    }

    @Override
    protected void initData() {
        if (bookDetailPresenter == null) {
            bookDetailPresenter = new BookDetailPresenter(this);
        }
        bookDetailPresenter.loadBookDetail(bookId);
    }

    @Override
    public void setBookDetail(CloudMetadata metadata) {
        this.metadata = metadata;
        Glide.with(this).load(metadata.getCoverUrl()).placeholder(R.drawable.book_cover).into(bookDetailBookCover);
        bookDetailBookName.setText(String.format(getString(R.string.book_detail_book_name), metadata.getName()));
        bookDetailBookPublisher.setText(String.format(getString(R.string.book_detail_book_publisher), metadata.getPublisher()));
        bookDetailLanguage.setText(String.format(getString(R.string.book_detail_book_language), metadata.getLanguage()));
        bookDetailPrice.setText(String.format(getString(R.string.book_detail_book_price), metadata.getPrice()));
        bookDetailBuyLayout.setVisibility(metadata.isPaid() ? View.GONE : View.VISIBLE);
        bookDetailRead.setVisibility(metadata.isPaid() ? View.VISIBLE : View.GONE);
        bookDetailRead.setText(isFileExists(metadata) ? getString(R.string.read) : getString(R.string.download));
    }

    @Override
    public void setOrderId(String id) {
        ActivityManager.startPayActivity(this, id);
    }

    @Override
    public void setCartCount(int count) {
        DRApplication.getInstance().setCartCount(count);
        shoppingCart.setText(String.format(getString(R.string.shopping_cart_count_format), DRApplication.getInstance().getCartCount()));
    }

    @OnClick({R.id.menu_back, R.id.title_bar_right_menu, R.id.book_detail_pay, R.id.book_detail_try_read, R.id.book_detail_add_to_cart, R.id.book_detail_read, R.id.title_bar_right_shopping_cart})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.menu_back:
                finish();
                break;
            case R.id.title_bar_right_menu:
                break;
            case R.id.book_detail_pay:
                bookDetailPresenter.createOrder(metadata.getCloudId());
                break;
            case R.id.book_detail_try_read:
                read();//// TODO: 17-9-8 try read
                break;
            case R.id.book_detail_add_to_cart:
                bookDetailPresenter.addToCart(metadata.getCloudId());
                break;
            case R.id.book_detail_read:
                read();
                break;
            case R.id.title_bar_right_shopping_cart:
                ActivityManager.startShoppingCartActivity(BookDetailActivity.this);
                break;
        }
    }

    private void read() {
        if (isFileExists(metadata)) {
            openCloudFile(metadata);
            return;
        }
        if (enableWifiOpenAndDetect()) {
            return;
        }
        startDownload(metadata);
    }

    private void startDownload(final Metadata eBook) {
        final String filePath = getDataSaveFilePath(eBook);
        String bookDownloadUrl = DeviceConfig.sharedInstance(DRApplication.getInstance()).getBookDownloadUrl(eBook.getGuid());
        String token = DRApplication.getCloudStore().getCloudManager().getToken();
        Map<String, String> header = new HashMap<>();
        header.put(Constant.HEADER_AUTHORIZATION, ContentService.CONTENT_AUTH_PREFIX + token);
        OnyxDownloadManager downLoaderManager = getDownLoaderManager();
        BaseDownloadTask download = downLoaderManager.download(DRApplication.getInstance(), header, bookDownloadUrl, filePath, eBook.getGuid(), new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e == null) {
                    setCloudMetadataNativeAbsolutePath(eBook, filePath);
                }
            }

            @Override
            public void progress(BaseRequest request, ProgressInfo info) {

            }
        });
        getDownLoaderManager().startDownload(download);
        CommonNotices.showMessage(DRApplication.getInstance(), getString(R.string.downloading));
    }

    private String getDataSaveFilePath(Metadata book) {
        if (checkBookMetadataPathValid(book)) {
            return book.getNativeAbsolutePath();
        }
        String fileName = FileUtils.fixNotAllowFileName(book.getName() + "." + book.getType());
        if (StringUtils.isBlank(fileName)) {
            return null;
        }
        return new File(CloudUtils.dataCacheDirectory(DRApplication.getInstance(), book.getGuid()), fileName)
                .getAbsolutePath();
    }

    private boolean checkBookMetadataPathValid(Metadata book) {
        if (StringUtils.isNotBlank(book.getNativeAbsolutePath()) && new File(book.getNativeAbsolutePath()).exists()) {
            return true;
        }
        return false;
    }

    private OnyxDownloadManager getDownLoaderManager() {
        return OnyxDownloadManager.getInstance();
    }

    private void setCloudMetadataNativeAbsolutePath(Metadata book, String path) {
        book.setNativeAbsolutePath(path);
        DownloadSucceedEvent event = new DownloadSucceedEvent(book);
        EventBus.getDefault().post(event);
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

    private boolean isFileExists(Metadata book) {
        if (checkBookMetadataPathValid(book)) {
            return true;
        }
        if (StringUtils.isNullOrEmpty(book.getGuid())) {
            return false;
        }
        File dir = CloudUtils.dataCacheDirectory(DRApplication.getInstance(), book.getGuid());
        if (dir.list() == null || dir.list().length <= 0) {
            return false;
        }
        String path = getDataSaveFilePath(book);
        if (StringUtils.isNullOrEmpty(path)) {
            return false;
        }
        File file = new File(path);
        if (!file.exists() && file.length() <= 0) {
            return false;
        }
        if (StringUtils.isNullOrEmpty(book.getNativeAbsolutePath())) {
            setCloudMetadataNativeAbsolutePath(book, path);
        }
        return true;
    }

    private void openCloudFile(final Metadata book) {
        String path = getDataSaveFilePath(book);
        if (StringUtils.isNullOrEmpty(path)) {
            return;
        }
        if (!FileUtils.fileExist(path)) {
            return;
        }
        ActivityManager.openBook(DRApplication.getInstance(), book, path, Constants.OTHER_SOURCE_TAG);
    }

    private boolean enableWifiOpenAndDetect() {
        if (!NetworkUtil.isWiFiConnected(DRApplication.getInstance())) {
            Device.currentDevice().enableWifiDetect(DRApplication.getInstance());
            NetworkUtil.enableWiFi(DRApplication.getInstance(), true);
            return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        initData();
    }
}
