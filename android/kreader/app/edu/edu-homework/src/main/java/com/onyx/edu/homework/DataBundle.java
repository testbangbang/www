package com.onyx.edu.homework;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.CloudStore;
import com.onyx.android.sdk.data.Constant;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.model.homework.Homework;
import com.onyx.android.sdk.data.utils.CloudConf;
import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.data.ShapeState;
import com.onyx.edu.homework.data.HomeworkIntent;
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
    private Homework homework;
    private EventBus eventBus;
    private NoteViewHelper noteViewHelper;
    private HomeworkState state = HomeworkState.DOING;
    private String childId;
    private String personalHomeworkId;

    public CloudManager getCloudManager() {
        return cloudManager;
    }

    public DataManager getDataManager() {
        if (dataManager == null) {
            dataManager = new DataManager();
        }
        return dataManager;
    }

    public DataBundle setPersonalHomeworkId(String personalHomeworkId) {
        this.personalHomeworkId = personalHomeworkId;
        return this;
    }

    public DataBundle setChildId(String childId) {
        this.childId = childId;
        return this;
    }

    public String getChildId() {
        return childId;
    }

    public String getPersonalHomeworkId() {
        return personalHomeworkId;
    }

    public void setHomeworkId(String id) {
        getHomework().setHomeworkId(id);
    }

    public void setHomework(Homework homework) {
        this.homework = homework;
    }

    public Homework getHomework() {
        if (homework == null) {
            homework = new Homework();
        }
        return homework;
    }

    public void updateHomeworkFromIntent(HomeworkIntent intent) {
        if (intent == null) {
            return;
        }
        getHomework().setTitle(intent.child.title);
        getHomework().setPublishedAnswer(getHomework().publishedAnswer || intent.readActive);
        getHomework().setHasReview(getHomework().hasReview || intent.checked);

        if (homework.needUpdateEndTime(intent.endTime)) {
            getHomework().setEndTime(intent.endTime);
        }
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

    public void quit() {
        getNoteViewHelper().quit();
    }

    public void post(Object event) {
        getEventBus().post(event);
    }

    public NoteViewHelper getNoteViewHelper() {
        if (noteViewHelper == null) {
            noteViewHelper = new NoteViewHelper(HomeworkApp.instance);
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
        if (isReview()) {
            getNoteViewHelper().updateShapeState(ShapeState.REDOING);
        }
    }

    public boolean isSubmitted() {
        return state == HomeworkState.SUBMITTED;
    }

    public boolean isDoing() {
        return state == HomeworkState.DOING || state == HomeworkState.SUBMITTED;
    }

    public boolean isReview() {
        return state == HomeworkState.REVIEW;
    }

    public boolean isDoingAndExpired() {
        return isDoing() && getHomework().isExpired();
    }

    public boolean canCheckAnswer() {
        return getHomework().isPublishedAnswer();
    }

    public boolean canGetReview() {
        return !isReview() && getHomework().hasReview();
    }

}
