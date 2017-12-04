package com.onyx.kcb.event;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by suicheng on 2017/9/11.
 */

public class OperationEvent {
    public final static int OPERATION_COPY = 0;
    public final static int OPERATION_CUT = 1;
    public final static int OPERATION_PASTE = 2;
    public final static int OPERATION_DELETE = 3;
    public final static int OPERATION_CANCEL = 4;

    private int operation = -1;

    public static OperationEvent create(int operation) {
        OperationEvent event = new OperationEvent();
        event.operation = operation;
        return event;
    }

    public static List<OperationEvent> createAll() {
        List<OperationEvent> list = new ArrayList<>();
        for (int i = OPERATION_COPY; i <= OPERATION_CANCEL; i++) {
            list.add(create(i));
        }
        return list;
    }

    public int getOperation() {
        return operation;
    }
}
