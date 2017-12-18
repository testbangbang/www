package com.onyx.jdread.shop.ui;

import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.onyx.jdread.shop.action.StoreBookDetailAction;
import com.onyx.jdread.shop.action.StoreBookRecommendListAction;
import com.onyx.jdread.shop.adapter.RecommendAdapter;
import com.onyx.jdread.shop.cloud.entity.jdbean.ResultBookBean;
import com.onyx.jdread.shop.event.OnRecommendItemClickEvent;
import com.onyx.jdread.shop.event.OnRecommendNextPageEvent;
import com.onyx.jdread.shop.model.BookDetailViewModel;
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
    private int ebookId;
    private PageRecyclerView recyclerViewRecommend;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        bookDetailBinding = FragmentBookDetailBinding.inflate(inflater, container, false);
        initView();
        initData();
        return bookDetailBinding.getRoot();
    }

    private void initData() {
        ebookId = PreferenceManager.getIntValue(JDReadApplication.getInstance(), Constants.SP_KEY_BOOK_ID, 0);
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
        bookDetailBinding.bookDetailInfo.bookDetailType1.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        bookDetailBinding.bookDetailInfo.bookDetailType2.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        initDividerItemDecoration();
        setRecommendRecycleView();
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
        return JDReadApplication.getStoreDataBundle().getBookDetailViewModel();
    }

    private EventBus getEventBus() {
        return JDReadApplication.getStoreDataBundle().getEventBus();
    }

    private void getBookDetailData() {
        StoreBookDetailAction storeBookDetailAction = new StoreBookDetailAction(ebookId);
        storeBookDetailAction.execute(JDReadApplication.getStoreDataBundle(), new RxCallback() {
            @Override
            public void onNext(Object o) {

            }
        });
    }

    private void getRecommendData() {
        StoreBookRecommendListAction recommendListAction = new StoreBookRecommendListAction(ebookId);
        recommendListAction.execute(JDReadApplication.getStoreDataBundle(), new RxCallback() {
            @Override
            public void onNext(Object o) {

            }
        });
    }

    private boolean isCurrentViewVisible(View view) {
        int visibility = view.getVisibility();
        if (visibility == View.VISIBLE) {
            return true;
        }
        return false;
    }

    private void setCurrent(int id, int position) {

    }

    private void visible(int id) {

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

    public void setBookId(int ebookId) {
        this.ebookId = ebookId;
        getBookDetail();
    }
}