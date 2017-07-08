package com.onyx.edu.manager.view.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.jiang.android.lib.adapter.expand.StickyRecyclerHeadersDecoration;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.model.v2.CloudGroup;
import com.onyx.android.sdk.data.model.v2.NeoAccountBase;
import com.onyx.android.sdk.data.request.cloud.CloudRequestChain;
import com.onyx.android.sdk.data.request.cloud.v2.CloudGroupListRequest;
import com.onyx.android.sdk.data.request.cloud.v2.CloudGroupUserListRequest;
import com.onyx.android.sdk.qrcode.utils.ScreenUtils;
import com.onyx.android.sdk.ui.utils.ToastUtils;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.edu.manager.AppApplication;
import com.onyx.edu.manager.R;
import com.onyx.edu.manager.adapter.ContactAdapter;
import com.onyx.edu.manager.adapter.GroupSelectAdapter;
import com.onyx.edu.manager.adapter.ItemClickListener;
import com.onyx.edu.manager.model.ContactEntity;
import com.onyx.edu.manager.pinyin.CharacterParser;
import com.onyx.edu.manager.pinyin.PinyinComparator;
import com.onyx.edu.manager.view.ui.DividerDecoration;
import com.onyx.edu.manager.view.ui.SideBar;

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

    @Bind(R.id.contact_recycler)
    RecyclerView contactRecyclerView;
    @Bind(R.id.contact_sidebar)
    SideBar contactSidebar;
    @Bind(R.id.contact_dialog)
    TextView contactTextDialog;

    private ContactAdapter contactAdapter;
    private List<ContactEntity> contactEntityList = new ArrayList<>();
    private CharacterParser characterParser = CharacterParser.getInstance();
    private PinyinComparator pinyinComparator = new PinyinComparator();

    private GroupSelectAdapter groupAdapter;
    private CloudGroup childGroup;
    private List<CloudGroup> parentGroupList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_manager);
        ButterKnife.bind(this);

        initView();
        initData();
    }

    private void initView() {
        initToolbar();
        initChildGroupPageView();
        initContentPageView();
        initSideBarView();
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
        titleView.setText("选择组织");
    }

    private void initData() {
        loadGroupAndUserList(null);
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
                if (e != null && group != null && StringUtils.isNotBlank(group._id)) {
                    ToastUtils.showToast(request.getContext().getApplicationContext(), "获取组员失败");
                    return;
                }
                if (group != null) {
                    addLibraryToParentRefList(group);
                }
            }
        });
    }

    private void loadGroupAndUserList(CloudGroup group, final BaseCallback baseCallback) {
        final String groupId = group != null ? group._id : null;
        CloudRequestChain requestChain = new CloudRequestChain();
        final CloudGroupListRequest groupListRequest = new CloudGroupListRequest(String.valueOf(groupId));
        final CloudGroupUserListRequest userListRequest = new CloudGroupUserListRequest(groupId);
        requestChain.addRequest(groupListRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null && StringUtils.isNotBlank(groupId)) {
                    BaseCallback.invoke(baseCallback, request, e);
                    return;
                }
                groupAdapter.setGroupContainer(childGroup = groupListRequest.getChildGroup());
            }
        });
        requestChain.addRequest(userListRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e == null) {
                    processUserList(userListRequest.getGroupUserList());
                }
                BaseCallback.invoke(baseCallback, request, e);
            }
        });
        requestChain.execute(this, AppApplication.getCloudManager());
    }

    private void processUserList(List<NeoAccountBase> userInfoList) {
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
        contactRecyclerView.getAdapter().notifyDataSetChanged();
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
        contactAdapter.setItemUnbundledClickListener(new ItemClickListener() {
            @Override
            public void onClick(int position, View view) {
                processDeviceUnbundled(contactEntityList.get(position));
            }

            @Override
            public void onLongClick(int position, View view) {
            }
        });
    }

    private void processDeviceUnbundled(ContactEntity entity) {
        String content = entity.accountInfo.getName() + "\n" +
                StringUtils.getBlankStr(entity.accountInfo.getFirstDevice().macAddress);
        new MaterialDialog.Builder(this)
                .theme(Theme.LIGHT)
                .title("用户设备解绑")
                .content(content)
                .contentGravity(GravityEnum.CENTER)
                .positiveText(R.string.ok)
                .positiveColorRes(R.color.colorPrimary)
                .negativeText(R.string.cancel)
                .negativeColorRes(R.color.darker_gray)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        ToastUtils.showToast(getApplicationContext(), "暂未支持");
                    }
                }).show();
    }

    @OnClick(R.id.root_group)
    public void onRootGroupClick() {
        parentGroupLayout.removeAllViews();
        parentGroupList.clear();
        loadGroupAndUserList(null, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                contactEntityList.clear();
                contactRecyclerView.getAdapter().notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
