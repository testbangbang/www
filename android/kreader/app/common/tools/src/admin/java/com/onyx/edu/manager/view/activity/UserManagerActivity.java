package com.onyx.edu.manager.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.jiang.android.lib.adapter.expand.StickyRecyclerHeadersDecoration;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.common.ContentException;
import com.onyx.android.sdk.data.model.Device;
import com.onyx.android.sdk.data.model.v2.AccountCommon;
import com.onyx.android.sdk.data.model.v2.CloudGroup;
import com.onyx.android.sdk.data.model.v2.DeviceBind;
import com.onyx.android.sdk.data.model.v2.GroupUserInfo;
import com.onyx.android.sdk.data.model.v2.NeoAccountBase;
import com.onyx.android.sdk.data.request.cloud.CloudRequestChain;
import com.onyx.android.sdk.data.request.cloud.v2.AccountUnBindByDeviceRequest;
import com.onyx.android.sdk.data.request.cloud.v2.CloudGroupRequest;
import com.onyx.android.sdk.data.request.cloud.v2.CloudGroupUserListRequest;
import com.onyx.android.sdk.data.request.cloud.v2.CloudUserInfoByMacRequest;
import com.onyx.android.sdk.data.utils.JSONObjectParseUtils;
import com.onyx.android.sdk.qrcode.utils.ScreenUtils;
import com.onyx.android.sdk.ui.utils.ToastUtils;
import com.onyx.android.sdk.utils.ActivityUtil;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.InputMethodUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.edu.manager.AdminApplication;
import com.onyx.edu.manager.R;
import com.onyx.edu.manager.adapter.ContactAdapter;
import com.onyx.edu.manager.adapter.GroupSelectAdapter;
import com.onyx.edu.manager.adapter.ItemClickListener;
import com.onyx.edu.manager.event.DataRefreshEvent;
import com.onyx.edu.manager.manager.ContentManager;
import com.onyx.edu.manager.model.ContactEntity;
import com.onyx.edu.manager.pinyin.CharacterParser;
import com.onyx.edu.manager.pinyin.PinyinComparator;
import com.onyx.edu.manager.view.ui.DividerDecoration;
import com.onyx.edu.manager.view.ui.SideBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by suicheng on 2017/7/8.
 */
public class UserManagerActivity extends AppCompatActivity {
    @Bind(R.id.parent_group)
    LinearLayout parentGroupLayout;
    @Bind(R.id.child_group)
    RecyclerView childGroupLayout;
    @Bind(R.id.root_group)
    TextView rootGroupTv;

    @Bind(R.id.contact_recycler)
    RecyclerView contactRecyclerView;
    @Bind(R.id.contact_sidebar)
    SideBar contactSidebar;
    @Bind(R.id.contact_dialog)
    TextView contactTextDialog;
    @Bind(R.id.edit_search)
    EditText contactSearch;

    private ContactAdapter<ContactEntity> contactAdapter;
    private List<ContactEntity> contactEntityList = new ArrayList<>();
    private List<ContactEntity> searchContactEntityList = new ArrayList<>();
    private CharacterParser characterParser = CharacterParser.getInstance();
    private PinyinComparator pinyinComparator = new PinyinComparator();

    private GroupSelectAdapter groupAdapter;
    private CloudGroup rootGroup;
    private CloudGroup childGroup;
    private List<CloudGroup> parentGroupList = new ArrayList<>();

    private GroupUserInfo groupUserInfoSelected;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_manager);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        initView();
        initData();
    }

    private void initView() {
        initToolbar();
        initChildGroupPageView();
        initContentPageView();
        initContactSearchView();
        initSideBarView();
    }

    private void initContactSearchView() {
        contactSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    InputMethodUtils.hideInputKeyboard(UserManagerActivity.this);
                    startSearch(contactSearch.getEditableText().toString());
                    return true;
                }
                return false;
            }
        });
    }

    private void startSearch(String searchText) {
        if (StringUtils.isNullOrEmpty(searchText)) {
            contactAdapter.setContactEntityList(contactEntityList);
            return;
        }
        List<ContactEntity> list = new ArrayList<>();
        for (ContactEntity entity : contactEntityList) {
            if (entity.username.contains(searchText) || entity.sortLetter.contains(searchText)) {
                list.add(entity);
            }
        }
        searchContactEntityList.clear();
        searchContactEntityList.addAll(list);
        contactAdapter.setContactEntityList(searchContactEntityList);
    }

    private void initToolbar() {
        View view = findViewById(R.id.toolbar_header);
        view.findViewById(R.id.toolbar_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView titleView = (TextView) view.findViewById(R.id.toolbar_title);
        titleView.setText(R.string.main_item_user_manager);
        TextView otherTextView = (TextView) view.findViewById(R.id.toolbar_other);
        otherTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNewUserItemClick();
            }
        });
        otherTextView.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.icon_add_white), null, null, null);
    }

    private void onNewUserItemClick() {
        startActivity(new Intent(this, QrScannerActivity.class));
    }

    private void initData() {
        loadInitData();
    }

    private void loadInitData() {
        loadGroupAndUserList(getLastCloudGroup(), new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (childGroup != null) {
                    rootGroup = childGroup;
                    if (StringUtils.isNotBlank(rootGroup.name)) {
                        rootGroupTv.setText(rootGroup.name);
                    }
                }
            }
        });
    }

    private void initChildGroupPageView() {
        childGroupLayout.setLayoutManager(new GridLayoutManager(this, 3));
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
        requestChain.execute(this, AdminApplication.getCloudManager());
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
        contactEntityList.clear();
        contactEntityList.addAll(list);
        sortContactEntityList(contactEntityList);
        contactAdapter.setContactEntityList(contactEntityList);
    }

    private String getContactEntityUsername(ContactEntity entity) {
        if (StringUtils.isNullOrEmpty(entity.username)) {
            entity.username = "------";
        }
        return entity.username.replaceAll("\\b", "")
                .replaceAll("\b", "");
    }

    private String getContactEntityMac(ContactEntity entity) {
        if (entity.accountInfo == null || entity.accountInfo.getFirstDevice() == null) {
            return null;
        }
        return entity.accountInfo.getFirstDevice().macAddress;
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
        TextView tv = (TextView) LayoutInflater.from(this).inflate(R.layout.group_parent_item, null);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins((int) ScreenUtils.getDimenPixelSize(this, 10), 0, 0, 0);
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

    private CloudGroup getLastCloudGroup() {
        if (CollectionUtils.isNullOrEmpty(parentGroupList)) {
            return rootGroup;
        }
        return parentGroupList.get(parentGroupList.size() - 1);
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

    private void initContentPageView() {
        contactRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        contactRecyclerView.setAdapter(contactAdapter = new ContactAdapter<ContactEntity>(this, contactEntityList) {
            @Override
            public String getMacAddress(ContactEntity entity) {
                return getContactEntityMac(entity);
            }
        });
        contactRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState != RecyclerView.SCROLL_STATE_IDLE) {
                    contactAdapter.closeOpenedSwipeLayout(null);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            }
        });

        final StickyRecyclerHeadersDecoration headersDecor = new StickyRecyclerHeadersDecoration(contactAdapter);
        contactRecyclerView.addItemDecoration(headersDecor);
        contactRecyclerView.addItemDecoration(new DividerDecoration(this));
        contactAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                headersDecor.invalidateHeaders();
            }
        });
        contactAdapter.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(int position, View view) {
                startUserDeviceBindActivity(contactAdapter.getItem(position));
            }

            @Override
            public void onLongClick(int position, View view) {

            }
        });
        contactAdapter.setItemUnbundledClickListener(new ItemClickListener() {
            @Override
            public void onClick(int position, View view) {
                showDeviceUnbundledDialog(contactAdapter.getItem(position));
            }

            @Override
            public void onLongClick(int position, View view) {
            }
        });
    }

    private void startUserDeviceBindActivity(final ContactEntity entity) {
        GroupUserInfo groupUserInfo = new GroupUserInfo();
        groupUserInfo.user = entity.accountInfo;
        groupUserInfo.groups.add(getLastCloudGroup());
        groupUserInfo.device = new DeviceBind();
        if (groupUserInfo.user != null && !CollectionUtils.isNullOrEmpty(groupUserInfo.user.devices)) {
            groupUserInfo.device.mac = groupUserInfo.user.getFirstDevice().macAddress;
            groupUserInfo.device.model = groupUserInfo.user.getFirstDevice().model;
        }
        groupUserInfoSelected = groupUserInfo;
        Intent intent = new Intent(this, DeviceBindingActivity.class);
        intent.putExtra(ContentManager.KEY_GROUP_USER_INFO, JSONObjectParseUtils.toJson(groupUserInfo));
        ActivityUtil.startActivitySafely(this, intent);
    }

    private void showDeviceUnbundledDialog(final ContactEntity entity) {
        if (StringUtils.isNullOrEmpty(getContactEntityMac(entity))) {
            ToastUtils.showToast(getApplicationContext(), R.string.mac_address_invalid);
            return;
        }
        String content = StringUtils.getBlankStr(entity.username) + "\n" + getContactEntityMac(entity);
        new MaterialDialog.Builder(this)
                .theme(Theme.LIGHT)
                .title(R.string.user_device_un_bind)
                .content(content)
                .contentGravity(GravityEnum.CENTER)
                .positiveText(R.string.ok)
                .positiveColorRes(R.color.colorPrimary)
                .negativeText(R.string.cancel)
                .negativeColorRes(R.color.darker_gray)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        startAccountUnbindByDevice(entity);
                    }
                }).show();
    }

    private void startAccountUnbindByDevice(final ContactEntity entity) {
        Device device = entity.accountInfo.getFirstDevice();
        DeviceBind deviceBind = new DeviceBind();
        deviceBind.mac = device.macAddress;
        deviceBind.model = device.model;
        deviceBind.info = JSONObjectParseUtils.parseObject(entity.accountInfo.info, AccountCommon.class);
        final AccountUnBindByDeviceRequest unBindByDeviceRequest = new AccountUnBindByDeviceRequest(entity.accountInfo,
                deviceBind);
        AdminApplication.getCloudManager().submitRequest(this, unBindByDeviceRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null || !unBindByDeviceRequest.isSuccessResult()) {
                    ToastUtils.showToast(getApplicationContext(), R.string.device_unbind_fail);
                    return;
                }
                processUnBindSuccess(entity);
            }
        });
    }

    private void processUnBindSuccess(ContactEntity entity) {
        Device device = entity.accountInfo.getFirstDevice();
        if (device != null) {
            device.macAddress = null;
        }
        contactAdapter.notifyDataSetChanged();
        ToastUtils.showToast(getApplicationContext(), R.string.device_unbind_success);
    }

    @OnClick(R.id.root_group)
    public void onRootGroupClick() {
        parentGroupLayout.removeAllViews();
        parentGroupList.clear();
        contactEntityList.clear();
        contactRecyclerView.getAdapter().notifyDataSetChanged();
        loadInitData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataRefreshEvent(DataRefreshEvent event) {
        fetchUserInfoFromCloud();
    }

    private void fetchUserInfoFromCloud() {
        if (groupUserInfoSelected == null || groupUserInfoSelected.device == null ||
                StringUtils.isNullOrEmpty(groupUserInfoSelected.device.mac)) {
            return;
        }
        final DeviceBind deviceBind = groupUserInfoSelected.device;
        final CloudUserInfoByMacRequest userInfoByMacRequest = new CloudUserInfoByMacRequest(deviceBind.mac);
        AdminApplication.getCloudManager().submitRequest(this, userInfoByMacRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (processFetchUserInfoException(e)) {
                    return;
                }
                GroupUserInfo groupUserInfo = userInfoByMacRequest.getGroupUserInfo();
                if (groupUserInfo != null && groupUserInfoSelected.user._id.equals(groupUserInfo.user._id)) {
                    groupUserInfoSelected.user = groupUserInfo.user;
                    groupUserInfoSelected.device = groupUserInfo.device;
                    groupUserInfoSelected.groups = groupUserInfo.groups;
                    contactAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private boolean processFetchUserInfoException(Throwable e) {
        if (e == null) {
            return false;
        }
        if (ContentException.isCloudException(e) && ((ContentException.CloudException) e).isCloudNotFound()) {
            if (groupUserInfoSelected.user != null) {
                groupUserInfoSelected.user.devices.clear();
                contactAdapter.notifyDataSetChanged();
            }
            return true;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (CollectionUtils.isNullOrEmpty(parentGroupList)) {
            super.onBackPressed();
            return;
        }
        parentGroupLayout.removeViewAt(parentGroupLayout.getChildCount() - 1);
        parentGroupList.remove(parentGroupList.size() - 1);
        loadGroupAndUserList(getLastCloudGroup(), null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        ButterKnife.unbind(this);
    }
}
