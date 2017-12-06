package com.onyx.knote.ui.dialog;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.onyx.android.sdk.scribble.data.AscDescOrder;
import com.onyx.android.sdk.scribble.data.SortBy;
import com.onyx.android.sdk.ui.dialog.OnyxAlertDialog;
import com.onyx.android.sdk.ui.view.DisableScrollLinearManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.knote.R;

import java.util.ArrayList;

public class DialogSortBy extends OnyxAlertDialog {
    private static final String TAG = DialogSortBy.class.getSimpleName();
    static final public String ARGS_SORT_BY = "args_sort_by";

    private ArrayList<SortByItem> sortByItemArrayList = new ArrayList<>();

    private
    @SortBy.SortByDef
    int currentSortBy;
    private PageRecyclerView sortByPageRecyclerView;
    private Callback mCallBack;

    private class SortByItem {
        public SortByItem(String title, int sortBy, boolean isActivated) {
            this.title = title;
            this.sortBy = sortBy;
            this.isActivated = isActivated;
        }

        private String title;
        private
        @SortBy.SortByDef
        int sortBy;
        private boolean isActivated;
    }

    public DialogSortBy setCallBack(Callback mCallBack) {
        this.mCallBack = mCallBack;
        return this;
    }

    public interface Callback {
        void onSortBy(@com.onyx.android.sdk.scribble.data.SortBy.SortByDef int sortBy, @AscDescOrder.AscDescOrderDef int ascOrder);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        currentSortBy = SortBy.translate(getArguments().getInt(ARGS_SORT_BY));
        setParams(new Params().setTittleString(getString(R.string.sort))
                .setCustomContentLayoutResID(R.layout.alert_dialog_content_sory_by)
                .setEnablePageIndicator(false)
                .setPositiveButtonText(getString(R.string.asc))
                .setNegativeButtonText(getString(R.string.desc))
                .setCustomLayoutHeight((int) (getSortByList().size() * getResources().getDimension(R.dimen.dialog_move_folder_item_height)))
                .setCustomViewAction(new CustomViewAction() {
                    @Override
                    public void onCreateCustomView(View customView, TextView pageIndicator) {
                        sortByPageRecyclerView = (PageRecyclerView) customView.findViewById(R.id.sort_by_page_recycler_view);
                        initRecyclerView(sortByPageRecyclerView);
                    }
                }).setPositiveAction(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mCallBack != null) {
                            mCallBack.onSortBy(currentSortBy, AscDescOrder.ASC);
                        }
                        dismiss();
                    }
                }).setNegativeAction(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mCallBack != null) {
                            mCallBack.onSortBy(currentSortBy, AscDescOrder.DESC);
                        }
                        dismiss();
                    }
                }));
        super.onCreate(savedInstanceState);
    }

    private void initRecyclerView(PageRecyclerView recyclerView) {
        recyclerView.setLayoutManager(new DisableScrollLinearManager(getActivity()));
        recyclerView.setAdapter(new PageRecyclerView.PageAdapter() {
            @Override
            public int getRowCount() {
                return getSortByList().size();
            }

            @Override
            public int getColumnCount() {
                return 1;
            }

            @Override
            public int getDataCount() {
                return getSortByList().size();
            }

            @Override
            public RecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
                return new DialogSortBy.SortByItemHolder(LayoutInflater.from(parent.getContext()).
                        inflate(R.layout.sort_by_item, parent, false));
            }

            @Override
            public void onPageBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                SortByItem item = getSortByList().get(position);
                ((DialogSortBy.SortByItemHolder) holder).bindView(pageRecyclerView, item);
            }
        });
    }

    private class SortByItemHolder extends RecyclerView.ViewHolder {
        private RadioButton sortByItem;

        public SortByItemHolder(View itemView) {
            super(itemView);
            sortByItem = (RadioButton) itemView.findViewById(R.id.radio_item);
        }

        public void bindView(final PageRecyclerView pageRecyclerView, final SortByItem item) {
            sortByItem.setText(item.title);
            sortByItem.setChecked(item.isActivated);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (SortByItem sortByItem : getSortByList()) {
                        if (sortByItem.sortBy == item.sortBy) {
                            sortByItem.isActivated = true;
                            currentSortBy = sortByItem.sortBy;
                        } else {
                            sortByItem.isActivated = false;
                        }
                    }
                    pageRecyclerView.notifyDataSetChanged();
                }
            });
        }
    }

    private ArrayList<SortByItem> getSortByList() {
        if (sortByItemArrayList.size() == 0) {
            sortByItemArrayList.add(new SortByItem(getString(R.string.updated_at),
                    SortBy.UPDATED_AT, currentSortBy == SortBy.UPDATED_AT));
            sortByItemArrayList.add(new SortByItem(getString(R.string.title),
                    SortBy.TITLE, currentSortBy == SortBy.TITLE));
            sortByItemArrayList.add(new SortByItem(getString(R.string.created_at),
                    SortBy.CREATED_AT, currentSortBy == SortBy.CREATED_AT));
            sortByItemArrayList.add(new SortByItem(getString(R.string.type),
                    SortBy.TYPE, currentSortBy == SortBy.TYPE));
        }
        return sortByItemArrayList;
    }

    public void show(FragmentManager fm) {
        show(fm, TAG);
    }

}
