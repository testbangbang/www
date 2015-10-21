package com.onyx.reader.host.layout;

import com.onyx.reader.api.ReaderDocumentPosition;

/**
 * Created by zhuzeng on 10/18/15.
 */
public class ReaderPositionHolder {

    private ReaderDocumentPosition lastPosition;
    private ReaderDocumentPosition currentPosition;
    private ReaderLayoutManager layoutManager;

    public ReaderPositionHolder(final ReaderLayoutManager lm) {
        layoutManager = lm;
    }

    public ReaderDocumentPosition getLastPosition() {
        return lastPosition;
    }

    public ReaderDocumentPosition getCurrentPosition() {
        return currentPosition;
    }

    public boolean updatePosition(final ReaderDocumentPosition newPosition) {
        if (newPosition != null) {
            lastPosition = currentPosition;
            currentPosition = newPosition;
            return true;
        }
        return false;
    }

    public boolean firstPage() {
        ReaderDocumentPosition newPosition = layoutManager.getNavigator().firstPage();
        return updatePosition(newPosition);
    }

    public boolean lastPage() {
        ReaderDocumentPosition newPosition = layoutManager.getNavigator().lastPage();
        return updatePosition(newPosition);
    }

    public boolean nextPage() {
        ReaderDocumentPosition newPosition = layoutManager.getNavigator().nextPage(currentPosition);
        return updatePosition(newPosition);
    }

    public boolean prevPage() {
        ReaderDocumentPosition newPosition = layoutManager.getNavigator().prevPage(currentPosition);
        return updatePosition(newPosition);
    }

    public boolean nextScreen() {
        ReaderDocumentPosition newPosition = layoutManager.getNavigator().nextScreen(currentPosition);
        return updatePosition(newPosition);
    }

    public boolean prevScreen() {
        ReaderDocumentPosition newPosition = layoutManager.getNavigator().prevScreen(currentPosition);
        return updatePosition(newPosition);
    }

    public boolean gotoPosition(final ReaderDocumentPosition position) {
        if (layoutManager.getNavigator().gotoPosition(position)) {
            updatePosition(position);
            return true;
        }
        return false;
    }

}
