package com.onyx.android.sdk.ui.dialog;

import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

import com.onyx.android.sdk.ui.R;

public class DialogSearchView extends DialogBaseOnyx
{
    public static interface IHandler
    {
        public void searchForward();
        public void searchBackward();
        public void dismissdialog();
        public void showSearchAll();
    }

    private TextView mTextViewPage = null;
    private IHandler mHandler = null;

    public DialogSearchView(Context context, IHandler handler)
    {
        super(context, R.style.dialog_search_view);

        setContentView(R.layout.dialog_search_view);
        mHandler = handler;
        
        ImageView buttonForward = (ImageView) findViewById(R.id.imagebutton_forward);
        buttonForward.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                mHandler.searchForward();
            }
        });

        ImageView buttonBackward = (ImageView) findViewById(R.id.imagebutton_backward);
        buttonBackward.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                mHandler.searchBackward();
            }
        });

        ImageView buttonSearchAll = (ImageView) findViewById(R.id.imagebutton_all);
        buttonSearchAll.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                mHandler.showSearchAll();
            }
        });
        buttonSearchAll.setVisibility(View.GONE);

        ImageView buttonDismiss = (ImageView) findViewById(R.id.imagebutton_dismiss);
        buttonDismiss.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                mHandler.dismissdialog();
            }
        });

        mTextViewPage = (TextView) findViewById(R.id.textview_page);
        mTextViewPage.setText("");

        Window window = getWindow();
        LayoutParams params = window.getAttributes();
        params.y = window.getWindowManager().getDefaultDisplay().getHeight();
        window.setAttributes(params);

        setCanceledOnTouchOutside(false);
    }

    public void setPage(int current, int total)
    {
        mTextViewPage.setText(String.format(DialogSearchView.this.getContext().getResources().getString(R.string.search_view_page), current, total));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            mHandler.dismissdialog();

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
