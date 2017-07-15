package com.onyx.edu.manager.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.onyx.android.sdk.ui.utils.ToastUtils;
import com.onyx.android.sdk.utils.ActivityUtil;
import com.onyx.android.sdk.utils.NetworkUtil;
import com.onyx.edu.manager.R;
import com.onyx.edu.manager.adapter.FuncSelectAdapter;
import com.onyx.edu.manager.adapter.ItemClickListener;
import com.onyx.edu.manager.model.FuncItemEntity;
import com.onyx.edu.manager.view.activity.AccountInfoActivity;
import com.onyx.edu.manager.view.activity.QrScannerActivity;
import com.onyx.edu.manager.view.activity.UserManagerActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by suicheng on 2017/7/8.
 */
public class FuncSelectFragment extends Fragment {
    @Bind(R.id.content_pageView)
    RecyclerView contentPageView;

    private FuncSelectAdapter selectAdapter;
    private List<FuncItemEntity> funcItemEntityList = new ArrayList<>();

    public static Fragment newInstance() {
        return new FuncSelectFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_func_select, container, false);
        ButterKnife.bind(this, view);
        initView((ViewGroup) view);
        initData();

        return view;
    }

    private void initView(ViewGroup parentView) {
        initToolbar(parentView);
        contentPageView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        contentPageView.setAdapter(selectAdapter = new FuncSelectAdapter(funcItemEntityList));
        selectAdapter.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(int position, View view) {
                ActivityUtil.startActivitySafely(view.getContext(), funcItemEntityList.get(position).intent);
            }

            @Override
            public void onLongClick(int position, View view) {
            }
        });
    }

    private void initToolbar(View parentView) {
        View view = parentView.findViewById(R.id.toolbar_header);
        view.findViewById(R.id.toolbar_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        TextView titleView = (TextView) view.findViewById(R.id.toolbar_title);
        titleView.setText(R.string.main_item_func_select);
    }

    private void initData() {
        if (!NetworkUtil.isWiFiConnected(getContext())) {
            ToastUtils.showToast(getContext().getApplicationContext(), R.string.network_is_not_connected);
        }
        funcItemEntityList.addAll(loadData());
        selectAdapter.notifyDataSetChanged();
    }

    private List<FuncItemEntity> loadData() {
        List<FuncItemEntity> itemEntityList = new ArrayList<>();
        itemEntityList.add(FuncItemEntity.create(R.mipmap.ic_code, getString(R.string.main_item_scanner),
                new Intent(getContext(), QrScannerActivity.class)));
        itemEntityList.add(FuncItemEntity.create(R.mipmap.ic_manage, getString(R.string.main_item_user_manager),
                new Intent(getContext(), UserManagerActivity.class)));
        itemEntityList.add(FuncItemEntity.create(R.mipmap.ic_account, getString(R.string.main_item_account_info),
                new Intent(getContext(), AccountInfoActivity.class)));
        return itemEntityList;
    }
}
