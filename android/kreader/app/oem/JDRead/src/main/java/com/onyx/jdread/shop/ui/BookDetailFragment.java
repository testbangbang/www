package com.onyx.jdread.shop.ui;

import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.widget.TextView;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.onyx.android.sdk.data.GPaginator;
import com.onyx.android.sdk.data.OnyxDownloadManager;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.utils.JSONObjectParseUtils;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.DialogBookInfoBinding;
import com.onyx.jdread.databinding.FragmentBookDetailBinding;
import com.onyx.jdread.databinding.LayoutBookCopyrightBinding;
import com.onyx.jdread.main.common.BaseFragment;
import com.onyx.jdread.main.common.CommonUtils;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.manager.ManagerActivityUtils;
import com.onyx.jdread.main.common.JDPreferenceManager;
import com.onyx.jdread.main.common.ToastUtil;
import com.onyx.jdread.personal.action.GetOrderUrlAction;
import com.onyx.jdread.personal.cloud.entity.jdbean.GetOrderUrlResultBean;
import com.onyx.jdread.personal.common.LoginHelper;
import com.onyx.jdread.personal.event.UserLoginResultEvent;
import com.onyx.jdread.personal.model.PersonalDataBundle;
import com.onyx.jdread.personal.model.PersonalViewModel;
import com.onyx.jdread.personal.model.UserLoginViewModel;
import com.onyx.jdread.reader.common.DocumentInfo;
import com.onyx.jdread.reader.common.OpenBookHelper;
import com.onyx.jdread.setting.ui.WifiFragment;
import com.onyx.jdread.shop.action.AddBookToSmoothCardAction;
import com.onyx.jdread.shop.action.AddOrDeleteCartAction;
import com.onyx.jdread.shop.action.BookDetailAction;
import com.onyx.jdread.shop.action.BookRecommendListAction;
import com.onyx.jdread.shop.action.BookshelfInsertAction;
import com.onyx.jdread.shop.action.DownloadAction;
import com.onyx.jdread.shop.action.MetadataQueryAction;
import com.onyx.jdread.shop.action.SearchBookListAction;
import com.onyx.jdread.shop.adapter.RecommendAdapter;
import com.onyx.jdread.shop.cloud.entity.jdbean.AddOrDelFromCartBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookDetailResultBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookExtraInfoBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookModelBooksResultBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.ResultBookBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.PageTagConstants;
import com.onyx.jdread.shop.event.BookDetailReadNowEvent;
import com.onyx.jdread.shop.event.BookDetailViewInfoEvent;
import com.onyx.jdread.shop.event.BookSearchKeyWordEvent;
import com.onyx.jdread.shop.event.BookSearchPathEvent;
import com.onyx.jdread.shop.event.BuyBookSuccessEvent;
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
import com.onyx.jdread.shop.event.RecommendItemClickEvent;
import com.onyx.jdread.shop.event.RecommendNextPageEvent;
import com.onyx.jdread.shop.event.TopBackEvent;
import com.onyx.jdread.shop.event.ViewCommentEvent;
import com.onyx.jdread.shop.model.BookDetailViewModel;
import com.onyx.jdread.shop.model.DialogBookInfoViewModel;
import com.onyx.jdread.shop.model.ShopDataBundle;
import com.onyx.jdread.shop.utils.BookDownloadUtils;
import com.onyx.jdread.shop.utils.DownLoadHelper;
import com.onyx.jdread.shop.utils.ViewHelper;
import com.onyx.jdread.shop.view.AutoPagedWebView;
import com.onyx.jdread.shop.view.DividerItemDecoration;
import com.onyx.jdread.util.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by jackdeng on 2017/12/16.
 */

public class BookDetailFragment extends BaseFragment {

    private FragmentBookDetailBinding bookDetailBinding;
    private int bookDetailSpace = JDReadApplication.getInstance().getResources().getInteger(R.integer.book_detail_recycle_view_space);
    private DividerItemDecoration itemDecoration;
    private long ebookId;
    private PageRecyclerView recyclerViewRecommend;
    private AlertDialog copyRightDialog;
    private boolean isTryRead;
    private boolean isSmoothRead;
    private String localPath;
    private BookDetailResultBean.DetailBean bookDetailBean;
    private int downloadTaskState;
    private TextView buyBookButton;
    private TextView nowReadButton;
    private boolean isDataBaseHaveBook;
    private int percentage;
    private boolean isWholeBook;
    private GPaginator paginator;
    private AlertDialog infoDialog;

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
        ebookId = JDPreferenceManager.getLongValue(Constants.SP_KEY_BOOK_ID, 0);
        getBookDetail();
    }

    private void getBookDetail() {
        queryMetadata();
        getBookDetailData();
        getRecommendData();
    }

    private void queryMetadata() {
        MetadataQueryAction metadataQueryAction = new MetadataQueryAction(String.valueOf(ebookId));
        metadataQueryAction.execute(getShopDataBundle(), new RxCallback<MetadataQueryAction>() {
            @Override
            public void onNext(MetadataQueryAction queryAction) {
                Metadata metadata = queryAction.getMetadataResult();
                String extraInfoStr = metadata.getExtraAttributes();
                BookExtraInfoBean extraInfoBean = JSONObjectParseUtils.toBean(extraInfoStr, BookExtraInfoBean.class);
                setQueryResult(extraInfoBean);
            }
        });
    }

    private void cleanData() {
        isTryRead = false;
        isSmoothRead = false;
        isDataBaseHaveBook = false;
        isWholeBook = false;
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
        getBookDetailViewModel().getTitleBarViewModel().leftText = getString(R.string.title_bar_title_book_detail);
        getBookDetailViewModel().getTitleBarViewModel().pageTag = PageTagConstants.BOOK_DETAIL;
        getBookDetailViewModel().getTitleBarViewModel().showRightText = false;
    }

    private void setRecommendRecycleView() {
        RecommendAdapter adapter = new RecommendAdapter(getEventBus());
        recyclerViewRecommend = bookDetailBinding.bookDetailInfo.recyclerViewRecommend;
        recyclerViewRecommend.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
        recyclerViewRecommend.addItemDecoration(itemDecoration);
        recyclerViewRecommend.setAdapter(adapter);
        paginator = recyclerViewRecommend.getPaginator();
        recyclerViewRecommend.setOnPagingListener(new PageRecyclerView.OnPagingListener() {
            @Override
            public void onPageChange(int position, int itemCount, int pageSize) {

            }
        });
    }

    private void initDividerItemDecoration() {
        itemDecoration = new DividerItemDecoration(JDReadApplication.getInstance(), DividerItemDecoration.HORIZONTAL_LIST);
        itemDecoration.setDrawLine(false);
        itemDecoration.setSpace(bookDetailSpace);
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

    private void getBookDetailData() {
        BookDetailAction bookDetailAction = new BookDetailAction(ebookId);
        bookDetailAction.execute(getShopDataBundle(), new RxCallback<BookDetailAction>() {
            @Override
            public void onNext(BookDetailAction bookDetailAction) {
                BookDetailResultBean bookDetailResultBean = bookDetailAction.getBookDetailResultBean();
                if (bookDetailResultBean != null) {
                    if (bookDetailResultBean.result_code != Integer.valueOf(Constants.RESULT_CODE_SUCCESS)) {
                        if (!StringUtils.isNullOrEmpty(bookDetailResultBean.message)) {
                            ToastUtil.showToast(bookDetailResultBean.message);
                        }
                        return;
                    }
                    bookDetailBean = bookDetailResultBean.data;
                    //TODO set NowReadButton show or hide with free and set buyBookButton text;

                    if (StringUtils.isNullOrEmpty(bookDetailBean.try_url)) {
                        nowReadButton.setVisibility(View.GONE);
                    }
                    if (!bookDetailBean.can_buy) {
                        buyBookButton.setVisibility(View.GONE);
                    }
                    if (!StringUtils.isNullOrEmpty(bookDetailBean.author) && !getString(R.string.content_empty).equals(bookDetailBean.author)) {
                        getAuthorBooksData(bookDetailBean.author);
                    } else {
                        bookDetailBean.setAuthor(getString(R.string.error_content_author_unknown));
                    }
                }
            }
        });
    }

    private void getAuthorBooksData(String keyWord) {
        SearchBookListAction booksAction = new SearchBookListAction("", 1, CloudApiContext.CategoryLevel2BookList.SORT_KEY_DEFAULT_VALUES,
                CloudApiContext.CategoryLevel2BookList.SORT_TYPE_DEFAULT_VALUES, keyWord, CloudApiContext.SearchBook.FILTER_DEFAULT);
        booksAction.execute(getShopDataBundle(), new RxCallback<SearchBookListAction>() {
            @Override
            public void onNext(SearchBookListAction action) {
                BookModelBooksResultBean booksResultBean = action.getBooksResultBean();
                if (booksResultBean != null && booksResultBean.data != null) {
                    if (booksResultBean.data.items != null && booksResultBean.data.items.size() > 0) {
                        bookDetailBinding.bookDetailInfo.bookDetailAuthor.setEnabled(true);
                        bookDetailBinding.bookDetailInfo.bookDetailAuthor.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
                        bookDetailBinding.bookDetailInfo.bookDetailAuthor.setCompoundDrawables(null,null,getContext().getResources().getDrawable(R.mipmap.ic_go_view_all),null);
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

            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN, priority = Integer.MAX_VALUE)
    public void onRecommendItemClickEvent(RecommendItemClickEvent event) {
        ResultBookBean bookBean = event.getBookBean();
        if (bookBean != null) {
            cleanData();
            setBookId(bookBean.ebook_id);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRecommendNextPageEvent(RecommendNextPageEvent event) {
        if (recyclerViewRecommend != null){
            if (paginator != null) {
                if (paginator.isLastPage()) {
                    recyclerViewRecommend.gotoPage(0);
                } else {
                    recyclerViewRecommend.nextPage();
                }
            }
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
        if (checkWfiDisConnected()) {
            return;
        }
        if (getViewEventCallBack() != null) {
            getViewEventCallBack().gotoView(CommentFragment.class.getName());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDownloadWholeBookEvent(DownloadWholeBookEvent event) {
        if (checkWfiDisConnected()) {
            return;
        }
        BookDetailResultBean bookDetailResultBean = event.getBookDetailResultBean();
        if (bookDetailResultBean != null) {
            bookDetailBean = bookDetailResultBean.data;
            BookExtraInfoBean extraInfoBean = new BookExtraInfoBean();
            bookDetailBean.bookExtraInfoBean = extraInfoBean;
            if (!JDReadApplication.getInstance().getLogin()) {
                LoginHelper.showUserLoginDialog(getActivity(), getUserLoginViewModel());
            } else {
                smoothDownload();
            }
        }
    }

    private boolean checkWfiDisConnected() {
        if (!Utils.isNetworkConnected(JDReadApplication.getInstance())) {
            ToastUtil.showToast(JDReadApplication.getInstance().getResources().getString(R.string.wifi_no_connected));
            return true;
        }
        return false;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCopyrightEvent(CopyrightEvent event) {
        if (checkWfiDisConnected()) {
            return;
        }
        showCopyRightDialog();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCopyrightCancelEvent(CopyrightCancelEvent event) {
        dismissCopyRightDialog();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGoShopingCartEvent(GoShopingCartEvent event) {
        if (checkWfiDisConnected()) {
            return;
        }
        if (bookDetailBean != null) {
            addToCart(bookDetailBean.ebook_id);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBookDetailReadNowEvent(BookDetailReadNowEvent event) {
        if (checkWfiDisConnected()) {
            return;
        }
        BookDetailResultBean bookDetailResultBean = event.getBookDetailResultBean();
        if (bookDetailResultBean != null) {
            bookDetailBean = bookDetailResultBean.data;
            BookExtraInfoBean extraInfoBean = new BookExtraInfoBean();
            bookDetailBean.bookExtraInfoBean = extraInfoBean;
            tryDownload(bookDetailBean);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDownloadStartEvent(DownloadStartEvent event) {
        isWholeBook = bookDetailBean.bookExtraInfoBean.isWholeBook;
        if (isWholeBook) {
            changeBuyBookButtonState();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDownloadFinishEvent(DownloadFinishEvent event) {
        BaseDownloadTask task = OnyxDownloadManager.getInstance().getTask(event.tag);
        if (task != null) {
            handlerDownloadResult(task);
            isWholeBook = bookDetailBean.bookExtraInfoBean.isWholeBook;
            if (isWholeBook) {
                upDataButtonDown(buyBookButton, true, bookDetailBean.bookExtraInfoBean);
            } else {
                upDataButtonDown(nowReadButton, true, bookDetailBean.bookExtraInfoBean);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDownloadingEvent(DownloadingEvent event) {
        BaseDownloadTask task = OnyxDownloadManager.getInstance().getTask(event.tag);
        if (task != null && event.progressInfoModel != null) {
            percentage = (int) (event.progressInfoModel.progress * 100);
            handlerDownloadResult(task);
            isWholeBook = bookDetailBean.bookExtraInfoBean.isWholeBook;
            if (isWholeBook) {
                upDataButtonDown(buyBookButton, false, bookDetailBean.bookExtraInfoBean);
            } else {
                upDataButtonDown(nowReadButton, false, bookDetailBean.bookExtraInfoBean);
            }
        }
    }

    private void handlerDownloadResult(BaseDownloadTask task) {
        downloadTaskState = task.getStatus();
        localPath = task.getPath();
        bookDetailBean.bookExtraInfoBean.downLoadState = downloadTaskState;
        bookDetailBean.bookExtraInfoBean.downLoadTaskTag = task.getTag();
        if (DownLoadHelper.isDownloaded(downloadTaskState)) {
            percentage = DownLoadHelper.DOWNLOAD_PERCENT_FINISH;
        }
        bookDetailBean.bookExtraInfoBean.percentage = percentage;
        bookDetailBean.bookExtraInfoBean.localPath = localPath;
        if (DownLoadHelper.canInsertBookDetail(downloadTaskState)) {
            insertBookDetail(bookDetailBean, localPath);
        }
    }

    private void insertBookDetail(BookDetailResultBean.DetailBean bookDetailBean, String localPath) {
        BookshelfInsertAction insertAction = new BookshelfInsertAction(bookDetailBean, localPath);
        insertAction.execute(getShopDataBundle(), new RxCallback() {
            @Override
            public void onNext(Object o) {

            }
        });
    }

    private void upDataButtonDown(TextView button, boolean enabled, BookExtraInfoBean infoBean) {
        button.setEnabled(enabled);
        if (DownLoadHelper.isDownloading(infoBean.downLoadState)) {
            button.setText(percentage + "%" + getString(R.string.book_detail_downloading));
        } else if (DownLoadHelper.isDownloaded(infoBean.downLoadState)) {
            button.setText(getString(R.string.book_detail_button_now_read));
        } else if (DownLoadHelper.isError(infoBean.downLoadState)) {
            button.setText(getString(R.string.book_detail_tip_try_again));
        }
    }

    private void smoothDownload() {
        if (isWholeBook && isDataBaseHaveBook && new File(localPath).exists()) {
            openBook(bookDetailBean.name, localPath);
            return;
        }

        if (isWholeBook && DownLoadHelper.isDownloaded(downloadTaskState) && new File(localPath).exists()) {
            openBook(bookDetailBean.name, localPath);
            return;
        }

        if (bookDetailBean.can_read && !bookDetailBean.add_cart) {
            addBookToSmoothCardList(bookDetailBean, true);
            return;
        }

        String path = CommonUtils.getJDBooksPath() + File.separator + bookDetailBean.name;
        if (path.equals(localPath)) {
            if (bookDetailBean != null && DownLoadHelper.isDownloading(downloadTaskState)) {
                ToastUtil.showToast(JDReadApplication.getInstance(), getString(R.string.book_detail_downloading));
                return;
            }
            if (bookDetailBean != null && DownLoadHelper.isPause(downloadTaskState)) {
                BookDownloadUtils.download(bookDetailBean,getShopDataBundle());
                ToastUtil.showToast(JDReadApplication.getInstance(), getString(R.string.book_detail_download_go_on));
                return;
            }
        }

        if (!CommonUtils.isNetworkConnected(JDReadApplication.getInstance())) {
            ManagerActivityUtils.showWifiDialog(getActivity());
            return;
        }

        if (bookDetailBean.can_read || bookDetailBean.isAlreadyBuy) {
            BookDownloadUtils.download(bookDetailBean,getShopDataBundle());
            return;
        }

        if (bookDetailBean.can_buy) {
            showPayDialog(bookDetailBean.ebook_id);
            return;
        }
    }

    private void addBookToSmoothCardList(BookDetailResultBean.DetailBean bookDetailBean, boolean shouldDownLoad) {
        AddBookToSmoothCardAction addBookToSmoothCardAction = new AddBookToSmoothCardAction(bookDetailBean, shouldDownLoad);
        addBookToSmoothCardAction.execute(getShopDataBundle(), new RxCallback() {
            @Override
            public void onNext(Object o) {

            }
        });
    }

    private void openBook(String name, String localPath) {
        DocumentInfo documentInfo = new DocumentInfo();
        documentInfo.setBookPath(localPath);
        documentInfo.setBookName(name);
        OpenBookHelper.openBook(super.getContext(), documentInfo);
    }

    private void showPayDialog(long ebookId){
        ArrayList<String> bookIds = new ArrayList<>();
        bookIds.add(String.valueOf(ebookId));
        final GetOrderUrlAction orderUrlAction = new GetOrderUrlAction(bookIds);
        orderUrlAction.execute(PersonalDataBundle.getInstance(), new RxCallback() {
            @Override
            public void onNext(Object o) {
                GetOrderUrlResultBean orderUrlResultBean = PersonalDataBundle.getInstance().getOrderUrlResultBean();
                if (orderUrlResultBean != null) {
                    String url = CloudApiContext.JD_BOOK_ORDER_URL + CloudApiContext.GotoOrder.ORDER_ORDERSTEP1_ACTION;
                    String tokenKey = CloudApiContext.GotoOrder.TOKENKEY;
                    String payUrl = url + tokenKey + orderUrlResultBean.getTokenKey();
                    PayFragment payFragment = new PayFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.PAY_URL, payUrl);
                    payFragment.setArguments(bundle);
                    payFragment.show(getActivity().getFragmentManager(), "");
                }
            }
        });
    }

    private void addToCart(long ebookId) {
        final AddOrDeleteCartAction addOrDeleteCartAction = new AddOrDeleteCartAction(new String[]{String.valueOf(ebookId)}, Constants.CART_TYPE_ADD);
        addOrDeleteCartAction.execute(getShopDataBundle(), new RxCallback() {
            @Override
            public void onNext(Object o) {
                AddOrDelFromCartBean.ResultBean result = addOrDeleteCartAction.getResult();
                if (result != null){
                    setAddOrDelFromCart(result);
                }
            }
        });
    }

    public void setAddOrDelFromCart(AddOrDelFromCartBean.ResultBean result) {
        gotoShopCartFragment();
    }

    private void gotoShopCartFragment() {
        getViewEventCallBack().gotoView(ShopCartFragment.class.getName());
    }

    private void tryDownload(BookDetailResultBean.DetailBean bookDetailBean) {
        if (bookDetailBean == null) {
            return;
        }
        if (isDataBaseHaveBook && new File(localPath).exists()) {
            openBook(bookDetailBean.name, localPath);
            return;
        }
        if (DownLoadHelper.isDownloaded(downloadTaskState) && new File(localPath).exists()) {
            openBook(bookDetailBean.name, localPath);
            return;
        }
        if (!ViewHelper.isCanNowRead(bookDetailBean)) {
            ToastUtil.showToast(getContext(), getResources().getString(R.string.the_book_unsupported_try_read));
            return;
        }
        if (StringUtils.isNullOrEmpty(bookDetailBean.try_url)) {
            ToastUtil.showToast(getContext(), getResources().getString(R.string.empty_url));
            return;
        }
        if (!CommonUtils.isNetworkConnected(JDReadApplication.getInstance())) {
            ManagerActivityUtils.showWifiDialog(getActivity());
            return;
        }
        if (DownLoadHelper.isDownloading(downloadTaskState)) {
            ToastUtil.showToast(JDReadApplication.getInstance(), getString(R.string.book_detail_downloading));
            return;
        }
        nowReadButton.setEnabled(false);
        nowReadButton.setText(getString(R.string.book_detail_downloading));
        download(bookDetailBean);
        ToastUtil.showToast(JDReadApplication.getInstance(), bookDetailBean.name + getString(R.string.book_detail_tip_book_add_to_bookself));
    }

    private void download(BookDetailResultBean.DetailBean bookDetailBean) {
        String tryDownLoadUrl = bookDetailBean.try_url;
        if (StringUtils.isNullOrEmpty(tryDownLoadUrl)) {
            ToastUtil.showToast(getContext(), getResources().getString(R.string.empty_url));
            return;
        }
        String bookName = tryDownLoadUrl.substring(tryDownLoadUrl.lastIndexOf("/") + 1);
        bookName = bookName.substring(0, bookName.indexOf(Constants.BOOK_FORMAT)) + Constants.BOOK_FORMAT;
        String localPath = CommonUtils.getJDBooksPath() + File.separator + bookName;
        DownloadAction downloadAction = new DownloadAction(getContext(), tryDownLoadUrl, localPath, bookDetailBean.name);
        downloadAction.execute(getShopDataBundle(), new RxCallback() {
            @Override
            public void onNext(Object o) {

            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
            }
        });
    }

    public void setQueryResult(BookExtraInfoBean extraInfoBean) {
        if (extraInfoBean != null) {
            localPath = extraInfoBean.localPath;
            isDataBaseHaveBook = DownLoadHelper.isDownloaded(extraInfoBean.downLoadState);
            isWholeBook = extraInfoBean.isWholeBook;
            if (isWholeBook && isDataBaseHaveBook) {
                nowReadButton.setVisibility(View.GONE);
                buyBookButton.setText(getString(R.string.book_detail_button_now_read));
            }
        }
    }

    public Context getContext() {
        return JDReadApplication.getInstance().getApplicationContext();
    }

    private void showCopyRightDialog() {
        LayoutBookCopyrightBinding copyrightBinding = LayoutBookCopyrightBinding.inflate(LayoutInflater.from(getActivity()), null, false);
        copyrightBinding.setBookDetailViewModel(getBookDetailViewModel());
        if (copyRightDialog == null) {
            AlertDialog.Builder copyRightDialogBuild = new AlertDialog.Builder(getActivity());
            copyRightDialogBuild.setView(copyrightBinding.getRoot());
            copyRightDialogBuild.setCancelable(true);
            copyRightDialog = copyRightDialogBuild.create();
        }
        if (copyRightDialog != null) {
            copyRightDialog.show();
        }
    }

    private void dismissCopyRightDialog() {
        if (copyRightDialog != null && copyRightDialog.isShowing()) {
            copyRightDialog.dismiss();
        }
    }

    public void setBookId(long ebookId) {
        this.ebookId = ebookId;
        queryMetadata();
        getBookDetail();
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
        dismissCopyRightDialog();
        LoginHelper.dismissUserLoginDialog();
        copyRightDialog = null;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoadingDialogEvent(LoadingDialogEvent event) {
        if (isAdded()) {
            showLoadingDialog(getString(event.getResId()));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onHideAllDialogEvent(HideAllDialogEvent event) {
        hideLoadingDialog();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBuyBookSuccessEvent(BuyBookSuccessEvent event) {
        String msg = getString(R.string.buy_book_success) + bookDetailBean.name + getString(R.string.book_detail_tip_book_add_to_bookself);
        ToastUtil.showToast(JDReadApplication.getInstance(),msg);
        addBookToSmoothCardList(bookDetailBean, true);
        changeBuyBookButtonState();
    }

    private void changeBuyBookButtonState() {
        nowReadButton.setVisibility(View.GONE);
        buyBookButton.setEnabled(false);
        buyBookButton.setText(getString(R.string.book_detail_downloading));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserLoginResultEvent(UserLoginResultEvent event) {
        if(JDReadApplication.getInstance().getString(R.string.login_success).equals(event.getMessage())) {
            getBookDetailData();
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
            JDPreferenceManager.setStringValue(Constants.SP_KEY_SEARCH_BOOK_CAT_ID, "");
            JDPreferenceManager.setStringValue(Constants.SP_KEY_KEYWORD, event.keyWord);
            getViewEventCallBack().gotoView(SearchBookListFragment.class.getName());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBookSearchPathEvent(BookSearchPathEvent event) {
        if (getViewEventCallBack() != null) {
            JDPreferenceManager.setStringValue(Constants.SP_KEY_KEYWORD, "");
            JDPreferenceManager.setStringValue(Constants.SP_KEY_SEARCH_BOOK_CAT_ID, event.catId);
            getViewEventCallBack().gotoView(SearchBookListFragment.class.getName());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBookDetailViewInfoEvent(BookDetailViewInfoEvent event) {
        showInfoDialog(event.info);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        hideLoadingDialog();
        dismissCopyRightDialog();
        dismissInfoDialog();
    }

    private void showInfoDialog(String content) {
        if (StringUtils.isNullOrEmpty(content)) {
            return;
        }
        DialogBookInfoBinding infoBinding = DialogBookInfoBinding.inflate(LayoutInflater.from(getActivity()), null, false);
        final DialogBookInfoViewModel dialogBookInfoViewModel = getBookDetailViewModel().getDialogBookInfoViewModel();
        dialogBookInfoViewModel.content.set(content);
        dialogBookInfoViewModel.title.set(JDReadApplication.getInstance().getResources().getString(R.string.book_detail_text_view_content_introduce));
        infoBinding.setViewModel(dialogBookInfoViewModel);
        if (infoDialog == null) {
            AlertDialog.Builder build = new AlertDialog.Builder(getActivity());
            build.setView(infoBinding.getRoot());
            build.setCancelable(true);
            AutoPagedWebView pagedWebView = infoBinding.bookInfoWebView;
            WebSettings settings = pagedWebView.getSettings();
            settings.setSupportZoom(true);
            settings.setTextZoom(Constants.WEB_VIEW_TEXT_ZOOM);
            pagedWebView.setPageChangedListener(new AutoPagedWebView.PageChangedListener() {
                @Override
                public void onPageChanged(int currentPage, int totalPage) {
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
            infoDialog = build.create();
        }
        if (infoDialog != null) {
            infoDialog.show();
        }
    }

    private void dismissInfoDialog() {
        if (infoDialog != null && infoDialog.isShowing()) {
            infoDialog.dismiss();
        }
    }
}
