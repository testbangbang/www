package com.onyx.edu.note.scribble.view;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.edu.note.NoteManager;
import com.onyx.edu.note.R;
import com.onyx.edu.note.data.ScribbleFunctionBarMenuID;
import com.onyx.edu.note.data.ScribbleFunctionMenuIDType;
import com.onyx.edu.note.data.ScribbleSubMenuID;
import com.onyx.edu.note.databinding.ScribbleFunctionItemBinding;
import com.onyx.edu.note.databinding.ScribbleSubMenuBinding;
import com.onyx.edu.note.scribble.ScribbleFunctionItemViewHolder;
import com.onyx.edu.note.scribble.ScribbleFunctionItemViewModel;
import com.onyx.edu.note.scribble.ScribbleItemNavigator;
import com.onyx.edu.note.ui.PageAdapter;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by solskjaer49 on 16/8/4 15:05.
 */
public class ScribbleSubMenu extends RelativeLayout {

    /**
     * onCancel():let operator know the no operate but user dismiss the menu.
     * <p>
     * onLayoutStateChanged():use in custom pos.
     */
    public static abstract class Callback {
        public abstract void onLayoutStateChanged();

        public abstract void onCancel();
    }

    private final Callback mCallback;
    private int mPositionID;
    private ScribbleSubMenuBinding mBinding;
    private ScribbleFunctionAdapter mAdapter;

    private void initRecyclerView() {
        PageRecyclerView subMenuRecyclerView = mBinding.subMenuRecyclerView;
        subMenuRecyclerView.setLayoutManager(new DisableScrollGridManager(getContext()));
        subMenuRecyclerView.setHasFixedSize(true);
        buildFunctionAdapter();
        subMenuRecyclerView.setAdapter(mAdapter);
    }

    public ScribbleSubMenu(Context context, RelativeLayout parentLayout, Callback callback, int positionID) {
        this(context, parentLayout, callback, positionID, true);
    }

    public ScribbleSubMenu(Context context, RelativeLayout parentLayout, Callback callback,
                           int positionID, boolean isShowInfoBar) {
        super(context);
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(context),
                R.layout.scribble_sub_menu, this, true);
        setBackgroundColor(Color.TRANSPARENT);
        mBinding.dismissZone.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss(true);
            }
        });
        mPositionID = positionID;
        mCallback = callback;
        parentLayout.addView(this, setMenuPosition(isShowInfoBar));
        initRecyclerView();
        setVisibility(View.GONE);
    }

    public void show(final @ScribbleFunctionBarMenuID.ScribbleFunctionBarMenuDef
                             int category, List<Integer> dataList,final boolean isLineLayoutMode) {
        mAdapter.setRawData(dataList, getContext());
        updateIndicator(category,isLineLayoutMode);
        reConfigMenuHeight();
        setVisibility(VISIBLE);
    }

    private void reConfigMenuHeight() {
        int height = (int) getContext().getResources().getDimension(R.dimen.onyx_sub_note_menu_height);
        RelativeLayout.LayoutParams layoutParams = (LayoutParams) mBinding.subMenuRecyclerView.getLayoutParams();
        layoutParams.height = height * mAdapter.getRowCount();
        mBinding.subMenuRecyclerView.setLayoutParams(layoutParams);
    }

    /**
     * @param isCancel if no submenu item was previous selected -> true,otherwise false.
     */
    public void dismiss(boolean isCancel) {
        if (mCallback != null && isCancel) {
            mCallback.onCancel();
        }
        setVisibility(GONE);
    }

    public boolean isShow() {
        return getVisibility() == VISIBLE;
    }

    public void rePositionAfterNewConfiguration(boolean isShowStatusBar) {
        setLayoutParams(setMenuPosition(isShowStatusBar));
    }

    private LayoutParams setMenuPosition(boolean isShowInfoBar) {
        LayoutParams p = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        if (isShowInfoBar) {
            p.addRule(RelativeLayout.ABOVE, mPositionID);
        } else {
            p.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        }
        p.addRule(RelativeLayout.BELOW, R.id.tool_bar);
        p.addRule(RelativeLayout.CENTER_HORIZONTAL);
        return p;
    }

    private void updateIndicator(@ScribbleFunctionBarMenuID.ScribbleFunctionBarMenuDef int mainMenuID,boolean isLineLayoutMode) {
        NoteManager manager = NoteManager.sharedInstance(getContext().getApplicationContext());
        int targetID = Integer.MIN_VALUE;
        switch (mainMenuID) {
            case ScribbleFunctionBarMenuID.ERASER:
            case ScribbleFunctionBarMenuID.PEN_STYLE:
                targetID = ScribbleSubMenuID.menuIdFromShapeType(manager.getShapeDataInfo().getCurrentShapeType());
                break;
            case ScribbleFunctionBarMenuID.BG:
                targetID = ScribbleSubMenuID.menuIdFromBg(isLineLayoutMode ?
                        manager.getShapeDataInfo().getLineLayoutBackground() : manager.getShapeDataInfo().getBackground());
                break;
            case ScribbleFunctionBarMenuID.PEN_WIDTH:
                targetID = ScribbleSubMenuID.menuIdFromStrokeWidth(manager.getShapeDataInfo().getStrokeWidth());
                break;
        }
        for (ScribbleFunctionItemViewModel itemViewModel : mAdapter.getItemVMList()) {
            itemViewModel.mIsChecked.set(itemViewModel.getItemID() == targetID);
        }
    }

    private void buildFunctionAdapter() {
        mAdapter = new ScribbleFunctionAdapter(getContext(), mBinding);
    }

    public static class ScribbleFunctionAdapter extends PageAdapter<ScribbleFunctionItemViewHolder,
            Integer, ScribbleFunctionItemViewModel> {
        private ScribbleItemNavigator mItemNavigator;
        private LayoutInflater mLayoutInflater;
        private WeakReference<Context> mContextWeakReference;
        private WeakReference<ScribbleSubMenuBinding> mBindingWeakReference;

        ScribbleFunctionAdapter(Context context, ScribbleSubMenuBinding binding) {
            if (context instanceof ScribbleItemNavigator) {
                mItemNavigator = (ScribbleItemNavigator) context;
            }
            mLayoutInflater = LayoutInflater.from(context);
            mContextWeakReference = new WeakReference<>(context.getApplicationContext());
            mBindingWeakReference = new WeakReference<>(binding);
        }

        @Override
        public int getRowCount() {
            return getItemVMList().size() > getColumnCount() ?
                    (int) Math.ceil((float) getItemVMList().size() / (float) getColumnCount()) : 1;
        }

        @Override
        public int getColumnCount() {
            if (mContextWeakReference.get() != null) {
                return mContextWeakReference.get().getResources().getInteger(R.integer.note_menu_columns);
            } else {
                return 0;
            }
        }

        @Override
        public ScribbleFunctionItemViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
            return new ScribbleFunctionItemViewHolder(ScribbleFunctionItemBinding.inflate(mLayoutInflater,
                    parent, false));
        }

        @Override
        public void onPageBindViewHolder(ScribbleFunctionItemViewHolder holder, int position) {
            holder.bindTo(getItemVMList().get(position));
        }

        @Override
        public void setRawData(List<Integer> rawData, Context context) {
            super.setRawData(rawData, context);
            for (Integer subMenuID : rawData) {
                ScribbleFunctionItemViewModel viewModel = new
                        ScribbleFunctionItemViewModel(subMenuID, ScribbleFunctionMenuIDType.SUB_MENU);
                viewModel.setNavigator(mItemNavigator);
                getItemVMList().add(viewModel);
            }
            if (mBindingWeakReference.get() != null) {
                mBindingWeakReference.get().subMenuRecyclerView.notifyDataSetChanged();
            }
        }
    }
}
