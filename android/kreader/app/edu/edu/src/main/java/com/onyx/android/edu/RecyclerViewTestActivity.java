package com.onyx.android.edu;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.onyx.android.edu.base.BaseActivity;
import com.onyx.android.sdk.ui.view.AdaptiveHeightPageAdapter;
import com.onyx.android.sdk.ui.view.AdaptiveWidthPageAdapter;
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

    @Bind(R.id.wrap_width_page)
    PageRecyclerView wrapWidthPage;
    @Bind(R.id.wrap_height_page)
    PageRecyclerView wrapHeightPage;
    @Bind(R.id.test_button)
    Button testButton;

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_recycler_test;
    }

    @Override
    protected void initView() {
        initWrapWidthPage();
        initWrapHeightPage();
    }

    private void initWrapWidthPage() {
        final List<Integer> datas = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            datas.add(i);
        }

        wrapWidthPage.setLayoutManager(new DisableScrollGridManager(this));
        PageRecyclerView.PageAdapter adapter = new AdaptiveWidthPageAdapter() {
            @Override
            public int getRowCount() {
                return 1;
            }

            @Override
            public int getColumnCount() {
                return 6;
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
            }
        };
        wrapWidthPage.setAdapter(adapter);
    }

    private void initWrapHeightPage() {
        final List<Integer> datas = new ArrayList<>();
        for (int i = 0; i < 23; i++) {
            datas.add(i);
        }

        wrapHeightPage.setLayoutManager(new DisableScrollGridManager(this));
        PageRecyclerView.PageAdapter adapter = new AdaptiveHeightPageAdapter() {
            @Override
            public int getRowCount() {
                return 10;
            }

            @Override
            public int getColumnCount() {
                return 1;
            }

            @Override
            public int getDataCount() {
                return datas.size();
            }

            @Override
            public RecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
                return new ExerciseViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_warp_heigh_test, parent, false));
            }

            @Override
            public void onPageBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
                ((ExerciseViewHolder) holder).title.setText(position + "");
            }
        };
        wrapHeightPage.setAdapter(adapter);
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
