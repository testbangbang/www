package com.onyx.jdread.shop.ui;

import android.content.Context;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jingdong.app.reader.data.DrmTools;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.model.FileDownloadStatus;
import com.onyx.android.sdk.data.OnyxDownloadManager;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.utils.JSONObjectParseUtils;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.DialogBookInfoBinding;
import com.onyx.jdread.databinding.FragmentBookDetailBinding;
import com.onyx.jdread.databinding.LayoutBookBatchDownloadBinding;
import com.onyx.jdread.databinding.LayoutBookCopyrightBinding;
import com.onyx.jdread.main.common.BaseFragment;
import com.onyx.jdread.main.common.CommonUtils;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.main.common.ToastUtil;
import com.onyx.jdread.personal.common.LoginHelper;
import com.onyx.jdread.personal.dialog.TopUpDialog;
import com.onyx.jdread.personal.event.UserLoginResultEvent;
import com.onyx.jdread.personal.model.PersonalDataBundle;
import com.onyx.jdread.personal.model.PersonalViewModel;
import com.onyx.jdread.personal.model.UserLoginViewModel;
import com.onyx.jdread.reader.common.DocumentInfo;
import com.onyx.jdread.reader.common.OpenBookHelper;
import com.onyx.jdread.reader.ui.view.PageTextView;
import com.onyx.jdread.setting.ui.WifiFragment;
import com.onyx.jdread.shop.action.AddOrDeleteCartAction;
import com.onyx.jdread.shop.action.BookDetailAction;
import com.onyx.jdread.shop.action.BookRecommendListAction;
import com.onyx.jdread.shop.action.BookshelfInsertAction;
import com.onyx.jdread.shop.action.DownloadAction;
import com.onyx.jdread.shop.action.FileDeleteAction;
import com.onyx.jdread.shop.action.GetChapterGroupInfoAction;
import com.onyx.jdread.shop.action.GetChapterStartIdAction;
import com.onyx.jdread.shop.action.GetChaptersContentAction;
import com.onyx.jdread.shop.action.GetOrderInfoAction;
import com.onyx.jdread.shop.action.MetadataQueryAction;
import com.onyx.jdread.shop.action.SearchBookListAction;
import com.onyx.jdread.shop.adapter.BatchDownloadChaptersAdapter;
import com.onyx.jdread.shop.adapter.RecommendAdapter;
import com.onyx.jdread.shop.cloud.entity.NetBookPayParamsBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.BaseResultBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.BatchDownloadResultBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookDetailResultBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookExtraInfoBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookModelBooksResultBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.GetChapterStartIdResult;
import com.onyx.jdread.shop.cloud.entity.jdbean.GetOrderInfoResultBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.ResultBookBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.UpdateBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.PageTagConstants;
import com.onyx.jdread.shop.event.BookDetailReadNowEvent;
import com.onyx.jdread.shop.event.BookDetailViewInfoEvent;
import com.onyx.jdread.shop.event.BookSearchKeyWordEvent;
import com.onyx.jdread.shop.event.BookSearchPathEvent;
import com.onyx.jdread.shop.event.BuyBookSuccessEvent;
import com.onyx.jdread.shop.event.ChapterGroupItemClickEvent;
import com.onyx.jdread.shop.event.CopyrightCancelEvent;
import com.onyx.jdread.shop.event.CopyrightEvent;
import com.onyx.jdread.shop.event.DownloadFinishEvent;
import com.onyx.jdread.shop.event.DownloadStartEvent;
import com.onyx.jdread.shop.event.DownloadWholeBookEvent;
import com.onyx.jdread.shop.event.DownloadingEvent;
import com.onyx.jdread.shop.event.GoShopingCartEvent;
import com.onyx.jdread.shop.event.HideAllDialogEvent;
import com.onyx.jdread.shop.event.LoadingDialogEvent;
import com.onyx.jdread.shop.event.MenuWifiSettingEvent;
import com.onyx.jdread.shop.event.PayByCashSuccessEvent;
import com.onyx.jdread.shop.event.RecommendItemClickEvent;
import com.onyx.jdread.shop.event.RecommendNextPageEvent;
import com.onyx.jdread.shop.event.TopBackEvent;
import com.onyx.jdread.shop.event.ViewCommentEvent;
import com.onyx.jdread.shop.event.ViewDirectoryEvent;
import com.onyx.jdread.shop.model.BookBatchDownloadViewModel;
import com.onyx.jdread.shop.model.BookDetailViewModel;
import com.onyx.jdread.shop.model.DialogBookInfoViewModel;
import com.onyx.jdread.shop.model.ShopDataBundle;
import com.onyx.jdread.shop.utils.BookDownloadUtils;
import com.onyx.jdread.shop.utils.DownLoadHelper;
import com.onyx.jdread.shop.utils.ViewHelper;
import com.onyx.jdread.shop.view.BookInfoDialog;
import com.onyx.jdread.shop.view.DividerItemDecoration;
import com.onyx.jdread.shop.view.SubjectBookItemSpaceItemDecoration;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.Serializable;

/**
 * Created by jackdeng on 2017/12/16.
 */

public class BookDetailFragment extends BaseFragment {

    private FragmentBookDetailBinding bookDetailBinding;
    private int bookRecommendSpace = ResManager.getDimens(R.dimen.book_detail_recommend_recycle_view_space);
    private SubjectBookItemSpaceItemDecoration itemDecoration;
    private long ebookId;
    private PageRecyclerView recyclerViewRecommend;
    private BookInfoDialog copyRightDialog;
    private String localPath;
    private BookDetailResultBean.DetailBean bookDetailBean;
    private int downloadTaskState;
    private TextView buyBookButton;
    private TextView nowReadButton;
    private int percentage;
    private boolean isWholeBookDownLoad;
    private BookInfoDialog infoDialog;
    private boolean hasAddToCart = false;
    private BookInfoDialog batchDownloadDialog;
    private BookBatchDownloadViewModel batchDownloadViewModel;
    private String start_chapter;
    private boolean hasDoLogin;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        bookDetailBinding = FragmentBookDetailBinding.inflate(inflater, container, false);
        initView();
        initLibrary();
        initData();
        return bookDetailBinding.getRoot();
    }

    private void initLibrary() {
        if (!getEventBus().isRegistered(this)) {
            getEventBus().register(this);
        }
    }

    private void initData() {
        cleanData();
        ebookId = getBookId();
        getBookDetail();
    }

    private void getBookDetail() {
        getBookDetailData(false);
        getRecommendData();
    }

    private void queryMetadata() {
        MetadataQueryAction metadataQueryAction = new MetadataQueryAction(String.valueOf(ebookId));
        metadataQueryAction.execute(getShopDataBundle(), new RxCallback<MetadataQueryAction>() {
            @Override
            public void onNext(MetadataQueryAction queryAction) {
                Metadata metadata = queryAction.getMetadataResult();
                if (metadata != null) {
                    String extraInfoStr = metadata.getDownloadInfo();
                    BookExtraInfoBean extraInfoBean = JSONObjectParseUtils.toBean(extraInfoStr, BookExtraInfoBean.class);
                    localPath = metadata.getNativeAbsolutePath();
                    if (extraInfoBean.localPath != null) {
                        setQueryResult(extraInfoBean);
                    }
                }
            }
        });
    }

    private void cleanData() {
        isWholeBookDownLoad = false;
        hasAddToCart = false;
        ebookId = 0;
        downloadTaskState = 0;
        localPath = "";
    }

    private void initView() {
        bookDetailBinding.setBookDetailViewModel(getBookDetailViewModel());
        //TODO  Do not do temporarily:  bookDetailBinding.bookDetailInfo.bookDetailYuedouPriceOld.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        bookDetailBinding.bookDetailInfo.bookDetailCategorySecondPath.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        bookDetailBinding.bookDetailInfo.bookDetailCategoryThirdPath.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        nowReadButton = bookDetailBinding.bookDetailInfo.bookDetailNowRead;
        buyBookButton = bookDetailBinding.bookDetailInfo.bookDetailBuyBook;
        initDividerItemDecoration();
        setRecommendRecycleView();
        getBookDetailViewModel().getTitleBarViewModel().leftText = ResManager.getString(R.string.title_bar_title_book_detail);
        getBookDetailViewModel().getTitleBarViewModel().pageTag = PageTagConstants.BOOK_DETAIL;
        getBookDetailViewModel().getTitleBarViewModel().showRightText = false;
        checkWifi(getBookDetailViewModel().getTitleBarViewModel().leftText);
    }

    private void setRecommendRecycleView() {
        RecommendAdapter adapter = new RecommendAdapter(getEventBus());
        recyclerViewRecommend = bookDetailBinding.bookDetailInfo.recyclerViewRecommend;
        recyclerViewRecommend.setPageTurningCycled(true);
        recyclerViewRecommend.setCanTouchPageTurning(false);
        recyclerViewRecommend.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
        recyclerViewRecommend.addItemDecoration(itemDecoration);
        recyclerViewRecommend.setAdapter(adapter);
    }

    private void initDividerItemDecoration() {
        itemDecoration = new SubjectBookItemSpaceItemDecoration(false, bookRecommendSpace);
    }

    @Override
    public void onResume() {
        super.onResume();
        initLibrary();
    }

    @Override
    public void onStop() {
        super.onStop();
        getEventBus().unregister(this);
    }

    private BookDetailViewModel getBookDetailViewModel() {
        return getShopDataBundle().getBookDetailViewModel();
    }

    private EventBus getEventBus() {
        return getShopDataBundle().getEventBus();
    }

    private void getBookDetailData(final boolean shouldDownloadWholeBook) {
        BookDetailAction bookDetailAction = new BookDetailAction(ebookId, JDReadApplication.getInstance().getLogin());
        bookDetailAction.execute(getShopDataBundle(), new RxCallback<BookDetailAction>() {
            @Override
            public void onNext(BookDetailAction bookDetailAction) {
                BookDetailResultBean bookDetailResultBean = bookDetailAction.getBookDetailResultBean();
                if (bookDetailResultBean != null) {
                    if (bookDetailResultBean.result_code != Integer.valueOf(Constants.RESULT_CODE_SUCCESS)) {
                        return;
                    }

                    if (bookDetailBean != null && (bookDetailBean.ebook_id == bookDetailResultBean.data.ebook_id)) {
                        BookDetailResultBean.DetailBean newData = bookDetailResultBean.data;
                        newData.bookExtraInfoBean = bookDetailBean.bookExtraInfoBean;
                        newData.key = bookDetailBean.key;
                        newData.random = bookDetailBean.random;
                        bookDetailBean = newData;
                    } else {
                        bookDetailBean = bookDetailResultBean.data;
                    }

                    queryMetadata();

                    if (!ViewHelper.isCanNowRead(bookDetailBean)) {
                        hideNowReadButton();
                    }
                    if (!bookDetailBean.can_buy) {
                        if (!isWholeBookDownLoad && !fileIsExists(localPath) && !ViewHelper.isCanNowRead(bookDetailBean)) {
                            hideNowReadButton();
                        }
                        showShopCartView(false);
                    }
                    if (!StringUtils.isNullOrEmpty(bookDetailBean.author) && !ResManager.getString(R.string.content_empty).equals(bookDetailBean.author)) {
                        getAuthorBooksData(bookDetailBean.author);
                    } else {
                        bookDetailBean.setAuthor(ResManager.getString(R.string.error_content_author_unknown));
                    }

                    if (bookDetailBean.free) {
                        bookDetailBinding.bookDetailInfo.bookDetailYuedouPriceOld.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                        bookDetailBinding.bookDetailInfo.bookDetailYuedouPriceOld.setTextColor(ResManager.getColor(R.color.text_gray_color));
                        bookDetailBinding.bookDetailInfo.bookDetailDiscount.setVisibility(View.VISIBLE);
                        bookDetailBean.promotion = ResManager.getString(R.string.free_for_a_limited_time);
                        hideNowReadButton();
                    }

                    if (ViewHelper.isNetBook(bookDetailBean.book_type)) {
                        buyBookButton.setText(ResManager.getString(R.string.batch_download));
                        resetNowReadButton();
                        showShopCartView(false);
                        if (!StringUtils.isNullOrEmpty(bookDetailBean.modified)) {
                            getBookDetailViewModel().updateTimeInfo.set(ViewHelper.getNetBookUpdateTimeInfo(bookDetailBean.modified));
                        }
                    }

                    if (shouldDownloadWholeBook) {
                        smoothDownload();
                    }
                }
            }
        });
    }

    private void showShopCartView(boolean show) {
        bookDetailBinding.bookDetailInfo.shopCartContainer.setVisibility(show ? View.VISIBLE : View.GONE);
        bookDetailBinding.bookDetailInfo.spaceTwo.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void hideNowReadButton() {
        nowReadButton.setVisibility(View.GONE);
        bookDetailBinding.bookDetailInfo.spaceOne.setVisibility(View.GONE);
    }

    private void getAuthorBooksData(String keyWord) {
        SearchBookListAction booksAction = new SearchBookListAction("", 1, CloudApiContext.CategoryLevel2BookList.SORT_KEY_DEFAULT_VALUES,
                CloudApiContext.CategoryLevel2BookList.SORT_TYPE_DEFAULT_VALUES, keyWord, CloudApiContext.SearchBook.FILTER_DEFAULT);
        booksAction.execute(getShopDataBundle(), new RxCallback<SearchBookListAction>() {
            @Override
            public void onNext(SearchBookListAction action) {
                BookModelBooksResultBean booksResultBean = action.getBooksResultBean();
                if (booksResultBean != null && booksResultBean.data != null) {
                    if (booksResultBean.data.items != null && booksResultBean.data.items.size() > Constants.SHOP_MAIN_INDEX_ONE) {
                        bookDetailBinding.bookDetailInfo.bookDetailAuthor.setEnabled(true);
                        bookDetailBinding.bookDetailInfo.bookDetailAuthor.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
                    }
                }
            }
        });
    }

    private void getRecommendData() {
        BookRecommendListAction recommendListAction = new BookRecommendListAction(ebookId);
        recommendListAction.execute(getShopDataBundle(), new RxCallback() {
            @Override
            public void onNext(Object o) {
                if (recyclerViewRecommend != null) {
                    recyclerViewRecommend.gotoPage(0);
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN, priority = Integer.MAX_VALUE)
    public void onRecommendItemClickEvent(RecommendItemClickEvent event) {
        if (checkWifiDisconnected()) {
            return;
        }
        ResultBookBean bookBean = event.getBookBean();
        if (bookBean != null) {
            cleanData();
            initButton();
            switchToRecommendBook(bookBean.ebook_id);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onViewDirectoryEvent(ViewDirectoryEvent event) {
        if (fileIsExists(localPath)) {
            openBook(localPath, bookDetailBean);
        } else {
            ToastUtil.showToast(R.string.the_book_not_download);
        }
    }

    private void initButton() {
        resetNowReadButton();
        resetBuyBookButton();
        showShopCartView(true);
    }

    private void resetBuyBookButton() {
        buyBookButton.setVisibility(View.VISIBLE);
        buyBookButton.setEnabled(true);
        buyBookButton.setText(ResManager.getString(R.string.book_detail_button_buy_whole_book));
        bookDetailBinding.bookDetailInfo.spaceOne.setVisibility(View.VISIBLE);
    }

    private void resetNowReadButton() {
        nowReadButton.setVisibility(View.VISIBLE);
        nowReadButton.setEnabled(true);
        nowReadButton.setText(ResManager.getString(R.string.book_detail_button_now_read));
        bookDetailBinding.bookDetailInfo.spaceOne.setVisibility(View.VISIBLE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRecommendNextPageEvent(RecommendNextPageEvent event) {
        if (recyclerViewRecommend != null) {
            recyclerViewRecommend.nextPage();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBookDetailTopBackEvent(TopBackEvent event) {
        if (getViewEventCallBack() != null) {
            getViewEventCallBack().viewBack();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onViewCommentEvent(ViewCommentEvent event) {
        if (getViewEventCallBack() != null) {
            Bundle bundle = new Bundle();
            bundle.putLong(Constants.SP_KEY_BOOK_ID, getBookId());
            getViewEventCallBack().gotoView(CommentFragment.class.getName(), bundle);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDownloadWholeBookEvent(DownloadWholeBookEvent event) {
        if (bookDetailBean != null) {
            if (bookDetailBean.bookExtraInfoBean == null) {
                bookDetailBean.bookExtraInfoBean = new BookExtraInfoBean();
                bookDetailBean.bookExtraInfoBean.isWholeBookDownLoad = isWholeBookDownLoad;
            }
            if (!JDReadApplication.getInstance().getLogin() && !LoginHelper.loginDialogIsShowing()) {
                LoginHelper.showUserLoginDialog(getUserLoginViewModel(), getActivity());
                hasDoLogin = true;
            } else {
                smoothDownload();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCopyrightEvent(CopyrightEvent event) {
        showCopyRightDialog();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCopyrightCancelEvent(CopyrightCancelEvent event) {
        dismissCopyRightDialog();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGoShopingCartEvent(GoShopingCartEvent event) {
        if (!JDReadApplication.getInstance().getLogin()) {
            LoginHelper.showUserLoginDialog(getUserLoginViewModel(), getActivity());
        } else {
            if (!hasAddToCart) {
                if (bookDetailBean != null) {
                    if (bookDetailBean.add_cart) {
                        hasAddToCart = true;
                        ToastUtil.showToast(ResManager.getString(R.string.book_detail_add_cart_tip_the_book_already_add_cart));
                        return;
                    }
                    if (!bookDetailBean.can_buy) {
                        ToastUtil.showToast(ResManager.getString(R.string.book_detail_add_cart_tip_the_book_not_can_buy));
                        return;
                    }
                    addToCart(bookDetailBean.ebook_id);
                }
            } else {
                gotoShopCartFragment();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBookDetailReadNowEvent(BookDetailReadNowEvent event) {
        if (bookDetailBean != null) {
            if (bookDetailBean.bookExtraInfoBean == null) {
                bookDetailBean.bookExtraInfoBean = new BookExtraInfoBean();
            }
            tryDownload(bookDetailBean);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDownloadStartEvent(DownloadStartEvent event) {
        String tag = (String) event.tag;
        if (!checkDownloadCurrentBook(tag)) {
            return;
        }
        isWholeBookDownLoad = DownLoadHelper.isCurrentDownWholeBook(tag);
        if (isWholeBookDownLoad) {
            changeBuyBookButtonState();
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDownloadFinishEvent(DownloadFinishEvent event) {
        if (!checkDownloadCurrentBook((String) event.tag)) {
            return;
        }
        BaseDownloadTask task = OnyxDownloadManager.getInstance().getTask(event.tag);
        if (task != null) {
            handlerDownloadResult(task);
            isWholeBookDownLoad = bookDetailBean.bookExtraInfoBean.isWholeBookDownLoad;
            if (isWholeBookDownLoad) {
                nowReadButton.setEnabled(true);
                if (JDReadApplication.getInstance().getLogin()) {
                    hideNowReadButton();
                    showShopCartView(false);
                    upDataButtonDown(buyBookButton, true, bookDetailBean.bookExtraInfoBean.downLoadState);
                }
            } else {
                buyBookButton.setEnabled(true);
                upDataButtonDown(nowReadButton, true, bookDetailBean.bookExtraInfoBean.downLoadState);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDownloadingEvent(DownloadingEvent event) {
        if (!checkDownloadCurrentBook((String) event.tag)) {
            return;
        }
        BaseDownloadTask task = OnyxDownloadManager.getInstance().getTask(event.tag);
        if (task != null && event.progressInfoModel != null) {
            percentage = (int) (event.progressInfoModel.progress * 100);
            handlerDownloadResult(task);
            isWholeBookDownLoad = bookDetailBean.bookExtraInfoBean.isWholeBookDownLoad;
            if (isWholeBookDownLoad) {
                if (JDReadApplication.getInstance().getLogin()) {
                    hideNowReadButton();
                    showShopCartView(false);
                    upDataButtonDown(buyBookButton, false, bookDetailBean.bookExtraInfoBean.downLoadState);
                } else {
                    DownLoadHelper.stopDownloadingTask(task.getTag());
                }
            } else {
                upDataButtonDown(nowReadButton, false, bookDetailBean.bookExtraInfoBean.downLoadState);
            }
        }
    }

    private boolean checkDownloadCurrentBook(String tag) {
        if (tag != null) {
            tag = tag.replace(Constants.WHOLE_BOOK_DOWNLOAD_TAG, "");
            return bookDetailBean != null && tag.equals(String.valueOf(ebookId));
        } else {
            return false;
        }
    }

    private void handlerDownloadResult(BaseDownloadTask task) {
        downloadTaskState = task.getStatus();
        localPath = task.getPath();
        if (bookDetailBean.bookExtraInfoBean == null) {
            bookDetailBean.bookExtraInfoBean = new BookExtraInfoBean();
        }
        bookDetailBean.bookExtraInfoBean.downLoadState = downloadTaskState;
        bookDetailBean.bookExtraInfoBean.downLoadTaskTag = task.getTag();
        bookDetailBean.bookExtraInfoBean.isWholeBookDownLoad = DownLoadHelper.isCurrentDownWholeBook((String) task.getTag());
        if (DownLoadHelper.isDownloaded(downloadTaskState)) {
            percentage = DownLoadHelper.DOWNLOAD_PERCENT_FINISH;
        }
        bookDetailBean.bookExtraInfoBean.percentage = percentage;
        bookDetailBean.bookExtraInfoBean.localPath = localPath;
        bookDetailBean.bookExtraInfoBean.downloadUrl = task.getUrl();
        bookDetailBean.bookExtraInfoBean.progress = task.getSmallFileSoFarBytes();
        bookDetailBean.bookExtraInfoBean.totalSize = task.getSmallFileTotalBytes();
    }

    private void insertBookDetail(BookDetailResultBean.DetailBean bookDetailBean, String localPath) {
        BookshelfInsertAction insertAction = new BookshelfInsertAction(bookDetailBean, localPath);
        insertAction.execute(getShopDataBundle(), new RxCallback() {
            @Override
            public void onNext(Object o) {

            }
        });
    }

    private void upDataButtonDown(TextView button, boolean enabled, int downLoadState) {
        button.setEnabled(enabled);
        if (DownLoadHelper.isDownloading(downLoadState)) {
            button.setText(percentage + "%" + ResManager.getString(R.string.book_detail_downloading));
        } else if (DownLoadHelper.isDownloaded(downLoadState)) {
            button.setText(ResManager.getString(R.string.book_detail_button_now_read));
            ToastUtil.showToast(ResManager.getString(R.string.download_finished));
        } else if (DownLoadHelper.isError(downLoadState) || DownLoadHelper.isPause(downLoadState)) {
            button.setText(ResManager.getString(R.string.book_detail_tip_download_pause));
        }
    }

    private void smoothDownload() {

        if (ViewHelper.isNetBook(bookDetailBean.book_type)) {
            getChapterGroupInfo();
            return;
        }

        if (isWholeBookAlreadyDownload()) {
            openBook(localPath, bookDetailBean);
            return;
        }

        if (checkWifiDisconnected()) {
            return;
        }

        if (PersonalDataBundle.getInstance().isUserVip()) {
            if (bookDetailBean.can_read) {
                bookDetailBean.downLoadType = CloudApiContext.BookDownLoad.TYPE_SMOOTH_READ;
                downLoadWholeBook();
                return;
            }
        }

        if (!bookDetailBean.can_buy || bookDetailBean.isAlreadyBuy) {
            downLoadWholeBook();
            return;
        }

        if (bookDetailBean.can_buy) {
            startPayWholeBook(bookDetailBean.ebook_id);
            return;
        }
    }

    private void getChapterGroupInfo() {
        GetChapterStartIdAction getChapterStartIdAction = new GetChapterStartIdAction(ebookId);
        getChapterStartIdAction.execute(getShopDataBundle(), new RxCallback<GetChapterStartIdAction>() {
            @Override
            public void onNext(GetChapterStartIdAction getChapterStartIdAction) {
                GetChapterStartIdResult resultBean = getChapterStartIdAction.getResultBean();
                if (BaseResultBean.checkSuccess(resultBean)) {
                    if (resultBean.data != null) {
                        start_chapter = resultBean.data.start_chapter;
                        if (StringUtils.isNullOrEmpty(start_chapter)) {
                            ToastUtil.showToast(ResManager.getString(R.string.down_book_server_error));
                            return;
                        }
                        GetChapterGroupInfoAction action = new GetChapterGroupInfoAction(ebookId, start_chapter);
                        action.setViewModel(getBookBatchDownloadViewModel());
                        action.execute(getShopDataBundle(), new RxCallback<GetChapterGroupInfoAction>() {
                            @Override
                            public void onNext(GetChapterGroupInfoAction action) {
                                BatchDownloadResultBean.DataBean data = getBookBatchDownloadViewModel().getDataBean();
                                if (data != null && data.list != null) {
                                    if (data.book_can_buy) {
                                        showBatchDownload();
                                    } else {
                                        ToastUtil.showToast(getString(R.string.not_existent));
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    private BookBatchDownloadViewModel getBookBatchDownloadViewModel() {
        if (batchDownloadViewModel == null) {
            batchDownloadViewModel = new BookBatchDownloadViewModel(getEventBus());
        }
        return batchDownloadViewModel;
    }

    private void downLoadWholeBook() {
        if (checkWifiDisconnected()) {
            return;
        }
        nowReadButton.setEnabled(false);
        showShopCartView(false);
        changeBuyBookButtonState();
        bookDetailBean.bookExtraInfoBean.isWholeBookDownLoad = true;
        BookDownloadUtils.download(bookDetailBean, getShopDataBundle(), new RxCallback() {
            @Override
            public void onNext(Object o) {

            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                ToastUtil.showToast(ResManager.getString(R.string.download_fail));
                upDataButtonDown(buyBookButton, true, FileDownloadStatus.error);
            }
        });
    }

    private void openBook(String localPath, BookDetailResultBean.DetailBean detailBean) {
        DocumentInfo documentInfo = new DocumentInfo();
        DocumentInfo.SecurityInfo securityInfo = new DocumentInfo.SecurityInfo();
        if (detailBean.bookExtraInfoBean != null && detailBean.bookExtraInfoBean.isWholeBookDownLoad) {
            securityInfo.setKey(detailBean.bookExtraInfoBean.key);
            securityInfo.setRandom(detailBean.bookExtraInfoBean.random);
        }
        securityInfo.setUuId(DrmTools.getHardwareId(Build.SERIAL));
        documentInfo.setSecurityInfo(securityInfo);
        documentInfo.setBookPath(localPath);
        documentInfo.setBookName(detailBean.name);
        documentInfo.setWholeBookDownLoad(detailBean.bookExtraInfoBean.isWholeBookDownLoad);
        documentInfo.setCloudId(detailBean.ebook_id);
        OpenBookHelper.openBook(super.getContext(), documentInfo);
    }

    private void startPayWholeBook(long ebookId) {
        getOrderInfo(new String[]{String.valueOf(ebookId)});
    }

    private void getOrderInfo(String[] bookIds) {
        if (bookIds != null) {
            GetOrderInfoAction action = new GetOrderInfoAction(bookIds);
            action.execute(getShopDataBundle(), new RxCallback<GetOrderInfoAction>() {
                @Override
                public void onNext(GetOrderInfoAction getOrderInfoAction) {
                    GetOrderInfoResultBean.DataBean dataBean = getOrderInfoAction.getDataBean();
                    if (dataBean != null) {
                        showPayDialog(Constants.PAY_DIALOG_TYPE_PAY_ORDER, dataBean);
                    }
                }

                @Override
                public void onError(Throwable throwable) {
                    super.onError(throwable);
                }
            });
        }
    }

    private void addToCart(long ebookId) {
        if (checkWifiDisconnected()) {
            return;
        }
        final AddOrDeleteCartAction addOrDeleteCartAction = new AddOrDeleteCartAction(new String[]{String.valueOf(ebookId)}, Constants.CART_TYPE_ADD);
        addOrDeleteCartAction.execute(getShopDataBundle(), new RxCallback() {
            @Override
            public void onNext(Object o) {
                UpdateBean data = addOrDeleteCartAction.getData();
                if (data != null) {
                    setAddOrDelFromCart(data);
                }
            }
        });
    }

    public void setAddOrDelFromCart(UpdateBean result) {
        ToastUtil.showToast(ResManager.getString(R.string.book_detail_success_add_cart));
        hasAddToCart = true;
    }

    private void gotoShopCartFragment() {
        getViewEventCallBack().gotoView(ShopCartFragment.class.getName());
    }

    private void tryDownload(BookDetailResultBean.DetailBean bookDetailBean) {
        if (bookDetailBean == null) {
            return;
        }
        if (!isWholeBookDownLoad && DownLoadHelper.isDownloaded(downloadTaskState) && fileIsExists(localPath)) {
            openBook(localPath, bookDetailBean);
            return;
        }

        if (checkWifiDisconnected()) {
            return;
        }

        if (StringUtils.isNullOrEmpty(bookDetailBean.try_url)) {
            ToastUtil.showToast(getContext(), ResManager.getString(R.string.empty_url));
            return;
        }
        if (DownLoadHelper.isDownloading(downloadTaskState)) {
            ToastUtil.showToast(JDReadApplication.getInstance(), ResManager.getString(R.string.book_detail_downloading));
            return;
        }
        nowReadButton.setEnabled(false);
        buyBookButton.setEnabled(false);
        nowReadButton.setText(ResManager.getString(R.string.book_detail_downloading));
        bookDetailBean.bookExtraInfoBean.isWholeBookDownLoad = false;
        download(bookDetailBean);
        ToastUtil.showToast(JDReadApplication.getInstance(), bookDetailBean.name + ResManager.getString(R.string.book_detail_tip_book_add_to_bookself));
    }

    private void download(final BookDetailResultBean.DetailBean bookDetailBean) {
        final String tryDownLoadUrl = bookDetailBean.try_url;
        if (StringUtils.isNullOrEmpty(tryDownLoadUrl)) {
            ToastUtil.showToast(getContext(), ResManager.getString(R.string.empty_url));
            return;
        }
        final String localPath = CommonUtils.getJDBooksPath() + File.separator + bookDetailBean.name + Constants.BOOK_FORMAT;
        if (FileUtils.fileExist(localPath)) {
            FileDeleteAction fileDeleteAction = new FileDeleteAction(localPath);
            fileDeleteAction.execute(getShopDataBundle(), new RxCallback() {
                @Override
                public void onNext(Object o) {
                    startTryDownload(bookDetailBean, tryDownLoadUrl, localPath);
                }

                @Override
                public void onError(Throwable throwable) {
                    super.onError(throwable);
                    ToastUtil.showToast(ResManager.getString(R.string.download_fail));
                }
            });
        } else {
            startTryDownload(bookDetailBean, tryDownLoadUrl, localPath);
        }
    }

    private void startTryDownload(BookDetailResultBean.DetailBean bookDetailBean, String tryDownLoadUrl, String localPath) {
        String downloadTag = bookDetailBean.ebook_id + "";
        bookDetailBean.bookExtraInfoBean.downLoadTaskTag = downloadTag;
        bookDetailBean.bookExtraInfoBean.localPath = localPath;
        insertBookDetail(bookDetailBean, localPath);
        DownloadAction downloadAction = new DownloadAction(getContext(), tryDownLoadUrl, localPath, downloadTag);
        downloadAction.execute(getShopDataBundle(), new RxCallback() {
            @Override
            public void onNext(Object o) {

            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                ToastUtil.showToast(ResManager.getString(R.string.download_fail));
                upDataButtonDown(nowReadButton, true, FileDownloadStatus.error);
            }
        });
    }

    public void setQueryResult(BookExtraInfoBean extraInfoBean) {
        if (extraInfoBean != null) {
            localPath = extraInfoBean.localPath;
            downloadTaskState = extraInfoBean.downLoadState;
            isWholeBookDownLoad = extraInfoBean.isWholeBookDownLoad;
            if (bookDetailBean != null) {
                bookDetailBean.bookExtraInfoBean = extraInfoBean;
                bookDetailBean.bookExtraInfoBean.isWholeBookDownLoad = isWholeBookDownLoad;
            }
            if (isWholeBookAlreadyDownload() && JDReadApplication.getInstance().getLogin()) {
                hideNowReadButton();
                showShopCartView(false);
                buyBookButton.setText(ResManager.getString(R.string.book_detail_button_now_read));
            }
        }
    }

    private boolean isWholeBookAlreadyDownload() {
        return isWholeBookDownLoad && DownLoadHelper.isDownloaded(downloadTaskState) && fileIsExists(localPath);
    }

    private boolean fileIsExists(String localPath) {
        if (localPath != null) {
            return FileUtils.fileExist(localPath);
        } else {
            return false;
        }
    }

    public Context getContext() {
        return JDReadApplication.getInstance().getApplicationContext();
    }

    private void showBatchDownload() {
        if (ViewHelper.dialogIsShowing(batchDownloadDialog)) {
            return;
        }
        LayoutBookBatchDownloadBinding batchDownloadBinding = LayoutBookBatchDownloadBinding.inflate(LayoutInflater.from(getActivity()), null, false);
        batchDownloadBinding.setViewModel(getBookBatchDownloadViewModel());
        if (batchDownloadDialog == null) {
            batchDownloadDialog = new BookInfoDialog(JDReadApplication.getInstance(), R.style.CustomDialogStyle);
            batchDownloadDialog.setView(batchDownloadBinding.getRoot());
            batchDownloadBinding.closeDialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismissBatchDownloadDialog();
                }
            });
            PageRecyclerView batchDownloadRecyclerView = batchDownloadBinding.batchDownloadRecyclerView;
            batchDownloadRecyclerView.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
            DividerItemDecoration decoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST);
            decoration.setSpace(ResManager.getInteger(R.integer.book_batch_download_recycle_view_item_space));
            batchDownloadRecyclerView.addItemDecoration(decoration);
            RecyclerView.Adapter adapter = new BatchDownloadChaptersAdapter(getEventBus());
            batchDownloadRecyclerView.setAdapter(adapter);
        }
        if (!ViewHelper.dialogIsShowing(batchDownloadDialog)) {
            batchDownloadDialog.show();
        }
    }

    private void showCopyRightDialog() {
        if (ViewHelper.dialogIsShowing(copyRightDialog)) {
            return;
        }
        LayoutBookCopyrightBinding copyrightBinding = LayoutBookCopyrightBinding.inflate(LayoutInflater.from(getActivity()), null, false);
        copyrightBinding.setBookDetailViewModel(getBookDetailViewModel());
        if (copyRightDialog == null) {
            copyRightDialog = new BookInfoDialog(JDReadApplication.getInstance(), R.style.CustomDialogStyle);
            copyRightDialog.setView(copyrightBinding.getRoot());
        }
        if (!ViewHelper.dialogIsShowing(copyRightDialog)) {
            copyRightDialog.show();
        }
    }

    private void dismissCopyRightDialog() {
        ViewHelper.dismissDialog(copyRightDialog);
    }

    private void dismissBatchDownloadDialog() {
        ViewHelper.dismissDialog(batchDownloadDialog);
    }

    private void switchToRecommendBook(long ebookId) {
        setBookId(ebookId);
        getBookDetail();
    }

    public long getBookId() {
        Bundle bundle = getBundle();
        if (bundle != null) {
            return bundle.getLong(Constants.SP_KEY_BOOK_ID, 0);
        } else {
            return 0;
        }
    }

    public void setBookId(long ebookId) {
        this.ebookId = ebookId;
        Bundle bundle = getBundle();
        if (bundle != null) {
            bundle.putLong(Constants.SP_KEY_BOOK_ID, ebookId);
        }
    }

    public ShopDataBundle getShopDataBundle() {
        return ShopDataBundle.getInstance();
    }

    public PersonalDataBundle getPersonalDataBundle() {
        return PersonalDataBundle.getInstance();
    }

    public PersonalViewModel getPersonalViewModel() {
        return getPersonalDataBundle().getPersonalViewModel();
    }

    public UserLoginViewModel getUserLoginViewModel() {
        return getPersonalViewModel().getUserLoginViewModel();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        copyRightDialog = null;
        infoDialog = null;
        batchDownloadDialog = null;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoadingDialogEvent(LoadingDialogEvent event) {
        if (isAdded()) {
            showLoadingDialog(ResManager.getString(event.getResId()));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onHideAllDialogEvent(HideAllDialogEvent event) {
        hideLoadingDialog();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBuyBookSuccessEvent(BuyBookSuccessEvent event) {
        if (event.isNetBook) {
            ToastUtil.showToast(ResManager.getString(R.string.buy_book_success));
        } else {
            String msg = ResManager.getString(R.string.buy_book_success) + bookDetailBean.name + ResManager.getString(R.string.book_detail_tip_book_add_to_bookself);
            bookDetailBean.isAlreadyBuy = true;
            ToastUtil.showToast(JDReadApplication.getInstance(), msg);
            downLoadWholeBook();
        }
    }

    private void getChapterContent(String type, String ids, boolean can_try) {
        GetChaptersContentAction getChaptersContentAction = new GetChaptersContentAction(ebookId, type, ids, can_try);
        getChaptersContentAction.execute(getShopDataBundle(), new RxCallback() {
            @Override
            public void onNext(Object o) {

            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPayByCashSuccessEvent(PayByCashSuccessEvent event) {
        onBuyBookSuccessEvent(null);
    }

    private void changeBuyBookButtonState() {
        hideNowReadButton();
        buyBookButton.setEnabled(false);
        buyBookButton.setText(ResManager.getString(R.string.book_detail_downloading));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserLoginResultEvent(UserLoginResultEvent event) {
        if (hasDoLogin && ResManager.getString(R.string.login_success).equals(event.getMessage())) {
            hasDoLogin = false;
            getBookDetailData(!isWholeBookAlreadyDownload());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMenuWifiSettingEvent(MenuWifiSettingEvent event) {
        if (getViewEventCallBack() != null) {
            getViewEventCallBack().gotoView(WifiFragment.class.getName());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBookSearchKeyWordEvent(BookSearchKeyWordEvent event) {
        if (getViewEventCallBack() != null) {
            Bundle bundle = new Bundle();
            bundle.putString(Constants.SP_KEY_SEARCH_BOOK_CAT_ID, "");
            bundle.putString(Constants.SP_KEY_KEYWORD, event.keyWord);
            getViewEventCallBack().gotoView(SearchBookListFragment.class.getName(), bundle);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBookSearchPathEvent(BookSearchPathEvent event) {
        if (getViewEventCallBack() != null) {
            Bundle bundle = new Bundle();
            bundle.putString(Constants.SP_KEY_SEARCH_BOOK_CAT_ID, event.catId);
            bundle.putString(Constants.SP_KEY_KEYWORD, event.catName);
            getViewEventCallBack().gotoView(SearchBookListFragment.class.getName(), bundle);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBookDetailViewInfoEvent(BookDetailViewInfoEvent event) {
        showInfoDialog(event.info);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChapterGroupItemClickEvent(ChapterGroupItemClickEvent event) {
        BatchDownloadResultBean.DataBean.ListBean listBean = event.listBean;
        if (listBean != null) {
            buyChapter(listBean);
        }
    }

    private void buyChapter(final BatchDownloadResultBean.DataBean.ListBean listBean) {
        dismissBatchDownloadDialog();
        NetBookPayParamsBean payParamsBean = new NetBookPayParamsBean();
        BatchDownloadResultBean.DataBean dataBean = getBookBatchDownloadViewModel().getDataBean();
        payParamsBean.ebookId = ebookId;
        payParamsBean.start_chapter = start_chapter;
        payParamsBean.count = listBean.count;
        payParamsBean.jd_price = listBean.jd_price;
        payParamsBean.voucher = dataBean.voucher;
        payParamsBean.yuedou = dataBean.yuedou;
        if (bookDetailBean != null) {
            payParamsBean.bookName = bookDetailBean.name;
        }
        showPayDialog(Constants.PAY_DIALOG_TYPE_NET_BOOK, payParamsBean);
    }

    private void showPayDialog(int payType, Serializable orderInfo) {
        TopUpDialog dialog = new TopUpDialog();
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.PAY_DIALOG_TYPE, payType);
        bundle.putSerializable(Constants.ORDER_INFO, orderInfo);
        dialog.setArguments(bundle);
        dialog.show(getActivity().getFragmentManager(), "");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        hideAllDialog();
    }

    private void hideAllDialog() {
        hideLoadingDialog();
        dismissCopyRightDialog();
        dismissInfoDialog();
        dismissBatchDownloadDialog();
        LoginHelper.dismissUserLoginDialog();
    }

    private void showInfoDialog(String content) {
        if (ViewHelper.dialogIsShowing(infoDialog)) {
            return;
        }
        if (StringUtils.isNullOrEmpty(content)) {
            return;
        }
        DialogBookInfoBinding infoBinding = DialogBookInfoBinding.inflate(LayoutInflater.from(getActivity()), null, false);
        final DialogBookInfoViewModel dialogBookInfoViewModel = getBookDetailViewModel().getDialogBookInfoViewModel();
        dialogBookInfoViewModel.content.set(content);
        dialogBookInfoViewModel.title.set(ResManager.getString(R.string.book_detail_text_view_content_introduce));
        infoBinding.setViewModel(dialogBookInfoViewModel);
        infoDialog = new BookInfoDialog(JDReadApplication.getInstance(), R.style.CustomDialogStyle);
        infoDialog.setView(infoBinding.getRoot());
        PageTextView pagedWebView = infoBinding.bookInfoWebView;
        pagedWebView.setOnPagingListener(new PageTextView.OnPagingListener() {
            @Override
            public void onPageChange(int currentPage, int totalPage) {
                dialogBookInfoViewModel.currentPage.set(currentPage);
                dialogBookInfoViewModel.totalPage.set(totalPage);
            }
        });
        infoBinding.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissInfoDialog();
            }
        });
        if (!ViewHelper.dialogIsShowing(infoDialog)) {
            infoDialog.show();
        }
    }

    private void dismissInfoDialog() {
        ViewHelper.dismissDialog(infoDialog);
    }
}
