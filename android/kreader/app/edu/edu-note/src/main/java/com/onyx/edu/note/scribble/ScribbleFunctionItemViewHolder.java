package com.onyx.edu.note.scribble;

import com.onyx.edu.note.databinding.ScribbleFunctionItemBinding;
import com.onyx.edu.note.ui.BindingViewHolder;

/**
 * Created by solskjaer49 on 2017/7/10 18:12.
 */

public class ScribbleFunctionItemViewHolder extends BindingViewHolder<ScribbleFunctionItemBinding, ScribbleFunctionItemViewModel> {
    public ScribbleFunctionItemViewHolder(ScribbleFunctionItemBinding binding) {
        super(binding);
    }

    @Override
    public void bindTo(ScribbleFunctionItemViewModel model) {
        mBinding.setViewModel(model);
        mBinding.executePendingBindings();
    }
}
