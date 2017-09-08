package com.onyx.android.dr.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.common.ActivityManager;
import com.onyx.android.dr.data.database.InformalEssayEntity;
import com.onyx.android.dr.util.TimeUtils;
import com.onyx.android.sdk.ui.view.PageRecyclerView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by zhouzhiming on 17-7-11.
 */
public class SpeechRecordingAdapter extends PageRecyclerView.PageAdapter<SpeechRecordingAdapter.ViewHolder> {
    private List<InformalEssayEntity> dataList;
    private OnItemClickListener onItemClickListener;

    public void setDataList(List<InformalEssayEntity> dataList) {
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
        View inflate = View.inflate(DRApplication.getInstance(), R.layout.item_speech_recording, null);
        return new ViewHolder(inflate);
    }

    @Override
    public void onPageBindViewHolder(final ViewHolder holder, final int position) {
        InformalEssayEntity bean = dataList.get(position);
        long currentTime = bean.currentTime;
        holder.content.setText(bean.content);
        holder.time.setText(TimeUtils.getDate(currentTime));
        holder.title.setText(bean.title);
        holder.wordNumber.setText(bean.wordNumber);
        holder.startSpeech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InformalEssayEntity informalEssayEntity = dataList.get(position);
                ActivityManager.startSpeechRecordingActivity(DRApplication.getInstance(), informalEssayEntity.title, informalEssayEntity.content);
            }
        });
    }

    public interface OnItemClickListener {
        void setOnItemClick(int position, boolean isCheck);

        void setOnItemCheckedChanged(int position, boolean isCheck);
    }

    public void setOnItemListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.item_speech_recording_start_speech)
        ImageView startSpeech;
        @Bind(R.id.item_speech_recording_content)
        TextView content;
        @Bind(R.id.item_speech_recording_time)
        TextView time;
        @Bind(R.id.item_speech_recording_title)
        TextView title;
        @Bind(R.id.item_speech_recording_number)
        TextView wordNumber;
        View rootView;

        ViewHolder(View view) {
            super(view);
            this.rootView = view;
            ButterKnife.bind(this, view);
        }
    }
}
