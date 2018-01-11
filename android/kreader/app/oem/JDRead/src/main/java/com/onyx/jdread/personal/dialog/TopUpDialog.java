package com.onyx.jdread.personal.dialog;

import android.app.DialogFragment;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.DialogTopUpBinding;
import com.onyx.jdread.personal.action.GetTopUpValueAction;
import com.onyx.jdread.personal.adapter.TopUpAdapter;
import com.onyx.jdread.personal.cloud.entity.jdbean.TopUpValueBean;
import com.onyx.jdread.personal.model.PersonalDataBundle;
import com.onyx.jdread.shop.view.DividerItemDecoration;
import com.onyx.jdread.util.Utils;

import java.util.List;

/**
 * Created by li on 2017/12/29.
 */

public class TopUpDialog extends DialogFragment {
    private DialogTopUpBinding binding;
    private TopUpAdapter topUpAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = (DialogTopUpBinding) DataBindingUtil.inflate(inflater, R.layout.dialog_top_up_layout, container, false);
        initView();
        initData();
        initListener();
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        int screenWidth = Utils.getScreenWidth(JDReadApplication.getInstance());
        int screenHeight = Utils.getScreenHeight(JDReadApplication.getInstance());
        attributes.width = (int) (screenWidth * 0.8);
        attributes.height = (int) (screenHeight * 0.6);
        window.setAttributes(attributes);
    }

    private void initView() {
        binding.dialogTopUpRecycler.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
        DividerItemDecoration decoration = new DividerItemDecoration(JDReadApplication.getInstance(), DividerItemDecoration.VERTICAL_LIST);
        binding.dialogTopUpRecycler.addItemDecoration(decoration);
        topUpAdapter = new TopUpAdapter(PersonalDataBundle.getInstance().getEventBus());
        binding.dialogTopUpRecycler.setAdapter(topUpAdapter);
    }

    private void initData() {
        GetTopUpValueAction action = new GetTopUpValueAction();
        action.execute(PersonalDataBundle.getInstance(), new RxCallback() {
            @Override
            public void onNext(Object o) {
                List<TopUpValueBean> topValueBeans = PersonalDataBundle.getInstance().getTopValueBeans();
                topUpAdapter.setData(topValueBeans);
            }
        });
    }

    private void initListener() {
        if (topUpAdapter != null) {
            topUpAdapter.setOnItemClickListener(new PageRecyclerView.PageAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    dismiss();
                }
            });
        }

        binding.dialogTopUpClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
