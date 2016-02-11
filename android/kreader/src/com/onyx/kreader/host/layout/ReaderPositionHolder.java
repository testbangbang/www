package com.onyx.kreader.host.layout;

import com.onyx.kreader.api.ReaderPagePosition;

/**
 * Created by zhuzeng on 10/18/15.
 */
public class ReaderPositionHolder {

    private ReaderPagePosition lastPosition;
    private ReaderPagePosition currentPosition;
    private ReaderLayoutManager layoutManager;

    public ReaderPositionHolder(final ReaderLayoutManager lm) {
        layoutManager = lm;
    }

    public ReaderPagePosition getLastPosition() {
        return lastPosition;
    }

    public ReaderPagePosition getCurrentPosition() {
        return currentPosition;
    }

    public boolean updatePosition(final ReaderPagePosition newPosition) {
        if (newPosition != null) {
            lastPosition = currentPosition;
            currentPosition = newPosition;
            return true;
        }
        return false;
    }

    public boolean firstPage() {
        ReaderPagePosition newPosition = layoutManager.getNavigator().firstPage();
        return updatePosition(newPosition);
    }

    public boolean lastPage() {
        ReaderPagePosition newPosition = layoutManager.getNavigator().lastPage();
        return updatePosition(newPosition);
    }

    public boolean nextPage() {
        ReaderPagePosition newPosition = layoutManager.getNavigator().nextPage(currentPosition);
        return updatePosition(newPosition);
    }

    public boolean prevPage() {
        ReaderPagePosition newPosition = layoutManager.getNavigator().prevPage(currentPosition);
        return updatePosition(newPosition);
    }

    public boolean nextScreen() {
        ReaderPagePosition newPosition = layoutManager.getNavigator().nextScreen(currentPosition);
        return updatePosition(newPosition);
    }

    public boolean prevScreen() {
        ReaderPagePosition newPosition = layoutManager.getNavigator().prevScreen(currentPosition);
        return updatePosition(newPosition);
    }

    public boolean gotoPosition(final ReaderPagePosition position) {
        if (layoutManager.getNavigator().gotoPosition(position)) {
            updatePosition(position);
            return true;
        }
        return false;
    }

}
