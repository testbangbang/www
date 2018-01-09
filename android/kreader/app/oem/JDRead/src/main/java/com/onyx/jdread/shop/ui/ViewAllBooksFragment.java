package com.onyx.jdread.shop.ui;

import android.content.Context;
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
import com.onyx.jdread.databinding.FragmentViewAllBinding;
import com.onyx.jdread.main.common.BaseFragment;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.shop.action.BookModelAction;
import com.onyx.jdread.shop.adapter.SubjectListAdapter;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookModelResultBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.event.BookItemClickEvent;
import com.onyx.jdread.shop.event.HideAllDialogEvent;
import com.onyx.jdread.shop.event.LoadingDialogEvent;
import com.onyx.jdread.shop.event.TopBackEvent;
import com.onyx.jdread.shop.model.ShopDataBundle;
import com.onyx.jdread.shop.model.TitleBarViewModel;
import com.onyx.jdread.shop.model.ViewAllViewModel;
import com.onyx.jdread.shop.view.DividerItemDecoration;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by jackdeng on 2017/12/30.
 */

public class ViewAllBooksFragment extends BaseFragment {

    private FragmentViewAllBinding viewAllBinding;
    private int row = JDReadApplication.getInstance().getResources().getInteger(R.integer.subject_list_recycle_viw_row);
    private int col = JDReadApplication.getInstance().getResources().getInteger(R.integer.subject_list_recycle_viw_col);
    private PageRecyclerView recyclerView;
    private GPaginator paginator;
    private int currentPage = 1;
    private int fid;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewAllBinding = FragmentViewAllBinding.inflate(inflater, container, false);
        initView();
        initLibrary();
        initData();
        return viewAllBinding.getRoot();
    }

    private void initLibrary() {
        getEventBus().register(this);
    }

    private void initData() {
        String title= PreferenceManager.getStringValue(JDReadApplication.getInstance(), Constants.SP_KEY_SUBJECT_NAME, "");
        fid = PreferenceManager.getIntValue(JDReadApplication.getInstance(), Constants.SP_KEY_SUBJECT_FID, -1);
        getTitleBarViewModel().leftText = title;
        getBooksData(currentPage);
    }

    private void getBooksData(int currentPage) {
        int modelType = 0;
        if (fid == CloudApiContext.BookShopModule.NEW_BOOK_DELIVERY_ID) {
            modelType = CloudApiContext.BookShopModule.NEW_BOOK_DELIVERY_MODULE_TYPE;
        } else if (fid == CloudApiContext.BookShopModule.TODAY_SPECIAL_ID) {
            modelType = CloudApiContext.BookShopModule.TODAY_SPECIAL_MODULE_TYPE;
        } else if (fid == CloudApiContext.BookShopModule.IMPORTANT_RECOMMEND_ID) {
            modelType = CloudApiContext.BookShopModule.IMPORTANT_RECOMMEND_MODULE_TYPE;
        }
        BookModelAction booksAction = new BookModelAction(fid,modelType);
        booksAction.execute(getShopDataBundle(), new RxCallback<BookModelAction>() {
            @Override
            public void onNext(BookModelAction booksAction) {
                BookModelResultBean bookModelResultBean = booksAction.getBookModelResultBean();
                getViewAllViewModel().setBookList(bookModelResultBean.resultList);
                updateContentView();
            }
        });
    }

    private void initView() {
        SubjectListAdapter adapter = new SubjectListAdapter(getEventBus());
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getContextJD(), DividerItemDecoration.VERTICAL_LIST);
        itemDecoration.setDrawLine(false);
        recyclerView = viewAllBinding.recyclerViewSubjectList;
        recyclerView.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
        recyclerView.setAdapter(adapter);
        paginator = recyclerView.getPaginator();
        recyclerView.setOnPagingListener(new PageRecyclerView.OnPagingListener() {
            @Override
            public void onPageChange(int position, int itemCount, int pageSize) {
                if (paginator != null) {
                    int curPage = paginator.getCurrentPage();
                    setCurrentPage(curPage);
                }
            }
        });
        viewAllBinding.setViewModel(getViewAllViewModel());
    }

    private void initPageIndicator() {
        int size = 0;
        if (getViewAllViewModel().getBookList() != null) {
            size = getViewAllViewModel().getBookList().size();
        }
        paginator.resize(row, col, size);
        getViewAllViewModel().setTotalPage(paginator.pages());
        setCurrentPage(paginator.getCurrentPage());
    }

    private void updateContentView() {
        if (recyclerView == null) {
            return;
        }
        recyclerView.getAdapter().notifyDataSetChanged();
        initPageIndicator();
    }

    private void setCurrentPage(int currentPage) {
        getViewAllViewModel().setCurrentPage(currentPage + Constants.PAGE_STEP);
    }

    private ShopDataBundle getShopDataBundle() {
        return ShopDataBundle.getInstance();
    }

    private ViewAllViewModel getViewAllViewModel() {
        return getShopDataBundle().getViewAllViewModel();
    }

    private TitleBarViewModel getTitleBarViewModel() {
        return getViewAllViewModel().getTitleBarViewModel();
    }

    private EventBus getEventBus() {
        return getShopDataBundle().getEventBus();
    }

    private Context getContextJD() {
        return JDReadApplication.getInstance().getApplicationContext();
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTopBackEvent(TopBackEvent event) {
        if (getViewEventCallBack() != null) {
            getViewEventCallBack().viewBack();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBookItemClickEvent(BookItemClickEvent event) {
        PreferenceManager.setLongValue(JDReadApplication.getInstance(), Constants.SP_KEY_BOOK_ID, event.getBookBean().ebookId);
        if (getViewEventCallBack() != null) {
            getViewEventCallBack().gotoView(BookDetailFragment.class.getName());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoadingDialogEvent(LoadingDialogEvent event) {
        showLoadingDialog(getString(event.getResId()));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onHideAllDialogEvent(HideAllDialogEvent event) {
        hideLoadingDialog();
    }
}