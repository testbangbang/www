package com.onyx.android.dr.view;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.onyx.android.dr.R;
import com.onyx.android.dr.adapter.SearchDropDownListAdapter;
import com.onyx.android.dr.event.ClearHistoryEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.List;


/**
 * Created by huxiaomao on 2016/8/12.
 */
public class SearchDropDownListView implements View.OnClickListener{
    public static final int LIST_TYPE_HISTORY = 0;
    public static final int LIST_TYPE_SEARCH_RESULT = 1;
    private View contextView;
    private TextView tvSearchHistory = null;
    private Button btnClearSearchHistory = null;
    private ListView lv_search_history = null;
    private SearchDropDownListAdapter searchDropDownListAdapter = null;
    private PopupWindow popupWindow = null;

    private float yoff = 0.0f;
    public SearchDropDownListView(Context context) {
        contextView = LayoutInflater.from(context).inflate(R.layout.search_dropdown_listview, null);
        tvSearchHistory = (TextView) contextView.findViewById(R.id.tv_search_history);
        btnClearSearchHistory = (Button) contextView.findViewById(R.id.btn_clear_history);
        btnClearSearchHistory.setOnClickListener(this);
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
    }

    public void show(View anchor,int listType,List<String> headwordList) {
        if(headwordList == null || headwordList.size() <= 0){
            return;
        }

        if(listType == LIST_TYPE_HISTORY){
            tvSearchHistory.setVisibility(View.GONE);
            btnClearSearchHistory.setVisibility(View.VISIBLE);
        }else{
            tvSearchHistory.setVisibility(View.GONE);
            btnClearSearchHistory.setVisibility(View.GONE);
        }
        if (popupWindow.isShowing()) {
            searchDropDownListAdapter.setHeadwordList(headwordList);
            searchDropDownListAdapter.notifyDataSetChanged();
            return;
        }
        searchDropDownListAdapter.setHeadwordList(headwordList);
        popupWindow.setFocusable(false);
        popupWindow.setWidth(anchor.getWidth());
        popupWindow.showAsDropDown(anchor,0,(int) yoff);
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

    @Override
    public void onClick(View v) {
        ClearHistoryEvent clearHistoryEvent = new ClearHistoryEvent();
        EventBus.getDefault().post(clearHistoryEvent);
    }
}
