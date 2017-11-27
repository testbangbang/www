package com.onyx.android.dr.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.event.ArticlePushEnterEBookEvent;
import com.onyx.android.dr.util.TimeUtils;
import com.onyx.android.dr.view.PageRecyclerView;
import com.onyx.android.sdk.data.model.ArticleInfoBean;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.DateTimeUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by zhouzhiming on 2017/11/27.
 */
public class ArticlePushAdapter extends PageRecyclerView.PageAdapter<ArticlePushAdapter.ViewHolder> {
    private List<ArticleInfoBean> dataList;

    public void setReadSummaryList(List<ArticleInfoBean> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    @Override
    public int getRowCount() {
        return DRApplication.getInstance().getResources().getInteger(R.integer.item_article_push_row);
    }

    @Override
    public int getColumnCount() {
        return DRApplication.getInstance().getResources().getInteger(R.integer.good_sentence_tab_column);
    }

    @Override
    public int getDataCount() {
        return dataList == null ? 0 : dataList.size();
    }

    @Override
    public ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = View.inflate(parent.getContext(), R.layout.item_article_push, null);
        return new ViewHolder(inflate);
    }

    @Override
    public void onPageBindViewHolder(ViewHolder holder, int position) {
        final ArticleInfoBean entity = dataList.get(position);
        holder.title.setText(entity.library.name);
        holder.author.setText(entity.target.authors.get(0));
        holder.bookName.setText("《" + entity.target.title + "》");
        String time = DateTimeUtil.formatDate(entity.target.createdAt, TimeUtils.DATE_FORMAT_DATE);
        holder.time.setText(time);
        holder.checkBox.setChecked(entity.isChecked);
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                entity.isChecked = isChecked;
            }
        });
        holder.enterBookstore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new ArticlePushEnterEBookEvent());
            }
        });
    }

    @Override
    public void onClick(View v) {

    }

    static class ViewHolder extends PageRecyclerView.ViewHolder {
        @Bind(R.id.item_article_push_title)
        TextView title;
        @Bind(R.id.item_article_push_author)
        TextView author;
        @Bind(R.id.item_article_push_book_name)
        TextView bookName;
        @Bind(R.id.item_article_push_language)
        TextView language;
        @Bind(R.id.item_article_push_time)
        TextView time;
        @Bind(R.id.item_article_push_enter_bookstore)
        TextView enterBookstore;
        @Bind(R.id.item_article_push_checkbox)
        CheckBox checkBox;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public List<ArticleInfoBean> getSelectedList() {
        List<ArticleInfoBean> selectedList = new ArrayList<>();
        for (ArticleInfoBean entity : dataList) {
            if (entity.isChecked) {
                selectedList.add(entity);
            }
        }
        return selectedList;
    }

    public void selectAll(boolean check){
        if (!CollectionUtils.isNullOrEmpty(dataList)){
            for (ArticleInfoBean entity : dataList) {
                entity.isChecked = check;
            }
        }
        notifyDataSetChanged();
    }
}
