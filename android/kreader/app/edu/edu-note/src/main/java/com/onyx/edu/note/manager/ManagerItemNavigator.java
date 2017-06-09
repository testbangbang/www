package com.onyx.edu.note.manager;

/**
 * Created by solskjaer49 on 2017/6/8 16:50.
 * Defines the navigation actions that can be called from the note grid.
 */

public interface ManagerItemNavigator {
    void editNote(String uniqueID);

    void enterFolder(String uniqueID);

    void addNewNote();
}
