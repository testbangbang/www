package com.onyx.edu.note.scribble;

/**
 * Created by solskjaer49 on 2017/6/22 12:01.
 */

public interface ScribbleNavigator {
    void prevPage();

    void nextPage();

    void goToTargetPage();

    void addPage();

    void deletePage();

    void undo();

    void redo();

    void save();

    void goToSetting();

    void switchScribbleMode();
}
