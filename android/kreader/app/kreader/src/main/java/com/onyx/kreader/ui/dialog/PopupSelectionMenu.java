/**
 *
 */

package com.onyx.kreader.ui.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.RectF;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings.TextSize;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.ui.wifi.NetworkHelper;
import com.onyx.android.sdk.utils.DeviceUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.kreader.R;
import com.onyx.kreader.device.DeviceConfig;
import com.onyx.kreader.ui.actions.DictionaryQueryAction;
import com.onyx.kreader.ui.data.DictionaryQuery;
import com.onyx.kreader.ui.data.ReaderDataHolder;
import com.onyx.kreader.ui.data.SingletonSharedPreference;
import com.onyx.kreader.ui.highlight.HighlightCursor;
import com.onyx.kreader.ui.view.HTMLReaderWebView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.onyx.kreader.ui.data.SingletonSharedPreference.AnnotationHighlightStyle.Highlight;

public class PopupSelectionMenu extends LinearLayout {
    private static final String TAG = PopupSelectionMenu.class.getSimpleName();
    private static final int MAX_DICTIONARY_LOAD_COUNT = 6;
    private static final int DELAY_DICTIONARY_LOAD_TIME = 2000;
    public static final String BAIDU_BAIKE = "https://wapbaike.baidu.com/item/";
    public static final String YANDEX = "https://ya.ru/";


    public enum SelectionType {
        SingleWordType,
        MultiWordsType
    }

    public static abstract class MenuCallback {
        public abstract void resetSelection();
        public abstract String getSelectionText();
        public abstract void copy();
        public abstract void highLight();
        public abstract void addAnnotation();
        public abstract void showDictionary();
        public abstract void startTts();
        public abstract boolean supportSelectionMode();
        public abstract void closeMenu();
    }

    private final Activity mActivity;
    private List<String> dicts = new ArrayList<>();
    private List<DictionaryQuery> textDictionaryQueries = new ArrayList<>();
    private List<DictionaryQuery> soundDictionaryQueries = new ArrayList<>();
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
    private ImageView highlightView;
    private TextView highLightText;
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
    /**
     * eliminate compiler warning
     *
     * @param context
     */
    private PopupSelectionMenu(Context context) {
        super(context);
        throw new IllegalAccessError();
    }

    public PopupSelectionMenu(ReaderDataHolder readerDataHolder, RelativeLayout layout, MenuCallback menuCallback) {
        super(readerDataHolder.getContext());
        mActivity = (Activity)readerDataHolder.getContext();

        setFocusable(false);
        final LayoutInflater inflater = (LayoutInflater)
                readerDataHolder.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.popup_selection_menu, this, true);
        mMenuCallback = menuCallback;

        int heightDenominator = getResources().getInteger(R.integer.dict_height_absolutely_value);
        dictViewWidth = layout.getMeasuredWidth();
        dictViewHeight = (layout.getMeasuredHeight() * heightDenominator / 100);
        layout.addView(this);
        highlightView = (ImageView) findViewById(R.id.imageview_highlight);
        highLightText = (TextView) findViewById(R.id.highLightText);
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
        markerView = (ImageView) findViewById(R.id.marker_view);
        pronounce1 = (ImageView) findViewById(R.id.pronounce_1);
        pronounce2 = (ImageView) findViewById(R.id.pronounce_2);
        webSearch = (ImageView) findViewById(R.id.web_search);
        pronounce1.setEnabled(false);
        pronounce2.setEnabled(false);
        disableMenuByConfig();
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

        boolean isHighLight = SingletonSharedPreference.getAnnotationHighlightStyle(mActivity).equals(Highlight);
        highlightView.setImageResource(isHighLight ?
                R.drawable.ic_dialog_reader_choose_highlight : R.drawable.ic_dialog_reader_choose_underline);
        highLightText.setText(isHighLight ? mActivity.getString(R.string.Highlight) : mActivity.getString(R.string.settings_highlight_style_underline));

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
        mWebView.getSettings().setTextSize(TextSize.LARGER);
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

        LinearLayout imagebuttonCopy = (LinearLayout) findViewById(R.id.imagebutton_copy);
        imagebuttonCopy.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mMenuCallback.copy();
                mMenuCallback.resetSelection();
                PopupSelectionMenu.this.hide();
            }
        });

        LinearLayout imagebuttonHighlight = (LinearLayout) findViewById(R.id.imagebutton_highlight);
        imagebuttonHighlight.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mMenuCallback.highLight();
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
                PopupSelectionMenu.this.hide();
            }
        });

        findViewById(R.id.imagebutton_tts).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mMenuCallback.resetSelection();
                mMenuCallback.startTts();
                PopupSelectionMenu.this.hide();
            }
        });

        pronounce1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                playSoundDictionary(soundDictionaryQueries, 0);
            }
        });
        pronounce2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                playSoundDictionary(soundDictionaryQueries, 1);
            }
        });
        webSearch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                openBaiduBaike();
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
        setVisibility(View.GONE);
        initDictList();
    }

    private void disableMenuByConfig() {
        boolean ttsEnabled = DeviceConfig.sharedInstance(getContext()).isTtsEnabled();
        if (!ttsEnabled) {
            findViewById(R.id.imagebutton_tts).setVisibility(View.GONE);
        } else {
            findViewById(R.id.imagebutton_tts).setVisibility(View.VISIBLE);
        }

        boolean pronounceEnable = DeviceConfig.sharedInstance(getContext()).isPronounceEnable();
        if (!pronounceEnable) {
            pronounce1.setVisibility(View.GONE);
            pronounce2.setVisibility(View.GONE);
        } else {
            pronounce1.setVisibility(View.VISIBLE);
            pronounce2.setVisibility(View.VISIBLE);
        }
    }

    private void openBaiduBaike(){
        if (!NetworkHelper.requestWifi(getActivity())) {
            return;
        }

        String headWord = mDictTitle.getText().toString();
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse(YANDEX);
        intent.setData(content_url);
        getActivity().startActivity(intent);
    }

    private void toggleDictList() {
        if (dictListView.getVisibility() == GONE) {
            showDictListView(dictNameText);
        }else {
            hideDictListView();
        }
    }

    private void hideDictListView() {
        dictListView.setVisibility(GONE);
        markerView.setImageResource(R.drawable.ic_dict_unfold);
    }

    private void showDictListView(TextView dictNameText) {
        int maxWidth = (int) getContext().getResources().getDimension(R.dimen.dict_name_text_view_layout_width);
        int marginLeft =  dictNameText.getLeft() - ((maxWidth - dictNameText.getMeasuredWidth())/2);
        markerView.setImageResource(R.drawable.ic_dict_pack_up);
        dictListView.setVisibility(VISIBLE);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(marginLeft,0, 0,0);
        dictListView.setLayoutParams(lp);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void initDictList() {
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
                dictViewHolder.dictName.setText(textDictionaryQueries.get(position).getDictName());
                dictViewHolder.dictName.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showExplanation(textDictionaryQueries.get(position));
                        hideDictListView();
                    }
                });
            }

            @Override
            public int getItemCount() {
                return textDictionaryQueries.size();
            }
        });
    }

    private class DictViewHolder extends RecyclerView.ViewHolder{

        private TextView dictName;

        public DictViewHolder(View itemView) {
            super(itemView);
            dictName = (TextView)itemView.findViewById(R.id.dict_name);
        }
    }

    Activity getActivity() {
        return mActivity;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    public void show(final ReaderDataHolder readerDataHolder, boolean isWord) {
        if (readerDataHolder.getReaderUserDataInfo().getHighlightResult() == null) {
            return;
        }

        if (isWord) {
            updateTranslation(readerDataHolder, mMenuCallback.getSelectionText());
            showTranslation();
        } else {
            hideTranslation();
        }

        requestLayoutView(readerDataHolder);
    }

    public void hide() {
        mActivity.runOnUiThread(new Runnable() {
            public void run() {
                setVisibility(View.GONE);
            }
        });
    }

    private void requestLayoutView(ReaderDataHolder readerDataHolder){
        setVisibility(VISIBLE);

        HighlightCursor beginHighlightCursor = readerDataHolder.getSelectionManager().getHighlightCursor(HighlightCursor.BEGIN_CURSOR_INDEX);
        HighlightCursor endHighlightCursor = readerDataHolder.getSelectionManager().getHighlightCursor(HighlightCursor.END_CURSOR_INDEX);
        if (beginHighlightCursor == null || endHighlightCursor == null) {
            return;
        }
        RectF beginCursorRectF = beginHighlightCursor.getDisplayRect();
        RectF endCursorRectF = endHighlightCursor.getDisplayRect();

        float dividerHeight = readerDataHolder.getContext().getResources().getDimension(R.dimen.popup_selection_menu_divider_height);

        int w = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
        this.measure(w, h);
        int measuredHeight = this.getMeasuredHeight();

        RectF start = beginCursorRectF;
        RectF end = endCursorRectF;
        final float screenHeight = ((View) this.getParent()).getHeight();
        final float diffTop = start.top;
        final float diffBottom = screenHeight - end.bottom;

        if (diffTop > diffBottom){
            float y = start.top - dividerHeight - measuredHeight;
            y = isShowTranslation() ? 0 : Math.max(y,0);
            setY(y);
            topDividerLine.setVisibility(isShowTranslation() ? GONE : VISIBLE);
            bottomDividerLine.setVisibility(isShowTranslation() ? VISIBLE : VISIBLE);
        }else {
            float y = end.bottom + dividerHeight;
            y = isShowTranslation() ? screenHeight - dictViewHeight : Math.min(y,screenHeight - measuredHeight);
            setY(y);
            topDividerLine.setVisibility(isShowTranslation() ? VISIBLE : VISIBLE);
            bottomDividerLine.setVisibility(isShowTranslation() ? GONE : VISIBLE);
        }
    }

    public boolean isShowTranslation(){
        return this.webViewDividerLine.getVisibility() == VISIBLE;
    }

    public boolean isShow() {
        return (getVisibility() == View.VISIBLE);
    }

    public void hideTranslation() {
        setLayoutParams((int) (dictViewWidth * 0.9), ViewGroup.LayoutParams.WRAP_CONTENT);
        this.webViewDividerLine.setVisibility(GONE);
        dictLayout.setVisibility(View.GONE);
        toolsPlateLeftDividerLine.setVisibility(VISIBLE);
        toolsPlateRightDividerLine.setVisibility(VISIBLE);
    }

    public void showTranslation() {
        setLayoutParams(dictViewWidth, dictViewHeight);
        this.webViewDividerLine.setVisibility(VISIBLE);
        dictLayout.setVisibility(View.VISIBLE);
        toolsPlateLeftDividerLine.setVisibility(GONE);
        toolsPlateRightDividerLine.setVisibility(GONE);
    }

    public void setLayoutParams(int w, int h) {
        RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(w, h);
        p.addRule(RelativeLayout.CENTER_HORIZONTAL);
        setLayoutParams(p);
    }

    private void updateTranslation(final ReaderDataHolder readerDataHolder, String token) {
        token = StringUtils.trimPunctuation(token);
        mDictTitle.setText(token);
        dictionaryQuery(readerDataHolder, token);
    }

    private void dictionaryQuery(final ReaderDataHolder readerDataHolder, final String token) {
        final DictionaryQueryAction dictionaryQueryAction = new DictionaryQueryAction(token);
        dictionaryQueryAction.execute(readerDataHolder, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                textDictionaryQueries = dictionaryQueryAction.getTextDictionaryQueries();
                soundDictionaryQueries = dictionaryQueryAction.getSoundDictionaryQueries();
                handleDictionaryQuery(readerDataHolder, textDictionaryQueries, soundDictionaryQueries, token, dictionaryQueryAction.getErrorInfo());
            }
        });
    }

    private void handleDictionaryQuery(final ReaderDataHolder readerDataHolder, List<DictionaryQuery> textDictionaryQueries, List<DictionaryQuery> soundDictionaryQueries, final String token, final String error) {
        if (textDictionaryQueries.size() > 0 || soundDictionaryQueries.size() > 0) {
            DictionaryQuery query = textDictionaryQueries.size() > 0 ? textDictionaryQueries.get(0) : soundDictionaryQueries.get(0);
            int state = query.getState();
            if (state == DictionaryQuery.DICT_STATE_LOADING) {
                if (dictionaryLoadCount < MAX_DICTIONARY_LOAD_COUNT) {
                    dictionaryLoadCount++;
                    PopupSelectionMenu.this.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dictionaryQuery(readerDataHolder, token);
                        }
                    }, DELAY_DICTIONARY_LOAD_TIME);
                } else {
                    String content = readerDataHolder.getContext().getString(R.string.load_fail);
                    query.setExplanation(content);
                }
            }else {
                dictListView.getAdapter().notifyDataSetChanged();
            }
            if (state == DictionaryQuery.DICT_STATE_QUERY_SUCCESSFUL && textDictionaryQueries.size() > 0) {
                query = textDictionaryQueries.get(0);
            }
            if (state == DictionaryQuery.DICT_STATE_QUERY_SUCCESSFUL && soundDictionaryQueries.size() > 0) {
                enableSoundDictionary(soundDictionaryQueries);
            }
            showExplanation(query);
        }else {
            mWebView.setLoadCssStyle(false);
            mWebView.loadDataWithBaseURL(null, error, "text/html", "utf-8", "about:blank");
        }
    }

    private void enableSoundDictionary(final List<DictionaryQuery> soundDictionaryQueries) {
        pronounce1.setEnabled(soundDictionaryQueries.size() > 0);
        pronounce2.setEnabled(soundDictionaryQueries.size() > 1);
    }

    private void showExplanation(DictionaryQuery dictionaryQuery) {
        String dict = dictionaryQuery.getDictPath();
        String url = "file:///";
        if (!StringUtils.isNullOrEmpty(dict)) {
            url += dict.substring(0, dict.lastIndexOf("/"));
        }
        String dictName = dictionaryQuery.getDictName();
        if (!StringUtils.isNullOrEmpty(dictName)) {
            dictNameText.setText(dictName);
            markerView.setVisibility(VISIBLE);
        }

        mWebView.setLoadCssStyle(dictionaryQuery.getState() == DictionaryQuery.DICT_STATE_QUERY_SUCCESSFUL);
        mWebView.loadDataWithBaseURL(url, dictionaryQuery.getExplanation(), "text/html", "utf-8", "about:blank");
    }

    private void playSoundDictionary(final List<DictionaryQuery> soundDictionaryQueries, final int index) {
        if (soundDictionaryQueries.size() <= index) {
            return;
        }
        DictionaryQuery query = soundDictionaryQueries.get(index);
        String soundPath = query.getSoundPath();
        if (StringUtils.isNullOrEmpty(soundPath)) {
            return;
        }
        try {
            MediaPlayer player  =  new MediaPlayer();
            player.setDataSource(soundPath);
            player.prepare();
            player.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
