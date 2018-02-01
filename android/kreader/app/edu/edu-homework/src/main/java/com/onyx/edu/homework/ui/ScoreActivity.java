package com.onyx.edu.homework.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.model.StatisticsResult;
import com.onyx.android.sdk.data.model.homework.Question;
import com.onyx.android.sdk.data.model.homework.StaticRankResult;
import com.onyx.android.sdk.ui.view.CommonViewHolder;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.utils.NetworkUtil;
import com.onyx.edu.homework.DataBundle;
import com.onyx.edu.homework.R;
import com.onyx.edu.homework.action.CheckWifiAction;
import com.onyx.edu.homework.action.StaticRankAction;
import com.onyx.edu.homework.action.note.ShowExitDialogAction;
import com.onyx.edu.homework.base.BaseActivity;
import com.onyx.edu.homework.data.ScoreItemType;
import com.onyx.edu.homework.databinding.ActivityScoreBinding;
import com.onyx.edu.homework.event.ExitEvent;
import com.onyx.edu.homework.request.StaticRankRequest;

import org.greenrobot.eventbus.Subscribe;

import java.util.Locale;

/**
 * Created by lxm on 2018/1/29.
 */

public class ScoreActivity extends BaseActivity {

    private static final int TOP_TITLE_ITEM_TYPE = 0;
    private static final int LIST_SCORE_ITEM_TYPE = 1;

    private ActivityScoreBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_score);
        getDataBundle().register(this);
        initView();
        checkWifiConnection();
    }

    private void initView() {
        String title = getDataBundle().getHomework().title;
        binding.toolbar.title.setText(title);
        binding.toolbar.backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ShowExitDialogAction().execute(ScoreActivity.this, null);
            }
        });
        binding.returnQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        binding.nextPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.list.nextPage();
            }
        });
        binding.prevPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.list.prevPage();
            }
        });
        initScoreList();
    }

    private void initScoreList() {
        final int columns = 5;
        final int rows = 14;
        binding.list.setOnPagingListener(new PageRecyclerView.OnPagingListener() {
            @Override
            public void onPageChange(int position, int itemCount, int pageSize) {
                updatePageIndex();
            }
        });
        binding.list.setLayoutManager(new DisableScrollGridManager(this));
        binding.list.setAdapter(new PageRecyclerView.PageAdapter() {
            @Override
            public int getRowCount() {
                return rows;
            }

            @Override
            public int getColumnCount() {
                return columns;
            }

            @Override
            public int getDataCount() {
                return getDataBundle().getHomework().questions.size()*columns + columns;
            }

            @Override
            public RecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
                return new CommonViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.score_list_item, null));
            }

            @Override
            public void onPageBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                CommonViewHolder viewHolder = (CommonViewHolder)holder;
                int type = getItemViewType(position);

                if (type == TOP_TITLE_ITEM_TYPE) {
                    ScoreItemType scoreItemType = ScoreItemType.getValue(position);
                    viewHolder.setText(R.id.content, scoreItemType.getTitleResId());
                }else {
                    int index = (position - columns) / columns;
                    ScoreItemType scoreItemType = ScoreItemType.getValue(position % columns);
                    Question question = getDataBundle().getHomework().questions.get(index);
                    bindScoreContentItem(viewHolder, question, index, scoreItemType);
                }
                boolean firstColumn = position % columns == 0;
                viewHolder.setVisibility(R.id.left_line, firstColumn ? View.VISIBLE : View.GONE);
            }

            @Override
            public int getItemViewType(int position) {
                return position < columns ? TOP_TITLE_ITEM_TYPE : LIST_SCORE_ITEM_TYPE;
            }
        });
    }

    private void bindScoreContentItem(CommonViewHolder viewHolder, Question question, int index, ScoreItemType scoreItemType) {
        String content = null;
        switch (scoreItemType) {
            case QUESTION_NUMBER: {
                content = String.valueOf(index + 1);
            }
                break;
            case QUESTION_TYPE: {
                int questionIndex = Math.max(question.QuesType - 1, 0);
                content = getResources().getStringArray(R.array.question_type_list)[questionIndex];
            }
                break;
            case STATE: {
                content = question.review != null && question.review.isRightAnswer() ? "√" : "×";
            }
                break;
            case SCORE_VALUE:
                content = String.format(Locale.getDefault(), "%.1f", question.score);
                break;
            case SCORED: {
                content = String.format(Locale.getDefault(), "%.1f", question.review != null ? question.review.score : 0f);
            }
                break;
        }
        viewHolder.setText(R.id.content, content);
    }

    private void updatePageIndex() {
        binding.page.setText(String.format(Locale.getDefault(), "%d/%d", binding.list.getPaginator().getCurrentPage() + 1, binding.list.getPaginator().pages()));
    }

    private void checkWifiConnection() {
        if (NetworkUtil.isWiFiConnected(this)) {
            getStaticRank();
            return;
        }
        final CheckWifiAction checkWifiAction = new CheckWifiAction();
        checkWifiAction.execute(this, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (checkWifiAction.isConnected()) {
                    getStaticRank();
                }
            }
        });
    }

    private void getStaticRank() {
        new StaticRankAction(getDataBundle().getPublicHomeworkId()).execute(this, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                StaticRankRequest rankRequest = (StaticRankRequest) request;
                if (rankRequest.isSuccess()) {
                    StaticRankResult result = rankRequest.getStaticRank();
                    binding.rank.setText(getString(R.string.rank_str, result.position + 1, result.total));
                }
            }
        });
    }

    @Override
    public boolean isFullScreen() {
        return true;
    }

    public DataBundle getDataBundle() {
        return DataBundle.getInstance();
    }

    @Subscribe
    public void onExitEvent(ExitEvent exitEvent) {
        finish();
    }

    @Override
    protected void onDestroy() {
        getDataBundle().unregister(this);
        super.onDestroy();
    }
}
