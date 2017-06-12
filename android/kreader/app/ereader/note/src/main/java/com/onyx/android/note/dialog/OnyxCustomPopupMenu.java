package com.onyx.android.note.dialog;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.onyx.android.sdk.ui.view.OnyxPageDividerItemDecoration;

import java.util.List;

/**
 * Created by solskjaer49 on 16/6/21 18:40.
 */

public class OnyxCustomPopupMenu {
    PopupWindow popupWindow;
    RecyclerView recyclerView;
    View parentView;
    int positionX;
    MenuCallBack callBack;

    public static class MenuBean {
        int actionID;
        int stringRes;

        public MenuBean(int actionID, int stringRes) {
            this.actionID = actionID;
            this.stringRes = stringRes;
        }
    }

    public interface MenuCallBack {
        void onItemClick(int actionID);

        void onItemLongClick(int actionID);
    }

    public static OnyxCustomPopupMenu buildCustomOptionMenu(Activity activity, View parent, List<MenuBean> dataList, MenuCallBack callBack) {
        OnyxCustomPopupMenu menu = new OnyxCustomPopupMenu();
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
//        menu.popupWindow = new PopupWindow(activity.getLayoutInflater().inflate(R.layout.popwindow_option, null),
//                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        menu.popupWindow.setBackgroundDrawable(new ColorDrawable(activity.getResources().getColor(android.R.color.transparent)));
        menu.popupWindow.setOutsideTouchable(true);
        menu.parentView = parent;
        menu.callBack = callBack;
        menu.positionX = dm.widthPixels;
//        menu.recyclerView = (RecyclerView) menu.popupWindow.getContentView().findViewById(R.id.option_recycler_view);
        menu.recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        menu.recyclerView.setLayoutManager(layoutManager);
        menu.recyclerView.setAnimation(null);
        menu.recyclerView.addItemDecoration(new OnyxPageDividerItemDecoration(
                activity, OnyxPageDividerItemDecoration.VERTICAL));
        OptionAdapter adapter = new OptionAdapter(dataList, callBack);
        menu.recyclerView.setAdapter(adapter);
        return menu;
    }

    public void toggleStatus() {
        if (popupWindow != null) {
            if (popupWindow.isShowing()) {
                popupWindow.dismiss();
            } else {
                popupWindow.showAtLocation(parentView, Gravity.NO_GRAVITY, positionX, 0);
            }
        }
    }

    public void dismiss() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }

    static class OptionAdapter extends RecyclerView.Adapter<OptionAdapter.OptionViewHolder> {
        private List<MenuBean> listData;
        private MenuCallBack mMenuCallBack;

        OptionAdapter(List<MenuBean> mList, MenuCallBack mMenuCallBack) {
            super();
            this.listData = mList;
            this.mMenuCallBack = mMenuCallBack;
        }

        @Override
        public int getItemCount() {
            return listData.size();
        }

        @Override
        public OptionViewHolder onCreateViewHolder(ViewGroup viewGroup, int arg1) {
            View view = null;
//                    = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.popup_option_item, viewGroup, false);
            return new OptionViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final OptionViewHolder optionViewHolder, int arg1) {
            optionViewHolder.textView.setText(listData.get(arg1).stringRes);
            optionViewHolder.textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mMenuCallBack != null) {
                        mMenuCallBack.onItemClick(listData.get(optionViewHolder.getAdapterPosition()).actionID);
                    }
                }
            });
            optionViewHolder.textView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (mMenuCallBack != null) {
                        mMenuCallBack.onItemLongClick(listData.get(optionViewHolder.getAdapterPosition()).actionID);
                        return true;
                    }
                    return false;
                }
            });
        }

        class OptionViewHolder extends RecyclerView.ViewHolder {
            TextView textView;

            OptionViewHolder(View view) {
                super(view);
//                this.textView = (TextView) view.findViewById(R.id.option_text_item);
            }

        }
    }

}
