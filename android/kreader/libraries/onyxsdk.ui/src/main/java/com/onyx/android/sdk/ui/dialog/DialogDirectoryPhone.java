/**
 *
 */
package com.onyx.android.sdk.ui.dialog;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

import com.onyx.android.sdk.ui.R;
import com.onyx.android.sdk.device.DeviceInfo;
import com.onyx.android.sdk.device.IDeviceFactory.TouchType;
import com.onyx.android.sdk.ui.data.DirectoryAnnotationPhoneAdapter;
import com.onyx.android.sdk.ui.data.DirectoryItem;
import com.onyx.android.sdk.ui.data.DirectoryPhoneAdapter;
import com.onyx.android.sdk.ui.dialog.DialogDirectory.DirectoryTab;
import com.onyx.android.sdk.ui.dialog.DialogDirectory.IGotoPageHandler;
import com.onyx.android.sdk.ui.dialog.data.AnnotationItem;

/**
 * @author peekaboo
 */
public class DialogDirectoryPhone extends DialogBaseOnyx {
    private IGotoPageHandler mGotoPageHandler = null;
    private TextView mTextViewTitle = null;
    private final int TABWIDGET_ICON_HEIGHT = 50;

    public DialogDirectoryPhone(Context context, ArrayList<DirectoryItem> tocItems, ArrayList<DirectoryItem> bookmarkItems, ArrayList<AnnotationItem> annotationItems, final IGotoPageHandler gotoPageHandler, DirectoryTab tab) {
        super(context, R.style.full_screen_dialog);
        setContentView(R.layout.dialog_directory_phone);
        mGotoPageHandler = gotoPageHandler;
        TabHost tab_host = (TabHost) findViewById(R.id.tabhost);
        tab_host.setup();
        TextView toc = (TextView) LayoutInflater.from(context).inflate(R.layout.onyx_tabwidget, null);
        toc.setText(R.string.tabwidget_toc);
        TextView bookmark = (TextView) LayoutInflater.from(context).inflate(R.layout.onyx_tabwidget, null);
        bookmark.setText(R.string.tabwidget_bookmark);
        TextView annotation = (TextView) LayoutInflater.from(context).inflate(R.layout.onyx_tabwidget, null);
        annotation.setText(R.string.tabwidget_annotation);

        Resources resources = context.getResources();

        tab_host.addTab(tab_host.newTabSpec(resources.getString(R.string.tabwidget_toc)).setIndicator("", context.getResources().getDrawable(R.drawable.toc)).setContent(R.id.layout_toc));
        tab_host.addTab(tab_host.newTabSpec(resources.getString(R.string.tabwidget_bookmark)).setIndicator("", context.getResources().getDrawable(R.drawable.menu_bookmark)).setContent(R.id.layout_bookmark));

        if (DeviceInfo.currentDevice.getTouchType(context) != TouchType.None) {
            tab_host.addTab(tab_host.newTabSpec(resources.getString(R.string.tabwidget_annotation)).setIndicator("", context.getResources().getDrawable(R.drawable.note)).setContent(R.id.layout_annotation));
        } else {
            View v = this.findViewById(R.id.layout_annotation);
            v.setVisibility(View.GONE);
        }

        TabWidget tabWidget = (TabWidget) findViewById(android.R.id.tabs);
        for (int i = 0; i < tabWidget.getChildCount(); i++) {
            ImageView image = (ImageView) tabWidget.getChildAt(i).findViewById(android.R.id.icon);
            image.getLayoutParams().height = TABWIDGET_ICON_HEIGHT;
            image.getLayoutParams().width = TABWIDGET_ICON_HEIGHT;
        }

        tab_host.setOnTabChangedListener(new OnTabChangeListener() {

            @Override
            public void onTabChanged(String tabId) {
                mTextViewTitle.setText(tabId);
            }
        });

        mTextViewTitle = (TextView) findViewById(R.id.textview_title);
        /*TODO Layout 800*400 need these.*/
        ListView listViewTOC = (ListView) findViewById(R.id.listview_toc);
        ListView listViewBookmark = (ListView) findViewById(R.id.listview_bookmark);
        ListView listViewAnnotation = (ListView) findViewById(R.id.listview_annotation);

        if (tocItems != null) {
            DirectoryPhoneAdapter tocAdapter = new DirectoryPhoneAdapter(context, tocItems);
            listViewTOC.setAdapter(tocAdapter);
        }
        if (bookmarkItems != null) {
            DirectoryPhoneAdapter bookmarkAdapter = new DirectoryPhoneAdapter(context, bookmarkItems);
            listViewBookmark.setAdapter(bookmarkAdapter);
        }
        if (annotationItems != null) {
            DirectoryAnnotationPhoneAdapter annotationAdapter = new DirectoryAnnotationPhoneAdapter(context, annotationItems);
            listViewAnnotation.setAdapter(annotationAdapter);
        }

        listViewTOC.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DialogDirectoryPhone.this.dismiss();
                DirectoryItem item = (DirectoryItem) view.getTag();
                mGotoPageHandler.jumpTOC(item);
            }
        });
        listViewBookmark.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DialogDirectoryPhone.this.dismiss();
                DirectoryItem item = (DirectoryItem) view.getTag();
                mGotoPageHandler.jumpBookmark(item);
            }
        });
        listViewAnnotation.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DialogDirectoryPhone.this.dismiss();
                DirectoryItem item = (DirectoryItem) view.getTag();
                mGotoPageHandler.jumpAnnotation(item);
            }
        });

        Button button_exit = (Button) findViewById(R.id.button_exit);
        button_exit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DialogDirectoryPhone.this.dismiss();
            }
        });

        switch (tab) {
            case toc:
                tab_host.setCurrentTab(0);
                mTextViewTitle.setText(R.string.tabwidget_toc);
                break;
            case bookmark:
                tab_host.setCurrentTab(1);
                mTextViewTitle.setText(R.string.tabwidget_bookmark);
                break;
            case annotation:
                tab_host.setCurrentTab(2);
                mTextViewTitle.setText(R.string.tabwidget_annotation);
                break;
            default:
                tab_host.setCurrentTab(0);
                mTextViewTitle.setText(R.string.tabwidget_toc);
                break;
        }
    }
}
