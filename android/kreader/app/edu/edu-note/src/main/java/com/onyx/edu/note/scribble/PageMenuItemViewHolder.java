package com.onyx.edu.note.scribble;

import com.onyx.edu.note.databinding.PageMenuItemBinding;
import com.onyx.edu.note.ui.BindingViewHolder;

/**
 * Created by solskjaer49 on 2017/7/10 18:12.
 */

public class PageMenuItemViewHolder extends BindingViewHolder<PageMenuItemBinding, PageMenuItemViewModel> {
    public PageMenuItemViewHolder(PageMenuItemBinding binding) {
        super(binding);
    }

    @Override
    public void bindTo(PageMenuItemViewModel model) {
        mBinding.setViewModel(model);
        mBinding.executePendingBindings();
    }
}
