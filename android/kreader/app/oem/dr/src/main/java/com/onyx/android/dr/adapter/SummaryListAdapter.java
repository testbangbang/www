package com.onyx.android.dr.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.reader.data.ReadSummaryEntity;
import com.onyx.android.dr.reader.event.ReadingSummaryMenuEvent;
import com.onyx.android.dr.view.PageRecyclerView;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by hehai on 17-9-20.
 */

public class SummaryListAdapter extends PageRecyclerView.PageAdapter<SummaryListAdapter.ViewHolder> {
    private List<ReadSummaryEntity> readSummaryList;

    public void setReadSummaryList(List<ReadSummaryEntity> readSummaryList) {
        this.readSummaryList = readSummaryList;
        notifyDataSetChanged();
    }

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
        return readSummaryList == null ? 0 : readSummaryList.size();
    }

    @Override
    public ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = View.inflate(parent.getContext(), R.layout.item_summary_list, null);
        return new ViewHolder(inflate);
    }

    @Override
    public void onPageBindViewHolder(ViewHolder holder, int position) {
        final ReadSummaryEntity entity = readSummaryList.get(position);
        holder.itemSummaryTime.setText(entity.time);
        holder.itemSummaryBookName.setText(entity.bookName);
        holder.itemSummaryPageNumber.setText(entity.pageNumber);
        if(StringUtils.isNullOrEmpty(entity.summary)){
            holder.itemSummaryBookAbstract.setText(DRApplication.getInstance().getString(R.string.nothing));
        }else{
            holder.itemSummaryBookAbstract.setText(entity.summary);
        }
        holder.itemSummaryCheckbox.setChecked(entity.isChecked);
        holder.itemSummaryCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                entity.isChecked = isChecked;
            }
        });
        holder.itemSummaryBookAbstract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReadingSummaryMenuEvent event = new ReadingSummaryMenuEvent();
                event.setPageNumber(entity.pageNumber);
                event.setBookName(entity.bookName);
                EventBus.getDefault().post(event);
            }
        });
    }

    @Override
    public void onClick(View v) {
    }

    static class ViewHolder extends PageRecyclerView.ViewHolder {
        @Bind(R.id.item_summary_time)
        TextView itemSummaryTime;
        @Bind(R.id.item_summary_book_name)
        TextView itemSummaryBookName;
        @Bind(R.id.item_summary_page_number)
        TextView itemSummaryPageNumber;
        @Bind(R.id.item_summary_book_abstract)
        TextView itemSummaryBookAbstract;
        @Bind(R.id.item_summary_checkbox)
        CheckBox itemSummaryCheckbox;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public List<ReadSummaryEntity> getSelectedList() {
        List<ReadSummaryEntity> selectedList = new ArrayList<>();
        for (ReadSummaryEntity entity : readSummaryList) {
            if (entity.isChecked) {
                selectedList.add(entity);
            }
        }
        return selectedList;
    }

    public void selectAll(boolean check){
        if (!CollectionUtils.isNullOrEmpty(readSummaryList)){
            for (ReadSummaryEntity entity : readSummaryList) {
                entity.isChecked = check;
            }
        }
        notifyDataSetChanged();
    }
}
