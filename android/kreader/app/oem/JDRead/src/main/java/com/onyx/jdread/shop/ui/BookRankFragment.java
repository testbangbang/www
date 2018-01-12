package com.onyx.jdread.shop.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.onyx.jdread.databinding.FragmentBookRankBinding;
import com.onyx.jdread.library.event.HideAllDialogEvent;
import com.onyx.jdread.library.event.LoadingDialogEvent;
import com.onyx.jdread.main.common.BaseFragment;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.shop.action.BookSpecialTodayAction;
import com.onyx.jdread.shop.action.NewBookAction;
import com.onyx.jdread.shop.adapter.BookRankAdapter;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookModelResultBean;
import com.onyx.jdread.shop.event.BookItemClickEvent;
import com.onyx.jdread.shop.event.TopBackEvent;
import com.onyx.jdread.shop.event.ViewAllClickEvent;
import com.onyx.jdread.shop.model.RankViewModel;
import com.onyx.jdread.shop.model.ShopDataBundle;
import com.onyx.jdread.shop.model.SubjectViewModel;
import com.onyx.jdread.shop.view.DividerItemDecoration;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by jackdeng on 2018/1/4.
 */

public class BookRankFragment extends BaseFragment {
    private static final int SCROLL_TOTAL = 3;
    private static final int DATA_RESULT = 10;
    private int temp = 10;
    private FragmentBookRankBinding bookRankBinding;
    private int bookDetailSpace = JDReadApplication.getInstance().getResources().getInteger(R.integer.book_detail_recycle_view_space);
    private DividerItemDecoration itemDecoration;
    private PageRecyclerView recyclerView;
    private ArrayList<SubjectViewModel> dataList = new ArrayList<>();
    private BookModelResultBean newBookResultBean;
    private BookModelResultBean specialTodayResultBean;
    private CustomHandler mHandler;
    private GPaginator paginator;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        bookRankBinding = FragmentBookRankBinding.inflate(inflater, container, false);
        initView();
        initLibrary();
        initData();
        return bookRankBinding.getRoot();
    }

    private void initLibrary() {
        if (!getEventBus().isRegistered(this)) {
            getEventBus().register(this);
        }
    }

    private void initData() {
        mHandler = new CustomHandler(getActivity());
        dataList.clear();
        temp = 10;
        final NewBookAction newBookAction = new NewBookAction(JDReadApplication.getInstance());
        newBookAction.execute(getShopDataBundle(), new RxCallback() {
            @Override
            public void onNext(Object action) {
                newBookResultBean = newBookAction.getBookModelResultBean();
                temp++;
                mHandler.sendEmptyMessage(DATA_RESULT);
            }
        });

        final BookSpecialTodayAction bookSpecialTodayAction = new BookSpecialTodayAction(JDReadApplication.getInstance());
        bookSpecialTodayAction.execute(getShopDataBundle(), new RxCallback() {
            @Override
            public void onNext(Object o) {
                specialTodayResultBean = bookSpecialTodayAction.getBookModelResultBean();
                temp++;
                mHandler.sendEmptyMessage(DATA_RESULT);
            }
        });
    }

    private class CustomHandler extends Handler {
        private final WeakReference<Activity> mActivity;

        public CustomHandler(Activity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            if (mActivity.get() == null) {
                return;
            }
            int what = msg.what;
            if (what == DATA_RESULT) {
                if (temp - DATA_RESULT == 2) {
                    for (int i = 0; i < 6; i++) {
                        SubjectViewModel subjectViewModel = new SubjectViewModel();
                        subjectViewModel.setEventBus(getEventBus());
                        if (i == 1 || i == 3) {
                            subjectViewModel.setShowNextTitle(true);
                            subjectViewModel.setModelBeanNext(newBookResultBean);
                        }
                        if (i % 2 == 1) {
                            subjectViewModel.setModelBean(newBookResultBean);
                        } else {
                            subjectViewModel.setModelBean(specialTodayResultBean);
                        }
                        dataList.add(subjectViewModel);
                    }
                    getRankViewModel().setRankItems(dataList);
                }
            }
        }
    }

    private void initView() {
        initDividerItemDecoration();
        setRecycleView();
        bookRankBinding.scrollBar.setTotal(SCROLL_TOTAL);
        bookRankBinding.setRankViewModel(getRankViewModel());
        getRankViewModel().getTitleBarViewModel().leftText = getString(R.string.ranking);
    }

    private void setRecycleView() {
        BookRankAdapter adapter = new BookRankAdapter();
        recyclerView = bookRankBinding.recyclerViewRanks;
        recyclerView.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setAdapter(adapter);
        paginator = recyclerView.getPaginator();
        recyclerView.setOnPagingListener(new PageRecyclerView.OnPagingListener() {
            @Override
            public void onPageChange(int position, int itemCount, int pageSize) {
                int currentPage = paginator.getCurrentPage();
                bookRankBinding.scrollBar.setFocusPosition(currentPage);
            }
        });
    }

    private void initDividerItemDecoration() {
        itemDecoration = new DividerItemDecoration(JDReadApplication.getInstance(), DividerItemDecoration.VERTICAL_LIST);
        itemDecoration.setDrawLine(false);
        itemDecoration.setSpace(bookDetailSpace);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
        getEventBus().unregister(this);
    }

    private EventBus getEventBus() {
        return getShopDataBundle().getEventBus();
    }

    public ShopDataBundle getShopDataBundle() {
        return ShopDataBundle.getInstance();
    }

    private RankViewModel getRankViewModel() {
        return getShopDataBundle().getRankViewModel();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoadingDialogEvent(LoadingDialogEvent event) {
        showLoadingDialog(getString(event.getResId()));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onHideAllDialogEvent(HideAllDialogEvent event) {
        hideLoadingDialog();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTopBackEvent(TopBackEvent event) {
        if (getViewEventCallBack() != null) {
            getViewEventCallBack().viewBack();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onViewAllClickEvent(ViewAllClickEvent event) {
        PreferenceManager.setStringValue(JDReadApplication.getInstance(), Constants.SP_KEY_SUBJECT_NAME, event.subjectName);
        PreferenceManager.setIntValue(JDReadApplication.getInstance(), Constants.SP_KEY_SUBJECT_MODEL_ID, event.modelId);
        if (getViewEventCallBack() != null) {
            getViewEventCallBack().gotoView(ViewAllBooksFragment.class.getName());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBookItemClickEvent(BookItemClickEvent event) {
        PreferenceManager.setLongValue(JDReadApplication.getInstance(), Constants.SP_KEY_BOOK_ID, event.getBookBean().ebookId);
        if (getViewEventCallBack() != null) {
            getViewEventCallBack().gotoView(BookDetailFragment.class.getName());
        }
    }
}
