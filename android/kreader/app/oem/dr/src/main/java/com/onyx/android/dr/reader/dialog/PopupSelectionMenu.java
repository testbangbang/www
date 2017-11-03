package com.onyx.android.dr.reader.dialog;

import android.app.Activity;
import android.content.Context;
import android.graphics.RectF;
import android.media.MediaPlayer;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.bean.NewWordBean;
import com.onyx.android.dr.common.ActivityManager;
import com.onyx.android.dr.common.CommonNotices;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.manager.OperatingDataManager;
import com.onyx.android.dr.reader.event.ReaderGoodSentenceMenuEvent;
import com.onyx.android.dr.reader.highlight.HighlightCursor;
import com.onyx.android.dr.reader.presenter.ReaderPresenter;
import com.onyx.android.dr.reader.view.HTMLReaderWebView;
import com.onyx.android.dr.util.Utils;
import com.onyx.android.sdk.dict.data.DictionaryQueryResult;
import com.onyx.android.sdk.dict.request.QueryWordRequest;
import com.onyx.android.sdk.dict.request.common.DictBaseCallback;
import com.onyx.android.sdk.dict.request.common.DictBaseRequest;
import com.onyx.android.sdk.reader.common.PageAnnotation;
import com.onyx.android.sdk.ui.wifi.NetworkHelper;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PopupSelectionMenu extends LinearLayout {
    private static final String TAG = PopupSelectionMenu.class.getSimpleName();
    private static final int MAX_DICTIONARY_LOAD_COUNT = 6;
    private static final int DELAY_DICTIONARY_LOAD_TIME = 2000;
    private final LinearLayout noDataContainer;
    private QueryWordRequest queryWordRequest;
    private List<String> pathList;
    private View addWordButton;
    private View baiduButton;
    private View addSentenceButton;
    private View deleteMarkButton;
    public static final String BAIDU_BAIKE = "https://wapbaike.baidu.com/item/";
    public static final String WIKTIONARY_URL = "https://en.wiktionary.org/wiki/";
    private int number;

    public enum SelectionType {
        SingleWordType,
        MultiWordsType
    }

    public static abstract class MenuCallback {
        public abstract void resetSelection();

        public abstract String getSelectionText();

        public abstract void copy();

        public abstract void onLineation();

        public abstract void addAnnotation();

        public abstract void showDictionary();

        public abstract void startTts();

        public abstract boolean supportSelectionMode();

        public abstract void closeMenu();

        public abstract void deleteAnnotation();
    }

    private final Activity mActivity;
    private List<String> dicts = new ArrayList<>();
    private List<DictionaryQueryResult> dictionaryQueries = new ArrayList<>();
    private TextView mDictTitle;
    private HTMLReaderWebView mWebView;
    private TextView mPageIndicator;
    private MenuCallback mMenuCallback;
    private View mDictNextPage;
    private View mDictPrevPage;
    private View webViewDividerLine;
    private View topDividerLine;
    private View bottomDividerLine;
    private View toolsPlateLeftDividerLine;
    private View toolsPlateRightDividerLine;
    private TextView noteText;
    private TextView dictNameText;
    private RecyclerView dictListView;
    private LinearLayout dictLayout;
    private ImageView markerView;
    private ImageView pronounce1;
    private ImageView pronounce2;
    private ImageView webSearch;
    private int dictViewHeight;
    private int dictViewWidth;
    private int dictionaryLoadCount;
    private DialogAnnotation.AnnotationAction action;

    /**
     * eliminate compiler warning
     *
     * @param context
     */
    private PopupSelectionMenu(Context context) {
        super(context);
        throw new IllegalAccessError();
    }

    public PopupSelectionMenu(final ReaderPresenter readerPresenter, RelativeLayout layout, final DialogAnnotation.AnnotationAction action, MenuCallback menuCallback) {
        super(readerPresenter.getReaderView().getViewContext());
        mActivity = (Activity) readerPresenter.getReaderView().getViewContext();
        this.action = action;

        setFocusable(false);
        final LayoutInflater inflater = (LayoutInflater)
                readerPresenter.getReaderView().getViewContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.popup_selection_menu, this, true);
        mMenuCallback = menuCallback;

        int heightDenominator = getResources().getInteger(R.integer.dict_height_absolutely_value);
        dictViewWidth = layout.getMeasuredWidth();
        dictViewHeight = (layout.getMeasuredHeight() * heightDenominator / 85);
        layout.addView(this);
        noteText = (TextView) findViewById(R.id.noteText);
        mDictTitle = (TextView) findViewById(R.id.dict_title);
        webViewDividerLine = findViewById(R.id.webView_divider_line);
        topDividerLine = findViewById(R.id.top_divider_line);
        bottomDividerLine = findViewById(R.id.bottom_divider_line);
        toolsPlateLeftDividerLine = findViewById(R.id.tools_plate_left_divider_line);
        toolsPlateRightDividerLine = findViewById(R.id.tools_plate_right_divider_line);
        mDictNextPage = findViewById(R.id.dict_next_page);
        dictNameText = (TextView) findViewById(R.id.dict_name);
        dictListView = (RecyclerView) findViewById(R.id.dict_list);
        dictLayout = (LinearLayout) findViewById(R.id.layout_dict);
        noDataContainer = (LinearLayout) findViewById(R.id.popup_selection_menu_no_data_container);
        markerView = (ImageView) findViewById(R.id.marker_view);
        pronounce1 = (ImageView) findViewById(R.id.pronounce_1);
        pronounce2 = (ImageView) findViewById(R.id.pronounce_2);
        webSearch = (ImageView) findViewById(R.id.web_search);
        pronounce1.setEnabled(false);
        pronounce2.setEnabled(false);
        mDictNextPage.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mWebView != null) {
                    mWebView.nextPage();
                }
            }
        });
        markerView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleDictList();
            }
        });
        dictNameText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleDictList();
            }
        });

        mDictPrevPage = findViewById(R.id.dict_prev_page);
        mDictPrevPage.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mWebView != null) {
                    mWebView.prevPage();
                }
            }
        });

        mPageIndicator = (TextView) findViewById(R.id.page_indicator);

        mWebView = (HTMLReaderWebView) findViewById(R.id.explain);
        mWebView.getSettings().setTextSize(WebSettings.TextSize.LARGER);
        mWebView.setPageTurnType(HTMLReaderWebView.PAGE_TURN_TYPE_VERTICAL);
        mWebView.setPageTurnThreshold(15);
        mWebView.registerOnOnPageChangedListener(new HTMLReaderWebView.OnPageChangedListener() {

            @Override
            public void onPageChanged(int totalPage, int curPage) {
                if (totalPage > 1) {
                    mPageIndicator.setVisibility(View.VISIBLE);
                    mDictNextPage.setVisibility(View.VISIBLE);
                    mDictPrevPage.setVisibility(View.VISIBLE);
                }
                mPageIndicator.setText(curPage + "/" + totalPage);
            }
        });

        LinearLayout imagebuttonLineation = (LinearLayout) findViewById(R.id.imagebutton_lineation);
        imagebuttonLineation.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (action == DialogAnnotation.AnnotationAction.update) {
                    mMenuCallback.deleteAnnotation();
                } else {
                    mMenuCallback.onLineation();
                }
                PopupSelectionMenu.this.hide();
            }
        });

        LinearLayout imagebuttonScreenshot = (LinearLayout) findViewById(R.id.imagebutton_screenshot);
        imagebuttonScreenshot.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                PopupSelectionMenu.this.hide();
            }
        });

        LinearLayout imagebuttonAnnotation = (LinearLayout) findViewById(R.id.imagebutton_annotaion);
        imagebuttonAnnotation.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mMenuCallback.addAnnotation();
                PopupSelectionMenu.this.hide();
            }
        });

        LinearLayout button_dict = (LinearLayout) findViewById(R.id.imagebutton_dict);
        button_dict.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mMenuCallback.showDictionary();
                mMenuCallback.resetSelection();
                mMenuCallback.closeMenu();
            }
        });

        findViewById(R.id.imagebutton_search).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mMenuCallback.resetSelection();
                mMenuCallback.startTts();
                mMenuCallback.closeMenu();
            }
        });

        pronounce1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                playSoundDictionary(dictionaryQueries, 0);
            }
        });
        pronounce2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                playSoundDictionary(dictionaryQueries, 1);
            }
        });
        webSearch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                webSearchWord();
            }
        });

        final ImageView buttonCloseMenu = (ImageView) findViewById(R.id.button_close);
        buttonCloseMenu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mMenuCallback.closeMenu();
            }
        });

        dictLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideDictListView();
            }
        });

        addWordButton = findViewById(R.id.imagebutton_add_word);
        baiduButton = findViewById(R.id.imagebutton_bai_du);
        addSentenceButton = findViewById(R.id.imagebutton_add_good_sentence);
        deleteMarkButton = findViewById(R.id.imagebutton_delete_mark);

        addWordButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewWord(readerPresenter);
                mMenuCallback.closeMenu();
            }
        });
        baiduButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                webSearchWord();
                mMenuCallback.closeMenu();
            }
        });
        addSentenceButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new ReaderGoodSentenceMenuEvent());
                mMenuCallback.closeMenu();
            }
        });
        deleteMarkButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mMenuCallback.deleteAnnotation();
            }
        });
        setVisibility(View.GONE);
        initDictList(readerPresenter);
    }

    private void addNewWord(ReaderPresenter readerPresenter) {
        NewWordBean newWordBean = new NewWordBean();
        newWordBean.setReadingMatter(readerPresenter.getBookInfo().getBookName());
        newWordBean.setPageNumber(String.valueOf(readerPresenter.getReaderViewInfo().getFirstVisiblePage().getName()));
        newWordBean.setNewWordType(getGoodSentenceType(readerPresenter.getBookInfo().getLanguage()));
        newWordBean.setTag(true);
        newWordBean.setNewWord(readerPresenter.getBookOperate().getSelectionText());
        OperatingDataManager.getInstance().insertNewWord(newWordBean);
    }

    private void updateButtonName(ReaderPresenter readerPresenter) {
        String buttonName;
        if (action == DialogAnnotation.AnnotationAction.add) {
            buttonName = readerPresenter.getReaderView().getViewContext().getString(R.string.annotation);
        } else {
            buttonName = readerPresenter.getReaderView().getViewContext().getString(R.string.delete);
        }
        ((TextView) findViewById(R.id.text_view_lineation)).setText(buttonName);
    }

    private void webSearchWord() {
        if (!NetworkHelper.requestWifi(getActivity())) {
            return;
        }

        String headWord = mDictTitle.getText().toString();
        ActivityManager.startBaiduBaiKeActivity(getActivity(), headWord);
    }

    private void toggleDictList() {
        if (dictListView.getVisibility() == GONE) {
            showDictListView(dictNameText);
        } else {
            hideDictListView();
        }
    }

    private void hideDictListView() {
        dictListView.setVisibility(GONE);
        markerView.setImageResource(R.drawable.ic_dict_unfold);
    }

    private void showDictListView(TextView dictNameText) {
        int maxWidth = (int) getContext().getResources().getDimension(R.dimen.dict_name_text_view_layout_width);
        int marginLeft = dictNameText.getLeft() - ((maxWidth - dictNameText.getMeasuredWidth()) / 2);
        markerView.setImageResource(R.drawable.ic_dict_pack_up);
        dictListView.setVisibility(VISIBLE);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(marginLeft, 0, 0, 0);
        dictListView.setLayoutParams(lp);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void initDictList(ReaderPresenter readerPresenter) {
        dicts.clear();
        dictListView.setLayoutManager(new LinearLayoutManager(getContext()));
        dictListView.setAdapter(new RecyclerView.Adapter() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new DictViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.dict_list_item_view, parent, false));
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
                DictViewHolder dictViewHolder = (DictViewHolder) holder;
                dictViewHolder.dictName.setText(dictionaryQueries.get(position).getDictionary().name);
                dictViewHolder.dictName.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showExplanation(dictionaryQueries.get(position));
                        hideDictListView();
                    }
                });
            }

            @Override
            public int getItemCount() {
                return dictionaryQueries.size();
            }
        });

        updateTranslation(readerPresenter, readerPresenter.getBookOperate().getSelectionText());
    }

    private class DictViewHolder extends RecyclerView.ViewHolder {

        private TextView dictName;

        public DictViewHolder(View itemView) {
            super(itemView);
            dictName = (TextView) itemView.findViewById(R.id.dict_name);
        }
    }

    Activity getActivity() {
        return mActivity;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    public void show(final ReaderPresenter readerPresenter, boolean isWord) {
        hideTranslation(isWord);
        updateButtonName(readerPresenter);
        requestLayoutView(readerPresenter);
    }

    public void hide() {
        mActivity.runOnUiThread(new Runnable() {
            public void run() {
                setVisibility(View.GONE);
            }
        });
    }

    private void requestLayoutView(ReaderPresenter readerPresenter) {
        setVisibility(VISIBLE);
        HighlightCursor beginHighlightCursor;
        HighlightCursor endHighlightCursor;
        RectF beginCursorRectF;
        RectF endCursorRectF;
        if (readerPresenter.getPageAnnotation() == null) {
            beginHighlightCursor = readerPresenter.getReaderSelectionManager().getHighlightCursor(HighlightCursor.BEGIN_CURSOR_INDEX);
            endHighlightCursor = readerPresenter.getReaderSelectionManager().getHighlightCursor(HighlightCursor.END_CURSOR_INDEX);
            if (beginHighlightCursor == null || endHighlightCursor == null) {
                return;
            }
            beginCursorRectF = beginHighlightCursor.getDisplayRect();
            endCursorRectF = endHighlightCursor.getDisplayRect();
        } else {
            PageAnnotation pageAnnotation = readerPresenter.getPageAnnotation();
            if (pageAnnotation.getRectangles().size() <= 0) {
                return;
            }
            beginCursorRectF = pageAnnotation.getRectangles().get(0);
            endCursorRectF = pageAnnotation.getRectangles().get(pageAnnotation.getRectangles().size() - 1);
        }

        float dividerHeight = readerPresenter.getReaderView().getViewContext().getResources().getDimension(R.dimen.popup_selection_menu_divider_height);

        int w = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        int h = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        this.measure(w, h);
        int measuredHeight = this.getMeasuredHeight();

        RectF start = beginCursorRectF;
        RectF end = endCursorRectF;
        final float screenHeight = ((View) this.getParent()).getHeight();
        final float diffTop = start.top;
        final float diffBottom = screenHeight - end.bottom;

        if (diffTop > diffBottom) {
            float y = start.top - dividerHeight - measuredHeight;
            y = isShowTranslation() ? 0 : Math.max(y, 0);
            setY(y);
            topDividerLine.setVisibility(isShowTranslation() ? GONE : VISIBLE);
            bottomDividerLine.setVisibility(isShowTranslation() ? VISIBLE : VISIBLE);
        } else {
            float y = end.bottom + dividerHeight;
            y = isShowTranslation() ? screenHeight - dictViewHeight : Math.min(y, screenHeight - measuredHeight);
            setY(y);
            topDividerLine.setVisibility(isShowTranslation() ? VISIBLE : VISIBLE);
            bottomDividerLine.setVisibility(isShowTranslation() ? GONE : VISIBLE);
        }
    }

    public boolean isShowTranslation() {
        return this.webViewDividerLine.getVisibility() == VISIBLE;
    }

    public boolean isShow() {
        return (getVisibility() == View.VISIBLE);
    }

    public void hideTranslation(boolean isWord) {
        setLayoutParams((int) (dictViewWidth * 0.9), ViewGroup.LayoutParams.WRAP_CONTENT);
        if (!isWord) {
            this.webViewDividerLine.setVisibility(GONE);
            dictLayout.setVisibility(View.GONE);
        }
        addWordButton.setVisibility(isWord ? VISIBLE : GONE);
        baiduButton.setVisibility(isWord ? VISIBLE : GONE);
        addSentenceButton.setVisibility(isWord ? GONE : VISIBLE);
        deleteMarkButton.setVisibility(isWord ? GONE : VISIBLE);
        toolsPlateLeftDividerLine.setVisibility(VISIBLE);
        toolsPlateRightDividerLine.setVisibility(VISIBLE);
    }

    public void showTranslation() {
        setLayoutParams(dictViewWidth, dictViewHeight);
        this.webViewDividerLine.setVisibility(GONE);
        dictLayout.setVisibility(View.VISIBLE);
        toolsPlateLeftDividerLine.setVisibility(GONE);
        toolsPlateRightDividerLine.setVisibility(GONE);
    }

    public void setLayoutParams(int w, int h) {
        RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(w, h);
        p.addRule(RelativeLayout.CENTER_HORIZONTAL);
        setLayoutParams(p);
    }

    private void updateTranslation(final ReaderPresenter readerPresenter, String token) {
        token = StringUtils.trimPunctuation(token);
        mDictTitle.setText(token);
        pathList = Utils.getDictPathListByMap(getGoodSentenceType(readerPresenter.getBookInfo().getLanguage()));
        dictionaryQuery(readerPresenter, token);
    }

    private void dictionaryQuery(final ReaderPresenter readerPresenter, final String token) {
        queryWordRequest = new QueryWordRequest(token);
        number = 0;
        boolean bRet = DRApplication.getInstance().getDictionaryManager().sendRequest(DRApplication.getInstance(), queryWordRequest, pathList, new DictBaseCallback() {
            @Override
            public void done(DictBaseRequest request, Exception e) {
                if (queryWordRequest.queryResult == null || queryWordRequest.queryResult.size() <= 0) {
                    number++;
                    if (number <= 1) {
                        setViewGone();
                    }
                    return;
                }
                setViewVisible();
                dictionaryQueries.clear();
                for (String key : queryWordRequest.queryResult.keySet()) {
                    dicts.add(key);
                    dictionaryQueries.add(queryWordRequest.queryResult.get(key));
                }
                if (!CollectionUtils.isNullOrEmpty(dictionaryQueries)) {
                    showExplanation(dictionaryQueries.get(0));
                }
            }
        });
        if (!bRet) {
            setViewGone();
            CommonNotices.showMessage(readerPresenter.getReaderView().getApplicationContext(), readerPresenter.getReaderView().getApplicationContext().getResources().getString(R.string.headword_search_empty));
        }
    }

    private void setViewGone() {
        noDataContainer.setVisibility(View.VISIBLE);
        mWebView.setVisibility(View.GONE);
        dictNameText.setVisibility(View.INVISIBLE);
        markerView.setVisibility(View.INVISIBLE);
    }

    private void setViewVisible() {
        noDataContainer.setVisibility(View.GONE);
        mWebView.setVisibility(View.VISIBLE);
        dictNameText.setVisibility(View.VISIBLE);
        markerView.setVisibility(View.VISIBLE);
    }

    private int getGoodSentenceType(String language) {
        int type;
        if (StringUtils.isNotBlank(language)) {
            switch (language) {
                case Constants.CHINESE:
                    type = Constants.CHINESE_TYPE;
                    break;
                case Constants.ENGLISH:
                    type = Constants.ENGLISH_TYPE;
                    break;
                default:
                    type = Constants.OTHER_TYPE;
            }
        } else {
            type = Constants.OTHER_TYPE;
        }
        return type;
    }

    private void enableSoundDictionary(final List<DictionaryQueryResult> soundDictionaryQueries) {
        pronounce1.setEnabled(soundDictionaryQueries.size() > 0);
        pronounce2.setEnabled(soundDictionaryQueries.size() > 1);
    }

    private void showExplanation(DictionaryQueryResult dictionaryQuery) {
        String dict = dictionaryQuery.getDictionary().dictPath;
        String url = "file:///";
        if (!StringUtils.isNullOrEmpty(dict)) {
            url += dict.substring(0, dict.lastIndexOf("/"));
        }
        String dictName = dictionaryQuery.getDictionary().name;
        if (!StringUtils.isNullOrEmpty(dictName)) {
            dictNameText.setText(dictName);
            markerView.setVisibility(VISIBLE);
        }

        mWebView.setLoadCssStyle(true);
        mWebView.loadDataWithBaseURL(url, dictionaryQuery.getExplanation(), "text/html", "utf-8", "about:blank");
    }

    private void playSoundDictionary(final List<DictionaryQueryResult> soundDictionaryQueries, final int index) {
        if (soundDictionaryQueries.size() <= index) {
            return;
        }
        DictionaryQueryResult query = soundDictionaryQueries.get(index);
        String soundPath = query.getSoundPath();
        if (StringUtils.isNullOrEmpty(soundPath)) {
            return;
        }
        try {
            MediaPlayer player = new MediaPlayer();
            player.setDataSource(soundPath);
            player.prepare();
            player.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public DialogAnnotation.AnnotationAction getAction() {
        return action;
    }

    public void setAction(DialogAnnotation.AnnotationAction action) {
        this.action = action;
    }
}
