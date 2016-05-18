/**
 *
 */
package com.onyx.android.sdk.ui.dialog;

import android.content.Context;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;

import com.onyx.android.sdk.ui.R;
import com.onyx.android.sdk.data.sys.OnyxSysCenter;
import com.onyx.android.sdk.ui.data.SelectionAdapter;

import java.util.ArrayList;

/**
 *
 * @author qingyue
 *
 */
@Deprecated
public class DialogScreenRefresh extends DialogBaseSettings
{
    private final static String TAG = DialogScreenRefresh.class.getSimpleName();

    public interface onScreenRefreshListener
    {
        public void screenRefresh(int pageTurning);
    }
    private onScreenRefreshListener mOnScreenRefreshListener = new onScreenRefreshListener()
    {

        @Override
        public void screenRefresh(int pageTurning)
        {
            //do nothing
        }
    };
    public void setOnScreenRefreshListener(onScreenRefreshListener l)
    {
        mOnScreenRefreshListener = l;
    }

    /**
     * times to reset display to eliminate ghost pixels
     */
    public static int DEFAULT_INTERVAL_COUNT = 5;

    private SelectionAdapter mAdapter = null;
    final ArrayList<Pair<String, Object>> mItems = new ArrayList<Pair<String, Object>>();

    public DialogScreenRefresh(final Context context)
    {
        this(context, 1);
    }

    public DialogScreenRefresh(final Context context, int pageTurning)
    {
        super(context);

        mItems.add(new Pair<String, Object>(this.getContext().getString(R.string.always), 1));
        mItems.add(new Pair<String, Object>(this.getContext().getString(R.string.every_3_pages), 3));
        mItems.add(new Pair<String, Object>(this.getContext().getString(R.string.every_5_pages), 5));
        mItems.add(new Pair<String, Object>(this.getContext().getString(R.string.every_7_pages), 7));
        mItems.add(new Pair<String, Object>(this.getContext().getString(R.string.every_9_pages), 9));
        mItems.add(new Pair<String, Object>(this.getContext().getString(R.string.never), Integer.MAX_VALUE));
        int interval = OnyxSysCenter.getScreenUpdateGCInterval(this.getContext(), DEFAULT_INTERVAL_COUNT);
        this.getButtonSet().setVisibility(View.GONE);
        this.getButtonCancel().setVisibility(View.GONE);
        this.setCanceledOnTouchOutside(true);
        mAdapter = new SelectionAdapter(context, this.getGridView(), mItems, 0);
        for (int i = 0; i < mItems.size(); i++) {
            if ((Integer)mItems.get(i).second == interval) {
                mAdapter.setSelection(i);
                break;
            }
        }
        this.getGridView().setAdapter(mAdapter);
        this.getGridView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i < 0) {
                    return;
                }

                int value = (Integer)mItems.get(i).second;
                OnyxSysCenter.setScreenUpdateGCInterval(context , value);
                mOnScreenRefreshListener.screenRefresh(value);
                DialogScreenRefresh.this.dismiss();
                Log.d(TAG, "render reset time: " + value);
            }
        });

        this.getTextViewTitle().setText(R.string.screen_refresh);

        mAdapter.getPaginator().setPageSize(mItems.size());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.cancel();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
