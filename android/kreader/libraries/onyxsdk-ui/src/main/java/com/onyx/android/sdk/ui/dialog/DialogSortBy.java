package com.onyx.android.sdk.ui.dialog;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.onyx.android.sdk.data.SortOrder;
import com.onyx.android.sdk.ui.R;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.StringUtils;

import java.util.List;

/**
 * Created by suicheng on 2017/2/27.
 */
public class DialogSortBy extends OnyxAlertDialog {

    private String title;
    private List<String> sortByList;
    private int currentSortBySelectedIndex = -1;
    private SortOrder currentSortOrderSelected = SortOrder.Asc;

    private boolean showSortOrderLayout = true;

    private PageRecyclerView contentPageView;
    private int pageViewRowCount = 5;
    private int pageViewColCount = 1;

    private RadioGroup orderRadioGroup;

    //must init all field if necessary set it manually
    private AlignLayoutParams alignParams;

    public DialogSortBy() {
    }

    public DialogSortBy(String title, List<String> sortByList) {
        this.title = title;
        this.sortByList = sortByList;
    }

    private void initAlignLayoutParam() {
        alignParams = new AlignLayoutParams(getResources().getDimensionPixelSize(R.dimen.dialog_sort_by_x_pos),
                getResources().getDimensionPixelSize(R.dimen.dialog_sort_by_y_pos));
        alignParams.width = getResources().getDimensionPixelSize(R.dimen.dialog_sort_by_width);
        alignParams.height = getResources().getDimensionPixelSize(R.dimen.dialog_sort_by_height);;
    }

    public interface OnSortByListener {
        void onSortBy(int position, String sortBy, SortOrder sortOrder);
    }

    private OnSortByListener onSortByListener = new OnSortByListener() {
        @Override
        public void onSortBy(int position, String sortBy, SortOrder sortOrder) {
        }
    };

    public void setOnSortByListener(OnSortByListener l) {
        onSortByListener = l;
    }

    public boolean isShowSortOrderLayout() {
        return showSortOrderLayout;
    }

    public void setShowSortOrderLayout(boolean showSortOrderLayout) {
        this.showSortOrderLayout = showSortOrderLayout;
    }

    public void setCurrentSortBySelectedIndex(int index) {
        this.currentSortBySelectedIndex = index;
    }

    public void setCurrentSortOrderSelected(SortOrder sortOrder) {
        this.currentSortOrderSelected = sortOrder;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        initAlignLayoutParam();
        setParams(new Params().setEnableTittle(false)
                .setCustomContentLayoutResID(R.layout.dialog_sort_by)
                .setCustomLayoutBackgroundResId(R.drawable.dialog_square_border)
                .setDialogWidth(alignParams.width)
                .setCustomLayoutHeight(alignParams.height)
                .setCustomViewAction(new CustomViewAction() {
                    @Override
                    public void onCreateCustomView(View customView, TextView pageIndicator) {
                        initView(customView);
                    }
                })
                .setEnableFunctionPanel(false)
        );
        super.onCreate(savedInstanceState);
    }

    private void initView(View viewGroup) {
        initTitleView(viewGroup);
        initRadioGroupView(viewGroup);
        initContentPageView(viewGroup);
    }

    private void initTitleView(View viewGroup) {
        if (StringUtils.isNullOrEmpty(title)) {
            viewGroup.findViewById(R.id.dialog_tittleBar).setVisibility(View.GONE);
            return;
        }
        TextView textView = (TextView) viewGroup.findViewById(R.id.textView_title);
        textView.setText(title);
    }

    private void initRadioGroupView(View viewGroup) {
        orderRadioGroup = (RadioGroup) viewGroup.findViewById(R.id.radioGroupOrder);
        if (!isShowSortOrderLayout()) {
            orderRadioGroup.setVisibility(View.GONE);
            return;
        }
        RadioButton radioButton = (RadioButton) orderRadioGroup.getChildAt(currentSortOrderSelected == SortOrder.Asc ? 0 : 1);
        radioButton.setChecked(true);
    }

    private void initContentPageView(View customView) {
        contentPageView = (PageRecyclerView) customView.findViewById(R.id.content_pageView);
        contentPageView.setLayoutManager(new DisableScrollGridManager(getActivity()));
        contentPageView.setAdapter(new PageRecyclerView.PageAdapter<PageViewHolder>() {
            @Override
            public int getRowCount() {
                return getDataCount() > pageViewRowCount ? pageViewRowCount : getDataCount();
            }

            @Override
            public int getColumnCount() {
                return pageViewColCount;
            }

            @Override
            public int getDataCount() {
                return CollectionUtils.getSize(sortByList);
            }

            @Override
            public PageViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
                return new PageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_sort_by_item, parent, false));
            }

            @Override
            public void onPageBindViewHolder(PageViewHolder viewHolder, int position) {
                viewHolder.itemView.setTag(position);
                viewHolder.radioItem.setText(sortByList.get(position));
                viewHolder.radioItem.setChecked(position == currentSortBySelectedIndex);
            }
        });
    }

    private void processItemSelect(int position) {
        currentSortBySelectedIndex = position;
        contentPageView.getAdapter().notifyDataSetChanged();
        if (onSortByListener != null) {
            onSortByListener.onSortBy(position, sortByList.get(position),
                    orderRadioGroup.getCheckedRadioButtonId() == R.id.radioAsc ? SortOrder.Asc : SortOrder.Desc);
        }
        dismiss();
    }

    class PageViewHolder extends RecyclerView.ViewHolder {
        RadioButton radioItem;

        PageViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    processItemSelect((int) v.getTag());
                }
            });
            radioItem = (RadioButton) itemView.findViewById(R.id.radio_item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        alignDialog();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void alignDialog() {
        Window window = getDialog().getWindow();
        if (window != null && alignParams != null) {
            window.setGravity(alignParams.gravity);
            WindowManager.LayoutParams params = window.getAttributes();
            params.x = alignParams.x;
            params.y = alignParams.y;
            window.setAttributes(params);
        }
    }

    public void show(FragmentManager fm) {
        super.show(fm, this.getClass().getSimpleName());
    }

    public void setAlignParams(AlignLayoutParams params) {
        this.alignParams = params;
    }

    public static class AlignLayoutParams {
        public int gravity = Gravity.TOP | Gravity.RIGHT;
        public int x, y;
        public int width, height;

        public AlignLayoutParams(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public AlignLayoutParams(int gravity) {
            this.gravity = gravity;
        }
    }
}
