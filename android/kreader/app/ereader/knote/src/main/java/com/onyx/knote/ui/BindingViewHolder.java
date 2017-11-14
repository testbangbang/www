package com.onyx.knote.ui;

import android.databinding.BaseObservable;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;

/**
 * Created by solskjaer49 on 2016/11/29 11:23.
 */

public abstract class BindingViewHolder<VB extends ViewDataBinding,T extends BaseObservable> extends RecyclerView.ViewHolder {
    protected final VB mBinding;

    public BindingViewHolder(VB binding) {
        super(binding.getRoot());
        mBinding = binding;
    }

    public VB getBinding() {
        return mBinding;
    }

    public abstract void bindTo(T observable);

}
