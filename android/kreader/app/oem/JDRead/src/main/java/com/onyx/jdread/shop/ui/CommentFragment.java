package com.onyx.jdread.shop.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.data.GPaginator;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.DialogBookInfoBinding;
import com.onyx.jdread.databinding.FragmentBookCommentBinding;
import com.onyx.jdread.library.view.DashLineItemDivider;
import com.onyx.jdread.main.common.BaseFragment;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.reader.ui.view.PageTextView;
import com.onyx.jdread.shop.action.BookCommentListAction;
import com.onyx.jdread.shop.adapter.BookCommentsAdapter;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookCommentsResultBean;
import com.onyx.jdread.shop.common.PageTagConstants;
import com.onyx.jdread.shop.event.BookDetailViewInfoEvent;
import com.onyx.jdread.shop.event.HideAllDialogEvent;
import com.onyx.jdread.shop.event.LoadingDialogEvent;
import com.onyx.jdread.shop.event.TopBackEvent;
import com.onyx.jdread.shop.event.TopRightTitleEvent;
import com.onyx.jdread.shop.model.BookDetailViewModel;
import com.onyx.jdread.shop.model.DialogBookInfoViewModel;
import com.onyx.jdread.shop.model.ShopDataBundle;
import com.onyx.jdread.shop.utils.ViewHelper;
import com.onyx.jdread.shop.view.BookInfoDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static com.onyx.jdread.main.common.Constants.PAGE_STEP;

/**
 * Created by jackdeng on 2017/12/18.
 */

public class CommentFragment extends BaseFragment {

    private FragmentBookCommentBinding bookCommentBinding;
    private int bookDetailSpace = ResManager.getInteger(R.integer.book_detail_recycle_view_space);
    private DashLineItemDivider itemDecoration;
    private long ebookId;
    private PageRecyclerView recyclerViewComments;
    private int currentPage = 1;
    private GPaginator paginator;
    private BookInfoDialog infoDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        bookCommentBinding = FragmentBookCommentBinding.inflate(inflater, container, false);
        initView();
        initLibrary();
        initData();
        return bookCommentBinding.getRoot();
    }

    private void initLibrary() {
        if (!getEventBus().isRegistered(this)) {
            getEventBus().register(this);
        }
    }

    private void initData() {
        resetCurrentPage();
        Bundle bundle = getBundle();
        if (bundle != null) {
            ebookId = bundle.getLong(Constants.SP_KEY_BOOK_ID, 0);
            getBookComments();
        }
    }

    private void getBookComments() {
        getCommentsData();
    }

    private void initView() {
        bookCommentBinding.setBookDetailViewModel(getBookDetailViewModel());
        getBookDetailViewModel().getTitleBarViewModel().leftText = getString(R.string.title_bar_title_book_comment);
        getBookDetailViewModel().getTitleBarViewModel().pageTag = PageTagConstants.BOOK_COMMENT;
        initDividerItemDecoration();
        setCommentsRecycleView();
        checkWifi(getBookDetailViewModel().getTitleBarViewModel().leftText);
    }

    private void setCommentsRecycleView() {
        BookCommentsAdapter adapter = new BookCommentsAdapter(getEventBus());
        recyclerViewComments = bookCommentBinding.recyclerViewComments;
        recyclerViewComments.setPageTurningCycled(true);
        recyclerViewComments.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
        recyclerViewComments.addItemDecoration(itemDecoration);
        recyclerViewComments.setAdapter(adapter);
        paginator = recyclerViewComments.getPaginator();
        recyclerViewComments.setOnPagingListener(new PageRecyclerView.OnPagingListener() {
            @Override
            public void onPageChange(int position, int itemCount, int pageSize) {
                if (paginator != null) {
                    setCurrentPage(paginator.getCurrentPage());
                }
            }
        });
    }

    private void setCurrentPage(int currentPage) {
        getBookDetailViewModel().setCurrentPage(currentPage + PAGE_STEP);
    }

    private int getCurrentPage() {
        return getBookDetailViewModel().getCurrentPage() - PAGE_STEP;
    }

    private void resetCurrentPage() {
        getBookDetailViewModel().setCurrentPage(0);
    }

    private int getValidContentPage() {
        int page = getCurrentPage();
        if (page >= paginator.pages()) {
            return 0;
        }
        return page;
    }

    private void initDividerItemDecoration() {
        itemDecoration = new DashLineItemDivider();
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

    private void getCommentsData() {
        BookCommentListAction commentListAction = new BookCommentListAction(ebookId, currentPage);
        commentListAction.execute(getShopDataBundle(), new RxCallback<BookCommentListAction>() {
            @Override
            public void onNext(BookCommentListAction action) {
                BookCommentsResultBean.DataBean dataBean = action.getbookCommentsBean();
                updateContentView(dataBean);
                if (dataBean.comments.size() > 0) {
                    bookCommentBinding.noComments.setVisibility(View.GONE);
                }
            }
        });
    }

    private void updateContentView(BookCommentsResultBean.DataBean resultBean) {
        if (recyclerViewComments == null) {
            return;
        }
        initPageIndicator(resultBean);
        gotoPage(getValidContentPage());
    }

    private void initPageIndicator(BookCommentsResultBean.DataBean resultBean) {
        int size = resultBean == null ? 0 : CollectionUtils.getSize(resultBean.comments);
        recyclerViewComments.resize(recyclerViewComments.getPageAdapter().getRowCount(),
                recyclerViewComments.getPageAdapter().getColumnCount(), size);
        getBookDetailViewModel().setTotalPage(paginator.pages());
        setCurrentPage(paginator.getCurrentPage());
    }

    private void gotoPage(int page) {
        if (recyclerViewComments == null) {
            return;
        }
        recyclerViewComments.gotoPage(page);
    }

    public ShopDataBundle getShopDataBundle() {
        return ShopDataBundle.getInstance();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBookDetailTopBackEvent(TopBackEvent event) {
        if (isInfoDialogShowing()) {
            return;
        }
        if (getViewEventCallBack() != null) {
            getViewEventCallBack().viewBack();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBookDetailTopRightEvent(TopRightTitleEvent event) {

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
    public void onBookDetailViewInfoEvent(BookDetailViewInfoEvent event) {
        showInfoDialog(event.info);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        hideLoadingDialog();
        dismissInfoDialog();
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
        dialogBookInfoViewModel.title.set(ResManager.getString(R.string.book_comment_detail));
        infoBinding.setViewModel(dialogBookInfoViewModel);
        infoDialog = new BookInfoDialog(JDReadApplication.getInstance());
        infoDialog.setView(infoBinding.getRoot());
        infoDialog.setCancelable(false);
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
        if (infoDialog != null && infoDialog.isShowing()) {
            infoDialog.dismiss();
            infoDialog = null;
        }
    }

    private boolean isInfoDialogShowing() {
        return infoDialog != null && infoDialog.isShowing();
    }
}