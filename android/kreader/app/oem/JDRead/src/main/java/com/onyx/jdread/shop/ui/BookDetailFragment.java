package com.onyx.jdread.shop.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.onyx.android.sdk.data.OnyxDownloadManager;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.utils.JSONObjectParseUtils;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.utils.PreferenceManager;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.common.BaseFragment;
import com.onyx.jdread.common.CommonUtils;
import com.onyx.jdread.common.Constants;
import com.onyx.jdread.common.ManagerActivityUtils;
import com.onyx.jdread.common.ToastUtil;
import com.onyx.jdread.databinding.FragmentBookDetailBinding;
import com.onyx.jdread.databinding.LayoutBookCopyrightBinding;
import com.onyx.jdread.shop.action.BookDetailAction;
import com.onyx.jdread.shop.action.BookRecommendListAction;
import com.onyx.jdread.shop.action.BookshelfInsertAction;
import com.onyx.jdread.shop.action.DownloadAction;
import com.onyx.jdread.shop.action.MetadataQueryAction;
import com.onyx.jdread.shop.adapter.RecommendAdapter;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookDetailResultBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookExtraInfoBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.ResultBookBean;
import com.onyx.jdread.shop.common.PageTagConstants;
import com.onyx.jdread.shop.event.BookShelfEvent;
import com.onyx.jdread.shop.event.DownloadingEvent;
import com.onyx.jdread.shop.event.OnBookDetailReadNowEvent;
import com.onyx.jdread.shop.event.OnBookDetailTopBackEvent;
import com.onyx.jdread.shop.event.OnCopyrightCancelEvent;
import com.onyx.jdread.shop.event.OnCopyrightEvent;
import com.onyx.jdread.shop.event.OnRecommendItemClickEvent;
import com.onyx.jdread.shop.event.OnRecommendNextPageEvent;
import com.onyx.jdread.shop.event.OnViewCommentEvent;
import com.onyx.jdread.shop.event.ShopSmoothCardEvent;
import com.onyx.jdread.shop.model.BookDetailViewModel;
import com.onyx.jdread.shop.model.ShopDataBundle;
import com.onyx.jdread.shop.utils.DownLoadHelper;
import com.onyx.jdread.shop.view.CustomDialog;
import com.onyx.jdread.shop.view.DividerItemDecoration;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

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
    private BookDetailResultBean.Detail bookDetailBean;
    private int downloadTaskState;
    private TextView buyBookButton;
    private TextView nowReadButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        bookDetailBinding = FragmentBookDetailBinding.inflate(inflater, container, false);
        initView();
        initData();
        return bookDetailBinding.getRoot();
    }

    private void initData() {
        cleanData();
        ebookId = PreferenceManager.getLongValue(JDReadApplication.getInstance(), Constants.SP_KEY_BOOK_ID, 0);
        getBookDetail();
    }

    private void getBookDetail() {
        queryMetadata();
        getBookDetailData();
        getRecommendData();
    }

    private void queryMetadata() {
        MetadataQueryAction metadataQueryAction = new MetadataQueryAction("");
        metadataQueryAction.execute(getShopDataBundle(), new RxCallback<MetadataQueryAction>() {
            @Override
            public void onNext(MetadataQueryAction queryAction) {
                Metadata metadata = queryAction.getMetadataResult();
                String extraInfoStr = metadata.getExtraAttributes();
                BookExtraInfoBean extraInfoBean = JSONObjectParseUtils.toBean(extraInfoStr,BookExtraInfoBean.class);
                setQueryResult(extraInfoBean);
            }
        });
    }

    private void cleanData() {
        isTryRead = false;
        isSmoothRead = false;
        ebookId = 0;
        downloadTaskState = 0;
        localPath = "";
    }

    private void initView() {
        bookDetailBinding.setBookDetailViewModel(getBookDetailViewModel());
        bookDetailBinding.bookDetailInfo.bookDetailAuthor.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        bookDetailBinding.bookDetailInfo.bookDetailYuedouPriceOld.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        bookDetailBinding.bookDetailInfo.bookDetailCategoryPath.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        nowReadButton = bookDetailBinding.bookDetailInfo.bookDetailNowRead;
        buyBookButton = bookDetailBinding.bookDetailInfo.bookDetailBuyBook;
        initDividerItemDecoration();
        setRecommendRecycleView();
        getBookDetailViewModel().setTitle(getString(R.string.title_bar_title_book_detail));
        getBookDetailViewModel().setPageTag(PageTagConstants.BOOK_DETAIL);
        getBookDetailViewModel().setShowRightText(false);
    }

    private void setRecommendRecycleView() {
        RecommendAdapter adapter = new RecommendAdapter(getEventBus());
        recyclerViewRecommend = bookDetailBinding.bookDetailInfo.recyclerViewRecommend;
        recyclerViewRecommend.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
        recyclerViewRecommend.addItemDecoration(itemDecoration);
        recyclerViewRecommend.setAdapter(adapter);
    }

    private void initDividerItemDecoration() {
        itemDecoration = new DividerItemDecoration(JDReadApplication.getInstance(), DividerItemDecoration.HORIZONTAL_LIST);
        itemDecoration.setDrawLine(false);
        itemDecoration.setSpace(bookDetailSpace);
    }

    @Override
    public void onResume() {
        super.onResume();
        getEventBus().register(this);
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
        bookDetailAction.execute(getShopDataBundle(), new RxCallback() {
            @Override
            public void onNext(Object o) {

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
    public void onRecommendItemClickEvent(OnRecommendItemClickEvent event) {
        ResultBookBean bookBean = event.getBookBean();
        cleanData();
        setBookId(bookBean.ebookId);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRecommendNextPageEvent(OnRecommendNextPageEvent event) {
        recyclerViewRecommend.nextPage();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBookDetailTopBackEvent(OnBookDetailTopBackEvent event) {
        if (getViewEventCallBack() != null) {
            getViewEventCallBack().viewBack();
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onViewCommentEvent(OnViewCommentEvent event) {
        if (getViewEventCallBack() != null) {
            getViewEventCallBack().gotoView(CommentFragment.class.getName());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCopyrightEvent(OnCopyrightEvent event) {
        showCopyRightDialog();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCopyrightCancelEvent(OnCopyrightCancelEvent event) {
        dismissCopyRightDialog();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBookDetailReadNowEvent(OnBookDetailReadNowEvent event) {
        bookDetailBean = event.getBookDetailBean();
        BookExtraInfoBean extraInfoBean = new BookExtraInfoBean();
        extraInfoBean.isWholeBook = bookDetailBean.isTryRead();
        bookDetailBean.setBookExtraInfoBean(extraInfoBean);
        tryDownload(bookDetailBean);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDownloadingEvent(DownloadingEvent event) {
        BaseDownloadTask task = OnyxDownloadManager.getInstance().getTask(event.tag);
        if (task != null) {
            int state = task.getStatus();
            bookDetailBean.getBookExtraInfoBean().downLoadstate = state;
            if(DownLoadHelper.isDownloaded(state)){
                bookDetailBean.getBookExtraInfoBean().percentage = DownLoadHelper.DOWNLOAD_PERCENT_FINISH;
            } else {
                bookDetailBean.getBookExtraInfoBean().percentage = (int) event.progressInfo.progress;
            }
            downloadTaskState = state;
            bookDetailBean.getBookExtraInfoBean().localPath = task.getPath();
            localPath = task.getPath();
            if (DownLoadHelper.canInsertBookDetail(state)) {
                insertBookDetail(bookDetailBean, localPath);
            }
            upDataButton();
        }
    }

    private void upDataButton() {
        if (bookDetailBean == null) {
            return;
        }

        if (bookDetailBean.getBookExtraInfoBean().isWholeBook) {
            buttonDownloadState(buyBookButton,  bookDetailBean.getBookExtraInfoBean().percentage + "%" + getString(R.string.book_detail_downloading),
                    downloadTaskState, getString(R.string.fragment_book_bought_download_status_read), null);
        } else {
            buttonDownloadState(nowReadButton, bookDetailBean.getBookExtraInfoBean().percentage + "%" + getString(R.string.book_detail_downloading),
                    downloadTaskState, null, getString(R.string.book_detail_button_try_read));
        }
    }

    private void buttonDownloadState(TextView button, String percentage, int state, String smoothRead, String tryRead) {
        button.setText(percentage);
        if (DownLoadHelper.isDownloaded(state)) {
            ToastUtil.showToast(getContext(),getString(R.string.book_detail_tip_Success));
            button.setText(StringUtils.isNullOrEmpty(smoothRead) ? tryRead : smoothRead);
        } else if(DownLoadHelper.isError(state)){
            ToastUtil.showToast(getContext(),getString(R.string.book_detail_tip_try_again));
            button.setText(getString(R.string.book_detail_button_try_again));
        }
    }

    private void insertBookDetail(BookDetailResultBean.Detail bookDetailBean, String localPath) {
        BookshelfInsertAction insertAction = new BookshelfInsertAction(bookDetailBean, localPath);
        insertAction.execute(getShopDataBundle(), new RxCallback() {
            @Override
            public void onNext(Object o) {

            }
        });
    }

    private void tryDownload(BookDetailResultBean.Detail bookDetailBean) {
        if (!CommonUtils.isNetworkConnected(JDReadApplication.getInstance())) {
            ManagerActivityUtils.showWifiDialog(getActivity());
            return;
        }

        String strNowRead = nowReadButton.getText().toString();
        if (!getString(R.string.book_detail_button_now_read).equals(strNowRead)) {
            download(bookDetailBean);
            ToastUtil.showToast(getContext(), getString(R.string.book_detail_download_go_on));
            return;
        }

        if (bookDetailBean == null) {
            return;
        }

        if (isTryRead && DownLoadHelper.isDownloaded(downloadTaskState) && new File(localPath).exists()) {
            insertBookDetail(bookDetailBean, localPath);
            return;
        }

        if (bookDetailBean != null && DownLoadHelper.isDownloaded(downloadTaskState) && new File(localPath).exists()) {
            insertBookDetail(bookDetailBean, localPath);
            return;
        }

        if (bookDetailBean != null && DownLoadHelper.isDownloading(downloadTaskState)) {
            ToastUtil.showToast(JDReadApplication.getInstance(), getString(R.string.book_detail_downloading));
            return;
        }

        if (StringUtils.isNullOrEmpty(bookDetailBean.getTryDownLoadUrl())) {
            ToastUtil.showToast(getContext(), getResources().getString(R.string.empty_url));
            return;
        }

        download(bookDetailBean);
        showDownloadAfterGoDialog(getString(R.string.book_detail_try_read), getString(R.string.book_detail_download_add_shelf),
                getString(R.string.book_detail_go_shelf));
    }

    private void showDownloadAfterGoDialog(String title, String message, final String positiveText) {
        CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());
        builder.setTitle(title).setMessage(message)
                .setPositiveButton(positiveText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (getString(R.string.book_detail_go_shelf).equals(positiveText)) {
                            EventBus.getDefault().post(new BookShelfEvent());
                        } else {
                            EventBus.getDefault().post(new ShopSmoothCardEvent());
                        }
                    }
                })
                .setNegativeButton(getString(R.string.book_detail_stay), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
    }

    private void download(BookDetailResultBean.Detail bookDetailBean) {
        String tryDownLoadUrl = bookDetailBean.getTryDownLoadUrl();
        if (StringUtils.isNullOrEmpty(tryDownLoadUrl)) {
            ToastUtil.showToast(getContext(), getResources().getString(R.string.empty_url));
            return;
        }
        String bookName = tryDownLoadUrl.substring(tryDownLoadUrl.lastIndexOf("/") + 1);
        bookName = bookName.substring(0, bookName.indexOf(Constants.BOOK_FORMAT)) + Constants.BOOK_FORMAT;
        String localPath = CommonUtils.getJDBooksPath() + File.separator + bookName;
        DownloadAction downloadAction = new DownloadAction(getContext(), tryDownLoadUrl, localPath, bookDetailBean.getBookName());
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
        isTryRead = false;
        isSmoothRead = false;
        localPath = "";

        if (extraInfoBean != null) {
            if (extraInfoBean.isWholeBook) {
                isSmoothRead = true;
                TextView smoothRead = buyBookButton;
                setButtonState(extraInfoBean, getString(R.string.fragment_book_bought_download_status_read), smoothRead);
                smoothRead.setEnabled(true);
            } else {
                isTryRead = true;
                TextView nowRead = nowReadButton;
                nowRead.setEnabled(true);
                setButtonState(extraInfoBean, getString(R.string.book_detail_button_try_read), nowRead);
            }
        }
    }

    private void setButtonState(BookExtraInfoBean extraInfoBean, String text, TextView button) {
        if (DownLoadHelper.isDownloaded(extraInfoBean.downLoadstate)) {
            button.setText(text);
            if (!isTryRead) {
                nowReadButton.setEnabled(false);
            }
        } else {
            double percentage = extraInfoBean.percentage;
            if(percentage == DownLoadHelper.DOWNLOAD_PERCENT_FINISH){
                button.setText(getString(R.string.book_detail_button_now_read));
            } else {
                button.setText(percentage + "%" + getString(R.string.book_detail_downloading));
            }
        }
    }

    public Context getContext() {
        return JDReadApplication.getInstance().getApplicationContext();
    }

    private void showCopyRightDialog() {
        if (copyRightDialog == null) {
            AlertDialog.Builder copyRightDialogBuild = new AlertDialog.Builder(getActivity());
            LayoutBookCopyrightBinding copyrightBinding = LayoutBookCopyrightBinding.inflate(LayoutInflater.from(getActivity()), null, false);
            copyrightBinding.setBookDetailViewModel(getBookDetailViewModel());
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
        getBookDetail();
    }

    public ShopDataBundle getShopDataBundle() {
        return ShopDataBundle.getInstance();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        dismissCopyRightDialog();
        copyRightDialog = null;
    }
}