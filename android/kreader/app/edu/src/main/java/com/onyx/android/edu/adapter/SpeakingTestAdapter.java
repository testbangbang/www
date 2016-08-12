package com.onyx.android.edu.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onyx.android.edu.R;
import com.onyx.android.edu.db.model.AtomicQuiz;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by ming on 16/7/6.
 */
public class SpeakingTestAdapter extends RecyclerView.Adapter<SpeakingTestAdapter.ViewHolder> {

    List<AtomicQuiz> mAtomicQuizList;

    public SpeakingTestAdapter(List<AtomicQuiz> atomicQuizList) {
        mAtomicQuizList = atomicQuizList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_speaking_test, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
//        holder.mSoundLayout.setVisibility(View.GONE);
        holder.mTranslateText.setVisibility(View.VISIBLE);
        holder.mTranslateText.setText(position + "");
    }

    @Override
    public int getItemCount() {
        return mAtomicQuizList == null ? 0 : mAtomicQuizList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.sound_layout)
        LinearLayout mSoundLayout;
        @Bind(R.id.translate_text)
        TextView mTranslateText;

        public ViewHolder(View view) {
            super(view);
//            R.layout.item_speaking_test
            ButterKnife.bind(this, view);
        }
    }
}
