package com.onyx.android.dr.reader.view;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.onyx.android.dr.R;
import com.onyx.android.dr.reader.adapter.SearchDropDownListAdapter;
import com.onyx.android.dr.reader.data.DictionaryQuery;
import com.onyx.android.dr.reader.event.DropDownListViewDismissEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by huxiaomao on 2016/8/12.
 */
public class DropDownListView {
    private View contextView;
    private ListView lv_search_history = null;
    private SearchDropDownListAdapter searchDropDownListAdapter = null;
    private PopupWindow popupWindow = null;

    private float yoff = 0.0f;
    public DropDownListView(Context context) {
        contextView = LayoutInflater.from(context).inflate(R.layout.dropdown_listview, null);
        lv_search_history = (ListView)contextView.findViewById(R.id.lv_search_history);
        searchDropDownListAdapter = new SearchDropDownListAdapter(context);
        lv_search_history.setAdapter(searchDropDownListAdapter);
        popupWindow = new PopupWindow(contextView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        yoff = context.getResources().getDimension(R.dimen.search_drop_down_listview_yoff);
        lv_search_history.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                searchDropDownListAdapter.getSelectKeyword(position);
            }
        });
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                EventBus.getDefault().post(new DropDownListViewDismissEvent());
            }
        });
    }

    public void show(View anchor,List<DictionaryQuery> dictList) {
        if(dictList == null || dictList.size() <= 0){
            return;
        }

        if (popupWindow.isShowing()) {
            searchDropDownListAdapter.setHeadwordList(dictList);
            searchDropDownListAdapter.notifyDataSetChanged();
            return;
        }
        searchDropDownListAdapter.setHeadwordList(dictList);
        popupWindow.setFocusable(false);
        popupWindow.setWidth(anchor.getWidth());
        popupWindow.showAsDropDown(anchor);
    }

    public void hide() {
        if(popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }

    public boolean isShowing(){
        if(popupWindow != null){
            return popupWindow.isShowing();
        }else{
            return false;
        }
    }

    public void setFocusable(){
        if(popupWindow != null){
            popupWindow.setFocusable(true);
            lv_search_history.setFocusable(true);
            lv_search_history.requestFocus();
        }
    }


}
