package com.onyx.edu.manager.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jiang.android.lib.adapter.expand.StickyRecyclerHeadersDecoration;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.model.v2.CloudGroup;
import com.onyx.android.sdk.data.model.v2.DeviceBind;
import com.onyx.android.sdk.data.model.v2.GroupUserInfo;
import com.onyx.android.sdk.data.model.v2.NeoAccountBase;
import com.onyx.android.sdk.data.request.cloud.CloudRequestChain;
import com.onyx.android.sdk.data.request.cloud.v2.CloudGroupRequest;
import com.onyx.android.sdk.data.request.cloud.v2.CloudGroupUserListRequest;
import com.onyx.android.sdk.data.utils.JSONObjectParseUtils;
import com.onyx.android.sdk.qrcode.utils.ScreenUtils;
import com.onyx.android.sdk.ui.utils.ToastUtils;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.InputMethodUtils;
import com.onyx.android.sdk.utils.NetworkUtil;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.edu.manager.AdminApplication;
import com.onyx.edu.manager.R;
import com.onyx.edu.manager.adapter.ContactAdapter;
import com.onyx.edu.manager.adapter.GroupSelectAdapter;
import com.onyx.edu.manager.adapter.ItemClickListener;
import com.onyx.edu.manager.event.DeviceBindCommitEvent;
import com.onyx.edu.manager.manager.ContentManager;
import com.onyx.edu.manager.model.ContactEntity;
import com.onyx.edu.manager.pinyin.CharacterParser;
import com.onyx.edu.manager.view.ui.DividerDecoration;
import com.onyx.edu.manager.view.ui.SideBar;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by suicheng on 2017/7/7.
 */
public class DeviceUserInfoFragment extends Fragment {

    @Bind(R.id.contact_recycler)
    RecyclerView contactRecyclerView;
    @Bind(R.id.contact_sidebar)
    SideBar contactSidebar;
    @Bind(R.id.contact_dialog)
    TextView contactTextDialog;
    @Bind(R.id.tv_mac_address)
    TextView macAddressTv;
    @Bind(R.id.edit_search)
    EditText contactSearch;

    @Bind(R.id.parent_group)
    LinearLayout parentGroupLayout;
    @Bind(R.id.child_group)
    RecyclerView childGroupLayout;
    @Bind(R.id.root_group)
    TextView rootGroupTv;

    private GroupUserInfo groupUserInfo;

    private ContactAdapter<ContactEntity> contactAdapter;
    private List<ContactEntity> deviceBindEntityList = new ArrayList<>();
    private List<ContactEntity> searchDeviceEntityList = new ArrayList<>();
    private CharacterParser characterParser = CharacterParser.getInstance();
    private Comparator pinyinComparator = new Comparator<ContactEntity>() {
        @Override
        public int compare(ContactEntity o1, ContactEntity o2) {
            return o1.sortLetter.compareTo(o2.sortLetter);
        }
    };

    private GroupSelectAdapter groupAdapter;
    private CloudGroup childGroup;
    private static CloudGroup rootGroup;
    private static List<CloudGroup> parentGroupList = new ArrayList<>();

    public static Fragment newInstance(GroupUserInfo groupUserInfo) {
        Bundle bundle = new Bundle();
        if (groupUserInfo != null) {
            bundle.putString(ContentManager.KEY_GROUP_USER_INFO, JSONObjectParseUtils.toJson(groupUserInfo));
        }
        Fragment fragment = new DeviceUserInfoFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bind_user_info_list, container, false);
        ButterKnife.bind(this, view);

        initToolbar(view);
        initView();
        initData();
        return view;
    }

    private void initView() {
        initChildGroupPageView();
        initContentPageView();
        initSideBarView();
        initContactSearchView();
        updateMacAddress(getGroupUserInfo().device);
        updateRootGroupView(rootGroup);
        updateParentGroupRef();
    }

    private void initToolbar(View parentView) {
        View view = parentView.findViewById(R.id.toolbar_header);
        TextView titleView = (TextView) view.findViewById(R.id.toolbar_title);
        titleView.setText(R.string.user_device_bind);
    }

    private void initContactSearchView() {
        contactSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    InputMethodUtils.hideInputKeyboard(getContext());
                    startSearch(contactSearch.getEditableText().toString());
                    return true;
                }
                return false;
            }
        });
    }

    private void startSearch(String searchText) {
        if (StringUtils.isNullOrEmpty(searchText)) {
            contactAdapter.setContactEntityList(deviceBindEntityList);
            return;
        }
        List<ContactEntity> list = new ArrayList<>();
        for (ContactEntity entity : deviceBindEntityList) {
            if (entity.username.contains(searchText) || entity.sortLetter.contains(searchText)) {
                list.add(entity);
            }
        }
        searchDeviceEntityList.clear();
        searchDeviceEntityList.addAll(list);
        contactAdapter.setContactEntityList(searchDeviceEntityList);
    }

    private void initSideBarView() {
        contactSidebar.setTextView(contactTextDialog);
        contactSidebar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                int position = contactAdapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    contactRecyclerView.getLayoutManager().scrollToPosition(position);
                }
            }
        });
    }

    private void updateMacAddress(DeviceBind deviceBind) {
        if (deviceBind == null) {
            return;
        }
        macAddressTv.setText(String.format(getString(R.string.mac_address_format), deviceBind.mac));
    }

    private void updateRootGroupView(CloudGroup cloudGroup) {
        if (cloudGroup != null) {
            rootGroup = cloudGroup;
            if (StringUtils.isNotBlank(rootGroup.name)) {
                rootGroupTv.setText(rootGroup.name);
            }
        }
    }

    private void updateParentGroupRef() {
        if(CollectionUtils.isNullOrEmpty(parentGroupList)) {
            return;
        }
        for (CloudGroup group : parentGroupList) {
            parentGroupLayout.addView(getParentGroupTextView(group));
        }
    }

    private boolean isUserDeviceBound(ContactEntity entity) {
        if (entity.accountInfo == null || CollectionUtils.isNullOrEmpty(entity.accountInfo.devices)) {
            return false;
        }
        return true;
    }

    private void initContentPageView() {
        contactRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        contactRecyclerView.setAdapter(contactAdapter = new ContactAdapter<ContactEntity>(getContext(),
                deviceBindEntityList) {
            @Override
            public String getMacAddress(ContactEntity entity) {
                return isUserDeviceBound(entity) ? getString(R.string.has_bound) : getString(R.string.has_no_bind);
            }
        });

        final StickyRecyclerHeadersDecoration headersDecor = new StickyRecyclerHeadersDecoration(contactAdapter);
        contactRecyclerView.addItemDecoration(headersDecor);
        contactRecyclerView.addItemDecoration(new DividerDecoration(getContext()));
        contactAdapter.setSwipeAble(false);
        contactAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                headersDecor.invalidateHeaders();
            }
        });
        contactAdapter.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(int position, View view) {
                if (!NetworkUtil.isStringValidMacAddress(groupUserInfo.device.mac)) {
                    ToastUtils.showToast(getContext().getApplicationContext(), R.string.mac_address_invalid_for_bind);
                    return;
                }
                startCommitDeviceBind(contactAdapter.getItem(position).accountInfo);
            }

            @Override
            public void onLongClick(int position, View view) {
            }
        });
    }

    private GroupUserInfo getGroupUserInfo() {
        if (groupUserInfo == null) {
            groupUserInfo = JSONObjectParseUtils.parseObject(getArguments().getString(ContentManager.KEY_GROUP_USER_INFO),
                    GroupUserInfo.class);
            if (groupUserInfo == null) {
                groupUserInfo = new GroupUserInfo();
            }
        }
        return groupUserInfo;
    }

    private void initData() {
        final CloudGroup lastGroup = getLastCloudGroup();
        loadGroupAndUserList(lastGroup, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (lastGroup == null) {
                    updateRootGroupView(childGroup);
                }
            }
        });
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
        loadGroupAndUserList(group);
    }

    private void loadGroupAndUserList(final CloudGroup group) {
        loadGroupAndUserList(group, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (group != null) {
                    addLibraryToParentRefList(group);
                }
                if (e != null && group != null && StringUtils.isNotBlank(group._id)) {
                    ToastUtils.showToast(request.getContext().getApplicationContext(), R.string.group_has_no_users);
                }
            }
        });
    }

    private void loadGroupAndUserList(CloudGroup group, final BaseCallback baseCallback) {
        final String groupId = group != null ? group._id : null;
        CloudRequestChain requestChain = new CloudRequestChain();
        addCloudGroupListRequest(requestChain, groupId, baseCallback);
        addCloudGroupUserListRequest(requestChain, groupId, baseCallback);
        requestChain.setAbortException(false);
        requestChain.execute(getContext(), AdminApplication.getCloudManager());
    }

    private void addCloudGroupListRequest(CloudRequestChain requestChain, final String groupId, final BaseCallback baseCallback) {
        final CloudGroupRequest groupListRequest = new CloudGroupRequest(String.valueOf(groupId));
        requestChain.addRequest(groupListRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                groupAdapter.setGroupContainer(childGroup = groupListRequest.getChildGroup());
                if (StringUtils.isNullOrEmpty(groupId)) {
                    BaseCallback.invoke(baseCallback, request, e);
                }
            }
        });
    }

    private void addCloudGroupUserListRequest(CloudRequestChain requestChain, String groupId,
                                              final BaseCallback baseCallback) {
        if (StringUtils.isNullOrEmpty(groupId)) {
            return;
        }
        final CloudGroupUserListRequest userListRequest = new CloudGroupUserListRequest(groupId);
        requestChain.addRequest(userListRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e == null) {
                    processUserList(userListRequest.getGroupUserList());
                }
                BaseCallback.invoke(baseCallback, request, e);
            }
        });
    }

    private void processUserList(List<NeoAccountBase> userInfoList) {
        if (CollectionUtils.isNullOrEmpty(userInfoList)) {
            userInfoList = new ArrayList<>();
        }
        List<ContactEntity> list = new ArrayList<>();
        for (NeoAccountBase accountBase : userInfoList) {
            ContactEntity entity = new ContactEntity();
            entity.username = accountBase.getName();
            entity.accountInfo = accountBase;
            list.add(entity);
        }
        deviceBindEntityList.clear();
        deviceBindEntityList.addAll(list);
        sortContactEntityList(deviceBindEntityList);
        contactAdapter.setContactEntityList(deviceBindEntityList);
    }

    private void sortContactEntityList(List<ContactEntity> contactEntityList) {
        for (ContactEntity contactEntity : contactEntityList) {
            String pinyin = characterParser.getSelling(getContactEntityUsername(contactEntity));
            String sortString = pinyin.substring(0, 1).toUpperCase();
            if (sortString.matches("[A-Z]")) {
                contactEntity.sortLetter = sortString;
            } else {
                contactEntity.sortLetter = sortString;
            }
        }
        Collections.sort(contactEntityList, pinyinComparator);
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
        loadGroupAndUserList(parentGroupList.get(index), null);
    }

    private static CloudGroup getLastCloudGroup() {
        if (CollectionUtils.isNullOrEmpty(parentGroupList)) {
            return rootGroup;
        }
        return parentGroupList.get(parentGroupList.size() - 1);
    }

    private String getContactEntityUsername(ContactEntity entity) {
        if (StringUtils.isNullOrEmpty(entity.username)) {
            entity.username = "------";
        }
        return entity.username.replaceAll("\\b", "")
                .replaceAll("\b", "");
    }

    private CloudGroup getWillAddToGroup() {
        CloudGroup lastGroup = getLastCloudGroup();
        if (lastGroup != null) {
            return lastGroup;
        }
        return rootGroup;
    }

    private void startCommitDeviceBind(NeoAccountBase accountBase) {
        CloudGroup willAddGroup = getWillAddToGroup();
        if (willAddGroup == null) {
            ToastUtils.showToast(getContext().getApplicationContext(), R.string.group_has_no_selection);
            return;
        }
        GroupUserInfo groupUserInfo = getGroupUserInfo();
        groupUserInfo.user = accountBase;
        groupUserInfo.groups.add(willAddGroup);
        DeviceBindCommitEvent bindCommitEvent = new DeviceBindCommitEvent(groupUserInfo);
        EventBus.getDefault().post(bindCommitEvent);
    }

    @OnClick(R.id.toolbar_back)
    public void onToolbarBackClick() {
        getActivity().finish();
    }

    @OnClick(R.id.tv_add_new_user)
    public void onAddNewUserClick() {
        startCommitDeviceBind(null);
    }

    @OnClick(R.id.root_group)
    public void onRootGroupClick() {
        parentGroupLayout.removeAllViews();
        parentGroupList.clear();
        deviceBindEntityList.clear();
        contactRecyclerView.getAdapter().notifyDataSetChanged();
        initData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        return processBackKey();
                    }
                }
                return false;
            }
        });
    }

    private boolean processBackKey() {
        if (CollectionUtils.isNullOrEmpty(parentGroupList)) {
            getActivity().onBackPressed();
            return false;
        }
        parentGroupLayout.removeViewAt(parentGroupLayout.getChildCount() - 1);
        parentGroupList.remove(parentGroupList.size() - 1);
        loadGroupAndUserList(getLastCloudGroup(), null);
        return true;
    }
}
