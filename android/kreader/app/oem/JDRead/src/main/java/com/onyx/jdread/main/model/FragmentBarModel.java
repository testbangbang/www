package com.onyx.jdread.main.model;

import android.databinding.BaseObservable;
import android.databinding.ObservableField;

import com.onyx.jdread.main.common.BaseFragment;

/**
 * Created by suicheng on 2018/3/6.
 */
public class FragmentBarModel extends BaseObservable {
    public final ObservableField<String> name = new ObservableField<>();
    public final ObservableField<BaseFragment> baseFragment = new ObservableField<>();

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public BaseFragment getBaseFragment() {
        return baseFragment.get();
    }

    public void setBaseFragment(BaseFragment fragment) {
        baseFragment.set(fragment);
    }
}
