package com.onyx.android.edu.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.onyx.android.edu.R;
import com.onyx.android.sdk.ui.view.PageRecyclerView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ming on 16/8/18.
 */
public class ExercisePractiseAdapter extends PageRecyclerView.PageAdapter {

    public interface CallBack{
        void onItemClick();
    }

    private CallBack callBack;

    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }

    @Override
    public int getRowCount() {
        return 12;
    }

    @Override
    public int getColumnCount() {
        return 1;
    }

    @Override
    public int getDataCount() {
        return 14;
    }

    @Override
    public RecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        return new ExerciseResultViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exercise_practise, parent, false));
    }

    @Override
    public void onPageBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ExerciseResultViewHolder viewHolder = (ExerciseResultViewHolder) holder;
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callBack != null){
                    callBack.onItemClick();
                }
            }
        });
    }

    static class ExerciseResultViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.title)
        TextView title;
        @Bind(R.id.time)
        TextView time;
        @Bind(R.id.progress)
        TextView progress;

        public ExerciseResultViewHolder(View view) {
            super(view);
//            R.layout.item_exercise_practise
            ButterKnife.bind(this, view);
        }
    }
}
