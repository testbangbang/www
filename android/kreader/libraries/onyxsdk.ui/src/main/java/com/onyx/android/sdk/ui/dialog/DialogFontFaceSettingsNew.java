/**
 * 
 */
package com.onyx.android.sdk.ui.dialog;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.onyx.android.sdk.ui.R;
import com.onyx.android.sdk.data.GAdapter;
import com.onyx.android.sdk.data.GObject;
import com.onyx.android.sdk.data.SelectionOption;
import com.onyx.android.sdk.data.util.GAdapterUtil;
import com.onyx.android.sdk.data.util.SelectionOptionUtil;
import com.onyx.android.sdk.ui.OnyxTableItemView;
import com.onyx.android.sdk.ui.OnyxTableView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

/**
 * 
 * @author qingyue
 *
 */
@Deprecated
public class DialogFontFaceSettingsNew extends DialogBaseOnyx
{
    public interface onSettingsFontFaceListener
    {
        public void settingfontFace(int location);
    }
    private onSettingsFontFaceListener mOnSettingsFontFaceLinstener = new onSettingsFontFaceListener()
    {
        
        @Override
        public void settingfontFace(int location)
        {
            //do nothing
        }
    };
    public void setOnSettingsFontFaceListener(onSettingsFontFaceListener l)
    {
        mOnSettingsFontFaceLinstener = l;
    }

    public static abstract class SettingsCallback {
        public void onSelectionChanged(ArrayList<Object> selectedItems) {
        }

        public void onFinished(boolean cancel) {
        }
    }

    private SettingsCallback mCallback = null;
    public void setCallback(SettingsCallback callback) {
        mCallback = callback;
    }

    public DialogFontFaceSettingsNew(Context context, String[] fontFaces, String currentFontFace)
    {
        super(context);
        setCanceledOnTouchOutside(true);
        setContentView(R.layout.onyx_dialog_selection_settings);
        ((TextView)findViewById(R.id.textview_title)).setText(R.string.font_face);
        final OnyxTableView tableView = (OnyxTableView)findViewById(R.id.tableview_selection);

        final GAdapter adapter = GAdapter.createFromStringList(Arrays.asList(fontFaces), GAdapterUtil.TAG_TITLE_STRING);
        final SelectionOption selectionOption = new SelectionOption(true, false);
        adapter.getOptions().putObject(GAdapterUtil.TAG_SELECTION_OPTION, selectionOption);
        for (GObject o : adapter.getList()) {
            o.putBoolean(GAdapterUtil.TAG_IN_SELECTION, true);
            o.putInt(GAdapterUtil.TAG_SELECTION_CHECKED_VIEW_ID, R.id.imageview_circle);

            if (currentFontFace.endsWith(o.getString(GAdapterUtil.TAG_TITLE_STRING))) {
                o.putBoolean(GAdapterUtil.TAG_SELECTED, true);
                ((HashSet<Object>)selectionOption.getSelections()).add(o);
            } else {
                o.putBoolean(GAdapterUtil.TAG_SELECTED, false);
            }
        }

        int MAX_ROW = 6;
        int rows = Math.min(adapter.getList().size(), MAX_ROW);
        tableView.setupAsGridView(adapter, rows, 1, null,
                R.layout.onyx_dialog_selection_settings_item, null);
        tableView.setCallback(new OnyxTableView.TableViewCallback() {
            @Override
            public void onItemClick(OnyxTableItemView view) {
                boolean changed = SelectionOptionUtil.toggleSelection(view.getData(), selectionOption);
                if (changed) {
                    if (mCallback != null) {
                        Collection<GObject> selections = (Collection<GObject>) selectionOption.getSelections();
                        ArrayList<Object> items = new ArrayList<Object>();
                        for (GObject o : selections) {
                            items.add(o.getString(GAdapterUtil.TAG_TITLE_STRING));
                        }
                        mCallback.onSelectionChanged(items);
                    }
                }
            }

            @Override
            public void afterPageChanged(OnyxTableView tableView, int newPage, int oldPage) {
                ((TextView)findViewById(R.id.textview_page)).setText(String.valueOf(newPage + 1) +
                        "/" + tableView.getPageCount());
            }
        });

        findViewById(R.id.button_previous).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tableView.previousPage();
            }
        });

        findViewById(R.id.button_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tableView.nextPage();
            }
        });

        findViewById(R.id.button_set).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mCallback != null) {
                    mCallback.onFinished(false);
                }
                dismiss();
            }
        });

        findViewById(R.id.button_cancel).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mCallback != null) {
                    mCallback.onFinished(true);
                }
                cancel();
            }
        });
        WindowManager.LayoutParams p =this.getWindow().getAttributes();
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);

        p.height = (dm.heightPixels * 7) / 10;
                //((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 510,
                //context.getResources().getDisplayMetrics()));
        p.width = dm.widthPixels - ((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20,
                context.getResources().getDisplayMetrics()));
        this.getWindow().setAttributes(p);
    }


}
