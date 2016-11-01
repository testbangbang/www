package com.onyx.android.edu;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.onyx.android.edu.base.BaseActivity;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ming on 16/9/6.
 */
public class RecyclerViewTestActivity extends BaseActivity {

    private static final String TAG = RecyclerViewTestActivity.class.getSimpleName();

    @Bind(R.id.page_recycler_view)
    PageRecyclerView pageRecyclerView;
    @Bind(R.id.test_button)
    Button testButton;

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_recycler_test;
    }

    @Override
    protected void initView() {
        final List<Integer> datas = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            datas.add(i);
        }
        pageRecyclerView.setLayoutManager(new DisableScrollGridManager(this));
        PageRecyclerView.PageAdapter adapter = new PageRecyclerView.PageAdapter() {
            @Override
            public int getRowCount() {
                return 3;
            }

            @Override
            public int getColumnCount() {
                return 4;
            }

            @Override
            public int getDataCount() {
                return datas.size();
            }

            @Override
            public RecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
                return new ExerciseViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_test, parent, false));
            }

            @Override
            public void onPageBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
                ((ExerciseViewHolder) holder).title.setText(position + "");

                if (pageRecyclerView.getCurrentFocusedPosition() == position) {
                    ((ExerciseViewHolder) holder).title.setTextColor(getResources().getColor(R.color.white));
                } else {
                    ((ExerciseViewHolder) holder).title.setTextColor(getResources().getColor(R.color.black));
                }
            }
        };

        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datas.clear();
                for (int i = 0; i < 20; i++) {
                    datas.add(i);
                }
                pageRecyclerView.notifyDataSetChanged();
            }
        });
        pageRecyclerView.setAdapter(adapter);

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    static class ExerciseViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.title)
        TextView title;

        public ExerciseViewHolder(View view) {
            super(view);
//            R.layout.item_recycler_test
            ButterKnife.bind(this, view);
        }
    }

}
