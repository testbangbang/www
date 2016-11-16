package com.onyx.android.note.dialog;

import android.app.FragmentManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.onyx.android.note.R;
import com.onyx.android.note.data.ScribbleMode;
import com.onyx.android.note.utils.NoteAppConfig;
import com.onyx.android.sdk.data.GAdapter;
import com.onyx.android.sdk.data.GAdapterUtil;
import com.onyx.android.sdk.data.GObject;
import com.onyx.android.sdk.ui.dialog.OnyxAlertDialog;
import com.onyx.android.sdk.ui.view.ContentItemView;
import com.onyx.android.sdk.ui.view.ContentView;

import java.util.HashMap;

public class DialogChooseScribbleMode extends OnyxAlertDialog {
    private static final String TAG = DialogChooseScribbleMode.class.getSimpleName();
    private ContentView mContentView;
    private GAdapter mAdapter;
    private Callback mCallBack;

    public DialogChooseScribbleMode setCallBack(Callback mCallBack) {
        this.mCallBack = mCallBack;
        return this;
    }

    public interface Callback {
        void onModeChosen(@ScribbleMode.ScribbleModeDef int mode);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        buildOptionsAdapter();
        Params params = new Params().setTittleString(getString(R.string.sort))
                .setCustomContentLayoutResID(R.layout.alert_dialog_content_choose_mode)
                .setEnableFunctionPanel(false)
                .setEnablePageIndicator(false)
                .setCustomLayoutHeight((int) (2.3 * (getResources().getDimensionPixelSize(R.dimen.button_minHeight))))
                .setCustomViewAction(new CustomViewAction() {
                    @Override
                    public void onCreateCustomView(View customView, TextView pageIndicator) {
                        mContentView = (ContentView) customView.findViewById(R.id.choose_mode_content_view);
                        HashMap<String, Integer> mapping = new HashMap<>();
                        mapping.put(GAdapterUtil.TAG_TITLE_RESOURCE, R.id.mode_title);
                        mContentView.setupGridLayout(2, 1);
                        mContentView.setAlwaysShowPageIndicator(false);
                        mContentView.setSubLayoutParameter(R.layout.scirbble_mode_item, mapping);
                        mContentView.setAdapter(mAdapter, 0);
                        mContentView.setCallback(new ContentView.ContentViewCallback() {
                            @Override
                            public void onItemClick(ContentItemView view) {
                                GObject temp = view.getData();
                                if (mCallBack != null) {
                                    mCallBack.onModeChosen(ScribbleMode.translate(GAdapterUtil.getUniqueIdAsIntegerType(temp)));
                                }
                                dismiss();
                            }
                        });
                    }
                });
        if (NoteAppConfig.sharedInstance(getActivity()).useMXUIStyle()) {
            params.setCustomLayoutResID(R.layout.mx_custom_alert_dialog);
        }
        setParams(params);
        super.onCreate(savedInstanceState);
    }

    private GAdapter buildOptionsAdapter() {
        if (mAdapter == null) {
            mAdapter = new GAdapter();
            mAdapter.addObject(createSortOrderItem(R.string.normal_scribble, ScribbleMode.MODE_NORMAL_SCRIBBLE));
            mAdapter.addObject(createSortOrderItem(R.string.span_scribble, ScribbleMode.MODE_SPAN_SCRIBBLE));
        }
        return mAdapter;
    }

    private GObject createSortOrderItem(int stringResource, @ScribbleMode.ScribbleModeDef int mode) {
        GObject object = GAdapterUtil.createTableItem(stringResource, 0, 0, 0, null);
        object.putInt(GAdapterUtil.TAG_UNIQUE_ID, mode);
        return object;
    }

    public void show(FragmentManager fm) {
        show(fm, TAG);
    }

}
