package com.onyx.edu.homework;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.CloudStore;
import com.onyx.android.sdk.data.Constant;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.model.homework.Homework;
import com.onyx.android.sdk.data.model.homework.Question;
import com.onyx.android.sdk.data.utils.CloudConf;
import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.data.ShapeState;
import com.onyx.android.sdk.utils.Debug;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.edu.homework.data.HomeworkIntent;
import com.onyx.edu.homework.data.HomeworkState;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

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
    private HomeworkState state = HomeworkState.BEFORE_SUBMIT;
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
        if (!getEventBus().isRegistered(subscriber)) {
            getEventBus().register(subscriber);
        }else {
            Debug.i(subscriber + " has been registered");
        }
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
        return state == HomeworkState.SUBMITTED || state == HomeworkState.SUBMITTED_AFTER_REVIEW;
    }

    public boolean isSubmittedAfterReview() {
        return state == HomeworkState.SUBMITTED_AFTER_REVIEW;
    }

    public boolean isSubmittedBeforeReview() {
        return state == HomeworkState.SUBMITTED;
    }

    public boolean isReview() {
        return state == HomeworkState.REVIEW;
    }

    public boolean isExpired() {
        return getHomework().isExpired();
    }

    public boolean beforeReview() {
        return state.ordinal() < HomeworkState.REVIEW.ordinal();
    }

    public boolean afterReview() {
        return state.ordinal() >= HomeworkState.REVIEW.ordinal();
    }

    public boolean canCheckAnswer() {
        return getHomework().isPublishedAnswer();
    }

    public boolean canGetReview() {
        return !isReview() && getHomework().hasReview();
    }

    public boolean hasReview() {
        return getHomework().hasReview();
    }

    public boolean isCorrectingAnswer() {
        return isSubmittedAfterReview() || isReview();
    }

    public List<Question> getQuestions() {
        return getHomework().getQuestions();
    }

    public Question getQuestion(String docId) {
        List<Question> questions = getHomework().getQuestions();
        if (CollectionUtils.isNullOrEmpty(questions)) {
            return null;
        }
        for (Question question : questions) {
            if (question.uniqueId.equals(docId)) {
                return question;
            }
        }
        return null;
    }
}
