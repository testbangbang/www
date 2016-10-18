package com.onyx.android.sdk;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.model.Member;
import com.onyx.android.sdk.data.model.OnyxAccount;
import com.onyx.android.sdk.data.model.OnyxGroup;
import com.onyx.android.sdk.data.v1.OnyxGroupService;
import com.onyx.android.sdk.data.v1.ServiceFactory;
import com.onyx.android.sdk.utils.TestUtils;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

/**
 * Created by suicheng on 2016/9/23.
 */
public class GroupMemberTest extends ApplicationTestCase<Application> {

    static OnyxGroupService service;
    static OnyxAccount currentAccount;

    public GroupMemberTest() {
        super(Application.class);
    }

    private final OnyxGroupService getService() {
        if (service == null) {
            CloudManager cloudManager = new CloudManager();
            service = ServiceFactory.getGroupService(cloudManager.getCloudConf().getApiBase());
        }
        return service;
    }

    public static OnyxAccount getCurrentAccount() throws Exception {
        if (currentAccount == null || currentAccount.sessionToken == null) {
            currentAccount = AccountTest.testSignUpRequest();
        }
        return currentAccount;
    }

    public static OnyxGroup getRandomGroup() {
        OnyxGroup group = new OnyxGroup(TestUtils.randString());
        group.description = TestUtils.randString();
        group.children = new ArrayList<>();
        group.members = new ArrayList<>();
        return group;
    }

    public static List<OnyxGroup> getRandomGroup(int count) {
        List<OnyxGroup> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            list.add(getRandomGroup());
        }
        return list;
    }

    public static Member getRandomMember() {
        Member member = new Member(TestUtils.randString());
        member.account = AccountUtils.generateRandomAccount();
        return member;
    }

    public static List<Member> getRandomMember(int count) {
        List<Member> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            list.add(getRandomMember());
        }
        return list;
    }

    public void testGroupSave() {
        DataManager.init(getContext(), null);
        Delete.table(OnyxGroup.class);

        int childrenCount = TestUtils.randInt(2, 6);
        int membersCount = TestUtils.randInt(2, 4);
        OnyxGroup group = getRandomGroup();
        group.children.addAll(getRandomGroup(childrenCount));
        group.members.addAll(getRandomMember(membersCount));
        group.save();

        OnyxGroup testGroup = new Select().from(OnyxGroup.class).where().querySingle();
        assertNotNull(testGroup);
        assertTrue(testGroup.children.size() == childrenCount);
        assertTrue(testGroup.members.size() == membersCount);
    }

    public void testGroupCreate() throws Exception {
        OnyxGroup group = getRandomGroup();
        Response<OnyxGroup> response = getService().createGroup(group, getCurrentAccount().sessionToken).execute();
        assertNotNull(response);
        assertNotNull(response.body());
        OnyxGroup createdGroup = response.body();
        assertNotNull(createdGroup.creatorId);
        assertEquals(group.name, createdGroup.name);
        assertEquals(group.description, createdGroup.description);
    }

}