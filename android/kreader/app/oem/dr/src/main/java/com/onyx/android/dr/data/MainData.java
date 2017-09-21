package com.onyx.android.dr.data;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.action.ActionChain;
import com.onyx.android.dr.action.AuthTokenAction;
import com.onyx.android.dr.action.CloudLibraryListLoadAction;
import com.onyx.android.dr.event.MainLibraryTabEvent;
import com.onyx.android.dr.request.cloud.RequestGetMyGroup;
import com.onyx.android.dr.request.cloud.RequestIndexServiceAndLogin;
import com.onyx.android.dr.request.local.RequestLoadLocalDB;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.request.cloud.v2.CloudChildLibraryListLoadRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hehai on 17-6-28.
 */

public class MainData {
    public List<MenuBean> loadTabMenu(String userType) {
        return MainTabMenuConfig.getMenuData(userType);
    }

    public void login(RequestIndexServiceAndLogin req, BaseCallback baseCallback) {
        DRApplication.getCloudStore().submitRequest(DRApplication.getInstance(), req, baseCallback);
    }

    public void loadLocalDB(RequestLoadLocalDB req, BaseCallback baseCallback) {
        DRApplication.getDataManager().submit(DRApplication.getInstance(), req, baseCallback);
    }

    public void getMyGroup(RequestGetMyGroup req, BaseCallback baseCallback) {
        DRApplication.getCloudStore().submitRequest(DRApplication.getInstance(), req, baseCallback);
    }

    public void getLibraryList(CloudChildLibraryListLoadRequest req, BaseCallback baseCallback) {
        DRApplication.getCloudStore().submitRequest(DRApplication.getInstance(), req, baseCallback);
    }

    public List<MenuBean> loadTabMenu(List<Library> libraryList) {
        List<MenuBean> list = new ArrayList<>();
        for (Library library : libraryList) {
            MainLibraryTabEvent event = new MainLibraryTabEvent(library);
            MenuBean menuBean = new MenuBean(library.getName(), library.getName(), "", event);
            list.add(menuBean);
        }
        List<MenuBean> menuData = MainTabMenuConfig.getMenuData("");
        list.addAll(menuData);
        return list;
    }
}
