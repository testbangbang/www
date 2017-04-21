package com.onyx.android.eschool.dialog;

import android.app.FragmentManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.onyx.android.eschool.R;
import com.onyx.android.sdk.ui.dialog.OnyxAlertDialog;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.EllipsizingTextView;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.utils.CollectionUtils;

import java.util.Arrays;
import java.util.List;

/**
 * Created by suicheng on 2016/11/8.
 */

public class DialogMultiTextSingleChoice extends OnyxAlertDialog {
    private static int INVALID_VALUE = -1;
    private static int DEFAULT_PAGE_VIEW_ROW_COUNT = 6;

    private Handler handler = new Handler();
    private PageRecyclerView pageRecyclerView;
    private TextView pageIndicator;

    private String dialogTitle;
    private List<String> checkTexts;
    private int pageViewRowCount = DEFAULT_PAGE_VIEW_ROW_COUNT;
    private int index = INVALID_VALUE;

    private OnCheckListener listener;

    public DialogMultiTextSingleChoice() {
        super();
    }

    public DialogMultiTextSingleChoice(String dialogTitle, List<String> checkTexts) {
        this.dialogTitle = dialogTitle;
        this.checkTexts = checkTexts;
    }

    public DialogMultiTextSingleChoice(String dialogTitle, String[] checkTexts) {
        this(dialogTitle, Arrays.asList(checkTexts));
    }

    public interface OnCheckListener {
        void onChecked(int index, String checkText);
    }

    public void setOnCheckListener(OnCheckListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setParams(new Params().setTittleString(dialogTitle)
                .setCustomContentLayoutResID(R.layout.alert_dialog_content_recycler_view)
                .setCustomLayoutHeight(getCustomLayoutHeight())
                .setEnablePageIndicator(true)
                .setCanceledOnTouchOutside(false)
                .setCustomViewAction(new CustomViewAction() {
                    @Override
                    public void onCreateCustomView(View customView, TextView pageView) {
                        pageIndicator = pageView;
                        initPageRecyclerView(customView);
                    }
                })
                .setPositiveAction(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (index < 0) {
                            Toast.makeText(getActivity(), "提示:当前没有任何选择", Toast.LENGTH_LONG).show();
                            return;
                        }
                        dismiss();
                        if (listener != null) {
                            listener.onChecked(index, checkTexts.get(index));
                        }
                    }
                })
        );
        super.onCreate(savedInstanceState);
    }

    private void initPageRecyclerView(View customView) {
        pageRecyclerView = (PageRecyclerView) customView.findViewById(R.id.page_recycler_view);
        pageRecyclerView.setLayoutManager(new DisableScrollGridManager(getActivity()));
        pageRecyclerView.setOnPagingListener(new PageRecyclerView.OnPagingListener() {
            @Override
            public void onPageChange(int position, int itemCount, int pageSize) {
                updatePageIndicatorView();
            }
        });
        pageRecyclerView.setAdapter(new PageRecyclerView.PageAdapter() {
            @Override
            public int getRowCount() {
                return pageViewRowCount;
            }

            @Override
            public int getColumnCount() {
                return 1;
            }

            @Override
            public int getDataCount() {
                return CollectionUtils.getSize(checkTexts);
            }

            @Override
            public RecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
                return new PageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.alert_dialog_content_checkbox_text, parent, false));
            }

            @Override
            public void onPageBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                PageViewHolder viewHolder = (PageViewHolder) holder;
                viewHolder.itemView.setTag(position);

                viewHolder.checkBoxView.setChecked(index == position);
                viewHolder.titleView.setText(checkTexts.get(position));
            }
        });
        updatePageIndicatorView();
    }

    private void updatePageIndicatorView() {
        int currentPage = pageRecyclerView.getPaginator().getCurrentPage() + 1;
        int totalPage = pageRecyclerView.getPaginator().pages();
        if (totalPage == 0) {
            totalPage = 1;
        }
        pageIndicator.setText(currentPage + "/" + totalPage);
    }

    private void notifyDataChanged() {
        pageRecyclerView.notifyDataSetChanged();
    }

    private void processItemClick(View itemView, TextView textView, CheckBox checkBox) {
        int position = (int) itemView.getTag();
        if (position == index) {
            index = INVALID_VALUE;
        } else {
            index = position;
        }
        notifyDataChanged();
    }

    private void processCheckBoxClick(View itemView, CheckBox checkBox) {
        int position = (int) itemView.getTag();
        index = checkBox.isChecked() ? position : INVALID_VALUE;
        notifyDataChanged();
    }

    class PageViewHolder extends RecyclerView.ViewHolder {
        public CheckBox checkBoxView;
        public EllipsizingTextView titleView;

        public PageViewHolder(final View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    processItemClick(itemView, titleView, checkBoxView);
                }
            });
            titleView = (EllipsizingTextView) itemView.findViewById(R.id.ellipse_text_view);
            checkBoxView = (CheckBox) itemView.findViewById(R.id.checkbox_view);
            checkBoxView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    processCheckBoxClick(itemView, checkBoxView);
                }
            });
        }
    }

    private int getCustomLayoutHeight() {
        return 350;
    }

    public void show(FragmentManager fm) {
        super.show(fm, this.getClass().getSimpleName());
    }
}
