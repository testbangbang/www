package com.onyx.android.dr.statistics;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.onyx.android.dr.R;
import com.onyx.android.sdk.data.model.Book;
import com.onyx.android.sdk.data.model.StatisticsResult;
import com.onyx.android.sdk.ui.view.CommonViewHolder;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.utils.DateTimeUtil;

import org.apache.commons.collections4.map.LinkedMap;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ming on 2017/2/13.
 */

public class ReadRecordFragment extends StatisticsFragment {

    @Bind(R.id.page_category)
    PageRecyclerView pageCategory;
    @Bind(R.id.text_read_most)
    TextView textReadMost;
    @Bind(R.id.page_read_most)
    PageRecyclerView pageReadMost;
    @Bind(R.id.text_read_carefully)
    TextView textReadCarefully;
    @Bind(R.id.page_read_carefully)
    PageRecyclerView pageReadCarefully;
    @Bind(R.id.page_content)
    PageRecyclerView pageContent;

    private Map<String, String> categoryMap;
    private int[] categoryTitleIDs = {R.string.science, R.string.life, R.string.art, R.string.children_book,
            R.string.magazine, R.string.fiction, R.string.education, R.string.finance_and_economics};
    private Set<BookCategory> hasCategorys = new HashSet<>();

    public static ReadRecordFragment newInstance() {
        return new ReadRecordFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_read_record, container, false);
        ButterKnife.bind(this, view);
        initView();
        initCategoryPage();
        initReadMostPage();
        initReadCarefullyPage();
        return view;
    }

    private void initView() {
        textReadMost.setText(getContext().getString(R.string.read_longest_book, ""));
        textReadCarefully.setText(getContext().getString(R.string.read_most_carefully_book, ""));
    }

    private void loadBookName() {
        if (statisticsResult != null) {
            Book longestBook = statisticsResult.getLongestReadTimeBook();
            if (longestBook != null) {
                textReadMost.setText(getContext().getString(R.string.read_longest_book, longestBook.getName()));
            }
            Book carefullyBook = statisticsResult.getMostCarefulBook();
            if (carefullyBook != null) {
                textReadCarefully.setText(getContext().getString(R.string.read_most_carefully_book, carefullyBook.getName()));
            }
        }
    }

    private void initReadMostPage() {
        final int cols = 3;
        final int rows = 1;
        final int count = cols * rows;
        pageReadMost.setLayoutManager(new DisableScrollGridManager(getContext()));
        pageReadMost.setAdapter(new PageRecyclerView.PageAdapter() {
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
                return new CommonViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_common_statistics_item, parent, false));
            }

            @Override
            public void onPageBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                CommonViewHolder viewHolder = (CommonViewHolder) holder;
                setDashLineVisibility(viewHolder, position, count, cols);
                if (statisticsResult != null) {
                    Book book = statisticsResult.getLongestReadTimeBook();
                    if (book != null) {
                        switch (position) {
                            case 0:
                                String startTime = DateTimeUtil.formatDate(book.getBegin(), DateTimeUtil.DATE_FORMAT_YYYYMMDD_HHMM);
                                viewHolder.setText(R.id.content, getString(R.string.read_start_time, startTime));
                                break;
                            case 1:
                                String useTimes = DateTimeUtil.formatTime(getContext(), book.getReadingTime() / 1000);
                                viewHolder.setText(R.id.content, getString(R.string.read_use_time, useTimes));
                                break;
                            case 2:
                                String endTime = DateTimeUtil.formatDate(book.getEnd(), DateTimeUtil.DATE_FORMAT_YYYYMMDD_HHMM);
                                viewHolder.setText(R.id.content, getString(R.string.read_end_time, endTime));
                                break;
                        }
                    }

                }
            }
        });
    }

    private void initReadCarefullyPage() {
        final int cols = 3;
        final int rows = 1;
        final int count = cols * rows;
        pageReadCarefully.setLayoutManager(new DisableScrollGridManager(getContext()));
        pageReadCarefully.setAdapter(new PageRecyclerView.PageAdapter() {
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
                return new CommonViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_common_statistics_item, parent, false));
            }

            @Override
            public void onPageBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                CommonViewHolder viewHolder = (CommonViewHolder) holder;
                setDashLineVisibility(viewHolder, position, count, cols);
                if (statisticsResult != null) {
                    Book book = statisticsResult.getMostCarefulBook();
                    if (book != null) {
                        long count;
                        switch (position) {
                            case 0:
                                count = book.getTextSelect();
                                viewHolder.setText(R.id.content, getString(R.string.high_light_count, count));
                                break;
                            case 1:
                                count = book.getAnnotation();
                                viewHolder.setText(R.id.content, getString(R.string.annotation_count, count));
                                break;
                            case 2:
                                count = book.getLookupDic();
                                viewHolder.setText(R.id.content, getString(R.string.look_up_dict_count, count));
                                break;
                        }
                    }

                }
            }
        });
    }

    private void initCategoryPage() {
        final int cols = 2;
        final int rows = 4;
        final int count = cols * rows;
        pageCategory.setLayoutManager(new DisableScrollGridManager(getContext()));
        pageCategory.setAdapter(new PageRecyclerView.PageAdapter() {
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
                return new CommonViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_content_category_item, parent, false));
            }

            @Override
            public void onPageBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                CommonViewHolder viewHolder = (CommonViewHolder) holder;

                setDashLineVisibility(viewHolder, position, count, cols);
                String title = "";
                double value = 0;
                if (statisticsResult != null && statisticsResult.getBookTypeAgg() != null) {
                    LinkedMap<String, Double> bookTypeAgg = statisticsResult.getBookTypeAgg();
                    if (position < bookTypeAgg.size()) {
                        String typeId = bookTypeAgg.get(position);
                        if (categoryMap != null) {
                            BookCategory category = BookCategoryUtils.getBookCategory(categoryMap, typeId);
                            title = BookCategoryUtils.getCategoryName(getContext(), category);
                            hasCategorys.add(category);
                        }
                        value = bookTypeAgg.getValue(position) * 100;
                    } else {
                        for (BookCategory category : BookCategory.values()) {
                            if (!hasCategorys.contains(category)) {
                                title = BookCategoryUtils.getCategoryName(getContext(), category);
                                hasCategorys.add(category);
                                break;
                            }
                        }
                    }
                } else {
                    title = getString(categoryTitleIDs[position]);
                }
                viewHolder.setText(R.id.text_category, title);
                viewHolder.setText(R.id.text_proportion, String.format("%d%%", (int) value));
            }
        });
    }

    private void updateReadContentPage() {
        if (statisticsResult == null) {
            return;
        }
        final List<Book> recentBooks = statisticsResult.getRecentReadingBooks();
        if (recentBooks == null || recentBooks.size() == 0) {
            return;
        }
        final int cols = 1;
        final int rows = getResources().getInteger(R.integer.statistics_recently_book_count);
        final int count = cols * rows;
        pageContent.setLayoutManager(new DisableScrollGridManager(getContext()));
        pageContent.setAdapter(new PageRecyclerView.PageAdapter() {
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
                return recentBooks.size();
            }

            @Override
            public RecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
                return new CommonViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_read_content_item, parent, false));
            }

            @Override
            public void onPageBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                CommonViewHolder viewHolder = (CommonViewHolder) holder;
                Book book = recentBooks.get(position);
                viewHolder.setText(R.id.name, book.getName());
                String startTime = DateTimeUtil.formatDate(book.getBegin(), DateTimeUtil.DATE_FORMAT_YYYYMMDD_HHMM);
                String useTimes = DateTimeUtil.formatTime(getContext(), book.getReadingTime() / 1000);
                String endTime = DateTimeUtil.formatDate(book.getEnd(), DateTimeUtil.DATE_FORMAT_YYYYMMDD_HHMM);
                String str = String.format("%s ~ %s (%s)", startTime, endTime, useTimes);
                viewHolder.setText(R.id.value, str);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private void setDashLineVisibility(final CommonViewHolder viewHolder, final int position, final int count, final int cols) {
        int index = position + 1;
        viewHolder.setVisibility(R.id.right_dash_line, (index % cols == 0 && index / cols > 0) ? View.GONE : View.VISIBLE);
        viewHolder.setVisibility(R.id.bottom_dash_line, index > (count - cols) ? View.GONE : View.VISIBLE);
    }

    @Override
    public void refreshStatistics(StatisticsResult statisticsResult) {
        hasCategorys.clear();
        categoryMap = BookCategoryUtils.getCategoryMap(getContext());
        loadBookName();
        if (pageReadMost != null) {
            pageReadMost.notifyDataSetChanged();
        }
        if (pageReadCarefully != null) {
            pageReadCarefully.notifyDataSetChanged();
        }
        if (pageCategory != null) {
            pageCategory.notifyDataSetChanged();
        }
        updateReadContentPage();
    }
}
