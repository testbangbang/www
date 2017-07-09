package com.onyx.edu.manager.view.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.jiang.android.lib.adapter.expand.StickyRecyclerHeadersDecoration;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.model.v2.DeviceBind;
import com.onyx.android.sdk.data.model.v2.GroupUserInfo;
import com.onyx.android.sdk.data.model.v2.UserInfoBind;
import com.onyx.android.sdk.data.request.cloud.v2.CloudGroupDeviceBindRequest;
import com.onyx.android.sdk.data.utils.JSONObjectParseUtils;
import com.onyx.android.sdk.ui.utils.ToastUtils;
import com.onyx.android.sdk.utils.NetworkUtil;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.edu.manager.AppApplication;
import com.onyx.edu.manager.R;
import com.onyx.edu.manager.adapter.ContactAdapter;
import com.onyx.edu.manager.adapter.ItemClickListener;
import com.onyx.edu.manager.event.DeviceBindCommitEvent;
import com.onyx.edu.manager.manager.ContentManager;
import com.onyx.edu.manager.model.UserInfoBindEntity;
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

/**
 * Created by suicheng on 2017/7/7.
 */
public class DeviceUserInfoFragment extends Fragment {

    private static final int REQUEST_BIND_DEVICE = 1001;

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

    private GroupUserInfo groupUserInfo;

    private ContactAdapter contactAdapter;
    private List<UserInfoBindEntity> deviceBindEntityList = new ArrayList<>();
    private List<UserInfoBindEntity> searchDeviceEntityList = new ArrayList<>();
    private CharacterParser characterParser = CharacterParser.getInstance();
    private Comparator pinyinComparator = new Comparator<UserInfoBindEntity>() {
        @Override
        public int compare(UserInfoBindEntity o1, UserInfoBindEntity o2) {
            return o1.sortLetter.compareTo(o2.sortLetter);
        }
    };

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
        initView();
        initData();
        return view;
    }

    private void initView() {
        initContentPageView();
        initSideBarView();
        initContactSearchView();
        updateMacAddress(getGroupUserInfo().device);
    }

    private void initContactSearchView() {
        contactSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
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
        List<UserInfoBindEntity> list = new ArrayList<>();
        for (UserInfoBindEntity entity : deviceBindEntityList) {
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
        macAddressTv.setText(String.format(getString(R.string.mac_address_format), deviceBind.mac));
    }

    private boolean isUserInfoBound(UserInfoBindEntity entity) {
        if (entity.userInfoBind == null || StringUtils.isNullOrEmpty(entity.userInfoBind.userId)) {
            return false;
        }
        return true;
    }

    private void initContentPageView() {
        contactRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        contactRecyclerView.setAdapter(contactAdapter = new ContactAdapter<UserInfoBindEntity>(getContext(),
                deviceBindEntityList) {
            @Override
            public String getMacAddress(UserInfoBindEntity entity) {
                return isUserInfoBound(entity) ? "已绑定" : "未绑定";
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
                    ToastUtils.showToast(getContext().getApplicationContext(), "Mac地址不合法，无法提供绑定");
                    return;
                }
                startCommitDeviceInfoBind(deviceBindEntityList.get(position).userInfoBind);
            }

            @Override
            public void onLongClick(int position, View view) {
            }
        });
    }

    private GroupUserInfo getGroupUserInfo() {
        if (groupUserInfo != null) {
            return groupUserInfo;
        }
        groupUserInfo = JSONObjectParseUtils.parseObject(getArguments().getString(ContentManager.KEY_GROUP_USER_INFO),
                GroupUserInfo.class);
        if (groupUserInfo == null) {
            groupUserInfo = new GroupUserInfo();
        }
        return groupUserInfo;
    }

    private void initData() {
        final CloudGroupDeviceBindRequest bindRequest = new CloudGroupDeviceBindRequest(getGroupUserInfo().groups.get(0)._id);
        AppApplication.getCloudManager().submitRequest(getContext(), bindRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                List<UserInfoBind> deviceBindList;
                if (e != null || (deviceBindList = bindRequest.getUserInfoBindList()) == null) {
                    ToastUtils.showToast(getContext().getApplicationContext(), "群组里无用户");
                    return;
                }
                processDeviceBindList(deviceBindList);
            }
        });
    }

    private void processDeviceBindList(List<UserInfoBind> deviceBindList) {
        List<UserInfoBindEntity> list = new ArrayList<>();
        for (UserInfoBind userInfo : deviceBindList) {
            UserInfoBindEntity entity = new UserInfoBindEntity();
            entity.username = userInfo.name;
            entity.userInfoBind = userInfo;
            list.add(entity);
        }
        deviceBindEntityList.clear();
        deviceBindEntityList.addAll(list);
        splitContactList(deviceBindEntityList);
        contactRecyclerView.getAdapter().notifyDataSetChanged();
    }

    private void splitContactList(List<UserInfoBindEntity> contactEntityList) {
        for (UserInfoBindEntity entity : contactEntityList) {
            String pinyin = characterParser.getSelling(entity.username);
            String sortString = pinyin.substring(0, 1).toUpperCase();
            if (sortString.matches("[A-Z]")) {
                entity.sortLetter = sortString;
            } else {
                entity.sortLetter = sortString;
            }
        }
        Collections.sort(contactEntityList, pinyinComparator);
    }

    private void startCommitDeviceInfoBind(UserInfoBind userInfoBind) {
        GroupUserInfo groupUserInfo = getGroupUserInfo();
        groupUserInfo.userBind = userInfoBind;
        EventBus.getDefault().post(new DeviceBindCommitEvent(groupUserInfo));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
