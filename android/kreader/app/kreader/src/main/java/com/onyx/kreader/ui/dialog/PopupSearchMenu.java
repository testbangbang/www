package com.onyx.kreader.ui.dialog;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.onyx.kreader.R;
import com.onyx.android.sdk.reader.api.ReaderSearchOptions;

public final class PopupSearchMenu extends LinearLayout {

    public static enum SearchDirection{Forward,Backward}
    public static enum SearchResult{SUCCEED,EMPTY}

    public static abstract class MenuCallback {
        public abstract void search(SearchDirection mSearchDirection);
        public abstract void disMissMenu();
        public abstract void showSearchAll();
    }

    private final MenuCallback mMenuCallback;

    private boolean mIsShow = false;
    private ImageButton mButtonSearchAll =null;
    private ProgressBar mSearchProgressBar;
    private Context mContext;
    private DisplayMetrics dm;

    private ReaderSearchOptions searchOptions;

    public PopupSearchMenu(Context context, RelativeLayout parentLayout, MenuCallback callback) {
        super(context);
        final LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.popup_search_menu, this, true);
        dm=context.getResources().getDisplayMetrics();
        final int POSITION_PARENT_RIGHT = 1; // TODO hardcoded value first
        parentLayout.addView(this, setMenuPosition(POSITION_PARENT_RIGHT));
        setFocusable(false);
        this.setVisibility(View.GONE);
        mMenuCallback = callback;
        mContext=context;
        ImageButton mButtonForward = (ImageButton) findViewById(R.id.imagebutton_forward);
        ImageButton mButtonBackward = (ImageButton) findViewById(R.id.imagebutton_backward);
//        mButtonSearchAll =(ImageButton)findViewById(R.id.imagebutton_all);
        ImageButton mButtonDismiss = (ImageButton) findViewById(R.id.imagebutton_dismiss);
        mSearchProgressBar=(ProgressBar)findViewById(R.id.searchProgressBar);


        mButtonForward.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mMenuCallback.search(SearchDirection.Forward);
            }
        });

        mButtonBackward.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mMenuCallback.search(SearchDirection.Backward);
            }
        });

//        mButtonSearchAll.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mMenuCallback.showSearchAll();
//            }
//        });

        mButtonDismiss.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mMenuCallback.disMissMenu();
            }
        });

    }

    public ReaderSearchOptions getSearchOptions() {
        return searchOptions;
    }

    public void setSearchOptions(ReaderSearchOptions searchOptions) {
        this.searchOptions = searchOptions;
    }

    public void show()
    {
        setVisibility(View.VISIBLE);
        mSearchProgressBar.setVisibility(VISIBLE);
        mIsShow = true;
    }

    public void hide()
    {
        setVisibility(View.GONE);
        mIsShow = false;
    }

    public void hideProgressBar(){
        mSearchProgressBar.setVisibility(INVISIBLE);
    }
    public boolean isShow()
    {
        return (getVisibility() == View.VISIBLE);
    }

    public void searchDone(SearchResult searchResult){
        if (this.mSearchProgressBar.getVisibility()==VISIBLE){
            this.hideProgressBar();
        }
        switch (searchResult){
            case SUCCEED:
                break;
            case EMPTY:
                Toast.makeText(mContext, R.string.not_found, Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    public void rePositionAfterNewConfiguration(int positionValue){
        setLayoutParams(setMenuPosition(positionValue));
    }

    private RelativeLayout.LayoutParams setMenuPosition(int positionValue){
        RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        int sideMarginValue=(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,25,dm);
        int bottomMarginValue=(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,60,dm);
        if (positionValue==1){
            p.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            p.setMargins(0,0,sideMarginValue,bottomMarginValue);
        }else {
            p.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            p.setMargins(sideMarginValue,0,0,bottomMarginValue);
        }
        p.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//        p.addRule(RelativeLayout.RIGHT_OF,R.id.status_page_layout);
//        p.addRule(RelativeLayout.LEFT_OF,R.id.view_navigator);
        return p;
    }
}
