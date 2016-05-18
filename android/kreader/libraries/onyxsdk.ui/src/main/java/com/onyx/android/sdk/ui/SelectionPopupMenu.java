/**
 * 
 */

package com.onyx.android.sdk.ui;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings.TextSize;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.onyx.android.sdk.ui.R;
import com.onyx.android.sdk.device.DeviceInfo;
import com.onyx.android.sdk.reader.TextSelectionMode;
import com.onyx.android.sdk.ui.util.HTMLReaderWebView;
import com.onyx.android.sdk.ui.util.HTMLReaderWebView.OnPageChangedListener;

/**
 * 
 * @author qingyue
 *
 */
public class SelectionPopupMenu extends LinearLayout {
    
    public static interface ISelectionHandler
    {
        public void resetSelection();
        public String getSelectionText();
        public void copy();
        public void highLight();
        public void addAnnotation();
        public void showDictionary();
        public boolean supportSelectionMode();
        public TextSelectionMode getSelectionMode();
        public void setSelectionMode(TextSelectionMode mode);
    }

    private final Activity mActivity;
    private ISelectionHandler mHandler;
    private TextView mDictTitle;
    private HTMLReaderWebView mWebView;
    private TextView mPageIndicator;
    private View mDictNextPage;
    private View mDictPrevPage;
    
    /**
     * eliminate compiler warning
     * 
     * @param context
     */
    private SelectionPopupMenu(Context context)
    {
        super(context);
        throw new IllegalAccessError();
    }

    public SelectionPopupMenu(Activity activity, final ISelectionHandler handler, RelativeLayout layout)
    {
        super(activity);
        mActivity = activity;
        mHandler = handler;
        
        setFocusable(false);

        final LayoutInflater inflater = (LayoutInflater)
                activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.control_selection_popup_menu, this, true);

        if (DeviceInfo.currentDevice.IsSmallScreen()) {
            View v = this.findViewById(R.id.imageview_copy);
            if (v != null) {
                v.setVisibility(View.GONE);
            }
            v = this.findViewById(R.id.imageview_highlight);
            if (v != null) {
                v.setVisibility(View.GONE);
            }
            v = this.findViewById(R.id.imageview_annotation);
            if (v != null) {
                v.setVisibility(View.GONE);
            }
            v = this.findViewById(R.id.imageview_translate);
            if (v != null) {
                v.setVisibility(View.GONE);
            }
        }

        RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        p.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        p.addRule(RelativeLayout.CENTER_HORIZONTAL);
        layout.addView(this, p);
        
        mDictTitle = (TextView) findViewById(R.id.dict_title);
        
        mDictNextPage = findViewById(R.id.dict_next_page);
        mDictNextPage.setOnClickListener(new View.OnClickListener()
        {
            
            @Override
            public void onClick(View v)
            {
                if (mWebView != null) {
                    mWebView.nextPage();
                }
            }
        });
        
        mDictPrevPage = findViewById(R.id.dict_prev_page);
        mDictPrevPage.setOnClickListener(new View.OnClickListener()
        {
            
            @Override
            public void onClick(View v)
            {
                if (mWebView != null) {
                    mWebView.prevPage();
                }
            }
        });

        mPageIndicator = (TextView) findViewById(R.id.page_indicator);

        mWebView = (HTMLReaderWebView) findViewById(R.id.explain);
        mWebView.getSettings().setTextSize(TextSize.LARGER);
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.setPageTurnType(HTMLReaderWebView.PAGE_TURN_TYPE_VERTICAL);
        mWebView.setPageTurnThreshold(15);
        mWebView.registerOnOnPageChangedListener(new OnPageChangedListener() {

            @Override
            public void onPageChanged(int totalPage, int curPage)
            {
                if (totalPage > 1) {
                    mPageIndicator.setVisibility(View.VISIBLE);
                    mDictNextPage.setVisibility(View.VISIBLE);
                    mDictPrevPage.setVisibility(View.VISIBLE);
                }
                mPageIndicator.setText(curPage + "/" + totalPage);
            }
        });
        
        View radioGroupSelectionMode = findViewById(R.id.radioGroupSelectionMode);
        if (handler.supportSelectionMode()) {
            radioGroupSelectionMode.setVisibility(View.VISIBLE);
        }
        
        RadioButton radioIntelligent = (RadioButton)findViewById(R.id.radioIntelligent);
        radioIntelligent.setOnCheckedChangeListener(new RadioButton.OnCheckedChangeListener()
        {
            
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked) {
                    handler.setSelectionMode(TextSelectionMode.Intelligent);
                }
            }
        });
        RadioButton radioPrecise = (RadioButton)findViewById(R.id.radioPrecise);
        radioPrecise.setOnCheckedChangeListener(new RadioButton.OnCheckedChangeListener()
        {
            
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked) {
                    handler.setSelectionMode(TextSelectionMode.Precise);
                }
            }
        });
        
        if (handler.getSelectionMode() == TextSelectionMode.Intelligent) {
            radioIntelligent.setChecked(true);
        }
        else if (handler.getSelectionMode() == TextSelectionMode.Precise) {
            radioPrecise.setChecked(true);
        }
        else {
            assert(false);
        }

        LinearLayout imagebuttonCopy = (LinearLayout) findViewById(R.id.imagebutton_copy);
        imagebuttonCopy.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                handler.copy();
                handler.resetSelection();
                SelectionPopupMenu.this.hide();
            }
        });
        
        LinearLayout imagebuttonHighlight = (LinearLayout) findViewById(R.id.imagebutton_highlight);
        imagebuttonHighlight.setOnClickListener(new OnClickListener()
        {
            
            @Override
            public void onClick(View v)
            {
                handler.highLight();
                SelectionPopupMenu.this.hide();
            }
        });

        LinearLayout imagebuttonAnnotation = (LinearLayout) findViewById(R.id.imagebutton_annotaion);
        imagebuttonAnnotation.setOnClickListener(new OnClickListener()
        {
            
            @Override
            public void onClick(View v)
            {
                handler.addAnnotation();
                SelectionPopupMenu.this.hide();
            }
        });

        LinearLayout button_dict = (LinearLayout) findViewById(R.id.imagebutton_dict);
        button_dict.setOnClickListener(new OnClickListener()
        {
            
            @Override
            public void onClick(View v)
            {
                handler.showDictionary();
                handler.resetSelection();
                SelectionPopupMenu.this.hide();
            }
        });

        setVisibility(View.GONE);
    }
    
    Activity getActivity()
    {
        return mActivity;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        return true;
    }

    public void show()
    {
        mActivity.runOnUiThread(new Runnable()
        {
            public void run()
            {
                SelectionPopupMenu.this.updateTranslation(mHandler.getSelectionText());
                setVisibility(View.VISIBLE);
            }
        });
    }

    public void hide()
    {
        mActivity.runOnUiThread(new Runnable()
        {
            public void run()
            {
                setVisibility(View.GONE);
            }
        });
    }

    public void move(int selectionStartY, int selectionEndY) {
        if (this == null) {
            return;
        }

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
                );
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

        final int verticalPosition;
        final int screenHeight = ((View)this.getParent()).getHeight();
        final int diffTop = screenHeight - selectionEndY;
        final int diffBottom = selectionEndY;
        if (diffTop > diffBottom) {
            verticalPosition = diffTop > this.getHeight() + 20
                    ? RelativeLayout.ALIGN_PARENT_BOTTOM : RelativeLayout.CENTER_VERTICAL;
        } else {
            verticalPosition = diffBottom > this.getHeight() + 20
                    ? RelativeLayout.ALIGN_PARENT_TOP : RelativeLayout.CENTER_VERTICAL;
        }

        layoutParams.addRule(verticalPosition);
        setLayoutParams(layoutParams);
    }

    public boolean isShow() {
        return (getVisibility() == View.VISIBLE);
    }
    
    public void hideTranslation()
    {
        this.findViewById(R.id.layout_dict).setVisibility(View.GONE);
    }

    private String getExplanation(String token)
    {
        Cursor cursor = mActivity.getContentResolver().query(
                Uri.parse("content://com.onyx.android.dict.OnyxDictProvider"), null, "token=\'" + token + "\'", null,
                null);
        String expString = "";
        try {
            if (cursor == null || cursor.getCount() == 0)
                return null;
            int count = cursor.getCount();
            int index = 0;
            while (cursor.moveToNext()) {
                expString += cursor.getString(3);
                if (index >= 0 && index < count - 1)
                    expString += "<br><br><br><br>";
                index++;
            }
            return expString;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
    
    private void updateTranslation(String token)
    {
        mDictTitle.setText(token);
        mWebView.loadDataWithBaseURL(null, getExplanation(token), "text/html", "utf-8", null);
    }
}
