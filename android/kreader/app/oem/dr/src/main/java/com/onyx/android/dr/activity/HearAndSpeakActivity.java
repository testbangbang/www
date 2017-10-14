package com.onyx.android.dr.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.onyx.android.dr.R;
import com.onyx.android.dr.data.MenuBean;
import com.onyx.android.dr.event.ArticleRepeatAfterEvent;
import com.onyx.android.dr.event.SearchKeywordEvent;
import com.onyx.android.dr.event.SpeechRecordingEvent;
import com.onyx.android.dr.fragment.ArticleToReadFragment;
import com.onyx.android.dr.fragment.BaseFragment;
import com.onyx.android.dr.fragment.ChildViewID;
import com.onyx.android.dr.fragment.PronounceEvaluationFragment;
import com.onyx.android.dr.fragment.SpeechRecordingFragment;
import com.onyx.android.dr.interfaces.HearAndSpeakView;
import com.onyx.android.dr.util.DRPreferenceManager;
import com.onyx.android.dr.util.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by zhouzhiming on 2017/7/31.
 */
public class HearAndSpeakActivity extends BaseActivity implements HearAndSpeakView {
    @Bind(R.id.hear_and_speak_activity_container)
    FrameLayout containerFrameLayout;
    @Bind(R.id.hear_and_speak_activity_radio_group)
    RadioGroup radioGroup;
    @Bind(R.id.hear_and_speak_activity_article_repeat_after)
    RadioButton articleRepeatAfter;
    @Bind(R.id.hear_and_speak_activity_speech_recording)
    RadioButton speechRecording;
    @Bind(R.id.hear_and_speak_activity_pronounce_evaluation)
    RadioButton pronounceEvaluation;
    @Bind(R.id.image)
    ImageView image;
    @Bind(R.id.menu_back)
    LinearLayout menuBack;
    @Bind(R.id.title_bar_title)
    TextView title;
    @Bind(R.id.title_bar_right_image)
    ImageView searchImage;
    @Bind(R.id.title_bar_right_edit_text)
    EditText searchEditText;
    private int currentPageID = ChildViewID.ARTICLE_REPEAT_AFTER;
    private SparseArray<BaseFragment> childViewList = new SparseArray<>();
    private FragmentManager fragmentManager;
    private BaseFragment currentFragment;
    private int number = 0;

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_hear_and_speak;
    }

    @Override
    protected void initConfig() {
    }

    @Override
    protected void initView() {
        initRecyclerView();
    }

    private void initRecyclerView() {
    }

    @Override
    protected void initData() {
        BaseFragment baseFragment = new SpeechRecordingFragment();
        childViewList.put(ChildViewID.ARTICLE_REPEAT_AFTER, baseFragment);
        switchCurrentFragment(ChildViewID.ARTICLE_REPEAT_AFTER);
        searchImage.setVisibility(View.VISIBLE);
        searchEditText.setVisibility(View.GONE);
        initTitleData();
        initEvent();
    }

    private void initTitleData() {
        image.setImageResource(R.drawable.listen_and_speak);
        image.invalidate();
        title.setText(getString(R.string.menu_listen_and_say));
    }

    public void switchCurrentFragment(int pageID) {
        if (number == 0) {
            if (currentPageID == pageID) {
                return;
            }
        }
        if (fragmentManager == null) {
            fragmentManager = getFragmentManager();
        }
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (currentFragment != null && currentFragment.isVisible()) {
            transaction.hide(currentFragment);
        }
        BaseFragment baseFragment = getPageView(pageID);
        if (baseFragment.isStored) {
            transaction.show(baseFragment);
        } else {
            transaction.add(R.id.hear_and_speak_activity_container, baseFragment);
            childViewList.put(pageID, baseFragment);
        }
        currentFragment = baseFragment;
        currentPageID = pageID;
        transaction.commitAllowingStateLoss();
    }

    private BaseFragment getPageView(int pageID) {
        BaseFragment baseFragment = childViewList.get(pageID);
        if (baseFragment == null) {
            switch (pageID) {
                case ChildViewID.ARTICLE_REPEAT_AFTER:
                    baseFragment = new ArticleToReadFragment();
                    break;
                case ChildViewID.SPEECH_RECORDING:
                    baseFragment = new SpeechRecordingFragment();
                    break;
                case ChildViewID.PRONOUNCE_EVALUATION:
                    baseFragment = new PronounceEvaluationFragment();
                    break;
            }
        } else {
            baseFragment.isStored = true;
        }
        return baseFragment;
    }

    @Override
    public void setHearAndSpeakData(List<MenuBean> menuDatas) {
    }

    public void initEvent() {
    }

    @OnClick({R.id.menu_back,
            R.id.hear_and_speak_activity_speech_recording,
            R.id.hear_and_speak_activity_pronounce_evaluation,
            R.id.title_bar_right_image,
            R.id.hear_and_speak_activity_article_repeat_after})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.menu_back:
                finish();
                break;
            case R.id.hear_and_speak_activity_article_repeat_after:
                showRelatedFragment(ChildViewID.ARTICLE_REPEAT_AFTER);
                break;
            case R.id.hear_and_speak_activity_speech_recording:
                showRelatedFragment(ChildViewID.SPEECH_RECORDING);
                break;
            case R.id.hear_and_speak_activity_pronounce_evaluation:
                showRelatedFragment(ChildViewID.PRONOUNCE_EVALUATION);
                break;
            case R.id.title_bar_right_image:
                searchData();
                break;
        }
    }

    private void showRelatedFragment(int tag) {
        DRPreferenceManager.saveSearchKeyword(this, "");
        searchEditText.setVisibility(View.GONE);
        number = 0;
        switchCurrentFragment(tag);
    }

    private void searchData() {
        if (number == 0) {
            searchEditText.setVisibility(View.VISIBLE);
            number++;
        } else {
            String text = searchEditText.getText().toString();
            DRPreferenceManager.saveSearchKeyword(this, text);
            Utils.hideSoftWindow(this);
            EventBus.getDefault().post(new SearchKeywordEvent());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onArticleRepeatAfterEvent(ArticleRepeatAfterEvent event) {
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSpeechRecordingEvent(SpeechRecordingEvent event) {
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        BaseFragment currentFragment = getPageView(currentPageID);
        if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (currentFragment != null && currentFragment.onKeyBack()) {
                return true;
            }
        }
        if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_PAGE_UP) {
            if (currentFragment != null && currentFragment.onKeyPageUp()) {
                return true;
            }
        }
        if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_PAGE_DOWN) {
            if (currentFragment != null && currentFragment.onKeyPageDown()) {
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
