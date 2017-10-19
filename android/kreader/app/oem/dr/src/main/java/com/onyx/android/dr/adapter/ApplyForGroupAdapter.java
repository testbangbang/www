package com.onyx.android.dr.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.dialog.AlertInfoDialog;
import com.onyx.android.dr.presenter.ApplyForGroupPresenter;
import com.onyx.android.dr.util.TimeUtils;
import com.onyx.android.sdk.data.model.v2.PendingGroupBean;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.utils.DateTimeUtil;
import com.onyx.android.sdk.utils.StringUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by zhouzhiming on 2017/8/31.
 */
public class ApplyForGroupAdapter extends PageRecyclerView.PageAdapter<ApplyForGroupAdapter.ViewHolder> {
    private List<PendingGroupBean> dataList;
    private OnItemClickListener onItemClickListener;
    private ApplyForGroupPresenter presenter;
    private Context context;
    private AlertInfoDialog alertDialog;

    public void setDataList(Context context, List<PendingGroupBean> dataList, ApplyForGroupPresenter presenter) {
        this.dataList = dataList;
        this.presenter = presenter;
        this.context = context;
    }

    @Override
    public int getRowCount() {
        return DRApplication.getInstance().getResources().getInteger(R.integer.manager_group_adapter_row);
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
        View inflate = View.inflate(DRApplication.getInstance(), R.layout.item_apply_for_group, null);
        return new ViewHolder(inflate);
    }

    @Override
    public void onPageBindViewHolder(final ViewHolder holder, final int position) {
        final PendingGroupBean bean = dataList.get(position);
        String time = DateTimeUtil.formatDate(bean.createdAt, TimeUtils.DATE_FORMAT_DATE);
        holder.time.setText(time);
        holder.proposer.setText(bean.user.name);
        if(StringUtils.isNullOrEmpty(bean.info)) {
            holder.applicationNote.setText(DRApplication.getInstance().getString(R.string.nothing));
        }else{
            holder.applicationNote.setText(bean.info);
        }
        if (bean.status == Constants.PENDING_TAG) {
            holder.operateContainer.setVisibility(View.VISIBLE);
            holder.alreadyPass.setVisibility(View.GONE);
        }else if(bean.status == Constants.PASS_TAG) {
            holder.operateContainer.setVisibility(View.GONE);
            holder.alreadyPass.setVisibility(View.VISIBLE);
            holder.alreadyPass.setText(DRApplication.getInstance().getString(R.string.apply_for_group_item_already_pass));
        }else if(bean.status == Constants.REFUSE_TAG){
            holder.operateContainer.setVisibility(View.GONE);
            holder.alreadyPass.setVisibility(View.VISIBLE);
            holder.alreadyPass.setText(DRApplication.getInstance().getString(R.string.apply_for_group_item_already_refuse));
        }
        holder.pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.disposePendingGroup(bean._id, Constants.PASS_TAG);
            }
        });
        holder.refuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.disposePendingGroup(bean._id, Constants.REFUSE_TAG);
            }
        });
    }

    public interface OnItemClickListener {
        void setOnItemClick(int position, boolean isCheck);
        void setOnItemCheckedChanged(int position, boolean isCheck);
    }

    public void setOnItemListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.apply_for_group_item_time)
        TextView time;
        @Bind(R.id.apply_for_group_item_proposer)
        TextView proposer;
        @Bind(R.id.apply_for_group_item_application_note)
        TextView applicationNote;
        @Bind(R.id.apply_for_group_item_pass)
        TextView pass;
        @Bind(R.id.apply_for_group_item_refuse)
        TextView refuse;
        @Bind(R.id.apply_for_group_item_already_pass)
        TextView alreadyPass;
        @Bind(R.id.apply_for_group_item_operate_container)
        LinearLayout operateContainer;
        View rootView;

        ViewHolder(View view) {
            super(view);
            this.rootView = view;
            ButterKnife.bind(this, view);
        }
    }
}
