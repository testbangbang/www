package com.onyx.android.libsetting.view.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.ViewGroup;

import com.onyx.android.libsetting.R;
import com.onyx.android.libsetting.databinding.ActivityLocaleSelectBinding;
import com.onyx.android.libsetting.databinding.LocaleInfoItemBinding;
import com.onyx.android.libsetting.model.LocaleInfo;
import com.onyx.android.libsetting.util.InputMethodLanguageSettingUtil;
import com.onyx.android.libsetting.util.SettingRecyclerViewUtil;
import com.onyx.android.libsetting.view.BindingViewHolder;
import com.onyx.android.libsetting.view.PageRecyclerViewItemClickListener;
import com.onyx.android.libsetting.view.SettingPageAdapter;
import com.onyx.android.sdk.ui.activity.OnyxAppCompatActivity;
import com.onyx.android.sdk.ui.view.DisableScrollLinearManager;
import com.onyx.android.sdk.ui.view.OnyxPageDividerItemDecoration;
import com.onyx.android.sdk.ui.view.PageRecyclerView;

import java.util.List;

public class LocaleSelectActivity extends OnyxAppCompatActivity {
    ActivityLocaleSelectBinding binding;
    List<LocaleInfo> dataList;
    SettingPageAdapter<LanguageItemViewHolder, LocaleInfo> adapter;
    OnyxPageDividerItemDecoration itemDecoration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_locale_select);
        initSupportActionBarWithCustomBackFunction();
        initRecyclerView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateData();
    }

    private void updateData() {
        dataList = InputMethodLanguageSettingUtil.buildLanguageList(this);
        adapter.setDataList(dataList);
        binding.languageSelectRecyclerView.notifyDataSetChanged();
    }

    private void initRecyclerView() {
        PageRecyclerView resultRecyclerView = binding.languageSelectRecyclerView;
        resultRecyclerView.setHasFixedSize(true);
        resultRecyclerView.setLayoutManager(new DisableScrollLinearManager(this));
        buildAdapter();
        itemDecoration = new OnyxPageDividerItemDecoration(this, OnyxPageDividerItemDecoration.VERTICAL);
        resultRecyclerView.addItemDecoration(itemDecoration);
        itemDecoration.setActualChildCount(adapter.getRowCount());
        resultRecyclerView.setItemDecorationHeight(itemDecoration.getDivider().getIntrinsicHeight());
        resultRecyclerView.setAdapter(adapter);
    }

    private void buildAdapter() {
        adapter = new SettingPageAdapter<LanguageItemViewHolder, LocaleInfo>() {
            @Override
            public int getRowCount() {
                return LocaleSelectActivity.this.getResources().getInteger(R.integer.locale_per_page_item_count);
            }

            @Override
            public int getColumnCount() {
                return 1;
            }

            @Override
            public LanguageItemViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
                return new LanguageItemViewHolder(LocaleInfoItemBinding.inflate(getLayoutInflater(), parent, false));
            }

            @Override
            public void onPageBindViewHolder(LanguageItemViewHolder holder, int position) {
                super.onPageBindViewHolder(holder, position);
                SettingRecyclerViewUtil.updateItemDecoration(pageRecyclerView, this, itemDecoration);
                holder.bindTo(getDataList().get(position));
            }
        };

        adapter.setItemClickListener(new PageRecyclerViewItemClickListener<LocaleInfo>() {
            @Override
            public void itemClick(final LocaleInfo localeInfo) {
                InputMethodLanguageSettingUtil.updateLocale(localeInfo.getLocale());
                onBackPressed();
            }
        });
    }

    private class LanguageItemViewHolder extends BindingViewHolder<LocaleInfoItemBinding, LocaleInfo> {
        LanguageItemViewHolder(LocaleInfoItemBinding binding) {
            super(binding);
        }

        public void bindTo(LocaleInfo localeInfo) {
            mBinding.setLocaleInfo(localeInfo);
            mBinding.executePendingBindings();
        }

    }
}
