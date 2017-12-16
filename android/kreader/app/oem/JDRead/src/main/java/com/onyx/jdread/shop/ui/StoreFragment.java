package com.onyx.jdread.shop.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.common.BaseFragment;
import com.onyx.jdread.databinding.FragmentBookStoreBinding;
import com.onyx.jdread.databinding.FragmentBookStoreOneBinding;
import com.onyx.jdread.databinding.FragmentBookStoreThreeBinding;
import com.onyx.jdread.databinding.FragmentBookStoreTwoBinding;
import com.onyx.jdread.shop.action.StoreCategoryAction;
import com.onyx.jdread.shop.action.StoreFreeJournalAction;
import com.onyx.jdread.shop.action.StoreNewBookAction;
import com.onyx.jdread.shop.adapter.BannerSubjectAdapter;
import com.onyx.jdread.shop.adapter.CategorySubjectAdapter;
import com.onyx.jdread.shop.adapter.SubjectAdapter;
import com.onyx.jdread.shop.event.OnRankViewClick;
import com.onyx.jdread.shop.event.OnStoreBakcTopClick;
import com.onyx.jdread.shop.model.BookStoreViewModel;
import com.onyx.jdread.shop.view.DividerItemDecoration;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by huxiaomao on 2017/12/7.
 */

public class StoreFragment extends BaseFragment {

    private static final int SCROLL_TOTAL = 3;
    private static final int SCROLL_ONE = 0;
    private static final int SCROLL_TWO = 1;
    private static final int SCROLL_THREE = 2;
    private static final int FLING_MIN_DISTANCE = 30;
    private FragmentBookStoreBinding bookStoreBinding;
    private GestureDetector gestureDetector;
    private FragmentBookStoreOneBinding storeOneBinding;
    private FragmentBookStoreTwoBinding storeTwoBinding;
    private FragmentBookStoreThreeBinding storeThreeBinding;
    private int bannerSpace = JDReadApplication.getInstance().getResources().getInteger(R.integer.store_banner_recycle_view_space);
    private DividerItemDecoration itemDecoration;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        bookStoreBinding = FragmentBookStoreBinding.inflate(inflater, container, false);
        initView();
        initData();
        return bookStoreBinding.getRoot();
    }

    private void initData() {
        getRecyclerViewSubjectOneData();
        getRecyclerViewSubjectTwoData();
        getRecyclerViewCategoryData();
    }

    private void initView() {
        bookStoreBinding.setView(this);
        bookStoreBinding.scrollBar.setTotal(SCROLL_TOTAL);
        storeOneBinding = bookStoreBinding.bookStoreOne;
        storeTwoBinding = bookStoreBinding.bookStoreTwo;
        storeThreeBinding = bookStoreBinding.bookStoreThree;
        bookStoreBinding.setViewModel(getBookStoreViewModel());
        initDividerItemDecoration();
        setRecyclerViewBanner();
        setRecyclerViewCoverSubjectOne();
        setRecyclerViewCoverSubjectTwoBackUp();
        setRecyclerViewCoverSubjectTwo();
        setRecyclerViewCoverSubjectFourBackup();
        setRecyclerViewCoverSubjectFour();
        setRecyclerViewCategory();
    }

    private void initDividerItemDecoration() {
        itemDecoration = new DividerItemDecoration(JDReadApplication.getInstance(), DividerItemDecoration.HORIZONTAL_LIST);
        itemDecoration.setDrawLine(false);
        itemDecoration.setSpace(bannerSpace);
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

    private BookStoreViewModel getBookStoreViewModel() {
        return JDReadApplication.getStoreDataBundle().getStoreViewModel();
    }

    private EventBus getEventBus() {
        return JDReadApplication.getStoreDataBundle().getEventBus();
    }

    public GestureDetector getGestureDetector() {
        if (gestureDetector == null) {
            gestureDetector = new GestureDetector(getActivity(), gestureListener);
        }
        return gestureDetector;
    }

    private GestureDetector.OnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float y_axis = e1.getY() - e2.getY();
            if (isCurrentViewVisible(storeOneBinding.getRoot()) && y_axis > FLING_MIN_DISTANCE) {
                setCurrent(R.id.book_store_two, SCROLL_TWO);
            } else if (isCurrentViewVisible(storeTwoBinding.getRoot()) && y_axis > FLING_MIN_DISTANCE) {
                setCurrent(R.id.book_store_three, SCROLL_THREE);
            } else if (isCurrentViewVisible(storeTwoBinding.getRoot()) && -(y_axis) > FLING_MIN_DISTANCE) {
                setCurrent(R.id.book_store_one, SCROLL_ONE);
            } else if (isCurrentViewVisible(storeThreeBinding.getRoot()) && -(y_axis) > FLING_MIN_DISTANCE) {
                setCurrent(R.id.book_store_two, SCROLL_TWO);
            }
            return false;
        }
    };

    private boolean isCurrentViewVisible(View view) {
        int visibility = view.getVisibility();
        if (visibility == View.VISIBLE) {
            return true;
        }
        return false;
    }

    private void setCurrent(int id, int position) {
        visible(id);
        bookStoreBinding.scrollBar.setFocusPosition(position);
    }

    private void visible(int id) {
        storeOneBinding.getRoot().setVisibility(id == R.id.book_store_one ? View.VISIBLE : View.GONE);
        storeTwoBinding.getRoot().setVisibility(id == R.id.book_store_two ? View.VISIBLE : View.GONE);
        storeThreeBinding.getRoot().setVisibility(id == R.id.book_store_three ? View.VISIBLE : View.GONE);
    }

    private void setRecyclerViewBanner() {
        BannerSubjectAdapter adapter = new BannerSubjectAdapter();
        PageRecyclerView recyclerView = storeOneBinding.recyclerViewBanner;
        recyclerView.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setAdapter(adapter);
    }

    private void setRecyclerViewCoverSubjectOne() {
        SubjectAdapter recyclerViewOneAdapter = new SubjectAdapter();
        PageRecyclerView recyclerViewOne = storeOneBinding.recyclerViewCoverSubjectOne;
        recyclerViewOne.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
        recyclerViewOne.addItemDecoration(itemDecoration);
        recyclerViewOne.setAdapter(recyclerViewOneAdapter);
    }

    private void setRecyclerViewCoverSubjectTwoBackUp() {
        SubjectAdapter recyclerViewTwoAdapter = new SubjectAdapter();
        PageRecyclerView recyclerViewTwo = storeOneBinding.recyclerViewCoverSubjectTwoBackUp;
        recyclerViewTwo.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
        recyclerViewTwo.addItemDecoration(itemDecoration);
        recyclerViewTwo.setAdapter(recyclerViewTwoAdapter);
    }

    private void setRecyclerViewCoverSubjectTwo() {
        SubjectAdapter adapter = new SubjectAdapter();
        PageRecyclerView recyclerView = storeTwoBinding.recyclerViewCoverSubjectTwo;
        recyclerView.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setAdapter(adapter);
    }

    private void setRecyclerViewCategory() {
        CategorySubjectAdapter adapter = new CategorySubjectAdapter();
        PageRecyclerView recyclerView = storeTwoBinding.recyclerViewCategorySubject;
        recyclerView.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
        recyclerView.setAdapter(adapter);
    }

    private void setRecyclerViewCoverSubjectFourBackup() {
        SubjectAdapter adapter = new SubjectAdapter();
        PageRecyclerView recyclerView = storeTwoBinding.recyclerViewCoverSubjectFourBackup;
        recyclerView.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
        recyclerView.setAdapter(adapter);
    }

    private void setRecyclerViewCoverSubjectFour() {
        SubjectAdapter adapter = new SubjectAdapter();
        PageRecyclerView recyclerView = storeThreeBinding.recyclerViewCoverSubjectFour;
        recyclerView.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setAdapter(adapter);
    }

    private void getRecyclerViewSubjectOneData() {
        StoreNewBookAction storeNewBookAction = new StoreNewBookAction(JDReadApplication.getInstance());
        storeNewBookAction.execute(JDReadApplication.getStoreDataBundle(), new RxCallback() {
            @Override
            public void onNext(Object o) {

            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);

            }
        });
    }

    public void getRecyclerViewSubjectTwoData() {
        StoreFreeJournalAction storeNewBookAction = new StoreFreeJournalAction(JDReadApplication.getInstance());
        storeNewBookAction.execute(JDReadApplication.getStoreDataBundle(), new RxCallback() {
            @Override
            public void onNext(Object o) {

            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
            }
        });
    }

    private void getRecyclerViewCategoryData() {
        StoreCategoryAction storeCategoryAction = new StoreCategoryAction(JDReadApplication.getInstance());
        storeCategoryAction.execute(JDReadApplication.getStoreDataBundle(), new RxCallback() {
            @Override
            public void onNext(Object o) {

            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
            }
        });
    }

    @Subscribe
    public void onStoreBakcTopClick(OnStoreBakcTopClick event) {
        setCurrent(R.id.book_store_one, SCROLL_ONE);
    }

    @Subscribe
    public void onRankViewClick(OnRankViewClick event) {

    }

}
