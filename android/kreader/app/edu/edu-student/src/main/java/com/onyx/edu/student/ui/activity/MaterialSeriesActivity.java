package com.onyx.edu.student.ui.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.widget.RadioGroup;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.v2.GroupContainer;
import com.onyx.android.sdk.ui.activity.OnyxAppCompatActivity;
import com.onyx.android.sdk.ui.utils.ToastUtils;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.edu.student.R;
import com.onyx.edu.student.StudentApp;
import com.onyx.edu.student.action.CloudGroupContainerListLoadAction;
import com.onyx.edu.student.adapter.ViewPagerAdapter;
import com.onyx.edu.student.databinding.CommonRadioGroupContentLayoutBinding;
import com.onyx.edu.student.events.BookLibraryEvent;
import com.onyx.edu.student.events.DataRefreshEvent;
import com.onyx.edu.student.events.TabSwitchEvent;
import com.onyx.edu.student.holder.LibraryDataHolder;
import com.onyx.edu.student.listener.OnPageChangeListenerImpl;
import com.onyx.edu.student.model.TabItem;
import com.onyx.edu.student.ui.fragment.ContentFragment;
import com.onyx.edu.student.ui.view.CustomRadioGroup;
import com.onyx.edu.student.ui.view.NoSwipePager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by suicheng on 2017/10/28.
 */
public class MaterialSeriesActivity extends OnyxAppCompatActivity {

    private CommonRadioGroupContentLayoutBinding binding;
    private NoSwipePager pagerView;

    private LibraryDataHolder dataHolder;
    private List<TabItem> tabItemList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.common_radio_group_content_layout);

        initView();
        initFragment();
        loadData();
    }

    private void initView() {
        initGroupSelector();
        initViewPager();
    }

    private void initGroupSelector() {
        binding.categoryGroup.updateItemsText(getResources().getStringArray(R.array.material_group));
        binding.categoryGroup.setOnCheckedChangeListener(new CustomRadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId, int checkedIndex) {
                selectShowFragment(checkedIndex);
            }
        });
    }

    private void initViewPager() {
        pagerView = binding.contentViewPager;
        pagerView.addFilterScrollableViewClass(RecyclerView.class);
        pagerView.addFilterScrollableViewClass(ViewPager.class);
        pagerView.addOnPageChangeListener(new OnPageChangeListenerImpl());
        pagerView.setAdapter(new ViewPagerAdapter(getApplicationContext(), tabItemList, getSupportFragmentManager()));
    }

    private void initFragment() {
        tabItemList.clear();
        String[] materialGroup = getResources().getStringArray(R.array.material_group);
        for (String title : materialGroup) {
            tabItemList.add(getTabItem(title));
        }
        pagerView.setAdapter(new ViewPagerAdapter(getApplicationContext(), tabItemList, getSupportFragmentManager()));
        selectShowFragment(0);
    }

    private void loadData() {
        final CloudGroupContainerListLoadAction groupLoadAction = new CloudGroupContainerListLoadAction();
        groupLoadAction.execute(getApplicationContext(), getDataHolder(), new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                dismissProgressDialog(groupLoadAction);
                if (e != null || CollectionUtils.isNullOrEmpty(groupLoadAction.getContainerList())) {
                    ToastUtils.showToast(getApplicationContext(), R.string.online_group_load_error);
                    return;
                }
                processGroupSelect(groupLoadAction.getContainerList(), 0);
            }
        });
        showProgressDialog(groupLoadAction, R.string.loading, null);
    }

    private void processGroupSelect(List<GroupContainer> groupContainerList, int index) {
        if (index >= CollectionUtils.getSize(groupContainerList)) {
            return;
        }
        List<Library> libraryList = new ArrayList<>();
        if (!CollectionUtils.isNullOrEmpty(groupContainerList)) {
            libraryList = groupContainerList.get(index).libraryList;
        }
        notifyDataChanged(libraryList);
    }

    private void notifyDataChanged(List<Library> list) {
        if (CollectionUtils.isNullOrEmpty(list)) {
            return;
        }
        for (Library library : list) {
            EventBus.getDefault().postSticky(new BookLibraryEvent(library));
        }
        resetTabItemsArgument(list);
    }

    private TabItem getTabItem(String name) {
        return new TabItem(name, ContentFragment.class).setExtraArgument(getExtraArgument(name));
    }

    private ContentFragment.Args getExtraArgument(String fragmentName) {
        ContentFragment.Args args = new ContentFragment.Args();
        args.fragmentName = fragmentName;
        return args;
    }

    private void resetTabItemsArgument(List<Library> list) {
        if (CollectionUtils.isNullOrEmpty(list)) {
            return;
        }
        for (TabItem tabItem : tabItemList) {
            for (Library library : list) {
                if (library.getName().equals(tabItem.getTabTitle())) {
                    ContentFragment.Args args = (ContentFragment.Args) tabItem.extraArgument;
                    args.library = library;
                    break;
                }
            }
        }
    }

    private void selectShowFragment(int selectedPosition) {
        pagerView.setCurrentItem(selectedPosition, false);
    }

    private LibraryDataHolder getDataHolder() {
        if (dataHolder == null) {
            dataHolder = new LibraryDataHolder();
            dataHolder.setCloudManager(StudentApp.getCloudStore());
        }
        return dataHolder;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (filterKeyEvent(event)) {
            return super.dispatchKeyEvent(event);
        }
        EventBus.getDefault().post(event);
        return true;
    }

    private boolean filterKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK || event.getKeyCode() == KeyEvent.KEYCODE_MENU) {
            return true;
        }
        return false;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataRefreshEvent(DataRefreshEvent event) {
        loadData();
    }

    @Subscribe
    public void onTabSwitchEvent(TabSwitchEvent event) {
        if (event.isNextTabSwitch()) {
            binding.categoryGroup.nextTab();
        } else {
            binding.categoryGroup.prevTab();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissAllProgressDialog();
    }
}
