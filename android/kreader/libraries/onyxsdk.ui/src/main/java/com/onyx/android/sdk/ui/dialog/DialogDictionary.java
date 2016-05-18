package com.onyx.android.sdk.ui.dialog;

import com.onyx.android.sdk.ui.R;
import com.onyx.android.sdk.ui.util.HTMLReaderWebView;
import com.onyx.android.sdk.ui.util.HTMLReaderWebView.OnPageChangedListener;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings.TextSize;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DialogDictionary extends Dialog implements OnClickListener
{
    private TextView dictTitle;
    private LinearLayout dictionary;
    private HTMLReaderWebView webView;
    private String tokenString;
    private TextView pageIndicator;
    private TextView legalTextView;
    private Context mContext;

    //
    // private int mCurrentPage;
    // private int mTotalPage;

    public DialogDictionary(Context context, String token)
    {
        super(context);
        mContext = context;
        tokenString = token;
        init();
    }

    private void init()
    {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCanceledOnTouchOutside(true);
        setContentView(R.layout.dialog_dictionary);
        dictTitle = (TextView) findViewById(R.id.dict_title);
        dictTitle.setText(tokenString);

        pageIndicator = (TextView) findViewById(R.id.page_indicator);

        dictionary = (LinearLayout) findViewById(R.id.dictionary);
        dictionary.setOnClickListener(this);

        webView = (HTMLReaderWebView) findViewById(R.id.explain);
        webView.loadDataWithBaseURL(null, getExplanation(tokenString), "text/html", "utf-8", null);
        webView.getSettings().setTextSize(TextSize.LARGER);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setPageTurnType(HTMLReaderWebView.PAGE_TURN_TYPE_VERTICAL);
        webView.setPageTurnThreshold(100);
        webView.registerOnOnPageChangedListener(new OnPageChangedListener() {

            @Override
            public void onPageChanged(int totalPage, int curPage)
            {
                pageIndicator.setText(curPage + "/" + totalPage);
            }
        });
        
        legalTextView = (TextView)findViewById(R.id.legal_textview);
        legalTextView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {

        if (v == dictionary) {
            Intent intent = new Intent("com.onyx.android.dict.ui.OnyxDictActivity");
            intent.putExtra("token", tokenString);
            mContext.startActivity(intent);
            dismiss();
        } else if (v == legalTextView) {
            Intent intent = new Intent("com.onyx.android.dict.ui.LegalActivity");
            mContext.startActivity(intent);
        }
        
    }

    public String getExplanation(String token)
    {
        Cursor cursor = mContext.getContentResolver().query(
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
}