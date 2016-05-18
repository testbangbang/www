package com.onyx.android.sdk.ui.dialog;

import java.util.ArrayList;

import android.app.Activity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.onyx.android.sdk.ui.R;
import com.onyx.android.sdk.ui.OnyxGridView;
import com.onyx.android.sdk.ui.data.ReaderSettingsAdapter;

public class DialogReaderSettings extends DialogBaseOnyx
{
    public enum ReaderSettingsItemProperty{PageMargins}

    public interface onPageMarginsListener
    {
        public int onSetPageMargins(int margin);
    }
    private onPageMarginsListener mOnPageMarginsListener = new onPageMarginsListener()
    {
        
        @Override
        public int onSetPageMargins(int margin)
        {
            return 0;
        }
    };
    public void setOnPageMarginsListener(onPageMarginsListener l)
    {
        mOnPageMarginsListener = l;
    }
    
    public static final String sPageMargins = "page_margins";

    private Activity mActivity = null;
    private OnyxGridView mOnyxGridView = null;
    private ReaderSettingsAdapter mAdapter = null;
    private ArrayList<ReaderSettingsItem> mReaderSettingsItems = new ArrayList<ReaderSettingsItem>();
    private int mDefaultMargins = 0;

    public DialogReaderSettings(Activity activity, int defaultMargins)
    {
        super(activity, R.style.dialog_seekbar);

        setContentView(R.layout.reader_settings);

        mActivity = activity;
        mDefaultMargins = defaultMargins;

        mReaderSettingsItems.add(new ReaderSettingsItem(ReaderSettingsItemProperty.PageMargins, R.string.page_margins));

        mOnyxGridView = (OnyxGridView) findViewById(R.id.onyxgridview_settings_item);
        mAdapter = new ReaderSettingsAdapter(mOnyxGridView, mReaderSettingsItems);
        mOnyxGridView.setAdapter(mAdapter);
        mOnyxGridView.setOnItemClickListener(new OnItemClickListener()
        {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                itemAction((ReaderSettingsItemProperty)view.getTag());
            }
        });

        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = window.getWindowManager().getDefaultDisplay().getWidth();
        params.height = window.getWindowManager().getDefaultDisplay().getHeight();

        mAdapter.getPaginator().setPageSize(mReaderSettingsItems.size());
    }

    private void itemAction(ReaderSettingsItemProperty property)
    {
        switch (property) {
        case PageMargins:
            DialogPageMargins dialog_margins = new DialogPageMargins(mActivity, mDefaultMargins);
            dialog_margins.setOnPageMargingsListener(new DialogPageMargins.onPageMarginsListener()
            {

                @Override
                public void onPageMargins(int pageMargins)
                {
                    mDefaultMargins = mOnPageMarginsListener.onSetPageMargins(pageMargins);
                }
            });
            dialog_margins.show();
            return;
        default:
            break;
        }
    }

    public class ReaderSettingsItem
    {
        ReaderSettingsItemProperty mItemProperty = null;
        String mName = null;
        public ReaderSettingsItem(ReaderSettingsItemProperty item, int name)
        {
            mItemProperty = item;
            mName = mActivity.getResources().getString(name);
        }

        public ReaderSettingsItemProperty getReaderSettingsItemProperty()
        {
            return mItemProperty;
        }

        public String getReaderSettingsItemName()
        {
            return mName;
        }
    }
}
