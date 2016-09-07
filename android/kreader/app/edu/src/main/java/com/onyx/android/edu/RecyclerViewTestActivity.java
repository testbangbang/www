package com.onyx.android.edu;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.onyx.android.edu.base.BaseActivity;
import com.onyx.android.sdk.ui.view.PageRecyclerView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ming on 16/9/6.
 */
public class RecyclerViewTestActivity extends BaseActivity {
    
    private static final String TAG = RecyclerViewTestActivity.class.getSimpleName();
    
    @Bind(R.id.page_recycler_view)
    PageRecyclerView pageRecyclerView;

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_recycler_test;
    }

    @Override
    protected void initView() {
        pageRecyclerView.setLayoutManager(new PageRecyclerView.DisableScrollGridManager(this));
        PageRecyclerView.PageAdapter adapter = new PageRecyclerView.PageAdapter() {
            @Override
            public int getRowCount() {
                return 4;
            }

            @Override
            public int getColumnCount() {
                return 4;
            }

            @Override
            public int getDataCount() {
                return 100;
            }

            @Override
            public RecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
                return new ExerciseViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_test, parent, false));
            }

            @Override
            public void onPageBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
                ((ExerciseViewHolder)holder).title.setText(position +"");

                if (pageRecyclerView.getCurrentFocusedPosition() == position){
                    ((ExerciseViewHolder)holder).title.setTextColor(getResources().getColor(R.color.white));
                }else {
                    ((ExerciseViewHolder)holder).title.setTextColor(getResources().getColor(R.color.black));
                }
            }
        };
        pageRecyclerView.setOnChangeFocusListener(new PageRecyclerView.OnChangeFocusListener() {
            @Override
            public void onNextFocus(int position) {
                Log.d(TAG, "onNextFocus: ");
            }

            @Override
            public void onPrevFocus(int position) {
                Log.d(TAG, "onPrevFocus: ");
            }
        });
        pageRecyclerView.setAdapter(adapter);

    }

    @Override
    protected void initData() {

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
