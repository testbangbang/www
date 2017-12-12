package com.onyx.edu.homework;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.CloudStore;
import com.onyx.android.sdk.data.Constant;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.utils.CloudConf;
import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.edu.homework.data.Homework;
import com.onyx.edu.homework.data.HomeworkState;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by lxm on 2017/12/5.
 */

public class DataBundle {

    private static final DataBundle ourInstance = new DataBundle();

    public static DataBundle getInstance() {
        return ourInstance;
    }

    private DataBundle() {
        initCloudManager();
    }

    private void initCloudManager() {
        if (cloudManager == null) {
            cloudManager = CloudStore.createCloudManager(CloudConf.create(Constant.ONYX_HOST_BASE,
                    Constant.ONYX_API_BASE,
                    Constant.DEFAULT_CLOUD_STORAGE));
        }
    }

    private CloudManager cloudManager;
    private DataManager dataManager;
    private String homeworkId;
    private EventBus eventBus;
    private NoteViewHelper noteViewHelper;
    private HomeworkState state = HomeworkState.DOING;

    public CloudManager getCloudManager() {
        return cloudManager;
    }

    public DataManager getDataManager() {
        if (dataManager == null) {
            dataManager = new DataManager();
        }
        return dataManager;
    }

    public String getHomeworkId() {
        return homeworkId;
    }

    public void setHomeworkId(String homeworkId) {
        this.homeworkId = homeworkId;
    }

    public EventBus getEventBus() {
        if (eventBus == null) {
            eventBus = new EventBus();
        }
        return eventBus;
    }

    public void register(Object subscriber) {
        getEventBus().register(subscriber);
    }

    public void unregister(Object subscriber) {
        getEventBus().unregister(subscriber);
    }

    public void post(Object event) {
        getEventBus().post(event);
    }

    public NoteViewHelper getNoteViewHelper() {
        if (noteViewHelper == null) {
            noteViewHelper = new NoteViewHelper();
        }
        return noteViewHelper;
    }

    public void resetNoteViewHelper() {
        getNoteViewHelper().reset();
    }

    public HomeworkState getState() {
        return state;
    }

    public void setState(HomeworkState state) {
        this.state = state;
    }

    public boolean isDone() {
        return state == HomeworkState.DONE;
    }

    public boolean isDoing() {
        return state == HomeworkState.DOING;
    }

    public boolean isReview() {
        return state == HomeworkState.REVIEW;
    }

}
