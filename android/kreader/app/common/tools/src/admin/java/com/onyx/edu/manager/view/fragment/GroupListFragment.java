package com.onyx.edu.manager.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.model.v2.CloudGroup;
import com.onyx.android.sdk.data.request.cloud.v2.CloudGroupRequest;
import com.onyx.android.sdk.qrcode.utils.ScreenUtils;
import com.onyx.android.sdk.ui.utils.ToastUtils;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.edu.manager.AdminApplication;
import com.onyx.edu.manager.R;
import com.onyx.edu.manager.adapter.GroupSelectAdapter;
import com.onyx.edu.manager.adapter.ItemClickListener;
import com.onyx.edu.manager.event.GroupSelectEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by suicheng on 2017/7/7.
 */
public class GroupListFragment extends Fragment {

    @Bind(R.id.parent_group)
    LinearLayout parentGroupLayout;
    @Bind(R.id.child_group)
    RecyclerView childGroupLayout;

    private GroupSelectAdapter groupAdapter;
    private CloudGroup childGroup;
    private List<CloudGroup> parentGroupList = new ArrayList<>();

    public static Fragment newInstance() {
        return new GroupListFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_select, container, false);
        ButterKnife.bind(this, view);

        initToolbar(view);
        initView();
        initData();
        return view;
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
        titleView.setText(R.string.node_select);
    }

    private void initView() {
        initChildGroupPageView();
    }

    private void initData() {
        loadGroup(null, null);
    }

    private void initChildGroupPageView() {
        childGroupLayout.setLayoutManager(new GridLayoutManager(getContext(), 3));
        childGroupLayout.setAdapter(groupAdapter = new GroupSelectAdapter(childGroup));
        groupAdapter.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(int position, View view) {
                processGroupItemClick(childGroup.children.get(position));
            }

            @Override
            public void onLongClick(int position, View view) {
            }
        });
    }

    private void processGroupItemClick(final CloudGroup group) {
        loadGroup(group, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (group != null) {
                    addLibraryToParentRefList(group);
                }
            }
        });
    }

    private void loadGroup(final CloudGroup group, final BaseCallback baseCallback) {
        final String groupId = group != null ? group._id : null;
        final CloudGroupRequest groupListRequest = new CloudGroupRequest(String.valueOf(groupId));
        AdminApplication.getCloudManager().submitRequest(getContext(), groupListRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null && StringUtils.isNotBlank(groupId)) {
                    return;
                }
                groupAdapter.setGroupContainer(childGroup = groupListRequest.getChildGroup());
                BaseCallback.invoke(baseCallback, request, e);
            }
        });
    }

    private void addLibraryToParentRefList(CloudGroup group) {
        if (!CollectionUtils.isNullOrEmpty(parentGroupList)) {
            if (parentGroupList.get(parentGroupList.size() - 1)._id.equals(group._id)) {
                return;
            }
        }
        parentGroupList.add(group);
        parentGroupLayout.addView(getParentGroupTextView(group));
    }

    private TextView getParentGroupTextView(CloudGroup group) {
        TextView tv = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.group_parent_item, null);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins((int) ScreenUtils.getDimenPixelSize(getContext(), 10), 0, 0, 0);
        tv.setLayoutParams(layoutParams);
        tv.setText(group.name);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processLibraryRefViewClick(v);
            }
        });
        return tv;
    }

    private void processLibraryRefViewClick(View v) {
        int index = parentGroupLayout.indexOfChild(v);
        if (index == parentGroupLayout.getChildCount() - 1) {
            return;
        }
        int removeCount = parentGroupLayout.getChildCount() - 1 - index;
        for (int i = 0; i < removeCount; i++) {
            parentGroupLayout.removeViewAt(parentGroupLayout.getChildCount() - 1);
            parentGroupList.remove(parentGroupList.size() - 1);
        }
        loadGroup(parentGroupList.get(index), null);
    }

    @OnClick(R.id.root_group)
    public void onRootGroupClick() {
        parentGroupLayout.removeAllViews();
        parentGroupList.clear();
        initData();
    }

    @OnClick(R.id.btn_select)
    public void onGroupSelectClick() {
        CloudGroup currentGroup = getCurrentGroup();
        if (currentGroup == null) {
            ToastUtils.showToast(getContext().getApplicationContext(), R.string.node_select_empty_tip);
            return;
        }
        GroupSelectEvent selectEvent = new GroupSelectEvent(currentGroup);
        EventBus.getDefault().post(selectEvent);
    }

    private CloudGroup getCurrentGroup() {
        if (CollectionUtils.isNullOrEmpty(parentGroupList)) {
            return null;
        }
        return parentGroupList.get(parentGroupList.size() - 1);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
