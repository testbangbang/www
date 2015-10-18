package com.onyx.reader.host.layout;

import com.onyx.reader.api.ReaderDocumentPosition;

/**
 * Created by zhuzeng on 10/18/15.
 */
public class ReaderPositionHolder {

    private ReaderDocumentPosition position;
    private ReaderLayoutManager layoutManager;

    public ReaderPositionHolder(final ReaderLayoutManager lm) {
        layoutManager = lm;
    }

    public ReaderDocumentPosition getPosition() {
        return position;
    }

    private boolean updatePosition(final ReaderDocumentPosition newPosition) {
        if (newPosition != null) {
            position = newPosition;
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
        ReaderDocumentPosition newPosition = layoutManager.getNavigator().nextPage(position);
        return updatePosition(newPosition);
    }


    public boolean prevPage() {
        ReaderDocumentPosition newPosition = layoutManager.getNavigator().prevPage(position);
        return updatePosition(newPosition);
    }

    public boolean nextScreen() {
        ReaderDocumentPosition newPosition = layoutManager.getNavigator().nextScreen(position);
        return updatePosition(newPosition);
    }


    public boolean prevScreen() {
        ReaderDocumentPosition newPosition = layoutManager.getNavigator().prevScreen(position);
        return updatePosition(newPosition);
    }

}
