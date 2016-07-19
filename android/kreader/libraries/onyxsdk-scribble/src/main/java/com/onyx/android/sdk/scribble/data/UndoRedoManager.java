package com.onyx.android.sdk.scribble.data;


import com.onyx.android.sdk.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zengzhu on 2/21/16.
 *
 * a1 a2 a3 a4 a5  |  a6 a7 a8 a9 aa
 *     back     current    forward
 */
public class UndoRedoManager {

    public static class Action<T> {
        private String actionName;
        private T object;

        public Action(final String name, final T ref) {
            actionName = name;
            object = ref;
        }

        public boolean equals(final Object other) {
            if (this == other) {
                return true;
            }
            if (other == null) {
                return false;
            }
            if (!(other instanceof Action)) {
                return false;
            }
            Action that = (Action) other;
            return actionName.equals(that.actionName) && object == that.object;
        }

        public String getActionName() {
            return actionName;
        }

        public T getObject() {
            return object;
        }
    }

    private Action current;
    private List<Action> back = new ArrayList<Action>();
    private List<Action> forward = new ArrayList<Action>();

    /**
     * move back, undo, move from back to forward list.
     * @return
     */
    public Action undo() {
        if (back.isEmpty()) {
            return null;
        }
        current = back.remove(back.size() - 1);
        add(forward, current);
        return current;
    }

    /**
     * move forward, redo. move from forward to back
     * @return
     */
    public Action redo() {
        if (forward.isEmpty()) {
            return null;
        }
        current = forward.remove(forward.size() - 1);
        add(back, current);
        return current;
    }

    /**
     * add to back list, so caller can undo
     * @param action
     * @param allowDuplicate allow duplicated item or not.
     * @return
     */
    public boolean addToHistory(final Action action, boolean allowDuplicate) {
        if (action == null || StringUtils.isNullOrEmpty(action.actionName)) {
            return false;
        }
        if (!allowDuplicate &&
            current != null &&
            current.equals(action)) {
            return false;
        }
        current = action;
        add(back, current);
        forward.clear();
        return true;
    }

    public void clear() {
        forward.clear();
        back.clear();
        current = null;
    }

    public Action getCurrent() {
        return current;
    }

    public boolean canUndo() {
        return !back.isEmpty();
    }

    public boolean canRedo() {
        return !forward.isEmpty();
    }

    private boolean add(final List<Action> list, final Action action) {
        if (action != null && StringUtils.isNotBlank(action.actionName)) {
            list.add(action);
            return true;
        }
        return false;
    }

}
