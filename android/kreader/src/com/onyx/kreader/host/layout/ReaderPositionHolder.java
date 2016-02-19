package com.onyx.kreader.host.layout;

import com.onyx.kreader.host.math.PositionSnapshot;
import com.onyx.kreader.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuzeng on 10/18/15.
 */
public class ReaderPositionHolder {

    private PositionSnapshot lastPosition;
    private String currentPosition;
    private List<String> positionStack = new ArrayList<String>();
    private ReaderLayoutManager layoutManager;

    public ReaderPositionHolder(final ReaderLayoutManager lm) {
        layoutManager = lm;
    }

    public PositionSnapshot getLastPosition() {
        return lastPosition;
    }

    public String getCurrentPosition() {
        return currentPosition;
    }

    public boolean updatePosition(final String newPosition) {
        if (newPosition != null) {
            //lastPosition = currentPosition;
            //currentPosition = newPosition;
            return true;
        }
        return false;
    }

    public boolean firstPage() {
        String newPosition = layoutManager.getNavigator().firstPage();
        return updatePosition(newPosition);
    }

    public boolean lastPage() {
        String newPosition = layoutManager.getNavigator().lastPage();
        return updatePosition(newPosition);
    }

    public boolean nextPage() {
        String newPosition = layoutManager.getNavigator().nextPage(currentPosition);
        return updatePosition(newPosition);
    }

    public boolean prevPage() {
        String newPosition = layoutManager.getNavigator().prevPage(currentPosition);
        return updatePosition(newPosition);
    }

    public boolean nextScreen() {
        String newPosition = layoutManager.getNavigator().nextScreen(currentPosition);
        return updatePosition(newPosition);
    }

    public boolean prevScreen() {
        String newPosition = layoutManager.getNavigator().prevScreen(currentPosition);
        return updatePosition(newPosition);
    }

    public boolean gotoPosition(final String position) {
        return false;
    }

}
