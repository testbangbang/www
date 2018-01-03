package com.onyx.android.sdk.data.request.cloud.v2;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.DataManagerHelper;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.db.ContentDatabase;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.common.FetchPolicy;
import com.onyx.android.sdk.data.model.v2.CloudGroup;
import com.onyx.android.sdk.data.model.v2.CloudLibrary;
import com.onyx.android.sdk.data.model.v2.GroupContainer;
import com.onyx.android.sdk.data.model.v2.Subject;
import com.onyx.android.sdk.data.model.v2.Subject_Table;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;
import com.onyx.android.sdk.data.utils.RetrofitUtils;
import com.onyx.android.sdk.data.utils.StoreUtils;
import com.onyx.android.sdk.data.v1.ServiceFactory;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

/**
 * Created by suicheng on 2017/7/29.
 */
public class CloudGroupContainerListRequest extends BaseCloudRequest {

    List<GroupContainer> containerList;

    public List<GroupContainer> getContainerList() {
        return containerList;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        containerList = fetchGroupContainerData(parent);
    }

    private List<GroupContainer> fetchGroupContainerData(CloudManager parent) {
        List<GroupContainer> list;
        try {
            list = fetchFromCloud(parent);
        } catch (Exception e) {
            list = fetchFromLocal(parent);
        }
        return list;
    }

    private List<GroupContainer> fetchFromCloud(CloudManager parent) throws Exception {
        List<GroupContainer> list = new ArrayList<>();
        Response<List<GroupContainer>> response = RetrofitUtils.executeCall(ServiceFactory.getContentService(
                parent.getCloudConf().getApiBase()).getMyGroupContainerList());
        if (response.isSuccessful()) {
            clearTable(parent);
            list = response.body();
            if (!CollectionUtils.isNullOrEmpty(list)) {
                saveLibraryListToLocal(parent, list);
                saveGroupListToLocal(parent, list);
                saveSubjectListToLocal(parent, list);
            }
        }
        return list;
    }

    private List<GroupContainer> fetchFromLocal(CloudManager parent) {
        List<CloudGroup> groupList = StoreUtils.queryDataList(CloudGroup.class);
        if (CollectionUtils.isNullOrEmpty(groupList)) {
            return new ArrayList<>();
        }
        List<GroupContainer> containerList = new ArrayList<>();
        for (CloudGroup cloudGroup : groupList) {
            GroupContainer container = new GroupContainer();
            container.group = cloudGroup;
            container.libraryList = fetchLibraryListFromLocal(parent, cloudGroup.library);
            container.subjectList = fetchSubjectListFromLocal(parent, cloudGroup._id);
            containerList.add(container);
        }
        return containerList;
    }

    private List<Library> fetchLibraryListFromLocal(CloudManager parent, String libraryId) {
        QueryArgs queryArgs = new QueryArgs();
        queryArgs.libraryUniqueId = libraryId;
        queryArgs.fetchPolicy = FetchPolicy.MEM_DB_ONLY;
        return DataManagerHelper.fetchLibraryLibraryList(getContext(), parent.getCloudDataProvider(), queryArgs);
    }

    private List<Subject> fetchSubjectListFromLocal(CloudManager parent, String categoryId) {
        return new Select().from(Subject.class)
                .where(Subject_Table.category.eq(categoryId))
                .queryList();
    }

    private void saveGroupListToLocal(CloudManager parent, List<GroupContainer> containerList) {
        if (CollectionUtils.isNullOrEmpty(containerList)) {
            return;
        }
        List<CloudGroup> groupList = new ArrayList<>();
        for (GroupContainer groupContainer : containerList) {
            groupList.add(groupContainer.group);
        }
        //use local database
        try {
            StoreUtils.saveToLocal(ContentDatabase.class, groupList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveLibraryListToLocal(CloudManager parent, List<GroupContainer> containerList) {
        if (CollectionUtils.isNullOrEmpty(containerList)) {
            return;
        }
        for (GroupContainer groupContainer : containerList) {
            if (CollectionUtils.isNullOrEmpty(groupContainer.libraryList)) {
                continue;
            }
            DataManagerHelper.saveLibraryListToLocal(parent.getCloudDataProvider(), getCloudLibrary(groupContainer.libraryList));
        }
    }

    private void saveSubjectListToLocal(CloudManager parent, List<GroupContainer> containerList) {
        for (GroupContainer groupContainer : containerList) {
            if (CollectionUtils.isNullOrEmpty(groupContainer.subjectList)) {
                continue;
            }
            for (Subject subject : groupContainer.subjectList) {
                subject.category = groupContainer.group._id;
            }
            StoreUtils.saveToLocal(ContentDatabase.class, groupContainer.subjectList);
        }
    }

    private void clearTable(CloudManager parent) {
        Delete.table(CloudGroup.class);
        Delete.table(Subject.class);
        DataManagerHelper.clearLibrary(parent.getCloudDataProvider());
    }

    private List<Library> getCloudLibrary(List<Library> loadedLibraryList) {
        return CloudLibrary.getCloudLibraryList(loadedLibraryList);
    }
}
