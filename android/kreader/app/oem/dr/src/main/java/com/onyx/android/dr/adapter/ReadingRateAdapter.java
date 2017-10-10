package com.onyx.android.dr.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.data.database.ReadingRateEntity;
import com.onyx.android.dr.view.PageRecyclerView;
import com.onyx.android.sdk.utils.DateTimeUtil;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by zhouzhiming on 17-7-11.
 */
public class ReadingRateAdapter extends PageRecyclerView.PageAdapter<ReadingRateAdapter.ViewHolder> {
    private List<ReadingRateEntity> dataList;
    private OnItemClickListener onItemClickListener;

    public void setDataList(List<ReadingRateEntity> dataList) {
        this.dataList = dataList;
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
        return dataList == null ? 0 : dataList.size();
    }

    @Override
    public ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = View.inflate(DRApplication.getInstance(), R.layout.item_reading_rate, null);
        return new ViewHolder(inflate);
    }

    @Override
    public void onPageBindViewHolder(final ViewHolder holder, final int position) {
        ReadingRateEntity bean = dataList.get(position);
        String time = DateTimeUtil.formatDate(bean.time, DateTimeUtil.DATE_FORMAT_YYYYMMDD_HHMM);
        holder.time.setText(time);
        holder.bookName.setText(bean.bookName);
        holder.timeHorizon.setText(bean.timeHorizon);
        holder.readingSummary.setText(String.valueOf(bean.readSummaryPiece));
        holder.readerResponsePiece.setText(String.valueOf(bean.readerResponsePiece));
        holder.readerResponseNumber.setText(String.valueOf(bean.readerResponseNumber));
        holder.languageType.setText(bean.language);
        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }

    @Override
    public void onClick(View view) {
    }

    public interface OnItemClickListener {
        void setOnItemClick(int position, boolean isCheck);

        void setOnItemCheckedChanged(int position, boolean isCheck);
    }

    public void setOnItemListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.reading_rate_item_time)
        TextView time;
        @Bind(R.id.reading_rate_item_book_name)
        TextView bookName;
        @Bind(R.id.reading_rate_item_time_horizon)
        TextView timeHorizon;
        @Bind(R.id.reading_rate_item_reading_quantity)
        TextView readingQuantity;
        @Bind(R.id.reading_rate_item_language_type)
        TextView languageType;
        @Bind(R.id.reading_rate_item_speech)
        TextView speech;
        @Bind(R.id.reading_rate_item_reading_summary)
        TextView readingSummary;
        @Bind(R.id.reading_rate_item_reader_response_piece)
        TextView readerResponsePiece;
        @Bind(R.id.reading_rate_item_reader_response_number)
        TextView readerResponseNumber;
        View rootView;

        ViewHolder(View view) {
            super(view);
            this.rootView = view;
            ButterKnife.bind(this, view);
        }
    }
}
