package com.onyx.android.edu.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onyx.android.edu.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ming on 16/8/17.
 */
public class ExerciseAdapter extends RecyclerView.Adapter {

    public interface CallBack{
        void OnClickItemListener();
    }

    private CallBack callBack;

    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ExerciseViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exercise, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ExerciseViewHolder exerciseViewHolder = (ExerciseViewHolder)holder;
        exerciseViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callBack != null){
                    callBack.OnClickItemListener();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return 4;
    }

    static class ExerciseViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.content)
        TextView content;
        @Bind(R.id.subject_name)
        TextView subjectName;
        @Bind(R.id.content_layout)
        LinearLayout contentLayout;

        public ExerciseViewHolder(View view) {
            super(view);
//            R.layout.item_exercise
            ButterKnife.bind(this, view);
        }
    }
}
