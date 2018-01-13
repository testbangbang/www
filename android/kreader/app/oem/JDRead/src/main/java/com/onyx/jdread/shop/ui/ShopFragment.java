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
import com.onyx.android.sdk.utils.PreferenceManager;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.FragmentBookShopBinding;
import com.onyx.jdread.databinding.FragmentBookShopOneBinding;
import com.onyx.jdread.databinding.FragmentBookShopThreeBinding;
import com.onyx.jdread.databinding.FragmentBookShopTwoBinding;
import com.onyx.jdread.main.common.BaseFragment;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.shop.action.BookCategoryAction;
import com.onyx.jdread.shop.action.ShopMainConfigAction;
import com.onyx.jdread.shop.adapter.BannerSubjectAdapter;
import com.onyx.jdread.shop.adapter.CategorySubjectAdapter;
import com.onyx.jdread.shop.adapter.SubjectAdapter;
import com.onyx.jdread.shop.event.BookItemClickEvent;
import com.onyx.jdread.shop.event.CategoryViewClick;
import com.onyx.jdread.shop.event.GoShopingCartEvent;
import com.onyx.jdread.shop.event.RankViewClick;
import com.onyx.jdread.shop.event.ShopBakcTopClick;
import com.onyx.jdread.shop.event.ShopMainViewAllBookEvent;
import com.onyx.jdread.shop.event.ViewAllClickEvent;
import com.onyx.jdread.shop.model.BookShopViewModel;
import com.onyx.jdread.shop.model.ShopDataBundle;
import com.onyx.jdread.shop.view.DividerItemDecoration;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by huxiaomao on 2017/12/7.
 */

public class ShopFragment extends BaseFragment {

    private static final int SCROLL_TOTAL = 3;
    private static final int SCROLL_ONE = 0;
    private static final int SCROLL_TWO = 1;
    private static final int SCROLL_THREE = 2;
    private static final int FLING_MIN_DISTANCE = 30;
    private FragmentBookShopBinding bookShopBinding;
    private GestureDetector gestureDetector;
    private FragmentBookShopOneBinding shopOneBinding;
    private FragmentBookShopTwoBinding shopTwoBinding;
    private FragmentBookShopThreeBinding shopThreeBinding;
    private int bannerSpace = JDReadApplication.getInstance().getResources().getInteger(R.integer.book_shop_recycle_view_space);
    private DividerItemDecoration itemDecoration;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        bookShopBinding = FragmentBookShopBinding.inflate(inflater, container, false);
        initView();
        initLibrary();
        initData();
        return bookShopBinding.getRoot();
    }

    private void initLibrary() {
        if (!getEventBus().isRegistered(this)) {
            getEventBus().register(this);
        }
    }

    private void initData() {
        getShopMainConfigData();
        getCategoryData();
    }

    private void initView() {
        bookShopBinding.scrollBar.setTotal(SCROLL_TOTAL);
        shopOneBinding = bookShopBinding.bookShopOne;
        shopTwoBinding = bookShopBinding.bookShopTwo;
        shopThreeBinding = bookShopBinding.bookShopThree;
        bookShopBinding.setViewModel(getBookShopViewModel());
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
        initLibrary();
    }

    @Override
    public void onStop() {
        super.onStop();
        getEventBus().unregister(this);
    }

    private BookShopViewModel getBookShopViewModel() {
        return getShopDataBundle().getShopViewModel();
    }

    private EventBus getEventBus() {
        return getShopDataBundle().getEventBus();
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
            if (isCurrentViewVisible(shopOneBinding.getRoot()) && y_axis > FLING_MIN_DISTANCE) {
                setCurrent(R.id.book_shop_two, SCROLL_TWO);
            } else if (isCurrentViewVisible(shopTwoBinding.getRoot()) && y_axis > FLING_MIN_DISTANCE) {
                setCurrent(R.id.book_shop_three, SCROLL_THREE);
            } else if (isCurrentViewVisible(shopTwoBinding.getRoot()) && -(y_axis) > FLING_MIN_DISTANCE) {
                setCurrent(R.id.book_shop_one, SCROLL_ONE);
            } else if (isCurrentViewVisible(shopThreeBinding.getRoot()) && -(y_axis) > FLING_MIN_DISTANCE) {
                setCurrent(R.id.book_shop_two, SCROLL_TWO);
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
        bookShopBinding.scrollBar.setFocusPosition(position);
    }

    private void visible(int id) {
        shopOneBinding.getRoot().setVisibility(id == R.id.book_shop_one ? View.VISIBLE : View.GONE);
        shopTwoBinding.getRoot().setVisibility(id == R.id.book_shop_two ? View.VISIBLE : View.GONE);
        shopThreeBinding.getRoot().setVisibility(id == R.id.book_shop_three ? View.VISIBLE : View.GONE);
    }

    private void setRecyclerViewBanner() {
        BannerSubjectAdapter adapter = new BannerSubjectAdapter(getEventBus());
        PageRecyclerView recyclerView = shopOneBinding.recyclerViewBanner;
        recyclerView.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setAdapter(adapter);
    }

    private void setRecyclerViewCoverSubjectOne() {
        SubjectAdapter recyclerViewOneAdapter = new SubjectAdapter(getEventBus());
        PageRecyclerView recyclerViewOne = shopOneBinding.recyclerViewCoverSubjectOne;
        recyclerViewOne.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
        recyclerViewOne.addItemDecoration(itemDecoration);
        recyclerViewOne.setAdapter(recyclerViewOneAdapter);
    }

    private void setRecyclerViewCoverSubjectTwoBackUp() {
        SubjectAdapter recyclerViewTwoAdapter = new SubjectAdapter(getEventBus());
        PageRecyclerView recyclerViewTwo = shopOneBinding.recyclerViewCoverSubjectTwoBackUp;
        recyclerViewTwo.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
        recyclerViewTwo.addItemDecoration(itemDecoration);
        recyclerViewTwo.setAdapter(recyclerViewTwoAdapter);
    }

    private void setRecyclerViewCoverSubjectTwo() {
        SubjectAdapter adapter = new SubjectAdapter(getEventBus());
        PageRecyclerView recyclerView = shopTwoBinding.recyclerViewCoverSubjectTwo;
        recyclerView.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setAdapter(adapter);
    }

    private void setRecyclerViewCategory() {
        CategorySubjectAdapter adapter = new CategorySubjectAdapter(getEventBus(),false);
        PageRecyclerView recyclerView = shopTwoBinding.recyclerViewCategorySubject;
        recyclerView.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
        recyclerView.setAdapter(adapter);
    }

    private void setRecyclerViewCoverSubjectFourBackup() {
        SubjectAdapter adapter = new SubjectAdapter(getEventBus());
        PageRecyclerView recyclerView = shopTwoBinding.recyclerViewCoverSubjectFourBackup;
        recyclerView.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
        recyclerView.setAdapter(adapter);
    }

    private void setRecyclerViewCoverSubjectFour() {
        SubjectAdapter adapter = new SubjectAdapter(getEventBus());
        PageRecyclerView recyclerView = shopThreeBinding.recyclerViewCoverSubjectFour;
        recyclerView.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setAdapter(adapter);
    }

    private void getShopMainConfigData() {
        ShopMainConfigAction configAction = new ShopMainConfigAction();
        configAction.execute(getShopDataBundle(), new RxCallback<ShopMainConfigAction>() {
            @Override
            public void onNext(ShopMainConfigAction configAction) {

            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
            }
        });
    }

    private void getCategoryData() {
        BookCategoryAction bookCategoryAction = new BookCategoryAction(true);
        bookCategoryAction.execute(getShopDataBundle(), new RxCallback() {
            @Override
            public void onNext(Object o) {

            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShopBakcTopClick(ShopBakcTopClick event) {
        setCurrent(R.id.book_shop_one, SCROLL_ONE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRankViewClick(RankViewClick event) {
        if (getViewEventCallBack() != null) {
            getViewEventCallBack().gotoView(BookRankFragment.class.getName());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCategoryViewClick(CategoryViewClick event) {
        if (getViewEventCallBack() != null) {
            getViewEventCallBack().gotoView(AllCategoryFragment.class.getName());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBookItemClickEvent(BookItemClickEvent event) {
        PreferenceManager.setLongValue(JDReadApplication.getInstance(), Constants.SP_KEY_BOOK_ID, event.getBookBean().ebook_id);
        if (getViewEventCallBack() != null) {
            getViewEventCallBack().gotoView(BookDetailFragment.class.getName());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGoShopingCartEvent(GoShopingCartEvent event) {
        getViewEventCallBack().gotoView(ShopCartFragment.class.getName());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShopMainViewAllBookEvent(ShopMainViewAllBookEvent event) {
        getViewEventCallBack().gotoView(AllCategoryFragment.class.getName());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onViewAllClickEvent(ViewAllClickEvent event) {
        PreferenceManager.setStringValue(JDReadApplication.getInstance(), Constants.SP_KEY_SUBJECT_NAME, event.subjectName);
        PreferenceManager.setIntValue(JDReadApplication.getInstance(), Constants.SP_KEY_SUBJECT_MODEL_ID, event.modelId);
        PreferenceManager.setIntValue(JDReadApplication.getInstance(), Constants.SP_KEY_SUBJECT_MODEL_TYPE, event.modelType);
        if (getViewEventCallBack() != null) {
            getViewEventCallBack().gotoView(ViewAllBooksFragment.class.getName());
        }
    }

    public ShopDataBundle getShopDataBundle() {
        return ShopDataBundle.getInstance();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        hideLoadingDialog();
    }
}
