package com.onyx.jdread.shop.ui;

import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.data.GPaginator;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.utils.PreferenceManager;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.common.BaseFragment;
import com.onyx.jdread.common.Constants;
import com.onyx.jdread.databinding.FragmentBookCommentBinding;
import com.onyx.jdread.shop.action.BookCommentListAction;
import com.onyx.jdread.shop.adapter.BookCommentsAdapter;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookCommentsResultBean;
import com.onyx.jdread.shop.common.PageTagConstants;
import com.onyx.jdread.shop.event.OnTopBackEvent;
import com.onyx.jdread.shop.event.OnTopRightEvent;
import com.onyx.jdread.shop.model.BookDetailViewModel;
import com.onyx.jdread.shop.model.ShopDataBundle;
import com.onyx.jdread.shop.view.DividerItemDecoration;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static com.onyx.jdread.common.Constants.PAGE_STEP;

/**
 * Created by jackdeng on 2017/12/18.
 */

public class CommentFragment extends BaseFragment {

    private FragmentBookCommentBinding bookCommentBinding;
    private int bookDetailSpace = JDReadApplication.getInstance().getResources().getInteger(R.integer.book_detail_recycle_view_space);
    private DividerItemDecoration itemDecoration;
    private long ebookId;
    private PageRecyclerView recyclerViewComments;
    private int currentPage = 1;
    private GPaginator paginator;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        bookCommentBinding = FragmentBookCommentBinding.inflate(inflater, container, false);
        initView();
        initData();
        return bookCommentBinding.getRoot();
    }

    private void initData() {
        ebookId = PreferenceManager.getLongValue(JDReadApplication.getInstance(), Constants.SP_KEY_BOOK_ID, 0);
        getBookComments();
    }

    private void getBookComments() {
        getCommentsData();
    }

    private void initView() {
        bookCommentBinding.setBookDetailViewModel(getBookDetailViewModel());
        getBookDetailViewModel().getTitleBarViewModel().leftText = getString(R.string.title_bar_title_book_comment);
        getBookDetailViewModel().getTitleBarViewModel().pageTag = PageTagConstants.BOOK_COMMENT;
        getBookDetailViewModel().getTitleBarViewModel().showRightText = true;
        getBookDetailViewModel().getTitleBarViewModel().rightText = getString(R.string.title_bar_title_book_write_comment);
        bookCommentBinding.layoutTitleBar.titleBarRightTitle.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        initDividerItemDecoration();
        setCommentsRecycleView();
    }

    private void setCommentsRecycleView() {
        BookCommentsAdapter adapter = new BookCommentsAdapter(getEventBus());
        recyclerViewComments = bookCommentBinding.recyclerViewComments;
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

    private void initDividerItemDecoration() {
        itemDecoration = new DividerItemDecoration(JDReadApplication.getInstance(), DividerItemDecoration.VERTICAL_LIST);
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

    private void getCommentsData() {
        BookCommentListAction commnetListAction = new BookCommentListAction(ebookId, currentPage);
        commnetListAction.execute(getShopDataBundle(), new RxCallback<BookCommentListAction>() {
            @Override
            public void onNext(BookCommentListAction action) {
                BookCommentsResultBean resultBean = action.getbookCommentsBean();
                if (resultBean != null && resultBean.getReviews() != null && resultBean.getReviews().getList() != null) {
                    initPageIndicator(resultBean);
                }
            }
        });
    }

    private void initPageIndicator(BookCommentsResultBean resultBean) {
        int size = resultBean.getReviews().getList().size();
        recyclerViewComments.resize(recyclerViewComments.getPageAdapter().getRowCount(), recyclerViewComments.getPageAdapter().getColumnCount(), size);
        getBookDetailViewModel().setTotalPage(paginator.pages());
        setCurrentPage(paginator.getCurrentPage());
    }

    public ShopDataBundle getShopDataBundle() {
        return ShopDataBundle.getInstance();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBookDetailTopBackEvent(OnTopBackEvent event) {
        if (getViewEventCallBack() != null) {
            getViewEventCallBack().viewBack();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBookDetailTopRightEvent(OnTopRightEvent event) {

    }
}