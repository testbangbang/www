package com.onyx.android.dr.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.common.ActivityManager;
import com.onyx.android.dr.dialog.AlertInfoDialog;
import com.onyx.android.dr.presenter.ManageGroupPresenter;
import com.onyx.android.dr.util.DRPreferenceManager;
import com.onyx.android.dr.util.Utils;
import com.onyx.android.sdk.data.model.DeleteGroupMemberBean;
import com.onyx.android.sdk.data.model.v2.AllGroupBean;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.utils.StringUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by zhouzhiming on 2017/8/31.
 */
public class ManageGroupAdapter extends PageRecyclerView.PageAdapter<ManageGroupAdapter.ViewHolder> {
    private List<AllGroupBean> dataList;
    private OnItemClickListener onItemClickListener;
    private String groupOwner = "creator";
    private String groupMember = "user";
    private String tag = "";
    private ManageGroupPresenter presenter;
    private Context context;
    private AlertInfoDialog alertDialog;

    public void setDataList(Context context, List<AllGroupBean> dataList, ManageGroupPresenter presenter) {
        this.dataList = dataList;
        this.presenter = presenter;
        this.context = context;
    }

    @Override
    public int getRowCount() {
        return DRApplication.getInstance().getResources().getInteger(R.integer.item_group_info_row);
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
        View inflate = View.inflate(DRApplication.getInstance(), R.layout.item_manage_group_info, null);
        return new ViewHolder(inflate);
    }

    @Override
    public void onPageBindViewHolder(final ViewHolder holder, final int position) {
        final AllGroupBean bean = dataList.get(position);
        holder.serialNumber.setText(String.valueOf(position + 1));
        holder.groupName.setText(bean.name);
        if (bean.role.equals(groupOwner)) {
            holder.identity.setText(DRApplication.getInstance().getString(R.string.group_owner));
            holder.state.setText(DRApplication.getInstance().getResources().getString(R.string.examine));
        } else if(bean.role.equals(groupMember)) {
            holder.identity.setText(DRApplication.getInstance().getString(R.string.group_member));
            holder.state.setText(DRApplication.getInstance().getResources().getString(R.string.group_state_exit));
        }
        holder.peopleNumber.setText(bean.usersCount + tag);
        if (StringUtils.isNullOrEmpty(bean.applyCount + tag) || bean.applyCount == 0) {
            holder.news.setText(DRApplication.getInstance().getString(R.string.device_setting_no));
        } else {
            holder.news.setText(bean.applyCount + tag);
        }
        holder.state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = holder.state.getText().toString();
                if (text.equals(DRApplication.getInstance().getResources().getString(R.string.examine))){
                    ActivityManager.startGroupMemberActivity(DRApplication.getInstance(), bean._id);
                }else if (text.equals(DRApplication.getInstance().getResources().getString(R.string.group_state_exit))){
                    showDialog(bean._id, position);
                }
            }
        });
    }

    private void showDialog(final String childId, final int position) {
        alertDialog = new AlertInfoDialog(context, context.getString(R.string.exit_group_hint), true,
                context.getResources().getString(R.string.dialog_button_confirm), context.getResources().getString(R.string.dialog_button_cancel));
        Utils.setDialogAttributes(alertDialog);
        alertDialog.setOKOnClickListener(new AlertInfoDialog.OnOKClickListener() {
            @Override
            public void onOKClick(int value) {
                String parentId = DRPreferenceManager.loadParentId(DRApplication.getInstance(), "");
                DeleteGroupMemberBean deleteGroupMemberBean = new DeleteGroupMemberBean();
                String[] array = new String[]{childId};
                deleteGroupMemberBean.setGroups(array);
                presenter.exitGroup(parentId, deleteGroupMemberBean);
                DRPreferenceManager.saveExitGroupPosition(context, position + tag);
            }
        });
        alertDialog.setCancelOnClickListener(new AlertInfoDialog.OnCancelClickListener() {
            @Override
            public void onCancelClick() {
                if (alertDialog.isShowing()) {
                    alertDialog.dismiss();
                }
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
        @Bind(R.id.manage_group_info_item_serial_number)
        TextView serialNumber;
        @Bind(R.id.manage_group_info_item_group_name)
        TextView groupName;
        @Bind(R.id.manage_group_info_item_identity)
        TextView identity;
        @Bind(R.id.manage_group_info_item_people_number)
        TextView peopleNumber;
        @Bind(R.id.manage_group_info_item_news)
        TextView news;
        @Bind(R.id.manage_group_info_item_state)
        TextView state;
        View rootView;

        ViewHolder(View view) {
            super(view);
            this.rootView = view;
            ButterKnife.bind(this, view);
        }
    }
}
