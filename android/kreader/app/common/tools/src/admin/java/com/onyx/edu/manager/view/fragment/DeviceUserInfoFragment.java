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
import com.onyx.android.sdk.data.model.v2.NeoAccountBase;
import com.onyx.android.sdk.data.request.cloud.v2.CloudGroupUserListRequest;
import com.onyx.android.sdk.data.utils.JSONObjectParseUtils;
import com.onyx.android.sdk.ui.utils.ToastUtils;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.InputMethodUtils;
import com.onyx.android.sdk.utils.NetworkUtil;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.edu.manager.AdminApplication;
import com.onyx.edu.manager.R;
import com.onyx.edu.manager.adapter.ContactAdapter;
import com.onyx.edu.manager.adapter.ItemClickListener;
import com.onyx.edu.manager.event.DeviceBindCommitEvent;
import com.onyx.edu.manager.event.GroupReSelectEvent;
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
        initContentPageView();
        initSideBarView();
        initContactSearchView();
        updateMacAddress(getGroupUserInfo().device);
    }

    private void initToolbar(View parentView) {
        View view = parentView.findViewById(R.id.toolbar_header);
        TextView titleView = (TextView) view.findViewById(R.id.toolbar_title);
        titleView.setText(R.string.user_device_bind);
        TextView otherTextView = (TextView) view.findViewById(R.id.toolbar_other);
        otherTextView.setText(R.string.node_re_select);
        otherTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                groupReselectClick();
            }
        });
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
        final CloudGroupUserListRequest userListRequest = new CloudGroupUserListRequest(getGroupUserInfo().groups.get(0)._id);
        AdminApplication.getCloudManager().submitRequest(getContext(), userListRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                List<NeoAccountBase> groupUserList;
                if (e != null || (CollectionUtils.isNullOrEmpty(groupUserList = userListRequest.getGroupUserList()))) {
                    ToastUtils.showToast(getContext().getApplicationContext(), R.string.group_has_no_users);
                    return;
                }
                processDeviceBindList(groupUserList);
            }
        });
    }

    private void processDeviceBindList(List<NeoAccountBase> accountList) {
        List<ContactEntity> list = new ArrayList<>();
        for (NeoAccountBase account : accountList) {
            ContactEntity entity = new ContactEntity();
            NeoAccountBase.parseInfo(account);
            entity.username = account.getName();
            entity.accountInfo = account;
            list.add(entity);
        }
        deviceBindEntityList.clear();
        deviceBindEntityList.addAll(list);
        splitContactList(deviceBindEntityList);
        contactRecyclerView.getAdapter().notifyDataSetChanged();
    }

    private void splitContactList(List<ContactEntity> contactEntityList) {
        for (ContactEntity entity : contactEntityList) {
            String pinyin = characterParser.getSelling(getContactEntityUsername(entity));
            String sortString = pinyin.substring(0, 1).toUpperCase();
            if (sortString.matches("[A-Z]")) {
                entity.sortLetter = sortString;
            } else {
                entity.sortLetter = sortString;
            }
        }
        Collections.sort(contactEntityList, pinyinComparator);
    }

    private String getContactEntityUsername(ContactEntity entity) {
        if (StringUtils.isNullOrEmpty(entity.username)) {
            entity.username = "------";
        }
        return entity.username.replaceAll("\\b", "")
                .replaceAll("\b", "");
    }

    private void startCommitDeviceBind(NeoAccountBase accountBase) {
        GroupUserInfo groupUserInfo = getGroupUserInfo();
        groupUserInfo.user = accountBase;
        EventBus.getDefault().post(new DeviceBindCommitEvent(groupUserInfo));
    }

    private void groupReselectClick() {
        EventBus.getDefault().post(new GroupReSelectEvent());
    }

    @OnClick(R.id.toolbar_back)
    public void onToolbarBackClick() {
        getActivity().finish();
    }

    @OnClick(R.id.tv_add_new_user)
    public void onAddNewUserClick() {
        startCommitDeviceBind(null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
