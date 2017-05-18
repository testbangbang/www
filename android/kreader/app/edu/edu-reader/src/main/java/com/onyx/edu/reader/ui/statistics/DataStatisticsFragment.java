package com.onyx.edu.reader.ui.statistics;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.onyx.android.sdk.data.model.StatisticsResult;
import com.onyx.android.sdk.ui.view.CommonViewHolder;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.edu.reader.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ming on 2017/2/10.
 */

public class DataStatisticsFragment extends StatisticsFragment {

    private static final String TAG = "DataStatisticsFragment";
    protected String[] times = new String[] {
            "0:00", "6:00", "12:00", "18:00", "24:00"
    };

    @Bind(R.id.read_analysis_icon)
    ImageView readAnalysisIcon;
    @Bind(R.id.reading_rank_text)
    TextView readingRankText;
    @Bind(R.id.page_statistics)
    PageRecyclerView pageStatistics;
    @Bind(R.id.read_time_icon)
    ImageView readTimeIcon;
    @Bind(R.id.reading_time_text)
    TextView readingTimeText;
    @Bind(R.id.reading_chart)
    LineChart readingChart;

    public static DataStatisticsFragment newInstance() {
        return new DataStatisticsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_data_analysis, container, false);
        ButterKnife.bind(this, view);
        initView();
        fillLineChart();
        initGrid();
        return view;
    }

    private void initView() {
        readingTimeText.setText(getContext().getString(R.string.reading_time_every_day, 0.00f, 0, 0));
        loadReadRank(0.00f);
        readingChart.setTouchEnabled(false);
    }

    private void loadReadRank(double readingLevel) {
        readingLevel = readingLevel * 100;
        String title = getContext().getString(R.string.reading_level);
        title = String.format(title, readingLevel);
        SpannableString str = new SpannableString(title);
        String level = String.format("%.2f", readingLevel);
        int start = title.indexOf(level);
        str.setSpan(new RelativeSizeSpan(4f), start, start + level.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        readingRankText.setText(str);
    }

    private void initGrid() {
        final int rows = 2;
        final int cols = 2;
        final int count = rows * cols;
        pageStatistics.setLayoutManager(new DisableScrollGridManager(getContext()));
        pageStatistics.setAdapter(new PageRecyclerView.PageAdapter() {
            @Override
            public int getRowCount() {
                return rows;
            }

            @Override
            public int getColumnCount() {
                return cols;
            }

            @Override
            public int getDataCount() {
                return count;
            }

            @Override
            public RecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
                return new CommonViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_experience_analysis_item, parent, false));
            }

            @Override
            public void onPageBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                CommonViewHolder viewHolder = (CommonViewHolder) holder;
                int title = R.string.read_count;
                long value = 0;
                switch (position) {
                    case 0:
                        title = R.string.read_count;
                        value = statisticsResult == null ? 0 : statisticsResult.getEventTypeAgg().getRead();
                        viewHolder.setText(R.id.value, String.valueOf(value));
                        break;
                    case 1:
                        title = R.string.finish_count;
                        value = statisticsResult == null ? 0 : statisticsResult.getEventTypeAgg().getFinish();
                        viewHolder.setText(R.id.value, String.valueOf(value));
                        break;
                    case 2:
                        title = R.string.read_time;
                        String str = "0";
                        if (statisticsResult != null) {
                            float time = ((float) statisticsResult.getTotalReadTime()) / 3600 / 1000;
                            str = String.format("%.2f", time);
                        }
                        viewHolder.setText(R.id.value, str);
                        break;
                    case 3:
                        title = R.string.statistics_annotation_count;
                        value = statisticsResult == null ? 0 : statisticsResult.getEventTypeAgg().getAnnotation() + statisticsResult.getEventTypeAgg().getTextSelect();
                        viewHolder.setText(R.id.value, String.valueOf(value));
                        break;
                }
                viewHolder.setText(R.id.title, title);
                viewHolder.setVisibility(R.id.right_dash_line, position % cols != 0 ? View.GONE : View.VISIBLE);
                viewHolder.setVisibility(R.id.bottom_dash_line, position >= (count - cols) ? View.GONE : View.VISIBLE);
            }
        });
    }

    private void fillLineChart() {
        XAxis xAxis = readingChart.getXAxis();
        xAxis.setEnabled(true);
        xAxis.setLabelCount(times.length, true);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setDrawGridLines(false);
        xAxis.setAxisLineColor(Color.BLACK);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinimum(0);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return times[(int) (value + 1) / 6];
            }
        });

        YAxis ly = readingChart.getAxisLeft();
        ly.setEnabled(true);
        ly.setDrawGridLines(true);
        ly.setLabelCount(5, false);
        ly.setAxisMinimum(0);
        ly.setTextColor(Color.WHITE);
        ly.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);

        readingChart.getAxisRight().setEnabled(false);
        readingChart.getDescription().setEnabled(false);
        readingChart.getLegend().setEnabled(false);
    }

    private void loadReadTimeDistribution() {
        if (statisticsResult == null) {
            return;
        }
        List<Integer> selfReadTimeDis = statisticsResult.getMyEventHourlyAgg();
        if (selfReadTimeDis == null || selfReadTimeDis.size() == 0) {
            return;
        }

        ArrayList<Entry> selfVals = new ArrayList<Entry>();
        for (int i = 0; i < selfReadTimeDis.size(); i++) {
            selfVals.add(new Entry(i, selfReadTimeDis.get(i)));
        }
        ArrayList<Entry> otherVals = new ArrayList<Entry>();
        List<Integer> otherReadTimeDis = statisticsResult.getEventHourlyAgg();
        if (otherReadTimeDis != null) {
            for (int i = 0; i < otherReadTimeDis.size(); i++) {
                otherVals.add(new Entry(i, otherReadTimeDis.get(i)));
            }
        }
        readingChart.setData(generateLineData(otherVals, selfVals));
        readingChart.invalidate();
    }

    private void loadAverageReadTime() {
        if (statisticsResult == null) {
            return;
        }
        long readTimeEveryDay = statisticsResult.getDailyAvgReadTime();
        float hour = ((float) readTimeEveryDay) / 1000 / 3600;
        List<Integer> selfReadTimeDis = statisticsResult.getMyEventHourlyAgg();
        int disStart = 0;
        int maxRecord = 0;
        if (selfReadTimeDis != null && selfReadTimeDis.size() > 0) {
            for (int i = 0; i < selfReadTimeDis.size(); i++) {
                if ((i + 2) < selfReadTimeDis.size()) {
                    int max = selfReadTimeDis.get(i) + selfReadTimeDis.get(i + 1) + selfReadTimeDis.get(i + 2);
                    if (max > maxRecord) {
                        maxRecord = max;
                        disStart = i;
                    }
                }
            }
        }
        readingTimeText.setText(getContext().getString(R.string.reading_time_every_day, hour, disStart, disStart + 2));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    protected LineData generateLineData(ArrayList<Entry> yVals1, ArrayList<Entry> yVals2) {
        ArrayList<ILineDataSet> sets = new ArrayList<ILineDataSet>();

        LineDataSet ds1 = new LineDataSet(yVals1, "");
        LineDataSet ds2 = new LineDataSet(yVals2, "");

        ds1.setColor(Color.GRAY);
        ds2.setColor(Color.BLACK);

        ds1.setFillColor(Color.GRAY);
        ds2.setFillColor(Color.BLACK);

        ds1.setLineWidth(4.0f);
        ds2.setLineWidth(4.0f);

        initLineDataSet(ds1);
        initLineDataSet(ds2);

        sets.add(ds1);
        sets.add(ds2);

        LineData d = new LineData(sets);
        return d;
    }

    private void initLineDataSet(LineDataSet set) {
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setCubicIntensity(0.2f);
        set.setDrawFilled(true);
        set.setDrawCircles(false);
        set.setCircleRadius(4f);
        set.setFillAlpha(200);
        set.setDrawHorizontalHighlightIndicator(false);
        set.setFillFormatter(new IFillFormatter() {
            @Override
            public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                return -10;
            }
        });
        set.setDrawValues(false);
    }

    @Override
    public void refreshStatistics(StatisticsResult statisticsResult) {
        if (pageStatistics != null) {
            pageStatistics.notifyDataSetChanged();
        }
        loadReadRank(statisticsResult == null ? 0 : statisticsResult.getReadingLevel());
        loadReadTimeDistribution();
        loadAverageReadTime();
    }
}
