package com.onyx.android.dr.data;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.action.ActionChain;
import com.onyx.android.dr.action.AuthTokenAction;
import com.onyx.android.dr.action.CloudLibraryListLoadAction;
import com.onyx.android.dr.event.MainLibraryTabEvent;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.data.model.Library;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hehai on 17-6-28.
 */

public class MainData {
    public void lookCloudLibraryList(CloudLibraryListLoadAction loadAction, BaseCallback baseCallback) {
        ActionChain actionChain = new ActionChain();
        actionChain.addAction(loadAction);
        actionChain.execute(DRApplication.getLibraryDataHolder(), baseCallback);
    }

    public List<MenuBean> loadTabMenu(String userType) {
        return MainTabMenuConfig.getMenuData(userType);
    }

    public void authToken(AuthTokenAction authTokenAction, BaseCallback baseCallback) {
        ActionChain actionChain = new ActionChain();
        actionChain.addAction(authTokenAction);
        actionChain.execute(DRApplication.getLibraryDataHolder(), baseCallback);
    }

    public List<MenuBean> loadTabMenu(List<Library> libraryList) {
        List<MenuBean> list = new ArrayList<>();
        for (Library library : libraryList) {
            MainLibraryTabEvent event = new MainLibraryTabEvent(library, MainTabMenuConfig.languages);
            MenuBean menuBean = new MenuBean(library.getName(), library.getName(), "", event);
            list.add(menuBean);
        }
        List<MenuBean> menuData = MainTabMenuConfig.getMenuData("");
        list.addAll(menuData);
        return list;
    }
}
