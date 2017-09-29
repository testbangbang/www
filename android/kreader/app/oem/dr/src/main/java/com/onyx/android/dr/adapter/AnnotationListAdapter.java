package com.onyx.android.dr.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.bean.AnnotationStatisticsBean;
import com.onyx.android.dr.util.TimeUtils;
import com.onyx.android.dr.view.PageRecyclerView;
import com.onyx.android.sdk.utils.CollectionUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by hehai on 17-9-27.
 */

public class AnnotationListAdapter extends PageRecyclerView.PageAdapter<AnnotationListAdapter.ViewHolder> {
    private List<AnnotationStatisticsBean> readAnnotationList;

    @Override
    public int getRowCount() {
        return DRApplication.getInstance().getResources().getInteger(R.integer.good_sentence_notebook_row);
    }

    @Override
    public int getColumnCount() {
        return DRApplication.getInstance().getResources().getInteger(R.integer.good_sentence_tab_column);
    }

    @Override
    public int getDataCount() {
        return readAnnotationList == null ? 0 : readAnnotationList.size();
    }

    @Override
    public ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = View.inflate(parent.getContext(), R.layout.item_annotation_list, null);
        return new ViewHolder(inflate);
    }

    @Override
    public void onPageBindViewHolder(ViewHolder holder, int position) {
        final AnnotationStatisticsBean statisticsBean = readAnnotationList.get(position);
        String time = "";
        try {
            time = TimeUtils.formatDate(statisticsBean.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.itemAnnotationTime.setText(time);
        holder.itemAnnotationBookName.setText(statisticsBean.getBook().getName());
        holder.itemAnnotationLibrary.setText(statisticsBean.getLibrary().getName());
        holder.itemAnnotationCount.setText(String.valueOf(statisticsBean.getCount()));
        holder.itemAnnotationCheckbox.setChecked(statisticsBean.isChecked());
        holder.itemAnnotationCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                statisticsBean.setChecked(isChecked);
            }
        });
    }

    public void setReadAnnotationList(List<AnnotationStatisticsBean> readAnnotationList) {
        this.readAnnotationList = readAnnotationList;
        notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {

    }

    static class ViewHolder extends PageRecyclerView.ViewHolder {
        @Bind(R.id.item_annotation_time)
        TextView itemAnnotationTime;
        @Bind(R.id.item_annotation_book_name)
        TextView itemAnnotationBookName;
        @Bind(R.id.item_annotation_library)
        TextView itemAnnotationLibrary;
        @Bind(R.id.item_annotation_count)
        TextView itemAnnotationCount;
        @Bind(R.id.item_annotation_checkbox)
        CheckBox itemAnnotationCheckbox;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public List<AnnotationStatisticsBean> getSelectedList() {
        List<AnnotationStatisticsBean> selectedList = new ArrayList<>();
        for (AnnotationStatisticsBean entity : readAnnotationList) {
            if (entity.isChecked()) {
                selectedList.add(entity);
            }
        }
        return selectedList;
    }

    public void selectAll(boolean check) {
        if (!CollectionUtils.isNullOrEmpty(readAnnotationList)) {
            for (AnnotationStatisticsBean entity : readAnnotationList) {
                entity.setChecked(check);
            }
        }
        notifyDataSetChanged();
    }
}
