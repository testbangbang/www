package com.onyx.jdread.personal.ui;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.ReadPreferenceBinding;
import com.onyx.jdread.main.common.BaseFragment;
import com.onyx.jdread.main.model.TitleBarModel;
import com.onyx.jdread.personal.adapter.ReadPreferenceAdapter;
import com.onyx.jdread.personal.model.PersonalDataBundle;
import com.onyx.jdread.setting.event.BackToSettingFragmentEvent;
import com.onyx.jdread.shop.action.BookCategoryAction;
import com.onyx.jdread.shop.cloud.entity.jdbean.CategoryListResultBean;
import com.onyx.jdread.shop.model.ShopDataBundle;
import com.onyx.jdread.shop.view.DividerItemDecoration;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by li on 2018/1/3.
 */

public class ReadPreferenceFragment extends BaseFragment {
    private ReadPreferenceBinding binding;
    private ReadPreferenceAdapter readPreferenceAdapter;
    private Map<Integer, List<CategoryListResultBean.CatListBean>> datas = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = (ReadPreferenceBinding) DataBindingUtil.inflate(inflater, R.layout.fragment_read_preference, container, false);
        initView();
        initData();
        initListener();
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        PersonalDataBundle.getInstance().getEventBus().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        PersonalDataBundle.getInstance().getEventBus().unregister(this);
    }

    private void initView() {
        binding.readPreferenceRecycler.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
        DividerItemDecoration decoration = new DividerItemDecoration(JDReadApplication.getInstance(), DividerItemDecoration.VERTICAL_LIST);
        binding.readPreferenceRecycler.addItemDecoration(decoration);
        readPreferenceAdapter = new ReadPreferenceAdapter();
        binding.readPreferenceRecycler.setAdapter(readPreferenceAdapter);
    }

    private void initData() {
        TitleBarModel titleModel = PersonalDataBundle.getInstance().getTitleModel();
        titleModel.title.set(JDReadApplication.getInstance().getResources().getString(R.string.read_preference));
        titleModel.backEvent.set(new BackToSettingFragmentEvent());
        binding.readPreferenceTitle.setTitleModel(titleModel);

        BookCategoryAction action = new BookCategoryAction(JDReadApplication.getInstance());
        action.execute(ShopDataBundle.getInstance(), new RxCallback() {
            @Override
            public void onNext(Object o) {
                List<CategoryListResultBean.CatListBean> categorySubjectItems =
                        ShopDataBundle.getInstance().getShopViewModel().getCategorySubjectItems();
                datas.put(categorySubjectItems.get(0).isLeaf, categorySubjectItems);
                if (readPreferenceAdapter != null) {
                    readPreferenceAdapter.setData(categorySubjectItems);
                }
            }
        });
    }

    private void initListener() {
        if (readPreferenceAdapter != null) {
            readPreferenceAdapter.setOnItemClickListener(new PageRecyclerView.PageAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    List<CategoryListResultBean.CatListBean> data = readPreferenceAdapter.getData();
                    CategoryListResultBean.CatListBean catListBean = data.get(position);
                    List<CategoryListResultBean.CatListBean> list = handleData(catListBean);
                    if (list != null && list.size() > 0) {
                        datas.put(list.get(0).isLeaf, list);
                        readPreferenceAdapter.setData(list);
                    }
                }
            });
        }

        binding.readPreferenceSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (datas.size() == 2) {
                    // TODO: 2018/1/3 save type
                    datas.remove(1);
                    readPreferenceAdapter.setData(datas.get(0));
                }
            }
        });
    }

    private List<CategoryListResultBean.CatListBean> handleData(CategoryListResultBean.CatListBean catListBean) {
        List<CategoryListResultBean.CatListBean> list = new ArrayList<>();
        if (catListBean.childList != null && catListBean.childList.size() > 0) {
            List<CategoryListResultBean.CatListBean.ChildListBean> childList = catListBean.childList;
            for (CategoryListResultBean.CatListBean.ChildListBean childListBean : childList) {
                CategoryListResultBean.CatListBean bean = new CategoryListResultBean.CatListBean();
                bean.amount = childListBean.amount;
                bean.catId = childListBean.catId;
                bean.catName = childListBean.catName;
                bean.catType = childListBean.catType;
                bean.isLeaf = childListBean.isLeaf;
                bean.shortName = childListBean.catName;
                bean.childList = null;
                list.add(bean);
            }
        }
        return list;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBackToSettingFragmentEvent(BackToSettingFragmentEvent event) {
        viewEventCallBack.viewBack();
    }
}
