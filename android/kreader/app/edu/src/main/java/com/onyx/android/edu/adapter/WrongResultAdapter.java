package com.onyx.android.edu.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.onyx.android.edu.R;
import com.onyx.android.edu.base.Config;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ming on 16/8/19.
 */
public class WrongResultAdapter extends RecyclerView.Adapter {

    private List<String> wrongCounts;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new WrongViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wrong_result, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        WrongViewHolder wrongViewHolder = (WrongViewHolder) holder;
        wrongViewHolder.exerciseCrown.setVisibility(position == 1 ? View.VISIBLE : View.INVISIBLE);
        wrongViewHolder.subjectIcon.setImageResource(Config.subjectResIds[position]);
        wrongViewHolder.subjectName.setText(Config.subjectNames[position]);
    }

    @Override
    public int getItemCount() {
        return Config.subjectNames.length;
    }

    static class WrongViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.wrong_count)
        Button wrongCount;
        @Bind(R.id.subject_icon)
        ImageView subjectIcon;
        @Bind(R.id.subject_name)
        TextView subjectName;
        @Bind(R.id.exercise_crown)
        ImageView exerciseCrown;

        public WrongViewHolder(View itemView) {
//            R.layout.item_wrong_result
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
