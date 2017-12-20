package com.onyx.jdread.shop.ui;

import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.utils.PreferenceManager;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.common.BaseFragment;
import com.onyx.jdread.common.Constants;
import com.onyx.jdread.databinding.FragmentBookDetailBinding;
import com.onyx.jdread.databinding.LayoutBookCopyrightBinding;
import com.onyx.jdread.shop.action.BookDetailAction;
import com.onyx.jdread.shop.action.BookRecommendListAction;
import com.onyx.jdread.shop.adapter.RecommendAdapter;
import com.onyx.jdread.shop.cloud.entity.jdbean.ResultBookBean;
import com.onyx.jdread.shop.common.PageTagConstants;
import com.onyx.jdread.shop.event.OnBookDetailTopBackEvent;
import com.onyx.jdread.shop.event.OnCopyrightCancelEvent;
import com.onyx.jdread.shop.event.OnCopyrightEvent;
import com.onyx.jdread.shop.event.OnRecommendItemClickEvent;
import com.onyx.jdread.shop.event.OnRecommendNextPageEvent;
import com.onyx.jdread.shop.event.OnViewCommentEvent;
import com.onyx.jdread.shop.model.BookDetailViewModel;
import com.onyx.jdread.shop.model.ShopDataBundle;
import com.onyx.jdread.shop.view.DividerItemDecoration;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        bookDetailBinding = FragmentBookDetailBinding.inflate(inflater, container, false);
        initView();
        initData();
        return bookDetailBinding.getRoot();
    }

    private void initData() {
        ebookId = PreferenceManager.getLongValue(JDReadApplication.getInstance(), Constants.SP_KEY_BOOK_ID, 0);
        getBookDetail();
    }

    private void getBookDetail() {
        getBookDetailData();
        getRecommendData();
    }

    private void initView() {
        bookDetailBinding.setBookDetailViewModel(getBookDetailViewModel());
        bookDetailBinding.bookDetailInfo.bookDetailAuthor.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        bookDetailBinding.bookDetailInfo.bookDetailYuedouPriceOld.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        bookDetailBinding.bookDetailInfo.bookDetailCategoryPath.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
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