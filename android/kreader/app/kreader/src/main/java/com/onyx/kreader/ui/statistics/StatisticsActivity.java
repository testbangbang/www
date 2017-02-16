package com.onyx.kreader.ui.statistics;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.CloudStore;
import com.onyx.android.sdk.data.model.StatisticsResult;
import com.onyx.android.sdk.data.request.cloud.GetStatisticsRequest;
import com.onyx.android.sdk.ui.view.OnyxCustomViewPager;
import com.onyx.kreader.R;
import com.onyx.kreader.ui.dialog.DialogLoading;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ming on 2017/2/9.
 */

public class StatisticsActivity extends ActionBarActivity {

    @Bind(R.id.back_icon)
    ImageView backIcon;
    @Bind(R.id.pager)
    OnyxCustomViewPager pager;
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.page)
    TextView page;

    private CloudStore cloudStore;
    private DataStatisticsFragment dataStatisticsFragment;
    private ReadRecordFragment readRecordFragment;
    private DialogLoading dialogLoading;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    private void initView() {
        pager.setPagingEnabled(false);
        pager.setUseGesturesPage(true);
        pager.setUseKeyPage(true);
        PageAdapter adapter = new PageAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                switch (position) {
                    case 0:
                        page.setText("1/2");
                        title.setText(R.string.data_analysis);
                        break;
                    case 1:
                        page.setText("2/2");
                        title.setText(R.string.reading_record);
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initData() {
        cloudStore = new CloudStore();
        dataStatisticsFragment = DataStatisticsFragment.newInstance();
        readRecordFragment = ReadRecordFragment.newInstance();
        dialogLoading = new DialogLoading(this,
                getString(R.string.loading), false, null);
        dialogLoading.show();
        final GetStatisticsRequest statisticsRequest = new GetStatisticsRequest(this);
        cloudStore.submitRequest(this, statisticsRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                StatisticsResult statisticsResult = statisticsRequest.getStatisticsResult();
                dataStatisticsFragment.setStatisticsResult(statisticsResult);
                readRecordFragment.setStatisticsResult(statisticsResult);
                if (dialogLoading != null) {
                    dialogLoading.dismiss();
                }
            }
        });
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

    private class PageAdapter extends FragmentPagerAdapter {

        public PageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment f = null;
            switch (position) {
                case 0:
                    f = dataStatisticsFragment;
                    page.setText("1/2");
                    title.setText(R.string.data_analysis);
                    break;
                case 1:
                    page.setText("2/2");
                    title.setText(R.string.reading_record);
                    f = readRecordFragment;
            }
            return f;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
