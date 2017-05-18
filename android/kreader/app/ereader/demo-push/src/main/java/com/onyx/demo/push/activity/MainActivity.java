package com.onyx.demo.push.activity;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.onyx.android.sdk.ui.activity.OnyxAppCompatActivity;
import com.onyx.android.sdk.ui.view.DisableScrollLinearManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.demo.push.R;
import com.onyx.demo.push.events.PushLoadFinishedEvent;
import com.onyx.demo.push.model.PushContent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends OnyxAppCompatActivity {

    @Bind(R.id.content_pageView)
    PageRecyclerView contentPageView;

    private List<PushContent> productList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPushProductEvent(PushLoadFinishedEvent event) {
        PushContent product = event.product;
        if (product != null) {
            productList.add(product);
            notifyDataChanged(productList);
        }
    }

    private void notifyDataChanged(List<PushContent> list) {
        productList = list;
        contentPageView.notifyDataSetChanged();
    }

    private void initView() {
        contentPageView.setLayoutManager(new DisableScrollLinearManager(this));
        contentPageView.setAdapter(new PageRecyclerView.PageAdapter<PushItemViewHolder>() {
            @Override
            public int getRowCount() {
                return 6;
            }

            @Override
            public int getColumnCount() {
                return 1;
            }

            @Override
            public int getDataCount() {
                return CollectionUtils.getSize(productList);
            }

            @Override
            public PushItemViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
                return new PushItemViewHolder(LayoutInflater.from(MainActivity.this).inflate(R.layout.push_item, null));
            }

            @Override
            public void onPageBindViewHolder(PushItemViewHolder holder, int position) {
                holder.productName.setText(productList.get(position).name);
                holder.productUrl.setText(productList.get(position).url);
            }
        });
    }

    class PushItemViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.product_name)
        TextView productName;
        @Bind(R.id.product_url)
        TextView productUrl;

        public PushItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}