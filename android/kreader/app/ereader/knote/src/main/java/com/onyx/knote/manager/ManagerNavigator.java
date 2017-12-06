package com.onyx.knote.manager;

/**
 * Created by solskjaer49 on 2017/6/6 17:18.
 */

public interface ManagerNavigator {
    void showNewFolderTitleIllegal();

    void updateFolderCreateStatus(boolean succeed);

    void updateNoteRemoveStatus(boolean succeed);

    void onGoUp();

}
