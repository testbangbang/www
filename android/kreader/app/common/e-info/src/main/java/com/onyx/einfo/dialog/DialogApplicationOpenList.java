package com.onyx.einfo.dialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.onyx.android.sdk.data.AppDataInfo;
import com.onyx.android.sdk.ui.dialog.OnyxAlertDialog;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.einfo.R;

import java.util.ArrayList;
import java.util.List;

public class DialogApplicationOpenList extends OnyxAlertDialog {

    private ImageView buttonNext;
    private ImageView buttonPrevious;
    private TextView textViewPage;
    private PageRecyclerView contentView;
    private CheckBox checkBoxDefaultOpen;
    private int pageRowCount = 4;

    private List<AppDataInfo> appDataInfoList = new ArrayList<>();

    private DialogInterface.OnKeyListener keyAction;

    public interface OnApplicationSelectedListener {
        void onApplicationSelected(AppDataInfo info, boolean makeDefault);
    }

    private OnApplicationSelectedListener appSelectedListener = new OnApplicationSelectedListener() {
        @Override
        public void onApplicationSelected(AppDataInfo info, boolean makeDefault) {
        }
    };

    public void setKeyAction(DialogInterface.OnKeyListener keyAction) {
        this.keyAction = keyAction;
    }

    public void setOnApplicationSelectedListener(OnApplicationSelectedListener l) {
        appSelectedListener = l;
    }

    public DialogApplicationOpenList(List<AppDataInfo> list) {
        appDataInfoList = list;
    }

    private int getContentRowCount() {
        int dataSize = CollectionUtils.getSize(appDataInfoList);
        return dataSize >= pageRowCount ? pageRowCount : dataSize;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setParams(new OnyxAlertDialog.Params()
                .setEnableTittle(false)
                .setCustomContentLayoutResID(R.layout.dialog_application_openlist)
                .setCustomLayoutHeight((int) (getResources().getDimensionPixelSize(R.dimen.dialog_app_open_list_item_height)
                        * (getContentRowCount() + 0.4f)))
                .setKeyAction(keyAction)
                .setCustomViewAction(new OnyxAlertDialog.CustomViewAction() {
                    @Override
                    public void onCreateCustomView(View customView, TextView pageIndicator) {
                        initView(customView);
                    }
                }).setEnableFunctionPanel(false));
        super.onCreate(savedInstanceState);
    }

    private void initView(View parentView) {
        initContentView(parentView);
        initTitleBarView(parentView);
        initCheckBoxView(parentView);
        setShowingPagingButton();
    }

    private void setShowingPagingButton() {
        if (contentView.getPaginator().pages() > 1) {
            buttonPrevious.setVisibility(View.VISIBLE);
            buttonNext.setVisibility(View.VISIBLE);
        } else {
            buttonPrevious.setVisibility(View.GONE);
            buttonNext.setVisibility(View.GONE);
        }
    }

    private void initCheckBoxView(View parentView) {
        checkBoxDefaultOpen = (CheckBox) parentView.findViewById(R.id.checkbox_default_open);
        checkBoxDefaultOpen.setChecked(false);
    }

    private void initTitleBarView(View parentView) {
        TextView dialogTittleView = (TextView) parentView.findViewById(R.id.textView_title);
        dialogTittleView.setText(R.string.open_with);
        buttonNext = (ImageView) parentView.findViewById(R.id.button_next);
        buttonPrevious = (ImageView) parentView.findViewById(R.id.button_previous);
        textViewPage = (TextView) parentView.findViewById(R.id.page_size_indicator);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contentView.nextPage();
            }
        });
        buttonPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contentView.prevPage();
            }
        });
        updatePageView();
    }

    private void updatePageView() {
        textViewPage.setText(contentView.getPaginator().getProgressText());
    }

    private void initContentView(View parentView) {
        contentView = (PageRecyclerView) parentView.findViewById(R.id.apps_contentView);
        contentView.setLayoutManager(new DisableScrollGridManager(contentView.getContext()));
        contentView.setOnPagingListener(new PageRecyclerView.OnPagingListener() {
            @Override
            public void onPageChange(int position, int itemCount, int pageSize) {
                updatePageView();
            }
        });
        contentView.setAdapter(new PageRecyclerView.PageAdapter<PageViewHolder>() {
            @Override
            public int getRowCount() {
                return getContentRowCount();
            }

            @Override
            public int getColumnCount() {
                return 1;
            }

            @Override
            public int getDataCount() {
                return CollectionUtils.getSize(appDataInfoList);
            }

            @Override
            public PageViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
                return new PageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_application_openlist_item, parent, false));
            }

            @Override
            public void onPageBindViewHolder(PageViewHolder holder, int position) {
                holder.itemView.setTag(position);

                AppDataInfo info = appDataInfoList.get(position);
                holder.appNameView.setText(info.labelName);
                holder.appIconImage.setImageDrawable(info.iconDrawable);
            }
        });
    }

    private void processItemClick(int position) {
        AppDataInfo appDataInfo = appDataInfoList.get(position);
        appSelectedListener.onApplicationSelected(appDataInfo, checkBoxDefaultOpen.isChecked());
        dismiss();
    }

    class PageViewHolder extends RecyclerView.ViewHolder {
        private ImageView appIconImage;
        private TextView appNameView;

        private PageViewHolder(final View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    processItemClick((Integer) v.getTag());
                }
            });
            appNameView = (TextView) itemView.findViewById(R.id.textView_app_name);
            appIconImage = (ImageView) itemView.findViewById(R.id.imageView_app_cover);
        }
    }
}
