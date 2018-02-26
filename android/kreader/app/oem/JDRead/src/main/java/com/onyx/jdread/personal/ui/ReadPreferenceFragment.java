package com.onyx.jdread.personal.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.main.model.TitleBarModel;
import com.onyx.jdread.personal.action.GetReadPreferenceAction;
import com.onyx.jdread.personal.action.SetReadPreferenceAction;
import com.onyx.jdread.personal.adapter.ReadPreferenceAdapter;
import com.onyx.jdread.personal.cloud.entity.jdbean.SetReadPreferenceBean;
import com.onyx.jdread.personal.model.PersonalDataBundle;
import com.onyx.jdread.setting.event.BackToSettingFragmentEvent;
import com.onyx.jdread.shop.action.BookCategoryAction;
import com.onyx.jdread.shop.cloud.entity.jdbean.CategoryListResultBean;
import com.onyx.jdread.shop.model.ShopDataBundle;
import com.onyx.jdread.shop.view.DividerItemDecoration;
import com.onyx.jdread.util.Utils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by li on 2018/1/3.
 */

public class ReadPreferenceFragment extends BaseFragment {
    private ReadPreferenceBinding binding;
    private ReadPreferenceAdapter readPreferenceAdapter;
    private final static int SUB_CATEGORY = 2;
    private Map<Integer, List<CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo>> datas = new HashMap<>();

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
        Utils.ensureRegister(PersonalDataBundle.getInstance().getEventBus(), this);
    }

    @Override
    public void onStop() {
        super.onStop();
        Utils.ensureUnregister(PersonalDataBundle.getInstance().getEventBus(), this);
    }

    private void initView() {
        binding.readPreferenceRecycler.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
        DividerItemDecoration decoration = new DividerItemDecoration(JDReadApplication.getInstance(), DividerItemDecoration.VERTICAL_LIST);
        binding.readPreferenceRecycler.addItemDecoration(decoration);
        readPreferenceAdapter = new ReadPreferenceAdapter();
        binding.readPreferenceRecycler.setAdapter(readPreferenceAdapter);
        binding.readPreferenceRecycler.setPageTurningCycled(true);
    }

    private void initData() {
        TitleBarModel titleModel = PersonalDataBundle.getInstance().getTitleModel();
        titleModel.title.set(JDReadApplication.getInstance().getResources().getString(R.string.my_read_preference));
        titleModel.backEvent.set(new BackToSettingFragmentEvent());
        binding.readPreferenceTitle.setTitleModel(titleModel);

        if (datas != null && datas.size() > 0) {
            datas.clear();
        }
        getPreferences();
    }

    private void getPreferences() {
        final GetReadPreferenceAction getReadPreferenceAction = new GetReadPreferenceAction();
        getReadPreferenceAction.execute(PersonalDataBundle.getInstance(), new RxCallback() {
            @Override
            public void onNext(Object o) {
                List<Integer> getPreferenceItems = getReadPreferenceAction.getData();
                getCategory(getPreferenceItems);
            }
        });
    }

    private void getCategory(final List<Integer> getPreferenceItems) {
        List<CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo> categoryBean = ShopDataBundle.getInstance().getCategoryBean();
        if (categoryBean != null && categoryBean.size() > 0) {
            datas.put(categoryBean.get(0).cateLevel, categoryBean);
            if (readPreferenceAdapter != null) {
                handleData(getPreferenceItems, categoryBean);
                readPreferenceAdapter.setData(categoryBean);
            }
        } else {
            final BookCategoryAction action = new BookCategoryAction();
            action.execute(ShopDataBundle.getInstance(), new RxCallback() {
                @Override
                public void onNext(Object o) {
                    List<CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo> cateTwo = ShopDataBundle.getInstance().getCategoryBean();
                    datas.put(cateTwo.get(0).cateLevel, cateTwo);
                    if (readPreferenceAdapter != null) {
                        handleData(getPreferenceItems, cateTwo);
                        readPreferenceAdapter.setData(cateTwo);
                    }
                }
            });
        }
    }

    private void handleData(List<Integer> getPreferenceItems, List<CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo> categorys) {
        for (int i = 0; i < categorys.size(); i++) {
            CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo category = categorys.get(i);
            category.name = changeCategoryName(category.name);
            List<CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo> subCategory = categorys.get(i).sub_category;
            setSavedCategory(getPreferenceItems, subCategory);
        }
    }

    private void setSavedCategory(List<Integer> items, List<CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo> subCategory) {
        if (items != null && items.size() > 0) {
            for (int i = 0; i < subCategory.size(); i++) {
                CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo categoryBean = subCategory.get(i);
                for (int j = 0; j < items.size(); j++) {
                    Integer selected = items.get(j);
                    if (categoryBean.id == selected) {
                        categoryBean.isSelect = true;
                    } else {
                        categoryBean.isSelect = false;
                    }
                }
            }
        }
    }

    private String changeCategoryName(String name) {
        String result = "";
        if (Constants.CATEGORY_MATH_CONTENT.equals(name)) {
            result = ResManager.getString(R.string.category_publish);
        } else if (Constants.CATEGORY_BOY_ORIGINAL.equals(name)) {
            result = ResManager.getString(R.string.category_boy);
        } else if (Constants.CATEGORY_GIRL_ORIGINAL.equals(name)) {
            result = ResManager.getString(R.string.category_girl);
        } else {
            result = name;
        }
        return result;
    }

    private void initListener() {
        if (readPreferenceAdapter != null) {
            readPreferenceAdapter.setOnItemClickListener(new PageRecyclerView.PageAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    List<CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo> data = readPreferenceAdapter.getData();
                    CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo catListBean = data.get(position);
                    List<CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo> subCategory = catListBean.sub_category;
                    if (subCategory != null && subCategory.size() > 0) {
                        datas.put(subCategory.get(0).cateLevel, subCategory);
                        readPreferenceAdapter.setData(subCategory);
                    }
                }
            });
        }

        binding.readPreferenceSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (datas.size() == SUB_CATEGORY) {
                    final List<CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo> selectedBean = readPreferenceAdapter.getSelectedBean();
                    if (selectedBean != null) {
                        final SetReadPreferenceAction action = new SetReadPreferenceAction(selectedBean);
                        action.execute(PersonalDataBundle.getInstance(), new RxCallback() {
                            @Override
                            public void onNext(Object o) {
                                SetReadPreferenceBean resultBean = action.getResultBean();
                                if (resultBean.getResultCode() == 0) {
                                    readPreferenceAdapter.setData(datas.get(SUB_CATEGORY - 1));
                                    datas.remove(SUB_CATEGORY);
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBackToSettingFragmentEvent(BackToSettingFragmentEvent event) {
        if (datas.size() == SUB_CATEGORY) {
            backToCategory();
            return;
        }
        viewEventCallBack.viewBack();
    }

    private void backToCategory() {
        getPreferences();
        datas.remove(SUB_CATEGORY);
    }
}
