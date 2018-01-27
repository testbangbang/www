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
import com.onyx.jdread.databinding.FragmentBookShopBinding;
import com.onyx.jdread.databinding.FragmentBookShopFiveBinding;
import com.onyx.jdread.databinding.FragmentBookShopFourBinding;
import com.onyx.jdread.databinding.FragmentBookShopOneBinding;
import com.onyx.jdread.databinding.FragmentBookShopSixBinding;
import com.onyx.jdread.databinding.FragmentBookShopThreeBinding;
import com.onyx.jdread.databinding.FragmentBookShopTwoBinding;
import com.onyx.jdread.library.ui.SearchBookFragment;
import com.onyx.jdread.main.common.BaseFragment;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.main.common.JDPreferenceManager;
import com.onyx.jdread.main.common.ToastUtil;
import com.onyx.jdread.shop.action.BookCategoryAction;
import com.onyx.jdread.shop.action.ShopMainConfigAction;
import com.onyx.jdread.shop.adapter.BannerSubjectAdapter;
import com.onyx.jdread.shop.adapter.CategorySubjectAdapter;
import com.onyx.jdread.shop.adapter.SubjectAdapter;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookModelConfigResultBean;
import com.onyx.jdread.shop.event.BookItemClickEvent;
import com.onyx.jdread.shop.event.CategoryViewClick;
import com.onyx.jdread.shop.event.EnjoyReadViewClick;
import com.onyx.jdread.shop.event.GoShopingCartEvent;
import com.onyx.jdread.shop.event.NewBookViewClick;
import com.onyx.jdread.shop.event.RankViewClick;
import com.onyx.jdread.shop.event.SaleViewClick;
import com.onyx.jdread.shop.event.SearchViewClickEvent;
import com.onyx.jdread.shop.event.ShopBakcTopClick;
import com.onyx.jdread.shop.event.ShopMainViewAllBookEvent;
import com.onyx.jdread.shop.event.ViewAllClickEvent;
import com.onyx.jdread.shop.model.BookShopViewModel;
import com.onyx.jdread.shop.model.ShopDataBundle;
import com.onyx.jdread.shop.view.DividerItemDecoration;
import com.onyx.jdread.util.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by huxiaomao on 2017/12/7.
 */

public class ShopFragment extends BaseFragment {

    private static final int SCROLL_TOTAL = 6;
    private static final int SCROLL_ONE = 0;
    private static final int SCROLL_TWO = 1;
    private static final int SCROLL_THREE = 2;
    private static final int SCROLL_FOUR = 3;
    private static final int SCROLL_FIVE = 4;
    private static final int SCROLL_SIX = 5;
    private static final int FLING_MIN_DISTANCE = 30;
    private FragmentBookShopBinding bookShopBinding;
    private GestureDetector gestureDetector;
    private FragmentBookShopOneBinding shopOneBinding;
    private FragmentBookShopTwoBinding shopTwoBinding;
    private FragmentBookShopThreeBinding shopThreeBinding;
    private FragmentBookShopFourBinding shopFourBinding;
    private FragmentBookShopFiveBinding shopFiveBinding;
    private FragmentBookShopSixBinding shopSixBinding;
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
        shopFourBinding = bookShopBinding.bookShopFour;
        shopFiveBinding = bookShopBinding.bookShopFive;
        shopSixBinding = bookShopBinding.bookShopSix;
        bookShopBinding.setViewModel(getBookShopViewModel());
        initDividerItemDecoration();
        setRecyclerViewBanner();
        setRecyclerViewCoverSubjectOne();
        setRecyclerViewCoverSubjectTwoBackUp();
        setRecyclerViewCoverSubjectTwo();
        setRecyclerViewCoverSubjectCommon();
        setRecyclerViewTitleSubject();
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
            }  else if (isCurrentViewVisible(shopThreeBinding.getRoot()) && y_axis > FLING_MIN_DISTANCE) {
                setCurrent(R.id.book_shop_four, SCROLL_FOUR);
            }  else if (isCurrentViewVisible(shopFourBinding.getRoot()) && y_axis > FLING_MIN_DISTANCE) {
                setCurrent(R.id.book_shop_five, SCROLL_FIVE);
            }  else if (isCurrentViewVisible(shopFiveBinding.getRoot()) && y_axis > FLING_MIN_DISTANCE) {
                setCurrent(R.id.book_shop_six, SCROLL_SIX);
            } else if (isCurrentViewVisible(shopSixBinding.getRoot()) && -(y_axis) > FLING_MIN_DISTANCE) {
                setCurrent(R.id.book_shop_five, SCROLL_FIVE);
            } else if (isCurrentViewVisible(shopFiveBinding.getRoot()) && -(y_axis) > FLING_MIN_DISTANCE) {
                setCurrent(R.id.book_shop_four, SCROLL_FOUR);
            } else if (isCurrentViewVisible(shopFourBinding.getRoot()) && -(y_axis) > FLING_MIN_DISTANCE) {
                setCurrent(R.id.book_shop_three, SCROLL_THREE);
            } else if (isCurrentViewVisible(shopThreeBinding.getRoot()) && -(y_axis) > FLING_MIN_DISTANCE) {
                setCurrent(R.id.book_shop_two, SCROLL_TWO);
            } else if (isCurrentViewVisible(shopTwoBinding.getRoot()) && -(y_axis) > FLING_MIN_DISTANCE) {
                setCurrent(R.id.book_shop_one, SCROLL_ONE);
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
        shopFourBinding.getRoot().setVisibility(id == R.id.book_shop_four ? View.VISIBLE : View.GONE);
        shopFiveBinding.getRoot().setVisibility(id == R.id.book_shop_five ? View.VISIBLE : View.GONE);
        shopSixBinding.getRoot().setVisibility(id == R.id.book_shop_six ? View.VISIBLE : View.GONE);
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
        PageRecyclerView recyclerViewOne = shopOneBinding.sujectItemOne.recyclerViewSuject;
        recyclerViewOne.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
        recyclerViewOne.addItemDecoration(itemDecoration);
        recyclerViewOne.setAdapter(recyclerViewOneAdapter);
    }

    private void setRecyclerViewCoverSubjectTwoBackUp() {
        SubjectAdapter recyclerViewTwoAdapter = new SubjectAdapter(getEventBus());
        PageRecyclerView recyclerViewTwo = shopOneBinding.sujectItemTwo.recyclerViewSuject;
        recyclerViewTwo.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
        recyclerViewTwo.addItemDecoration(itemDecoration);
        recyclerViewTwo.setAdapter(recyclerViewTwoAdapter);
    }

    private void setRecyclerViewCoverSubjectTwo() {
        SubjectAdapter adapter = new SubjectAdapter(getEventBus());
        PageRecyclerView recyclerView = shopTwoBinding.sujectItemOne.recyclerViewSuject;
        recyclerView.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setAdapter(adapter);
    }

    private void setRecyclerViewTitleSubject() {
        CategorySubjectAdapter adapter = new CategorySubjectAdapter(getEventBus(),false);
        PageRecyclerView recyclerView = shopTwoBinding.recyclerViewTitleSubject;
        recyclerView.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
        recyclerView.setAdapter(adapter);
    }

    private void setRecyclerViewCoverSubjectCommon() {
        SubjectAdapter adapterFourBackup = new SubjectAdapter(getEventBus());
        PageRecyclerView recyclerViewFourBackup = shopTwoBinding.sujectItemTwo.recyclerViewSuject;
        recyclerViewFourBackup.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
        recyclerViewFourBackup.setAdapter(adapterFourBackup);
        SubjectAdapter adapterOne = new SubjectAdapter(getEventBus());
        PageRecyclerView recyclerViewOne = shopThreeBinding.sujectItemOne.recyclerViewSuject;
        recyclerViewOne.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
        recyclerViewOne.addItemDecoration(itemDecoration);
        recyclerViewOne.setAdapter(adapterOne);
        SubjectAdapter adapterTwo = new SubjectAdapter(getEventBus());
        PageRecyclerView recyclerViewTwo = shopThreeBinding.sujectItemTwo.recyclerViewSuject;
        recyclerViewTwo.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
        recyclerViewTwo.addItemDecoration(itemDecoration);
        recyclerViewTwo.setAdapter(adapterTwo);
        SubjectAdapter adapterThree = new SubjectAdapter(getEventBus());
        PageRecyclerView recyclerViewThree = shopFourBinding.sujectItemOne.recyclerViewSuject;
        recyclerViewThree.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
        recyclerViewThree.addItemDecoration(itemDecoration);
        recyclerViewThree.setAdapter(adapterThree);
        SubjectAdapter adapterFour = new SubjectAdapter(getEventBus());
        PageRecyclerView recyclerViewFour = shopFourBinding.sujectItemTwo.recyclerViewSuject;
        recyclerViewFour.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
        recyclerViewFour.addItemDecoration(itemDecoration);
        recyclerViewFour.setAdapter(adapterFour);
        SubjectAdapter adapterFive = new SubjectAdapter(getEventBus());
        PageRecyclerView recyclerViewFive = shopFiveBinding.sujectItemOne.recyclerViewSuject;
        recyclerViewFive.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
        recyclerViewFive.addItemDecoration(itemDecoration);
        recyclerViewFive.setAdapter(adapterFive);
        SubjectAdapter adapterSix = new SubjectAdapter(getEventBus());
        PageRecyclerView recyclerViewSix = shopFiveBinding.sujectItemTwo.recyclerViewSuject;
        recyclerViewSix.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
        recyclerViewSix.addItemDecoration(itemDecoration);
        recyclerViewSix.setAdapter(adapterSix);
        SubjectAdapter adapterLast = new SubjectAdapter(getEventBus());
        PageRecyclerView recyclerViewLast = shopSixBinding.recyclerViewCoverSubjectLast;
        recyclerViewLast.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
        recyclerViewLast.addItemDecoration(itemDecoration);
        recyclerViewLast.setAdapter(adapterLast);
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
        if (checkWfiDisConnected()) {
            return;
        }
        if (getViewEventCallBack() != null) {
            getViewEventCallBack().gotoView(BookRankFragment.class.getName());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEnjoyReadViewClick(EnjoyReadViewClick event) {
        if (checkWfiDisConnected()) {
            return;
        }
        if (getViewEventCallBack() != null) {
            getViewEventCallBack().gotoView(BookVIPReadFragment.class.getName());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSaleViewClick(SaleViewClick event) {
        if (checkWfiDisConnected()) {
            return;
        }
        if (getViewEventCallBack() != null) {
            getViewEventCallBack().gotoView(BookSaleFragment.class.getName());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNewBookViewClick(NewBookViewClick event) {
        if (checkWfiDisConnected()) {
            return;
        }
        if (getViewEventCallBack() != null) {
            getViewEventCallBack().gotoView(BookNewBooksFragment.class.getName());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCategoryViewClick(CategoryViewClick event) {
        if (checkWfiDisConnected()) {
            return;
        }
        if (getViewEventCallBack() != null) {
            getViewEventCallBack().gotoView(AllCategoryFragment.class.getName());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSearchViewClickEvent(SearchViewClickEvent event) {
        if (getViewEventCallBack() != null) {
            getViewEventCallBack().gotoView(SearchBookFragment.class.getSimpleName());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBookItemClickEvent(BookItemClickEvent event) {
        if (checkWfiDisConnected()) {
            return;
        }
        JDPreferenceManager.setLongValue(Constants.SP_KEY_BOOK_ID, event.getBookBean().ebook_id);
        if (getViewEventCallBack() != null) {
            getViewEventCallBack().gotoView(BookDetailFragment.class.getName());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGoShopingCartEvent(GoShopingCartEvent event) {
        if (checkWfiDisConnected()) {
            return;
        }
        getViewEventCallBack().gotoView(ShopCartFragment.class.getName());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShopMainViewAllBookEvent(ShopMainViewAllBookEvent event) {
        if (checkWfiDisConnected()) {
            return;
        }
        getViewEventCallBack().gotoView(AllCategoryFragment.class.getName());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onViewAllClickEvent(ViewAllClickEvent event) {
        if (checkWfiDisConnected()) {
            return;
        }
        BookModelConfigResultBean.DataBean.ModulesBean modulesBean = event.modulesBean;
        if (modulesBean != null) {
            JDPreferenceManager.setStringValue(Constants.SP_KEY_SUBJECT_NAME, modulesBean.show_name);
            JDPreferenceManager.setIntValue(Constants.SP_KEY_BOOK_LIST_TYPE, Constants.BOOK_LIST_TYPE_BOOK_MODEL);
            JDPreferenceManager.setIntValue(Constants.SP_KEY_SUBJECT_MODEL_ID, modulesBean.id);
            JDPreferenceManager.setIntValue(Constants.SP_KEY_SUBJECT_MODEL_TYPE, modulesBean.f_type);
            if (getViewEventCallBack() != null) {
                getViewEventCallBack().gotoView(ViewAllBooksFragment.class.getName());
            }
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

    private boolean checkWfiDisConnected() {
        if (!Utils.isNetworkConnected(JDReadApplication.getInstance())) {
            ToastUtil.showToast(JDReadApplication.getInstance().getResources().getString(R.string.wifi_no_connected));
            return true;
        }
        return false;
    }
}
