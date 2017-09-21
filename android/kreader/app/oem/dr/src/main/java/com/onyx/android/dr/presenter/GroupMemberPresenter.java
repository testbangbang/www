package com.onyx.android.dr.presenter;

import com.onyx.android.dr.adapter.GroupMemberAdapter;
import com.onyx.android.dr.data.GroupMemberData;
import com.onyx.android.dr.interfaces.GroupMemberView;
import com.onyx.android.dr.request.cloud.DeleteGroupMemberRequest;
import com.onyx.android.dr.request.cloud.RequestGroupMember;
import com.onyx.android.dr.request.cloud.SearchGroupMemberRequest;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.model.DeleteGroupMemberBean;
import com.onyx.android.sdk.data.model.v2.ListBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by zhouzhiming on 2017/8/29.
 */
public class GroupMemberPresenter {
    private GroupMemberView groupMemberView;
    private GroupMemberData groupMemberData;

    public GroupMemberPresenter(GroupMemberView groupMemberView) {
        this.groupMemberView = groupMemberView;
        groupMemberData = new GroupMemberData();
    }

    public void getGroupMember(String id, String offset, String limit, String sortBy, String order) {
        final RequestGroupMember req = new RequestGroupMember(id, offset, limit, sortBy, order);
        groupMemberData.requestGroupMember(req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                groupMemberView.setGroupMemberResult(req.getGroup());
            }
        });
    }

    public void searchGroupMember(String id, String text) {
        final SearchGroupMemberRequest req = new SearchGroupMemberRequest(id, text);
        groupMemberData.requestSearchGroupMember(req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                groupMemberView.setGroupMemberResult(req.getGroup());
            }
        });
    }

    public ArrayList<Boolean> getListCheck(List<ListBean> groupList) {
        ArrayList<Boolean> listCheck = new ArrayList<>();
        for (int i = 0; i < groupList.size(); i++) {
            listCheck.add(false);
        }
        return listCheck;
    }

    public void deleteGroupMember(String id, DeleteGroupMemberBean bean) {
        final DeleteGroupMemberRequest req = new DeleteGroupMemberRequest(id, bean);
        groupMemberData.requestDeleteGroupMember(req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                groupMemberView.setDeleteGroupMemberResult(req.getResult());
            }
        });
    }

    public void remoteAdapterData(String id, ArrayList<Boolean> listCheck, GroupMemberAdapter adapter, List<ListBean> list) {
        int length = listCheck.size();
        DeleteGroupMemberBean deleteGroupMemberBean = new DeleteGroupMemberBean();
        String[] array = new String[]{};
        for (int i = length - 1; i >= 0; i--) {
            if (listCheck.get(i)) {
                //delete basedata data
                ListBean bean = list.get(i);
                array = Arrays.copyOf(array, array.length + 1);
                array[array.length - 1] = bean._id;
                list.remove(i);
                listCheck.remove(i);
                adapter.notifyItemRemoved(i);
            }
        }
        deleteGroupMemberBean.setUsers(array);
        deleteGroupMember(id, deleteGroupMemberBean);
        adapter.notifyItemRangeChanged(0, list.size());
    }

    public List<ListBean> getData(ArrayList<Boolean> listCheck, List<ListBean> list) {
        List<ListBean> exportList = new ArrayList<>();
        for (int i = 0, j = list.size(); i < j; i++) {
            Boolean aBoolean = listCheck.get(i);
            if (aBoolean) {
                ListBean bean = list.get(i);
                if (!exportList.contains(bean)) {
                    exportList.add(bean);
                }
            }
        }
        return exportList;
    }
}
